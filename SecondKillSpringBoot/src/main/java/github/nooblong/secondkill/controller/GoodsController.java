package github.nooblong.secondkill.controller;

import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    IUserService userService;

    @RequestMapping("/toList")
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, @CookieValue("userTicket") String ticket){
        if (!StringUtils.hasText(ticket)){
            return "login";
        }
        //通过session获取user
        //User user = (User) session.getAttribute(ticket);
        //通过redis获取user
        User user = userService.getUserByCookie(ticket, request, response);
        if (null == user){
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }

}
