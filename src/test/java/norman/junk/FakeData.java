package norman.junk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import norman.junk.controller.AcctController;
import norman.junk.domain.AcctType;
import norman.junk.domain.TranType;
import norman.junk.domain.UserRole;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeData {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    private static Random random = new Random();
    private static final int NBR_OF_ACCTS = 3;
    private static final String[] ACCT_NAME_PART_1 = {"Abominable", "Bulimic", "Cosmic", "Desperate", "Evil", "Funky",
            "Ginormous", "Hungry", "Interstellar", "Jurassic"};
    private static final String[] ACCT_NAME_PART_2 = {"Bank", "Credit Union", "Countinghouse", "Finance Corp", "Fund",
            "Investments", "Saving & Loan", "Thrift", "Treasury", "Trust"};
    private static final int BEGIN_DAYS_AGO = 30;
    private static final int MAX_DAYS_BETWEEN_TRANS = 5;
    private static final int CHECKS_ARE_ONE_IN = 3;
    private static final int NBR_OF_PAYEES = 5;
    private static final String[] PAYEE_NAME_PART_1 = {"Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious",
            "Pedantic", "Quiescent", "Recalcitrant", "Sleazy", "Taciturn"};
    private static final String[] PAYEE_NAME_PART_2 = {"Cable TV", "Credit Card", "Gas", "Gym", "Insurance",
            "Lawn Service", "Mortgage", "Power", "Subscription Service", "Water & Sewer"};
    private static final int FUTURE_DAYS_AHEAD = 30;
    private static final int DAYS_BETWEEN_PAYABLES = 7;
    private static final int NBR_OF_CATEGORIES = 10;
    private static final BigDecimal MINUS_ONE = new BigDecimal(-1);
    private static final int NBR_OF_USERS = 5;
    private static final String[] USER_FIRST_NAMES = {"Aedan", "Bilbo", "Cidi", "Drogo", "Elmer", "Fafner", "Gilmi",
            "Harrold", "Isden", "Jack"};
    private static final String[] USER_LAST_NAMES = {"Killdeer", "Longbottom", "Merrifello", "Nearmiss", "Overbarrel",
            "Pewter", "Queasitummi", "Rainbow", "Stinkbeard", "Talltopp"};
    private static final String[] QUESTIONS = {"Can God create a rock so heavy that he cannot lift it?",
            "Is the answer to this question \"no\"?",
            "If the barber shaves those and only those who do not shave themselves, then does the barber shave himself?",
            "Can a man drown in the fountain of eternal life?",
            "If everything is possible, is it possible for anything to be impossible?"};
    private static final String INSERT_INTO_ACCT = "INSERT INTO `acct` (`begin_balance`, `begin_date`, `credit_limit`, `name`, `type`, `version`) VALUES (%.2f,'%tF',%.2f,'%s','%s',0);%n";
    private static final String INSERT_INTO_ACCT_NBR = "INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('%tF','%s',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_TRAN = "INSERT INTO `tran` (`amount`, `check_number`, `name`, `post_date`, `type`, `version`, `acct_id`) VALUES (%.2f,%s,'%s','%tF','%s',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_PAYEE = "INSERT INTO `payee` (`name`, `number`, `version`) VALUES ('%s','%s',0);%n";
    private static final String INSERT_INTO_PAYABLE = "INSERT INTO `payable` (`amount_due`, `due_date`, `version`, `payee_id`) VALUES (%.2f,'%tF',0,(SELECT `id` FROM `payee` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_PAYMENT = "INSERT INTO `payment` (`amount_paid`, `paid_date`, `version`, `payable_id`) VALUES (%.2f,'%tF',0,(SELECT a.`id` FROM `payable` a JOIN `payee` b ON b.`id` = a.`payee_id` WHERE a.`due_date` = '%tF' AND b.`name` = '%s'));%n";
    private static final String INSERT_INTO_CATEGORY = "INSERT INTO `category` (`name`, `version`) VALUES ('%s',0);%n";
    private static final String INSERT_INTO_PATTERN = "INSERT INTO `pattern` (`seq`, `tran_name`, `version`, `category_id`) VALUES (%d,'%s',0,(SELECT `id` FROM `category` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_USER = "INSERT INTO `user` (`first_name`, `last_name`, `password`, `role`, `username`, `version`) VALUES ('%s','%s','%s','%s','%s',0);%n";
    private static final String INSERT_INTO_QUESTION = "INSERT INTO `security_question` (`question_text`, `version`) VALUES ('%s',0);%n";

    public static void main(String[] args) {
        logger.debug("Starting FakeData");
        FakeData me = new FakeData();
        me.accts();
        me.payees();
        me.categories();
        me.users();
        me.questions();
        logger.debug("Finished FakeData");
    }

    private void accts() {
        Set<String> acctNames = new LinkedHashSet<>();
        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            do {
                String acctName = ACCT_NAME_PART_1[random.nextInt(ACCT_NAME_PART_1.length)] + " " +
                        ACCT_NAME_PART_2[random.nextInt(ACCT_NAME_PART_2.length)];
                if (!acctNames.contains(acctName)) {
                    acctNames.add(acctName);
                }
            } while (acctNames.size() <= i);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date today = cal.getTime();
        cal.add(Calendar.DATE, BEGIN_DAYS_AGO * -1);
        Date beginDate = cal.getTime();
        for (String acctName : acctNames) {
            BigDecimal beginBalance = BigDecimal.valueOf(random.nextInt(100000), 2);
            BigDecimal creditLimit = BigDecimal.valueOf(100000, 2);
            AcctType acctType = AcctType.values()[random.nextInt(AcctType.values().length)];
            if (acctType == AcctType.CC) {
                beginBalance = beginBalance.multiply(MINUS_ONE);
                creditLimit = creditLimit.multiply(MINUS_ONE);
            }
            String number = RandomStringUtils.randomNumeric(12);
            System.out.printf(INSERT_INTO_ACCT, beginBalance, beginDate, creditLimit, acctName, acctType);
            System.out.printf(INSERT_INTO_ACCT_NBR, beginDate, number, acctName);
            tran(today, beginDate, acctName, acctType);
        }
    }

    private void tran(Date today, Date beginDate, String acctName, AcctType acctType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        cal.add(Calendar.DATE, random.nextInt(MAX_DAYS_BETWEEN_TRANS));
        Date postDate = cal.getTime();
        while (postDate.before(today)) {
            BigDecimal amount = BigDecimal.valueOf(random.nextInt(20000) - 10000, 2);
            String checkNumber = "NULL";
            TranType tranType = TranType.CREDIT;
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                tranType = TranType.DEBIT;
                if (acctType == AcctType.CHECKING && random.nextInt(CHECKS_ARE_ONE_IN) == 0) {
                    checkNumber = "'" + RandomStringUtils.randomNumeric(4) + "'";
                    tranType = TranType.CHECK;
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(StringUtils.capitalize(RandomStringUtils.randomAlphabetic(1, 10).toLowerCase()));
                sb.append(" ");
            }
            String tranName = sb.toString();
            if (tranName.length() > 50)
                tranName = tranName.substring(0, 50);
            System.out.printf(INSERT_INTO_TRAN, amount, checkNumber, tranName, postDate, tranType, acctName);
            cal.add(Calendar.DATE, random.nextInt(MAX_DAYS_BETWEEN_TRANS));
            postDate = cal.getTime();
        }
    }

    private void payees() {
        Set<String> payeeNames = new LinkedHashSet<>();
        for (int i = 0; i < NBR_OF_PAYEES; i++) {
            do {
                String payeeName = PAYEE_NAME_PART_1[random.nextInt(PAYEE_NAME_PART_1.length)] + " " +
                        PAYEE_NAME_PART_2[random.nextInt(PAYEE_NAME_PART_2.length)];
                if (!payeeNames.contains(payeeName)) {
                    payeeNames.add(payeeName);
                }
            } while (payeeNames.size() <= i);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date today = cal.getTime();
        cal.add(Calendar.DATE, FUTURE_DAYS_AHEAD);
        Date future = cal.getTime();
        cal.setTime(today);
        cal.add(Calendar.DATE, BEGIN_DAYS_AGO * -1);
        Date beginDate = cal.getTime();
        for (String payeeName : payeeNames) {
            String number = RandomStringUtils.randomNumeric(12);
            System.out.printf(INSERT_INTO_PAYEE, payeeName, number);
            payables(future, beginDate, payeeName);
        }
    }

    private void payables(Date future, Date beginDate, String payeeName) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        cal.add(Calendar.DATE, random.nextInt(DAYS_BETWEEN_PAYABLES));
        Date payDueDt = cal.getTime();
        while (payDueDt.before(future)) {
            int newBalInt = random.nextInt(9000) + 1000;
            BigDecimal newBalTot = BigDecimal.valueOf(newBalInt, 2);
            System.out.printf(INSERT_INTO_PAYABLE, newBalTot, payDueDt, payeeName);
            payments(payeeName, payDueDt, newBalInt);
            cal.add(Calendar.DATE, DAYS_BETWEEN_PAYABLES);
            payDueDt = cal.getTime();
        }
    }

    private void payments(String payeeName, Date payDueDt, int newBalInt) {
        List<BigDecimal> paidAmts = new ArrayList<>();
        // 0 = No payment.
        // 1 = One payment, paid in full.
        // 2 = Two payments, paid in full.
        // 3 = One payment, partial payment.
        // 4 = Two payments, partial payment.
        int paymentCase = random.nextInt(5);
        if (paymentCase == 1) {
            paidAmts.add(BigDecimal.valueOf(newBalInt, 2));
        } else if (paymentCase == 2) {
            int paidInt1 = newBalInt / 2 + random.nextInt(2000) - 1000;
            int paidInt2 = newBalInt - paidInt1;
            paidAmts.add(BigDecimal.valueOf(paidInt1, 2));
            paidAmts.add(BigDecimal.valueOf(paidInt2, 2));
        } else if (paymentCase == 3) {
            int paidInt = newBalInt - random.nextInt(1000);
            paidAmts.add(BigDecimal.valueOf(paidInt, 2));
        } else if (paymentCase == 4) {
            int paidInt1 = newBalInt / 2 - random.nextInt(500);
            int paidInt2 = newBalInt / 2 - random.nextInt(500);
            paidAmts.add(BigDecimal.valueOf(paidInt1, 2));
            paidAmts.add(BigDecimal.valueOf(paidInt2, 2));
        }
        for (BigDecimal paidAmt : paidAmts) {
            System.out.printf(INSERT_INTO_PAYMENT, paidAmt, payDueDt, payDueDt, payeeName);
        }
    }

    private void categories() {
        for (int i = 0; i < NBR_OF_CATEGORIES; i++) {
            String categoryName = StringUtils.capitalize(RandomStringUtils.randomAlphabetic(1, 10).toLowerCase());
            System.out.printf(INSERT_INTO_CATEGORY, categoryName);
            patterns(i, categoryName);
        }
    }

    private void patterns(int i, String categoryName) {
        int seq = i + 1;
        String pattern = categoryName;
        if (categoryName.length() > 1)
            pattern = categoryName.substring(0, 1);
        pattern += ".*";
        System.out.printf(INSERT_INTO_PATTERN, seq, pattern, categoryName);
    }

    private void users() {
        System.out.printf(INSERT_INTO_USER, "Admin", "Admin", "admin", UserRole.ADMIN, "admin");
        Set<String> usernames = new LinkedHashSet<>();
        for (int i = 0; i < NBR_OF_USERS; i++) {
            do {
                String username = RandomStringUtils.randomAlphabetic(3).toLowerCase() + "@example.com";
                if (!usernames.contains(username)) {
                    usernames.add(username);
                }
            } while (usernames.size() <= i);
        }
        for (String username : usernames) {
            String firstName = USER_FIRST_NAMES[random.nextInt(USER_FIRST_NAMES.length)];
            String lastName = USER_LAST_NAMES[random.nextInt(USER_LAST_NAMES.length)];
            UserRole role = UserRole.values()[random.nextInt(UserRole.values().length - 1) + 1];
            String password = "password";
            System.out.printf(INSERT_INTO_USER, firstName, lastName, password, role, username);
        }
    }

    private void questions() {
        for (String question : QUESTIONS) {
            System.out.printf(INSERT_INTO_QUESTION, question);
        }
    }
}