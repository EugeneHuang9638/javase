package io.masterslavereactor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 7788);
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
        outputStream.close();
        socket.close();
    }
}
