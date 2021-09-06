package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.UserMapper;
import github.nooblong.secondkill.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
