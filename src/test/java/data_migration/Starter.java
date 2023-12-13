package data_migration;

import data_migration.dao.User;
import data_migration.dao.UserScore;
import data_migration.dao.UsersResources;
import data_migration.source.DataMigration;
import data_migration.source.mapper.UserScoreMapper;
import data_migration.target.DataMigrationTarget;
import data_migration.target.mapper.UserMapper;
import data_migration.target.mapper.UsersResourcesMapper;
import io.github.kloping.rand.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @author github.kloping
 */
public class Starter {
    public static UserMapper userMapper;
    public static UsersResourcesMapper resourcesMapper;
    public static UserScoreMapper userScoreMapper;

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
        for (UserScore userScore : userScoreMapper.selectAll()) {
            try {
                //获得最小uid
                String uid = userMapper.selectMaxUid();
                if (uid == null) {
                    uid = "1000001";
                }
                //加随机
                Long tuid = Long.valueOf(uid);
                tuid = tuid + RandomUtils.RANDOM.nextInt(10) + 1;

                User user = new User();
                user.setId(userScore.getId());
                user.setUid(String.valueOf(tuid));
                user.setXp(userScore.getXp());
                user.setLevel(userScore.getLevel());
                user.setReg(System.currentTimeMillis());
                userMapper.insert(user);


                UsersResources resources = new UsersResources();
                resources.setScore(userScore.getScore());
                resources.setScore0(userScore.getScore0());
                resources.setFz(userScore.getFz());
                resources.setDays(userScore.getDays());
                resources.setDay(userScore.getDay());
                resources.setK(userScore.getK());
                resources.setUid(user.getUid());

                resourcesMapper.insert(resources);
                System.err.println("finish => " + userScore.getId() + " (" + user.getUid());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("on " + userScore.getId());
            }
        }
    }
}
