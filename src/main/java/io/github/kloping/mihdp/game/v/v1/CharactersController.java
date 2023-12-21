package io.github.kloping.mihdp.game.v.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.mapper.CharactersMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.p0.utils.NumberSelector;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;

import java.util.List;

/**
 * @author github.kloping
 */
@Controller
public class CharactersController {
    @AutoStand(id = "defaultConfig")
    JSONObject defaultConfig;
    @AutoStand
    LanguageConfig lconfig;
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;
    @AutoStand
    CharactersMapper charactersMapper;
    @AutoStand
    BeginController beginController;

    private User getUser(ReqDataPack dataPack) {
        String sid = dataPack.getSender_id();
        return getUser(sid);
    }

    private User getUser(String sid) {
        User user = userMapper.selectById(sid);
        return user;
    }

    @Before
    public User before(ReqDataPack dataPack) {
        User user = getUser(dataPack);
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return user;
    }

    {
        BaseService.MSG2ACTION.put("魂角列表", "characters");
    }

    @Action("characters")
    public Object characters(ReqDataPack pack, User user) {
        QueryWrapper<Character> qw = new QueryWrapper<>();
        qw.eq("uid", user.getUid());
        List<Character> characters = charactersMapper.selectList(qw);
        if (characters.isEmpty()) return "未觉醒任何魂角";
        return JSON.toJSONString(characters);
    }

    {
        BaseService.MSG2ACTION.put("领取魂角", "greenhorn");
    }

    @Action("greenhorn")
    public Object greenhorn(ReqDataPack pack, User user) {
        QueryWrapper<Character> qw = new QueryWrapper<>();
        qw.eq("uid", user.getUid());
        List<Character> characters = charactersMapper.selectList(qw);
        if (characters.isEmpty()) {
            NumberSelector.reg(pack.getSender_id()).set(1, d -> {
                NumberSelector.clear(pack.getSender_id());
                Character c0 = new Character();
                c0.setUid(user.getUid())
                        .setLevel(1).setCid(1001).setMid(0).setHp(100);
                charactersMapper.insert(c0);
                return "领取成功;使用'魂角列表'查看";
            }).set(2, d -> {
                NumberSelector.clear(pack.getSender_id());
                NumberSelector.clear(pack.getSender_id());
                Character c0 = new Character();
                c0.setUid(user.getUid())
                        .setLevel(1).setCid(1002).setMid(0).setHp(100);
                charactersMapper.insert(c0);
                return "领取成功;使用'魂角列表'查看";
            });
            return "符合领取条件;\n请选择要领取的魂角\n1.落日神弓 2.神空剑";
        }
        return "不符合领取条件";
    }

    {
        BaseService.MSG2ACTION.put("查看", "show_character");
    }

    @Action("show_character")
    public Object showC0(ReqDataPack pack, User user) {
        GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
        String text = generalData.allText();

        return "查询失败!\n可能尚未拥有.";
    }
}
