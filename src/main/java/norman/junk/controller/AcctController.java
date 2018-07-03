package norman.junk.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import norman.junk.JunkInconceivableException;
import norman.junk.JunkNotFoundException;
import norman.junk.JunkOfxParseException;
import norman.junk.JunkOptimisticLockingException;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.DataFile;
import norman.junk.domain.DataFileStatus;
import norman.junk.domain.DataLine;
import norman.junk.domain.Tran;
import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;
import norman.junk.service.OfxParseResponse;
import norman.junk.service.OfxParseService;
import norman.junk.service.OfxStmtTran;
import norman.junk.service.TranBalanceBean;
import norman.junk.service.TranService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static norman.junk.controller.MessagesConstants.NOT_FOUND_ERROR;
import static norman.junk.controller.MessagesConstants.OPTIMISTIC_LOCK_ERROR;
import static norman.junk.controller.MessagesConstants.RECONCILE_ERROR;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_RECONCILED;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_UPDATED;
import static norman.junk.controller.MessagesConstants.SUCCESSFULLY_UPLOADED_TRANS;
import static norman.junk.controller.MessagesConstants.UNEXPECTED_ERROR;
import static norman.junk.controller.MessagesConstants.UPLOADED_FILE_NOT_FOUND_ERROR;
import static norman.junk.controller.MessagesConstants.UPLOADED_FILE_READ_ERROR;

@Controller
public class AcctController {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private OfxParseService ofxParseService;
    @Autowired
    private TranService tranService;

    @RequestMapping("/acctList")
    public String loadList(Model model) {
        Iterable<Acct> accts = acctService.findAllAccts();
        model.addAttribute("accts", accts);
        return "acctList";
    }

