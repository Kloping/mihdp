package io.github.kloping.mihdp.game.v.v2;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.MySpringTool.exceptions.NoRunException;
import io.github.kloping.MySpringTool.interfaces.component.ContextManager;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.dao.Item;
import io.github.kloping.mihdp.game.scenario.Scenario;
import io.github.kloping.mihdp.game.scenario.ScenarioImpl;
import io.github.kloping.mihdp.game.scenario.ScenarioManager;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.csn.CiBase;
import io.github.kloping.mihdp.game.service.effs.AttEff;
import io.github.kloping.mihdp.game.service.fb.FbService;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.game.v.v0.BeginController;
import io.github.kloping.mihdp.game.v.v0.InfoController;
import io.github.kloping.mihdp.game.v.v1.CharactersController;
import io.github.kloping.mihdp.game.v.v1.ShopController;
import io.github.kloping.mihdp.game.v.v1.service.BaseCo;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.mapper.UsersResourcesMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.p0.utils.NumberSelector;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author github.kloping
 */
@Controller
public class FuBenController {
    @AutoStand
    BeginController beginController;
    @AutoStand
    UserMapper userMapper;
    @AutoStand
    LanguageConfig lconfig;
    @AutoStand
    ScenarioManager manager;
    @AutoStand
    RedisSource redisSource;
    @AutoStand
    ExecutorService executorService;
    @AutoStand
    BaseCo baseCi;
    @AutoStand
    FbService fbService;
    @AutoStand
    ContextManager contextManager;
    @AutoStand
    ShopController shopController;
    @AutoStand
    GameStaticResourceLoader resourceLoader;
    @AutoStand
    UsersResourcesMapper usersResourcesMapper;
    @AutoStand
    InfoController infoController;
    @AutoStand
    CharacterMapper characterMapper;
    @AutoStand
    CharactersController charactersController;

    public static final Integer EVE = 30;

