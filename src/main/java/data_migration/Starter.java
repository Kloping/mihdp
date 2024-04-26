package data_migration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import data_migration.target.DataMigrationTarget;
import io.github.kloping.mihdp.dao.Character;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.mapper.CharacterMapper;
import io.github.kloping.mihdp.mapper.UserMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * @author github.kloping
 */
public class Starter {

    public static void main(String[] args) throws Exception{
//        CountDownLatch cdl = new CountDownLatch(2);
//        new Thread(() -> {
//            DataMigration.main(args);
//            cdl.countDown();
//        }).start();
//        new Thread(() -> {
//            DataMigrationTarget.main(args);
//            cdl.countDown();
//        }).start();
//        cdl.await();

        DataMigrationTarget.main(args);

        CharacterMapper characterMapper = DataMigrationTarget.context.getBean(CharacterMapper.class);
        UserMapper userMapper = DataMigrationTarget.context.getBean(UserMapper.class);
        BufferedReader reader = new BufferedReader(new FileReader(new File("./libs/temp.csv")));
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                String[] sss = line.split(";");
                String qid = sss[0];
                String wh = sss[1];
                String p = sss[2];
                User user = userMapper.selectById(qid);
                if (user == null) continue;
                if ("1".equals(p)) {
                    QueryWrapper<Character> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("uid", user.getUid());
                    int i = 0;
                    List<Character> list = characterMapper.selectList(queryWrapper);
                    if (list == null || list.isEmpty()) continue;
                    for (Character character : list) {
                        if (i == 0) {
                            character.setCid(1000 + Integer.valueOf(wh));
                            queryWrapper.eq("cid", character.getId());
                            characterMapper.updateById(character);
                            System.out.println("ok for " + character.getId() + " of " + qid);
                        } else {
                            System.err.println("2 for " + character.getId() + "to");
                        }
                        i++;
                    }
                } else {
                    System.err.println(line + "for" + user.getUid());
                }
            } else break;
        }
//
//        data_migration.source.mapper.BagMapper bagMapper0 = DataMigration.context.getBean(data_migration.source.mapper.BagMapper.class);
//        data_migration.target.mapper.BagMaper bagMapper1 = DataMigrationTarget.context.getBean(data_migration.target.mapper.BagMaper.class);
//        UserMapper userMapper = DataMigrationTarget.context.getBean(UserMapper.class);
//        for (User user : userMapper.selectList(null)) {
//            String qidStr = user.getId();
//            try {
//                Long qid = Long.valueOf(qidStr);
//                int num = 0;
//                for (Integer i : bagMapper0.selectAll(qid)) {
//                    if (i == 103) num++;
//                }
//                bagMapper1.insert(new Bag(user.getUid(), 101, num, num));
//                System.out.format("qid(%s) o103 to n101 %s\n", qid, user.getUid(), num);
//            } catch (NumberFormatException e) {
//                System.err.println(e);
//            }
//        }

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
