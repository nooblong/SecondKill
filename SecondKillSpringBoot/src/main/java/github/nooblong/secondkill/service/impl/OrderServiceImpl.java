package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillGoods;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.exception.GlobalException;
import github.nooblong.secondkill.mapper.OrderMapper;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.service.ISeckillGoodsService;
import github.nooblong.secondkill.service.ISeckillOrderService;
import github.nooblong.secondkill.utils.MD5Util;
import github.nooblong.secondkill.utils.UUIDUtil;
import github.nooblong.secondkill.vo.OrderDetailVo;
import github.nooblong.secondkill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    ISeckillGoodsService seckillGoodsService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 增加订单
     *
     * @param user
     * @param goodsBo
     * @return
     */
    @Override
    public Order addOrder(User user, GoodsBo goodsBo) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goodsBo.getId()));
        //减库存
//        Integer stockCount = seckillGoods.getStockCount();
//        log.info("read stockCount: " + stockCount);
//        seckillGoods.setStockCount(stockCount - 1);
        boolean seckillResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql(
                        "stock_count = stock_count-1").eq("goods_id", goodsBo.getId())
                .gt("stock_count", 0));
        if (!seckillResult) {
            //判断是否还有库存
            redisTemplate.opsForValue().set("isStockEmpty:" + goodsBo.getId(), "0");
            return null;
        }
//        seckillGoodsService.updateById(seckillGoods);
        //生成新订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsBo.getId());
        order.setDeliveryAddressId(0L);
        order.setGoodsName(goodsBo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPerPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsBo.getId());
        seckillOrderService.save(seckillOrder);
        //存入redis
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsBo.getId(), seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsBo goodsBoByGoodsId = goodsService.findGoodsBoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsBo(goodsBoByGoodsId);
        return orderDetailVo;
    }

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String s = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId,
                s, 60, TimeUnit.SECONDS);
        return s;
    }

    /**
     * 校验秒杀地址
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     * 校验验证码
     *
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (!StringUtils.hasText(captcha) || user == null || goodsId < 0){
            return false;
        }
        String s = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(s);
    }
}
