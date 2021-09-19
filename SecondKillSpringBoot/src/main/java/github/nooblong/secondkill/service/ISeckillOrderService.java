package github.nooblong.secondkill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.nooblong.secondkill.entity.SeckillOrder;
import github.nooblong.secondkill.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId:success, 0:wait, -1:fail
     */
    Long getResult(User user, Long goodsId);
}
