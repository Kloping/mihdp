package data_migration.source;

import com.alibaba.druid.pool.DruidDataSource;
import data_migration.Starter;
import data_migration.source.mapper.UserScoreMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author github.kloping
 */
@SpringBootApplication(scanBasePackages = {"data_migration.source.mapper"})
@MapperScan("data_migration.source.mapper")
@Configuration
public class DataMigration {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DataMigration.class, args);
        Starter.userScoreMapper = context.getBean(UserScoreMapper.class);
    }

    @Bean
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/gdb0?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }
}
