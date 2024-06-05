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
                                "\n2024-6-5 :更新转让指令" +
                                "\n2024-6-4 :兑换列表更新,灵力修改5分钟,商城新增灵石" +
                                "\n2024-5-20:兑换列表更新" +
                                "\n2024-5-19:调整'打劫'积分,'副本列表'" +
                                "\n2024-4-17:'切换'自动,代码加注重构" +
                                "\n2024-4-11:修炼返回图片 而非文字" +
                                "\n2024-4-1 :修复修炼经验上限不升级的问题"
                )
                .append(new GeneralData.ResDataButton("兑换列表", "兑换列表"))
                .append(new GeneralData.ResDataButton("商城", "商城"))
                .append(new GeneralData.ResDataButton("转让", "转让"))
                .append(new GeneralData.ResDataButton("积分转让", "积分转让"))
                .build();
    }
}
