package github.nooblong.secondkill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.vo.LoginVo;
import github.nooblong.secondkill.vo.RespBean;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-06
 */
public interface IUserService extends IService<User> {
    RespBean doLogin(LoginVo loginVo);
}
