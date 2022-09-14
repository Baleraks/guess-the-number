package by.bondarik.guessthenumber;

import java.util.Random;

public class NumberGenerator {
    public static int generate(int from, int to) {
        return new Random().nextInt(to - from + 1) + from;
    }
}
