package io.github.kloping.mihdp.game.v.v0;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.map.MapUtils;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.ex.GeneralData.GeneralDataBuilder;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.p0.utils.NumberSelector;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.rand.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author github.kloping
 */
@Controller
public class BeginController {

    {
        BaseService.MSG2ACTION.put("注册", "reg");
    }

    @Action("reg")
    public Object reg(ReqDataPack pack, GameClient client) {
        if (userMapper.selectById(pack.getSender_id()) != null) return null;
        NumberSelector.reg(pack.getSender_id()).set(1, d -> {
            NumberSelector.DATA_MAP.remove(pack.getSender_id());
            NumberSelector.clear(pack.getSender_id());
            return regNow(pack.getSender_id());
        }).set(2, d -> {
            NumberSelector.DATA_MAP.remove(pack.getSender_id());
            NumberSelector.clear(pack.getSender_id());
            return regNow(pack.getSender_id());
        });
        return GeneralDataBuilder.create("新手教程开发中 回复此条消息附带1以完成注册")
                .append(new GeneralData.ResDataButton("确定", "1")).append(new GeneralData.ResDataButton("跳过", "2"))
                .build();
    }

    @AutoStand
    UserMapper userMapper;

    @AutoStand
    UsersResourcesMapper resourcesMapper;

    @AutoStand
    LanguageConfig languageConfig;

    public String regNow(String senderId) {
        User user = regNow0(senderId);
        return languageConfig.getString("RegistrationSuccessfulTips");
    }

    public User regNow0(String senderId) {
        try {
            User user = new User().setId(senderId);
            //获得最小uid
            String uid = userMapper.selectMaxUid();
            if (uid == null) {
                uid = "1000001";
            }
            //加随机
            Long tuid = Long.valueOf(uid);
            tuid = tuid + RandomUtils.RANDOM.nextInt(10) + 1;
            user.setUid(tuid.toString());
            //注册user
            userMapper.insert(user);
            //注册 resource
            UsersResources resources = new UsersResources();
            resources.setUid(tuid.toString());
            resourcesMapper.insert(resources);
            //清除选项
            NumberSelector.clear(senderId);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    {
        BaseService.MSG2ACTION.put("指令列表", "list-command");
    }

    private String COMMAND_ALL = null;
    private GeneralDataBuilder builder;

    @Action("list-command")
    public Object list(ReqDataPack pack, GameClient client) {
        builder = new GeneralDataBuilder();
        if (COMMAND_ALL == null) {
            StringBuilder sb0 = new StringBuilder();
            Map<String, List<String>> map = new HashMap<>();
            BaseService.MSG2ACTION.forEach((k, v) -> {
                MapUtils.append(map, v, k);
            });
            map.forEach((k, v) -> {
                sb0.append("- " + k).append("\n");
                for (String s : v) {
                    sb0.append("  - " + s).append("\n");
                    builder.append(new GeneralData.ResDataButton(s, s));
                }
            });
            COMMAND_ALL = sb0.toString();
            builder.append(COMMAND_ALL);
        }
        return builder.build();
    }

}
