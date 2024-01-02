package data_migration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import data_migration.dao.Character;
import data_migration.dao.Cycle;
import data_migration.dao.User;
import data_migration.dao.WhInfo;
import data_migration.source.DataMigration;
import data_migration.source.mapper.HhpzMapper;
import data_migration.source.mapper.WhInfoMapper;
import data_migration.target.DataMigrationTarget;
import data_migration.target.mapper.CharacterMapper;
import data_migration.target.mapper.CycleMapper;
import data_migration.target.mapper.UserMapper;
import io.github.kloping.rand.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @author github.kloping
 */
public class Starter {

    public static void main(String[] args) throws Exception{
        CountDownLatch cdl = new CountDownLatch(2);
        new Thread(() -> {
            DataMigration.main(args);
            cdl.countDown();
        }).start();
        new Thread(() -> {
            DataMigrationTarget.main(args);
            cdl.countDown();
        }).start();
        cdl.await();

        WhInfoMapper whInfoMapper = DataMigration.context.getBean(WhInfoMapper.class);
        HhpzMapper hhpzMapper = DataMigration.context.getBean(HhpzMapper.class);

        CharacterMapper charactersMapper = DataMigrationTarget.context.getBean(CharacterMapper.class);
        CycleMapper cycleMapper = DataMigrationTarget.context.getBean(CycleMapper.class);
        UserMapper userMapper = DataMigrationTarget.context.getBean(UserMapper.class);

        QueryWrapper<WhInfo> whInfoQueryWrapper = new QueryWrapper<>();
        whInfoQueryWrapper.gt("level", 20);

        for (WhInfo whInfo : whInfoMapper.selectList(whInfoQueryWrapper)) {
            Character character = new Character();
            character.setCid(RandomUtils.RANDOM.nextInt(1) + 1001);
            character.setLevel(whInfo.getLevel());
            User user = userMapper.selectById(whInfo.getQid());
            if (user != null) {
                character.setUid(user.getUid());
            } else {
                System.err.println("未查询到: " + whInfo.getQid());
                continue;
            }
            character.setMid(0).setHp(100);
            charactersMapper.insert(character);
            for (Integer o1 : hhpzMapper.select(whInfo.getQid(), whInfo.getP())) {
                Cycle cycle = new Cycle();
                cycle.setCid(character.getId());
                int id0 = o1 - 200;
                cycle.setOid(2000 + id0);
                                                       cycleMapper.insert(cycle);
            }
            System.out.println("完成: " + whInfo.getQid());
        }
    }
}
