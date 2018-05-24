package norman.junk.controller;

import norman.junk.JunkException;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.DataFile;
import norman.junk.domain.DataFileStatus;
import norman.junk.domain.DataLine;
import norman.junk.domain.Tran;
import norman.junk.service.AcctService;
import norman.junk.service.AcctSummaryBean;
import norman.junk.service.DataFileService;
import norman.junk.service.OfxParseResponse;
import norman.junk.service.OfxParseService;
import norman.junk.service.OfxStmtTran;
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

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AcctController {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private OfxParseService ofxParseService;

    @RequestMapping("/acct")
    public String loadView(@RequestParam("acctId") Long acctId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
        // If no account, we gots an error.
        if (!optionalAcct.isPresent()) {
            String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to view account and current account number.
        Acct acct = optionalAcct.get();
        model.addAttribute("acct", acct);
        Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        if (optionalAcctNbr.isPresent()) {
            model.addAttribute("currentAcctNbr", optionalAcctNbr.get());
        }
        return "acctView";
    }

    @RequestMapping("/acctList")
    public String loadList(Model model) {
        List<AcctSummaryBean> acctSummaries = acctService.findAllAcctSummaries();
        model.addAttribute("acctSummaries", acctSummaries);
        return "acctList";
    }

    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "acctId", required = false) Long acctId, Model model, RedirectAttributes redirectAttributes) {
        // If no acct id, new account.
        if (acctId == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        }
        Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
        // If no account, we gots an error.
        if (!optionalAcct.isPresent()) {
            String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit account and current account number.
        Acct acct = optionalAcct.get();
        Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        AcctForm acctForm = new AcctForm(acct, optionalAcctNbr.get());
        model.addAttribute("acctForm", acctForm);
        return "acctEdit";
    }

    @PostMapping("/acctEdit")
    public String processEdit(@Valid AcctForm acctForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctEdit";
        }
        Long acctId = acctForm.getId();
        Acct acct;
        if (acctId != null) {
            Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
            // If no account, we gots an error.
            if (!optionalAcct.isPresent()) {
                String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.error(errorMessage);
                return "redirect:/";
            }
            // Prepare to saveDataFile existing account.
            acct = acctForm.toAcct();
            // If either the acctNumber or effective date changed, prepare to saveDataFile current account number.
            if (!acctForm.getNumber().equals(acctForm.getOldNumber()) || !acctForm.getEffDate().equals(acctForm.getOldEffDate())) {
                AcctNbr acctNbr = acctForm.toAcctNbr();
                acctNbr.setAcct(acct);
                acct.getAcctNbrs().add(acctNbr);
            }
        } else {
            // If no acct id, prepare to saveDataFile new account.
            acct = acctForm.toAcct();
            AcctNbr acctNbr = acctForm.toAcctNbr();
            // For new accounts, effective date of account number is beginning date of account.
            acctNbr.setEffDate(acct.getBeginDate());
            acctNbr.setAcct(acct);
            acct.getAcctNbrs().add(acctNbr);
        }
        // Try to saveDataFile account.
        Acct save;
        try {
            save = acctService.saveAcct(acct);
            String successMessage = "Account successfully added, acctId=\"" + save.getId() + "\"";
            if (acctId != null)
                successMessage = "Account successfully updated, acctId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("acctId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New account could not be added";
            if (acctId != null)
                errorMessage = "Account could not be updated, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            if (acctId == null) {
                return "redirect:/";
            } else {
                redirectAttributes.addAttribute("acctId", acctId);
                return "redirect:/acct?acctId={acctId}";
            }
        }
        // If no datafile id, we're done.
        if (acctForm.getDataFileId() == null)
            return "redirect:/acct?acctId={acctId}";
        Long dataFileId = acctForm.getDataFileId();
        Optional<DataFile> optionalDataFile = dataFileService.findDataFileById(dataFileId);
        // If no data file, we gots an error.
        if (!optionalDataFile.isPresent()) {
            String errorMessage = "Data File not found, dataFileId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Update the status of the data file to say we saved the account.
        DataFile dataFile = optionalDataFile.get();
        dataFile.setStatus(DataFileStatus.ACCT_SAVED);
        try {
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (Exception e) {
            String errorMessage = "Data file could not be updated, dataFileId=\"" + dataFileId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
            logger.error(errorMessage, e);
            return "redirect:/";
        }
        // If data file will not parse, we gots an error.
        OfxParseResponse response;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
        // Save the new transactions.
        saveTrans(save, dataFile, response, acctService, dataFileService, redirectAttributes);
        return "redirect:/";
    }

    @GetMapping("/acctUpload")
    public String loadUpload(@RequestParam(value = "dataFileId") Long dataFileId, @RequestParam(value = "acctId", required = false) Long acctId, Model model, RedirectAttributes redirectAttributes) {
        Optional<DataFile> optionalDataFile = dataFileService.findDataFileById(dataFileId);
        // If no data file, we gots an error.
        if (!optionalDataFile.isPresent()) {
            String errorMessage = "Data File not found, dataFileId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // If data file will not parse, we gots an error.
        DataFile dataFile = optionalDataFile.get();
        OfxParseResponse response;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
        // If no acct id, new account.
        if (acctId == null) {
            AcctForm acctForm = new AcctForm(response);
            acctForm.setDataFileId(dataFile.getId());
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        }
        Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
        // If no account, we gots an error.
        if (!optionalAcct.isPresent()) {
            String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return "redirect:/";
        }
        // Prepare to edit account and current account number.
        Acct acct = optionalAcct.get();
        Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        AcctForm acctForm = new AcctForm(acct, optionalAcctNbr.get());
        acctForm.setNumber(response.getOfxAcct().getAcctId());
        acctForm.setDataFileId(dataFile.getId());
        model.addAttribute("acctForm", acctForm);
        return "acctEdit";
    }

    @PostMapping("/dataFileUpload")
    public String processUpload(@RequestParam(value = "multipartFile") MultipartFile multipartFile, Model model, RedirectAttributes redirectAttributes) {
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
        dataFile = dataFileService.saveDataFile(dataFile);
        // Try to parse the uploaded database.
        OfxParseResponse response;
        try {
            response = ofxParseService.parseUploadedFile(dataFile);
            dataFile.setStatus(DataFileStatus.PARSED);
            dataFile = dataFileService.saveDataFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
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
        // If we found exactly one account, saveDataFile the new transactions.
        if (acctMap.size() == 1) {
            Acct acct = acctMap.values().iterator().next();
            saveTrans(acct, dataFile, response, acctService, dataFileService, redirectAttributes);
            return "redirect:/";
        }
        if (acctMap.size() > 1) {
            // If we found multiple accounts, things are really fucked up.
            redirectAttributes.addFlashAttribute("errorMessage", "UNEXPECTED ERROR: Multiple Account Records found for fid=\"" + ofxFid + "\", number=\"" + ofxAcctId + "\"");
            return "redirect:/";
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
                Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
                acctNbrs.add(optionalAcctNbr.get());
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

    private void saveTrans(Acct acct, DataFile dataFile, OfxParseResponse response, AcctService acctService, DataFileService dataFileService, RedirectAttributes redirectAttributes) {
        int count = 0;
        for (OfxStmtTran ofxStmtTran : response.getOfxStmtTrans()) {
            List<Tran> trans = acctService.findTransByAcctIdAndFitId(acct.getId(), ofxStmtTran.getFitId());
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
            acctService.saveAcct(acct);
            dataFile.setStatus(DataFileStatus.TRAN_SAVED);
            dataFileService.saveDataFile(dataFile);
            String successMessage = "Account successfully updated with " + count + " transactions, acctId=\"" + acct.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "New transactions could not be added to account, acctId=\"" + acct.getId() + "\"";
            logger.error(errorMessage, e);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
    }
}