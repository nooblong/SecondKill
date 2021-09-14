package github.nooblong.secondkill.controller;


import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.vo.RespBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nooblong
 * @since 2021-09-06
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 查看用户信息
     * @param user auto
     * @return user
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
