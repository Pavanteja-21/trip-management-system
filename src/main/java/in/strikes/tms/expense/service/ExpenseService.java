package in.strikes.tms.expense.service;

import in.strikes.tms.expense.dto.CreateExpenseRequest;
import in.strikes.tms.expense.dto.ExpenseSplitRequest;
import in.strikes.tms.expense.entity.Expense;
import in.strikes.tms.expense.entity.ExpenseSplit;
import in.strikes.tms.expense.repository.ExpenseRepository;
import in.strikes.tms.expense.repository.ExpenseSplitRepository;
import in.strikes.tms.trip.entity.Trip;
import in.strikes.tms.trip.repository.TripMemberRepository;
import in.strikes.tms.trip.repository.TripRepository;
import in.strikes.tms.user.entity.User;
import in.strikes.tms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public Expense createExpense(Long tripId, Long paidByUserId, CreateExpenseRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        if(!tripMemberRepository.existsByTripIdAndUserId(tripId, paidByUserId)) {
            throw new IllegalArgumentException("Payment user is not a trip member");
        }

        BigDecimal splitTotal = BigDecimal.ZERO;
        Set<Long> userIds = new HashSet<>();

        for(ExpenseSplitRequest split : request.splits()) {
            if(split.shareAmount().signum() < 0) {
                throw new IllegalArgumentException("Split amount cannot be negative");
            }

            if(!userIds.add(split.userId())) {
                throw new IllegalArgumentException("Duplicate user in expense splits");
            }

            if(!tripMemberRepository.existsByTripIdAndUserId(tripId, split.userId())) {
                throw new IllegalArgumentException("Split user is not a trip member");
            }

            splitTotal = splitTotal.add(split.shareAmount());
        }

        if(splitTotal.compareTo(request.amount()) != 0) {
            throw new IllegalArgumentException("Split total must equal expense amount");
        }

        User paidBy = userRepository.findById(paidByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Expense expense = new Expense();
        expense.setTrip(trip);
        expense.setPaidBy(paidBy);
        expense.setTitle(request.title());
        expense.setDescription(request.description());
        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());

        Expense savedExpense = expenseRepository.save(expense);

        for(ExpenseSplitRequest split : request.splits()) {
            User splitUser = userRepository.findById(split.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            ExpenseSplit expenseSplit = new ExpenseSplit();
            expenseSplit.setExpense(savedExpense);
            expenseSplit.setUser(splitUser);
            expenseSplit.setShareAmount(split.shareAmount());
            splitRepository.save(expenseSplit);
        }

        return savedExpense;
    }
}
