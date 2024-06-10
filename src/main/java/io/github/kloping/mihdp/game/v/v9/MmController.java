package io.github.kloping.mihdp.game.v.v9;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.wss.data.ReqDataPack;

/**
 * @author github.kloping
 */
@Controller
public class MmController {
    @AutoStand(id = "defaultConfig")
    JSONObject defaultConfig;
    @AutoStand
    LanguageConfig lconfig;
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;
    @AutoStand
    CharacterMapper charactersMapper;
    @AutoStand
    BeginController beginController;
    @AutoStand
    RedisSource redisSource;

    private User getUser(ReqDataPack dataPack) {
        String sid = dataPack.getSender_id();
        return getUser(sid);
    }

    public User getUser(String sid) {
        User user = userMapper.selectById(sid);
        if (user == null) {
            if (sid.startsWith("@")) return getUserByUid(sid.substring(1));
            else return getUserByUid(sid);
        }
        return user;
    }

    private User getUserByUid(String uid) {
        QueryWrapper<User> qw0 = new QueryWrapper<>();
        qw0.eq("uid", uid);
        User user = userMapper.selectOne(qw0);
        return user;
    }

    @Before
    public User before(ReqDataPack dataPack) {
        User user = getUser(dataPack);
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return user;
    }

}
