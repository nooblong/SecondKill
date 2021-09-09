package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.UserMapper;
import github.nooblong.secondkill.service.IUserService;
import github.nooblong.secondkill.utils.MD5Util;
import github.nooblong.secondkill.vo.LoginVo;
import github.nooblong.secondkill.vo.RespBean;
import github.nooblong.secondkill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-06
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //用户名密码非空
        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //查询数据库
        User user = userMapper.selectById(mobile);
        if (null == user){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        log.info(user.toString());
        log.info(MD5Util.formPassToDBPass(password, user.getSlat()));
        //加密后比较密码
        if (!MD5Util.formPassToDBPass(password, user.getSlat()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        return RespBean.success();
    }
}
