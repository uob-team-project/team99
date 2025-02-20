package bham.team.domain.enumeration;

/**
 * The Milestone enumeration.
 */
public enum Milestone {
    M1("milestoneOne"),
    M2("milestoneTwo"),
    M3("milestoneThree");

    private final String value;

    Milestone(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
