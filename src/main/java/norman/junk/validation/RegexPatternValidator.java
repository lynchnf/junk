package norman.junk.validation;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class RegexPatternValidator implements ConstraintValidator<RegexPattern, String> {
    @Override
    public void initialize(RegexPattern constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(s))
            return true;
        try {
            Pattern pattern = Pattern.compile(s);
            return true;
        } catch (Exception PatternSyntaxException) {
            return false;
        }
    }
}