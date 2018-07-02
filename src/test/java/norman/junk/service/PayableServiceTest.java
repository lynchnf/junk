package norman.junk.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import norman.junk.FakeDataUtil;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.domain.Payment;
import norman.junk.repository.PayableRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class PayableServiceTest {
    private Random random = new Random();

    @TestConfiguration
    static class PayableServiceTestConfiguration {
        @Bean
        public PayableService payableService() {
            return new PayableService();
        }
    }

    @Autowired
    private PayableService payableService;
    @MockBean
    private PayableRepository payableRepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findAllPayables() {
    }

    @Test
    public void findPayableById() {
    }

    @Test
    public void savePayable() {
    }

    @Test
    public void findAllPayableDuesFull() {
        Payee payee = FakeDataUtil.buildPayee(1);
        Payable payable = FakeDataUtil.buildPayable(payee, 1, null);
        Payment payment = FakeDataUtil.buildFullPayment(payable, 1);
        List<Payable> payables = new ArrayList<>();
        payables.add(payable);
        Mockito.when(payableRepository.findAllByOrderByDueDate()).thenReturn(payables);
        List<PayableDueBean> payableDues = payableService.findAllPayableDues();
        assertEquals(0, payableDues.size());
    }

    @Test
    public void findAllPayableDuesPartial() {
        Payee payee = FakeDataUtil.buildPayee(1);
        Payable payable = FakeDataUtil.buildPayable(payee, 1, null);
        BigDecimal expected = payable.getAmountDue();
        Payment payment = FakeDataUtil.buildPartialPayment(payable, 1);
        // Balance due *should* be the payable amount due minus the payment amount paid.
        expected = expected.subtract(payment.getAmountPaid());
        List<Payable> payables = new ArrayList<>();
        payables.add(payable);
        Mockito.when(payableRepository.findAllByOrderByDueDate()).thenReturn(payables);
        List<PayableDueBean> payableDues = payableService.findAllPayableDues();
        assertEquals(1, payableDues.size());
        PayableDueBean payableDueBean = payableDues.iterator().next();
        assertEquals(0, payableDueBean.getBalanceDue().compareTo(expected));
        assertEquals(PayableService.OVERDUE_CLASS, payableDueBean.getStyleClass());
    }

    @Test
    public void findAllPayableDuesUnpaidPreviousBalance() {
        Payee payee = FakeDataUtil.buildPayee(1);
        Payable payable = FakeDataUtil.buildPayable(payee, 1, null);
        // Part of the current amount due is overdue balance from the previous payable. This should not be included in
        // this payable's balance due.
        //
        // Previous balance is between $1,000 and $10,000.
        int previousBalanceInt = random.nextInt(9000) + 1000;
        BigDecimal previousBalance = BigDecimal.valueOf(previousBalanceInt, 2);
        payable.setPreviousBalance(previousBalance);
        // Previous payments is between 5% and 40% of previous balance. It is a negative number.
        BigDecimal mult = BigDecimal.valueOf(random.nextInt(40) + 5, 2);
        BigDecimal previousPayments = previousBalance.multiply(mult).setScale(2, RoundingMode.HALF_UP);
        previousPayments = BigDecimal.ZERO.subtract(previousPayments);
        payable.setPreviousPayments(previousPayments);
        // Current charges is between $1,000 and $10,000.
        BigDecimal currentCharges = payable.getAmountDue();
        // The amount due is previous balance + previous payments + current charges.
        BigDecimal amountDue = previousBalance.add(previousPayments).add(currentCharges);
        payable.setAmountDue(amountDue);
        // Balance due *should* just be the current charges.
        BigDecimal expected = currentCharges;
        List<Payable> payables = new ArrayList<>();
        payables.add(payable);
        Mockito.when(payableRepository.findAllByOrderByDueDate()).thenReturn(payables);
/*
        System.out.println("previousBalance=\"" + previousBalance + "\"");
        System.out.println("previousPayments=\"" + previousPayments + "\"");
        System.out.println("currentCharges=\"" + currentCharges + "\"");
        System.out.println("amountDue=\"" + amountDue + "\"");
        System.out.println("expected=\"" + expected + "\"");
*/
        List<PayableDueBean> payableDues = payableService.findAllPayableDues();
        assertEquals(1, payableDues.size());
        PayableDueBean payableDueBean = payableDues.iterator().next();
        assertEquals(0, payableDueBean.getBalanceDue().compareTo(expected));
        assertEquals(PayableService.OVERDUE_CLASS, payableDueBean.getStyleClass());
    }

    @Test
    public void findAllPayableDuesOverpaidPreviousBalance() {
        Payee payee = FakeDataUtil.buildPayee(1);
        Payable payable = FakeDataUtil.buildPayable(payee, 1, null);
        // The previous balance was paid in full or over paid a bit. It should have no effect on this payable's balance
        // due.
        //
        // Previous balance is between $1,000 and $10,000.
        int previousBalanceInt = random.nextInt(9000) + 1000;
        BigDecimal previousBalance = BigDecimal.valueOf(previousBalanceInt, 2);
        payable.setPreviousBalance(previousBalance);
        // Previous payments is between 105% and 140% of previous balance. It is a negative number.
        BigDecimal mult = BigDecimal.valueOf(random.nextInt(40) + 105, 2);
        BigDecimal previousPayments = previousBalance.multiply(mult).setScale(2, RoundingMode.HALF_UP);
        previousPayments = BigDecimal.ZERO.subtract(previousPayments);
        payable.setPreviousPayments(previousPayments);
        // The amount due is the amount due, i.e. between $1,000 and $10,000.
        BigDecimal amountDue = payable.getAmountDue();
        // Current charges is the amount due - previous balance - previous payments.
        BigDecimal currentCharges = amountDue.subtract(previousBalance).subtract(previousPayments);
        // Balance due *should* be the amount due.
        BigDecimal expected = amountDue;
        List<Payable> payables = new ArrayList<>();
        payables.add(payable);
        Mockito.when(payableRepository.findAllByOrderByDueDate()).thenReturn(payables);
/*
        System.out.println("previousBalance=\"" + previousBalance + "\"");
        System.out.println("previousPayments=\"" + previousPayments + "\"");
        System.out.println("currentCharges=\"" + currentCharges + "\"");
        System.out.println("amountDue=\"" + amountDue + "\"");
        System.out.println("expected=\"" + expected + "\"");
*/
        List<PayableDueBean> payableDues = payableService.findAllPayableDues();
        assertEquals(1, payableDues.size());
        PayableDueBean payableDueBean = payableDues.iterator().next();
        assertEquals(0, payableDueBean.getBalanceDue().compareTo(expected));
        assertEquals(PayableService.OVERDUE_CLASS, payableDueBean.getStyleClass());
    }

    @Test
    public void findAllPayableDuesCredit() {
        Payee payee = FakeDataUtil.buildPayee(1);
        Payable payable = FakeDataUtil.buildPayable(payee, 1, null);
        // The previous balance was overpaid by so much, we currently have a credit.
        //
        // Previous balance is between $1,000 and $10,000.
        int previousBalanceInt = random.nextInt(9000) + 1000;
        BigDecimal previousBalance = BigDecimal.valueOf(previousBalanceInt, 2);
        payable.setPreviousBalance(previousBalance);
        // Current charges is between $1,000 and $10,000.
        BigDecimal currentCharges = payable.getAmountDue();
        // Previous payments is previous balance + current charges + 5% to 40%. It is a negative number.
        BigDecimal mult = BigDecimal.valueOf(random.nextInt(40) + 105, 2);
        BigDecimal previousPayments = previousBalance.add(currentCharges).multiply(mult)
                .setScale(2, RoundingMode.HALF_UP);
        previousPayments = BigDecimal.ZERO.subtract(previousPayments);
        payable.setPreviousPayments(previousPayments);
        // The amount due is previous balance + previous payments + current charges. It should be a credit.
        BigDecimal amountDue = previousBalance.add(previousPayments).add(currentCharges);
        payable.setAmountDue(amountDue);
        List<Payable> payables = new ArrayList<>();
        payables.add(payable);
        Mockito.when(payableRepository.findAllByOrderByDueDate()).thenReturn(payables);
/*
        System.out.println("previousBalance=\"" + previousBalance + "\"");
        System.out.println("previousPayments=\"" + previousPayments + "\"");
        System.out.println("currentCharges=\"" + currentCharges + "\"");
        System.out.println("amountDue=\"" + amountDue + "\"");
*/
        List<PayableDueBean> payableDues = payableService.findAllPayableDues();
        assertEquals(0, payableDues.size());
    }
}