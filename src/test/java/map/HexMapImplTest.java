package map;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HexMapImplTest {

    private static final int VALUE_COUNT = 10000;

    @Test
    public void testAgainstJavaHashMap() {
        Random rand = new Random(5656778386878L);

        Map<Hex, Integer> javaMap = new HashMap<>(HexMapImpl.CAPACITY);
        HexMap<Integer> hexMap = new HexMapImpl<>();

        for (int i = 0; i < VALUE_COUNT; i++) {
            Hex hex = Hex.at(rand.nextInt(150), rand.nextInt(150));
            Integer value = rand.nextInt();
            javaMap.put(hex, value);
            hexMap.put(hex, value);
        }

        assertEquals(javaMap.size(), hexMap.size());
        assertEquals(javaMap.keySet(), hexMap.hexes());
        for (Hex hex : hexMap.hexes()) {
            assertEquals(javaMap.get(hex), hexMap.get(hex));
        }
    }

    @Test
    public void testCollisions() {
        class HexWithCollision extends Hex {

            private HexWithCollision(int line, int diag) {
                super(line, diag);
            }

            @Override
            public int hashCode() {
                return 1;
            }
        }

        HexMap<Integer> map = new HexMapImpl<>();

        for (int i = 0; i < VALUE_COUNT; i++) {
            map.put(new HexWithCollision(i, i), i);
        }

        assertEquals(VALUE_COUNT, map.size());
        assertEquals(1/VALUE_COUNT, map.getHashFactor(), 0.001);
        int iterations = 0;
        for (Hex hex : map.hexes()) {
            assertEquals(hex.getLine(), (int) map.get(hex));
            iterations++;
        }
        assertEquals(VALUE_COUNT, iterations);
    }

    @Test
    public void comparePerfWithJavaHashMap() {
        warmup();

        // Generate a bunch of datas
        Random rand = new Random(1434164938326810L);
        Map<Hex, Integer> datas = new HashMap<>();
        for (int i = 0; i < 150; i++) {
            Hex hex = Hex.at(rand.nextInt(150), rand.nextInt(150));
            Integer value = rand.nextInt();
            datas.put(hex, value);
        }

        Stopwatch stopwatch = Stopwatch.createUnstarted();

        HashMap<Hex, Integer> javaMap = new HashMap<>(HexMapImpl.CAPACITY);

        stopwatch.reset().start();
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            javaMap.put(entry.getKey(), entry.getValue());
        }
        long javaPutTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        stopwatch.reset().start();
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            assertEquals(entry.getValue(), javaMap.get(entry.getKey()));
        }
        long javaGetTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        stopwatch.reset().start();
        for (Hex hex : javaMap.keySet()) {
            assertTrue(datas.containsKey(hex));
        }
        long javaIterTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        HexMap<Integer> hexMap = new HexMapImpl<>();

        stopwatch.reset().start();
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            hexMap.put(entry.getKey(), entry.getValue());
        }
        long hexPutTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        stopwatch.reset().start();
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            assertEquals(entry.getValue(), hexMap.get(entry.getKey()));
        }
        long hexGetTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        stopwatch.reset().start();
        for (Hex hex : hexMap.hexes()) {
            assertTrue(datas.containsKey(hex));
        }
        long hexIterTime = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        System.out.println("## HexMap vs java.util.HashMap perfs :");
        System.out.println("   * Java (get ) : " + javaGetTime);
        System.out.println("   * Hex  (get ) : " + hexGetTime);
        System.out.println("   * Java (put ) : " + javaPutTime);
        System.out.println("   * Hex  (put ) : " + hexPutTime);
        System.out.println("   * Java (iter) : " + javaIterTime);
        System.out.println("   * Hex  (iter) : " + hexIterTime);
    }

    private void warmup() {
        // Generate a bunch of datas
        Random rand = new Random(4519480954298L);
        Map<Hex, Integer> datas = new HashMap<>();
        for (int i = 0; i < VALUE_COUNT; i++) {
            Hex hex = Hex.at(rand.nextInt(150), rand.nextInt(150));
            Integer value = rand.nextInt();
            datas.put(hex, value);
        }

        HashMap<Hex, Integer> javaMap = new HashMap<>(HexMapImpl.CAPACITY);
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            javaMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            assertEquals(entry.getValue(), javaMap.get(entry.getKey()));
        }

        HexMap<Integer> hexMap = new HexMapImpl<>();
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            hexMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Hex, Integer> entry : datas.entrySet()) {
            assertEquals(entry.getValue(), hexMap.get(entry.getKey()));
        }
    }
}
