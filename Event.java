import java.util.*;

public class Event {
    private List<Edge> G[]; // adjList (Forward Stage)
    private List<Edge> Ginverse[]; // adjList (Backward Stage)
    private int[] inDegreeCounts; // array storing in-degree counts
    private int[] outDegreeCounts; // array storing out-degree counts
    private int[] eeArray; // stores earliest event
    private int[] leArray; // stores latest event
    private ArrayStack keyStack; // allowing for new stacks to be used in dif. methods
    private ArrayList<Integer> criticalEvents = new ArrayList<Integer>(); // stores criticalEvents
    private ArrayList<String> criticalPaths = new ArrayList<String>(); // stores criticalPaths
    private String actPathList = ""; // fluid string that stores each path within itself
    private int lastVertex; // has the last vertex in graph
    private int longestPath; // stores the longestPath length

    public Event(int n) { // Event constructor, creating linked lists of events, and initializing arrays
        G = new LinkedList[n];
        Ginverse = new LinkedList[n];
        inDegreeCounts = new int[n];
        outDegreeCounts = new int[n];
        eeArray = new int[n];
        leArray = new int[n];
        lastVertex = n - 1;
        generateLinkedLists();
    }

    private void generateLinkedLists() { // helper method for generating adjacency lists (linked lists data
                                         // implementation)
        for (int i = 0; i < G.length; i++) {
            G[i] = new LinkedList<>();
            Ginverse[i] = new LinkedList<>();
        }
    }

    public void addEdge(int v, int w, int dur, String actEdge) { // method adds edge (data) to each part of list
        G[v].add(new Edge(v, w, dur, actEdge)); // edges get added to the end of each list, in accordance to headnode
                                                // index
        Ginverse[w].add(new Edge(v, w, dur, actEdge)); // edges get added to the end of each list (inverse
                                                       // implemenation)
    }

    public void processDegrees() { // processing degrees (in & out) for each vertex in the graph
        // placeholder keys
        int key;
        int key2;
        for (int i = 0; i < G.length; i++) {
            for (int j = 0; j < G[i].size(); j++) { // go through adjancency list
                Edge tempEdge = G[i].get(j); // retrieve edge
                key = tempEdge.getW(); // for that edge, get the vertex# it points to
                inDegreeCounts[key]++; // increment in-degree count for that node
                key2 = tempEdge.getV(); // for that edge, get its vertex# (inverse implementation)
                outDegreeCounts[key2]++; // increment out-degree count for that node
            }
        }
    }

    public void eeComputation() { // computing the earliest event times
        int startVertex = 0; // start here (starting vertex is always 0)
        int key; // placeholder for getting vertices it points to
        int duration; // store duration of each edge
        keyStack = new ArrayStack(); // stack of keys
        keyStack.push(startVertex); // push the original start vertex (0), which should have no predecessors.
        while (!keyStack.empty()) { // while stack is not empty
            startVertex = (Integer) keyStack.pop(); // pop top key value
            for (int j = 0; j < G[startVertex].size(); j++) { // go through the vertex's list
                Edge tempEdge = G[startVertex].get(j); // retrieve edge in that index
                key = tempEdge.getW(); // retrieve the vertex it points to
                duration = tempEdge.getDur(); // retrieve the duration
                if (eeArray[key] < eeArray[startVertex] + duration) { // eeArray is by default, all initialized to 0,
                    eeArray[key] = eeArray[startVertex] + duration; // if the duration is longer (including
                                                                    // predecessors), it will be stored
                }
                inDegreeCounts[key]--; // decrement inDegreeCounts for that key
                if (inDegreeCounts[key] == 0) { // once the inDegreeCounts reach 0, push onto stack for the next
                                                // processing
                    keyStack.push(key);
                }
            } // end of for loop
        } // end of while
          // before method ends, store the longest path value.
    }

    public void leComputation() { // computing the latest event times
        int startVertex = lastVertex; // here, we start from the LAST vertex
        int key; // placeholder for getting vertices it gets POINTED FROM
        int duration; // duration
        keyStack = new ArrayStack(); // stack of keys
        initializeLeArray(); // initialize leArray
        keyStack.push(startVertex); // we start with the last vertex, which should have no successors.
        while (!keyStack.empty()) {
            startVertex = (Integer) keyStack.pop(); // pop top key value
            for (int j = 0; j < Ginverse[startVertex].size(); j++) { // now we are traversing through the inverse
                                                                     // adjlist
                Edge tempEdge = Ginverse[startVertex].get(j);
                key = tempEdge.getV(); // retrieve the vertex it gets pointed from
                duration = tempEdge.getDur(); // retrieve the duration
                if (leArray[key] > leArray[startVertex] - duration) { // subtract duration from longest path length,
                    leArray[key] = leArray[startVertex] - duration; // if its smaller, it will be saved
                }
                outDegreeCounts[key]--; // decrement outDegrees for that key
                if (outDegreeCounts[key] == 0) { // when 0, push onto stack
                    keyStack.push(key);
                }
            } // end of for loop
        } // end of while
    }

