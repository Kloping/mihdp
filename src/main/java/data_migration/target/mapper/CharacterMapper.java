package data_migration.target.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import data_migration.dao.Character;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author github.kloping
 */
@Mapper
public interface CharacterMapper extends BaseMapper<Character> {
}