    @Before
    public Object before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        Character character = charactersController.getCurrentCharacterOrLowestLevel(user.getUid());
        if (character == null) throw new NoRunException("Character==null for " + user);
        return new Object[]{character, dataPack.getSender_id(), user};
    }

    {
        BaseService.MSG2ACTION.put("进入副本", "join-fb");
        BaseService.MSG2ACTION.put("攻击", "att");
        BaseService.MSG2ACTION.put("撤离", "evacuate");
        BaseService.MSG2ACTION.put("副本列表", "fb-list");
        BaseService.MSG2ACTION.put("跳过", "jem");
        BaseService.MSG2ACTION.put("副本邀请", "fb-invite");
        BaseService.MSG2ACTION.put("退队", "fb-team-out");
        BaseService.MSG2ACTION.put("当前队伍", "fb-team");
    }

    @Action("fb-invite")
    public Object invite(ReqDataPack dataPack, User user) {
        GeneralData generalData = (GeneralData) dataPack.getArgs().get(GameClient.ODATA_KEY);
        GeneralData.ResDataAt at = generalData.find(GeneralData.ResDataAt.class);
        if (at == null) return lconfig.getString("TargetNotFoundPrompt");
        String aid = at.getId();
        User atUser = infoController.getUser(aid);
        if (atUser == null) return lconfig.getString("TargetUnregisteredPrompt");
        String fid = atUser.getId();
        NumberSelector.reg(fid).set(1, d -> {
            return new GeneralData.ResDataChain.GeneralDataBuilder()
                    .append(manager.invite(user.getId(), atUser.getId()) ? "邀请成功" : "邀请失败")
                    .append(new GeneralData.ResDataButton("退出队伍", "退队"))
                    .append(new GeneralData.ResDataButton("当前队伍", "当前队伍"))
                    .append(new GeneralData.ResDataButton("继续邀请", "副本邀请@"))
                    .append(new GeneralData.ResDataButton("进入副本", "进入副本"))
                    .build();
        }).set(2, d -> {
            return new GeneralData.ResDataChain.GeneralDataBuilder()
                    .append("已取消")
                    .append(new GeneralData.ResDataButton("退出队伍", "退队"))
                    .append(new GeneralData.ResDataButton("当前队伍", "当前队伍"))
                    .append(new GeneralData.ResDataButton("继续邀请", "副本邀请@"))
                    .append(new GeneralData.ResDataButton("进入副本", "进入副本"))
                    .build();
        });
        return new GeneralData.ResDataChain.GeneralDataBuilder()
                .append("邀请成功;回复数字:1:同意 2.取消")
                .append(new GeneralData.ResDataButton("同意", "1"))
                .append(new GeneralData.ResDataButton("取消", "2"))
                .build();
    }

    @Action("fb-team-out")
    public Object out(User user) {
        return manager.outTeam(user) ? "成功" : "异常";
    }

    @Action("fb-team")
    public Object team(User user) {
        if (!manager.id2list.containsKey(user.getId())) {
            return "无";
        } else {
            StringBuilder sb = new StringBuilder();
            for (User u : manager.id2list.get(user.getId())) {
                sb.append(u.getId()).append(" - ").append(u.getUid()).append("\n");
            }
            return sb.toString();
        }
    }

    @Action("fb-list")
    public Object fbList(Character character, String qid) {
        try {
            byte[] bytes = ReadUtils.readAll(new ClassPathResource("fb-list.jpg").getInputStream());
            GeneralData.ResDataChain.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder();

            builder.append("每次进入副本消耗" + EVE + "灵力\n使用`进入副本xxxx`\ntips:当前暂仅开放'原始森林','荒野森林'")
                    .append(new GeneralData.ResDataImage(bytes, 215, 350));
            for (String value : fbService.NAME2LEVEL_MIN.keySet()) {
                builder.append(new GeneralData.ResDataButton(value, "进入副本" + value));
            }
            builder.append(new GeneralData.ResDataButton("副本邀请@", "副本邀请@"))
                    .append(new GeneralData.ResDataButton("当前队伍", "当前队伍"));
            return builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Action("join-fb")
    public Object joinFb(Character character, String qid, User user, ReqDataPack pack) {
        UsersResources resources = usersResourcesMapper.selectById(user.getUid());
        infoController.getAndCalculateE(user.getUid());
        if (resources.getEnergy() < EVE) return "灵力不足;每次进入副本消耗" + EVE + "灵力";
        if (manager.id2scenario.containsKey(qid)) {
            return "未结束副本状态";
        } else {
            if (character.getHp() <= 100) return "当前魂角状态过低.使用'查看'";
            GeneralData generalData = (GeneralData) pack.getArgs().get(GameClient.ODATA_KEY);
            String name = generalData.allText().trim();

            LivingEntity[] es = fbService.generationLivingEntity(name);
            if (es == null) return "不存在该副本";

            resources.setEnergy(resources.getEnergy() - EVE);
            resources.applyE(redisSource);
            usersResourcesMapper.updateById(resources);

            LivingEntity[] cis = new LivingEntity[0];
            if (manager.id2list.containsKey(qid)) {
                List<User> list = manager.id2list.get(qid);
                cis = new LivingEntity[list.size()];
                int n = 0;
                for (User u : list) {
                    Character c0 = charactersController.getCurrentCharacterOrLowestLevel(u.getUid());
                    CiBase base = CiBase.create(baseCi.compute(c0));
                    base.fid = u.getId();
                    cis[n++] = base;
                }
            } else {
                CiBase ciBase = CiBase.create(baseCi.compute(character));
                ciBase.fid = qid;
                cis = new LivingEntity[]{ciBase};
            }

            Scenario scenario = new ScenarioImpl(cis, es, manager) {
                @Override
                public void reward(int[] ids) {
                    if (ids.length == 0) {
                        pack.send(new GeneralData.ResDataText("挑战失败"));
                    } else {
                        StringBuilder sb = new StringBuilder("获得:");
                        for (int id : ids) {
                            if (resourceLoader.itemMap.containsKey(id)) {
                                Item item = resourceLoader.itemMap.get(id);
                                sb.append(item.getName()).append(",");
                                shopController.appendItemToBag(user, item, 1);
                            }
                        }
                        pack.send(new GeneralData.ResDataText(sb.toString()));
                    }
                    LivingEntity entity = getCurrentEntity(qid);
                    character.setHp(entity.getHp());
                    characterMapper.updateById(character);
                }
            };

            if (manager.id2list.containsKey(qid)) {
                for (User u : manager.id2list.get(qid)) {
                    manager.id2scenario.put(u.getId(), scenario);
                }
            } else {
                manager.id2scenario.put(qid, scenario);
            }
            executorService.submit(scenario);
            return scenario.getTips(contextManager);
        }
    }

    @Action("att")
    public Object att(String qid) {
        if (!manager.id2scenario.containsKey(qid)) {
            return "未处于副本状态";
        } else {
            Scenario scenario = manager.id2scenario.get(qid);
            LivingEntity currentEntity = scenario.getCurrentEntity(qid);
            if (currentEntity instanceof CiBase) {
                CiBase ciBase = (CiBase) currentEntity;
                if (!ciBase.prep) return "非行动方";
                if (ciBase.fid.equals(qid)) {
                    ciBase.op = AttEff.TYPE;
                    ciBase.cdl.countDown();
                }
            }
            return scenario.getTips(contextManager);
        }
    }

    @Action("jem")
    public Object jem(String qid) {
        if (!manager.id2scenario.containsKey(qid)) {
            return "未处于副本状态";
        } else {
            Scenario scenario = manager.id2scenario.get(qid);
            LivingEntity currentEntity = scenario.getCurrentEntity(qid);
            if (currentEntity instanceof CiBase) {
                CiBase ciBase = (CiBase) currentEntity;
                if (ciBase.fid.equals(qid)) {
                    ciBase.op = 0;
                    ciBase.cdl.countDown();
                }
            }
            return scenario.getTips(contextManager);
        }
    }

    @Action("evacuate")
    public Object evacuate(String qid) {
        if (!manager.id2scenario.containsKey(qid)) {
            return "未处于副本状态";
        } else {
            Scenario scenario = manager.id2scenario.get(qid);
            scenario.destroy(scenario.getCurrentEntities(qid));
            return "完成";
        }
    }
}
