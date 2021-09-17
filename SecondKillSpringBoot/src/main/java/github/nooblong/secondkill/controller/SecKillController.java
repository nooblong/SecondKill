package github.nooblong.secondkill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.IOrderService;
import github.nooblong.secondkill.service.ISeckillOrderService;
import github.nooblong.secondkill.vo.RespBean;
import github.nooblong.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/secKill")
public class SecKillController {

    @Autowired
    IGoodsService goodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IOrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;

    @ResponseBody
    @RequestMapping(value = "/doSecKill", method = RequestMethod.POST)
    public RespBean doSecKill(Model model, User user, Long goodsId) {
        //判断是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
//        model.addAttribute("user", user);
        GoodsBo goodsBo = goodsService.findGoodsBoByGoodsId(goodsId);
        //判断是否剩余库存
        if (goodsBo.getStockCount() < 1) {
            model.addAttribute(model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage()));
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
//                .eq("user_id", user.getId())
//                .eq("goods_id", goodsId));
        if (seckillOrder != null){
//            model.addAttribute("errmsg", RespBeanEnum.TOO_MUCH.getMessage());
            return RespBean.error(RespBeanEnum.TOO_MUCH);
        }
        Order order = orderService.addOrder(user, goodsBo);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsBo);
        return RespBean.success(order);
    }

}
