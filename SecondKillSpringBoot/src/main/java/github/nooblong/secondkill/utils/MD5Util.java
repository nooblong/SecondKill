package github.nooblong.secondkill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "salt";

    public static String inputPassToFromPass(String inputPass) {
        String str = inputPass + salt;
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = formPass + salt;
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String formPass = inputPassToFromPass(inputPass);
        return formPassToDBPass(formPass, salt);
    }

    @Test
    void testThis() {
//        207acd61a3c1bd506d7e9a4535359f8a
        System.out.println(inputPassToFromPass("123456"));
//        f58c31b62adc4ca1ce04247943f394fb
        System.out.println(
                formPassToDBPass("207acd61a3c1bd506d7e9a4535359f8a",
                        salt));
        System.out.println(inputPassToDBPass("123456", salt));
    }

}
