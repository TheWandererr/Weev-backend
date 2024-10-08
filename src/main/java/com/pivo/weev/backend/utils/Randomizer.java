package com.pivo.weev.backend.utils;

import java.util.Random;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Randomizer {

    private static final Random random = new Random();

    public static int randomInt(int upperBound) {
        return random.nextInt(upperBound);
    }

    public static String sixDigitInt() {
        return String.format("%06d", randomInt(999999));
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
