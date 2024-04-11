package io.github.kloping.mihdp.game.v.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.date.DateUtils;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.Cycle;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.api.Addition;
import io.github.kloping.mihdp.game.impl.AdditionLogic;
import io.github.kloping.mihdp.game.s.CharacterInfo;
import io.github.kloping.mihdp.game.s.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.CycleMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.p0.utils.NumberSelector;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.rand.RandomUtils;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * -
 * @author github.kloping
 */
@Controller
public class CharactersController {
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
            return GeneralData.GeneralDataBuilder.create("符合领取条件;\n请选择要领取的魂角(操作不可逆)").append(1, "落日神弓").append(2, "神空剑").build();
        }
        return "不符合领取条件";
    }

    {
        BaseService.MSG2ACTION.put("魂角列表", "characters");
    }

    @Action("characters")
    public Object characters(ReqDataPack pack, User user) {
        QueryWrapper<Character> qw = new QueryWrapper<>();
        qw.eq("uid", user.getUid());
        List<Character> characters = charactersMapper.selectList(qw);
        if (characters.isEmpty()) return "未觉醒任何魂角,请'领取魂角'";
        final int w = 825, h = 825;
        if (pack.isArgValue("draw", true)) {
            try {
                ImageDrawer drawer = ImageDrawer.createOnRandomBg();
                drawer.size(w, h);
                int x = 5;
                int y = 15;
                for (Character character : charactersMapper.selectList(qw)) {
                    //预计展示 血量 经验 等级 图标 等..
                    Integer maxHp = redisSource.str2int.getValue("cid-hp-" + character.getId());
                    Integer maxXp = redisSource.str2int.getValue("cid-xp-" + character.getId());
                    if (maxHp == null || maxXp == null) {
                        CharacterOutResult result = compute(character);
                        if (result == null) return "未知错误 code:-201.";
                        maxHp = result.maxHp;
                        maxXp = result.maxXp;
                    }

                    drawerOneCharacter(drawer, x, y, character, maxXp, maxHp);
                    //draw next
                    x += 205;
                    if (x > 800) {
                        x = 5;
                        y += 405;
                    }
                }
                drawer.startDrawString(ImageDrawerUtils.SMALL_FONT16, Color.BLACK, DateUtils.getFormat(), 5, 807);
                GeneralData.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder()
                        .append(new GeneralData.ResDataImage(drawer.bytes(), w, h))
                        .append(new GeneralData.ResDataButton("查看[魂角名]", "查看"))
                        .append(new GeneralData.ResDataButton("切换[魂角名]", "切换"))
                        .append(new GeneralData.ResDataButton("使用[物品名]", "使用"))
                        .append(new GeneralData.ResDataButton("修炼", "修炼"));
                return builder.build();
            } catch (Exception e) {
                return "绘图失败." + e.getMessage();
            }
        } else {
            return JSON.toJSONString(characters);
        }
    }

    public CharacterOutResult compute(Character character) {
        //基础的魂角属性
        CharacterInfo characterInfo = resourceLoader.getCharacterInfoById(character.getCid());
        if (characterInfo == null) return null;
        for (Addition addition : additionLogic.getAddition(character)) {
            characterInfo.reg(addition);
        }
        //魂环加成
        QueryWrapper<Cycle> qw1 = new QueryWrapper<>();
        qw1.eq("cid", character.getId());
        List<Cycle> cycles = cycleMapper.selectList(qw1);
        for (Cycle cycle : cycles) {
            for (Addition addition : additionLogic.getAddition(cycle)) {
                characterInfo.reg(addition);
            }
        }
        characterInfo.setLevel(character.getLevel());
        int maxHp = characterInfo.getHp().getFinalValue();
        int maxXp = characterInfo.getXp().getFinalValue();
        redisSource.str2int.setValue("cid-hp-" + character.getId(), maxHp);
        redisSource.str2int.setValue("cid-xp-" + character.getId(), maxXp);
        return new CharacterOutResult(maxHp, maxXp);
    }

    public static class CharacterOutResult {
        public final Integer maxHp;
        public final Integer maxXp;

        public CharacterOutResult(Integer maxHp, Integer maxXp) {
            this.maxHp = maxHp;
            this.maxXp = maxXp;
        }
    }

    {
        BaseService.MSG2ACTION.put("查看", "show_character");
    }

    @AutoStand
    CycleMapper cycleMapper;
    @AutoStand
    AdditionLogic additionLogic;

    @Action("show_character")
    public Object showC0(ReqDataPack pack, User user) {
        GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
        String name = generalData.allText().trim();
        //基础的魂角属性
        CharacterInfo characterInfo = null;
        //用户魂角配置
        Character character = null;
        characterInfo = resourceLoader.getCharacterInfoByName(name);
        if (characterInfo == null) {
            Integer cid = redisSource.uid2cid.getValue(user.getUid());
            if (cid == null) return "查询失败!\n可能不存在该魂角";
            character = charactersMapper.selectById(cid);
            characterInfo = resourceLoader.getCharacterInfoById(character.getCid());
        }
        if (character == null) {
            QueryWrapper<Character> qw0 = new QueryWrapper<>();
            qw0.eq("uid", user.getUid());
            qw0.eq("cid", characterInfo.getId());
            character = charactersMapper.selectOne(qw0);
            if (character == null) return "查询失败!\n可能尚未拥有.";
        }
        for (Addition addition : additionLogic.getAddition(character)) {
            characterInfo.reg(addition);
        }
        //魂环加成
        QueryWrapper<Cycle> qw1 = new QueryWrapper<>();
        qw1.eq("cid", character.getId());
        List<Cycle> cycles = cycleMapper.selectList(qw1);
        for (Cycle cycle : cycles) {
            for (Addition addition : additionLogic.getAddition(cycle)) {
                characterInfo.reg(addition);
            }
        }
        characterInfo.setLevel(character.getLevel());

        redisSource.str2int.setValue("cid-hp-" + character.getId(), characterInfo.getHp().getFinalValue());
        redisSource.str2int.setValue("cid-xp-" + character.getId(), characterInfo.getXp().getFinalValue());
        final int w = 800, h = 1000;
        if (pack.isArgValue("draw", true)) {
            try {
                ImageDrawer drawer = ImageDrawer.createOnRandomBg();
                Character finalCharacter = character;
                CharacterInfo finalCharacterInfo = characterInfo;
                drawer.size(w, h)
                        .draw(resourceLoader.id2file.get(character.getCid()), 210, 210, 5, 35)
                        .fillRoundRect(ImageDrawerUtils.BLACK_A35, 250, 15, 540, 400, 15, 15)
                        .execute(graphics -> {
                            graphics.setFont(ImageDrawerUtils.SMALL_FONT18);
                            graphics.setColor(ImageDrawerUtils.BLACK_A75);
                            graphics.drawString(finalCharacter.getUid(), 2, graphics.getFontMetrics().getHeight());

                            graphics.setFont(ImageDrawerUtils.SMALL_FONT32);
                            graphics.setColor(ImageDrawerUtils.RED_A90);
                            int sw = graphics.getFontMetrics().stringWidth(finalCharacterInfo.getName());
                            int sx = (210 - sw) / 2;
                            graphics.drawString(finalCharacterInfo.getName(), sx + 5, 300);
                        })

                        .startDrawString(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "等级  :", 255, 50)
                        .drawString(characterInfo.getLevel(), ImageDrawerUtils.ORIGIN_A80)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "血量  :", 255, 5)
                        .drawString(characterInfo.getHp().getFinalValue())
                        .drawString(String.format("(+%s%%)", characterInfo.getHp().getBv() - 100), ImageDrawerUtils.BLUE5_A75)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "攻击  :", 255, 5)
                        .drawString(characterInfo.getAtt().getFinalValue())
                        .drawString(String.format("(+%s%%)", characterInfo.getAtt().getBv() - 100), ImageDrawerUtils.BLUE5_A75)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "魂力  :", 255, 5)
                        .drawString(characterInfo.getHl().getFinalValue())
                        .drawString(String.format("(+%s%%)", characterInfo.getHl().getBv() - 100), ImageDrawerUtils.BLUE5_A75)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "精神力:", 255, 5)
                        .drawString(characterInfo.getHj().getFinalValue())
                        .drawString(String.format("(+%s%%)", characterInfo.getHj().getBv() - 100), ImageDrawerUtils.BLUE5_A75)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "防御  :", 255, 5)
                        .drawString(characterInfo.getDefense().getFinalValue())
                        .drawString(String.format("(+%s%%)", characterInfo.getDefense().getBv() - 100), ImageDrawerUtils.BLUE5_A75)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "速度  :", 255, 5)
                        .drawString(characterInfo.getSpeed().getFinalValue())
                        .drawString(String.format("(+%s%%)", characterInfo.getSpeed().getBv() - 100), ImageDrawerUtils.BLUE5_A75)

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "爆率  :", 255, 5)
                        .drawString(characterInfo.getChc().getFinalValue() + "%")
                        .finishAndStartDrawString(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "爆伤  :", 520, 344)
                        .drawString(characterInfo.getChe().getFinalValue() + "%")

                        .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "命中率:", 255, 5)
                        .drawString(characterInfo.getEfr().getFinalValue() + "%")
                        .finishAndStartDrawString(ImageDrawerUtils.SMALL_FONT32, ImageDrawerUtils.BLACK_A90, "抵抗率:", 520, 386)
                        .drawString(characterInfo.getEfh().getFinalValue() + "%")

                        .finish();
                int x = 15;
                int y = 440;
                for (Cycle cycle : cycles) {
                    drawer.draw(resourceLoader.id2file.get(cycle.getOid()), 120, 120, x, y, (float) RandomUtils.RANDOM.nextInt(360));
                    x += 80;
                    if (x >= 400) {
                        x = 15;
                        y += 90;
                    }
                }
                GeneralData.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder()
                        .append(new GeneralData.ResDataImage(drawer.bytes(), w, h))
                        .append(new GeneralData.ResDataButton("魂角列表", "魂角列表"))
                        .append(new GeneralData.ResDataButton("使用", "使用"));
                return builder.build();
            } catch (Exception e) {
                return "绘图失败." + e.getMessage();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(JSON.parseObject(JSON.toJSONString(characterInfo)));
            jsonObject.put("cycle", cycles);
            return jsonObject;
        }
    }

    {
        BaseService.MSG2ACTION.put("切换", "handoff");
    }

    @Action("handoff")
    public Object handoff(ReqDataPack pack, User user) {
        GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
        String text = generalData.allText().trim();
        if (Judge.isEmpty(text)) return null;
        CharacterInfo characterInfo = resourceLoader.getCharacterInfoByName(text);
        if (characterInfo == null) return "切换失败!\n可能不存在该魂角";
        QueryWrapper<Character> qw0 = new QueryWrapper<>();
        qw0.eq("uid", user.getUid());
        qw0.eq("cid", characterInfo.getId());
        Character character = charactersMapper.selectOne(qw0);
        if (character == null) return "切换失败!\n可能尚未拥有.";
        redisSource.uid2cid.setValue(user.getUid(), character.getId());
        return "切换成功!";
    }

    {
        BaseService.MSG2ACTION.put("修炼", "xl");
    }

    @AutoStand
    ItemAboController itemAboController;

    @Action("xl")
    public Object xl(ReqDataPack pack, User user) {
        Long cd = redisSource.uid2cd.getValue(user.getUid());
        if (cd != null && cd > System.currentTimeMillis())
            return "冷却中..\n大约等待(分钟):" + ((cd - System.currentTimeMillis()) / 60000);
        Character character = getCharacterOrLowestLevel(user.getUid());
        if (character == null) return "暂无魂角,请先`领取魂角`";
        else {
            //预计展示 血量 经验 等级 图标 等..
            Integer maxHp = redisSource.str2int.getValue("cid-hp-" + character.getId());
            Integer maxXp = redisSource.str2int.getValue("cid-xp-" + character.getId());
            if (maxHp == null || maxXp == null) {
                C0MaxResult result = computeCharacterMax(character);
                if (result == null) {
                    return "计算加成异常.";
                }
                maxXp = result.maxXp;
                maxHp = result.maxHp;
            }
            //设置cd
            redisSource.uid2cd.setValue(user.getUid(), System.currentTimeMillis() + (18L * 60000));


            GeneralData.ResDataChain.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder();
            builder.append("每次修炼恢复当前使用魂角10%血量并增加10点经验");
            int hpa = NumberUtils.percentTo(10, maxHp).intValue();

            character.setHp(character.getHp() + hpa);
            character.setHp(character.getHp() > maxHp ? maxHp : character.getHp());
            charactersMapper.updateById(character);

            character.setXp(character.getXp() + 10);
            character.setXp(character.getXp() > (maxHp * 1.5) ? (int) (maxXp * 1.5) : character.getXp());
            if (character.getXp() >= maxXp) {
                if (!itemAboController.testForC(character, maxXp)) {
                    builder.append("\n等级限制,经验上限无法升级.再次升级需要吸收魂环.");
                } else {
                    C0MaxResult result = computeCharacterMax(character);
                    if (result == null) {
                        return "计算加成异常.";
                    }
                    maxXp = result.maxXp;
                    maxHp = result.maxHp;
                    charactersMapper.updateById(character);
                }
            }
            builder.append("\n当前:血量" + String.format("%s%%;等级:%s;经验:%s/%s"
                    , NumberUtils.toPercent(character.getHp(), maxHp)
                    , character.getLevel()
                    , character.getXp()
                    , maxHp));

            try {
                int x = 10, y = 10;
                int w = 220, h = 420;
                ImageDrawer drawer = ImageDrawer.createOnRandomBg();
                drawer.size(w, h);
                drawerOneCharacter(drawer, x, y, character, maxXp, maxHp);
                builder.append(new GeneralData.ResDataImage(drawer.bytes(), w, h));
            } catch (IOException e) {
                e.printStackTrace();
            }

            builder.append(new GeneralData.ResDataButton("魂角列表", "魂角列表"))
                    .append(new GeneralData.ResDataButton("查看", "查看"));
            return builder.build();
        }
    }

    private void drawerOneCharacter(ImageDrawer drawer, int x, int y, Character character, Integer maxXp, Integer maxHp) throws IOException {
        drawer.fillRoundRect(ImageDrawerUtils.WHITE_A35, x, y, 200, 400, 15, 15)
                .draw(resourceLoader.getFileById(character.getCid()), 180, 180, x + 10, y + 10);

        //draw xp
        drawer.startDrawString(ImageDrawerUtils.SMALL_FONT20, ImageDrawerUtils.BLACK_A90, "经验:", x + 5, y + 218)
                .drawString(character.getXp() + "/", ImageDrawerUtils.BLACK_A75)
                .drawString(maxXp, ImageDrawerUtils.RED_A90)
                .finish()
                .drawRoundRect(ImageDrawerUtils.WHITE_A80, x + 5, y + 225, 180, 30, 10, 10);

        int xb = NumberUtils.toPercent(character.getXp(), maxXp);
        int xw = NumberUtils.percentTo(xb, 180).intValue();
        drawer.fillRoundRect(xb < 50 ? ImageDrawerUtils.GREEN_A75 : xb < 80 ? ImageDrawerUtils.ORIGIN_A75 : ImageDrawerUtils.RED_A75
                , x + 5, y + 225, xw, 30, 10, 10);
        //=draw hp
        drawer.startDrawString(ImageDrawerUtils.SMALL_FONT20, ImageDrawerUtils.BLACK_A90, "血量:", x + 5, y + 278)
                .drawString(character.getHp() + "/", ImageDrawerUtils.BLACK_A75)
                .drawString(maxHp, ImageDrawerUtils.RED_A90)
                .finish()
                .drawRoundRect(ImageDrawerUtils.WHITE_A80, x + 5, y + 285, 180, 30, 10, 10);

        int hb = NumberUtils.toPercent(character.getHp(), maxHp);
        int hw = NumberUtils.percentTo(hb, 180).intValue();
        drawer.fillRoundRect(hb > 50 ? ImageDrawerUtils.GREEN_A75 : hb > 25 ? ImageDrawerUtils.ORIGIN_A75 : ImageDrawerUtils.RED_A75
                , x + 5, y + 285, hw, 30, 10, 10);

        //draw str
        drawer.startDrawString(ImageDrawerUtils.SMALL_FONT24, ImageDrawerUtils.BLACK_A90, "等级:", x + 5, y + 350)
                .drawString(character.getLevel(), ImageDrawerUtils.RED_A90)
                .drawString(String.format("(%s)", resourceLoader.getCharacterInfoById(character.getCid()).getName()), ImageDrawerUtils.BLACK_A90, ImageDrawerUtils.SMALL_FONT18_TYPE0)
                .finishAndStartDrawStringDown(ImageDrawerUtils.SMALL_FONT20, ImageDrawerUtils.BLACK_A90, "所属uid:", x + 5, 3)
                .drawString(character.getUid());
    }

    public C0MaxResult computeCharacterMax(Character character) {
        //基础的魂角属性
        CharacterInfo characterInfo = resourceLoader.getCharacterInfoById(character.getCid());
        if (characterInfo == null) return null;
        for (Addition addition : additionLogic.getAddition(character)) {
            characterInfo.reg(addition);
        }
        //魂环加成
        QueryWrapper<Cycle> qw1 = new QueryWrapper<>();
        qw1.eq("cid", character.getId());
        List<Cycle> cycles = cycleMapper.selectList(qw1);
        for (Cycle cycle : cycles) {
            for (Addition addition : additionLogic.getAddition(cycle)) {
                characterInfo.reg(addition);
            }
        }
        characterInfo.setLevel(character.getLevel());
        Integer maxHp = characterInfo.getHp().getFinalValue();
        Integer maxXp = characterInfo.getXp().getFinalValue();
        redisSource.str2int.setValue("cid-hp-" + character.getId(), maxHp);
        redisSource.str2int.setValue("cid-xp-" + character.getId(), maxXp);
        C0MaxResult result = new C0MaxResult(maxHp, maxXp);
        return result;
    }

    public static class C0MaxResult {
        public final Integer maxHp;
        public final Integer maxXp;

        public C0MaxResult(Integer maxHp, Integer maxXp) {
            this.maxHp = maxHp;
            this.maxXp = maxXp;
        }
    }


    public Character getCharacterOrLowestLevel(String uid) {
        Integer cid = redisSource.uid2cid.getValue(uid);
        if (cid == null) {
            QueryWrapper<Character> qw0 = new QueryWrapper<>();
            qw0.eq("uid", uid);
            qw0.orderBy(true, true, "level");
            List<Character> list = charactersMapper.selectList(qw0);
            if (list.isEmpty()) return null;
            return list.get(0);
        } else {
            return charactersMapper.selectById(cid);
        }
    }
}
