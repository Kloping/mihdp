package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.game.service.csn.CiBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 场景基础管理器
 *
 * @author github.kloping
 */
@Entity
public class ScenarioManager {
    public Map<String, Scenario> id2scenario = new ConcurrentHashMap<>();

    public void remove(Scenario scenario) {
        AtomicReference<String> id = new AtomicReference<>();
        id2scenario.forEach((k, v) -> {
            id.set(k);
        });
        if (id.get() != null) id2scenario.remove(id.get());
    }
}
