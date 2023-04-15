/*
    TODO: replace this with actual L0 sampler implementation
*/

import java.util.HashMap;

public class L0Sampler {
    HashMap<Long, Long> elementCount;
    public L0Sampler() {
        elementCount = new HashMap<>();
    }

    public void update(long element, long change) {
        if(elementCount.containsKey(element)) {
            long current_count = elementCount.get(element);
            assert current_count+change >= 0;
            if(current_count+change == 0) {
                elementCount.remove(element);
            }
            else {
                elementCount.put(element, current_count+change);
            }
        }
        else {
            assert change >= 0;
            elementCount.put(element, change);
        }
    }

    public long getSample() {
        long num_nonzero = elementCount.size();
        return 0;
    }
}
