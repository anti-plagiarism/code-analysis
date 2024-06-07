import java.util.*;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] groups = new int[n];
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            groups[i] = scanner.nextInt();
            for (int j = 0; j < groups[i]; j++) {
                list.add(scanner.nextInt());
            }
        }
        Arrays.sort(groups);
        Collections.sort(list);
        float res = 0;
        for (int i = 0; i < n; i++) {
            float sum = 0;
            for (int j = 0; j < groups[i]; j++) {
                int x = list.toArray().length - 1;
                sum += list.get(x);
                list.remove(x);
            }
            sum = sum / groups[i];
            res += sum;
        }
        System.out.println(res);

    }
}