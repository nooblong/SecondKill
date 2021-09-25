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
    BIND_ERROR(500212, "check format"),
    YOUR_ERROR(500213, "your error"),
    //sec kill
    EMPTY_STOCK(500500, "empty stock"),
    TOO_MUCH(500501, "buy too much"),
    WAIT(200001, "wait for result"),
    BAD_PATH(500502, "bad path"),
    BAD_CAPTCHA(500503, "bad captcha"),
    ACCESS_TOO_MUCH(500504, "too much access"),
    //order
    ORDER_NOT_EXIST(500600, "order not exist");

    private final Integer code;
    private final String message;

}
