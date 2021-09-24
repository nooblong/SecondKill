package github.nooblong.secondkill;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import github.nooblong.secondkill.entity.SeckillGoods;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.OrderMapper;
import github.nooblong.secondkill.mapper.UserMapper;
import github.nooblong.secondkill.service.IGoodsService;
import github.nooblong.secondkill.service.ISeckillGoodsService;
import github.nooblong.secondkill.utils.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Date;
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

    @Autowired
    IGoodsService goodsService;
    @Test
    void testGoodsBo(){
        System.out.println(goodsService.findGoodsBo());
    }

    @Autowired
    ISeckillGoodsService seckillGoodsService;
    @Test
    void test(){
        GoodsBo goodsBo = new GoodsBo();
        goodsBo.setId(3L);
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goodsBo.getId()));
        System.out.println(seckillGoods);
    }
    @Autowired
    OrderMapper orderMapper;
    @Test
    void addOrder(){
        Order order = new Order();
        order.setUserId(18128966990L);
        order.setGoodsId(3L);
        order.setDeliveryAddressId(0L);
        order.setGoodsName("hsrnm");
        order.setGoodsCount(1);
        order.setGoodsPerPrice(BigDecimal.valueOf(6.5));
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
    }

    @Autowired
    UserUtil userUtil;

    @Test
    void addUser() throws Exception {
        userUtil.createUset(5000);
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void testlock(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
    }

}
