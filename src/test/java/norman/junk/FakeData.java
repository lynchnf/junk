package norman.junk;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import norman.junk.controller.AcctController;
import norman.junk.domain.AcctType;
import norman.junk.domain.TranType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeData {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    private static final int NBR_OF_ACCTS = 5;
    private static final String[] ACCT_NAME_PART_1 = {"Last National", "Celestial", "Nuclear", "Funky Town",
            "The Sleazy"};
    private static final String[] ACCT_NAME_PART_2 = {"Bank", "Trust", "Saving & Loan", "Finance Corp", "Credit Union"};
    private static final String[] ACCT_NAME_PART_3 = {"of Evil", "in Neverland", "Cartel", "and Bait Shop",
            "All Covered with Cheese"};
    private static final int BEGIN_DAYS_AGO = 20;
    private static final int MAX_DAYS_BETWEEN_TRANS = 5;
    private static final int CHECKS_ARE_ONE_IN = 3;
    private static final int NBR_OF_PAYEES = 5;
    private static final String[] PAYEE_NAME_PART_1 = {"Ludicrous", "Hungry", "Outer Mongolia", "Sasquatch",
            "Antimatter", "Hobo", "Creeper", "Death Star", "Undead", "Choo-choo"};
    private static final String[] PAYEE_NAME_PART_2 = {"Power", "Gas", "Water & Sewer", "Cable TV", "Lawn Service",
            "Mortgage", "Insurance", "Credit Card", "Gym", "Subscription Service"};
    private static final BigDecimal MINUS_ONE = new BigDecimal(-1);
    private static Random random = new Random();

    public static void main(String[] args) {
        logger.debug("Starting FakeData");
        FakeData me = new FakeData();
        me.accts();
        me.payees();
        logger.debug("Finished FakeData");
    }

    private void accts() {
        Set<String> acctNames = new LinkedHashSet();
        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            do {
                String acctName = ACCT_NAME_PART_1[random.nextInt(ACCT_NAME_PART_1.length)] + " " +
                        ACCT_NAME_PART_2[random.nextInt(ACCT_NAME_PART_2.length)] + " " +
                        ACCT_NAME_PART_3[random.nextInt(ACCT_NAME_PART_3.length)];
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
            //logger.debug("name=\"" + name + "\"");
            BigDecimal beginBalance = BigDecimal.valueOf(random.nextInt(100000), 2);
            AcctType acctType = AcctType.values()[random.nextInt(AcctType.values().length)];
            if (acctType == AcctType.CC)
                beginBalance = beginBalance.multiply(MINUS_ONE);
            String number = RandomStringUtils.randomNumeric(12);
            System.out.printf("INSERT INTO `acct` (`begin_balance`, `begin_date`, `name`, `type`, `version`)" +
                    " VALUES (%.2f,'%tF','%s','%s',0);%n", beginBalance, beginDate, acctName, acctType);
            System.out.printf("INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`)" +
                    " VALUES ('%tF','%s',0,(SELECT id FROM acct WHERE name = '%s'));%n", beginDate, number, acctName);
            cal.setTime(beginDate);
            cal.add(Calendar.DATE, random.nextInt(MAX_DAYS_BETWEEN_TRANS));
            Date postDate = cal.getTime();
            while (postDate.before(today)) {
                BigDecimal amount = BigDecimal.valueOf(random.nextInt(20000) - 10000, 2);
                String checkNumber = "NULL";
                TranType tranType = TranType.DEBIT;
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    tranType = TranType.CREDIT;
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
                System.out
                        .printf("INSERT INTO `tran` (`amount`, `check_number`, `name`, `post_date`, `type`, `version`, `acct_id`)" +
                                        " VALUES (%.2f,%s,'%s','%tF','%s',0,(SELECT id FROM acct WHERE name = '%s'));%n",
                                amount, checkNumber, tranName, postDate, tranType, acctName);
                cal.add(Calendar.DATE, random.nextInt(MAX_DAYS_BETWEEN_TRANS));
                postDate = cal.getTime();
            }
        }
    }

    private void payees() {
        Set<String> payeeNames = new LinkedHashSet();
        for (int i = 0; i < NBR_OF_PAYEES; i++) {
            do {
                String payeeName = PAYEE_NAME_PART_1[random.nextInt(PAYEE_NAME_PART_1.length)] + " " +
                        PAYEE_NAME_PART_2[random.nextInt(PAYEE_NAME_PART_2.length)];
                if (!payeeNames.contains(payeeName)) {
                    payeeNames.add(payeeName);
                }
            } while (payeeNames.size() <= i);
        }
        for (String payeeName : payeeNames) {
            String number = RandomStringUtils.randomNumeric(12);
            System.out.printf("INSERT INTO `payee` (`name`, `number`, `version`)" + " VALUES ('%s','%s',0);%n",
                    payeeName, number);
        }
    }
}