package in.strikes.tms.expense.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ExpenseSplitRequest(
        @NotNull
        Long userId,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal shareAmount
) {}
