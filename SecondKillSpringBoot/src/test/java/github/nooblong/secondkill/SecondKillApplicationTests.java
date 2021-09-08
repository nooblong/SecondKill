package github.nooblong.secondkill;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SecondKillApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
        //报错原因：User中没有对应的id字段
        System.out.println(userMapper.selectById(1));

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("nickname", "test");
        System.out.println(userMapper.selectOne(queryWrapper));
    }

}
