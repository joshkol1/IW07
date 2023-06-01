/*
    Data structure mentioned in lemma 4.5. Parameter of delta. Given pair of vertices (u, v),
        - output YES if u and v are delta-potential friend
        - output NO if u and v are not 2*delta-potential friend
        - arbitrary answer otherwise
 */

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PotentialFriendOracle {
    private long numVertices;
    private long maxDegree;
    private double sampleProbability;
    // sampled vertex v --> edge slots incident to v
    private HashMap<Long, List<Pair<Long, Long>>> sampledVertexEdgeSlots;
    // sampled vertex v --> v's k-edge sampler
    private HashMap<Long, kEdgeSampler> sampledVertexEdgeSamplers;
    private double delta; // delta = epsilon/10
    private Graph<Long, DefaultEdge> sampleIncidenceGraph;
    public PotentialFriendOracle(long num_vertices, long max_degree, double delta) {
        this.numVertices = num_vertices;
        this.maxDegree = max_degree;
        sampledVertexEdgeSlots = new HashMap<>();
        this.delta = delta;
        this.sampleProbability = 10*Math.log(this.numVertices)/(this.delta*this.delta*maxDegree);
        sampleVertices();
    }

    // Pick set S of vertices, sampled each with same probability
    private void sampleVertices() {
        for(long v = 1; v <= this.numVertices; ++v) {
            double rng_value = ThreadLocalRandom.current().nextDouble();
            if(rng_value <= this.sampleProbability) {
                sampledVertexEdgeSlots.put(v, new ArrayList<>());
            }
        }
    }

    public void processEdgeSlots(HashMap<Long, List<Pair<Long, Long>>> edge_slots) {
        for(long v : edge_slots.keySet()) {
            if(!sampledVertexEdgeSlots.containsKey(v)) {
                continue;
            }
            for(Pair<Long, Long> pair: edge_slots.get(v)) {
                sampledVertexEdgeSlots.get(v).add(pair);
            }
            sampledVertexEdgeSamplers.put(v, new kEdgeSampler(sampledVertexEdgeSlots.get(v), maxDegree));
        }
    }

    public void processQuery(long v1, long v2, int change) {
        if(sampledVertexEdgeSamplers.containsKey(v1)) {
            sampledVertexEdgeSamplers.get(v1).processQuery(v1, v2, change);
        }
        if(sampledVertexEdgeSamplers.containsKey(v2)) {
            sampledVertexEdgeSamplers.get(v2).processQuery(v1, v2, change);
        }
    }

    public void constructSampleIncidenceGraph() {
        this.sampleIncidenceGraph = new SimpleGraph<>(DefaultEdge.class);
        for(long v = 1; v <= this.numVertices; ++v) {
            this.sampleIncidenceGraph.addVertex(v);
        }
        for(long vertex : sampledVertexEdgeSamplers.keySet()) {
            // With high probability, kEdgeSampler returns all incident edges for each vertex
            List<Pair<Long, Long>> incident_edges = sampledVertexEdgeSamplers.get(vertex).outputEdges();
            for(Pair<Long, Long> edge : incident_edges) {
                this.sampleIncidenceGraph.addEdge(edge.getFirst(), edge.getSecond());
            }
        }
    }

    public boolean isPotentialFriend(long v1, long v2) {
        assert sampleIncidenceGraph != null;
        Set<Long> v1_incident = Graphs.neighborSetOf(this.sampleIncidenceGraph, v1);
        Set<Long> v2_incident = Graphs.neighborSetOf(this.sampleIncidenceGraph, v2);
        long common_neighbor_count = 0;
        for(long sample_vertex : this.sampledVertexEdgeSlots.keySet()) {
            if(v1_incident.contains(sample_vertex) && v2_incident.contains(sample_vertex)) {
                ++common_neighbor_count;
            }
        }
        return common_neighbor_count >= (1-1.5*this.delta)*this.maxDegree*this.sampleProbability;
    }
}
