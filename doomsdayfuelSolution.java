
/*
Doomsday Fuel
=============

Making fuel for the LAMBCHOP's reactor core is a tricky process because of the exotic matter involved. It starts as raw ore, then during processing, begins randomly changing between forms, eventually reaching a stable form. There may be multiple stable forms that a sample could ultimately reach, not all of which are useful as fuel. 

Commander Lambda has tasked you to help the scientists increase fuel creation efficiency by predicting the end state of a given ore sample. You have carefully studied the different structures that the ore can take and which transitions it undergoes. It appears that, while random, the probability of each structure transforming is fixed. That is, each time the ore is in 1 state, it has the same probabilities of entering the next state (which might be the same state).  You have recorded the observed transitions in a matrix. The others in the lab have hypothesized more exotic forms that the ore can become, but you haven't seen all of them.

Write a function answer(m) that takes an array of array of nonnegative ints representing how many times that state has gone to the next state and return an array of ints for each terminal state giving the exact probabilities of each terminal state, represented as the numerator for each state, then the denominator for all of them at the end and in simplest form. The matrix is at most 10 by 10. It is guaranteed that no matter which state the ore is in, there is a path from that state to a terminal state. That is, the processing will always eventually end in a stable state. The ore starts in state 0. The denominator will fit within a signed 32-bit integer during the calculation, as long as the fraction is simplified regularly. 

For example, consider the matrix m:
[
  [0,1,0,0,0,1],  # s0, the initial state, goes to s1 and s5 with equal probability
  [4,0,0,3,2,0],  # s1 can become s0, s3, or s4, but with different probabilities
  [0,0,0,0,0,0],  # s2 is terminal, and unreachable (never observed in practice)
  [0,0,0,0,0,0],  # s3 is terminal
  [0,0,0,0,0,0],  # s4 is terminal
  [0,0,0,0,0,0],  # s5 is terminal
]
So, we can consider different paths to terminal states, such as:
s0 -> s1 -> s3
s0 -> s1 -> s0 -> s1 -> s0 -> s1 -> s4
s0 -> s1 -> s0 -> s5
Tracing the probabilities of each, we find that
s2 has probability 0
s3 has probability 3/14
s4 has probability 1/7
s5 has probability 9/14
So, putting that together, and making a common denominator, gives an answer in the form of
[s2.numerator, s3.numerator, s4.numerator, s5.numerator, denominator] which is
[0, 3, 2, 9, 14].

Test cases
==========

Inputs:
    (int) m = [[0, 2, 1, 0, 0], [0, 0, 0, 3, 4], [0, 0, 0, 0, 0], [0, 0, 0, 0, 0], [0, 0, 0, 0, 0]]
Output:
    (int list) [7, 6, 8, 21]

Inputs:
    (int) m = [[0, 1, 0, 0, 0, 1], [4, 0, 0, 3, 2, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0]]
Output:
    (int list) [0, 3, 2, 9, 14]
*/
import java.lang.Math;
import java.util.Arrays;

public class doomsdayfuelSolution {
    // Represent the contents of m as a fraction, num/den
    private static long[][][] f;
    private static long[][][] r;
    private static long[][][] q;
    private static long[][][] fr;

