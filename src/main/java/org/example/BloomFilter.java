package org.example;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BloomFilter<T> {

    private int size;
    private List<HashFunction<T>> hashFunctions;
    private List<Boolean> filterBuckets;

    private Set<Integer> getKeyBuckets(T key) {
        return hashFunctions.parallelStream()
                .map(hashFunction -> hashFunction.hash(key))
                .map(hashedKey -> hashedKey.chars()
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining()))
                .map(BigInteger::new)
                .map(numberHash -> numberHash.mod(BigInteger.valueOf(size)))
                .map(BigInteger::intValue)
                .collect(Collectors.toSet());
    }

    public void add(T key) {
        Set<Integer> keyBuckets = getKeyBuckets(key);

        keyBuckets.parallelStream()
                .forEach(keyBucket -> filterBuckets.add(keyBucket, true));
    }

    public boolean isPresent(T key) {
        Set<Integer> keyBuckets = getKeyBuckets(key);

        return keyBuckets.stream().anyMatch(keyBucket -> filterBuckets.get(keyBucket));
    }

}
