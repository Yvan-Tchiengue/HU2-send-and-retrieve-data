package pis.hue2.server;
// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import pis.hue2.common.DataSocketReader;
import pis.hue2.common.Instruction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Server class
public class LaunchServer {

    // counter for clients
    private final AtomicInteger i = new AtomicInteger(0);
    private final ServerSocket socket;
    private final File directory;
    private final HashMap<String, Integer> lockedFiles = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public LaunchServer(int port, File directory) throws IOException {
        socket = new ServerSocket(port);
        this.directory = directory;
    }

    /**Diese Methode ermöglicht uns den Zugriff auf das Dokument offensichtlich dann, wenn es frei ist,
     * da nicht mehrere Kunden gleichzeitig Zugriff auf das Dokument haben sollen.
     * @param file
     * @param write
     */

    private void waitUntilFree(String file, boolean write) {
        while (!tryLock(file, write)) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**Sie hilft uns, die Datei zu blockieren
     * @param file
     * @param write
     * @return
     */
    private boolean tryLock(String file, boolean write) {
        lock.lock();
        try {
            Integer i = lockedFiles.get(file);   //pour avoir acces a la valeur du hashmap
            if (i == null) {
                if (write) {
                    lockedFiles.put(file, -1);
                } else {
                    lockedFiles.put(file, 1);
                }
                return true;
            } else if (!write && i > 0) {
                lockedFiles.put(file, i + 1);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**Diese Entsperrmethode hilft uns, die gesperrte Datei zu entsperren.
     * @param file
     */

    private void unlock(String file) {
        lock.lock();
        try {
            Integer i = lockedFiles.get(file);
            if (i > 1) {
                lockedFiles.put(file, i - 1);
            } else {
                lockedFiles.remove(file);
            }
        } finally {
            lock.unlock();
        }
    }

    /**Sie wird uns helfen, eine Liste der Dokumente auf dem Server zu erstellen.
     * @param f
     * @param builder
     * @param substr
     */

    private static void doList(File f, StringBuilder builder, int substr) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    doList(file, builder, substr);
                } else {
                    if (!builder.isEmpty()) {
                        builder.append('\n');
                    }
                    builder.append(file.getAbsolutePath().substring(substr));
                }
            }
        }
    }

    /**diese Methode empfängt Client-Daten über den Socket.
     * @param socket
     */
    private void acceptClient(Socket socket) {
        boolean isLoggedIn = false;
        String lastPutFile = null;
        int currentId = -1;
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            DataSocketReader reader = new DataSocketReader(socket);
            while (true) {
                final String received = reader.readLine();
                if (received == null) {
                    System.out.println("End of stream reached!");
                    break;
                }
                if (received.equals(Instruction.CON.name())) {
                    if ((currentId = i.getAndIncrement()) < 3) {
                        writer.println(Instruction.ACK.name());
                       // System.out.println("ACK received");
                        //TextArea l1 = new TextArea();

                        //l1.setText("ACK received");
                        isLoggedIn = true;
                    } else {
                        i.decrementAndGet();
                        writer.println(Instruction.DND.name());
                        break;
                    }
                } else {
                    if (!isLoggedIn) {
                        System.out.println("Illegal communication by client!");
                        break;
                    }
                    //debug System.out.println(currentId + " " + received);
                    if (received.equals(Instruction.PUT.name())) {
                        lastPutFile = reader.readLine();
                        File f = new File(directory, lastPutFile);
                        waitUntilFree(lastPutFile, true);
                        try {
                            if (f.exists()) {
                                f.delete();
                            }
                            f.createNewFile();
                        } finally {
                            unlock(lastPutFile);
                        }
                        writer.println(Instruction.ACK.name());
                    } else if (received.equals(Instruction.LST.name())) {
                        writer.println(Instruction.DAT.name());
                        String dirname = directory.getAbsolutePath();
                        int length = dirname.endsWith("\\") || dirname.endsWith("/") ? dirname.length() : dirname.length() + 1;
                        StringBuilder builder = new StringBuilder();
                        doList(directory, builder, length);
                        byte[] data = builder.toString().getBytes(Charset.defaultCharset());
                        writer.println(data.length);
                        socket.getOutputStream().write(data);
                        socket.getOutputStream().flush();
                    } else if (received.equals(Instruction.DAT.name())) {
                        if (lastPutFile == null) {
                            System.out.println("Illegal file upload!");
                            break;
                        }
                        long size;
                        try {
                            size = Long.parseLong(reader.readLine());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            break;
                        }
                        File f = new File(directory, lastPutFile);
                        waitUntilFree(lastPutFile, true);
                        try {
                            FileOutputStream os = new FileOutputStream(f);
                            byte[] buffer = new byte[1024 * 64];
                            int current;
                            while (size > 0 && (current = socket.getInputStream().read(buffer, 0, (int) Math.min(size, buffer.length))) >= 0) {
                                os.write(buffer, 0, current);
                                size -= current;
                            }
                            os.close();
                        } finally {
                            unlock(lastPutFile);
                        }
                        writer.println(Instruction.ACK.name());
                    } else if (received.equals(Instruction.GET.name())) {
                        String s = reader.readLine();
                        File f = new File(directory, s);
                        if (!f.exists()) {
                            writer.println(Instruction.DND.name());
                            continue;
                        }
                        writer.println(Instruction.DAT.name());
                        waitUntilFree(s, false);
                        try {
                            long length = f.length();
                            writer.println(length);
                            FileInputStream fis = new FileInputStream(f);
                            byte[] data = new byte[1024 * 64];
                            int count;
                            while ((count = fis.read(data, 0, (int) Math.min(data.length, length))) >= 0 && length > 0) {
                                socket.getOutputStream().write(data, 0, count);
                                length -= count;
                            }
                        } finally {
                            unlock(s);
                        }
                    } else if (received.equals(Instruction.DEL.name())) {
                        String s = reader.readLine();
                        File f = new File(directory, s);
                        if (f.exists()) {
                            waitUntilFree(s, true);
                            try {
                                f.delete();
                            } finally {
                                unlock(s);
                            }
                        }
                        writer.println(Instruction.ACK.name());
                    } else if (received.equals(Instruction.DSC.name())) {
                        writer.println(Instruction.DSC.name());
                        System.out.println("Disconnected!");
                        break;
                    }
                    if (!received.equals(Instruction.PUT.name())) {
                        lastPutFile = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isLoggedIn) {
            i.decrementAndGet();
        }
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    /**Diese Methode stellt die Verbindung zwischen dem Server und dem Client her.
     * @throws IOException
     */
    public void start() throws IOException {
        while (true) {
            final Socket s = socket.accept();
            new Thread(() -> acceptClient(s)).start();
            System.out.println("New client request received : " + s);
        }
    }

    public static void main(String[] args) throws IOException {
        new LaunchServer(2000, new File("server")).start();
    }


}