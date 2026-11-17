package in.strikes.tms.itinerary.repository;

import in.strikes.tms.itinerary.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

    List<Itinerary> findByTripOrderByItineraryDateAsc(Long tripId);

    Optional<Itinerary> findByTripIdAndItineraryDate(Long tripId, LocalDate itineraryDate);

    Boolean existsByTripIdAndItineraryDate(Long tripId, LocalDate itineraryDate);
}
