import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MetaAlgorithmTest {
    public static void main(String[] args) {
        int vertices = 10;
        int delta = 9;
        int colors_sampled = 5;
        SublinearSpace skeleton = new SublinearSpace(0, 0, 0, 0);
        List<Set<Long>> color_samples = skeleton.sampleColors(vertices, delta, colors_sampled);
        for(int v = 1; v < color_samples.size(); ++v) {
            System.out.println("Vertex " + v + ": " + color_samples.get(v));
        }
        System.out.println();
        List<Set<Long>> chi_sets = skeleton.calculateChiSets(delta, color_samples);
        for(int c = 1; c <= delta+1; ++c) {
            System.out.println("Color " + c + ": " + chi_sets.get(c));
        }
    }
}
