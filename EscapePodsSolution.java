import java.util.LinkedList;
import java.lang.Math;

public class EscapePodsSolution {
    private static int[][] network;
    private static int[][] preflow;
    private static int[] height;
    private static int[] excess;
    private static int[] c_arc;
    private static LinkedList<Integer> queue;

    public static int solution(int[] entrances, int[] exits, int[][] path) {
        setup_network(entrances, exits, path);
        queue = new LinkedList<Integer>();

        int i = 1;
        for (; i < network.length - 1; ++i) {
            queue.add(Integer.valueOf(i));
        }

        preflow_init();

        while (!queue.isEmpty()) {
            int u = queue.peek().intValue();
            int old_height = height[u];
            discharge(u);
            if (height[u] <= old_height) {
                queue.poll();
            }
        }

        int maxflow = 0;
        for (i = 0; i < network.length - 2; ++i) {
            maxflow += preflow[0][i];
        }

        return maxflow;
    }

    /*
     * Augments the provided path with a single source node, and a single sink node.
     * The new source node connects to the original source nodes with infinite
     * capacity for flow, and the old sink nodes connect to the new sink node with
     * infinite capacity for flow.
     * 
     * Chosen value of infinite here is the maximum possible flow outlined in the
     * problem specifications, though a higher value could also work.
     */
    private static void setup_network(int[] entrances, int[] exits, int[][] path) {
        network = new int[path.length + 2][path.length + 2];
        for (int i = 0; i < entrances.length; ++i) {
            network[0][entrances[i] + 1] = 2000000;
        }
        for (int r = 0; r < path.length; ++r) {
            for (int c = 0; c < path.length; ++c) {
                network[r + 1][c + 1] = path[r][c];
            }
        }
        for (int j = 0; j < exits.length; ++j) {
            network[exits[j] + 1][network.length - 1] = 2000000;
        }
        return;
    }

    /*
     * Initializes the preflow, height, excess, and current_arc (c_arc) lists.
     * 
     * Preflow is initialized with a saturating push from the source to all its
     * connected vertices
     * 
     * Height is initialized to 0 for every vertex, except the source, which has a
     * height equal to the number of vertices in the graph.
     * 
     * Excess is initialized with a 0 for every vertex except the source, which has
     * infinite excess.
     * 
     * c_arc is initialized to 0 and is simply used for the discharge process
     */
    private static void preflow_init() {
        preflow = new int[network.length][network.length];

        height = new int[network.length];
        height[0] = height.length;

        excess = new int[network.length];
        excess[0] = Integer.MAX_VALUE;
        
        c_arc = new int[network.length];
        for (int i = 1; i < network.length; ++i) {
            if (network[0][i] > 0) {
                push(0, i);
            }
        }

    }

    /*
     * Moves flow from u to v, up to the amount of excess in u, or the capacity of
     * edge (u, v).
     * 
     * If v is an intermediary node which gains excess from this process, and it is
     * not present in the discharge queue, it is reactivated.
     */
    private static void push(int u, int v) {
        int dif = Math.min(excess[u], network[u][v] - preflow[u][v]);
        preflow[u][v] += dif;
        preflow[v][u] -= dif;
        excess[u] -= dif;
        excess[v] += dif;

        // Reactivating v, adding to queue.
        if (v != 0 && v != network.length - 1 && excess[v] > 0 && !queue.contains(Integer.valueOf(v))) {
            queue.add(Integer.valueOf(v));
        }

    }

    /*
     * Raises the label of the node u to one higher than the lowest value among the
     * labels of the node's neighbors in the residual graph.
     */
    private static void relabel(int u) {
        int min = Integer.MAX_VALUE;
        for (int v = 0; v < height.length; ++v) {
            if ((network[u][v] - preflow[u][v]) > 0) {
                min = Math.min(min, height[v]);
                height[u] = min + 1;
            }
        }
    }

    /*
     * Continually pushes flow from, or relabels node u until it no longer has an
     * excess of flow. Acts upon nodes using a "current-arc" data structure, i.e. a
     * static cyclical ordering of its neighbors in the residual graph.
     */
    private static void discharge(int u) {
        while (excess[u] > 0) {
            if (c_arc[u] < network.length) {
                int v = c_arc[u];
                if ((network[u][v] - preflow[u][v]) > 0 && height[u] > height[v]) {
                    push(u, v);
                } else {
                    c_arc[u] += 1;
                }
            } else {
                relabel(u);
                c_arc[u] = 0;
            }
        }
    }


    public static void main(String args[])
    {
        //{0, 1}, {4, 5}, {{0, 0, 4, 6, 0, 0}, {0, 0, 5, 2, 0, 0}, {0, 0, 0, 0, 4, 4}, {0, 0, 0, 0, 6, 6}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}}
        int[] entrances = new int[]{0, 1};
        int[] exits = new int[]{4,5};
        int[][] input = new int[][]{
            {0, 0, 4, 6, 0, 0},
            {0, 0, 5, 2, 0, 0},
            {0, 0, 0, 0, 4, 4},
            {0, 0, 0, 0, 6, 6},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0}};
        
        System.out.println("Test Case 1:");
        System.out.println("Maximum flow equals: "+solution(entrances, exits, input));

        // relabel(1);
        // System.out.println("Labels, after relabel(1): " + Arrays.toString(height));
        // push(1,3);
        // System.out.println("Grid after push(1,3) = ");
        // for (int[] row : preflow) {
        //     System.out.println(Arrays.toString(row));
        // }


        //{0}, {3}, {{0, 7, 0, 0}, {0, 0, 6, 0}, {0, 0, 0, 8}, {9, 0, 0, 0}}
        entrances = new int[]{0};
        exits = new int[]{3};
        input = new int[][]{{0, 7, 0, 0}, {0, 0, 6, 0}, {0, 0, 0, 8}, {9, 0, 0, 0}};

        System.out.println("\nTest Case 2:");
        // System.out.println("Entrances = " + Arrays.toString(entrances));
        // System.out.println("Exits = " + Arrays.toString(exits));
        // System.out.println("Input = ");
        // for (int[] row : input) {
        //     System.out.println(Arrays.toString(row));
        // }
        
        // setup_network(entrances, exits, input);
        
        // System.out.println("Network = ");
        // for (int[] row : network) {
        //     System.out.println(Arrays.toString(row));
        // }
        
        // preflow_init();
        // System.out.println("Preflow = ");
        // for (int[] row : preflow) {
        //     System.out.println(Arrays.toString(row));
        // }
        // System.out.println("Excess: " + Arrays.toString(excess));
        // System.out.println("Labels: " + Arrays.toString(height));
        System.out.println("Maximum flow equals: " + solution(entrances, exits, input));

    }
}