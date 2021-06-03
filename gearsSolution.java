import java.util.Arrays;

public class gearsSolution {
    public static int[] solution(int[] pegs) {
        int[] gaps = new int[pegs.length-1];
        int[] solution = {-1,-1};
        int end = gaps.length-1;
        boolean even;
        //set parity bit
        if(gaps.length % 2 == 0)
            even = true;
        else
            even = false;
        
        //get size of gaps between pegs
        for(int i = 1; i < pegs.length; ++i)
        {
            gaps[i-1] = pegs[i]-pegs[i-1];
        }
        
        //reduce matrix to final column
        for(int i = 1; i < gaps.length; ++i)
        {
            gaps[i] -= gaps[i-1];
        }
        
        //check that no value in solution vector is negative
        if(even)
            gaps[end] *= -1;
        if(gaps[end] < 0)
            return solution;
        
        float last_val = (float)gaps[end];
        if(!even)
        {
            last_val /= 3;
        }
        last_val *= 2;
        
        for(int i = gaps.length-2; i >= 0; --i)
        {
            if((i % 2 == 0) && (gaps[i]-last_val < 0))
            {
                return solution;
            }
            if((i % 2 == 1) && (gaps[i]+last_val < 0))
            {
                return solution;
            }
        }
        
        if(even)
            solution = new int[]{2*gaps[end], 1};
        if(!even)
        {
            int s1 = 2*gaps[end];
            if(s1%3 == 0)
            {
                solution = new int[]{s1/3, 1};
            }
            else
            {
                solution = new int[]{2*gaps[end], 3};
            }
        }
        return solution;
    }

    public static void main(String[] args) {
        int[] input = {4, 17, 50};
        System.out.println("Testing "+Arrays.toString(input)+": ");
        int[] result = solution(input);
        System.out.println(Arrays.toString(result));
    }
}