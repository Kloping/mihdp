package data_migration.source.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import data_migration.dao.WhInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author github-kloping
 */
@Repository
public interface WhInfoMapper extends BaseMapper<WhInfo> {
    /**
     * get排名
     *
     * @param num
     * @return
     */
    @Select("SELECT * FROM wh_info ORDER BY `level` DESC,`xp` DESC LIMIT #{num}")
    List<WhInfo> getOrderByLevel(@Param("num") Integer num);
}
