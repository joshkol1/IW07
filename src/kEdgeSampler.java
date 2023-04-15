/*
    Implementation of data structure in proposition 4.2 of Assadi, Chen, Khanna.
    Given subset P of pairs of vertices and integer k, output set S of k edges from
    edges in P which appear in final graph, with high probability.
    TODO: replace this with something which uses L0 samplers; current space usage is O(P)
 */
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class kEdgeSampler {
    private HashMap<Pair<Long, Long>, Integer> pairsToTrack;
    private long k;
    public kEdgeSampler(List<Pair<Long, Long>> vertex_pairs, long k) {
        this.pairsToTrack = new HashMap<>();
        for(Pair<Long, Long> vp : vertex_pairs) {
            long first = vp.getFirst();
            long second = vp.getSecond();
            if(first > second) {
                long temp = first;
                first = second;
                second = temp;
            }
            pairsToTrack.put(new Pair<>(first, second), 0);
        }
        this.k = k;
    }

    public void processQuery(long v1, long v2, int change) {
        if(v1 > v2) {
            long temp = v1;
            v1 = v2;
            v2 = temp;
        }
        Pair<Long, Long> edge = new Pair<>(v1, v2);
        if(!pairsToTrack.containsKey(edge)) {
            return;
        }
        int count = pairsToTrack.get(edge);
        pairsToTrack.put(edge, count+change);
    }

    public List<Pair<Long, Long>> outputEdges() {
        List<Pair<Long, Long>> present_edges = new ArrayList<>();
        for(Pair<Long, Long> edge : pairsToTrack.keySet()) {
            if(pairsToTrack.get(edge) > 0) {
                present_edges.add(new Pair<>(edge.getFirst(), edge.getSecond()));
            }
        }
        Collections.shuffle(present_edges);
        List<Pair<Long, Long>> answer = new ArrayList<>();
        for(int i = 0; i < Math.min(this.k, present_edges.size()); ++i) {
            Pair<Long, Long> edge = present_edges.get(i);
            answer.add(new Pair<>(edge.getFirst(), edge.getSecond()));
        }
        return answer;
    }
}
