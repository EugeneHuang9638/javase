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
 */
public class ByteBufferTest {


    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

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

        System.out.println("填充字符串后四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position()); // ===> 正在操作的位置变成了10，是因为avengerEug这个字符串转成byte数组后的长度为10
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());


        System.out.println("-------------------分隔符--------------------");
        System.out.println("从byteBuffer中读取我们刚刚放进的数据，调用flip方法切换成读模式");
        // 要从byteBuffer中读取数据时，需要调用flip方法切换成读模式
        byteBuffer.flip();
        System.out.println("切换成读模式后四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit()); // ==> limit由1024变成10了
        System.out.println("position: ==> " + byteBuffer.position()); // ==> position由10变成0了
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());
        System.out.println("limit由1024变成10了, position由10变成0了。\n 得出结论：当调用flip切换成读模式时，整个byteBuffer的限制大小仅仅为10。\n " +
                "这是符合条件的，因为我们的缓冲区此时只有10个长度的数据，即avengerEug字符串转化成byte数组的长度");

        System.out.println("开始读取数据....");
        // 传入一个字节数组给get方法，字节数组的长度就是limit的值，执行完get方法后，会将数据填充到传入的byte数组中
        byte[] readByte = new byte[byteBuffer.limit()];
        byteBuffer.get(readByte);
        System.out.println("读取到的数据：" + new String(readByte));
        System.out.println("读取数据后的四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position()); // ==> position变成10了，正常。因为读取数据时操作到了10个位置
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());

        // 此时无法再继续写数据了，因为limit为10，position也为10了，已经达到界限了。
        // byteBuffer.put("asdfsdaf".getBytes());  // ==> 抛异常：java.nio.BufferOverflowException

        System.out.println("-------------------分隔符--------------------");
        System.out.println("调用clear方法，重回写模式，但缓冲区的数据会被清空，我们获取不到avengerEug数据了");
        // 重回写模式
        byteBuffer.clear();
        System.out.println("重回写模式后的四个属性的值：");
        System.out.println("limit: ==> " + byteBuffer.limit());
        System.out.println("position: ==> " + byteBuffer.position());
        System.out.println("capacity: ==>" + byteBuffer.capacity());
        System.out.println("mark: ==>" + byteBuffer.mark());
        System.out.println("结论：当调用clear方法后，byteBuffer会被清空，相当于回到最原始的情况了");


    }
}
