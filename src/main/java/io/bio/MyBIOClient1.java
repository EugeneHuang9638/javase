package io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * BIO客户端
 */
public class MyBIOClient1 {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8800);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = null;
        while (true) {
            scanner = new Scanner(System.in);
            // 设置scanner遇到换行符才读取数据，默认是遇到空格和换行都会读取数据的
            scanner.useDelimiter("\n");
            String next = scanner.next();

            if ("exit".equals(next)) {
                break;
            }

            outputStream.write(next.getBytes());
            outputStream.flush();
        }

    }
}
