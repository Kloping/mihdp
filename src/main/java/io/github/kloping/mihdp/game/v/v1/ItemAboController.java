package io.github.kloping.mihdp.game.v.v1;

import io.github.kloping.MySpringTool.annotations.*;
import io.github.kloping.arr.Class2OMap;
import io.github.kloping.mihdp.dao.Bag;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.game.v.v1.service.ItemOf;
import io.github.kloping.mihdp.mapper.BagMaper;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.number.NumberUtils;
import lombok.Data;

/**
 * @author github.kloping
 */
@Controller
public class ItemAboController {
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    GameStaticResourceLoader resourceLoader;
    @AutoStand
    BeginController beginController;
    @AutoStand
    BagMaper bagMaper;

    @Before
    public User before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return user;
    }

    @AutoStandAfter
    public void after() {
        BaseService.MSG2ACTION.put("使用", "use");
    }

    @AutoStand
    ItemOf itemOf;

    @Action("use")
    public Object use(User user, ReqDataPack pack) {
        GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
        String name = generalData.allText().trim();
        //过滤数量
        Integer num = NumberUtils.getIntegerFromString(name, 1);
        //寻找物品对象
        Item item = null;
        for (Item value : resourceLoader.itemMap.values()) {
            if (name.contains(value.getName())) {
                if (item == null)
                    item = value;
                else if (value.getName().length() > item.getName().length())
                    item = value;
                else continue;
            }
        }
        if (item == null) return null;
        //判断数量是否足够
        Bag bag = bagMaper.selectByUidAndRid(user.getUid(), item.getId());
        if (bag == null || bag.getNum() < num) return "已拥有物品数量不足.";
        Class2OMap map = Class2OMap.create(user, pack, bag, item, num, generalData.allText());
        if (itemOf.CONTEXT_MAP.containsKey(item.getId())) {
            UseState state = itemOf.CONTEXT_MAP.get(item.getId()).execute(map);
            if (state.ok) {
                bag.setNum(bag.getNum() - state.c);
                bag.save(bagMaper);
                return state.out;
            } else return "使用失败!\n" + (state.out == null ? "" : state.out);
        } else return "相关物品无法使用或暂无法主动使用";
    }

    @Data
    public static class UseState {
        public UseState(boolean ok, Object out, Integer c) {
            this.ok = ok;
            this.out = out;
            this.c = c;
        }

        /**
         * 是否执行成功
         */
        private boolean ok = false;
        /**
         * 输出数据
         */
        private Object out;
        /**
         * 成功次数计数
         */
        private Integer c = 0;
    }


    {
        BaseService.MSG2ACTION.put("背包", "bag");
        BaseService.MSG2ACTION.put("我的背包", "bag");
    }

    @Action("bag")
    public Object bag(User user) throws Exception {
        ImageDrawer drawer = ImageDrawer.createOnBg(1);
        int w = 1060, h = 820;
        drawer.size(1060, 820);
        int x = 10, y = 30;
        drawer.startDrawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLACK_A85, "uid:" + user.getUid(), 2, 22).finish();
        for (Bag bag : bagMaper.selectByUid(user.getUid())) {
            drawer.fillRoundRect(ImageDrawerUtils.BLACK_A35, x, y, 200, 250, 15, 15)
                    .draw(resourceLoader.getFileById(bag.getRid()), 200, 200, x, y)
                    .startDrawString().layout(1)
                    .drawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLACK_A90, resourceLoader.itemMap.get(bag.getRid()).getName(), x, y + 200)
                    .drawString(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A95, bag.getNum().toString(), x, y + 230)
                    .finish();
            x += 200;
            if (x >= 1000) {
                x = 10;
                y += 250;
            }
        }
        GeneralData.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder()
                .append(new GeneralData.ResDataImage(drawer.bytes(), w, h))
                .append(new GeneralData.ResDataButton("商城", "商城"))
                .append(new GeneralData.ResDataButton("使用", "使用"));
        return builder.build();
    }
}
