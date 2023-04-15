import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
public class ConflictGraphTest {
    public static void main(String[] args) {
        int num_vertices = 10;
        int delta = 5;
        int colors_sampled = 2;
        int k = 5;
        SublinearSpace coloring_finder = new SublinearSpace(num_vertices, delta, 0, 0, colors_sampled, k);
        List<Set<Long>> sampled_colors = coloring_finder.getSampledColors();
        System.out.println("Colors:");
        for(int v = 1; v <= num_vertices; ++v) {
            System.out.println("Vertex " + v + " samples colors: " + sampled_colors.get(v));
        }
        System.out.println();
        System.out.println("Edges:");
        for(int i = 0; i < 15; ++i) {
            int v1 = ThreadLocalRandom.current().nextInt(1, num_vertices + 1);
            int v2 = ThreadLocalRandom.current().nextInt(1, num_vertices + 1);
            if(v1 == v2) {
                continue;
            }
            System.out.println(v1+"-->"+v2);
            coloring_finder.processQuery(v1, v2, 1);
        }
        System.out.println();
        Graph<Long, DefaultEdge> conflict_graph = coloring_finder.constructConflictGraph();
        System.out.println("Conflict graph edges");
        for(DefaultEdge e : conflict_graph.edgeSet()) {
            System.out.println(e);
        }
    }
}
