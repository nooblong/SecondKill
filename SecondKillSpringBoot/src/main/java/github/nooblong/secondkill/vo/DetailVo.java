package github.nooblong.secondkill.vo;

import github.nooblong.secondkill.bo.GoodsBo;
import github.nooblong.secondkill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private User user;
    private GoodsBo goodsBo;
    private int secKillStatus;
    private int remainSeconds;

}
