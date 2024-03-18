package data_migration.target.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import data_migration.target.dao.Bag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author github.kloping
 */
@Mapper
public interface BagMaper extends BaseMapper<Bag> {
    @Select("SELECT * FROM `bag` WHERE `uid`=#{uid}")
    List<Bag> selectByUid(@Param("uid") String uid);

    @Select("SELECT * FROM `bag` WHERE `uid`=#{uid} AND `rid`=#{rid}")
    List<Bag> selectByUidAndRid(@Param("uid") String uid, @Param("rid") Integer rid);
}
