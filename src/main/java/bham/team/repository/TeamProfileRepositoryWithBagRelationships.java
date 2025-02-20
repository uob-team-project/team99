package bham.team.repository;

import bham.team.domain.TeamProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface TeamProfileRepositoryWithBagRelationships {
    Optional<TeamProfile> fetchBagRelationships(Optional<TeamProfile> teamProfile);

    List<TeamProfile> fetchBagRelationships(List<TeamProfile> teamProfiles);

    Page<TeamProfile> fetchBagRelationships(Page<TeamProfile> teamProfiles);
}
