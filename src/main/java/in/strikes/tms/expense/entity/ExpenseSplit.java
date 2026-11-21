package in.strikes.tms.expense.entity;

import in.strikes.tms.common.entity.BaseEntity;
import in.strikes.tms.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits", uniqueConstraints = {
        @UniqueConstraint(name = "uk_expense_splits_expense_user",
                columnNames = {"expense_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class ExpenseSplit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "share_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal shareAmount;
}
