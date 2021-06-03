public class cakeSolution {
    public static int solution(String x) {
        // Your code here
        String seq = new String();

        for (int i = 1; i < (x.length() / 2); ++i) {
            if (x.charAt(i) == x.charAt(0)) {
                if (x.length() % i == 0) {
                    seq = x.substring(0, i);
                    int j = i;
                    for (; j < x.length(); j += i) {
                        if (!seq.equals(x.substring(j, j + i))) {
                            break;
                        }
                    }
                    if (j >= x.length()) {
                        return (x.length() / i);
                    }
                }
            }
        }
        return 1;
    }
}