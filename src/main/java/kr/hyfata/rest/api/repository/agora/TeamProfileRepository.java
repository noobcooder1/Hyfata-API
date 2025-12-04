package kr.hyfata.rest.api.repository.agora;

import kr.hyfata.rest.api.entity.agora.TeamProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamProfileRepository extends JpaRepository<TeamProfile, Long> {

    Optional<TeamProfile> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    Optional<TeamProfile> findByTeamIdAndUserId(Long teamId, Long userId);

    List<TeamProfile> findByTeamId(Long teamId);

    List<TeamProfile> findByUserId(Long userId);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
}
