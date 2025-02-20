package bham.team.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ScreenshotAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertScreenshotAllPropertiesEquals(Screenshot expected, Screenshot actual) {
        assertScreenshotAutoGeneratedPropertiesEquals(expected, actual);
        assertScreenshotAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertScreenshotAllUpdatablePropertiesEquals(Screenshot expected, Screenshot actual) {
        assertScreenshotUpdatableFieldsEquals(expected, actual);
        assertScreenshotUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertScreenshotAutoGeneratedPropertiesEquals(Screenshot expected, Screenshot actual) {
        assertThat(expected)
            .as("Verify Screenshot auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertScreenshotUpdatableFieldsEquals(Screenshot expected, Screenshot actual) {
        assertThat(expected)
            .as("Verify Screenshot relevant properties")
            .satisfies(e -> assertThat(e.getPic()).as("check pic").isEqualTo(actual.getPic()))
            .satisfies(e -> assertThat(e.getPicContentType()).as("check pic contenty type").isEqualTo(actual.getPicContentType()))
            .satisfies(e -> assertThat(e.getCaption()).as("check caption").isEqualTo(actual.getCaption()))
            .satisfies(e -> assertThat(e.getMilestone()).as("check milestone").isEqualTo(actual.getMilestone()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertScreenshotUpdatableRelationshipsEquals(Screenshot expected, Screenshot actual) {
        assertThat(expected)
            .as("Verify Screenshot relationships")
            .satisfies(e -> assertThat(e.getTeamProfiles()).as("check teamProfiles").isEqualTo(actual.getTeamProfiles()));
    }
}
