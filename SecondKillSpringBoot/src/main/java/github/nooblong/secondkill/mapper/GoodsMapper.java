package github.nooblong.secondkill.mapper;

import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return 列表
     */
    List<GoodsBo> findGoodsBo();

    /**
     * 获取单个商品
     * @return Goods
     */
    GoodsBo findGoodsBoByGoodsId(Long goodsId);
}
