package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.MySpringTool.interfaces.component.ContextManager;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.GameStaticResourceLoader;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.csn.CiBase;
import io.github.kloping.mihdp.utils.ImageDrawer;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
import io.github.kloping.number.NumberUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author github.kloping
 */
public class ScenarioImpl implements Scenario {
    /**
     * 场景a方
     */
    LivingEntity[] as;
    /**
     * 场景b方
     */
    LivingEntity[] bs;
    /**
     * 全部
     */
    LivingEntity[] als;
    /**
     * 默认场景距离1w m
     */

    public static final Integer MAX_JOURNEY = 10000;


    private Boolean run = true;
    private ScenarioManager manager;

    public ScenarioImpl(LivingEntity[] as, LivingEntity[] bs, ScenarioManager manager) {
        this.as = as;
        this.bs = bs;
        this.manager = manager;
        List<LivingEntity> list = new LinkedList<>();
        for (LivingEntity e : bs) {
            list.add(e);
        }

        for (LivingEntity e : as) {
            list.add(e);
        }

        als = list.toArray(new LivingEntity[0]);
    }

    @Override
    public void run() {
        //循环 直至下一个实体行动
        while (run) {
            try {
                test0();
                allV1();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void test0() {
        boolean k = false;
        for (LivingEntity e : as) {
            if (e.getHp() != 0) k = true;
        }
        if (!k) destroy(as);
        k = false;
        for (LivingEntity e : bs) {
            if (e.getHp() != 0) k = true;
        }
        if (!k) destroy(bs);
    }

    private void step(LivingEntity e) {
        e.distance = e.distance - e.getSpeed().getFinalValue();
    }

    public void allV1() {
        for (LivingEntity e : als) {
            step(e);
        }
        letg();
    }

    private void letg() {
        LivingEntity min = null;
        boolean next = false;
        for (LivingEntity al : als) {
            if (al.distance <= 0) {
                if (min == null) min = al;
                else {
                    next = true;
                    if (al.distance < min.distance) {
                        min = al;

                    }
                }
            }
        }
        if (min != null) {
            Object o = min.letDoPre(this, cdl, getOrs(min));
            if (o instanceof String) {
                if (Judge.isNotEmpty(o.toString())) {
                    builder.append("\n").append(o.toString());
                }
            } else if (o instanceof GeneralData) {
                builder.append((GeneralData) o);
            }

            if (min instanceof CiBase) {
                pcdl.countDown();
            }

            o = min.letDo(this, cdl, getOrs(min));
            if (o instanceof String) {
                if (Judge.isNotEmpty(o.toString())) {
                    builder.append("\n").append(o.toString());
                }
            } else if (o instanceof GeneralData) {
                builder.append((GeneralData) o);
            }

            min.distance = MAX_JOURNEY;
        }
        if (next) letg();
    }

    private LivingEntity[] getOrs(LivingEntity min) {
        for (LivingEntity a : as) {
            if (a == min) return bs;
        }
        return as;
    }

    @Override
    public void destroy(LivingEntity[] as) {
        run = false;
        manager.remove(this);
    }


    private GeneralData.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder();

    /**
     * 进程cdl
     */
    public CountDownLatch cdl = new CountDownLatch(1);
    /**
     * 玩家cdl 当有玩家开始阻塞式行动时countDown()
     */
    public CountDownLatch pcdl = new CountDownLatch(1);

    @Override
    public Object out(ContextManager context) {
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getTips(context);
    }

    @Override
    public Object getTips(ContextManager context) {
        try {
            pcdl.await();
            pcdl = new CountDownLatch(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GeneralData.ResDataChain chain = builder.build();
        chain.getList().add(drawScenario(context));
        return chain;
    }

    @Override
    public LivingEntity[] getCurrentEntities(String id) {
        for (LivingEntity a : as) {
            if (a instanceof CiBase) {
                CiBase base = (CiBase) a;
                if (base.fid.equals(id)) return as;
            }
        }
        for (LivingEntity a : bs) {
            if (a instanceof CiBase) {
                CiBase base = (CiBase) a;
                if (base.fid.equals(id)) return bs;
            }
        }
        return new LivingEntity[0];
    }

    @Override
    public LivingEntity getCurrentEntity(String id) {
        for (LivingEntity al : als) {
            if (al instanceof CiBase) {
                CiBase base = (CiBase) al;
                if (base.fid.equals(id)) return al;
            }
        }
        return null;
    }

    private GeneralData drawScenario(ContextManager context) {
        try {
            GameStaticResourceLoader resourceLoader = context.getContextEntity(GameStaticResourceLoader.class);
            byte[] bytes = ReadUtils.readAll(new ClassPathResource(String.format("static.png")).getInputStream());
            ImageDrawer drawer = new ImageDrawer(bytes);
            int x = 50;
            for (LivingEntity a : as) {
                drawer.draw(resourceLoader.getFileById(a.getCid()), 200, 200, x, 300);
                int hbv = NumberUtils.toPercent(a.getHp(), a.getMaxHp().getFinalValue());
                drawer.fillRoundRect(ImageDrawerUtils.WHITE_A80, x + 200 - 20, 300, 20, 200, 5, 5);
                drawer.fillRoundRect(ImageDrawerUtils.GREEN_A75, x + 200 - 20, 300, 20, 2 * hbv, 5, 5);
                x += 200;
            }
            //80-240
            GeneralData.GeneralDataBuilder builder = new GeneralData.GeneralDataBuilder()
                    .append(new GeneralData.ResDataImage(drawer.bytes(), 900, 600))
                    .append(new GeneralData.ResDataButton("行动:攻击", "攻击"))
                    .append(new GeneralData.ResDataButton("行动:撤离", "撤离"));
            return builder.build();
        } catch (IOException e) {
            e.printStackTrace();
            return new GeneralData.ResDataText("绘图失败;" + e.getMessage());
        }
    }
}