    private void initializeLeArray() { // helper method for le computation
        longestPath = eeArray[eeArray.length - 1]; // get the longestPath (stored in the last index of eeArray)
        for (int i = 0; i < leArray.length; i++) {
            leArray[i] = longestPath; // make each index store the longest path
        }
    }

    public void findCriticalPath() { // this method will call other methods in preparation of the recursePath method
        findCriticalEvents();
        keyStack = new ArrayStack(); // stack that is used in the dfs search
        int duration = 0; // duration starts at 0
        recursePath(0, duration, keyStack); // start with the first vertex which headnode is 0
    }

    private void findCriticalEvents() { // create an array of critical event(s) useful for finding critical path(s)
        for (int i = 0; i < eeArray.length; i++) {
            if (eeArray[i] == leArray[i]) { // aka if the difference is zero, it's a critical event
                criticalEvents.add(i);
            }
        }
    }

    private void recursePath(int headNode, int duration, ArrayStack keyStack) { // dfs search to find critical paths
        for (int j = 0; j < G[headNode].size(); j++) { // traverse through this headnode's list
            Edge tempEdge = G[headNode].get(j); // get this edge on graph
            int key = tempEdge.getW(); // get vertex it points to
            if (criticalEvents.contains(key)) { // if it points to a critical event..
                String pathString = tempEdge.getActEdge(); // get act String
                int time = tempEdge.getDur(); // get duration (time)
                duration += time; // add to this duration
                keyStack.push(pathString); // pushing pathString for later trace
                if (duration < longestPath) { // if not yet reached the end of path,
                    // there is more to look for within the next node...
                    recursePath(key, duration, keyStack); // (vertex it points to, current duration, current stack)
                } else { // (duration == longestPath) aka no more paths beyond this node...
                    if (key == lastVertex) { // reached last vertex...(variable initialized in constructor)
                        actPathList = keyStack.printStack(); // save path
                        criticalPaths.add(actPathList); // save to criticalPathList
                    }
                }
                keyStack.pop(); // go back one path
                duration -= time; // going back, so need to subtract the time that was added
            } // end of if statement
        } // end of for loop (keeps looping if more elements to look through)
    } // end of method (recursive method)

    @Override
    public String toString() {
        String message = "";
        if (!isAcyclic()) { // If the graph is not acyclic, it is not a DAG.
            message += "\nThe given input is not a DAG. Cannot display invalid values...sorry!"; // will not compute
                                                                                                 // values for an
                                                                                                 // incorrect
            // graph
        } else { // it is a DAG!
            message += output(message); // displaying time results for given graph
            // message += debuggingTestCaseOne();
        }
        return message;
    }

    private boolean isAcyclic() { // helper method to check if the given input is a directed graph.
        return eeArray[0] == 0 && leArray[0] == 0; // if the first node (0), has an ee & le of 0, the graph is valid.
    }

    private String output(String message) { // computing output for given DAG
        message += "\nThe earliest time that each event may occur is listed below:";
        for (int i = 0; i < G.length; i++) {
            message += "\nEvent " + i + ": " + eeArray[i] + " unit time";
        }
        message += "\n\nThe latest time that each event may occur is listed below:";
        for (int i = 0; i < Ginverse.length; i++) {
            message += "\nEvent " + i + ": " + leArray[i] + " unit time";
        }
        message += "\n\nFollowing are the critical events:";
        for (int i = 0; i < criticalEvents.size(); i++) {
            message += "\nEvent: " + criticalEvents.get(i);
        }
        message += "\n\nThe following is the list of critical activities for each critical path:";
        for (int i = 0; i < criticalPaths.size(); i++) {
            message += "\n" + criticalPaths.get(i);
        }
        message += "\n\nThe longest path in this activity network has a duration of: \n" + longestPath + " unit time\n";
        return message;
    }
/*
    private String debuggingTestCaseOne() { // debugging for Test Case #1; adjlist data,..in/out degrees. (temp)
        String debugConsole = "";
        String debugConsole2 = "";
        for (int i = 0; i < G.length; i++) {
            debugConsole += "\nFor Head Node: " + "[" + i + "]" + " In Degree Count: " + inDegreeCounts[i];
            debugConsole += G[i];
        }
        for (int i = 0; i < G.length; i++) {
            debugConsole2 += "\nFor Head Node: " + "[" + i + "]" + " Out Degree Count: " + outDegreeCounts[i];
            debugConsole2 += Ginverse[i];
        }
        return debugConsole + "\n\n--*INVERSE*--\n" + debugConsole2;
    }
*/
} // end of class