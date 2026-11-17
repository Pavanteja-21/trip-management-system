package in.strikes.tms.invitation.repository;

import in.strikes.tms.invitation.entity.InvitationStatus;
import in.strikes.tms.invitation.entity.TripInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {

    List<TripInvitation> findByEmailIgnoreCase(String inviteeEmail);

    List<TripInvitation> findByTripId(Long tripId);

    Optional<TripInvitation> findByIdAndInviteeEmailIgnoreCase(Long id, String inviteeEmail);

    Boolean existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(Long tripId, String inviteeEmail, InvitationStatus status);
}
