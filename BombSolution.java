
/*
Bomb, Baby!
===========

You're so close to destroying the LAMBCHOP doomsday device you can taste it! But in order to do so, you need to deploy special self-replicating bombs designed for you by the brightest scientists on Bunny Planet. There are two types: Mach bombs (M) and Facula bombs (F). The bombs, once released into the LAMBCHOP's inner workings, will automatically deploy to all the strategic points you've identified and destroy them at the same time. 

But there's a few catches. First, the bombs self-replicate via one of two distinct processes: 
Every Mach bomb retrieves a sync unit from a Facula bomb; for every Mach bomb, a Facula bomb is created;
Every Facula bomb spontaneously creates a Mach bomb.

For example, if you had 3 Mach bombs and 2 Facula bombs, they could either produce 3 Mach bombs and 5 Facula bombs, or 5 Mach bombs and 2 Facula bombs. The replication process can be changed each cycle. 

Second, you need to ensure that you have exactly the right number of Mach and Facula bombs to destroy the LAMBCHOP device. Too few, and the device might survive. Too many, and you might overload the mass capacitors and create a singularity at the heart of the space station - not good! 

And finally, you were only able to smuggle one of each type of bomb - one Mach, one Facula - aboard the ship when you arrived, so that's all you have to start with. (Thus it may be impossible to deploy the bombs to destroy the LAMBCHOP, but that's not going to stop you from trying!) 

You need to know how many replication cycles (generations) it will take to generate the correct amount of bombs to destroy the LAMBCHOP. Write a function answer(M, F) where M and F are the number of Mach and Facula bombs needed. Return the fewest number of generations (as a string) that need to pass before you'll have the exact number of bombs necessary to destroy the LAMBCHOP, or the string "impossible" if this can't be done! M and F will be string representations of positive integers no larger than 10^50. For example, if M = "2" and F = "1", one generation would need to pass, so the answer would be "1". However, if M = "2" and F = "4", it would not be possible.

Test cases
==========

Inputs:
    (string) M = "2"
    (string) F = "1"
Output:
    (string) "1"

Inputs:
    (string) M = "4"
    (string) F = "7"
Output:
    (string) "4"
*/
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class BombSolution {

    private static class pair
    {
        final public BigInteger m;
        final public BigInteger f;

        pair() {
            m = BigInteger.ONE;
            f = BigInteger.ONE;
        }

        pair(BigInteger m_in, BigInteger f_in)
        {
            m = m_in;
            f = f_in;
        }

        public int hashCode()
        {
            return Objects.hash(m, f);
        }

        public boolean equals(Object o)
        {
            if(this == o)
                return true;
            if(!(o instanceof pair))
                return false;
            pair rhs = (pair) o;
            return (this.m.equals(rhs.m) && this.f.equals(rhs.f));
        }

        public String toString()
        {
            return "("+m.toString()+", "+f.toString()+")";
        }
    }

    private static HashMap<pair, Long> cache = new HashMap<pair, Long>();

    public static String solution(String x, String y) {
        BigInteger M = new BigInteger(x);
        BigInteger F = new BigInteger(y);
        BigInteger temp;

        BigInteger solution = BigInteger.ZERO;

        while(M.compareTo(BigInteger.ONE) >= 0 && F.compareTo(BigInteger.ONE) >= 0)
        {
            // System.out.println("Current values are: ("+M.toString()+", "+F.toString()+")");
            // System.out.println("Solution = "+solution);
            if(M.intValue() == 1 && F.intValue() == 1)
                return solution.toString();
            if(F.equals(BigInteger.ONE))
            {
                solution = solution.add(M.subtract(BigInteger.ONE));
                return solution.toString();
            }
            if (M.equals(BigInteger.ONE)) {
                solution = solution.add(F.subtract(BigInteger.ONE));
                return solution.toString();
            }
            if(M.compareTo(F) < 0)
            {
                temp = F.mod(M);
                solution = solution.add(F.divide(M));
                F = temp;
            }
            else
            {
                temp = M.mod(F);
                solution = solution.add(M.divide(F));
                M = temp;
            }
            // System.out.println("Current values are: (" + M.toString() + ", " + F.toString() + ")");
        }
        return "impossible";
    }

/*
    public static String solution(String x, String y) {
        pair oneone = new pair();
        cache.put(oneone, 0L);
        pair target = new pair(new BigInteger(x), new BigInteger(y));
        pair current;

        ArrayDeque<pair> stack = new ArrayDeque<pair>();
        stack.push(target);


        while(!stack.isEmpty())
        {
            if(stack.isEmpty())
                break;
            current = stack.pop();
            pair minus_m = new pair(current.m, current.f.subtract(current.m));
            if (minus_m.f.compareTo(BigInteger.ONE) < 0) 
            {
                cache.put(minus_m, Long.MAX_VALUE);
            }
            pair minus_f = new pair(current.m.subtract(current.f), current.f);
            if (minus_f.m.compareTo(BigInteger.ONE) < 0) 
            {
                cache.put(minus_f, Long.MAX_VALUE);
            }
            if(cache.containsKey(minus_m) && cache.containsKey(minus_f))
            {
                if(cache.get(minus_m) < Long.MAX_VALUE || cache.get(minus_f) < Long.MAX_VALUE)
                    cache.put(current, Math.min(cache.get(minus_m), cache.get(minus_f))+1);
                else
                    cache.put(current, Long.MAX_VALUE);
            }
            else
            {
                stack.push(current);
                if(!cache.containsKey(minus_m))
                {
                    stack.push(minus_m);
                }

                if(!cache.containsKey(minus_f))
                {
                    stack.push(minus_f);
                }
            }
        }

        Long ans = cache.get(target);
        if(ans.equals(Long.MAX_VALUE))
        {
            return "impossible";
        }
        return ans.toString();            
    }
*/
    public static void main(String[] args) {
        System.out.println("Test Case (0, 1). Expecting impossible: " + solution("0", "1"));
        System.out.println("Test Case (1, 1). Expecting 0: " + solution("1", "1"));
        System.out.println("Test Case (2, 1). Expecting 1: "+solution("2", "1"));
        System.out.println("Test Case (4, 7). Expecting 4: " + solution("4", "7"));
        System.out.println("Test Case (34323099453200774623564, 134987345). Expecting ?: " + solution("34323099453200774623564", "134987345"));
    }
}