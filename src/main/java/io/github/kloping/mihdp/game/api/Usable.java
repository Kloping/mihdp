package io.github.kloping.mihdp.game.api;

/**
 * 所有可以用的
 *
 * @author github.kloping
 */
public interface Usable {
    /**
     * 使用
     *
     * @param active  主动使用者
     * @param passive 被动使用者
     * @return
     */
    boolean use(User active, User passive);
}
