package github.nooblong.secondkill.bo;

import github.nooblong.secondkill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeckillMessage {

    private User user;

    private Long goodsId;

}
