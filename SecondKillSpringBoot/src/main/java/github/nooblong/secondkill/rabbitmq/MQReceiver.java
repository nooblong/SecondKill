package github.nooblong.secondkill.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.bo.SeckillMessage;
import github.nooblong.secondkill.config.RabbitMQConfig;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IOrderService orderService;

    /**
     * 接受消息，下单
     * @param msg
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String msg){
        log.info("receive: " + msg);
        SeckillMessage seckillMessage;
        try {
            seckillMessage = objectMapper.readValue(msg.getBytes(), SeckillMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //获取商品对象
        GoodsBo goodsBo = goodsService.findGoodsBoByGoodsId(goodsId);
        //判断库存
        if (goodsBo.getStockCount() < 1){
            return;
        }
        //判断重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return;
        }
        //下单
        orderService.addOrder(user, goodsBo);

    }

}
