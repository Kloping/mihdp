package io.github.kloping.mihdp.game.v.v1;

import io.github.kloping.MySpringTool.annotations.*;
import io.github.kloping.mihdp.dao.Bag;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.api.UseItemInterface;
import io.github.kloping.mihdp.game.s.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
@Controller
public class ItemAboController {
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
    @AutoStand
    CharacterMapper charactersMapper;
    @AutoStand
    CharactersController charactersController;

    @Before
    public User before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return user;
    }

    public Map<String, UseItemInterface<User, Object>> useItemInterfaceMap = new HashMap<>();


    @AutoStandAfter
    public void after() {
        BaseService.MSG2ACTION.put("使用", "use");
    }

    private void init() {
        //使用经验石的效果
        useItemInterfaceMap.put(resourceLoader.ITEM_MAP.get(101).getName(), (c, user) -> {
            Character character = charactersController.getCharacterOrLowestLevel(user.getUid());
            if (character == null) return "未拥有任何魂角";
            Integer maxXp = redisSource.str2int.getValue("cid-xp-" + character.getId());
            int xpa = 0, levela = 0;
            for (int i = 0; i < c; i++) {
                character.setXp(character.getXp() + 100);
                xpa += 100;
                if (character.getXp() >= maxXp) {
                    if (character.getLevel() % 10 == 0) {
                        character.setXp(maxXp);
                        charactersMapper.updateById(character);
                        return "经验上限喽";
                    } else {
                        character.setXp(character.getXp() - maxXp);
                        character.setLevel(character.getLevel() + 1);
                        maxXp = charactersController.compute(character).maxXp;
                        levela++;
                    }
                }
            }
            charactersMapper.updateById(character);
            return String.format("对魂角(%s)使用完成(%s).\n累计增加了%s经验值\n增加了%s级",
                    resourceLoader.getCharacterInfoById(character.getCid()).getName(), c, xpa, levela);
        });
        useItemInterfaceMap.put(resourceLoader.ITEM_MAP.get(102).getName(), (c, user) -> {
            Character character = charactersController.getCharacterOrLowestLevel(user.getUid());
            if (character == null) return "未拥有任何魂角";
            Integer maxXp = redisSource.str2int.getValue("cid-xp-" + character.getId());
            int xpa = 0, levela = 0;
            for (int i = 0; i < c; i++) {
                character.setXp(character.getXp() + 200);
                xpa += 200;
                if (character.getXp() >= maxXp) {
                    if (character.getLevel() % 10 == 0) {
                        character.setXp(maxXp);
                        charactersMapper.updateById(character);
                        return "经验上限喽";
                    } else {
                        character.setXp(character.getXp() - maxXp);
                        character.setLevel(character.getLevel() + 1);
                        maxXp = charactersController.compute(character).maxXp;
                        levela++;
                    }
                }
            }
            charactersMapper.updateById(character);
            return String.format("对魂角(%s)使用完成(%s).\n累计增加了%s经验值\n增加了%s级",
                    resourceLoader.getCharacterInfoById(character.getCid()).getName(), c, xpa, levela);
        });
    }

    public static final Integer USE_STATE_OK = 0;

    @Action("use")
    public Object use(User user, ReqDataPack pack) {
        synchronized (useItemInterfaceMap) {
            if (useItemInterfaceMap.isEmpty()) {
                init();
            }
        }
        GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
        String name = generalData.allText().trim();
        Integer num = NumberUtils.getIntegerFromString(name, 1);
        name = name.replace(num.toString(), "");
        UseItemInterface<User, Object> itemInterface = useItemInterfaceMap.get(name);
        if (itemInterface == null) return "未发现相关物品或物品暂无法主动使用.";
        else {
            synchronized (user.getUid()) {
                try {
                    return itemInterface.execute(num, user);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "使用途中.发生异常.\n" + e.getMessage();
                }
            }
        }
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
        drawer.startDrawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLACK_A85, "uid:" + user.getUid(), 2, 17).finish();
        for (Bag bag : bagMaper.selectByUid(user.getUid())) {
            drawer.fillRoundRect(ImageDrawerUtils.BLACK_A35, x, y, 200, 250, 15, 15)
                    .draw(resourceLoader.getFileById(bag.getRid()), 200, 200, x, y)
                    .startDrawString().layout(1)
                    .drawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLACK_A90, resourceLoader.ITEM_MAP.get(bag.getRid()).getName(), x, y + 200)
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
