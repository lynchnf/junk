package norman.junk.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import norman.junk.JunkException;
import norman.junk.domain.*;
import norman.junk.service.AcctService;
import norman.junk.service.DataFileService;
import norman.junk.util.ControllerUtils;
import norman.junk.util.OfxParseResponse;
import norman.junk.util.OfxParseUtils;

@Controller
public class DataFileController {
    private static final Logger logger = LoggerFactory.getLogger(DataFileController.class);
    @Autowired
    private AcctService acctService;
    @Autowired
    private DataFileService dataFileService;

    @PostMapping("/dataFileUpload")
    public String processUpload(@RequestParam(value = "multipartFile") MultipartFile multipartFile, Model model, RedirectAttributes redirectAttributes) {
        if (multipartFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Upload file is empty or missing.");
            return "redirect:/";
        }
        // Try to upload the file and saveDataFile it to the database.
        DataFile dataFile;
        try {
            dataFile = saveUploadedFile(multipartFile);
        } catch (JunkException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
        // Try to parse the uploaded database.
        OfxParseResponse response;
        try {
            response = OfxParseUtils.parseUploadedFile(dataFile);
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
            ControllerUtils.saveTrans(acct, dataFile, response, acctService, dataFileService, redirectAttributes);
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
            return ControllerUtils.newAcctFromDataFile(model, dataFile, response);
        }
    }

    @RequestMapping("/dataFile")
    public String loadView(@RequestParam("dataFileId") Long dataFileId, Model model, RedirectAttributes redirectAttributes) {
        Optional<DataFile> optional = dataFileService.findDataFileById(dataFileId);
        if (optional.isPresent()) {
            model.addAttribute("dataFile", optional.get());
            return "dataFileView";
        } else {
            String errorMessage = "Account not found, dataFileId=\"" + dataFileId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.warn(errorMessage);
            return "redirect:/";
        }
    }

    @RequestMapping("/dataFileList")
    public String loadView(Model model) {
        Iterable<DataFile> dataFiles = dataFileService.findAllDataFiles();
        model.addAttribute("dataFiles", dataFiles);
        return "dataFileList";
    }

    private DataFile saveUploadedFile(MultipartFile multipartFile) throws JunkException {
        DataFile dataFile = new DataFile();
        dataFile.setOriginalFilename(multipartFile.getOriginalFilename());
        dataFile.setContentType(multipartFile.getContentType());
        dataFile.setSize(multipartFile.getSize());
        dataFile.setUploadTimestamp(new Date());
        dataFile.setStatus(DataFileStatus.UPLOADED);
        dataFile = readLines(multipartFile, dataFile);
        return dataFileService.saveDataFile(dataFile);
    }

    private DataFile readLines(MultipartFile multipartFile, DataFile dataFile) throws JunkException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            String line;
            int seq = 0;
            while ((line = reader.readLine()) != null) {
                DataLine dataLine = new DataLine();
                dataLine.setDataFile(dataFile);
                dataLine.setSeq(seq++);
                dataLine.setText(line);
                dataFile.getDataLines().add(dataLine);
            }
            return dataFile;
        } catch (IOException e) {
            String msg = "Error while reading from uploaded file " + multipartFile.getOriginalFilename() + ".";
            logger.error(msg, e);
            throw new JunkException(msg, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    String msg = "Error ignored while closing input stream.";
                    logger.warn(msg, e);
                }
            }
        }
    }
}