package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillGoods;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.OrderMapper;
import github.nooblong.secondkill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.service.ISeckillGoodsService;
import github.nooblong.secondkill.service.ISeckillOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    /**
     * 增加订单
     *
     * @param user
     * @param goodsBo
     * @return
     */
    @Override
    public synchronized Order addOrder(User user, GoodsBo goodsBo) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goodsBo.getId()));
        //减库存
        Integer stockCount = seckillGoods.getStockCount();
        log.info("read stockCount: " + stockCount);
        seckillGoods.setStockCount(stockCount - 1);
        seckillGoodsService.updateById(seckillGoods);
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
        return order;
    }
}
