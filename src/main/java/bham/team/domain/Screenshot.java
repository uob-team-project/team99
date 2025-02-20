package bham.team.domain;

import bham.team.domain.enumeration.Milestone;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Screenshot.
 */
@Entity
@Table(name = "screenshot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Screenshot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "pic")
    private byte[] pic;

    @Column(name = "pic_content_type")
    private String picContentType;

    @Column(name = "caption")
    private String caption;

    @Enumerated(EnumType.STRING)
    @Column(name = "milestone")
    private Milestone milestone;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "imageGalleries")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "imageGalleries", "members" }, allowSetters = true)
    private Set<TeamProfile> teamProfiles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Screenshot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPic() {
        return this.pic;
    }

    public Screenshot pic(byte[] pic) {
        this.setPic(pic);
        return this;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public String getPicContentType() {
        return this.picContentType;
    }

    public Screenshot picContentType(String picContentType) {
        this.picContentType = picContentType;
        return this;
    }

    public void setPicContentType(String picContentType) {
        this.picContentType = picContentType;
    }

    public String getCaption() {
        return this.caption;
    }

    public Screenshot caption(String caption) {
        this.setCaption(caption);
        return this;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Milestone getMilestone() {
        return this.milestone;
    }

    public Screenshot milestone(Milestone milestone) {
        this.setMilestone(milestone);
        return this;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    public Set<TeamProfile> getTeamProfiles() {
        return this.teamProfiles;
    }

    public void setTeamProfiles(Set<TeamProfile> teamProfiles) {
        if (this.teamProfiles != null) {
            this.teamProfiles.forEach(i -> i.removeImageGallery(this));
        }
        if (teamProfiles != null) {
            teamProfiles.forEach(i -> i.addImageGallery(this));
        }
        this.teamProfiles = teamProfiles;
    }

    public Screenshot teamProfiles(Set<TeamProfile> teamProfiles) {
        this.setTeamProfiles(teamProfiles);
        return this;
    }

    public Screenshot addTeamProfile(TeamProfile teamProfile) {
        this.teamProfiles.add(teamProfile);
        teamProfile.getImageGalleries().add(this);
        return this;
    }

    public Screenshot removeTeamProfile(TeamProfile teamProfile) {
        this.teamProfiles.remove(teamProfile);
        teamProfile.getImageGalleries().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Screenshot)) {
            return false;
        }
        return getId() != null && getId().equals(((Screenshot) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Screenshot{" +
            "id=" + getId() +
            ", pic='" + getPic() + "'" +
            ", picContentType='" + getPicContentType() + "'" +
            ", caption='" + getCaption() + "'" +
            ", milestone='" + getMilestone() + "'" +
            "}";
    }
}
