package org.example;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BloomFilter {

    private int size;
    private List<HashFunction> hashFunctions;
    private List<Boolean> filterBuckets;

    private static BloomFilter instance;

    public synchronized BloomFilter getInstance() {
        if (instance == null) {
            instance = new BloomFilter();
        }
        return instance;
    }

    private Set<Integer> getKeyBuckets(String key) {
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

    public void add(String key) {
        Set<Integer> keyBuckets = getKeyBuckets(key);

        keyBuckets.parallelStream()
                .forEach(keyBucket -> filterBuckets.add(keyBucket, true));
    }

    public boolean isPresent(String key) {
        Set<Integer> keyBuckets = getKeyBuckets(key);

        return keyBuckets.stream().allMatch(keyBucket -> filterBuckets.get(keyBucket));
    }

}
