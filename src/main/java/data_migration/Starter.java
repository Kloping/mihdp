package data_migration;

import data_migration.source.DataMigration;
import data_migration.target.DataMigrationTarget;
import io.github.kloping.mihdp.dao.Bag;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.mapper.UserMapper;

import java.util.HashMap;
import java.util.Map;
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

//        DataMigrationTarget.main(args);


        data_migration.source.mapper.BagMapper bagMapper0 = DataMigration.context.getBean(data_migration.source.mapper.BagMapper.class);
        io.github.kloping.mihdp.mapper.BagMaper bagMapper1 = DataMigrationTarget.context.getBean(io.github.kloping.mihdp.mapper.BagMaper.class);
        UserMapper userMapper = DataMigrationTarget.context.getBean(UserMapper.class);
        for (User user : userMapper.selectList(null)) {
            String qidStr = user.getId();
            try {
                Long qid = Long.valueOf(qidStr);
                Map<Integer, Integer> id2n = new HashMap<>();
                for (Integer id : bagMapper0.selectAll(qid)) {
                    if (id2n.containsKey(id)) {
                        Integer n = id2n.get(id);
                        n++;
                        id2n.put(id, n);
                    } else id2n.put(id, 1);
                }
                id2n.forEach((id, n) -> {
                    Integer ide = id + 1800;
                    bagMapper1.insert(new Bag(user.getUid(), ide, n, n));
                    System.out.format("qid(%s-%s) %s fix %s\n", qid, user.getUid(), ide, n);
                });
            } catch (NumberFormatException e) {
                System.err.println(e);
            }
        }

    }

//    private static void extractedWhHh() {
//        WhInfoMapper whInfoMapper = DataMigration.context.getBean(WhInfoMapper.class);
//        HhpzMapper hhpzMapper = DataMigration.context.getBean(HhpzMapper.class);
//
//        CharacterMapper charactersMapper = DataMigrationTarget.context.getBean(CharacterMapper.class);
//        CycleMapper cycleMapper = DataMigrationTarget.context.getBean(CycleMapper.class);
//        UserMapper userMapper = DataMigrationTarget.context.getBean(UserMapper.class);
//
//        QueryWrapper<WhInfo> whInfoQueryWrapper = new QueryWrapper<>();
//        whInfoQueryWrapper.gt("level", 20);
//
//        for (WhInfo whInfo : whInfoMapper.selectList(whInfoQueryWrapper)) {
//            Character character = new Character();
//            character.setCid(RandomUtils.RANDOM.nextInt(1) + 1001);
//            character.setLevel(whInfo.getLevel());
//            User user = userMapper.selectById(whInfo.getQid());
//            if (user != null) {
//                character.setUid(user.getUid());
//            } else {
//                System.err.println("未查询到: " + whInfo.getQid());
//                continue;
//            }
//            character.setMid(0).setHp(100);
//            charactersMapper.insert(character);
//            for (Integer o1 : hhpzMapper.select(whInfo.getQid(), whInfo.getP())) {
//                Cycle cycle = new Cycle();
//                cycle.setCid(character.getId());
//                int id0 = o1 - 200;
//                cycle.setOid(2000 + id0);
//                cycleMapper.insert(cycle);
//            }
//            System.out.println("完成: " + whInfo.getQid());
//        }
//    }
}
