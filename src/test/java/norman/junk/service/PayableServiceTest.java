package norman.junk.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import norman.junk.FakeDataUtil;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.domain.Payment;
import norman.junk.repository.PayableRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class PayableServiceTest {
    private Random random = new Random();
    private Long payable1Id;
    private Long payable2Id;
    private Payee payee1;
    private Payable payable1;
    private BigDecimal payable1AmountDue;

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
        payable1Id = Long.valueOf(1);
        payable2Id = Long.valueOf(2);
        payee1 = FakeDataUtil.buildPayee(3);
        payable1 = FakeDataUtil.buildPayable(payee1, payable1Id, null);
        payable1AmountDue = payable1.getAmountDue();
    }

    @Test
    public void findAllPayables() {
        Mockito.when(payableRepository.findAll()).thenReturn(new ArrayList<>());
        Iterable<Payable> payables = payableService.findAllPayables();
        assertFalse(payables.iterator().hasNext());
    }

    @Test
    public void findPayableById() throws Exception {
        Mockito.when(payableRepository.findById(payable1Id)).thenReturn(Optional.of(payable1));
        Payable payable = payableService.findPayableById(payable1Id);
        assertEquals(0, payable1AmountDue.compareTo(payable.getAmountDue()));
    }

    @Test
    public void findPayableByIdNotFound() {
        Mockito.when(payableRepository.findById(payable2Id)).thenReturn(Optional.empty());
        try {
            Payable payable = payableService.findPayableById(payable2Id);
            fail();
        } catch (JunkNotFoundException e) {
        }
    }

    @Test
    public void savePayable() throws Exception {
        Mockito.when(payableRepository.save(Mockito.any(Payable.class))).thenReturn(payable1);
        Payable payable = payableService.savePayable(payable1);
        assertEquals(0, payable1AmountDue.compareTo(payable.getAmountDue()));
    }

    @Test
    public void savePayableOptimisticLocking() {
        Mockito.when(payableRepository.save(Mockito.any(Payable.class)))
                .thenThrow(ObjectOptimisticLockingFailureException.class);
        try {
            Payable payable = payableService.savePayable(payable1);
            fail();
        } catch (JunkOptimisticLockingException e) {
        }
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