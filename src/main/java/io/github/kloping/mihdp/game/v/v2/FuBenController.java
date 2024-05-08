package io.github.kloping.mihdp.game.v.v2;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.game.v.v1.CharactersController;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author github.kloping
 */
@Controller
public class FuBenController {
    @AutoStand
    BeginController beginController;
    @AutoStand
    UserMapper userMapper;
    @Autowired
    CharactersController charactersController;

    @Before
    public Character before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return charactersController.getCurrentCharacterOrLowestLevel(user.getUid());
    }


    {
        BaseService.MSG2ACTION.put("副本列表", "fb-list");
    }

    @Action("fb-list")
    public Object fbList(Character character, String qid) {
        return null;
    }

    {
        BaseService.MSG2ACTION.put("进入副本", "join-fb");
    }

    @Action("join-fb")
    public Object joinFb(Character character, String qid) {
        return null;
    }
}
