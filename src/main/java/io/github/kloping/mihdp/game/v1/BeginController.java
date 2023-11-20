package io.github.kloping.mihdp.game.v1;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.game.services.BaseService;
import io.github.kloping.mihdp.game.utils.NumberSelector;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;

/**
 * @author github.kloping
 */
@Controller
public class BeginController {

    {
        BaseService.MSG2ACTION.put("注册", "reg");
    }

    @Action("reg")
    public String reg(ReqDataPack pack, GameClient client) {
        NumberSelector.reg(pack.getSender_id())
                .set(1, d -> "当前是新手教程阶段\n1.继续 2.跳过").set(2, d -> {
                    NumberSelector.DATA_MAP.remove(pack.getSender_id());
                    NumberSelector.clear(pack.getSender_id());
                    return regNow(pack.getSender_id());
                })
                .next(1, d -> "嵌套教程 1.继续 2.跳过")
                .next(1, d -> "1.确定注册 2.取消注册")
                .next(2, d -> {
                    NumberSelector.clear(pack.getSender_id());
                    return "取消成功";
                }).set(1, d -> regNow(pack.getSender_id()));
        return "即将进入新手教程阶段\n完成后可快速入门和获得大量奖励\n1.确定 2.跳过";
    }

    private String regNow(String senderId) {
        NumberSelector.clear(senderId);
        return "注册成功!";
    }
}
