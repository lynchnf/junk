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
import norman.junk.domain.Category;
import norman.junk.domain.Pattern;
import norman.junk.domain.Payable;
import norman.junk.domain.Payee;
import norman.junk.domain.Payment;
import norman.junk.domain.Tran;
import norman.junk.domain.TranType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
    private static final int MAX_DAYS_BETWEEN_TRANS = 5;
    private static final int CHECKS_ARE_ONE_IN = 3;
    private static final String INSERT_INTO_TRAN = "INSERT INTO `tran` (`amount`, `check_number`, `name`, `post_date`, `type`, `version`, `acct_id`) VALUES (%.2f,%s,'%s','%tF','%s',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final int NBR_OF_PAYEES = 5;
    private static final String[] PAYEE_NAME_PART_1 = {"Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious",
            "Pedantic", "Quiescent", "Recalcitrant", "Sleazy", "Taciturn"};
    private static final String[] PAYEE_NAME_PART_2 = {"Cable TV", "Credit Card", "Gas", "Gym", "Insurance",
            "Lawn Service", "Mortgage", "Power", "Subscription Service", "Water and Sewer"};
    private static final String INSERT_INTO_PAYEE = "INSERT INTO `payee` (`name`, `number`, `version`) VALUES ('%s','%s',0);%n";
    private static final int PAYABLE_BEGIN_DAYS_AGO = 17;
    private static final int DAYS_BETWEEN_PAYABLES = 7;
    private static final int PAYABLE_FUTURE_DAYS_AHEAD = 30;
    private static final String INSERT_INTO_PAYABLE = "INSERT INTO `payable` (`amount_due`, `due_date`, `version`, `payee_id`) VALUES (%.2f,'%tF',0,(SELECT `id` FROM `payee` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_PAYMENT = "INSERT INTO `payment` (`amount_paid`, `paid_date`, `version`, `payable_id`) VALUES (%.2f,'%tF',0,(SELECT a.`id` FROM `payable` a JOIN `payee` b ON b.`id` = a.`payee_id` WHERE a.`due_date` = '%tF' AND b.`name` = '%s'));%n";
    private static final int NBR_OF_CATEGORIES = 10;
    private static final String INSERT_INTO_CATEGORY = "INSERT INTO `category` (`name`, `version`) VALUES ('%s',0);%n";
    private static final String INSERT_INTO_PATTERN = "INSERT INTO `pattern` (`seq`, `tran_name`, `version`, `category_id`) VALUES (%d,'%s',0,(SELECT `id` FROM `category` WHERE `name` = '%s'));%n";

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        logger.debug("Starting FakeDataUtil");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date today = cal.getTime();
        long acctId = 1;
        long acctNbrId = 1;
        long tranId = 1;
        Set<String> uniqueAcctNames = new HashSet<>();
        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, uniqueAcctNames);
            System.out.printf(INSERT_INTO_ACCT, acct.getBeginBalance(), acct.getBeginDate(), acct.getCreditLimit(),
                    acct.getName(), acct.getType());
            AcctNbr acctNbr = buildAcctNbr(acct, acctNbrId++);
            System.out.printf(INSERT_INTO_ACCT_NBR, acctNbr.getEffDate(), acctNbr.getNumber(), acct.getName());
            Date postDate = null;
            do {
                Tran tran = buildTran(acct, tranId++, postDate);
                postDate = tran.getPostDate();
                if (postDate.before(today)) {
                    String checkNumber = "NULL";
                    if (tran.getCheckNumber() != null)
                        checkNumber = "'" + tran.getCheckNumber() + "'";
                    System.out.printf(INSERT_INTO_TRAN, tran.getAmount(), checkNumber, tran.getName(), postDate,
                            tran.getType(), acct.getName());
                }
            } while (postDate.before(today));
        }
        long payeeId = 1;
        long payableId = 1;
        long paymentId = 1;
        cal.setTime(today);
        cal.add(Calendar.DATE, PAYABLE_FUTURE_DAYS_AHEAD);
        Date future = cal.getTime();
        Set<String> uniquePayeeNames = new HashSet<>();
        for (int i = 0; i < NBR_OF_PAYEES; i++) {
            Payee payee = buildPayee(payeeId++, uniquePayeeNames);
            System.out.printf(INSERT_INTO_PAYEE, payee.getName(), payee.getNumber());
            //

            Date dueDate = null;
            do {
                Payable payable = buildPayable(payee, payableId++, dueDate);
                dueDate = payable.getDueDate();
                if (dueDate.before(future)) {
                    System.out
                            .printf(INSERT_INTO_PAYABLE, payable.getAmountDue(), payable.getDueDate(), payee.getName());
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
                }
            } while (dueDate.before(future));
        }
        long categoryId = 1;
        long patternId = 1;
        Set<String> uniqueCategoryNames = new HashSet<>();
        for (int i = 0; i < NBR_OF_CATEGORIES; i++) {
            Category category = buildCategory(categoryId++, uniqueCategoryNames);
            System.out.printf(INSERT_INTO_CATEGORY, category.getName());
            Pattern pattern = buildPattern(category, patternId++);
            System.out.printf(INSERT_INTO_PATTERN, pattern.getSeq(), pattern.getTranName(), category.getName());
        }
        logger.debug("Finished FakeDataUtil");
    }

    public static Acct buildAcct(long id) {
        Set<String> uniqueNames = new HashSet<>();
        return buildAcct(id, uniqueNames);
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

    public static Tran buildTran(Acct acct, long id, Date previousPostDate) {
        Calendar cal = Calendar.getInstance();
        if (previousPostDate == null) {
            cal.setTime(acct.getBeginDate());
        } else {
            cal.setTime(previousPostDate);
        }
        cal.add(Calendar.DATE, random.nextInt(MAX_DAYS_BETWEEN_TRANS));
        Date postDate = cal.getTime();
        BigDecimal amount = BigDecimal.valueOf(random.nextInt(20000) - 10000, 2);
        String checkNumber = null;
        TranType tranType = TranType.CREDIT;
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            tranType = TranType.DEBIT;
            if (acct.getType() == AcctType.CHECKING && random.nextInt(CHECKS_ARE_ONE_IN) == 0) {
                checkNumber = RandomStringUtils.randomNumeric(4);
                tranType = TranType.CHECK;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(StringUtils.capitalize(RandomStringUtils.randomAlphabetic(1, 10).toLowerCase()));
            sb.append(" ");
        }
        String name = sb.toString();
        if (name.length() > 50)
            name = name.substring(0, 50);
        Tran tran = new Tran();
        tran.setId(id);
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setName(name);
        tran.setCheckNumber(checkNumber);
        tran.setType(tranType);
        tran.setAcct(acct);
        acct.getTrans().add(tran);
        return tran;
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

    public static Category buildCategory(long id) {
        Set<String> uniqueNames = new HashSet<>();
        return buildCategory(id, uniqueNames);
    }

    public static Category buildCategory(long id, Set<String> uniqueNames) {
        String name;
        do {
            name = StringUtils.capitalize(RandomStringUtils.randomAlphabetic(1, 10).toLowerCase());
        } while (uniqueNames.contains(name));
        uniqueNames.add(name);
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    public static Pattern buildPattern(Category category, long id) {
        String tranName = category.getName();
        if (tranName.length() > 1)
            tranName = tranName.substring(0, 1);
        tranName += ".*";
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setSeq((int) id);
        pattern.setTranName(tranName);
        pattern.setCategory(category);
        return pattern;
    }
}