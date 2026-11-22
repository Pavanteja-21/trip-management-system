package in.strikes.tms.expense.service;

import in.strikes.tms.expense.dto.MemberBalanceResponse;
import in.strikes.tms.expense.entity.Expense;
import in.strikes.tms.expense.entity.ExpenseSplit;
import in.strikes.tms.expense.repository.ExpenseRepository;
import in.strikes.tms.expense.repository.ExpenseSplitRepository;
import in.strikes.tms.settlement.entity.Settlement;
import in.strikes.tms.settlement.repository.SettlementRepository;
import in.strikes.tms.trip.entity.TripMember;
import in.strikes.tms.trip.repository.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final TripMemberRepository tripMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final SettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    public List<MemberBalanceResponse> getBalance(Long tripId) {
        List<TripMember> members = tripMemberRepository.findByTripId(tripId);
        List<Expense> expenses = expenseRepository.findByTripIdOrderByExpenseDateDesc(tripId);
        List<Settlement> settlements = settlementRepository.findByTripIdOrderBySettleAtDesc(tripId);

        List<MemberBalanceResponse> results = new ArrayList<>();

        for(TripMember member : members) {
            Long userId = member.getUser().getId();
            BigDecimal totalPaid = expenses.stream()
                    .filter(e -> e.getPaidBy().getId().equals(userId))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ExpenseSplit> splits = splitRepository.findByExpenseTripIdAndUserId(tripId, userId);

            BigDecimal totalShare = splits.stream()
                    .map(ExpenseSplit::getShareAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal settlementsSent = settlements.stream()
                    .filter(s -> s.getPayer().getId().equals(userId))
                    .map(Settlement::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal settlementsReceived = settlements.stream()
                    .filter(s -> s.getPayee().getId().equals(userId))
                    .map(Settlement::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Net Balance = Paid Amount - Share Amount + Settlements Sent - Settlements Received
            BigDecimal netBalance = totalPaid
                    .subtract(totalShare)
                    .add(settlementsSent)
                    .subtract(settlementsReceived);

            results.add(new MemberBalanceResponse(
                    userId,
                    member.getUser().getFullName(),
                    totalPaid,
                    totalShare,
                    settlementsSent,
                    settlementsReceived,
                    netBalance
            ));
        }

        return  results;
    }
}
