package norman.junk;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = RequiredFieldValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredField {
    String message() default "Required field.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}