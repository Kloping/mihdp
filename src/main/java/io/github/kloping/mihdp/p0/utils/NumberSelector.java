package io.github.kloping.mihdp.p0.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
public class NumberSelector {
    public static Map<String, NumberSelector> DATA_MAP = new HashMap<>();

    private NumberSelector pre;

    @Getter
    private NumberSelector next;

    private String id;

    private Map<Integer, SelectorInvoke> map = new HashMap<>();

    public static NumberSelector reg(String id) {
        if (DATA_MAP.containsKey(id)) return null;
        NumberSelector selector = new NumberSelector();
        selector.id = id;
        DATA_MAP.put(id, selector);
        return selector;
    }

    public static void clear(String id) {
        DATA_MAP.remove(id);
    }

    public void clear() {
        DATA_MAP.remove(id);
    }

    public NumberSelector set(Integer i, SelectorInvoke invoke) {
        map.put(i, invoke);
        return this;
    }

    public SelectorInvoke get(int i) {
        if (this.map.containsKey(i))
            return this.map.get(i);
        else if (this.pre != null)
            return this.pre.get(i);
        return null;
    }

    public NumberSelector next() {
        NumberSelector selector = new NumberSelector();
        selector.id = this.id;
        this.next = selector;
        selector.pre = this;
        return selector;
    }

    public NumberSelector next(int i, SelectorInvoke invoke) {
        return next().set(i, invoke);
    }
}
