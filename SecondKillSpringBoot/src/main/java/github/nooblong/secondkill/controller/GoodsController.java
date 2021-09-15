package github.nooblong.secondkill.controller;

import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.IUserService;
import github.nooblong.secondkill.utils.CookieUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    IUserService userService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
//    @RequestMapping("/toList")
//    public String toList(HttpServletRequest request, HttpServletResponse response,
//                         Model model, @CookieValue(CookieUtil.TICKET_NAME) String ticket){
//        if (!StringUtils.hasText(ticket)){
//            return "login";
//        }
//        //通过session获取user
//        //User user = (User) session.getAttribute(ticket);
//        //通过redis获取user
//        User user = userService.getUserByCookie(ticket, request, response);
//        if (null == user){
//            return "login";
//        }
//        model.addAttribute("user", user);
//        return "goodsList";
//    }

    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsBo());
        //从redis获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasText(html)){
            return html;
        }
        //手动生成html
        WebContext webContext = new WebContext(request,response,request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);
        if (StringUtils.hasText(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(@PathVariable Long goodsId, Model model, User user,
                           HttpServletRequest request, HttpServletResponse response){
        //从redis获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goodsDetails:" + goodsId);
        if (StringUtils.hasText(html)){
            return html;
        }

        model.addAttribute("user", user);
        GoodsBo goodsBo = goodsService.findGoodsBoByGoodsId(goodsId);
        Date startDate = goodsBo.getStartDate();
        Date endDate = goodsBo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus;
        int remainSeconds;
        if (nowDate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int)(startDate.getTime()-nowDate.getTime())/1000;
        } else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("goods", goodsBo);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        //手动生成html
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (StringUtils.hasText(html)){
            valueOperations.set("goodsDetails:"+goodsBo.getId(), html, 60, TimeUnit.SECONDS);
        }
        return html;
    }
}
