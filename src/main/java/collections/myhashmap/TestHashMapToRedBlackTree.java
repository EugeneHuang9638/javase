package collections.myhashmap;


import java.util.HashMap;

public class TestHashMapToRedBlackTree {

    public static void main(String[] args) {
        HashMap<Student, Integer> map = new HashMap<>();

        for (int i = 0; i < 20; i++) {
            System.out.println(new Student(i).hashCode());
            map.put(new Student(i), i);
            map.put(null, 1);
        }

        System.out.println(1);

    }

    private static class Student {

        transient private int userId;

        public Student(int userId) {
            this.userId = userId;
        }


        // 所有的student对象的hashCode都为1
        // 所以不停的put元素进hashMap，
        // 最终得到的index都是一样的, 来测试红黑树的情况
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return userId == student.userId;
        }

        @Override
        public String toString() {
            return "";

        }
    }
}

