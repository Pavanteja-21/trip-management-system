package in.strikes.tms.settlement.service;

import in.strikes.tms.settlement.dto.CreateSettlementRequest;
import in.strikes.tms.settlement.entity.Settlement;
import in.strikes.tms.settlement.repository.SettlementRepository;
import in.strikes.tms.trip.entity.Trip;
import in.strikes.tms.trip.repository.TripMemberRepository;
import in.strikes.tms.trip.repository.TripRepository;
import in.strikes.tms.user.entity.User;
import in.strikes.tms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public Settlement createSettlement(Long tripId, Long payerId, CreateSettlementRequest request) {

        if(payerId.equals(request.payeeId())) {
            throw new IllegalArgumentException("Payer & payee cannot be same user");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        if(!tripMemberRepository.existsByTripIdAndUserId(tripId, payerId)) {
            throw new IllegalArgumentException("Payer is not a member of this trip");
        }

        if(!tripMemberRepository.existsByTripIdAndUserId(tripId, request.payeeId())) {
            throw new IllegalArgumentException("Payee is not a member of this trip");
        }

        User payer = userRepository.findById(payerId)
                .orElseThrow(() -> new IllegalArgumentException("Payer is not found"));

        User payee = userRepository.findById(request.payeeId())
                .orElseThrow(() -> new IllegalArgumentException("Payee is not found"));

        Settlement settlement = new Settlement();
        settlement.setTrip(trip);
        settlement.setPayer(payer);
        settlement.setPayee(payee);
        settlement.setAmount(request.amount());
        settlement.setNote(request.note());

        return settlementRepository.save(settlement);
    }
}
