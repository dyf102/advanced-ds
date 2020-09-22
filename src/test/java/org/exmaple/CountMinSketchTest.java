package org.exmaple;

import org.example.CountMinSketch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CountMinSketchTest {

    CountMinSketch countMinSketch;
    @Before
    public void setup() {
        countMinSketch = new CountMinSketch();
    }


    @Test
    public void testAdd() {
        countMinSketch.add("hello");
        Assert.assertEquals(countMinSketch.getCount("hello"), 1);
    }

    @Test
    public void testGetCount() {

    }
}
