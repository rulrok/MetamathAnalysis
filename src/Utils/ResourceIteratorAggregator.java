package Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.neo4j.graphdb.ResourceIterator;

public class ResourceIteratorAggregator<T> implements ResourceIterator<T> {

    private final List<ResourceIterator<T>> iterators;
    private ResourceIterator<T> actualIterator;

    public ResourceIteratorAggregator() {
        this(10);
    }

    public ResourceIteratorAggregator(int initialSize) {
        iterators = new ArrayList<>(initialSize);
    }

    public ResourceIteratorAggregator(ResourceIterator<T>... iterators) {
        this(iterators.length);
        this.iterators.addAll(Arrays.asList(iterators));

        actualIterator = this.iterators.remove(0);
    }

    public void addIterator(ResourceIterator<T> iterator) {
        if (actualIterator == null) {
            actualIterator = iterator;
        } else {
            iterators.add(iterator);
        }
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasNext() {

        if (actualIterator.hasNext()) {
            return true;
        } else if (iterators.isEmpty()) {
            return false;
        } else {
            actualIterator = iterators.remove(0);
            return actualIterator.hasNext();
        }
    }

    @Override
    public T next() {
        return actualIterator.next();
    }

}
