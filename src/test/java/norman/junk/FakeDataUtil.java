package norman.junk;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.AcctType;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.domain.Payment;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeDataUtil {
    private static final Logger logger = LoggerFactory.getLogger(FakeDataUtil.class);
    private static Random random = new Random();
    private static final int NBR_OF_ACCTS = 3;
    private static final String[] ACCT_NAME_PART_1 = {"Abominable", "Bulimic", "Cosmic", "Desperate", "Evil", "Funky",
            "Ginormous", "Hungry", "Interstellar", "Jurassic"};
    private static final String[] ACCT_NAME_PART_2 = {"Bank", "Credit Union", "Countinghouse", "Finance Corp", "Fund",
            "Investments", "Saving & Loan", "Thrift", "Treasury", "Trust"};
    private static final int ACCT_BEGIN_DAYS_AGO = 30;
    private static final String INSERT_INTO_ACCT = "INSERT INTO `acct` (`begin_balance`, `begin_date`, `credit_limit`, `name`, `type`, `version`) VALUES (%.2f,'%tF',%.2f,'%s','%s',0);%n";

    private static final String INSERT_INTO_ACCT_NBR = "INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('%tF','%s',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";


    private static final int NBR_OF_PAYEES = 5;
    private static final String[] PAYEE_NAME_PART_1 = {"Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious",
            "Pedantic", "Quiescent", "Recalcitrant", "Sleazy", "Taciturn"};
    private static final String[] PAYEE_NAME_PART_2 = {"Cable TV", "Credit Card", "Gas", "Gym", "Insurance",
            "Lawn Service", "Mortgage", "Power", "Subscription Service", "Water and Sewer"};
    private static final String INSERT_INTO_PAYEE = "INSERT INTO `payee` (`name`, `number`, `version`) VALUES ('%s','%s',0);%n";
    private static final int NBR_OF_PAYABLES = 7;
    private static final int PAYABLE_BEGIN_DAYS_AGO = 17;
    private static final int DAYS_BETWEEN_PAYABLES = 7;
    private static final String INSERT_INTO_PAYABLE = "INSERT INTO `payable` (`amount_due`, `due_date`, `version`, `payee_id`) VALUES (%.2f,'%tF',0,(SELECT `id` FROM `payee` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_PAYMENT = "INSERT INTO `payment` (`amount_paid`, `paid_date`, `version`, `payable_id`) VALUES (%.2f,'%tF',0,(SELECT a.`id` FROM `payable` a JOIN `payee` b ON b.`id` = a.`payee_id` WHERE a.`due_date` = '%tF' AND b.`name` = '%s'));%n";

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        logger.debug("Starting FakeDataUtil");
        long acctId = 1;
        long acctNbrId = 1;
        Set<String> uniqueAcctNames = new HashSet<>();
        for (int i = 0; i< NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, uniqueAcctNames);
            System.out.printf(INSERT_INTO_ACCT, acct.getBeginBalance(), acct.getBeginDate(), acct.getCreditLimit(),acct.getName(),acct.getType());
            AcctNbr acctNbr = buildAcctNbr(acct, acctNbrId++);
            System.out.printf(INSERT_INTO_ACCT_NBR, acctNbr.getEffDate(),acctNbr.getNumber(),acct.getName());
        }

        long payeeId = 1;
        long payableId = 1;
        long paymentId = 1;
        Set<String> uniquePayeeNames = new HashSet<>();
        for (int i = 0; i < NBR_OF_PAYEES; i++) {
            Payee payee = buildPayee(payeeId++, uniquePayeeNames);
            System.out.printf(INSERT_INTO_PAYEE, payee.getName(), payee.getNumber());
            //
            Date previousDueDate = null;
            for (int j = 0; j < NBR_OF_PAYABLES; j++) {
                Payable payable = buildPayable(payee, payableId++, previousDueDate);
                System.out.printf(INSERT_INTO_PAYABLE, payable.getAmountDue(), payable.getDueDate(), payee.getName());
                // 0 = No payment.
                // 1 = One payment, paid in full.
                // 2 = Two payments, paid in full.
                // 3 = One payment, partial payment.
                // 4 = Two payments, partial payment.
                int paymentCase = random.nextInt(5);
                List<Payment> payments = new ArrayList<>();
                if (paymentCase == 1) {
                    payments.add(buildFullPayment(payable, paymentId++));
                } else if (paymentCase == 2) {
                    payments.add(buildPartialPayment(payable, paymentId++));
                    payments.add(buildFullPayment(payable, paymentId++));
                } else if (paymentCase == 3) {
                    payments.add(buildPartialPayment(payable, paymentId++));
                } else if (paymentCase == 4) {
                    payments.add(buildPartialPayment(payable, paymentId++));
                    payments.add(buildPartialPayment(payable, paymentId++));
                }
                for (Payment payment : payments) {
                    System.out.printf(INSERT_INTO_PAYMENT, payment.getAmountPaid(), payment.getPaidDate(),
                            payable.getDueDate(), payee.getName());
                }
                previousDueDate = payable.getDueDate();
            }
        }
        logger.debug("Finished FakeDataUtil");
    }
    public static Acct buildAcct(long id, Set<String> uniqueNames) {
        String name;
        do {
             name = ACCT_NAME_PART_1[random.nextInt(ACCT_NAME_PART_1.length)] + " " +
                    ACCT_NAME_PART_2[random.nextInt(ACCT_NAME_PART_2.length)];

        } while (uniqueNames.contains(name));
        uniqueNames.add(name);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, ACCT_BEGIN_DAYS_AGO * -1);
        Date beginDate = cal.getTime();

        BigDecimal beginBalance = BigDecimal.valueOf(random.nextInt(100000), 2);
        BigDecimal creditLimit = BigDecimal.valueOf(100000, 2);
        AcctType acctType = AcctType.values()[random.nextInt(AcctType.values().length)];
        if (acctType == AcctType.CC) {
            beginBalance = BigDecimal.ZERO.subtract(beginBalance);
            creditLimit = BigDecimal.ZERO.subtract(creditLimit);
        }
        Acct acct = new Acct();
        acct.setId(id);
        acct.setName(name);
        acct.setBeginDate(beginDate);
        acct.setBeginBalance(beginBalance);
        acct.setCreditLimit(creditLimit);
        acct.setType(acctType);
        return acct;
    }

    public static AcctNbr buildAcctNbr(Acct acct, long id) {
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setId(id);
        acctNbr.setNumber(RandomStringUtils.randomNumeric(12));
        acctNbr.setEffDate(acct.getBeginDate());
        acctNbr.setAcct(acct);
        acct.getAcctNbrs().add(acctNbr);
        return acctNbr;
    }

    public static Payee buildPayee(long id) {
        Set<String> uniqueNames = new HashSet<>();
        return buildPayee(id, uniqueNames);
    }

    public static Payee buildPayee(long id, Set<String> uniqueNames) {
        String name;
        do {
            name = PAYEE_NAME_PART_1[random.nextInt(PAYEE_NAME_PART_1.length)] + " " +
                    PAYEE_NAME_PART_2[random.nextInt(PAYEE_NAME_PART_2.length)];
        } while (uniqueNames.contains(name));
        uniqueNames.add(name);
        Payee payee = new Payee();
        payee.setId(id);
        payee.setName(name);
        payee.setNumber(RandomStringUtils.randomNumeric(12));
        return payee;
    }

    public static Payable buildPayable(Payee payee, long id, Date previousDueDate) {
        int amountDueInt = random.nextInt(9000) + 1000;
        BigDecimal amountDue = BigDecimal.valueOf(amountDueInt, 2);
        Calendar cal = Calendar.getInstance();
        if (previousDueDate == null) {
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.add(Calendar.DATE, PAYABLE_BEGIN_DAYS_AGO * -1);
            cal.add(Calendar.DATE, random.nextInt(DAYS_BETWEEN_PAYABLES));
        } else {
            cal.setTime(previousDueDate);
            cal.add(Calendar.DATE, DAYS_BETWEEN_PAYABLES);
        }
        Date dueDate = cal.getTime();
        Payable payable = new Payable();
        payable.setId(id);
        payable.setAmountDue(amountDue);
        payable.setDueDate(dueDate);
        payable.setPayee(payee);
        payee.getPayables().add(payable);
        return payable;
    }

    public static Payment buildFullPayment(Payable payable, long id) {
        BigDecimal amountPaidSoFar = BigDecimal.ZERO;
        for (Payment payment : payable.getPayments()) {
            amountPaidSoFar = amountPaidSoFar.add(payment.getAmountPaid());
        }
        BigDecimal amountPaid = payable.getAmountDue().subtract(amountPaidSoFar);
        return buildPaymentImpl(payable, id, amountPaid);
    }

    public static Payment buildPartialPayment(Payable payable, long id) {
        BigDecimal mult = BigDecimal.valueOf(random.nextInt(40) + 5, 2);
        BigDecimal amountPaid = payable.getAmountDue().multiply(mult).setScale(2, RoundingMode.HALF_UP);
        return buildPaymentImpl(payable, id, amountPaid);
    }

    private static Payment buildPaymentImpl(Payable payable, long id, BigDecimal amountPaid) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmountPaid(amountPaid);
        payment.setPaidDate(payable.getDueDate());
        payment.setPayable(payable);
        payable.getPayments().add(payment);
        return payment;
    }
}