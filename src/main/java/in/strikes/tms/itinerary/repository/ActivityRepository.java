package in.strikes.tms.itinerary.repository;

import in.strikes.tms.itinerary.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByItineraryIsOrderByDisplayOrderAsc(Long itineraryId);

    List<Activity> findByItineraryTripOrderByDisplayOrderAsc(Long tripId);
}
