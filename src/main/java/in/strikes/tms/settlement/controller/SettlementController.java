package in.strikes.tms.settlement.controller;

import in.strikes.tms.settlement.dto.CreateSettlementRequest;
import in.strikes.tms.settlement.service.SettlementService;
import in.strikes.tms.user.entity.User;
import in.strikes.tms.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{tripId}/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final UserRepository userRepository;
    private final SettlementService settlementService;

    @PostMapping
    public ResponseEntity<Void> createSettlement(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateSettlementRequest request,
            Authentication authentication
    ) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        settlementService.createSettlement(tripId, currentUser.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