    @RequestMapping("/acct")
    public String loadView(@RequestParam("acctId") Long acctId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Acct acct = acctService.findAcctById(acctId);
            model.addAttribute("acct", acct);
            AcctNbr currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
            model.addAttribute("currentAcctNbr", currentAcctNbr);
            List<TranBalanceBean> tranBalances = acctService.findTranBalancesByAcctId(acct.getId());
            model.addAttribute("tranBalances", tranBalances);
            return "acctView";
        } catch (JunkNotFoundException e) {
            String msg = String.format(NOT_FOUND_ERROR, "Acct", acctId);
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/acctList";
        }
    }

    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "acctId", required = false) Long acctId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no acct id, new acct.
        if (acctId == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        }
        // Otherwise, edit existing acct.
        try {
            Acct acct = acctService.findAcctById(acctId);
            AcctNbr currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
            AcctForm acctForm = new AcctForm(acct, currentAcctNbr);
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        } catch (JunkNotFoundException e) {
            String msg = String.format(NOT_FOUND_ERROR, "Acct", acctId);
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/acctList";
        }
    }

    @PostMapping("/acctEdit")
    public String processEdit(@Valid AcctForm acctForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctEdit";
        }
        // Convert form to entity ...
        Long acctId = acctForm.getId();
        Acct acct = acctForm.toAcct();
        // If this is an existing account and either the acctNumber or effective date changed, we need to save the
        // account number with the account.
        if (acctId != null && (!acctForm.getNumber().equals(acctForm.getOldNumber()) ||
                !acctForm.getEffDate().equals(acctForm.getOldEffDate()))) {
            AcctNbr acctNbr = acctForm.toAcctNbr();
            acctNbr.setAcct(acct);
            acct.getAcctNbrs().add(acctNbr);
        } else if (acctId == null) {
            // For new accounts, effective date of account number is beginning date of account.
            AcctNbr acctNbr = acctForm.toAcctNbr();
            acctNbr.setEffDate(acct.getBeginDate());
            acctNbr.setAcct(acct);
            acct.getAcctNbrs().add(acctNbr);
        }
        // ... and save.
        Acct save = null;
        try {
            save = acctService.saveAcct(acct);
        } catch (JunkOptimisticLockingException e) {
            // FIXME Unhandled JunkOptimisticLockingException for Acct
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-01: " + e.getMessage(), e);
        }
        String successMessage = String.format(SUCCESSFULLY_ADDED, "Acct", save.getId());
        if (acctId != null)
            successMessage = String.format(SUCCESSFULLY_UPDATED, "Acct", save.getId());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("acctId", save.getId());
        // If no data file id, we're done.
        if (acctForm.getDataFileId() == null)
            return "redirect:/acct?acctId={acctId}";
        // If we have a data file id, then we're trying to parse a data file and save it into a new or existing account
        // and into new transactions.
        //
        // Update the status of the data file to say we saved the account.
        Long dataFileId = acctForm.getDataFileId();
        DataFile dataFile = null;
        try {
            dataFile = dataFileService.findDataFileById(dataFileId);
        } catch (JunkNotFoundException e) {
            // FIXME Unhandled JunkNotFoundException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-02: " + e.getMessage(), e);
        }
        dataFile.setStatus(DataFileStatus.ACCT_SAVED);
        try {
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (JunkOptimisticLockingException e) {
            // FIXME Unhandled JunkOptimisticLockingException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-03: " + e.getMessage(), e);
        }
        // ************************************************************************************************************
        //
        // Parse the data file.
        OfxParseResponse response = null;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkOfxParseException e) {
            // FIXME Unhandled JunkOfxParseException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-04: " + e.getMessage(), e);
        }
        // The OFX file downloaded from the bank should never, ever have duplicate entries, but guess what!
        // Sometime it does! Now we have to guard against that.
        Map<String, OfxStmtTran> ofxStmtTranMap = new LinkedHashMap<>();
        for (OfxStmtTran ofxStmtTran : response.getOfxStmtTrans()) {
            String fitId = StringUtils.trimToNull(ofxStmtTran.getFitId());
            if (fitId != null)
                ofxStmtTranMap.put(fitId, ofxStmtTran);
        }
        int count = 0;
        for (OfxStmtTran ofxStmtTran : ofxStmtTranMap.values()) {
            List<Tran> trans = acctService.findTransByAcctIdAndFitId(acct.getId(), ofxStmtTran.getFitId());
            if (trans.size() > 1) {
                // FIXME Unhandled Bad Thing.
                throw new JunkInconceivableException(UNEXPECTED_ERROR + "-05");
            }
            if (trans.size() == 0 && (ofxStmtTran.getPostDate().equals(acct.getBeginDate()) ||
                    ofxStmtTran.getPostDate().after(acct.getBeginDate()))) {
                Tran tran = new Tran();
                tran.setType(ofxStmtTran.getType());
                tran.setPostDate(ofxStmtTran.getPostDate());
                tran.setUserDate(ofxStmtTran.getUserDate());
                tran.setAmount(ofxStmtTran.getAmount());
                tran.setFitId(StringUtils.trimToNull(ofxStmtTran.getFitId()));
                tran.setSic(StringUtils.trimToNull(ofxStmtTran.getSic()));
                tran.setCheckNumber(StringUtils.trimToNull(ofxStmtTran.getCheckNumber()));
                tran.setCorrectFitId(StringUtils.trimToNull(ofxStmtTran.getCorrectFitId()));
                tran.setCorrectAction(ofxStmtTran.getCorrectAction());
                tran.setName(StringUtils.trimToNull(ofxStmtTran.getName()));
                tran.setOfxCategory(StringUtils.trimToNull(ofxStmtTran.getCategory()));
                tran.setMemo(StringUtils.trimToNull(ofxStmtTran.getMemo()));
                tran.setReconciled(Boolean.FALSE);
                tran.setAcct(acct);
                acct.getTrans().add(tran);
                count++;
            }
        }
        try {
            acctService.saveAcct(acct);
        } catch (JunkOptimisticLockingException e) {
            // FIXME Unhandled JunkOptimisticLockingException for Acct
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-06: " + e.getMessage(), e);
        }
        dataFile.setStatus(DataFileStatus.TRAN_SAVED);
        try {
            dataFileService.saveDataFile(dataFile);
        } catch (JunkOptimisticLockingException e) {
            // FIXME Unhandled JunkOptimisticLockingException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-07: " + e.getMessage(), e);
        }
        successMessage = String.format(SUCCESSFULLY_UPLOADED_TRANS, count, acct.getId());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/";
    }

    @GetMapping("/acctUpload")
    public String loadUpload(@RequestParam(value = "dataFileId") Long dataFileId,
            @RequestParam(value = "acctId", required = false) Long acctId, Model model,
            RedirectAttributes redirectAttributes) {
        // A data file has been uploaded and the application could not determine which account it belongs to, so we
        // showed the user a page where he made that decision. Now we need to load the account edit page so the user can
        // save the account and continue to parse the data file.
        DataFile dataFile = null;
        try {
            dataFile = dataFileService.findDataFileById(dataFileId);
        } catch (JunkNotFoundException e) {
            // FIXME Unhandled JunkNotFoundException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-11: " + e.getMessage(), e);
        }
        // If data file will not parse, we gots an error.
        OfxParseResponse response = null;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkOfxParseException e) {
            // FIXME Unhandled JunkOfxParseException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-12: " + e.getMessage(), e);
        }
        // If no account id, new account.
        if (acctId == null) {
            AcctForm acctForm = new AcctForm(response);
            acctForm.setDataFileId(dataFile.getId());
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        }
        // Otherwise, edit existing acount.
        Acct acct = null;
        try {
            acct = acctService.findAcctById(acctId);
        } catch (JunkNotFoundException e) {
            // FIXME Unhandled JunkNotFoundException for Acct
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-13: " + e.getMessage(), e);
        }
        AcctNbr currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        AcctForm acctForm = new AcctForm(acct, currentAcctNbr);
        acctForm.setNumber(response.getOfxAcct().getAcctId());
        acctForm.setDataFileId(dataFile.getId());
        model.addAttribute("acctForm", acctForm);
        return "acctEdit";
    }

    @PostMapping("/dataFileUpload")
    public String processUpload(@RequestParam(value = "multipartFile") MultipartFile multipartFile, Model model,
            RedirectAttributes redirectAttributes) {
        // Part 1: Upload a file, read it, and save it in a data file database entity.
        if (multipartFile.isEmpty()) {
            // No need to log this. I press UPLOAD without selecting a file all the time.
            redirectAttributes.addFlashAttribute("errorMessage", UPLOADED_FILE_NOT_FOUND_ERROR);
            return "redirect:/";
        }
        // Try to upload the file and save it to the database.
        DataFile dataFile = new DataFile();
        dataFile.setOriginalFilename(multipartFile.getOriginalFilename());
        dataFile.setContentType(multipartFile.getContentType());
        dataFile.setSize(multipartFile.getSize());
        dataFile.setUploadTimestamp(new Date());
        dataFile.setStatus(DataFileStatus.UPLOADED);
        // Read the lines of the file.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            String line;
            int seq = 0;
            while ((line = reader.readLine()) != null) {
                DataLine dataLine = new DataLine();
                dataLine.setDataFile(dataFile);
                dataLine.setSeq(seq++);
                dataLine.setText(line);
                dataFile.getDataLines().add(dataLine);
            }
        } catch (IOException e) {
            String msg = String.format(UPLOADED_FILE_READ_ERROR, multipartFile.getOriginalFilename());
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/";
        }
        try {
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (JunkOptimisticLockingException e) {
            // FIXME Unhandled JunkOptimisticLockingException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-21: " + e.getMessage(), e);
        }
        //
        // Part 2: Parse the data file.
        OfxParseResponse response = null;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkOfxParseException e) {
            // FIXME Unhandled JunkOfxParseException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-22: " + e.getMessage(), e);
        }
        dataFile.setStatus(DataFileStatus.PARSED);
        try {
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (JunkOptimisticLockingException e) {
            // FIXME Unhandled JunkOptimisticLockingException for DataFile
            logger.error(UNEXPECTED_ERROR, e);
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-23: " + e.getMessage(), e);
        }
        //
        // Part 3: Save the parsed data.
        //
        // Does this account already exist? Do multiple accounts exist? Try to find out using financial institution id
        // (which identifies the bank) and the account id (which is the account number).
        //
        // Note: The code I've written is probably overkill. It allows for the case where there are two (or more)
        // account number records that all point to the same account record. I think that is a possible, but unlikely
        // scenario.
        String ofxFid = response.getOfxInst().getFid();
        String ofxAcctId = response.getOfxAcct().getAcctId();
        List<AcctNbr> acctNbrsByFidAndNumber = acctService.findAcctNbrsByFidAndNumber(ofxFid, ofxAcctId);
        Map<Long, Acct> acctMap = new HashMap<>();
        for (AcctNbr acctNbr : acctNbrsByFidAndNumber) {
            Acct acct = acctNbr.getAcct();
            acctMap.put(acct.getId(), acct);
        }
        // If we found exactly one account, save the new transactions.
        if (acctMap.size() == 1) {
            Acct acct = acctMap.values().iterator().next();
            // The OFX file downloaded from the bank should never, ever have duplicate entries, but guess what!
            // Sometime it does! Now we have to guard against that.
            Map<String, OfxStmtTran> ofxStmtTranMap = new LinkedHashMap<>();
            for (OfxStmtTran ofxStmtTran : response.getOfxStmtTrans()) {
                String fitId = StringUtils.trimToNull(ofxStmtTran.getFitId());
                if (fitId != null)
                    ofxStmtTranMap.put(fitId, ofxStmtTran);
            }
            int count = 0;
            for (OfxStmtTran ofxStmtTran : ofxStmtTranMap.values()) {
                List<Tran> trans = acctService.findTransByAcctIdAndFitId(acct.getId(), ofxStmtTran.getFitId());
                if (trans.size() > 1) {
                    // FIXME Unhandled Bad Thing.
                    throw new JunkInconceivableException(UNEXPECTED_ERROR + "-24");
                }
                if (trans.size() == 0 && (ofxStmtTran.getPostDate().equals(acct.getBeginDate()) ||
                        ofxStmtTran.getPostDate().after(acct.getBeginDate()))) {
                    Tran tran = new Tran();
                    tran.setType(ofxStmtTran.getType());
                    tran.setPostDate(ofxStmtTran.getPostDate());
                    tran.setUserDate(ofxStmtTran.getUserDate());
                    tran.setAmount(ofxStmtTran.getAmount());
                    tran.setFitId(StringUtils.trimToNull(ofxStmtTran.getFitId()));
                    tran.setSic(StringUtils.trimToNull(ofxStmtTran.getSic()));
                    tran.setCheckNumber(StringUtils.trimToNull(ofxStmtTran.getCheckNumber()));
                    tran.setCorrectFitId(StringUtils.trimToNull(ofxStmtTran.getCorrectFitId()));
                    tran.setCorrectAction(ofxStmtTran.getCorrectAction());
                    tran.setName(StringUtils.trimToNull(ofxStmtTran.getName()));
                    tran.setOfxCategory(StringUtils.trimToNull(ofxStmtTran.getCategory()));
                    tran.setMemo(StringUtils.trimToNull(ofxStmtTran.getMemo()));
                    tran.setReconciled(Boolean.FALSE);
                    tran.setAcct(acct);
                    acct.getTrans().add(tran);
                    count++;
                }
            }
            try {
                acctService.saveAcct(acct);
            } catch (JunkOptimisticLockingException e) {
                // FIXME Unhandled JunkOptimisticLockingException for Acct
                logger.error(UNEXPECTED_ERROR, e);
                throw new JunkInconceivableException(UNEXPECTED_ERROR + "-25: " + e.getMessage(), e);
            }
            dataFile.setStatus(DataFileStatus.TRAN_SAVED);
            try {
                dataFileService.saveDataFile(dataFile);
            } catch (JunkOptimisticLockingException e) {
                // FIXME Unhandled JunkOptimisticLockingException for DataFile
                logger.error(UNEXPECTED_ERROR, e);
                throw new JunkInconceivableException(UNEXPECTED_ERROR + "-26: " + e.getMessage(), e);
            }
            String successMessage = String.format(SUCCESSFULLY_UPLOADED_TRANS, count, acct.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/";
        }
        if (acctMap.size() > 1) {
            // If we found multiple accounts, things are really fucked up.
            // FIXME Unhandled Bad Thing.
            throw new JunkInconceivableException(UNEXPECTED_ERROR + "-27");
        }
        // Otherwise, we found no account number records. Try again using just the financial institution id.
        List<Acct> acctsByFid = acctService.findAcctsByFid(ofxFid);
        // If we found one or more accounts, we will need to go to an account disambiguation page to get input from the user.
        if (acctsByFid.size() > 0) {
            model.addAttribute("dataFileId", dataFile.getId());
            model.addAttribute("ofxInst", response.getOfxInst());
            model.addAttribute("ofxAcct", response.getOfxAcct());
            List<AcctNbr> acctNbrs = new ArrayList<>();
            for (Acct acct : acctsByFid) {
                AcctNbr currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
                acctNbrs.add(currentAcctNbr);
            }
            model.addAttribute("acctNbrs", acctNbrs);
            return "dataFileAcct";
        } else {
            // Otherwise, the user must create a new account.
            AcctForm acctForm = new AcctForm(response);
            acctForm.setDataFileId(dataFile.getId());
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        }
    }

    @GetMapping("/acctReconcile")
    public String loadReconcile(@RequestParam(value = "acctId") Long acctId, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Acct acct = acctService.findAcctById(acctId);
            AcctReconcileForm acctReconcileForm = new AcctReconcileForm();
            acctReconcileForm.setAcctId(acctId);
            acctReconcileForm.setAcctName(acct.getName());
            model.addAttribute("acctReconcileForm", acctReconcileForm);
            return "acctReconcile";
        } catch (JunkNotFoundException e) {
            String msg = String.format(NOT_FOUND_ERROR, "Acct", acctId);
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/acctList";
        }
    }

    @PostMapping("/acctReconcile")
    public String processReconcile(@Valid AcctReconcileForm acctReconcileForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctReconcile";
        }
        Long acctId = acctReconcileForm.getAcctId();
        try {
            Acct acct = acctService.findAcctById(acctId);
            BigDecimal balance = acct.getBeginBalance();
            List<Tran> trans = acct.getTrans();
            List<Tran> reconcileUs = new ArrayList<>();
            for (Tran tran : trans) {
                Date postDate = tran.getPostDate();
                Date reconcileDate = acctReconcileForm.getReconcileDate();
                if (postDate.equals(reconcileDate) || postDate.before(reconcileDate)) {
                    balance = balance.add(tran.getAmount());
                    reconcileUs.add(tran);
                }
            }
            BigDecimal reconcileAmount = acctReconcileForm.getReconcileAmount();
            if (balance.compareTo(reconcileAmount) != 0) {
                String errorMessage = String.format(RECONCILE_ERROR, balance.subtract(reconcileAmount));
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                redirectAttributes.addAttribute("acctId", acctId);
                return "redirect:/acct?acctId={acctId}";
            }
            for (Tran reconcileMe : reconcileUs) {
                reconcileMe.setReconciled(Boolean.TRUE);
            }
            Iterable<Tran> saveAll = tranService.saveAllTrans(reconcileUs);
            redirectAttributes.addFlashAttribute("successMessage", String.format(SUCCESSFULLY_RECONCILED, acctId));
            redirectAttributes.addAttribute("acctId", acctId);
            return "redirect:/acct?acctId={acctId}";
        } catch (JunkNotFoundException e) {
            String msg = String.format(NOT_FOUND_ERROR, "Account", acctId);
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/acctList";
        } catch (JunkOptimisticLockingException e) {
            String msg = String.format(OPTIMISTIC_LOCK_ERROR, "Account", acctId);
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            redirectAttributes.addAttribute("acctId", acctId);
            return "redirect:/acct?acctId={acctId}";
        }
    }
}