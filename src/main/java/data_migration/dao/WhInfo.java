package data_migration.dao;


import lombok.Data;

/**
 * @author github-kloping
 */
@Data
public class WhInfo {
    /**
     * 攻击
     */
    public Long att = 10L;
    /**
     * 魂力
     */
    public Long hl = 100L;
    /**
     * 魂力最大值
     */
    public Long hll = 100L;
    /**
     * 血量
     */
    public Long hp = 100L;
    /**
     * 最大血量
     */
    public Long hpl = 100L;
    /**
     * 精神力
     */
    public Long hj = 100L;
    /**
     * 最大精神力
     */
    public Long hjL = 100L;
    /**
     * 等级
     */
    public Integer Level = 1;
    /**
     * 武魂
     */
    public Integer wh = 0;
    /**
     * 武魂类型
     */
    public Integer whType = -1;
    /**
     * 经验
     */
    public Long xp = 0L;
    public Long xpL = 100L;
    /**
     * 主人
     */
    public Long qid;

    public Integer p;
}