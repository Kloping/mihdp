package io.github.kloping.mihdp.game.v;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.Controller;
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
        return "独立式响应互动程序 开源于 https://github.com/Kloping/mihdp 正在复刻先前非独立程序并兼容数据" +
                "\n更新日志:2024-4-1,修复修炼经验上限不升级的问题";
    }
}
