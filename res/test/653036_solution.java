import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int t = in.nextInt();
        for (int i = 1; i<=t; i++){
            System.out.println(method(in.nextInt()));
        }
    }

    public static int method(int n){
        int cnt = 0;
        for (int i = 1; i * i <= n; i++) {
            if ((n & i) == 0) continue;
            cnt++;
            if (i * i != n)
                cnt++;
        }
        return cnt;
    }
}