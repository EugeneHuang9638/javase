package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * BIO客户端
 */
public class MyBIOClient {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8800);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("avengerEug`1".getBytes());
        outputStream.write("avengerEug`2".getBytes());
        outputStream.write("avengerEug`3".getBytes());
        outputStream.write("avengerEug`4".getBytes());
        outputStream.write("avengerEug`5".getBytes());
        outputStream.flush();

        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) != -1) {
            System.out.println(new String(bytes, 0, length));
        }

        outputStream.close();
    }
}
