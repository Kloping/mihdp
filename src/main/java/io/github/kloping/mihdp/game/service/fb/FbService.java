package io.github.kloping.mihdp.game.service.fb;

import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.game.service.LivingEntity;
import io.github.kloping.mihdp.game.service.csn.GoBase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author github.kloping
 */
@Entity
public class FbService {
    /**
     * 副本名称 对应 最小等级限制
     */
    public final Map<String, Integer> NAME2LEVEL_MIN = new HashMap<>();

    //数据初始化
    {
        NAME2LEVEL_MIN.put("原始森林", 1);
        NAME2LEVEL_MIN.put("荒野森林", 20);
        NAME2LEVEL_MIN.put("星斗森林", 60);
        NAME2LEVEL_MIN.put("落日森林", 80);
        NAME2LEVEL_MIN.put("极北之地", 110);
        NAME2LEVEL_MIN.put("神界之地", 130);
    }

    public LivingEntity[] generationLivingEntity(String text) {
        AtomicReference<Integer> level = new AtomicReference<>(null);
        NAME2LEVEL_MIN.forEach((k, v) -> {
            if (text.endsWith(k)) level.set(v);
        });
        if (level.get() == null) return null;
        return new LivingEntity[]{GoBase.create(1, 1)};
    }
}
