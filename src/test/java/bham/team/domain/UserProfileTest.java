package bham.team.domain;

import static bham.team.domain.TeamProfileTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();
        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void teamTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        TeamProfile teamProfileBack = getTeamProfileRandomSampleGenerator();

        userProfile.addTeam(teamProfileBack);
        assertThat(userProfile.getTeams()).containsOnly(teamProfileBack);

        userProfile.removeTeam(teamProfileBack);
        assertThat(userProfile.getTeams()).doesNotContain(teamProfileBack);

        userProfile.teams(new HashSet<>(Set.of(teamProfileBack)));
        assertThat(userProfile.getTeams()).containsOnly(teamProfileBack);

        userProfile.setTeams(new HashSet<>());
        assertThat(userProfile.getTeams()).doesNotContain(teamProfileBack);
    }
}
