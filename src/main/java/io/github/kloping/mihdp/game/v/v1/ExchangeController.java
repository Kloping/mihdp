package io.github.kloping.mihdp.game.v.v1;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.game.v.v0.InfoController;
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
    RedisSource redisSource;
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
        data.put("tips", out.getKey());
        data.put("t", out.getValue());
        data.putAll(infoController.getInfoData(pack, user));
        return data;
    }

    @Action("exchange-list")
    public Object elist(ReqDataPack pack, User user) throws Exception {
        return "\ntips,灵力每5分钟回复1点\n" +
                "兑换特殊币<n> #使用灵力+积分按照1:5的比例兑换为特殊币,上限2000\n" +
                "兑换打劫次数<n> #使用15灵力兑换1次打劫次数\n" +
                "兑换灵力<n> #使用1特殊币兑换1点灵力\n" +
                "兑换打工 #使用40灵力刷新一次打工\n";
    }

    public static final Map.Entry STATE0 = new AbstractMap.SimpleEntry<>("灵力不足!", false);
    public static final Map.Entry STATE1 = new AbstractMap.SimpleEntry<>("兑换成功!", true);
    public static final Map.Entry STATE2 = new AbstractMap.SimpleEntry<>("兑换上限!", true);

    {
        int fb = 5;
        EXCHANGE_MAP.put("特殊币", (p, u) -> {
            UsersResources resources = infoController.getAndCalculateE(u.getUid());
            if (resources.getGold() > 2000) return STATE2;
            Integer num = NumberUtils.getIntegerFromString(p.getContent(), 1);
            if (resources.getEnergy() < num) return STATE0;
            if (resources.getScore() < num * fb) return new AbstractMap.SimpleEntry<>("积分不足!", false);
            resources.setScore(resources.getScore() - (num * fb));
            resources.setEnergy(resources.getEnergy() - num);
            resources.applyE(redisSource);
            resources.setGold(resources.getGold() + num);
            usersResourcesMapper.updateById(resources);
            return new AbstractMap.SimpleEntry<>("兑换成功!当前特殊币:" + resources.getGold(), true);
        });
        EXCHANGE_MAP.put("打劫次数", (p, u) -> {
            UsersResources resources = infoController.getAndCalculateE(u.getUid());
            infoController.CalculateE(resources);
            Integer num = NumberUtils.getIntegerFromString(p.getContent(), 1);
            if (resources.getEnergy() < num * 15) return STATE0;
            resources.setEnergy(resources.getEnergy() - (num * 15));
            resources.setFz(resources.getFz() - num);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
            return STATE1;
        });
        EXCHANGE_MAP.put("打劫", EXCHANGE_MAP.get("打劫次数"));
        int wrm = 40;
        EXCHANGE_MAP.put("打工", (p, u) -> {
            UsersResources resources = infoController.getAndCalculateE(u.getUid());
            infoController.CalculateE(resources);
            if (resources.getEnergy() < wrm) return STATE0;
            resources.setEnergy(resources.getEnergy() - (wrm));
            resources.setK(0L);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
            return STATE1;
        });

        EXCHANGE_MAP.put("灵力", (p, u) -> {
            UsersResources resources = infoController.getAndCalculateE(u.getUid());
            Integer num = NumberUtils.getIntegerFromString(p.getContent(), 1);
            if (resources.getGold() < num)
                return new AbstractMap.SimpleEntry<>("你需要1点特殊币才能兑换1点灵力", false);
            resources.setGold(resources.getGold() - num);
            resources.setEnergy(resources.getEnergy() + num);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
            return STATE1;
        });
    }
}
