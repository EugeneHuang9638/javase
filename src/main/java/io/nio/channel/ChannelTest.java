package io.nio.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 认识NIO中的channel
 *
 * channel在nio中用于源节点和目标节点的链接，eg：从起始站到终点站铁轨
 * channel内部不保存数据，其数据存储在byteBuffer(可以理解成在铁轨中运货的火车)中
 *
 * channel包含：
 * 本地IO和网络IO
 * 本地IO包括: FileInputStream/FileOutputStream/RandomAccessFile
 * 网络IO包括：Socket/ServerSocket/DatagramSocket
 *
 *
 * 本案例测试：
 * 使用FileChannel配置缓冲区来实现文件的复制
 *
 */
public class ChannelTest {


    public static void main(String[] args) throws Exception {

//        copyFileByByteBuffer();
        copyFileByChannel();

    }

    /**
     * 直接通过channel交互数据
     * @throws IOException
     */
    private static void copyFileByChannel() throws IOException {
        FileChannel inChannel = new FileInputStream("D:\\demo.txt").getChannel();
        FileChannel outChannel = new FileOutputStream("D:\\tmp\\demo.txt").getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
    }

    private static void copyFileByByteBuffer() throws IOException {
        FileChannel inChannel = new FileInputStream("D:\\demo.txt").getChannel();
        FileChannel outChannel = new FileOutputStream("D:\\tmp\\demo.txt").getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // inChannel将文件流读取到byteBuffer缓冲区中，大小为1024位
        while (inChannel.read(byteBuffer) != -1) {
            // 将内容写到文件中去，因此要读取byteBuffer的数据，因此要切换成读模式
            byteBuffer.flip();

            outChannel.write(byteBuffer);
            // 切换成写模式
            byteBuffer.clear();
        }
    }
}
