package io.github.kloping.mihdp.game.v.v1;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.s.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.mihdp.wss.data.ReqDataPack;

/**
 * @author github.kloping
 */
@Controller
public class ShopController {
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    CharacterMapper charactersMapper;
    @AutoStand
    BeginController beginController;
    @AutoStand
    GameStaticResourceLoader resourceLoader;
    @AutoStand
    RedisSource redisSource;

    @Before
    public User before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return user;
    }

    {
        BaseService.MSG2ACTION.put("商城", "shop");
        BaseService.MSG2ACTION.put("商店", "shop");
        BaseService.MSG2ACTION.put("商场", "shop");
    }

    private Integer index = 0, max = 10;
    private byte[] shopBytes = null;

    @Action("shop")
    public Object shop() {
        if (shopBytes == null || index++ % max == 0) {
            ImageDrawer drawer = ImageDrawer.createOnRandomBg();
            resourceLoader.ITEM_MAP.forEach((k, v) -> {

            });
        }
        return new GeneralData.ResDataImage(shopBytes);
    }
}
