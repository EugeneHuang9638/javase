package program;

public class TestReturnFinallyOrder {


    /**
     * finally先执行，再执行catch中的return
     * @param x
     * @return
     */
    public static int test(int x) {
        try {
            System.out.println("xxxx");
            int a = x / 0;
        } catch (Exception e) {
            return x + 1;
        } finally {
            System.out.println(x);
        }

        return 0;
    }


    public static void main(String[] args) {
        test(1);
    }
}
