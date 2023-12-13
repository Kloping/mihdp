package data_migration.target;

import com.alibaba.druid.pool.DruidDataSource;
import data_migration.Starter;
import data_migration.dao.User;
import data_migration.dao.UserScore;
import data_migration.dao.UsersResources;
import data_migration.source.DataMigration;
import data_migration.target.mapper.UserMapper;
import data_migration.target.mapper.UsersResourcesMapper;
import io.github.kloping.rand.RandomUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = {"data_migration.target.mapper"})
@MapperScan("data_migration.target.mapper")
@Configuration
public class DataMigrationTarget {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DataMigrationTarget.class, args);
        Starter.userMapper = context.getBean(UserMapper.class);
        Starter.resourcesMapper = context.getBean(UsersResourcesMapper.class);
    }

    @Bean
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:sqlite:data.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");
        return dataSource;
    }
}