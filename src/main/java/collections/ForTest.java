package collections;

public class ForTest {


    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            for (int i1 = 0; i1 < 10; i1++) {
                if (i1 == 5) {
                    break;
                }
                System.out.print(i1);
            }
            System.out.println();
        }

    }
}