    public static int[] solution(int[][] m) {
    // Identify Terminal Rows:
        boolean[] terminal_rows = new boolean[m.length];
        int term_count = 0;

        for (int i = 0; i < m.length; ++i) {
            int[] row = m[i];
            boolean flag = true;
            for (int item : row) {
                if (item != 0) {
                    flag = false;
                }
            }
            if (flag) {
                ++term_count;
            }
            terminal_rows[i] = flag;
        }
        // All states terminal
        if (terminal_rows[0] || term_count == m.length) {
            int[] solution = new int[term_count + 1];
            solution[0] = 1;
            for (int i = 1; i < term_count; ++i) {
                solution[i] = 0;
            }
            solution[solution.length - 1] = 1;
            return solution;
        }

    // Determine row denominators:
        int[] denoms = new int[m.length];
        for (int i = 0; i < m.length; ++i) {
            denoms[i] = 1;
            if (!terminal_rows[i]) {
                int tmp = 0;
                for (int col : m[i]) {
                    tmp += col;
                }
                denoms[i] = tmp;
            }
        }

        // Set up R and Q blocks
        r = new long[m.length-term_count][term_count][2];
        q = new long[m.length - term_count][m.length - term_count][2];
        int r_r = 0, r_c = 0, q_r = 0, q_c = 0;
        
        for(int i = 0; i<m.length;++i)
        {
            r_c = 0; q_c = 0;
            if(r_r == r.length || q_r == q.length)
            {
                break;
            }
            if(terminal_rows[i])
            {
                continue;
            }
            for(int j = 0; j<m.length; ++j)
            {
                if(terminal_rows[j])
                {
                    r[r_r][r_c] = reduce(new long[]{m[i][j],denoms[i]});
                    // r[r_r][r_c][0] = m[i][j];
                    // r[r_r][r_c][1] = denoms[i];
                    ++r_c;
                }
                else
                {
                    q[q_r][q_c] = reduce(new long[] { m[i][j], denoms[i] });
                    // q[q_r][q_c][0] = m[i][j];
                    // q[q_r][q_c][1] = denoms[i];
                    ++q_c;
                }
            }
            ++r_r;
            ++q_r;
        }

        f = matrix_inverse(matrix_subtraction(identity(q.length), q));
        fr = matrix_multiplication(f, r);
        int[] solution = new int[fr[0].length+1];
        long[][] s0 = fr[0];
        long lcm = 1;
        for(int i = 0; i<fr[0].length; ++i)
        {
            lcm = (lcm*s0[i][1])/gcd(lcm, s0[i][1]);
            solution[i] = (int)s0[i][0];
        }
        solution[solution.length-1] = (int)lcm;

        for(int i = 0; i < s0.length; ++i)
        {
            if(s0[i][1] != lcm)
            {
                solution[i] *= (lcm/s0[i][1]);
            }
        }
        return solution;
    }

    private static long[][][] matrix_subtraction(long[][][] lhs, long[][][] rhs)
    {
        long[][][] solution = new long[lhs.length][lhs[0].length][2];
        for(int i = 0; i < lhs.length; ++i)
        {
            for(int j = 0; j < lhs[0].length; ++j)
            {
                if(lhs[i][j][1] != rhs[i][j][1])
                {
                    lhs[i][j][0] *= rhs[i][j][1];
                    rhs[i][j][0] *= lhs[i][j][1];
                    lhs[i][j][1] *= rhs[i][j][1];
                }
                solution[i][j][0] = lhs[i][j][0] - rhs[i][j][0];
                solution[i][j][1] = lhs[i][j][1];
                solution[i][j] = reduce(solution[i][j]);
                if(solution[i][j][1] < 0)
                {
                    solution[i][j][1] *= -1;
                    solution[i][j][0] *= -1;
                }
            }
        }
        return solution;
    }

    private static long[][][] matrix_multiplication(long[][][] lhs, long[][][] rhs)
    {
        long[][][] product = new long[lhs.length][rhs[0].length][2];
        for(int i = 0; i<product.length; ++i)
        {
            for(int j = 0; j < product[0].length; ++j)
            {
                long[] cell = {0,1};
                for(int k = 0; k < rhs.length; ++k)
                {
                    long[] cell_product = { lhs[i][k][0] * rhs[k][j][0], lhs[i][k][1] * rhs[k][j][1]};
                    if(cell_product[1] != cell[1])
                    {
                        cell_product[0] *= cell[1];
                        cell[0] *= cell_product[1];
                        cell[1] *= cell_product[1];
                    }
                    cell[0] += cell_product[0];
                }
                product[i][j] = reduce(cell);
            }
        }
        return product;
    }

