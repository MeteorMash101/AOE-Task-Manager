public class Edge {
    private int v, w; // vertices (start vertex, vertex it points to)
    private int dur; // activity duration
    private String actEdge; // activity edge name

    public Edge(int v, int w, int dur, String actEdge) {
        this.v = v;
        this.w = w;
        this.dur = dur;
        this.actEdge = actEdge;
    }

    // getters & setters
    public int getV() {
        return v;
    }

    public int getW() {
        return w;
    }

    public int getDur() {
        return dur;
    }

    public String getActEdge() {
        return actEdge;
    }

    public void setV(int v) {
        if (v >= 0) {
            this.v = v;
        }
    }

    public void setW(int w) {
        if (w >= 0) {
            this.w = w;
        }
    }

    public void setDur(int dur) {
        if (dur >= 0) {
            this.dur = dur;
        }
    }

    public void setActEdge(String actEdge) {
        this.actEdge = actEdge;
    }

    @Override
    public String toString() {
        String result = "";
        result += "\n(V1 #: " + v + ", to -> V2 #: " + w + ", Act.Edge Dur: " + dur + ", Act.Edge Name: " + actEdge
                + ")";
        return result;
    }
}