package program;

import java.util.ArrayList;

public class Test {

    public static void updateUser(User user) {

        user.setId(111);
    }

    private void test22(User user) {
        test11();
        this.updateUser(user);
    }

    private void test11() {

    }

    public static void main(String[] args) {
        User user = new User();
        System.out.println(user);

        Test.updateUser(user);
        System.out.println(user);


        ArrayList list = new ArrayList(101);
        list.add("1");
        list.set(6, 1);
        System.out.println(list.size());
    }
}

class User {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }
}