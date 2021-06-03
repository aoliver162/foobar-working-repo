import java.math.BigInteger;
import java.lang.Math;
import java.util.HashMap;

public class fuelSolution {
    private static HashMap<BigInteger, Integer> cache = new HashMap<BigInteger, Integer>();

    public static int solution(String x) {
        BigInteger input = new BigInteger(x);

        if (cache.get(input) != null) {
            return cache.get(input);
        }

        if (input.equals(BigInteger.ONE))
            return 0;

        if ((input.mod(BigInteger.TWO)).equals(BigInteger.ZERO)) {
            BigInteger half_input = input.divide(BigInteger.TWO);
            cache.put(input, 1 + solution(half_input.toString()));
            return cache.get(input);
        } else {
            BigInteger plus = input.add(BigInteger.ONE);
            BigInteger minus = input.subtract(BigInteger.ONE);
            if (cache.get(plus) == null)
                cache.put(plus, solution(plus.toString()));
            if (cache.get(minus) == null)
                cache.put(minus, solution(minus.toString()));
            return 1 + Math.min(cache.get(plus), cache.get(minus));
        }
    }

    public static void main(String[] args) {
        System.out.println(solution("15"));
    }
}