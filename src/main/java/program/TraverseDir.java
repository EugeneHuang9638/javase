package program;

import java.io.File;

/**
 * 使用递归遍历文件名
 */
public class TraverseDir {

    public static void recursive(File file) {
        if (!file.isDirectory()) {
            System.out.println(file.getPath());
        } else {
            File[] files = file.listFiles();
            for (File file1 : files) {
                recursive(file1);
            }
        }
    }


    public static void main(String[] args) {
        recursive(new File("C:\\Users"));
    }
}
