package io.github.kloping.mihdp.game.v.v2;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Before;
import io.github.kloping.MySpringTool.annotations.Controller;
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
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
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
    CharactersController charactersController;

    public static final Integer EVE = 30;

    @Before
    public Object before(ReqDataPack dataPack) {
        User user = userMapper.selectById(dataPack.getSender_id());
        if (user == null) user = beginController.regNow0(dataPack.getSender_id());
        return new Object[]{
                charactersController.getCurrentCharacterOrLowestLevel(user.getUid()),
                dataPack.getSender_id(), userMapper.selectById(dataPack.getSender_id())};
    }

    {
//        BaseService.MSG2ACTION.put("进入副本", "join-fb");
//        BaseService.MSG2ACTION.put("攻击", "att");
//        BaseService.MSG2ACTION.put("撤离", "evacuate");
//        BaseService.MSG2ACTION.put("副本列表", "fb-list");
    }

    @Action("fb-list")
    public Object fbList(Character character, String qid) {
        try {
            byte[] bytes = ReadUtils.readAll(new ClassPathResource("fb-list.jpg").getInputStream());
            return new GeneralData.ResDataChain.GeneralDataBuilder()
                    .append("每次进入副本消耗" + EVE + "灵力")
                    .append(new GeneralData.ResDataImage(bytes, 215, 350))
                    .append(new GeneralData.ResDataButton("原始森林","进入副本原始森林"))
                    .append(new GeneralData.ResDataButton("荒野森林","进入副本荒野森林"))
                    .append(new GeneralData.ResDataButton("星斗森林","进入副本星斗森林"))
                    .append(new GeneralData.ResDataButton("落日森林","进入副本落日森林"))
                    .append(new GeneralData.ResDataButton("极北之地","进入副本极北之地"))
                    .append(new GeneralData.ResDataButton("神界之地","进入副本神界之地"))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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

            CiBase ciBase = CiBase.create(baseCi.compute(character));
            ciBase.fid = qid;
            Scenario scenario = new ScenarioImpl(new LivingEntity[]{ciBase}, es, manager) {
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
            manager.id2scenario.put(qid, scenario);
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
                if (ciBase.fid.equals(qid)) {
                    ciBase.op = AttEff.TYPE;
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
