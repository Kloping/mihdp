package io.github.kloping.mihdp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.kloping.MySpringTool.StarterObjectApplication;
import io.github.kloping.MySpringTool.h1.impl.component.FieldManagerImpl;
import io.github.kloping.MySpringTool.h1.impl.component.PackageScannerImpl;
import io.github.kloping.MySpringTool.interfaces.Logger;
import io.github.kloping.MySpringTool.interfaces.component.ContextManager;
import io.github.kloping.MySpringTool.interfaces.component.FieldManager;
import io.github.kloping.MySpringTool.interfaces.component.PackageScanner;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.GameWebSocketServer;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.mihdp.ex.GeneralData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author github.kloping
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Main implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    Logger logger;

    @Autowired
    GameWebSocketServer gameWebSocketServer;

    @Override
    public void run(String... args) throws Exception {
        gameWebSocketServer.start();
        logger.info("start auto create need tables;");
        PackageScanner scanner = new PackageScannerImpl(true);
        for (Class<?> dclass : scanner.scan(Main.class, Main.class.getClassLoader(), "io.github.kloping.mihdp.dao")) {
            String sql = Utils.CreateTable.createTable(dclass);
            try {
                int state = jdbcTemplate.update(sql);
                if (state > 0) System.out.println(sql);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(sql);
            }
        }
        logger.info("tables create finished");

        logger.info("tables update");

    }

    public static StarterObjectApplication APPLICATION = null;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        APPLICATION = new StarterObjectApplication(BaseComponent.class);
        APPLICATION.setMainKey(String.class);
        APPLICATION.setAccessTypes(ReqDataPack.class, GameClient.class, GeneralData.class);
        APPLICATION.setWaitTime(60000);
        APPLICATION.run0(BaseComponent.class);
        ContextManager contextManager = APPLICATION.INSTANCE.getContextManager();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            Object obj = context.getBean(beanDefinitionName);
            if (obj == null) continue;
            if (obj instanceof BaseMapper) {
                contextManager.append(obj);
            }
        }
        FieldManager fieldManager = APPLICATION.INSTANCE.getFieldManager();
        if (fieldManager instanceof FieldManagerImpl) {
            FieldManagerImpl fm = (FieldManagerImpl) fieldManager;
            APPLICATION.logger.setLogLevel(3);
            fm.workStand();
            APPLICATION.logger.setLogLevel(0);
        }
    }
}
