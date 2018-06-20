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
import norman.junk.DatabaseException;
import norman.junk.JunkException;
import norman.junk.NotFoundException;
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

@Controller
public class AcctController {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    private static final String DATABASE_ERROR = "Unexpected Database Error.";
    private static final String DATA_FILE_NOT_FOUND = "DataFile not found.";
    private static final String ACCT_NOT_FOUND = "Acct not found.";
    private static final String ACCT_NBR_NOT_FOUND = "AcctNbr not found.";
    private static final String OFX_PARSE_ERROR = "OFX parse error.";
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private OfxParseService ofxParseService;
    @Autowired
    private TranService tranService;

    @RequestMapping("/acctList")
    public String loadList(Model model, RedirectAttributes redirectAttributes) {
        Iterable<Acct> accts;
        try {
            accts = acctService.findAllAccts();
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        model.addAttribute("accts", accts);
        return "acctList";
    }

    @RequestMapping("/acct")
    public String loadView(@RequestParam("acctId") Long acctId, Model model, RedirectAttributes redirectAttributes) {
        Acct acct;
        try {
            acct = acctService.findAcctById(acctId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("acct", acct);
        AcctNbr currentAcctNbr;
        try {
            currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NBR_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("currentAcctNbr", currentAcctNbr);
        List<TranBalanceBean> tranBalances = null;
        try {
            tranBalances = acctService.findTranBalancesByAcctId(acct.getId());
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
            return "redirect:/";
        }
        model.addAttribute("tranBalances", tranBalances);
        return "acctView";
    }

    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "acctId", required = false) Long acctId, Model model,
            RedirectAttributes redirectAttributes) {
        // If no acct id, new account.
        if (acctId == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        }
        // Otherwise, edit existing account.
        Acct acct;
        try {
            acct = acctService.findAcctById(acctId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
            return "redirect:/";
        }
        AcctNbr currentAcctNbr;
        try {
            currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NBR_NOT_FOUND);
            return "redirect:/";
        }
        AcctForm acctForm = new AcctForm(acct, currentAcctNbr);
        model.addAttribute("acctForm", acctForm);
        return "acctEdit";
    }

    @PostMapping("/acctEdit")
    public String processEdit(@Valid AcctForm acctForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctEdit";
        }
        Long acctId = acctForm.getId();
        Acct acct;
        // Verify existing account still exists.
        if (acctId != null) {
            try {
                acct = acctService.findAcctById(acctId);
            } catch (DatabaseException e) {
                redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
                return "redirect:/";
            } catch (NotFoundException e) {
                redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
                return "redirect:/";
            }
        }
        // Convert form to entity ...
        acct = acctForm.toAcct();
        // If this is an existing account and either the acctNumber or effective date changed, we need to save the account number with the account.
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
        // .. and save account.
        Acct save;
        try {
            save = acctService.saveAcct(acct);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        String successMessage = "Account successfully added, acctId=\"" + save.getId() + "\"";
        if (acctId != null)
            successMessage = "Account successfully updated, acctId=\"" + save.getId() + "\"";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("acctId", save.getId());
        // If no data file id, we're done.
        if (acctForm.getDataFileId() == null)
            return "redirect:/acct?acctId={acctId}";
        //
        // If we have a data file id, then we're trying to parse a data file and save it into a new or existing account and into new transactions.
        //
        // Verify existing data file still exists.
        Long dataFileId = acctForm.getDataFileId();
        DataFile dataFile;
        try {
            dataFile = dataFileService.findDataFileById(dataFileId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATA_FILE_NOT_FOUND);
            return "redirect:/";
        }
        // Update the status of the data file to say we saved the account.
        dataFile.setStatus(DataFileStatus.ACCT_SAVED);
        try {
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        // If data file will not parse, we gots an error.
        OfxParseResponse response;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", OFX_PARSE_ERROR);
            return "redirect:/";
        }
        // Save the new transactions.
        saveTrans(save, dataFile, response, acctService, dataFileService, redirectAttributes);
        return "redirect:/";
    }

    @GetMapping("/acctUpload")
    public String loadUpload(@RequestParam(value = "dataFileId") Long dataFileId,
            @RequestParam(value = "acctId", required = false) Long acctId, Model model,
            RedirectAttributes redirectAttributes) {
        // A data file has been uploaded and the application could not determine which account it belongs to, so we
        // showed the user a page where he made that decision. Now we need to load the account edit page so the user can
        // save the account and continue to parse the data file.
        DataFile dataFile;
        try {
            dataFile = dataFileService.findDataFileById(dataFileId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATA_FILE_NOT_FOUND);
            return "redirect:/";
        }
        // If data file will not parse, we gots an error.
        OfxParseResponse response;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", OFX_PARSE_ERROR);
            return "redirect:/";
        }
        // If no account id, new account.
        if (acctId == null) {
            AcctForm acctForm = new AcctForm(response);
            acctForm.setDataFileId(dataFile.getId());
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        }
        // Otherwise, edit existing acount.
        Acct acct;
        try {
            acct = acctService.findAcctById(acctId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
            return "redirect:/";
        }
        AcctNbr currentAcctNbr;
        try {
            currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NBR_NOT_FOUND);
            return "redirect:/";
        }
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
            redirectAttributes.addFlashAttribute("errorMessage", "Upload file is empty or missing.");
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
            String errorMessage = "Error while reading from uploaded file " + multipartFile.getOriginalFilename() + ".";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage, e);
            return "redirect:/";
        }
        try {
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        //
        // Part 2: Parse the data file.
        OfxParseResponse response;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
            dataFile.setStatus(DataFileStatus.PARSED);
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", OFX_PARSE_ERROR);
            return "redirect:/";
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
        List<AcctNbr> acctNbrsByFidAndNumber = null;
        try {
            acctNbrsByFidAndNumber = acctService.findAcctNbrsByFidAndNumber(ofxFid, ofxAcctId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        Map<Long, Acct> acctMap = new HashMap<>();
        for (AcctNbr acctNbr : acctNbrsByFidAndNumber) {
            Acct acct = acctNbr.getAcct();
            acctMap.put(acct.getId(), acct);
        }
        // If we found exactly one account, save the new transactions.
        if (acctMap.size() == 1) {
            Acct acct = acctMap.values().iterator().next();
            saveTrans(acct, dataFile, response, acctService, dataFileService, redirectAttributes);
            return "redirect:/";
        }
        if (acctMap.size() > 1) {
            // If we found multiple accounts, things are really fucked up.
            redirectAttributes.addFlashAttribute("errorMessage",
                    "UNEXPECTED ERROR: Multiple Account Records found for fid=\"" + ofxFid + "\", number=\"" +
                            ofxAcctId + "\"");
            return "redirect:/";
        }
        // Otherwise, we found no account number records. Try again using just the financial institution id.
        List<Acct> acctsByFid = null;
        try {
            acctsByFid = acctService.findAcctsByFid(ofxFid);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        }
        // If we found one or more accounts, we will need to go to an account disambiguation page to get input from the user.
        if (acctsByFid.size() > 0) {
            model.addAttribute("dataFileId", dataFile.getId());
            model.addAttribute("ofxInst", response.getOfxInst());
            model.addAttribute("ofxAcct", response.getOfxAcct());
            List<AcctNbr> acctNbrs = new ArrayList<>();
            for (Acct acct : acctsByFid) {
                AcctNbr currentAcctNbr;
                try {
                    currentAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
                } catch (DatabaseException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
                    return "redirect:/";
                } catch (NotFoundException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", ACCT_NBR_NOT_FOUND);
                    return "redirect:/";
                }
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
        Acct acct;
        try {
            acct = acctService.findAcctById(acctId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
            return "redirect:/";
        }
        AcctReconcileForm acctReconcileForm = new AcctReconcileForm();
        acctReconcileForm.setAcctId(acctId);
        acctReconcileForm.setAcctName(acct.getName());
        model.addAttribute("acctReconcileForm", acctReconcileForm);
        return "acctReconcile";
    }

    @PostMapping("/acctReconcile")
    public String processReconcile(@Valid AcctReconcileForm acctReconcileForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctReconcile";
        }
        Long acctId = acctReconcileForm.getAcctId();
        if (acctId == null) {
            String msg = "Account Id is null when reconciling an Account";
            redirectAttributes.addFlashAttribute("errorMessage", "UNEXPECTED ERROR: " + msg);
            logger.error(msg);
            return "redirect:/";
        }
        Acct acct;
        try {
            acct = acctService.findAcctById(acctId);
        } catch (DatabaseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", DATABASE_ERROR);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", ACCT_NOT_FOUND);
            return "redirect:/";
        }
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
            String errorMessage = String
                    .format("Account not able to reconciled, difference=%.2f", balance.subtract(reconcileAmount));
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            redirectAttributes.addAttribute("acctId", acctId);
            return "redirect:/acct?acctId={acctId}";
        }
        for (Tran reconcileMe : reconcileUs) {
            reconcileMe.setReconciled(Boolean.TRUE);
        }
        try {
            Iterable<Tran> saveAll = tranService.saveAllTrans(reconcileUs);
            String successMessage = "Account successfully reconciled, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "Account could not be reconciled, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
        }
        redirectAttributes.addAttribute("acctId", acctId);
        return "redirect:/acct?acctId={acctId}";
    }

    private void saveTrans(Acct acct, DataFile dataFile, OfxParseResponse response, AcctService acctService,
            DataFileService dataFileService, RedirectAttributes redirectAttributes) {
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
            List<Tran> trans = null;
            try {
                trans = acctService.findTransByAcctIdAndFitId(acct.getId(), ofxStmtTran.getFitId());
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            if (trans.size() > 1) {
                String errorMessage =
                        "UNEXPECTED ERROR: Multiple transactions found for acctId=\"" + acct.getId() + ", fitId=\"" +
                                ofxStmtTran.getFitId() + "\"";
                logger.error(errorMessage);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return;
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
            dataFile.setStatus(DataFileStatus.TRAN_SAVED);
            dataFileService.saveDataFile(dataFile);
            String successMessage =
                    "Account successfully updated with " + count + " transactions, acctId=\"" + acct.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "New transactions could not be added to account, acctId=\"" + acct.getId() + "\"";
            logger.error(errorMessage, e);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
    }
}