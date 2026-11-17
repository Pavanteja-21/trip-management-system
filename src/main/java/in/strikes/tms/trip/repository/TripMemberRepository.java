package in.strikes.tms.trip.repository;

import in.strikes.tms.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

    List<TripMember> findByTripId(Long id);

    List<TripMember> findByUserId(Long userId);

    Optional<TripMember> findByTripIdAndUserId(Long tripId, Long userId);

    Boolean existsByTripIdAndUserId(Long tripId, Long userId);
}
