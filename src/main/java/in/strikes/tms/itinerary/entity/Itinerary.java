package in.strikes.tms.itinerary.entity;

import in.strikes.tms.common.entity.BaseEntity;
import in.strikes.tms.trip.entity.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "itineraries", uniqueConstraints = {
        @UniqueConstraint(name = "uk_itineraries_trip_date", columnNames = {"trip_id", "itinerary_date"})
})
@Getter
@Setter
@NoArgsConstructor
public class Itinerary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "itinerary_date", nullable = false)
    private LocalDate itineraryDate;

    @Column(length = 1000)
    private String notes;
}
