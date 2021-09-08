package github.nooblong.secondkill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    SUCCESS(200, "SUCCESS"),
    ERROR(500,"服务器异常"),
    LOGIN_ERROR(500210, "用户名或密码错误");


    private final Integer code;
    private final String message;

}
