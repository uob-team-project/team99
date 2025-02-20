package bham.team.repository;

import bham.team.domain.TeamProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TeamProfileRepositoryWithBagRelationshipsImpl implements TeamProfileRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String TEAMPROFILES_PARAMETER = "teamProfiles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TeamProfile> fetchBagRelationships(Optional<TeamProfile> teamProfile) {
        return teamProfile.map(this::fetchImageGalleries);
    }

    @Override
    public Page<TeamProfile> fetchBagRelationships(Page<TeamProfile> teamProfiles) {
        return new PageImpl<>(
            fetchBagRelationships(teamProfiles.getContent()),
            teamProfiles.getPageable(),
            teamProfiles.getTotalElements()
        );
    }

    @Override
    public List<TeamProfile> fetchBagRelationships(List<TeamProfile> teamProfiles) {
        return Optional.of(teamProfiles).map(this::fetchImageGalleries).orElse(Collections.emptyList());
    }

    TeamProfile fetchImageGalleries(TeamProfile result) {
        return entityManager
            .createQuery(
                "select teamProfile from TeamProfile teamProfile left join fetch teamProfile.imageGalleries where teamProfile.id = :id",
                TeamProfile.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<TeamProfile> fetchImageGalleries(List<TeamProfile> teamProfiles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, teamProfiles.size()).forEach(index -> order.put(teamProfiles.get(index).getId(), index));
        List<TeamProfile> result = entityManager
            .createQuery(
                "select teamProfile from TeamProfile teamProfile left join fetch teamProfile.imageGalleries where teamProfile in :teamProfiles",
                TeamProfile.class
            )
            .setParameter(TEAMPROFILES_PARAMETER, teamProfiles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
