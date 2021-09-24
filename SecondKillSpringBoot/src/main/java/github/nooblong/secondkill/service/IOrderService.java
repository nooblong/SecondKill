package github.nooblong.secondkill.service;

import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
public interface IOrderService extends IService<Order> {

    Order addOrder(User user, GoodsBo goodsBo);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
