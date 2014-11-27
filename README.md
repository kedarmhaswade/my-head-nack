my-head-nack
============

An interesting anagram and a knack (k silent ;))

Details
=======
The problem details are [here](https://docs.google.com/a/jymob.com/document/d/1NiKv-MjULOFyyc8f5w8R_EqvuPJ10wJVJgZhtTK9VKc/edit#).

Instructions
============

This project is developed and tested on Ubuntu 14.04. Here are the requirements:

1. Maven 3 (tested with Maven 3.0.3)
2. Java 7+ (tested with Java 7, 8)

_If all is well, running run.sh script should do everything and take you into a shell which is rudimentary, but quite
useful_. 

The file __graph.conf__ contains a textual representation of the graph to analyze:

``` text
# First line: number of vertices
# Each subsequent line: coach-id followed by a comma-separated list of student-id's
# Example:
# 10
# 1 2,3,4
# 3 4,5
# 4 6,7,8,9
# 9 10

If you want to create a graph (subgraph, please :-P) of KA users, you can drop it into this file and run the 'setup' command
inside the shell. If that runs well, perhaps this project can be used as a testbed to analyze infections and limit them.

Design Details
==============

It was evident that this is a graph problem! I came to know that there's a nice dynamic programming application hidden
underneath as I started doing the implementation. Graph G is an ordered pair (V,E) of two sets and the standard
adjacency list and adjacency matrix implementations are quite useful and perhaps enough. In this case however, I made
a decision earlier that the 'read' ratio to write ratio is quite high in this particular application. The application
is designed to serve multiple queries of infections. So, I decided to a simple array of vertices (aptly named vertices
in the code: ComponentBuilder.java)

The components are created using a more or less standard implementation of union-find data structure, although care
is taken to go through the graph configuration only once. As the graph (really, the array of vertices) is built from
the lines of 'coaches' or 'coached-by' relations, the components are also built. Since it's an O(1) operation to reach
a component given a reference to a vertex, the decision of maintaining an array of vertices is justified. Note that the
set of edges is not maintained at all!

A shell is built to provide a rudimentary user interface. This was useful to think of at the design stage mainly because
that provide me a glimpse of what the 'useful queries' might look like! So, this is built with the user in mind ;).

Testing
=======
The setup command sets up the components and loads the given graph in graph.conf. You can run setup as many times as you
want. This is helpful because you can just modify graph.conf and rerun the setup to analyze a new graph. I have tested
it with a few small graphs. I intend to write a class to generate a graph with a given number of vertices and desired
number of components (that's another interesting problem, I guess ...).

 
