package norman.junk.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import norman.junk.controller.AcctForm;
import norman.junk.domain.Acct;
import norman.junk.domain.DataFile;
import norman.junk.domain.DataFileStatus;
import norman.junk.domain.Tran;
import norman.junk.repository.AcctRepository;
import norman.junk.repository.DataFileRepository;
import norman.junk.repository.TranRepository;

public class ControllerUtils {
    private static final Logger logger = LoggerFactory.getLogger(ControllerUtils.class);

    private ControllerUtils() {}

    public static void saveTrans(Acct acct, DataFile dataFile, OfxParseResponse response, AcctRepository acctRepository, DataFileRepository dataFileRepository, TranRepository tranRepository,
            RedirectAttributes redirectAttributes) {
        int count = 0;
        for (OfxStmtTran ofxStmtTran : response.getOfxStmtTrans()) {
            List<Tran> trans = tranRepository.findByAcct_IdAndFitId(acct.getId(), ofxStmtTran.getFitId());
            if (trans.size() > 1) {
                String errorMessage = "UNEXPECTED ERROR: Multiple transactions found for acctId=\"" + acct.getId() + ", fitId=\"" + ofxStmtTran.getFitId() + "\"";
                logger.error(errorMessage);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return;
            }
            if (ofxStmtTran.getPostDate().equals(acct.getBeginDate()) || ofxStmtTran.getPostDate().after(acct.getBeginDate())) {
                Tran tran = new Tran();
                tran.setType(ofxStmtTran.getType());
                tran.setPostDate(ofxStmtTran.getPostDate());
                tran.setUserDate(ofxStmtTran.getUserDate());
                tran.setAmount(ofxStmtTran.getAmount());
                tran.setFitId(ofxStmtTran.getFitId());
                tran.setSic(ofxStmtTran.getSic());
                tran.setCheckNumber(ofxStmtTran.getCheckNumber());
                tran.setCorrectFitId(ofxStmtTran.getCorrectFitId());
                tran.setCorrectAction(ofxStmtTran.getCorrectAction());
                tran.setName(ofxStmtTran.getName());
                tran.setCategory(ofxStmtTran.getCategory());
                tran.setMemo(ofxStmtTran.getMemo());
                tran.setAcct(acct);
                acct.getTrans().add(tran);
                count++;
            }
        }
        try {
            acctRepository.save(acct);
            dataFile.setStatus(DataFileStatus.TRAN_SAVED);
            dataFileRepository.save(dataFile);
            String successMessage = "Account successfully updated with " + count + " transactions, acctId=\"" + acct.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "New transactions could not be added to account, acctId=\"" + acct.getId() + "\"";
            logger.error(errorMessage, e);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
    }

    public static String newAcctFromDataFile(Model model, DataFile dataFile, OfxParseResponse response) {
        AcctForm acctForm = new AcctForm();
        acctForm.setOrganization(response.getOfxInst().getOrganization());
        acctForm.setFid(response.getOfxInst().getFid());
        acctForm.setBankId(response.getOfxAcct().getBankId());
        acctForm.setType(response.getOfxAcct().getType());
        acctForm.setNumber(response.getOfxAcct().getAcctId());
        acctForm.setDataFileId(dataFile.getId());
        model.addAttribute("acctForm", acctForm);
        return "acctEdit";
    }
}
