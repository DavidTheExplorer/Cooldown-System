package dte.calmdown.utils;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CollectionUtils
{
    /**
     * Returns all elements from the {@code first collection} that don't exist in the {@code second collection}.
     *
     * @param <T> The type of the elements in both collections.
     * @param collection1 The first collection.
     * @param collection2 The second collection.
     * @return The different elements between the first and the second collections.
     */
    public static <T> List<T> getDifferences(Collection<T> collection1, Collection<T> collection2)
    {
        return collection1.stream()
                .filter(element -> !collection2.contains(element))
                .collect(toList());
    }
}
