package org.example;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class CountMinSketch {
    private final int[][] data;
    private static final int MAX_SIZE = 4096;
    private static final int NUM_HASH_FUN = 4;
    private static final byte[] HASH_KEY = "CountMinSketch".getBytes();
    private final int size;

    HashFunction md5HashFunc;
    HashFunction murmurFunc;
    HashFunction goodFastHashFunc;
    HashFunction crc32Func;
    public CountMinSketch(int n) {
        if (n > MAX_SIZE) {
            throw new CountMinSketchException(String.format("size is too large(%d <= %d)", n, MAX_SIZE));
        }
        this.size = n;
        this.data = new int[NUM_HASH_FUN][n];
        this.md5HashFunc = Hashing.hmacMd5(HASH_KEY);
        this.murmurFunc = Hashing.murmur3_32();
        this.goodFastHashFunc = Hashing.goodFastHash(32);
        this.crc32Func = Hashing.crc32c();
    }
    public CountMinSketch() {
        this(MAX_SIZE);
    }

    private int[] hash(String s) {
        int []result = new int[4];
        Charset charset = Charset.defaultCharset();
        result[0] = Math.abs(this.md5HashFunc.hashString(s, charset).asInt()) % this.size;
        result[1] = Math.abs(this.murmurFunc.hashString(s, charset).asInt()) % this.size;
        result[2] = Math.abs(this.crc32Func.hashString(s, charset).asInt()) % this.size;
        result[3] = Math.abs(this.goodFastHashFunc.hashString(s, charset).asInt()) % this.size;
        return result;
    }

    /**
     * add string into frequency table
     * @param s String
     */
    public void add(String s) {
        int[] idxs = this.hash(s);
        for(int i = 0; i < 4; i++) {
            data[i][idxs[i]] ++;
            if (data[i][idxs[i]] == Integer.MIN_VALUE) {
                throw new CountMinSketchException("Count is too large");
            }
        }
    }

    /**
     * return approximate count of key s
     * @param s String
     * @return approx count
     */
    public int getCount(String s) {
        int[] idxs = this.hash(s);
        int count = Integer.MAX_VALUE;
        for(int i = 0; i < 4; i++) {
            count = Math.min(data[i][idxs[i]], count);
        }
        return count;
    }

    /**
     *
     * @return size
     */
    public int getSize() {
        return size;
    }
}
