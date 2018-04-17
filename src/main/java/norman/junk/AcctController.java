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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AcctController {
    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);
    @Autowired
    private AcctRepository acctRepository;

    @RequestMapping("/acct")
    public String loadView(@RequestParam("acctId") Long acctId, Model model) {
        Optional<Acct> optional = acctRepository.findById(acctId);
        if (optional.isPresent()) {
            model.addAttribute("acct", optional.get());
            return "acctView";
        } else {
            // TODO Display an error message on whatever page this was called from.
            return "acctNotFound";
        }
    }

    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "acctId", required = false) Long acctId, Model model) {
        if (acctId == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        } else {
            Optional<Acct> optional = acctRepository.findById(acctId);
            if (optional.isPresent()) {
                Acct acct = optional.get();
                // TODO Is there a more concise way to do this?
                AcctForm acctForm = new AcctForm();
                acctForm.setId(acct.getId());
                acctForm.setVersion(acct.getVersion());
                acctForm.setName(acct.getName());
                model.addAttribute("acctForm", acctForm);
                return "acctEdit";
            } else {
                // TODO Display an error message on whatever page this was called from.
                return "acctNotFound";
            }
        }
    }

    @PostMapping("/acctEdit")
    public String processEdit(@Valid AcctForm acctForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                logger.debug("error.objectName=\"" + error.getObjectName() + "\"");
                logger.debug("error.defaultMessage=\"" + error.getDefaultMessage() + "\"");
            }
            return "acctEdit";
        }
        // TODO Why does this server side validation blank out the name field?
        if (acctForm.getName() != null && acctForm.getName().equalsIgnoreCase("server")) {
            FieldError fieldError = new FieldError("acctForm", "name", "server side field error");
            bindingResult.addError(fieldError);
            ObjectError objectError = new ObjectError("acctForm", "server side global error");
            bindingResult.addError(objectError);
            return "acctEdit";
        }
        Long acctId = acctForm.getId();
        if (acctId == null) {
            Acct acct = new Acct();
            acct.setVersion(0);
            acct.setName(acctForm.getName());
            Acct save = acctRepository.save(acct);// TODO Insert needs to start after id=3.
            // TODO If save throws an error, we must display it.
            // TODO Otherwise, display a success message on whatever page this was called from.
            return "redirect:/acct?acctId=" + save.getId();
        } else {
            Optional<Acct> optional = acctRepository.findById(acctId);
            if (optional.isPresent()) {
                Acct acct = optional.get();
                acct.setVersion(acctForm.getVersion());
                acct.setName(acctForm.getName());
                Acct save = acctRepository.save(acct);// TODO Version does not work.
                // TODO If save throws an error, we must display it.
                // TODO Otherwise, display a success message on whatever page this was called from.
                return "redirect:/acct?acctId=" + save.getId();
            } else {
                // TODO Display an error message on whatever page this was called from.
                return "acctNotFound";
            }
        }
    }
}