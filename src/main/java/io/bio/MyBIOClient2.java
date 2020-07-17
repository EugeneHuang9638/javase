package io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * BIO客户端
 */
public class MyBIOClient2 {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8800);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = null;
        while (true) {
            scanner = new Scanner(System.in);
            String next = scanner.next();
            if ("exit".equals(next)) {
                break;
            }
            outputStream.write(next.getBytes());
            outputStream.flush();
        }

    }
}
