package github.nooblong.secondkill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.service.IUserService;
import github.nooblong.secondkill.utils.CookieUtil;
import github.nooblong.secondkill.vo.RespBean;
import github.nooblong.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 限制访问次数
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            //放入thread local
            User user = getUser(request, response);
            UserContext.setUser(user);
            //获取注解
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }

            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            //redis中user的key
            String key = request.getRequestURI();
            if (needLogin){
                if (user == null){
                    render(response, RespBeanEnum.LOGIN_ERROR);
                    return false;
                }
                key += ":" + user.getId();
            }

            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer)valueOperations.get(key);
            if (count == null){
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount){
                valueOperations.increment(key);
            }else {
                render(response, RespBeanEnum.ACCESS_TOO_MUCH);
                return false;
            }
        }
        return true;
    }

    /**
     * 构建返回对象
     * @param response
     * @param loginError
     */
    private void render(HttpServletResponse response, RespBeanEnum loginError) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        RespBean error = RespBean.error(loginError);
        writer.write(new ObjectMapper().writeValueAsString(error));
        writer.flush();
        writer.close();
    }

    /**
     * 获取登录用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String ticket = CookieUtil.getCookieValue(request, CookieUtil.TICKET_NAME);
        if (!StringUtils.hasText(ticket)) {
            return null;
        }
        return userService.getUserByCookie(ticket,request,response);
    }
}
