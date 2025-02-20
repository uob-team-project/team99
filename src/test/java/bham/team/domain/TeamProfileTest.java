package bham.team.domain;

import static bham.team.domain.ScreenshotTestSamples.*;
import static bham.team.domain.TeamProfileTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeamProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamProfile.class);
        TeamProfile teamProfile1 = getTeamProfileSample1();
        TeamProfile teamProfile2 = new TeamProfile();
        assertThat(teamProfile1).isNotEqualTo(teamProfile2);

        teamProfile2.setId(teamProfile1.getId());
        assertThat(teamProfile1).isEqualTo(teamProfile2);

        teamProfile2 = getTeamProfileSample2();
        assertThat(teamProfile1).isNotEqualTo(teamProfile2);
    }

    @Test
    void imageGalleryTest() {
        TeamProfile teamProfile = getTeamProfileRandomSampleGenerator();
        Screenshot screenshotBack = getScreenshotRandomSampleGenerator();

        teamProfile.addImageGallery(screenshotBack);
        assertThat(teamProfile.getImageGalleries()).containsOnly(screenshotBack);

        teamProfile.removeImageGallery(screenshotBack);
        assertThat(teamProfile.getImageGalleries()).doesNotContain(screenshotBack);

        teamProfile.imageGalleries(new HashSet<>(Set.of(screenshotBack)));
        assertThat(teamProfile.getImageGalleries()).containsOnly(screenshotBack);

        teamProfile.setImageGalleries(new HashSet<>());
        assertThat(teamProfile.getImageGalleries()).doesNotContain(screenshotBack);
    }

    @Test
    void memberTest() {
        TeamProfile teamProfile = getTeamProfileRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        teamProfile.addMember(userProfileBack);
        assertThat(teamProfile.getMembers()).containsOnly(userProfileBack);
        assertThat(userProfileBack.getTeams()).containsOnly(teamProfile);

        teamProfile.removeMember(userProfileBack);
        assertThat(teamProfile.getMembers()).doesNotContain(userProfileBack);
        assertThat(userProfileBack.getTeams()).doesNotContain(teamProfile);

        teamProfile.members(new HashSet<>(Set.of(userProfileBack)));
        assertThat(teamProfile.getMembers()).containsOnly(userProfileBack);
        assertThat(userProfileBack.getTeams()).containsOnly(teamProfile);

        teamProfile.setMembers(new HashSet<>());
        assertThat(teamProfile.getMembers()).doesNotContain(userProfileBack);
        assertThat(userProfileBack.getTeams()).doesNotContain(teamProfile);
    }
}
