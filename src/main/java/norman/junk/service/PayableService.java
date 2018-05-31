package norman.junk.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import norman.junk.domain.Payable;
import norman.junk.domain.Payment;
import norman.junk.repository.PayableRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayableService {
    private static final int WARNING_DAYS_AGO = 14;
    private Date today;
    private Date warning;
    @Autowired
    private PayableRepository payableRepository;

    public PayableService() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        today = cal.getTime();
        cal.add(Calendar.DATE, WARNING_DAYS_AGO);
        warning = cal.getTime();
    }

    public List<PayableBalanceBean> findPayableBalancesDue() {
        List<PayableBalanceBean> payableBalances = new ArrayList<>();
        Iterable<Payable> payables = payableRepository.findAllByOrderByPaymentDueDate();
        for (Payable payable : payables) {
            PayableBalanceBean payableBalance = toPayableBalanceBean(payable);
            if (payableBalance.getStyleClass() != null)
                payableBalances.add(payableBalance);
        }
        return payableBalances;
    }

    public List<PayableBalanceBean> findAllPayableBalances() {
        List<PayableBalanceBean> payableBalances = new ArrayList<>();
        Iterable<Payable> payables = payableRepository.findAll();
        for (Payable payable : payables) {
            PayableBalanceBean payableBalance = toPayableBalanceBean(payable);
            payableBalances.add(payableBalance);
        }
        return payableBalances;
    }

    private PayableBalanceBean toPayableBalanceBean(Payable payable) {
        String payeeDisplayName = payable.getPayee().getNickname();
        if (StringUtils.isBlank(payable.getPayee().getNickname())) {
            payeeDisplayName = payable.getPayee().getName();
        }
        Date paymentDueDate = payable.getPaymentDueDate();
        BigDecimal newBalanceTotal = payable.getNewBalanceTotal();
        BigDecimal balance = newBalanceTotal;
        Date lastPaymentDate = null;
        for (Payment payment : payable.getPayments()) {
            balance = balance.subtract(payment.getAmountPaid());
            Date paymentPaidDate = payment.getPaidDate();
            if (lastPaymentDate == null || lastPaymentDate.before(paymentPaidDate))
                lastPaymentDate = paymentPaidDate;
        }
        String styleClass = null;
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            if (paymentDueDate.before(today)) {
                styleClass = "bg-danger text-white";
            } else if (paymentDueDate.before(warning)) {
                styleClass = "bg-warning text-dark";
            } else {
                styleClass = "bg-info text-white";
            }
        } else {
            if (paymentDueDate.equals(today) || paymentDueDate.after(today) || lastPaymentDate.equals(today) ||
                    lastPaymentDate.after(today)) {
                styleClass = "bg-success text-white";
            }
        }
        return new PayableBalanceBean(payable.getId(), payeeDisplayName, paymentDueDate, newBalanceTotal,
                payable.getMinimumPaymentDue(), balance, lastPaymentDate, styleClass);
    }

    public Optional<Payable> findPayableById(Long payableId) {
        return payableRepository.findById(payableId);
    }

    public Payable savePayable(Payable payable) {
        return payableRepository.save(payable);
    }
}