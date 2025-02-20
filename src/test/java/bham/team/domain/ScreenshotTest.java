package bham.team.domain;

import static bham.team.domain.ScreenshotTestSamples.*;
import static bham.team.domain.TeamProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ScreenshotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Screenshot.class);
        Screenshot screenshot1 = getScreenshotSample1();
        Screenshot screenshot2 = new Screenshot();
        assertThat(screenshot1).isNotEqualTo(screenshot2);

        screenshot2.setId(screenshot1.getId());
        assertThat(screenshot1).isEqualTo(screenshot2);

        screenshot2 = getScreenshotSample2();
        assertThat(screenshot1).isNotEqualTo(screenshot2);
    }

    @Test
    void teamProfileTest() {
        Screenshot screenshot = getScreenshotRandomSampleGenerator();
        TeamProfile teamProfileBack = getTeamProfileRandomSampleGenerator();

        screenshot.addTeamProfile(teamProfileBack);
        assertThat(screenshot.getTeamProfiles()).containsOnly(teamProfileBack);
        assertThat(teamProfileBack.getImageGalleries()).containsOnly(screenshot);

        screenshot.removeTeamProfile(teamProfileBack);
        assertThat(screenshot.getTeamProfiles()).doesNotContain(teamProfileBack);
        assertThat(teamProfileBack.getImageGalleries()).doesNotContain(screenshot);

        screenshot.teamProfiles(new HashSet<>(Set.of(teamProfileBack)));
        assertThat(screenshot.getTeamProfiles()).containsOnly(teamProfileBack);
        assertThat(teamProfileBack.getImageGalleries()).containsOnly(screenshot);

        screenshot.setTeamProfiles(new HashSet<>());
        assertThat(screenshot.getTeamProfiles()).doesNotContain(teamProfileBack);
        assertThat(teamProfileBack.getImageGalleries()).doesNotContain(screenshot);
    }
}
