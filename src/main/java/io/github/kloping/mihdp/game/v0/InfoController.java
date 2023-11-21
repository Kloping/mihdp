package io.github.kloping.mihdp.game.v0;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.game.services.BaseService;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;

/**
 * @author github.kloping
 */
@Controller
public class InfoController {
    @AutoStand
    UserMapper userMapper;

    @AutoStand
    JSONObject config;

    {
        BaseService.MSG2ACTION.put("信息", "info");
    }

    @Action("info")
    public Object info(ReqDataPack dataPack, GameClient client) {
        String sid = dataPack.getSender_id();
        User user = userMapper.selectById(sid);
        if (user == null) return "您当前仍未'注册';请先进行'注册;";
        else {
            Integer level = user.getLevel();
            Integer max = config.getJSONArray("xp_list").getInteger(level - 1);
            StringBuilder sb = new StringBuilder();
            sb.append("uid:").append(user.getUid()).append("\n")
                    .append("等级:").append(level).append("\n")
                    .append("经验:").append(user.getXp()).append("/").append(max);
            return sb.toString();
        }
    }
}
