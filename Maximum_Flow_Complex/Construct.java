import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;

import java.io.FileReader;

public class Construct {


    static class Sink {
        public int V;
        public ArrayList<Integer> demands;
        public Sink(int v, int T) {
            this.V = v;
            this.demands = new ArrayList<>(Collections.nCopies(T, 0));
        }
        @Override
        public int hashCode() {
            return V;
        }

        @Override
        public boolean equals(Object obj) {
            Sink compare = (Sink) obj;
            if(this.V == compare.V)
                return true;
            else
                return false;
        }
    }
    public static class CustomComparator implements Comparator<Sink> {
        @Override
        public int compare(Sink o1, Sink o2) {
            return o1.V - o2.V;
        }
    }

    public static void main(String[] args) {
        if (0 < args.length) {
            try (FileReader inFile = new FileReader(args[0])) {
                try (BufferedReader br = new BufferedReader(inFile)) {
                    String line;
                    int N = 0;
                    int M = 0;
                    int P = 0;
                    int T = 0;
                    int Q = 0;
                    if((line = br.readLine()) != null) {
                        String[] params = line.split(" ");

                        N = Integer.parseInt(params[0]);
                        M = Integer.parseInt(params[1]);
                        P = Integer.parseInt(params[2]);
                        T = Integer.parseInt(params[3]);
                        Q = Integer.parseInt(params[4]);
                    }
                    ArrayList<Integer> edges = new ArrayList<Integer>();
                    for(int i=0; i< M; i++) {
                        int L = 0;
                        int R = 0;
                        int C = 0;
                        if((line = br.readLine()) != null) {
                            String[] params = line.split(" ");

                            L = Integer.parseInt(params[0]);
                            R = Integer.parseInt(params[1]);
                            C = Integer.parseInt(params[2]);
                            edges.add(L);
                            edges.add(R);
                            edges.add(C);
                        }
                    }
                    ArrayList<Integer> availableSpots = new ArrayList();
                    if((line = br.readLine()) != null) {
                        String[] params = line.split(" ");
                        for(String p : params) {
                            availableSpots.add(Integer.parseInt(p));
                        }
                    }


                    ArrayList<Integer> demands = new ArrayList<Integer>();
                    for(int i =0; i< Q; i++) {
                        int J = 0;
                        int K = 0;
                        int A = 0;
                        if((line = br.readLine()) != null) {
                            String[] params = line.split(" ");

                            J = Integer.parseInt(params[0]);
                            K = Integer.parseInt(params[1]);
                            A = Integer.parseInt(params[2]);
                            demands.add(J);
                            demands.add(K);
                            demands.add(A);
                        }
                    }

                    // Write a model and data
                    // model
                    try{
                        PrintWriter writer = new PrintWriter("model.mod", "UTF-8");
                        String batteriesSpotsString = "";
                        String edgeString = ""; //just edges
                        String edgeCapFull = ""; //edges back and forward with capacity
                        String sinks = ""; //sinks
                        String times = ""; //times 1..T
                        int totalDemand = 0;

                        for(int i = 1; i <= N; i++) {
                            if (availableSpots.contains(i)) {
                                batteriesSpotsString += " " + i;
                            }
                        }

                        for(int i = 0; i < demands.size(); ) {
                            int ti = demands.get(i);
                            i++;
                            int vr = demands.get(i);
                            i++;
                            int de = demands.get(i);
                            i++;

                            totalDemand += de;
                            String[] splitted = sinks.split(" ");
                            Boolean there = false;
                            for(String tok : splitted) {
                                if(tok.equals(Integer.toString(vr))) {
                                   there = true;
                                }
                            }
                            if(!there)
                                sinks += " " + vr ;

                        }

                        for(int t = 1; t<=T; t++) {
                            times += " " + t;
                        }

                        for(int i = 0; i < edges.size(); ) {
                            int uv = edges.get(i);
                            i++;
                            int vr = edges.get(i);
                            i++;
                            int ca = edges.get(i);
                            i++;
                            edgeString += " " + uv + " " + vr ;
                            edgeCapFull += " " + uv + " " + vr + " " + ca + " " + vr + " " + uv + " " + ca ;
                        }

                        writer.println("param n, integer, >=0;");
                        writer.println("param tt, integer, >=0;");
                        writer.println("set T:={1..tt}; # the set of times");
                        writer.println("set V:={1..n}; #set of nodes");
                        writer.println("set V1 within V; # sources - supply nodes");
                        writer.println("set V3 within V; # sinks - demand nodes");
                        writer.println("set Vb within V; # batteries");
                        writer.println("set V2 := V diff V1 diff V3 diff Vb; #trans nodes");
                        writer.println("set V4 := V3 diff Vb;");
                        writer.println("set A within V cross V; # set of arcs");
                        writer.println("set E within V cross V; # edges");

                        writer.println("set S{i in V} :={j in V: (i,j) in A}; # the set of direct successors of i");
                        writer.println("set P{i in V} :={j in V: (j,i) in A}; # the set of direct predecessors of i");

                        writer.println("param supply{V1} >=0; # the supplies");
                        writer.println("param demand{(V3 union Vb),T} >=0, default 0; #the demands 1");

                        writer.println("param c{A}>=0, default 0; # the capacities of arcs");

                        writer.println("var f{(i,j) in A, t in T} >=0, <= c[i,j]; # the flow on arc (i,j) at time t");
                        writer.println("var storedInit{i in Vb, t in T} >=0; #stored electricity in a battery at vertex i at the beginning");
                        writer.println("var storedEnd{i in Vb, t in T} >= 0; # stored electricity in a battery at the end of time");
                        writer.println("var battery{i in Vb} >= 0; #battery capacity");

                        writer.println("minimize obj: sum{i in Vb} battery[i];");
                        writer.println("s.t. supplies{i in V1}: sum{t in T} (sum{j in S[i]} f[i,j,t] - sum{j in P[i]} f[j,i,t]) = supply[i]; # overall time (out>in) by supply");
                        writer.println("s.t. trans{i in V2, t in T}: sum{j in S[i]} f[i,j,t] - sum{j in P[i]} f[j,i,t]=0; #out=in");
                        writer.println("s.t. demands{i in V4, t in T}: sum{j in S[i]} f[i,j,t] - sum{j in P[i]} f[j,i,t]=-demand[i,t]; #out < in by demand");
                        writer.println("s.t. storageBalanceInit{i in Vb, t in T:t=0}: storedInit[i,t] = 0; #initially storage is zero");
                        writer.println("s.t. storageBalance{i in Vb, t in T:t>1}: storedInit[i,t] = storedEnd[i,t-1]; #what was stored stays");
                        writer.println("s.t. storageEnd{i in Vb, t in T}: storedEnd[i,t] = storedInit[i,t] + sum{j in P[i]} f[j,i,t] - sum{j in S[i]} f[i,j,t] - demand[i,t];");
                        writer.println("s.t. batterycap1{i in Vb, t in T}: storedInit[i,t] <= battery[i]; # stored doesnt exceed battery cap. at any time");
                        writer.println("s.t. batterycap2{i in Vb, t in T}: storedEnd[i,t] <= battery[i]; # stored doesnt exceed battery cap. at any time");

                        writer.println("solve;");
                        writer.println("printf \"#OUTPUT \\n\";");
                        writer.println("printf \"%d\", sum{i in Vb} battery[i];");
                        writer.println("printf \"\\n\";");
                        writer.println("for{t in T: t <= " + T + "} {");
                        writer.println("printf {(i,j) in E} : \"%d \", (if f[i,j,t] = 0 then (if i > j then f[j,i,t] else -f[j,i,t]) else (if i > j then f[i,j,t] else -f[i,j,t]));");
                        writer.println("printf \"\\n\";");
                        writer.println("}");
                        writer.println("printf \"#OUTPUT END\";");
                        writer.println("printf \"\\n\";");
                        writer.println("data;");
                        writer.println("param n := " + N + "; # num of vertices");
                        writer.println("param tt := " + T + "; #time");
                        writer.println("set E :=" + edgeString + "; # edges for output");
                        writer.println("param: A: c :=" +edgeCapFull + "; # edges f and bw");
                        writer.println("param: V1: supply := 1 " + totalDemand + "; #sources v s");
                        writer.println("set Vb :=" + batteriesSpotsString + "; # batteries");
                        writer.println("set V3 :=" + sinks + "; # sinks at some time");
                        writer.println("param demand:" + times + ":=");
                        ArrayList<Sink> demandSinks = new ArrayList<Sink>();
                        ArrayList<Integer> visitedV = new ArrayList<>();
                        for(int i = 0; i < demands.size(); ) {
                            int ti = demands.get(i);
                            i++;
                            int vr = demands.get(i);
                            i++;
                            int de = demands.get(i);
                            i++;
                            if (visitedV.contains(vr)) {
                                Sink f = new Sink(vr,T);
                                int ind = demandSinks.indexOf(f);
                                Sink s = demandSinks.get(ind);
                                s.demands.set(ti-1,de);
                                //demandSinks.set(ind,s);
                            }
                            else {
                                Sink s = new Sink(vr,T);
                                s.demands.set(ti-1,de);
                                demandSinks.add(s);
                            }
                            Collections.sort(demandSinks, new CustomComparator());
                            visitedV.add(vr);
                        }

                        for(int i = 0; i < demandSinks.size(); i++) {
                            Sink s = demandSinks.get(i);
                            writer.print(s.V);
                            for(int j = 0; j < s.demands.size();j++) {
                                writer.print(" " + s.demands.get(j));
                            }
                            writer.println("");
                        }
                        writer.println(";");
                        //writer.println("2 0 5");
                        //writer.println("3 1 0");
                        //writer.println("4 1 5; #sinks times := V3 d1 d2 newline");
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
}
