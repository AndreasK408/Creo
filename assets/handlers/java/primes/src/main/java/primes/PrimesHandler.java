package primes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrimesHandler {

    public static List<Integer> generateFirstPrimes(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "`n` must be greater or equal to `1`, but was " + n + "."
            );
        }
        Map<Integer, List<Integer>> D = new HashMap<>();
        List<Integer> primes = new ArrayList<>(n);
        int q = 2;
        while (primes.size() < n) {
            if (!D.containsKey(q)) {
                primes.add(q);
                D.put(q * q, new ArrayList<>(List.of(q)));
            } else {
                for (int p : D.get(q)) {
                    D.computeIfAbsent(p + q, k -> new ArrayList<>()).add(p);
                }
                D.remove(q);
            }
            q++;
        }
        return primes;
    }
}
