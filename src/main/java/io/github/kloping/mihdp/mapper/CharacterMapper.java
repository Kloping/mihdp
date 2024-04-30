package io.github.kloping.mihdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.kloping.mihdp.dao.Character;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author github.kloping
 */
@Mapper
public interface CharacterMapper extends BaseMapper<Character> {
    @Select("SELECT * FROM character where uid=#{uid} AND cid=#{cid};")
    Character getOne(@Param("uid") String uid, @Param("cid") Integer cid);
}
