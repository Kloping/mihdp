package io.github.kloping.mihdp.game.scenario;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @AutoStand
    UserMapper userMapper;

    /**
     * id => list
     */
    public Map<String, List<User>> id2list = new HashMap<>();

    public boolean invite(String id, String id0) {
        List<User> list;
        if (id2list.containsKey(id)) {
            list = id2list.get(id);
        } else if (id2list.containsKey(id0)) {
            list = id2list.get(id0);
        } else list = new ArrayList<>();
        if (list.size() >= 4) return false;
        User user = userMapper.selectById(id);
        if (user == null) return false;
        list.add(user);
        user = userMapper.selectById(id0);
        if (user == null) return false;
        list.add(user);
        id2list.put(id, list);
        id2list.put(id0, list);
        return true;
    }

    public boolean outTeam(User user) {
        if (id2list.containsKey(user.getId())) {
            List<User> list = id2list.get(user.getId());
            list.remove(user);
            id2list.remove(user.getId());
            return true;
        } else return false;
    }
}
