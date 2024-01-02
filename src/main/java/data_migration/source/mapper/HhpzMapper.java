package data_migration.source.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author github.kloping
 */
@Mapper
public interface HhpzMapper {
    /**
     * insert a
     *
     * @param qid
     * @param oid
     * @param time
     * @return
     */
    @Insert("INSERT INTO `hhpz` (`qid`, `oid`, `p`, `time`) VALUES (#{qid}, #{oid}, #{p}, #{time});")
    Integer insert(@Param("qid") Long qid, @Param("oid") Integer oid, @Param("p") Integer p, @Param("time") Long time);

    /**
     * select hh
     *
     * @param qid
     * @return
     */
    @Select("SELECT `oid` FROM `hhpz` WHERE `qid`=#{qid} AND `p`=#{p} AND `state`=0 ORDER BY `time`;")
    List<Integer> select(@Param("qid") Long qid, @Param("p") Integer p);

    /**
     * delete
     *
     * @param id
     * @return
     */
    @Update("DELETE FROM `hhpz` WHERE `id`=#{id} AND `p`=#{p}")
    Integer delete(@Param("id") Integer id,@Param("p") Integer p);

    /**
     * update oid
     *
     * @param id
     * @param oid
     * @return
     */
    @Update("UPDATE `hhpz` SET `oid`=#{oid} WHERE `id`=#{id} AND `p`=#{p}")
    Integer update(@Param("id") Integer id, @Param("oid") Integer oid, @Param("p") Integer p);

    /**
     * select ids
     *
     * @param qid
     * @return
     */
    @Select("SELECT `id` FROM `hhpz` WHERE `qid`=#{qid} AND `p`=#{p} AND `state`=0 ORDER BY `time`;")
    List<Integer> selectIds(@Param("qid") Long qid, @Param("p") Integer p);
}
