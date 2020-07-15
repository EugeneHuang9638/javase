package io.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * 使用NIO实现文件的读写
 */
public class ReadAndWriteFile {


    public static void readFileNIO() {
        FileInputStream fin = null;

        try {
            URL url = new URL("http://49.235.135.230/index.html");
            ReadableByteChannel channel = Channels.newChannel(url.openStream());

            // 每次读取100个字节
            int capacity = 100;

            ByteBuffer bf = ByteBuffer.allocate(100);
            System.out.println("限制是: " + bf.limit() + ", 容量是: " + bf.capacity() + " 位置是: " + bf.position());

            int length = -1;
            while ((length = channel.read(bf)) != -1) {

                // 每次读取前要把buffer置空
                bf.clear();
                byte[] bytes = bf.array();
                System.out.write(bytes, 0, length);
                System.out.println();

            }

            channel.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[] args) {
        readFileNIO();
    }
}
