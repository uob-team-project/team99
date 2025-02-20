package bham.team.web.rest;

import bham.team.domain.TeamProfile;
import bham.team.repository.TeamProfileRepository;
import bham.team.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link bham.team.domain.TeamProfile}.
 */
@RestController
@RequestMapping("/api/team-profiles")
@Transactional
public class TeamProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeamProfileResource.class);

    private static final String ENTITY_NAME = "teamProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamProfileRepository teamProfileRepository;

    public TeamProfileResource(TeamProfileRepository teamProfileRepository) {
        this.teamProfileRepository = teamProfileRepository;
    }

    /**
     * {@code POST  /team-profiles} : Create a new teamProfile.
     *
     * @param teamProfile the teamProfile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamProfile, or with status {@code 400 (Bad Request)} if the teamProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TeamProfile> createTeamProfile(@Valid @RequestBody TeamProfile teamProfile) throws URISyntaxException {
        LOG.debug("REST request to save TeamProfile : {}", teamProfile);
        if (teamProfile.getId() != null) {
            throw new BadRequestAlertException("A new teamProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        teamProfile = teamProfileRepository.save(teamProfile);
        return ResponseEntity.created(new URI("/api/team-profiles/" + teamProfile.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, teamProfile.getId().toString()))
            .body(teamProfile);
    }

    /**
     * {@code PUT  /team-profiles/:id} : Updates an existing teamProfile.
     *
     * @param id the id of the teamProfile to save.
     * @param teamProfile the teamProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamProfile,
     * or with status {@code 400 (Bad Request)} if the teamProfile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeamProfile> updateTeamProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamProfile teamProfile
    ) throws URISyntaxException {
        LOG.debug("REST request to update TeamProfile : {}, {}", id, teamProfile);
        if (teamProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        teamProfile = teamProfileRepository.save(teamProfile);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, teamProfile.getId().toString()))
            .body(teamProfile);
    }

    /**
     * {@code PATCH  /team-profiles/:id} : Partial updates given fields of an existing teamProfile, field will ignore if it is null
     *
     * @param id the id of the teamProfile to save.
     * @param teamProfile the teamProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamProfile,
     * or with status {@code 400 (Bad Request)} if the teamProfile is not valid,
     * or with status {@code 404 (Not Found)} if the teamProfile is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeamProfile> partialUpdateTeamProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeamProfile teamProfile
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TeamProfile partially : {}, {}", id, teamProfile);
        if (teamProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeamProfile> result = teamProfileRepository
            .findById(teamProfile.getId())
            .map(existingTeamProfile -> {
                if (teamProfile.getTeamID() != null) {
                    existingTeamProfile.setTeamID(teamProfile.getTeamID());
                }
                if (teamProfile.getAppLink() != null) {
                    existingTeamProfile.setAppLink(teamProfile.getAppLink());
                }
                if (teamProfile.getAppDescription() != null) {
                    existingTeamProfile.setAppDescription(teamProfile.getAppDescription());
                }
                if (teamProfile.getLogoPic() != null) {
                    existingTeamProfile.setLogoPic(teamProfile.getLogoPic());
                }
                if (teamProfile.getLogoPicContentType() != null) {
                    existingTeamProfile.setLogoPicContentType(teamProfile.getLogoPicContentType());
                }
                if (teamProfile.getNickName() != null) {
                    existingTeamProfile.setNickName(teamProfile.getNickName());
                }
                if (teamProfile.getSlogan() != null) {
                    existingTeamProfile.setSlogan(teamProfile.getSlogan());
                }
                if (teamProfile.getVotes() != null) {
                    existingTeamProfile.setVotes(teamProfile.getVotes());
                }

                return existingTeamProfile;
            })
            .map(teamProfileRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, teamProfile.getId().toString())
        );
    }

    /**
     * {@code GET  /team-profiles} : get all the teamProfiles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamProfiles in body.
     */
    @GetMapping("")
    public List<TeamProfile> getAllTeamProfiles(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all TeamProfiles");
        if (eagerload) {
            return teamProfileRepository.findAllWithEagerRelationships();
        } else {
            return teamProfileRepository.findAll();
        }
    }

    /**
     * {@code GET  /team-profiles/:id} : get the "id" teamProfile.
     *
     * @param id the id of the teamProfile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamProfile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamProfile> getTeamProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TeamProfile : {}", id);
        Optional<TeamProfile> teamProfile = teamProfileRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(teamProfile);
    }

    /**
     * {@code DELETE  /team-profiles/:id} : delete the "id" teamProfile.
     *
     * @param id the id of the teamProfile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TeamProfile : {}", id);
        teamProfileRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
