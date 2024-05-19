package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.MySpringTool.annotations.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景基础管理器
 *
 * @author github.kloping
 */
@Entity
public class ScenarioManager {
    public Map<String, Scenario> id2scenario = new ConcurrentHashMap<>();
}
