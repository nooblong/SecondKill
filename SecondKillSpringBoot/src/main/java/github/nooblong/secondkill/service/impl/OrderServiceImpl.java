package github.nooblong.secondkill.service.impl;

import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.mapper.OrderMapper;
import github.nooblong.secondkill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
