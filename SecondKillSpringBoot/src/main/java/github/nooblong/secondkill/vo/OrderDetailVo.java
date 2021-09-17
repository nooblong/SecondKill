package github.nooblong.secondkill.vo;

import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private Order order;
    private GoodsBo goodsBo;
}
