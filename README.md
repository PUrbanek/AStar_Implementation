# AStar_Implementation
## How to build and run your code.
Run the program with sbt run
## The heuristic that you used.
I used Manhattan Distance as a heuristic. The cost of moving is always 1, and movements allowed are only horizontal and vertical.
## What you used for tie-breakers when you had two nodes in your priority queue with the same priority.
For calculating I used 3 factors:
  1) Manhattan distance
  2) Cross product of the vector between node->goal and start->goal, to prefer paths that are along the straight line from the starting point to the goal.
  3) When 2 above are equal, I used the most recently added node.
## What are the advantages of having a more sophisticated heuristic?  Are there any disadvantages?
Generally, the more sophisticated heuristic is applied, the more expensive it is computationally. The advantage may be a path that is better suited to what is needed, or less expansions necessary when searching for a path.
## How do you know that a heuristic is admissable?  How do you know that a heuristic is monotonic?
A heuristic is admissible if it does not overestimate the cost for any node, which means that the estimated cost of travel from node X to node Y will never be larger than factual cost of travel from X to Y. Consequence of this is that an admissible heuristic will return the shortest path to the goal if it exists.
A heuristic is monotonic when cost of travel to the goal is the same as, or equal to, the cost of travel to the neighbouring node and the cost of travel from this node to the goal.

In the implementation provided, the way of breaking ties is slightly breaking admissibility of the heuristic. Cross product of vectors is multiplied by a factor of 0.01 to avoid impacting the heuristic in a meaningful way. 
## Does the way you break ties matter?
Yes, as it may break admissibility and/or monotonicity of the heuristic.
