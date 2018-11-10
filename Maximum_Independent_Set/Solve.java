import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Solve {

    int N;
    ArrayList<Vertex> G = new ArrayList<Vertex>();

    class Vertex implements Comparable<Vertex> {
        public int x;
        public int degree;
        public ArrayList<Vertex> neighbors = new ArrayList<Vertex>();

        public void addNeighbor(Vertex y) {
            this.neighbors.add(y);
            if (!y.neighbors.contains(y)) {
                y.neighbors.add(this);
                y.degree++;
            }
            this.degree++;

        }

        public void removeNeighbor(Vertex y) {
            this.neighbors.remove(y);
            if (y.neighbors.contains(y)) {
                y.neighbors.remove(this);
                y.degree--;
            }
            this.degree--;

        }

        @Override
        public int compareTo(Vertex o) {
            if (this.degree < o.degree) {
                return -1;
            }
            if (this.degree > o.degree) {
                return 1;
            }
            return 0;
        }

        public String toString() {
            return "" + x;
        }
    }

    void readGraph(BufferedReader bufReader) throws Exception {
        try {
            String[] strNM = bufReader.readLine().trim().replaceAll(" +", " ").split(" ");
            N = Integer.parseInt(strNM[0]);
            int M = Integer.parseInt(strNM[1]);
            G.clear();

            for (int i = 0; i < N; i++) {
                Vertex V = new Vertex();
                V.x = i;
                G.add(V);
            }

            for (int k = 0; k < M; k++) {
                String[] strArr = bufReader.readLine().trim().replaceAll(" +", " ").split(" ");
                int u = Integer.parseInt(strArr[0]);
                int v = Integer.parseInt(strArr[1]);
                Vertex vertU = G.get(u);
                Vertex vertV = G.get(v);
                vertU.addNeighbor(vertV);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    ArrayList<Vertex> getNeighbors(Vertex v) {
        int i = v.x;
        return G.get(i).neighbors;
    }

    ArrayList<Vertex> intersect(ArrayList<Vertex> a, ArrayList<Vertex> b) {
        ArrayList<Vertex> intersected = new ArrayList<Vertex>(a);
        intersected.retainAll(b);
        return intersected;
    }

    ArrayList<Vertex> union(ArrayList<Vertex> a, ArrayList<Vertex> b) {
        ArrayList<Vertex> united = new ArrayList<Vertex>(a);
        united.addAll(b);
        return united;
    }

    ArrayList<Vertex> removeNeighbors(ArrayList<Vertex> a, Vertex v) {
        ArrayList<Vertex> newList = new ArrayList<Vertex>(a);
        newList.removeAll(v.neighbors);
        return newList;
    }

    void startBronKerbosch() {
        ArrayList<Vertex> X = new ArrayList<Vertex>();
        ArrayList<Vertex> R = new ArrayList<Vertex>();
        ArrayList<Vertex> P = new ArrayList<Vertex>(G);
        PivotBronKerbosch(R, P, X, "");
    }

    ArrayList<ArrayList<Vertex>> maxCliques = new ArrayList<>();

    void print(ArrayList<Vertex> R) {
        ArrayList<Vertex> maxClique = new ArrayList<>();
        maxClique.addAll(R);
        maxCliques.add(maxClique);
//        for (Vertex v : R) {
//            System.out.print(" " + (v.x));
//        }
//        System.out.println();
    }

    // Taken from wiki
    void PivotBronKerbosch(ArrayList<Vertex> R, ArrayList<Vertex> P, ArrayList<Vertex> X, String pre) {

        if ((P.size() == 0) && (X.size() == 0)) {
            print(R);
            return;
        }
        ArrayList<Vertex> P1 = new ArrayList<Vertex>(P);
        // Find Pivot 
        Vertex u = getVertexWithMaxDegree(union(P, X));

        // P = P / Neighbours(u)
        P = removeNeighbors(P, u);

        for (Vertex v : P) {
            R.add(v);
            PivotBronKerbosch(R, intersect(P1, getNeighbors(v)), intersect(X, getNeighbors(v)), pre + "\t");
            R.remove(v);
            P1.remove(v);
            X.add(v);
        }
    }

    Vertex getVertexWithMaxDegree(ArrayList<Vertex> g) {
        Collections.sort(g);
        return g.get(g.size() - 1);
    }

    static void writeModel(ArrayList<ArrayList<Vertex>> maxCls, String[] args) {
        if (0 < args.length) {
            try (FileReader inFile = new FileReader(args[0])) {
                try (BufferedReader br = new BufferedReader(inFile)) {
                    String line;
                    int N = 0;
                    int M = 0;

                    if((line = br.readLine()) != null) {
                        String[] params = line.split(" ");

                        N = Integer.parseInt(params[0]);
                        M = Integer.parseInt(params[1]);
                    }
                    ArrayList<Integer> edges = new ArrayList<Integer>();
                    for(int i=0; i< M; i++) {
                        int L = 0;
                        int R = 0;
                        if((line = br.readLine()) != null) {
                            String[] params = line.trim().replaceAll(" +", " ").split(" ");
                            L = Integer.parseInt(params[0]);
                            R = Integer.parseInt(params[1]);
                            edges.add(L);
                            edges.add(R);
                        }
                    }

                    // Write a model and data
                    // model
                    try{
                        PrintWriter writer = new PrintWriter("model.mod", "UTF-8");
                        String edgeString = ""; //just edges
                        String dontHaveEdge = ""; //vertices without a single edge
                        ArrayList<String> maxCliquesString = new ArrayList<>(); // vertices that are in max clique

                        ArrayList<Integer> vertices = new ArrayList<>();
                        for (int i =0; i < N; i++) {
                            vertices.add(i);
                        }
                        for(int i = 0; i < edges.size(); ) {
                            int uv = edges.get(i);
                            i++;
                            int vr = edges.get(i);
                            i++;
                            // remove those that have at least one edge
                            if(vertices.contains(uv)) {
                                int ind = vertices.indexOf(uv);
                                vertices.remove(ind);
                            }

                            if(vertices.contains(vr)) {
                                int ind = vertices.indexOf(vr);
                                vertices.remove(ind);
                            }
                            edgeString += " " + uv + " " + vr ;
                        }

                        Collections.sort(maxCls, new Comparator<ArrayList>(){
                            public int compare(ArrayList a1, ArrayList a2) {
                                return a2.size() - a1.size();
                            }
                        });

                        for (ArrayList<Vertex> vs : maxCls) {
                            String maxClique = "";
                            for(Vertex v : vs) {
                                maxClique += " " + v.x;
                            }
                            maxCliquesString.add(maxClique);
                        }

                        for(int v : vertices) {
                            dontHaveEdge += " " + v;
                        }
                        writer.println("param n, integer, >=0;");
                        writer.println("set V:={0..n}; #set of nodes");
                        writer.println("set E within V cross V; # edges");
                        writer.println("set De within V;");
                        for(int i = 0; i< maxCliquesString.size();i++) {
                            writer.println("set Maxcl" + i +" within V;");
                        }
                        writer.println("var x{i in V} binary;");

                        writer.println("maximize obj: sum {i in V} x[i];");
                        writer.println("s.t. edgeunique{(u,v) in E}: x[u] + x[v] <= 1;");
                        writer.println("s.t. independent{i in De}: x[i] = 1; #those that don't have edge are in idp set");
                        for(int i = 0; i< maxCliquesString.size();i++) {
                            writer.println("s.t. clique"+i+": sum {i in Maxcl"+i+"} x[i] <= 1;");;
                        }

                        writer.println("solve;");
                        writer.println("printf \"#OUTPUT: \\n\";");
                        writer.println("printf \"%d\", sum{i in V} x[i];");
                        writer.println("printf \"\\n\";");
                        writer.println("for {i in V: x[i] = 1} {");
                        writer.println("printf \"%d\\n\",i;");
                        writer.println("}");
                        writer.println("printf \"#OUTPUT END\";");
                        writer.println("printf \"\\n\";");
                        writer.println("data;");
                        writer.println("param n := " + N + "; # num of vertices");
                        writer.println("set E :=" + edgeString + "; # edges for output");
                        writer.println("set De :=" + dontHaveEdge + "; # dont have edge");
                        for(int i = 0; i< maxCliquesString.size();i++) {
                            String maxClique = maxCliquesString.get(i);
                            writer.println("set Maxcl"+i+ " :=" + maxClique + ";");
                        }

                        writer.println("end;");
                        writer.close();

                    } catch (IOException e) {
                        // do something
                    }

                }catch(IOException e) {

                }
            }catch(IOException e) {

            }

        } else {
            System.err.println("Invalid arguments count:" + args.length);
            System.exit(0);
        }
    }

    public static void main(String[] args) {

        if (0 < args.length) {
            try {
                BufferedReader bufReader = new BufferedReader(new FileReader(args[0]));
                Solve maxCliques = new Solve();
                try {
                    maxCliques.readGraph(bufReader);
                    maxCliques.startBronKerbosch();

                    writeModel(maxCliques.maxCliques, args);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bufReader.close();
                    } catch (Exception f) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
