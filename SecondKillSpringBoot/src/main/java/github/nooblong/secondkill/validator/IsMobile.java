package github.nooblong.secondkill.validator;

import org.springframework.util.StringUtils;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {

    boolean required() default true;

    String message() default "check mobile format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return validateMobile(value);
        } else {
            if (!StringUtils.hasText(value)) {
                return true;
            } else {
                return validateMobile(value);
            }
        }
    }

    public static boolean validateMobile(String in) {
//        Pattern pattern = Pattern.compile("^[1]\\d{10}$");
//        return pattern.matcher(in).matches();
        return true;
    }
}
