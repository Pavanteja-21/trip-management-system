package in.strikes.tms.expense.controller;

import in.strikes.tms.expense.dto.CreateExpenseRequest;
import in.strikes.tms.expense.dto.MemberBalanceResponse;
import in.strikes.tms.expense.service.BalanceService;
import in.strikes.tms.expense.service.ExpenseService;
import in.strikes.tms.trip.repository.TripMemberRepository;
import in.strikes.tms.user.entity.User;
import in.strikes.tms.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final BalanceService balanceService;
    private final UserRepository userRepository;
    private final TripMemberRepository tripMemberRepository;

    @PostMapping("/expenses")
    public ResponseEntity<Void> createExpense(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        expenseService.createExpense(tripId, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/balances")
    public ResponseEntity<List<MemberBalanceResponse>> getBalances(
            @PathVariable Long tripId,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);

        if(!tripMemberRepository.existsByTripIdAndUserId(tripId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(balanceService.getBalances(tripId));
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
