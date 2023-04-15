import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/*
    Both the sublinear space and time algorithms use the following meta-algorithm:
        1. Sample O(log n) colors L(v) uniformly at random for each vertex
        2. For each color c, calculate Chi_c where vertex v is in Chi_c if v samples c
        3. Construct the conflict graph
        4. Find a proper list coloring of the conflict graph, given color sample L(v) for each vertex v
    It therefore makes sense to have both inherit from this interface and implement each unspecified step themselves
 */

public abstract class MetaAlgorithm {
    // Index i contains list of colors sampled by vertex i
    protected List<Set<Long>> sampleColors(long vertices, long delta, long colors_sampled) {
        List<Set<Long>> color_samples = new ArrayList<>();
        color_samples.add(null); // for 1-indexing
        // Use algorithm L for reservoir sampling
        // If selecting k elements out of n, average runtime of O(k(1+log(n/k)))
        // k = log(n), so sampling for n vertices is O(n log(n)), fast enough
        for(long v = 1; v <= vertices; ++v) {
            List<Long> v_sample = new ArrayList<>();
            for(long i = 1; i <= colors_sampled; ++i) {
                v_sample.add(i);
            }
            double W = Math.exp(Math.log(ThreadLocalRandom.current().nextDouble())/colors_sampled);
            long i = colors_sampled;
            while(i <= delta+1) {
                i += Math.floor(Math.log(ThreadLocalRandom.current().nextDouble())/Math.log(1-W)) + 1;
                if(i <= delta+1) {
                    v_sample.set(ThreadLocalRandom.current().nextInt(v_sample.size()), i);
                    W *= Math.exp(Math.log(ThreadLocalRandom.current().nextDouble())/colors_sampled);
                }
            }
            color_samples.add(new HashSet<>(v_sample));
        }
        return color_samples;
    }

    // Index i contains vertices which sample color i
    protected List<Set<Long>> calculateChiSets(long delta, List<Set<Long>> color_samples) {
        List<Set<Long>> chi_sets = new ArrayList<>();
        chi_sets.add(null);
        for(long c = 1; c <= delta+1; ++c) {
            chi_sets.add(new HashSet<>());
        }
        for(long v = 1; v < color_samples.size(); ++v) {
            for(long c : color_samples.get((int)v)) {
                chi_sets.get((int)c).add(v);
            }
        }
        return chi_sets;
    }

    protected abstract Graph<Long, DefaultEdge> constructConflictGraph();

    protected abstract List<Long> findProperListColoring();
}
