package github.nooblong.secondkill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.vo.LoginVo;
import github.nooblong.secondkill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-06
 */
public interface IUserService extends IService<User> {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
}
