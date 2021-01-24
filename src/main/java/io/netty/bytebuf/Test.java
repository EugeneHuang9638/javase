package io.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class Test {

    public static void main(String[] args) {
        operateByteBuf();
        generateContentByteBuf("avengerEug");
    }

    private static void operateByteBuf() {
        /**
         * 创建bytebuf对象，该对象内部包含一个字节数组，长度为10
         * 通过readerIndex和writerIndex和capacity，将buffer分成三个区域
         * 已经读取的区域：[0, readerIndex)
         * 可读取的区域：[readerIndex, writerIndex)
         * 可写区域：[writerIndex, capacity)
         */
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println("byteBuf = " + byteBuf);


        for (int i = 10; i < 18; i++) {
            byteBuf.writeByte(i);
        }
        System.out.println("写了数据后的bytebuf = " + byteBuf);

        for (int i = 0; i < 5; i++) {
            System.out.println(byteBuf.getByte(i));
        }
        System.out.println("使用getByte获取数据后的byteBuf = " + byteBuf);

        for (int i = 0; i < 5; i++) {
            System.out.println(byteBuf.readByte());
        }
        System.out.println("使用readByte后 = " + byteBuf);
    }

    private static void generateContentByteBuf(String content) {
        // 使用Unpooled工具类创建bytebuf
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        System.out.println("------------------------------");
        System.out.println("根据内容生成的ByteBuf = " + byteBuf + " 默认的数组长度为30");

        if (byteBuf.hasArray()) {
            // 将byteBuf的内容转成字节数组
            byte[] array = byteBuf.array();
            System.out.println("转成byte字节数组后的长度：" + array.length);

            // 将字节数组转成字符串
            String deSerializable = new String(array, CharsetUtil.UTF_8);
            System.out.println("将字节数组转成字符串：" + deSerializable + " 注意下它的长度为30，其实avengerEug后面还输出了许多的空格");

            // 转成字符串后的byteBuf
            System.out.println("转成字符串后的byteBuf = " + byteBuf);

            System.out.println("readerIndex = " + byteBuf.readerIndex());
            System.out.println("writerIndex = " + byteBuf.writerIndex());
            System.out.println("capacity = " + byteBuf.capacity());

            System.out.println("获取第一个位置上的字节，转正ascii码输出 " + byteBuf.getByte(0));

            int readableLength = byteBuf.readableBytes();
            System.out.println("可读的字节数长度：" + readableLength);

            System.out.println("获取可读数据的所有数据，并转成char类型");
            for (int i = 0; i < readableLength; i++) {
                System.out.println((char) byteBuf.getByte(i));
            }

            System.out.println("从第一个位置开始，读取6个数据：" + byteBuf.getCharSequence(0, 6, CharsetUtil.UTF_8));
            System.out.println("从第6个位置开始，读取6个数据，如果可读的数据不够6个长度，则以实际的读取数量为准" + byteBuf.getCharSequence(6, 2, CharsetUtil.UTF_8));

        }
    }
}
