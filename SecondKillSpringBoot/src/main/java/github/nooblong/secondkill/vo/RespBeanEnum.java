package github.nooblong.secondkill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    //global
    SUCCESS(200, "success"),
    ERROR(500,"server error"),
    //login
    LOGIN_ERROR(500210, "check username"),
    PASSWORD_ERROR(500211, "check password"),
    BIND_ERROR(500212, "check format");


    private final Integer code;
    private final String message;

}
