import java.util.*;

public class AOETaskManager {
    private static Event adjList;
    private static int numOfVertices;
    private static int numOfEdges;
    private static int v, w; // vertices (v1, v2)
    private static int dur; // activity duration
    private static String actEdge; // activity edge name

    public static void main(String[] args) {
        welcomeMessage();
        promptUser();

        adjList.processDegrees(); // process before & after ee/le computation.
        adjList.eeComputation();
        adjList.leComputation();

        adjList.processDegrees();
        adjList.findCriticalPath(); // invoke method to find critical path(s)
        System.out.println("\nOUTPUT:");
        System.out.println(adjList); // results
    }

    private static void welcomeMessage() { // welcome/inform user
        System.out.println(
                "Hello! Welcome to AOE Task Manager! Our program calculates critical paths and other useful information.");
        System.out.println("In this program, events are denoted as vertices, and activites are edges.");
        System.out.println("Please input some information to help get us started.");
    }

    private static void promptUser() { // prompt input from user
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the number of Events...(labeled as Event 0, 1, etc.)");
        numOfVertices = scan.nextInt();
        adjList = new Event(numOfVertices);
        System.out.println("Please enter the number of Activities...(labeled as a1, a2, etc.)");
        numOfEdges = scan.nextInt();
        for (int i = 1; i <= numOfEdges; i++) {
            actEdge = "a" + i;
            System.out.println(
                    "Enter the following values for " + actEdge + ": start vertex #, end vertex #, activity duration");
            scan.useDelimiter("\\D");
            v = scan.nextInt();
            w = scan.nextInt();
            dur = scan.nextInt();
            adjList.addEdge(v, w, dur, actEdge); // adds to (head index node, vertex it points to, activity duration,
                                                 // activity
            // edge#)
        }
        scan.close();
    }
} // end class
