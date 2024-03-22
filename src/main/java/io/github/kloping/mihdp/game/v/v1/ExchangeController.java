package io.github.kloping.mihdp.game.v.v1;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.game.s.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.game.v.v0.InfoController;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.number.NumberUtils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 兑换控制
 *
 * @author github.kloping
 */
@Controller
public class ExchangeController {
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    CharacterMapper charactersMapper;
    @AutoStand
    RedisSource redisSource;
    @AutoStand
    GameStaticResourceLoader resourceLoader;
    @AutoStand
    InfoController infoController;
    @AutoStand
    BeginController beginController;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;

    @Before
    public User before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return user;
    }

    {
        BaseService.MSG2ACTION.put("兑换", "exchange");
        BaseService.MSG2ACTION.put("兑换列表", "exchange-list");
    }


    public static final Integer MAX_GOLD = 2000;
    public static final Integer ENERGY_EVE_MIN = 6;

    public static final Map<String, Exchange> EXCHANGE_MAP = new HashMap<>();

    public interface Exchange {
        Map.Entry<String, Boolean> do0(ReqDataPack pack, User user);
    }

    @Action("exchange")
    public Object e(ReqDataPack pack, User user) throws Exception {
        Map.Entry<String, Boolean> out = null;
        for (String key : EXCHANGE_MAP.keySet()) {
            if (pack.getContent().contains(key)) {
                out = EXCHANGE_MAP.get(key).do0(pack, user);
            }
        }
        if (out == null) return "兑换失败!\n未发现相关物品";
        JSONObject data = new JSONObject();
        data.putAll(infoController.getInfoData(pack, user));
        data.put("tips", out.getKey());
        data.put("t", out.getValue());
        return data;
    }

    @Action("exchange-list")
    public Object elist(ReqDataPack pack, User user) throws Exception {
        return "兑换特殊币<n> #使用灵力+积分按照1:10的比例兑换为特殊币;tips,灵力每6分钟回复1点\n" +
                "兑换打劫次数<n> #使用15灵力兑换1次打劫次数\n" +
                "兑换打工 #使用40灵力刷新一次打工";
    }

    {
        EXCHANGE_MAP.put("特殊币", (p, u) -> {
            UsersResources resources = usersResourcesMapper.selectById(u.getUid());
            infoController.CalculateE(resources);
            Integer num = NumberUtils.getIntegerFromString(p.getContent(), 1);
            if (resources.getEnergy() < num) return new AbstractMap.SimpleEntry<>("灵力不足!", false);
            if (resources.getScore() < num * 10) return new AbstractMap.SimpleEntry<>("积分不足!", false);
            resources.setScore(resources.getScore() - (num * 10));
            resources.setEnergy(resources.getEnergy() - num);
            resources.applyE(redisSource);
            resources.setGold(resources.getGold() + num);
            usersResourcesMapper.updateById(resources);
            return new AbstractMap.SimpleEntry<>("兑换成功!当前特殊币:" + resources.getGold(), true);
        });
        EXCHANGE_MAP.put("打劫次数", (p, u) -> {
            UsersResources resources = usersResourcesMapper.selectById(u.getUid());
            infoController.CalculateE(resources);
            Integer num = NumberUtils.getIntegerFromString(p.getContent(), 1);
            if (resources.getEnergy() < num * 15) return new AbstractMap.SimpleEntry<>("灵力不足!", false);
            resources.setEnergy(resources.getEnergy() - (num * 15));
            resources.setFz(resources.getFz() - num);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
            return new AbstractMap.SimpleEntry<>("兑换成功!", true);
        });
        EXCHANGE_MAP.put("打劫", EXCHANGE_MAP.get("打劫次数"));
        int wrm = 40;
        EXCHANGE_MAP.put("打工", (p, u) -> {
            UsersResources resources = usersResourcesMapper.selectById(u.getUid());
            infoController.CalculateE(resources);
            if (resources.getEnergy() < wrm) return new AbstractMap.SimpleEntry<>("灵力不足!", false);
            resources.setEnergy(resources.getEnergy() - (wrm));
            resources.setK(0L);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
            return new AbstractMap.SimpleEntry<>("兑换成功!", true);
        });
    }
}
