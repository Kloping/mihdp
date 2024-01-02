package data_migration.target;

import com.alibaba.druid.pool.DruidDataSource;
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

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(DataMigrationTarget.class, args);
    }

    @Bean
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:sqlite:data.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");
        return dataSource;
    }
}