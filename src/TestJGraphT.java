import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class TestJGraphT {
    public static void main(String[] args) {
        Graph<Long, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for(long v = 1; v <= 5; ++v) {
            graph.addVertex(v);
        }

    }
}
