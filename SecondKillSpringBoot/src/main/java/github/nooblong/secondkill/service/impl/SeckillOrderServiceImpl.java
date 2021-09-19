package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.SeckillOrderMapper;
import github.nooblong.secondkill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    SeckillOrderMapper seckillOrderMapper;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId:success, 0:wait, -1:fail
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (null != seckillOrder){
            return seckillOrder.getOrderId();
        } else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        } else {
            return 0L;
        }
    }
}
