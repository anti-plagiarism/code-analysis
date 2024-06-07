import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long N = sc.nextLong();
        for (long n = 1; n <= N; n++) {
            System.out.println(func(sc.nextLong()));
        }
    }

    public static int func(long n) {
        int cnt = 0;
        for (long i = 1; i * i <= n; i++) {
            if ((n & i) != 0) continue;
            cnt++;
            if (i * i != n) cnt++;
        }
        return cnt;
    }
}