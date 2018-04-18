package norman.junk;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class RequiredFieldValidator implements ConstraintValidator<RequiredField, String> {
    @Override
    public void initialize(RequiredField constraintAnnotation) {}

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(s);
    }
}