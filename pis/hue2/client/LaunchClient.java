package pis.hue2.client;

import pis.hue2.common.DataSocketReader;
import pis.hue2.common.Gui;
import pis.hue2.common.Instruction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;


// ClientHandler class
public class LaunchClient {
    final PrintWriter writer;
    final DataSocketReader reader;
    Socket s;
    boolean isloggedin;

    // constructor

    /**
     * il s'agit du constructeur de la classe LaunchClient
     * @param s
     * @throws IOException
     */
    public LaunchClient(Socket s) throws IOException {
        writer = new PrintWriter(s.getOutputStream(), true);
        reader = new DataSocketReader(s);
        this.s = s;
        this.isloggedin = true;
        if (!tryConnect()) {
            isloggedin = false;
            throw new IllegalStateException("Client must be connected to work!");
        }
    }
    /*
    Client:
    1. CON -> bei > 3: DND, andern: ACK
    2. LST, PUT, GET, DEL, DAT
    3. DSC -> DSC
     */

    /**
     * il nous dit si le client est connecte au serveur
     * @return isloggedin
     */

    public boolean isIsloggedin() {
        return isloggedin;
    }


    /**
     * er liest die vom Server kommenden Daten
     * @param size
     * @return
     */
    private byte[] readData(long size) {
        assert size >= 0 && size <= Integer.MAX_VALUE; //This function is only for objects, they are not to be meant to live on the hard drive!
        int newSize = (int) size;
        byte[] data = new byte[newSize];
        int offset = 0;
        try {
            while (offset < newSize) {
                offset += reader.read(data, offset, newSize - offset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    /**
     * Er liest die vom Server kommenden Daten, er liest eine Zeichenkette.
     * @return
     */
    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**er fordert die Verbindung zum Server über das CON-Protokoll an.
     * @return
     */
    private boolean tryConnect() {
        writer.println(Instruction.CON.name());
        String received = readLine();
        if(received.equals(Instruction.ACK.name())){
            System.out.println("ACK received");
        }
        return received != null && received.equals(Instruction.ACK.name());
    }

    /**
     * versucht sie, die Verbindung zum Server zu trennen, prüft aber,
     * ob die Verbindung bereits getrennt ist oder nicht
     * @return
     */
    public boolean disconnect() {
        if (!isloggedin) {
            return true;
        }
        writer.println(Instruction.DSC.name());
        String received = readLine();
        try {
            s.close();
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return received != null && received.equals(Instruction.DSC.name());
    }

    /**
     * er fordert die Dokumente vom Server an
     * @return
     */
    public String[] doList() {
        writer.println(Instruction.LST.name());
        String received = readLine();
        if (received == null || !received.equals(Instruction.DAT.name())) return null;
        String longStr = readLine();
        if (longStr == null) return null;
        long size;
        try {
            size = Long.parseLong(longStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        byte[] data = readData(size);
        if (data == null) return null;
        String s = new String(data, Charset.defaultCharset());
        if (s.length() == 0) {
            return new String[0];
        }
        return s.split("\n");
    }

    /**er versucht, die Dokumente auf dem Server zu speichern
     * @param name
     * @return
     */
    public boolean putFile(String name) {
        writer.println(Instruction.PUT.name());
        writer.println(name);
        String s = readLine();
        return s != null && s.equals(Instruction.ACK.name());
    }

    /**er schreibt die Daten, tatsächlich in ein Bit umgewandelt, um zu senden
     * @param remainingBytes
     * @param stream
     * @return
     */
    public boolean writeData(long remainingBytes, InputStream stream) {
        writer.println(Instruction.DAT.name());
        writer.println(remainingBytes);
        byte[] data = new byte[1024 * 64];
        try {
            int count = 0;
            while ((count = stream.read(data, 0, (int) Math.min(remainingBytes, data.length))) >= 0 && remainingBytes > 0) {
                s.getOutputStream().write(data, 0, count);
                remainingBytes -= count;
            }
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = readLine();
        return s != null && s.equals(Instruction.ACK.name());
    }

    /** er versucht, eine bestimmte Datei vom Server zu holen, indem er ihren Namen angibt
     * @param name
     * @param oStream
     * @return
     */
    public boolean getFile(String name, OutputStream oStream) {
        writer.println(Instruction.GET.name());
        writer.println(name);
        String str = readLine();
        if (str == null || !str.equals(Instruction.DAT.name())) return false;
        str = readLine();
        if (str == null) return false;
        long size;
        try {
            size = Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        byte[] data = new byte[1024 * 64];
        try {
            int count;
            while ((count = reader.read(data, 0, (int) Math.min(size, data.length))) >= 0 && size > 0) {
                oStream.write(data, 0, count);
                size -= count;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**ermöglicht das Löschen der Datei, wenn der Kunde um Löschung bittet
     * @param name
     * @return
     */
    public boolean deleteFile(String name) {
        writer.println(Instruction.DEL.name());
        writer.println(name);
        String s = readLine();
        return s != null && s.equals(Instruction.ACK.name());
    }

 /*   private static Thread createClient(String name) {
        return new Thread(() -> {
            try {
                LaunchClient client = new LaunchClient(new Socket(InetAddress.getLocalHost(), 2000));
                System.out.println(Arrays.toString(client.doList()));
                File f = new File("client", "test.txt");
                client.putFile(name + ".txt");
                FileInputStream fis = new FileInputStream(f);
                client.writeData(f.length(), fis);
                System.out.println(Arrays.toString(client.doList()));
                File fs = new File("client", "client2☺.txt");
                if (fs.exists()) {
                    fs.delete();
                }
                fs.createNewFile();
                FileOutputStream fi = new FileOutputStream(fs);
                client.getFile("client2☺.txt", fi);
                fi.close();
                client.deleteFile(name + ".txt");
                System.out.println(Arrays.toString(client.doList()));
                fis.close();
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }*/

    public static void main(String[] args) throws InterruptedException {
        Gui.main(new String[0]);
    }
}
