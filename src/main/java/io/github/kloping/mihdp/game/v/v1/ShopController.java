package io.github.kloping.mihdp.game.v.v1;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.mihdp.dao.Bag;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.mapper.BagMaper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.number.NumberUtils;

/**
 * @author github.kloping
 */
@Controller
public class ShopController {
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    RedisSource redisSource;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;
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

    private Integer getMaxByShopId(Integer id) {
        return id >= 2001 && id <= 2007 ? 10 : id == 103 ? 2 : 100;
    }

    private Integer getCurByShopIdAndUid(String sid, Integer id) {
        return addCurByShopIdAndUid(sid, id, 0);
    }

    private Integer addCurByShopIdAndUid(String sid, Integer id, int n) {
        String key = null;
        if (id >= 2001 && id <= 2007) {
            //魂环1限制
            key = String.format("buy-%s.%s", sid, "1");
        } else if (id == 103) {
            //灵石2限制
            key = String.format("buy-%s.%s", sid, "3");
        } else {
            //暂时 其余2限制
            key = String.format("buy-%s.%s", sid, "2");
        }
        Integer v = redisSource.weekDel.getValue(key);
        if (v == null) v = 0;
        if (n <= 0) return v;
        else return redisSource.weekDel.setValue(key, v + n).getValue(key);
    }

    {
        BaseService.MSG2ACTION.put("商城", "shop");
        BaseService.MSG2ACTION.put("商店", "shop");
        BaseService.MSG2ACTION.put("商场", "shop");
    }

    @Action("shop")
    public Object shop(ReqDataPack pack, User user) throws Exception {
        if (pack.isArgValue("draw", true)) {
            ImageDrawer drawer = ImageDrawer.createOnBg(2);
            final int w = 1030, h = 1225;
            drawer.size(w, h);
            int x = 5, y = 5;
            for (Integer id : resourceLoader.itemMap.keySet()) {
                Integer v0 = getCurByShopIdAndUid(user.getUid(), id);
                if (v0 == null) v0 = 0;
                Item item = resourceLoader.itemMap.get(id);
                if (item.getPrice() == null || item.getPrice() <= 0) continue;
                drawer.fillRoundRect(ImageDrawerUtils.BLACK_A35, x, y, 200, 400, 20, 20)
                        .draw(resourceLoader.getFileById(id), 200, 200, x, y)
                        .startDrawString().layout(1)
                        .drawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLUE3_A90, item.getName(), x, y + 220)
                        .layout(0, ImageDrawerUtils.WHITE_A90, ImageDrawerUtils.SMALL_FONT22)
                        .drawString(item.getDesc(), 200)
                        .drawString(ImageDrawerUtils.SMALL_FONT26, ImageDrawerUtils.BLACK_A75, "价格:", x, y + 350)
                        .drawString(item.getPrice().toString(), ImageDrawerUtils.RED_A90)
                        .drawString(ImageDrawerUtils.SMALL_FONT20, ImageDrawerUtils.BLACK_A75,
                                String.format("限:%s/%s", v0, getMaxByShopId(item.getId())), x, y + 380)
                        .finish();
                x += 205;
                if (x > 1000) {
                    x = 5;
                    y += 405;
                }
            }
            return new GeneralData.ResDataChain.GeneralDataBuilder()
                    .append(new GeneralData.ResDataImage(drawer.bytes(), w, h))
                    .append("使用积分购买商品,魂环商品共享购买上限,特殊物品独享上限,其余普通商品共享购买上限.\r购买次数每周刷新.或通过特殊方法获得购买次数")
                    .append(new GeneralData.ResDataButton("购买物品", "购买"))
                    .append(new GeneralData.ResDataButton("查看背包", "背包"))
                    .build();
        } else return JSON.toJSONString(resourceLoader.itemMap);
    }

    {
        BaseService.MSG2ACTION.put("购买", "buy");
        BaseService.MSG2ACTION.put("买", "buy");
    }

    @Action("buy")
    public Object buy(ReqDataPack pack, User user) {
        GeneralData generalData = pack.getGeneralData();
        String name = generalData.allText().trim();
        Item target = null;
        for (Integer id : resourceLoader.itemMap.keySet()) {
            Item item = resourceLoader.itemMap.get(id);
            if (name.contains(item.getName())) {
                if (target == null) target = item;
                else {
                    if (target.getName().length() < item.getName().length()) target = item;
                }
            }
        }
        if (target != null) {
            Integer num = NumberUtils.getIntegerFromString(name, 1);
            UsersResources resources = usersResourcesMapper.selectById(user.getUid());
            if (getCurByShopIdAndUid(user.getUid(), target.getId()) + num > getMaxByShopId(target.getId())) {
                return "购买上限!";
            }
            if (target.getPrice() * num > resources.getScore()) {
                return "积分不足!";
            } else {
                appendItemToBag(user, target, num);
                resources.setScore(resources.getScore() - (target.getPrice() * num));
                usersResourcesMapper.updateById(resources);
                addCurByShopIdAndUid(user.getUid(), target.getId(), num);
                return "购买成功!";
            }
        }
        return "未发现相关商品";
    }

    public void appendItemToBag(User user, Item target, Integer num) {
        Bag bag = bagMaper.selectByUidAndRid(user.getUid(), target.getId());
        if (bag == null) {
            bag = new Bag(user.getUid(), target.getId(), num, num);
            bagMaper.insert(bag);
        } else {
            bag.setNum(bag.getNum() + num).setSize(bag.getNum());
            QueryWrapper<Bag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", user.getUid());
            queryWrapper.eq("rid", target.getId());
            bagMaper.update(bag, queryWrapper);
        }
    }

}
