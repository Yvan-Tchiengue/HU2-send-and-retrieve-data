package pis.hue2.common;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class DataSocketReader {

    private final DataInputStream stream;

    /** es handelt sich um den Konstruktor von DataSocketReader
     * @param s
     * @throws IOException
     */
    public DataSocketReader(Socket s) throws IOException {
        stream = new DataInputStream(s.getInputStream());
    }


    /**
     * ermöglicht es, die Socket-Daten, die in Bit in Zeichen sind, umzuwandeln, um Strings zu erhalten.
     * @return builder.toString()
     * @throws IOException
     */
    public String readLine() throws IOException {
        StringBuilder builder = new StringBuilder();
        int i, temp = 0, count = 0;
        while (true) {
            do {
                i = stream.read();
            } while (i == -1); //Wait until data arrives
            if (i == '\r') continue;
            if (i == '\n') break;
            //Utf8 Support
            if ((i & 0x80) > 0) {
                if (count-- > 0) {
                    temp = temp | ((i & 0x3F) << (6 * count));
                    if (count == 0) {
                        builder.append((char) temp);
                    }
                } else {
                    temp = i;
                    count = 1;
                    temp <<= 2;
                    if ((temp & 0x80) > 0) {
                        count = 2;
                        temp <<= 1;
                        if ((temp & 0x80) > 0) {
                            count = 3;
                            temp = (i & 0x7) << 18;
                        } else {
                            temp = (i & 0xF) << 12;
                        }
                    } else {
                        temp = (i & 0x1F) << 6;
                    }
                }
                continue;
            } else {
                temp = i;
            }
            //char
            builder.append((char) temp);
        }
        return builder.toString();
    }

    /**
     * sie liest eine Zeichenkette
     * @param data
     * @param offset
     * @param length
     * @return
     * @throws IOException
     */
    public int read(byte[] data, int offset, int length) throws IOException {
        return stream.read(data, offset, length);
    }

    /**es ermöglicht das Schließen des Streams
     * @throws IOException
     */
    public void close() throws IOException {
        stream.close();
    }

}
