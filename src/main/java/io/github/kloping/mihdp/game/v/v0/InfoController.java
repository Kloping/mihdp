package io.github.kloping.mihdp.game.v.v0;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.kloping.MySpringTool.annotations.*;
import io.github.kloping.common.Public;
import io.github.kloping.date.DateUtils;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.v.DrawController;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.utils.DefConfig;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.rand.RandomUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @author github.kloping
 */
@Controller
public class InfoController {
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
        BaseService.MSG2ACTION.put("信息", "info");
        BaseService.MSG2ACTION.put("当前信息", "info");
        BaseService.MSG2ACTION.put("个人信息", "info");
    }

    @AutoStand
    DefConfig config;

    /**
     * 计算当前灵力多少 (仿计时
     *
     * @param resources
     */
    public void CalculateE(UsersResources resources) {
        String o0 = redisSource.id2ent.getValue(resources.getUid());
        if (o0 == null) return;
        Integer n1 = Integer.valueOf(o0.split(";")[0]);
        if (n1 != resources.getEnergy()) resources.setEnergy(n1);
        Long t0 = Long.valueOf(o0.split(";")[1]);
        //从redis获取数据 n1 代表记录的灵力值 t0 代表灵力值对应的时间戳
        Long t1 = System.currentTimeMillis() - t0;
        if (resources.getEnergy() >= 200) {
            resources.applyE(redisSource);
            return;
        }
        Integer ncd = config.getObj("ling-li.eve", Integer.class, 300000);
        if (t1 > ncd) {
            int e1 = (int) (t1 / ncd);
            resources.setEnergy(resources.getEnergy() + e1);
            if (resources.getEnergy() > 200) resources.setEnergy(200);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);
        }
    }

    private static GeneralData.ResDataChain.GeneralDataBuilder getGeneralDataBuilder(JSONObject data) {
        GeneralData.ResDataChain.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder();
        try {
            BufferedImage bi = DrawController.info(data, data.getString("tips"), data.getBoolean("t"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            builder.append(new GeneralData.ResDataImage(baos.toByteArray(), bi.getWidth(), bi.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
            builder.append("绘图失败,请稍后重试;" + e.getMessage());
        }
        return builder;
    }

    @Action("info")
    public Object info(ReqDataPack dataPack, User user) {
        JSONObject data = getInfoData(dataPack, user);
        data.put("tips", "信息获取成功");
        data.put("t", true);
        GeneralData.ResDataChain.GeneralDataBuilder builder = getGeneralDataBuilder(data);
        builder.append(new GeneralData.ResDataButton("存积分", "存积分"))
                .append(new GeneralData.ResDataButton("每日签到", "签到"))
                .append(new GeneralData.ResDataButton("赚积分", "打工"))
                .append(new GeneralData.ResDataButton("魂角列表", "魂角列表"))
                .append(new GeneralData.ResDataButton("商城√", "商城"))
                .append(new GeneralData.ResDataButton("绑定QQ(自己的真实QQ号)", "绑定QQ"));
        return builder.build();
    }

    public JSONObject getInfoData(ReqDataPack dataPack, User user) {
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        CalculateE(resources);
        Integer level = user.getLevel();
        JSONArray ar = defaultConfig.getJSONArray("xp_list");
        Integer max;
        if (ar.size() > level) {
            max = ar.getInteger(level - 1);
        } else {
            max = 99999;
        }
        if (user.getXp() >= max) {
            user.setLevel(user.getLevel() + 1);
            user.setXp(0);
            userMapper.updateById(user);
        }
        QueryWrapper<Character> qw = new QueryWrapper<>();
        qw.eq("uid", user.getUid());
        List<Character> characters = charactersMapper.selectList(qw);

        JSONObject data = JSONObject.parseObject(JSON.toJSONString(user));
        data.put("max", max);
        data.put("characters", JSONArray.parseArray(JSON.toJSONString(characters)));
        data.putAll(JSON.parseObject(JSON.toJSONString(resources)));

        if (dataPack.containsArgs(BaseService.BASE_ICON_ARGS)) {
            data.put("icon", dataPack.getArgValue(BaseService.BASE_ICON_ARGS));
        }
        if (dataPack.containsArgs(BaseService.BASE_NAME_ARGS)) {
            data.put("name", dataPack.getArgValue(BaseService.BASE_NAME_ARGS));
        }
        return data;
    }

    {
        BaseService.MSG2ACTION.put("签到", "sign");
        BaseService.MSG2ACTION.put("打卡", "sign");
    }

    @Action("sign")
    public Object sign(ReqDataPack dataPack, User user) {
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        JSONObject data = new JSONObject();
        if (resources.getDay() == DateUtils.getDay()) {
            data.put("tips", lconfig.getString("SignFail"));
            data.put("t", false);
        } else {
            int r = 300;
            r = (int) (r + (NumberUtils.percentTo(user.getLevel(), r)));
            resources.setScore0(resources.getScore0() + r);
            resources.setDay(DateUtils.getDay());
            resources.setDays(resources.getDays() + 1);
            user.addXp(1);
            resources.setFz(0);
            usersResourcesMapper.updateById(resources);
            userMapper.updateById(user);
            data.put("tips", lconfig.getString("SignSuccess", r));
            data.put("t", true);
        }
        data.putAll(getInfoData(dataPack, user));
        GeneralData.ResDataChain.GeneralDataBuilder builder = getGeneralDataBuilder(data);
        builder.append(new GeneralData.ResDataButton("存积分", "存积分"))
                .append(new GeneralData.ResDataButton("取积分", "取积分"))
                .append(new GeneralData.ResDataButton("赚积分", "打工"))
                .append(new GeneralData.ResDataButton("兑换列表", "兑换列表"));
        return builder.build();
    }

    {
        BaseService.MSG2ACTION.put("打工", "work0");
        BaseService.MSG2ACTION.put("干活", "work0");
        BaseService.MSG2ACTION.put("上班", "work0");
    }

    @Action("work0")
    public Object work0(ReqDataPack dataPack, User user) {
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        JSONObject data = new JSONObject();
        long k0 = resources.getK();
        if (k0 > System.currentTimeMillis()) {
            int m = (int) ((k0 - System.currentTimeMillis()) / 60000);
            data.put("tips", lconfig.getString("Work0Fail", m));
            data.put("t", false);
        } else {
            int f0 = RandomUtils.RANDOM.nextInt(6) + 18;
            resources.setK(System.currentTimeMillis() + f0 * 60L * 1000);
            int r = 300;
            r = (int) (r + (NumberUtils.percentTo(user.getLevel(), r)));
            user.addXp(1);
            resources.setScore(r + resources.getScore());
            usersResourcesMapper.updateById(resources);
            userMapper.updateById(user);
            data.put("tips", lconfig.getString("Work0Success", f0, r));
            data.put("t", true);
        }
        data.putAll(getInfoData(dataPack, user));
        GeneralData.ResDataChain.GeneralDataBuilder builder = getGeneralDataBuilder(data);
        builder.append(new GeneralData.ResDataButton("存积分", "存积分"))
                .append(new GeneralData.ResDataButton("取积分", "取积分"))
                .append(new GeneralData.ResDataButton("每日签到", "签到"))
                .append(new GeneralData.ResDataButton("兑换列表", "兑换列表"));
        return builder.build();
    }

    {
        BaseService.MSG2ACTION.put("取积分", "get0");
    }

    @Action("get0")
    public Object get0(ReqDataPack dataPack, User user) {
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        JSONObject data = new JSONObject();
        Integer sc = NumberUtils.getIntegerFromString(dataPack.getContent(), 1);
        if (resources.getScore0() >= sc) {
            resources.setScore(resources.getScore() + sc);
            resources.setScore0(resources.getScore0() - sc);
            usersResourcesMapper.updateById(resources);
            data.put("tips", lconfig.getString("Get0Success"));
            data.put("t", true);
        } else {
            data.put("tips", lconfig.getString("Get0Fail", sc, resources.getScore0()));
            data.put("t", false);
        }
        data.putAll(getInfoData(dataPack, user));
        return data;
    }

    {
        BaseService.MSG2ACTION.put("存积分", "put0");
    }

    @Action("put0")
    public Object put0(ReqDataPack dataPack, User user) {
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        JSONObject data = new JSONObject();
        Integer sc = NumberUtils.getIntegerFromString(dataPack.getContent(), 1);
        if (resources.getScore() >= sc) {
            resources.setScore(resources.getScore() - sc);
            resources.setScore0(resources.getScore0() + sc);
            usersResourcesMapper.updateById(resources);
            data.put("tips", lconfig.getString("Put0Success"));
            data.put("t", true);
        } else {
            data.put("tips", lconfig.getString("Put0Fail", sc, resources.getScore0()));
            data.put("t", false);
        }
        data.putAll(getInfoData(dataPack, user));
        return data;
    }

    {
        BaseService.MSG2ACTION.put("积分转让", "trans0");
        BaseService.MSG2ACTION.put("转让积分", "trans0");
    }

    @Action("trans0")
    public Object trans0(ReqDataPack dataPack, User user) {
        GeneralData generalData = (GeneralData) dataPack.getArgs().get(GameClient.ODATA_KEY);
        GeneralData.ResDataAt at = generalData.find(GeneralData.ResDataAt.class);
        if (at == null) return lconfig.getString("TargetNotFoundPrompt");
        String aid = at.getId();
        User atUser = getUser(aid);
        if (atUser == null) return lconfig.getString("TargetUnregisteredPrompt");
        GeneralData.ResDataText text = generalData.find(GeneralData.ResDataText.class);
        Integer sc = 1;
        if (text != null) sc = NumberUtils.getIntegerFromString(text.getContent(), 1);
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        UsersResources atResources = usersResourcesMapper.selectById(atUser.getUid());
        JSONObject data = new JSONObject();
        if (resources.getScore() >= sc) {
            resources.setScore(resources.getScore() - sc);
            atResources.setScore(atResources.getScore() + sc);
            usersResourcesMapper.updateById(resources);
            usersResourcesMapper.updateById(atResources);
            data.put("tips", lconfig.getString("Trans0Success"));
            data.put("t", true);
        } else {
            data.put("tips", lconfig.getString("Trans0Fail", sc, resources.getScore0()));
            data.put("t", false);
        }
        data.putAll(getInfoData(dataPack, user));
        return data;
    }

    {
        BaseService.MSG2ACTION.put("打劫", "rob0");
        BaseService.MSG2ACTION.put("抢劫", "rob0");
    }

    @Action("rob0")
    public Object rob0(ReqDataPack dataPack, User user) {
        GeneralData generalData = (GeneralData) dataPack.getArgs().get(GameClient.ODATA_KEY);
        GeneralData.ResDataAt at = generalData.find(GeneralData.ResDataAt.class);
        if (at == null) return lconfig.getString("TargetNotFoundPrompt");
        String aid = at.getId();
        User atUser = getUser(aid);
        if (atUser == null) return lconfig.getString("TargetUnregisteredPrompt");
        String text = generalData.allText();
        Integer sc = 1;
        if (!Judge.isEmpty(text)) sc = NumberUtils.getIntegerFromString(text, 1);
        UsersResources r = usersResourcesMapper.selectById(user.getUid());
        UsersResources r0 = usersResourcesMapper.selectById(atUser.getUid());
        JSONObject data = new JSONObject();
        if (sc == 1) {
            if (r.getScore() > 60) {
                if (r0.getScore() > 60) {
                    if (r.getFz() < 12) {
                        int l = RandomUtils.RANDOM.nextInt(30) + 50;
                        r.setScore(r.getScore() + l);
                        r0.setScore(r0.getScore() - l);
                        r.addFz(1);
                        usersResourcesMapper.updateById(r);
                        usersResourcesMapper.updateById(r0);
                        return lconfig.getString("Rob0Success", l);
                    } else return lconfig.getString("Rob0Fail0");
                } else return lconfig.getString("TargetScoreDeficiency");
            } else return lconfig.getString("ScoreDeficiency");
        } else {
            int suc = 0;
            int all = 0;
            for (Integer i = 0; i < sc; i++) {
                if (r.getScore() > 60) {
                    if (r0.getScore() > 60) {
                        if (r.getFz() < 12) {
                            int l = RandomUtils.RANDOM.nextInt(20) + 40;
                            r.setScore(r.getScore() + l);
                            r0.setScore(r0.getScore() - l);
                            r.addFz(1);
                            suc++;
                            all += l;
                        }
                    }
                }
            }
            if (suc > 0) {
                usersResourcesMapper.updateById(r0);
                usersResourcesMapper.updateById(r);
            }
            return lconfig.getString("Rob1Success", suc, all, suc);
        }
    }

    @CronSchedule("21 0 0 * * ? ")
    public void interest() {
        for (UsersResources userScore : usersResourcesMapper.selectList(null)) {
            Public.EXECUTOR_SERVICE.submit(() -> {
                try {
                    if (userScore.getScore0() <= 10000) {
                        return;
                    } else {
                        int s = (int) (userScore.getScore0() / 10000 * 4);
                        userScore.setScore0(userScore.getScore0() + s);
                        usersResourcesMapper.updateById(userScore);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
