package data_migration.target.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.kloping.mihdp.dao.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author github.kloping
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select(value = "SELECT uid FROM `user` ORDER BY `uid` DESC LIMIT 0,1")
    String selectMaxUid();
}
