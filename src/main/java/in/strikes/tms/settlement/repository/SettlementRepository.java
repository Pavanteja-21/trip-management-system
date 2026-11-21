package in.strikes.tms.settlement.repository;

import in.strikes.tms.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findByTripIdOrderBySettleAtDesc(Long tripId);

    List<Settlement> findByTripIdAndPayerId(Long tripId, Long payerId);

    List<Settlement> findByTripIdAndPayeeId(Long tripId, Long payeeId);
}
