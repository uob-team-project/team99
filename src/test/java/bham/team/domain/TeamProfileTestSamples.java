package bham.team.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TeamProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TeamProfile getTeamProfileSample1() {
        return new TeamProfile().id(1L).teamID(1).appLink("appLink1").nickName("nickName1").slogan("slogan1").votes(1);
    }

    public static TeamProfile getTeamProfileSample2() {
        return new TeamProfile().id(2L).teamID(2).appLink("appLink2").nickName("nickName2").slogan("slogan2").votes(2);
    }

    public static TeamProfile getTeamProfileRandomSampleGenerator() {
        return new TeamProfile()
            .id(longCount.incrementAndGet())
            .teamID(intCount.incrementAndGet())
            .appLink(UUID.randomUUID().toString())
            .nickName(UUID.randomUUID().toString())
            .slogan(UUID.randomUUID().toString())
            .votes(intCount.incrementAndGet());
    }
}
