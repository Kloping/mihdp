package io.github.kloping.mihdp.game.v0;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.Bag;
import io.github.kloping.mihdp.dao.Characters;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.game.services.BaseService;
import io.github.kloping.mihdp.mapper.BagMaper;
import io.github.kloping.mihdp.mapper.CharactersMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.spt.RedisOperate;

import java.util.List;

/**
 * @author github.kloping
 */
@Controller
public class InfoController {

    @AutoStand
    JSONObject config;
    @AutoStand
    public RedisOperate<String> redisOperate0;
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;
    @AutoStand
    BagMaper bagMaper;
    @AutoStand
    CharactersMapper charactersMapper;

    @Before
    public void before(ReqDataPack dataPack) {
    }

    {
        BaseService.MSG2ACTION.put("信息", "info");
        BaseService.MSG2ACTION.put("当前信息", "info");
    }

    @Action("info")
    public Object info(ReqDataPack dataPack, GameClient client) {
        String sid = dataPack.getSender_id();
        User user = userMapper.selectById(sid);
        if (user == null) return "您当前仍未'注册';请先进行'注册;";
        else {
            Integer level = user.getLevel();
            JSONArray ar = config.getJSONArray("xp_list");
            Integer max;
            if (ar.size() > level) {
                max = ar.getInteger(level - 1);
            } else {
                max = 99999;
            }
            JSONObject jo = JSONObject.parseObject(JSON.toJSONString(user));
            jo.put("max", max);
            return jo.toString();
        }
    }

    {
        BaseService.MSG2ACTION.put("资源", "resource");
        BaseService.MSG2ACTION.put("我的资源", "resource");
    }

    @Action("resource")
    public Object resource(ReqDataPack dataPack) {
        String sid = dataPack.getSender_id();
        User user = userMapper.selectById(sid);
        if (user == null) return "您当前仍未'注册';请先进行'注册;";
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        List<Bag> bags = bagMaper.selectByUid(user.getUid());
        QueryWrapper<Characters> qw = new QueryWrapper<>();
        qw.eq("uid", user.getUid());
        List<Characters> characters = charactersMapper.selectList(qw);
        JSONObject data = JSON.parseObject(JSON.toJSONString(user));
        data.put("bags", JSONArray.parseArray(JSON.toJSONString(bags)));
        data.put("characters", JSONArray.parseArray(JSON.toJSONString(characters)));
        data.putAll(JSON.parseObject(JSON.toJSONString(resources)));
        return data;
    }

    {
        BaseService.MSG2ACTION.put("商城", "shopping");
        BaseService.MSG2ACTION.put("商店", "shopping");
        BaseService.MSG2ACTION.put("商场", "shopping");
    }

    @Action("shopping")
    public Object shopping(ReqDataPack pack) {
        JSONObject data = new JSONObject();
        data.put("data", config.getJSONArray("resources"));
        data.putAll(config.getJSONObject("shopping"));
        return data;
    }
}
