package io.nio.bytebuffer;

import java.nio.ByteBuffer;

/**
 * 认识ByteBuffer
 *
 * ByteBuffer是nio中的一个缓存块（就是一个数组），
 * 用来存储数据的，其有四个属性：
 * limit: 限制整个buffer的容量大小  ---> limit有多大，表示byteBuffer中的数据内容有多大
 * position: 表示缓冲区正在操作数据的位置
 * capacity: byteBuffer缓存块数组的长度
 * mark: 备忘位置：表示缓冲区正在操作数据的上一个位置
 *
 * 通过get和put api来对byteBuffer的数据进行读取和填充
 *
 * clear和flip的区别
 *
 * clear源码：
 * position = 0;
 * limit = capacity;
 * mark = -1;
 * return this;
 *
 *
 * flip源码：
 * limit = position;
 * position = 0;
 * mark = -1;
 * return this;
 *
 * 可以看到区别就在于：limit的赋值
 * 在clear中，limit的值变成了capacity的值  --> limit变成capacity的值，若强行读取，可能会读取出空数据
 * 在flip中，limit的值变成了position的值   --> 实际上可以读取数据的长度
 *
 * 假设当前的limit = position = capacity  调用clear或flip所达到的效果是一样的
 * 假设当前的limit > position,   调用clear和flip的区别只有在读取操作时才有所体现，
 * 在clear后去读取数据，此时读取出来的数据包含空数据(limit 到 capacity的数据都会读取到)，
 * 在flip后去读取数据，此时读取的数据是真实的数据长度
 *
 *
 */
public class ByteBufferTest {


    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);

        // 看一下初始状态的四个属性的值
        System.out.println("初始状态下四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position());
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());

        System.out.println("-------------------分隔符--------------------");
        System.out.println("为byteBuffer填充一条字符串：avengerEug");
        byte[] bytes = "avengerEug".getBytes();
        byteBuffer.put(bytes);
        System.out.println("avengerEug字符串转成byte数组后的长度为：" + bytes.length);

        // ===> 此时在写两个字符进去的话，position会变成12，可以想象下，数组的长度为12，它的下标最大才11，如果下次操作下标为12的位置，那肯定会报错
//        bytes = "eu".getBytes();
//        byteBuffer.put(bytes);

        // ===> 结合上述写入"eu"字符的情况，由于position变成12了，此时若进行写数据，肯定直接报错
//        bytes = "g".getBytes();
//        byteBuffer.put(bytes);

        System.out.println("填充字符串后四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position()); // ===> 正在操作的位置变成了10，是因为avengerEug这个字符串转成byte数组后的长度为10
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());


        System.out.println("-------------------分隔符--------------------");
        System.out.println("从byteBuffer中读取我们刚刚放进的数据，调用flip方法切换成读模式");
        // 要从byteBuffer中读取数据时，需要调用flip方法切换成读模式
        byteBuffer.flip();
        // 将position置为0后，再写数据，测试是否会覆盖 ==> 的确会覆盖，但同时也会修改position的值
//        byteBuffer.put("xixi".getBytes());
        System.out.println("切换成读模式后四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit()); // ==> limit由12变成10了
        System.out.println("position: ==> " + byteBuffer.position()); // ==> position由10变成0了
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());
        System.out.println("limit由12变成10了, position由10变成0了。\n 得出结论：当调用flip切换成读模式时，整个byteBuffer的限制大小仅仅为10。\n " +
                "这是符合条件的，因为我们的缓冲区此时只有10个长度的数据，即avengerEug字符串转化成byte数组的长度");

        System.out.println("开始读取数据....");
        // 传入一个字节数组给get方法，字节数组的长度就是limit的值，执行完get方法后，会将数据填充到传入的byte数组中
        byte[] readByte = new byte[byteBuffer.limit() - byteBuffer.position()];
        byteBuffer.get(readByte);
        System.out.println("读取到的数据：" + new String(readByte));
        System.out.println("读取数据后的四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position()); // ==> position变成10了，正常。因为读取数据时操作到了10个位置
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());

        // 此时无法再继续写数据了，因为limit为10，position也为10了，已经达到界限了。
//         byteBuffer.put("as".getBytes());  // ==> 抛异常：java.nio.BufferOverflowException

        System.out.println("-------------------分隔符--------------------");
        System.out.println("调用clear方法，重回写模式，但缓冲区的数据会被清空，我们获取不到avengerEug数据了");
        // 重回写模式 --> 但要注意：缓存区的值还没有被删除，我依然可以读取缓存区的值
        byteBuffer.clear();
        System.out.println("重回写模式后的四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position());
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());
        System.out.println("结论：当调用clear方法后，byteBuffer会被清空，相当于回到最原始的情况了");

        // 即时切回成了写模式，缓存区的值依然可以读取。
        readByte = new byte[byteBuffer.limit() - byteBuffer.position()];
        byteBuffer.get(readByte);
        System.out.println("读取到的数据：" + new String(readByte));
    }
}
