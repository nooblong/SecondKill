package github.nooblong.secondkill.service;

import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nooblong
 * @since 2021-09-10
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsBo> findGoodsBo();

    GoodsBo findGoodsBoByGoodsId(Long goodsId);
}
