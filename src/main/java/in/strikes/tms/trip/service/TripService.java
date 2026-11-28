package in.strikes.tms.trip.service;

import in.strikes.tms.common.exception.ResourceNotFoundException;
import in.strikes.tms.trip.dto.*;
import in.strikes.tms.trip.entity.Trip;
import in.strikes.tms.trip.entity.TripMember;
import in.strikes.tms.trip.entity.TripMemberRole;
import in.strikes.tms.trip.repository.TripMemberRepository;
import in.strikes.tms.trip.repository.TripRepository;
import in.strikes.tms.user.entity.User;
import in.strikes.tms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripService {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;


    public TripResponse createTrip(String authenticatedEmail, CreateTripRequest request) {
        validateDates(request.startDate(), request.endDate());

        User owner = getUserByEmail(authenticatedEmail);

        Trip trip = new Trip();
        trip.setTitle(request.title().trim());
        trip.setDescription(request.description());
        trip.setDestination(request.destination());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setOwner(owner);

        Trip savedTrip = tripRepository.save(trip);

        TripMember ownerMembership = new TripMember();
        ownerMembership.setTrip(savedTrip);
        ownerMembership.setUser(owner);
        ownerMembership.setRole(TripMemberRole.OWNER);
        ownerMembership.setJoinedAt(LocalDateTime.now());

        tripMemberRepository.save(ownerMembership);

        return toTripResponse(savedTrip);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getMyTrips(String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        List<TripMember> memberships = tripMemberRepository.findByUserId(user.getId());

        return memberships.stream()
                .map(TripMember::getTrip)
                .distinct()
                .map(this::toTripResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        Trip trip = getTrip(tripId);

        requiredMembership(trip.getId(), user.getId());

        return toTripResponse(trip);
    }

    public TripResponse updateTrip(Long tripId, String authenticatedEmail, UpdateTripRequest request) {
        validateDates(request.startDate(), request.endDate());

        User user = getUserByEmail(authenticatedEmail);

        Trip trip = getTrip(tripId);

        requireOwnership(trip, user);

        trip.setTitle(request.title().trim());
        trip.setDestination(request.destination());
        trip.setDescription(request.description());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());

        return toTripResponse(tripRepository.save(trip));
    }

    public void deleteTrip(Long tripId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        Trip trip = getTrip(tripId);

        requireOwnership(trip, user);

        tripMemberRepository.deleteAll(tripMemberRepository.findByTripId(tripId));

        tripRepository.delete(trip);
    }

    @Transactional(readOnly = true)
    public List<TripMemberResponse> getMembers(Long tripId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        getTrip(tripId);

        requiredMembership(tripId, user.getId());

        return tripMemberRepository.findByTripId(tripId)
                .stream()
                .map(this::toMemberResponse)
                .toList();
    }

    public TripMemberResponse updateMemberRole(Long tripId, Long targetUserId, String authenticatedEmail, UpdateMemberRoleRequest request) {
        User actor = getUserByEmail(authenticatedEmail);

        Trip trip =  getTrip(tripId);

        requireOwnership(trip, actor);

        if(request.role() == TripMemberRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner role cannot be assigned through this endpoint");
        }

        TripMember target = tripMemberRepository.findByTripIdAndUserId(tripId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip member not found"));

        if(target.getRole() == TripMemberRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The owner role cannot be change here");
        }

        target.setRole(request.role());

        TripMember saved = tripMemberRepository.save(target);

        return toMemberResponse(saved);
    }

    public void removeMember(Long tripId, Long targetUserId, String authenticatedEmail) {
        User actor = getUserByEmail(authenticatedEmail);

        Trip trip = getTrip(tripId);

        requireOwnership(trip, actor);

        TripMember target = tripMemberRepository.findByTripIdAndUserId(tripId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip member not found"));

        if(target.getRole() == TripMemberRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The owner role cannot be removed");
        }

        tripMemberRepository.delete(target);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private Trip getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + tripId));
    }

    private void requiredMembership(Long tripId, Long userId) {
        if(!tripMemberRepository.existsByTripIdAndUserId(tripId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this trip");
        }
    }

    private void requireOwnership(Trip trip, User user) {
        boolean isOwner = trip.getOwner().getId().equals(user.getId());

        if(!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the trip owner can perform this action");
        }
    }

    private TripResponse toTripResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getDescription(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getOwner().getId(),
                trip.getOwner().getFullName()
        );
    }


    private TripMemberResponse toMemberResponse(TripMember member) {
        return new TripMemberResponse(
                member.getUser().getId(),
                member.getUser().getFullName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getJoinedAt()
        );
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if(endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }
    }
}
