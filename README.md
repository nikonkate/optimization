# Optimization Repository
Different Optimization Problem Solutions

# Maximum Independent Set
## Problem
Our goal is to find a maximum independent set in a given graph. According to the problem
statement the graph usually has a lot of cliques.
Below you can see the example of the given graph “small_n19.in”:
![Alt text](/graph.PNG "Graph")

## Model
Where 𝑊 is a subset of vertices with no edges at all (we thus know that those vertices must
be in the IDP set), and 𝐶 is one of the maximal cliques that we found using Bronh-Kerbosch
algorithm before we run our linear program (we thus know that only one vertex from this
clique should be in our independent set).
![Alt text](/model1.PNG "Model")

## How to run:
1) Compile Java source file using javac Solve.java
2) Parse data file to it as argument java Solve small_n19.in
3) Previous step will produce the model.mod file, run it using GLPK: glpsol -m
model.mod

# Maximum Flow of Electricity with Batteries and Storages
## Model
![Alt text](/model2a.PNG "Model")
![Alt text](/model2b.PNG "Model")
