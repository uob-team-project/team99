package bham.team.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TeamProfile.
 */
@Entity
@Table(name = "team_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "team_id", nullable = false)
    private Integer teamID;

    @Column(name = "app_link")
    private String appLink;

    @Lob
    @Column(name = "app_description")
    private String appDescription;

    @Lob
    @Column(name = "logo_pic")
    private byte[] logoPic;

    @Column(name = "logo_pic_content_type")
    private String logoPicContentType;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "slogan")
    private String slogan;

    @Column(name = "votes")
    private Integer votes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_team_profile__image_gallery",
        joinColumns = @JoinColumn(name = "team_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "image_gallery_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "teamProfiles" }, allowSetters = true)
    private Set<Screenshot> imageGalleries = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teams")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "teams" }, allowSetters = true)
    private Set<UserProfile> members = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTeamID() {
        return this.teamID;
    }

    public TeamProfile teamID(Integer teamID) {
        this.setTeamID(teamID);
        return this;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public String getAppLink() {
        return this.appLink;
    }

    public TeamProfile appLink(String appLink) {
        this.setAppLink(appLink);
        return this;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public String getAppDescription() {
        return this.appDescription;
    }

    public TeamProfile appDescription(String appDescription) {
        this.setAppDescription(appDescription);
        return this;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public byte[] getLogoPic() {
        return this.logoPic;
    }

    public TeamProfile logoPic(byte[] logoPic) {
        this.setLogoPic(logoPic);
        return this;
    }

    public void setLogoPic(byte[] logoPic) {
        this.logoPic = logoPic;
    }

    public String getLogoPicContentType() {
        return this.logoPicContentType;
    }

    public TeamProfile logoPicContentType(String logoPicContentType) {
        this.logoPicContentType = logoPicContentType;
        return this;
    }

    public void setLogoPicContentType(String logoPicContentType) {
        this.logoPicContentType = logoPicContentType;
    }

    public String getNickName() {
        return this.nickName;
    }

    public TeamProfile nickName(String nickName) {
        this.setNickName(nickName);
        return this;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSlogan() {
        return this.slogan;
    }

    public TeamProfile slogan(String slogan) {
        this.setSlogan(slogan);
        return this;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public Integer getVotes() {
        return this.votes;
    }

    public TeamProfile votes(Integer votes) {
        this.setVotes(votes);
        return this;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Set<Screenshot> getImageGalleries() {
        return this.imageGalleries;
    }

    public void setImageGalleries(Set<Screenshot> screenshots) {
        this.imageGalleries = screenshots;
    }

    public TeamProfile imageGalleries(Set<Screenshot> screenshots) {
        this.setImageGalleries(screenshots);
        return this;
    }

    public TeamProfile addImageGallery(Screenshot screenshot) {
        this.imageGalleries.add(screenshot);
        return this;
    }

    public TeamProfile removeImageGallery(Screenshot screenshot) {
        this.imageGalleries.remove(screenshot);
        return this;
    }

    public Set<UserProfile> getMembers() {
        return this.members;
    }

    public void setMembers(Set<UserProfile> userProfiles) {
        if (this.members != null) {
            this.members.forEach(i -> i.removeTeam(this));
        }
        if (userProfiles != null) {
            userProfiles.forEach(i -> i.addTeam(this));
        }
        this.members = userProfiles;
    }

    public TeamProfile members(Set<UserProfile> userProfiles) {
        this.setMembers(userProfiles);
        return this;
    }

    public TeamProfile addMember(UserProfile userProfile) {
        this.members.add(userProfile);
        userProfile.getTeams().add(this);
        return this;
    }

    public TeamProfile removeMember(UserProfile userProfile) {
        this.members.remove(userProfile);
        userProfile.getTeams().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((TeamProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamProfile{" +
            "id=" + getId() +
            ", teamID=" + getTeamID() +
            ", appLink='" + getAppLink() + "'" +
            ", appDescription='" + getAppDescription() + "'" +
            ", logoPic='" + getLogoPic() + "'" +
            ", logoPicContentType='" + getLogoPicContentType() + "'" +
            ", nickName='" + getNickName() + "'" +
            ", slogan='" + getSlogan() + "'" +
            ", votes=" + getVotes() +
            "}";
    }
}
