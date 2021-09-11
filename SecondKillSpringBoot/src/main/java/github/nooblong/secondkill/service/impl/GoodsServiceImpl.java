package github.nooblong.secondkill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Goods;
import github.nooblong.secondkill.mapper.GoodsMapper;
import github.nooblong.secondkill.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取商品列表
     * @return List
     */
    @Override
    public List<GoodsBo> findGoodsBo() {
        return goodsMapper.findGoodsBo();
    }
}
