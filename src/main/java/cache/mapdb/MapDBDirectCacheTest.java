package cache.mapdb;


import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.IOException;

/**
 * 磁盘缓存：
 *   缓存是基于内存的，如果我们的程序重启了，数据肯定就没有了。如果想让缓存的数据落地，
 *   此时可以使用磁盘缓存。
 */
public class MapDBDirectCacheTest {
    private static DB db;
    private static HTreeMap<String , String> userMap;

    static {
        db = DBMaker
                //文件缓存，将缓存数据保存到文件中（需要保证路径存在，若文件不存在则会创建文件，若路径不存在会报错）
                .fileDB("D:\\user.db")
                //在java程序结束之前先关闭db，保证数据文件的完整性
                .closeOnJvmShutdown()
                .transactionEnable()
                .make();
        userMap = db.hashMap("userMap", Serializer.STRING, Serializer.STRING)
                // create() 创建新的集合。 如果集合存在，将扔出异常。
                // open() 打开存在的集合。 如果集合不存在，将扔出异常。
                // createOrOpen() 如果存在就打开, 否则创建。
                .createOrOpen();
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        new Thread(() -> {
            while(true) {
                System.out.println("userMap size:" + userMap.size());
                HTreeMap.KeySet<String> keys = userMap.getKeys();
                for (Object key : keys) {
                    System.out.print("key:" + key);
                    System.out.print("\tvlaue:" + userMap.get(key));
                }
                System.out.println();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

        // 写入缓存，当系统重启后，也能将文件load到缓存中来，直接读取缓存
//        userMap.put("hello", "world!");
//        Thread.sleep(1000);
//        userMap.put("hello1", "100000");
//        Thread.sleep(1000);
//        userMap.put("hello2", "abcdefg");
//        // 将缓存写入文件D:\\user.db中
//        db.commit();
        Thread.sleep(2000);

    }
}
