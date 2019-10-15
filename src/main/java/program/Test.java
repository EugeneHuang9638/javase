package program;

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