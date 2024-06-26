package io.github.kloping.mihdp.game.v;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.p0.services.BaseService;

/**
 * @author github.kloping
 */
@Controller
public class TopInfoController {
    {
        BaseService.MSG2ACTION.put("更新日志", "update-log");
    }

    @Action("update-log")
    public Object log() {
        return new GeneralData.GeneralDataBuilder().append(
                        "独立式响应互动程序 开源于 github@Kloping/mihdp 正在复刻先前非独立程序并兼容数据" +
                                "\n更新日志:" +
                                "\n2024-6-20:指令'吸收';暂时测试开发 `副本列表`;`查看`面板更新" +
                                "\n2024-6-9 :修复'领取魂角'" +
                                "\n2024-6-5 :更新转让指令" +
                                "\n2024-6-4 :兑换列表更新,灵力修改5分钟,商城新增灵石" +
                                "\n2024-5-20:兑换列表更新" +
                                "\n2024-5-19:调整'打劫'积分,'副本列表'" +
                                "\n2024-4-17:'切换'自动,代码加注重构" +
                                "\n2024-4-11:修炼返回图片 而非文字" +
                                "\n2024-4-1 :修复修炼经验上限不升级的问题"
                )
                .append(new GeneralData.ResDataButton("吸收", "吸收"))
                .append(new GeneralData.ResDataButton("副本列表", "副本列表"))
                .append(new GeneralData.ResDataButton("当前队伍", "当前队伍"))
                .append(new GeneralData.ResDataButton("副本邀请", "副本邀请"))
                .build();
    }
}
