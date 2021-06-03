public class bunnySolution {
    public static String solution(long x, long y) {
        long ans = 0;
        for(long i = 1; i <= x; ++i)
        {
            ans += i;
        }
        for(long j = 1; j < y; ++j)
        {
            ans += (x+j-1);
        }
        return Long.toString(ans);
    }
    public static void main(String[] args) {
        System.out.println(solution(3,2));
    }
}