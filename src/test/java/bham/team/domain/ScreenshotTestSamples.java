package bham.team.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ScreenshotTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Screenshot getScreenshotSample1() {
        return new Screenshot().id(1L).caption("caption1");
    }

    public static Screenshot getScreenshotSample2() {
        return new Screenshot().id(2L).caption("caption2");
    }

    public static Screenshot getScreenshotRandomSampleGenerator() {
        return new Screenshot().id(longCount.incrementAndGet()).caption(UUID.randomUUID().toString());
    }
}