    private static long[][][] matrix_inverse(long[][][] operand)
    {
        long[][][] identity = identity(operand.length);
        long[][][] augmented = new long[operand.length][identity.length+operand.length][2];
        for(int i = 0; i < augmented.length; ++i){
            for(int j = 0; j < augmented[0].length; ++j)
            {
                if(j < operand.length)
                {
                    augmented[i][j][0] = operand[i][j][0]; 
                    augmented[i][j][1] = operand[i][j][1];
                }
                else
                {
                    augmented[i][j][0] = identity[i][j-operand.length][0];
                    augmented[i][j][1] = identity[i][j-operand.length][1];
                }
                
            }
        }

        int h = 0;
        int k = 0;
        while(h < augmented.length && k < augmented[0].length)
        {
            //Find k-th pivot
            int max_idx = k;
            float max_val = Math.abs(augmented[max_idx][k][0] / (float) augmented[max_idx][k][1]), cur_val;
            for (int i = h; i < augmented.length; i++) {
                cur_val = Math.abs(augmented[i][k][0] / (float) augmented[i][k][1]);
                if (cur_val > max_val) {
                    max_idx = i;
                    max_val = Math.abs(augmented[max_idx][k][0] / (float) augmented[max_idx][k][1]);
                }
            }
            //No pivot in current column
            if(augmented[max_idx][k][0] == 0)
                ++k;
            else
            {
                //Swap rows h and max_idx
                long[][] temp = augmented[h];
                augmented[h] = augmented[max_idx];
                augmented[max_idx] = temp;

                augmented[h] = scale_row(augmented[h], k);
                // Subtract Downwards
                for(int i = 0; i<augmented.length; ++i)
                {
                    if(i!=h)
                    {
                        // f := A[i, k] / A[h, k]
                        long[] factor = { (augmented[i][k][0]), augmented[i][k][1] };
                        if (factor[1] < 0) {
                            factor[0] *= -1;
                            factor[1] *= -1;
                        }
                        augmented[i][k][0] = 0;
                        augmented[i][k][1] = 1;
                        for (int j = k + 1; j < augmented[0].length; ++j) {
                            // A[i, j] := A[i, j] - A[h, j] * f
                            long[] scaled_b = new long[] { factor[0] * augmented[h][j][0],
                                    factor[1] * augmented[h][j][1] };
                            if (scaled_b[0] == 0)
                                scaled_b[1] = 1;
                            if (augmented[i][j][1] != scaled_b[1]) {
                                augmented[i][j][0] *= scaled_b[1];
                                scaled_b[0] *= augmented[i][j][1];
                                augmented[i][j][1] *= scaled_b[1];
                            }
                            augmented[i][j] = reduce(
                                    new long[] { augmented[i][j][0] - scaled_b[0], augmented[i][j][1] });
                        }
                    }             
                }
                ++h;
                ++k;
            }
        }

        long[][][] inverse = new long[augmented.length][augmented[0].length-identity.length][2];
        for(int i = 0; i < inverse.length; ++i)
        {
            for(int j = 0; j < inverse[0].length; ++j)
            {
                inverse[i][j] = augmented[i][j+operand.length];
            }
        }
        return inverse;
    }

    private static long[][] scale_row(long[][] row, int pivot)
    {
        long[] scalar = new long[]{row[pivot][0], row[pivot][1]};
        for(int i = pivot; i<row.length; ++i)
        {
            row[i][0] *= scalar[1];
            row[i][1] *= scalar[0];
            row[i] = reduce(row[i]);
        }
        return row;
    }

    private static long[] reduce(long[] operand)
    {
        long num = operand[0];
        long denom = operand[1];
        if(num == 0)
        {
            denom = 1;
            return new long[]{num, denom};
        }
        if(denom < 0)
        {
            num *= -1;
            denom *= -1;
        }
        long gcd = gcd(num, denom);
        return new long[]{num/gcd,denom/gcd};
    }

    private static long[][][] identity(int size)
    {
        long[][][] identity = new long[size][size][2];
        for (int i = 0; i < identity.length; ++i) {
            for (int j = 0; j < identity[0].length; ++j) {
                if (i == j)
                    identity[i][j][0] = 1;
                else
                    identity[i][j][0] = 0;
                identity[i][j][1] = 1;
            }
        }
        return identity;
    }

    private static long gcd(long a, long b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }

    public static void main(String[] args) {
        int[][] input = {
            { 0, 0, 12, 0, 15, 0, 0, 0, 1, 8},
            { 0, 0, 60, 0, 0, 7, 13, 0, 0, 0},
            { 0, 15, 0, 8, 7, 0, 0, 1, 9, 0},
            { 23, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            { 37, 35, 0, 0, 0, 0, 3, 21, 0, 0},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            };
        System.out.println(Arrays.toString(solution(input)));

        // System.out.println("\nPrinting Q:");
        // for (int[][] row : q) {
        //     StringBuilder output = new StringBuilder();
        //     output.append("[");
        //     for (int[] col : row) {
        //         output.append(Arrays.toString(col));
        //     }
        //     output.append("]");
        //     System.out.println(output);
        // }
        // System.out.println("\nPrinting F:");
        // for (int[][] row : f) {
        //     StringBuilder output = new StringBuilder();
        //     output.append("[");
        //     for (int[] col : row) {
        //         output.append(Arrays.toString(col));
        //     }
        //     output.append("]");
        //     System.out.println(output);
        // }
        // System.out.println("\nPrinting F*R:");
        // for (int[][] row : fr) {
        //     StringBuilder output = new StringBuilder();
        //     output.append("[");
        //     for (int[] col : row) {
        //         output.append(Arrays.toString(col));
        //     }
        //     output.append("]");
        //     System.out.println(output);
        // }

    }
}