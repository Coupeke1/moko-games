package be.kdg.team22.userservice.profile.domain.profile;


import org.jmolecules.ddd.annotation.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository {

    Optional<Profile> findById(ProfileId id);

    void save(Profile profile);

    List<Profile> findAll();
}
