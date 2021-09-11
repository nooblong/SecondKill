package github.nooblong.secondkill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.IOrderService;
import github.nooblong.secondkill.service.ISeckillOrderService;
import github.nooblong.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secKill")
public class SecKillController {

    @Autowired
    IGoodsService goodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IOrderService orderService;

    @RequestMapping("/doSecKill")
    public String doSecKill(Model model, User user, Long goodsId) {
        //判断是否登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsBo goodsBo = goodsService.findGoodsBoByGoodsId(goodsId);
        //判断是否剩余库存
        if (goodsBo.getGoodsStock() < 1) {
            model.addAttribute(model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage()));
            return "secKillFail";
        }
        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
//                .eq("user_id", user.getId())
//                .eq("goods_id", goodsId));
//        if (seckillOrder != null){
//            model.addAttribute("errmsg", RespBeanEnum.TOO_MUCH.getMessage());
//            return "secKillFail";
//        }
        Order order = orderService.addOrder(user, goodsBo);
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsBo);
        return "orderDetail";
    }

}
