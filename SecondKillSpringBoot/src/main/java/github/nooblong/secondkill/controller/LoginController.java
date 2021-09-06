package github.nooblong.secondkill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @ResponseBody
    @RequestMapping("/doLogin")
    public String doLogin(){
        return "doLogin";
    }

}
