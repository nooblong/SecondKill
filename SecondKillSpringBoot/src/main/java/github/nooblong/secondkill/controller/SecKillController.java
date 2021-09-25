package github.nooblong.secondkill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.ArithmeticCaptcha;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.bo.SeckillMessage;
import github.nooblong.secondkill.config.AccessLimit;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.exception.GlobalException;
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
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    RedisScript<Long> redisScript;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    @ResponseBody
    @RequestMapping(value = "/{path}/doSecKill", method = RequestMethod.POST)
    public RespBean doSecKill(Model model, User user, Long goodsId, @PathVariable String path) {
        //判断是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //判断路径
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.BAD_PATH);
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
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.TOO_MUCH);
        }
        //内存标记减少redis访问
        if (emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.TOO_MUCH);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //预减库存
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(redisScript,
                Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock < 0) {
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
     *
     * @param user
     * @param goodsId
     * @return orderId:success, 0:wait, -1:fail
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    @AccessLimit(second=100,maxCount=100)
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        //校验验证码
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.BAD_CAPTCHA);
        }
        //生成路径
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //设置图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setDateHeader("Expires", 0);
        //放入redis
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId,
                arithmeticCaptcha.text(), 300, TimeUnit.SECONDS);
        try {
            arithmeticCaptcha.out(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化库存到redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsBo> list = goodsService.findGoodsBo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsBo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsBo.getId(), goodsBo.getStockCount());
            emptyStockMap.put(goodsBo.getId(), false);
        });
    }
}
