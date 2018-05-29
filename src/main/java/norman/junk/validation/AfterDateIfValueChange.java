package norman.junk.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = AfterDateIfValueChangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterDateIfValueChange {
    String message() default "If string changed, new date must be after old date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String newDate();

    String oldDate();

    String newString();

    String oldString();
}