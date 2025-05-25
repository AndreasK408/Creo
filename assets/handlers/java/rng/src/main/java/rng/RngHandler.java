package rng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RngHandler {
    private static final Random random = new Random();
    public static List<Integer> generateRandomNumbers(int n, int min, int max) {
        if (n < 1) {
            throw new IllegalArgumentException("`n` must be greater or equal to 1, but was " + n);
        }
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            numbers.add(random.nextInt((max - min) + 1) + min);
        }
        return numbers;
    }
}
