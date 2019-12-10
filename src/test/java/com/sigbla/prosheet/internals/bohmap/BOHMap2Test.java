package com.sigbla.prosheet.internals.bohmap;

import org.junit.*;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests related to an earlier stack overflow error issue
 *
 * @author cfelde
 */
public class BOHMap2Test {
    private Random random;
    private BOHMap map;
    
    public BOHMap2Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        random = new Random();
        map = new BOHMap(1024 * 1024);
    }
    
    @After
    public void tearDown() {
        map.clear();
    }
    
    @Test
    public void keySetHasNext() {
        assertFalse(map.keySet().iterator().hasNext());
    }
    
    @Test
    public void keySetNext() {
        boolean gotException = false;
        try {
            map.keySet().iterator().next();
        } catch (NoSuchElementException ex) {
            gotException = true;
        }
        
        assertTrue(gotException);
    }
    
    @Test
    public void valuesHasNext() {
        assertFalse(map.values().iterator().hasNext());
    }
    
    @Test
    public void valuesNext() {
        boolean gotException = false;
        try {
            map.values().iterator().next();
        } catch (NoSuchElementException ex) {
            gotException = true;
        }
        
        assertTrue(gotException);
    }
    
    @Test
    public void entrySetHasNext() {
        assertFalse(map.entrySet().iterator().hasNext());
    }
    
    @Test
    public void entrySetNext() {
        boolean gotException = false;
        try {
            map.entrySet().iterator().next();
        } catch (NoSuchElementException ex) {
            gotException = true;
        }
        
        assertTrue(gotException);
    }
}
