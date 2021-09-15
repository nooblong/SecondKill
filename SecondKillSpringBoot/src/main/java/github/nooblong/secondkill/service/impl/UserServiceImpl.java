package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.exception.GlobalException;
import github.nooblong.secondkill.mapper.UserMapper;
import github.nooblong.secondkill.service.IUserService;
import github.nooblong.secondkill.utils.CookieUtil;
import github.nooblong.secondkill.utils.MD5Util;
import github.nooblong.secondkill.utils.UUIDUtil;
import github.nooblong.secondkill.vo.LoginVo;
import github.nooblong.secondkill.vo.RespBean;
import github.nooblong.secondkill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务实现类
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
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //用户名密码非空
        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)) {
            throw new GlobalException(RespBeanEnum.ERROR);
        }
        //查询数据库
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        log.info(user.toString());
        log.info(MD5Util.formPassToDBPass(password, user.getSlat()));
        //加密后比较密码
        if (!MD5Util.formPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.PASSWORD_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        //存入redis 格式：( key: user:<cookie> / value: User(Object Serializable) )
        redisTemplate.opsForValue().set("user:" + ticket, user);
//        request.getSession().setAttribute(ticket, user);
        //放入cookie
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据ticket从redis获取用户, 并放入cookie
     *
     * @param userTicket ticket
     * @return 用户
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasText(userTicket)) {
            return null;
        }
        //根据cookie获取user对象
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        //放入cookie
        if (user != null) {
            CookieUtil.setCookie(request, response, CookieUtil.TICKET_NAME, userTicket);
        }
        return user;
    }

    /**
     * 更新密码
     * @param userTicket
     * @param id
     * @param password
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, Long id, String password,
                                   HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int result = userMapper.updateById(user);
        if (1 == result){
            //更新成功，删除redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.YOUR_ERROR);
    }
}
