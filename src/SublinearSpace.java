import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SublinearSpace extends MetaAlgorithm {
    private final long numVertices;
    private final long maxDegree;
    private final double epsilon; // Parameter for HSS decomposition & # colors leading constant
    private final double alpha; // necessary for sparse vertex coloring
    private final long numColorsSampled; // O(alpha/epsilon^2 log n) in paper
    private final List<Set<Long>> sampledColors; // Index i: colors sampled by vertex i
    private final List<Set<Long>> chiSets; // Index i: vertices which sample color i
    private final long kValue; // O(log^2 n) in the paper, for conflict graph construction
    private final HashMap<Long, List<Pair<Long, Long>>> edgeSlots;
    private List<kEdgeSampler> conflictEdgeSamplers; // conflict edge sampler for each vertex
    private Graph<Long, DefaultEdge> conflictGraph; // Constructed at end of stream with edge samplers

    public SublinearSpace(long num_vertices, long max_degree, double epsilon, double alpha, long num_colors_sampled, long k_value) {
        this.numVertices = num_vertices;
        this.maxDegree = max_degree;
        this.epsilon = epsilon;
        this.alpha = alpha;
        this.numColorsSampled = num_colors_sampled;
        this.kValue = k_value;
        this.edgeSlots = new HashMap<>();
        // First, sample colors & calculate vertices which sample each color
        this.sampledColors = sampleColors(this.numVertices, this.maxDegree, this.numColorsSampled);
        this.chiSets = calculateChiSets(this.maxDegree, this.sampledColors);
        // Now follow steps in lemma 4.3 to get conflict graph edge samplers
        conflictEdgeSamplers = new ArrayList<>();
        conflictEdgeSamplers.add(null);
        for(long vertex = 1; vertex <= numVertices; ++vertex) {
            List<Pair<Long, Long>> edge_slots = calculateEdgeSlots(vertex);
            this.edgeSlots.put(vertex, edge_slots);
            conflictEdgeSamplers.add(new kEdgeSampler(edge_slots, this.kValue));
        }
    }

    public List<Set<Long>> getSampledColors() {
        return this.sampledColors;
    }

    // Find all v2 such that there is some color which both of v and v2 sample
    private List<Pair<Long, Long>> calculateEdgeSlots(long v) {
        List<Pair<Long, Long>> answer = new ArrayList<>();
        Set<Long> v_color_sample = sampledColors.get((int)v);
        for(long v2 = 1; v2 <= numVertices; ++v2) {
            if(v2 == v) {
                continue;
            }
            Set<Long> v2_color_sample = sampledColors.get((int)v2);
            for(long v_color : v_color_sample) {
                if(v2_color_sample.contains(v_color)) {
                    answer.add(new Pair<>(v, v2));
                    break;
                }
            }
        }
        return answer;
    }

    /*
        Method to handle edge insertions and deletions. change is +1 or -1
     */
    public void processQuery(long v1, long v2, int change) {
        conflictEdgeSamplers.get((int)v1).processQuery(v1, v2, change);
        conflictEdgeSamplers.get((int)v2).processQuery(v1, v2, change);
    }

    public Graph<Long, DefaultEdge> constructConflictGraph() {
        Graph<Long, DefaultEdge> conflict_graph = new SimpleGraph<>(DefaultEdge.class);
        for(long v = 1; v <= numVertices; ++v) {
            conflict_graph.addVertex(v);
        }
        for(long v = 1; v <= numVertices; ++v) {
            kEdgeSampler v_edge_sampler = this.conflictEdgeSamplers.get((int)v);
            List<Pair<Long, Long>> v_conflict_edges = v_edge_sampler.outputEdges();
            for(Pair<Long, Long> edge : v_conflict_edges) {
                conflict_graph.addEdge(edge.getFirst(), edge.getSecond());
            }
        }
        return conflict_graph;
    }

    protected List<Long> findProperListColoring() {
        return null;
    }
}
