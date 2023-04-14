import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class SublinearSpace extends MetaAlgorithm {
    long NumVertices;
    long Delta;
    private double Epsilon;
    private double Alpha;
    private Graph<Long, DefaultEdge> ConflictGraph;

    public SublinearSpace(long numVertices, long delta, double epsilon, double alpha) {
        NumVertices = numVertices;
        Delta = delta;
        Epsilon = epsilon;
        Alpha = alpha;
    }

    //
    public List<Long> findColoring() {
        return null;
    }

    protected Graph<Long, DefaultEdge> constructConflictGraph() {
        return null;
    }

    protected List<Long> findProperListColoring() {
        return null;
    }
}
