package norman.junk.controller;

import java.util.Optional;

import javax.validation.Valid;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import norman.junk.JunkException;
import norman.junk.domain.Acct;
import norman.junk.domain.AcctNbr;
import norman.junk.domain.DataFile;
import norman.junk.domain.DataFileStatus;
import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;
import norman.junk.util.ControllerUtils;
import norman.junk.util.OfxParseResponse;
import norman.junk.util.OfxParseUtils;

@Controller
public class AcctController {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;

    @RequestMapping("/acct")
    public String loadView(@RequestParam("acctId") Long acctId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
        // If no account, we gots an error.
        if (acctNotFound(acctId, optionalAcct, redirectAttributes)) return "redirect:/";
        // Prepare to view account and current account number.
        Acct acct = optionalAcct.get();
        model.addAttribute("acct", acct);
        Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        if (optionalAcctNbr.isPresent()) {
            model.addAttribute("currentAcctNbr", optionalAcctNbr.get());
        }
        return "acctView";
    }

    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "acctId", required = false) Long acctId, Model model, RedirectAttributes redirectAttributes) {
        // If no acct id, new account.
        if (acctId == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        }
        // If no account, we gots an error.
        Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
        if (acctNotFound(acctId, optionalAcct, redirectAttributes)) return "redirect:/";
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
            // If no account, we gots an error.
            Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
            if (acctNotFound(acctId, optionalAcct, redirectAttributes)) return "redirect:/";
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
            if (acctId != null) successMessage = "Account successfully updated, acctId=\"" + save.getId() + "\"";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("acctId", save.getId());
        } catch (Exception e) {
            String errorMessage = "New account could not be added";
            if (acctId != null) errorMessage = "Account could not be updated, acctId=\"" + acctId + "\"";
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
        if (acctForm.getDataFileId() == null) return "redirect:/acct?acctId={acctId}";
        // If no data file, we gots an error.
        Long dataFileId = acctForm.getDataFileId();
        Optional<DataFile> optionalDataFile = dataFileService.findDataFileById(dataFileId);
        if (dataFileNotFound(acctId, redirectAttributes, optionalDataFile)) return "redirect:/";
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
            response = OfxParseUtils.parseUploadedFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
        // Save the new transactions.
        ControllerUtils.saveTrans(save, dataFile, response, acctService, dataFileService, redirectAttributes);
        return "redirect:/";
    }

    @GetMapping("/acctUpload")
    public String loadUpload(@RequestParam(value = "dataFileId") Long dataFileId, @RequestParam(value = "acctId", required = false) Long acctId, Model model, RedirectAttributes redirectAttributes) {
        // If no data file, we gots an error.
        Optional<DataFile> optionalDataFile = dataFileService.findDataFileById(dataFileId);
        if (dataFileNotFound(acctId, redirectAttributes, optionalDataFile)) return "redirect:/";
        // If data file will not parse, we gots an error.
        DataFile dataFile = optionalDataFile.get();
        OfxParseResponse response;
        try {
            response = OfxParseUtils.parseUploadedFile(dataFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
        // If no acct id, new account.
        if (acctId == null) return ControllerUtils.newAcctFromDataFile(model, dataFile, response);
        // If no account, we gots an error.
        Optional<Acct> optionalAcct = acctService.findAcctById(acctId);
        if (acctNotFound(acctId, optionalAcct, redirectAttributes)) return "redirect:/";
        // Prepare to edit account and current account number.
        Acct acct = optionalAcct.get();
        Optional<AcctNbr> optionalAcctNbr = acctService.findCurrentAcctNbrByAcctId(acct.getId());
        AcctForm acctForm = new AcctForm(acct, optionalAcctNbr.get());
        acctForm.setNumber(response.getOfxAcct().getAcctId());
        acctForm.setDataFileId(dataFile.getId());
        model.addAttribute("acctForm", acctForm);
        return "acctEdit";
    }

    private boolean acctNotFound(Long acctId, Optional<Acct> optionalAcct, RedirectAttributes redirectAttributes) {
        if (!optionalAcct.isPresent()) {
            String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return true;
        }
        return false;
    }

    private boolean dataFileNotFound(@RequestParam(value = "acctId", required = false) Long acctId, RedirectAttributes redirectAttributes, Optional<DataFile> optionalDataFile) {
        if (!optionalDataFile.isPresent()) {
            String errorMessage = "Data File not found, dataFileId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.error(errorMessage);
            return true;
        }
        return false;
    }
}