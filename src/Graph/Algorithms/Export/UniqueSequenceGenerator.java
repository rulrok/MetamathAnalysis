package Graph.Algorithms.Export;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Reuel
 */
public class UniqueSequenceGenerator {

    private long nextId;
    private final Map<Long, Long> inputs;

    public UniqueSequenceGenerator() {
        this.nextId = 1;
        this.inputs = new HashMap<>();
    }

    /**
     * For each input value, it returns a unique sequencial value. If the same
     * number is input again, its old associated value is returned.
     *
     * @param input
     * @return
     */
    public long uniqueSequencialId(long input) {

        if (inputs.containsKey(input)) {
            return inputs.get(input);
        } else {
            inputs.put(input, nextId);
            nextId++;
            return nextId;
        }
    }
}
