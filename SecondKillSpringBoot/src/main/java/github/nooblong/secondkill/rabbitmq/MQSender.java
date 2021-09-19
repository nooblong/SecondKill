package github.nooblong.secondkill.rabbitmq;

import github.nooblong.secondkill.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     * @param msg
     */
    public void sendSeckillMessage(String msg){
        log.info("send: " + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE,"seckill.message", msg);
    }

}
