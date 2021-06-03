
/*
Gearing Up For Destruction
==========================

As Commander Lambda's personal assistant, you've been assigned the task of configuring the LAMBCHOP
doomsday device's axial orientation gears. It should be pretty simple - just add gears to create the
appropriate rotation ratio. But the problem is, due to the layout of the LAMBCHOP and the complicated
system of beams and pipes supporting it, the pegs that will support the gears are fixed in place.

The LAMBCHOP's engineers have given you lists identifying the placement of groups of pegs along various
support beams. You need to place a gear on each peg (otherwise the gears will collide with unoccupied
pegs). The engineers have plenty of gears in all different sizes stocked up, so you can choose gears
of any size, from a radius of 1 on up. Your goal is to build a system where the last gear rotates at
twice the rate (in revolutions per minute, or rpm) of the first gear, no matter the direction. Each
gear (except the last) touches and turns the gear on the next peg to the right.

Given a list of distinct positive integers named pegs representing the location of each peg along
the support beam, write a function answer(pegs) which, if there is a solution, returns a list of two
positive integers a and b representing the numerator and denominator of the first gear's radius in its
simplest form in order to achieve the goal above, such that radius = a/b. The ratio a/b should be greater
than or equal to 1. Not all support configurations will necessarily be capable of creating the proper
rotation ratio, so if the task is impossible, the function answer(pegs) should return the list [-1, -1].

For example, if the pegs are placed at [4, 30, 50], then the first gear could have a radius of 12,
the second gear could have a radius of 14, and the last one a radius of 6. Thus, the last gear would
rotate twice as fast as the first one. In this case, pegs would be [4, 30, 50] and answer(pegs) should
return [12, 1].

The list pegs will be given sorted in ascending order and will contain at least 2 and no more than 
20 distinct positive integers, all between 1 and 10000 inclusive.

Test cases
==========

Inputs:
    (int list) pegs = [4, 30, 50]
Output:
    (int list) [12, 1]

Inputs:
    (int list) pegs = [4, 17, 50]
Output:
    (int list) [-1, -1]
*/
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