package github.nooblong.secondkill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.bo.SeckillMessage;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.rabbitmq.MQSender;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.IOrderService;
import github.nooblong.secondkill.service.ISeckillOrderService;
import github.nooblong.secondkill.vo.RespBean;
import github.nooblong.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/secKill")
public class SecKillController implements InitializingBean {

    @Autowired
    IGoodsService goodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IOrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MQSender mqSender;
    @Autowired
    ObjectMapper objectMapper;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    @ResponseBody
    @RequestMapping(value = "/doSecKill", method = RequestMethod.POST)
    public RespBean doSecKill(Model model, User user, Long goodsId) {
        //判断是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

//        //判断是否剩余库存
//        if (goodsBo.getStockCount() < 1) {
//            model.addAttribute(model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage()));
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
////        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
////                .eq("user_id", user.getId())
////                .eq("goods_id", goodsId));
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.TOO_MUCH);
        }
        //内存标记减少redis访问
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.TOO_MUCH);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0){
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //发送消息
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        try {
            mqSender.sendSeckillMessage(objectMapper.writeValueAsString(seckillMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return RespBean.error(RespBeanEnum.WAIT);
    }

    /**
     * 查询秒杀结果
     * @param user
     * @param goodsId
     * @return orderId:success, 0:wait, -1:fail
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 初始化库存到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsBo> list = goodsService.findGoodsBo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsBo -> {
                redisTemplate.opsForValue().set("seckillGoods:" + goodsBo.getId(), goodsBo.getStockCount());
                emptyStockMap.put(goodsBo.getId(), false);
        });
    }
}
