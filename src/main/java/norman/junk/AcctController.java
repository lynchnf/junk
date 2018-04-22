package norman.junk;

import java.util.List;
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

@Controller
public class AcctController {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    @Autowired
    private AcctRepository acctRepository;
    @Autowired
    private AcctNbrRepository acctNbrRepository;

    @RequestMapping("/acct")
    public String loadView(@RequestParam("acctId") Long acctId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Acct> optional = acctRepository.findById(acctId);
        if (optional.isPresent()) {
            Acct acct = optional.get();
            model.addAttribute("acct", acct);
            List<AcctNbr> acctNbrs = acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(acct.getId());
            if (!acctNbrs.isEmpty()) {
                AcctNbr currentAcctNbr = acctNbrs.iterator().next();
                model.addAttribute("currentAcctNbr", currentAcctNbr);
            }
            return "acctView";
        } else {
            String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            logger.warn(errorMessage);
            return "redirect:/";
        }
    }

    /*
     * @GetMapping("/acctDelete") public String processDelete(@RequestParam(value = "acctId") Long acctId, Model model,
     * RedirectAttributes redirectAttributes) { Optional<Acct> optional = acctRepository.findById(acctId); if
     * (optional.isPresent()) { Acct acct = optional.get(); try { acctRepository.delete(acct); String successMessage =
     * "Account successfully deleted, acctId=\"" + acctId + "\""; redirectAttributes.addFlashAttribute("successMessage",
     * successMessage); } catch (Exception e) { String errorMessage = "Account could not be deleted, acctId=\"" + acctId +
     * "\""; redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
     * logger.error(errorMessage, e); } } else { String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
     * redirectAttributes.addFlashAttribute("errorMessage", errorMessage); logger.warn(errorMessage); } return "redirect:/";
     * }
     */
    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "acctId", required = false) Long acctId, Model model, RedirectAttributes redirectAttributes) {
        if (acctId == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        } else {
            Optional<Acct> optionalAcct = acctRepository.findById(acctId);
            if (optionalAcct.isPresent()) {
                Acct acct = optionalAcct.get();
                // TODO Is there a more concise way to do this?
                AcctForm acctForm = new AcctForm();
                acctForm.setId(acct.getId());
                acctForm.setVersion(acct.getVersion());
                acctForm.setName(acct.getName());
                acctForm.setBeginDate(acct.getBeginDate());
                acctForm.setBeginBalance(acct.getBeginBalance());
                acctForm.setOrganization(acct.getOrganization());
                acctForm.setFid(acct.getFid());
                acctForm.setBankId(acct.getBankId());
                acctForm.setType(acct.getType());
                List<AcctNbr> acctNbrs = acctNbrRepository.findTopByAcct_IdOrderByEffDateDesc(acct.getId());
                if (!acctNbrs.isEmpty()) {
                    AcctNbr acctNbr = acctNbrs.iterator().next();
                    acctForm.setAcctNbr(acctNbr.getAcctNbr());
                    acctForm.setOldAcctNbr(acctNbr.getAcctNbr());
                    acctForm.setEffDate(acctNbr.getEffDate());
                    acctForm.setOldEffDate(acctNbr.getEffDate());
                }
                model.addAttribute("acctForm", acctForm);
                return "acctEdit";
            } else {
                String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.warn(errorMessage);
                return "redirect:/";
            }
        }
    }

    @PostMapping("/acctEdit")
    public String processEdit(@Valid AcctForm acctForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctEdit";
        }
        Long acctId = acctForm.getId();
        if (acctId == null) {
            Acct acct = new Acct();
            acct.setName(acctForm.getName());
            acct.setBeginDate(acctForm.getBeginDate());
            acct.setBeginBalance(acctForm.getBeginBalance());
            acct.setOrganization(acctForm.getOrganization());
            acct.setFid(acctForm.getFid());
            acct.setBankId(acctForm.getBankId());
            acct.setType(acctForm.getType());
            AcctNbr acctNbr = new AcctNbr();
            acctNbr.setAcctNbr(acctForm.getAcctNbr());
            acctNbr.setEffDate(acctForm.getBeginDate());
            acctNbr.setAcct(acct);
            acct.getAcctNbrs().add(acctNbr);
            Acct save;
            try {
                save = acctRepository.save(acct);
                String successMessage = "Account successfully added, acctId=\"" + save.getId() + "\"";
                redirectAttributes.addFlashAttribute("successMessage", successMessage);
                redirectAttributes.addAttribute("acctId", save.getId());
                return "redirect:/acct?acctId={acctId}";
            } catch (Exception e) {
                String errorMessage = "New account could not be added";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
                logger.error(errorMessage, e);
                return "redirect:/";
            }
        } else {
            Optional<Acct> optional = acctRepository.findById(acctId);
            if (optional.isPresent()) {
                Acct acct = new Acct();
                acct.setId(optional.get().getId());
                acct.setVersion(acctForm.getVersion());
                acct.setName(acctForm.getName());
                acct.setBeginDate(acctForm.getBeginDate());
                acct.setBeginBalance(acctForm.getBeginBalance());
                acct.setOrganization(acctForm.getOrganization());
                acct.setFid(acctForm.getFid());
                acct.setBankId(acctForm.getBankId());
                acct.setType(acctForm.getType());
                // If either the acctNumber or effective date changed, ..
                if (!acctForm.getAcctNbr().equals(acctForm.getOldAcctNbr()) || !acctForm.getEffDate().equals(acctForm.getOldEffDate())) {
                    AcctNbr acctNbr = new AcctNbr();
                    acctNbr.setAcctNbr(acctForm.getAcctNbr());
                    acctNbr.setEffDate(acctForm.getEffDate());
                    acctNbr.setAcct(acct);
                    acct.getAcctNbrs().add(acctNbr);
                }
                Acct save;
                try {
                    save = acctRepository.save(acct);
                    String successMessage = "Account successfully updated, acctId=\"" + save.getId() + "\"";
                    redirectAttributes.addFlashAttribute("successMessage", successMessage);
                    redirectAttributes.addAttribute("acctId", save.getId());
                } catch (Exception e) {
                    String errorMessage = "Account could not be updated, acctId=\"" + acctId + "\"";
                    redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ", error=\"" + e.getMessage() + "\"");
                    logger.error(errorMessage, e);
                    redirectAttributes.addAttribute("acctId", acctId);
                }
                return "redirect:/acct?acctId={acctId}";
            } else {
                String errorMessage = "Account not found, acctId=\"" + acctId + "\"";
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                logger.warn(errorMessage);
                return "redirect:/";
            }
        }
    }
}