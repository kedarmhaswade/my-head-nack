package org.kedar.kai;

import java.io.*;
import java.util.BitSet;
import java.util.Random;

/**
 * Generates a graph.conf file with given vertices, edges and max number of edges per vertex.
 * The edges are created at random.
 * Created by kedar on 12/2/14.
 */
public class GenGraph {
    public static void main(String[] args) throws IOException {
        if(args.length != 3)
            throw new IllegalArgumentException("required 3 args: nvertices, nedges, nmax-edges-per-node");
        int n = Integer.valueOf(args[0]);
        int m = Integer.valueOf(args[1]);
        int d = Integer.valueOf(args[2]);
        buildBasic(n, m, d);
    }

    /**
     * There could be multiple strategies to build this graph, but doing it using a random number generator
     * for the entire graph in this method.
     * @param n int number of vertices
     * @param m int number of edges
     * @param d int number of maximum edges from a particular vertices
     * @throws IOException
     */
    private static void buildBasic(int n, int m, int d) throws IOException {
//        System.out.printf("n=%d, m=%d, max=%d %n", n, m, d);
        BitSet[] lines = new BitSet[n+1];
        for (int i = 1; i < lines.length; i++)
            lines[i] = new BitSet(d+1);
        Random r = new Random(System.currentTimeMillis());
        int mm = 0; //number of edges so far
        while (mm < m) {
            int from = r.nextInt(n);
            int to = r.nextInt(n);
//            System.out.printf("%d->%d%n", from, to);
            if (from == to)
                continue;
            if (lines[from+1].cardinality() >= d)
                continue; //redo
            if (lines[from+1].get(to+1))
                continue; //redo
            lines[from+1].set(to+1);
            mm += 1;
        }
        write(System.out, n, lines);
    }

    private static void write(PrintStream out, int n, BitSet[] lines) throws IOException {
        out.println(n);
        int i = 1;
        while(i < lines.length) {
            BitSet line = lines[i];
            out.print(i);
            out.print(' ');
            boolean firstDone = false;
            for (int j = 1; j < line.length(); j++) {
                if (line.get(j)) {
                    if (!firstDone)
                        firstDone = true;
                    else
                        out.print(',');
                    out.print(j);
                }
            }
            out.println();
            i += 1;
        }
        out.close();
    }
}
