package io.github.kloping.mihdp.game.v.v1;

import com.alibaba.fastjson.JSON;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.s.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
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
    public static final Integer MAX_SHOP = 50;
    private byte[] shopBytes = null;

    @Action("shop")
    public Object shop(ReqDataPack pack) throws Exception {
        if (pack.isArgValue("draw", true)) {
            if (shopBytes == null || index++ % max == 0) {
                ImageDrawer drawer = ImageDrawer.createOnBg(2);
                drawer.size(1030, 1225);
                int x = 5, y = 5;
                for (Integer id : resourceLoader.ITEM_MAP.keySet()) {
                    String key = String.format("buy-%s.%s", pack.getSender_id(), id);
                    Integer v0 = redisSource.id2shopMax.getValue(key);
                    if (v0 == null) {
                        v0 = 0;
                    }
                    Item item = resourceLoader.ITEM_MAP.get(id);
                    drawer.fillRoundRect(ImageDrawerUtils.BLACK_A35, x, y, 200, 400, 20, 20)
                            .draw(resourceLoader.getFileById(id), 200, 200, x, y)
                            .startDrawString()
                            .layout(1)
                            .drawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLUE3_A90, item.getName(), x, y + 220)
                            .layout(0, ImageDrawerUtils.WHITE_A90, ImageDrawerUtils.SMALL_FONT22)
                            .drawString(item.getDesc(), 200)
                            .drawString(ImageDrawerUtils.SMALL_FONT26, ImageDrawerUtils.BLACK_A75, "价格:", x, y + 350)
                            .drawString(item.getPrice().toString(), ImageDrawerUtils.RED_A90)
                            .drawString(ImageDrawerUtils.SMALL_FONT20, ImageDrawerUtils.BLACK_A75, String.format("限:%s/%s", v0, MAX_SHOP), x, y + 380)
                            .finish();
                    x += 205;
                    if (x > 1000) {
                        x = 5;
                        y += 405;
                    }
                }
                shopBytes = drawer.bytes();
            }
            return new GeneralData.ResDataImage(shopBytes);
        } else return JSON.toJSONString(resourceLoader.ITEM_MAP);
    }
}
