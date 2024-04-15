package io.github.kloping.mihdp;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.gson.Gson;
import io.github.kloping.MySpringTool.StarterObjectApplication;
import io.github.kloping.MySpringTool.h1.impl.component.PackageScannerImpl;
import io.github.kloping.MySpringTool.interfaces.Logger;
import io.github.kloping.MySpringTool.interfaces.component.ContextManager;
import io.github.kloping.MySpringTool.interfaces.component.PackageScanner;
import io.github.kloping.judge.Judge;
import io.github.kloping.map.MapUtils;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.utils.Utils;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.GameWebSocketServer;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动
 * @author github.kloping
 */
@SpringBootApplication(scanBasePackages = "io.github.kloping.mihdp")
@EnableAsync
@EnableScheduling
public class MihDpMain implements CommandLineRunner {
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
        for (Class<?> dclass : scanner.scan(MihDpMain.class, MihDpMain.class.getClassLoader(), "io.github.kloping.mihdp.dao")) {
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
        boolean k0 = false;
        for (Map<String, Object> e0 : jdbcTemplate.queryForList("pragma table_info ('character')")) {
            String name = e0.get("name").toString();
            if ("xp".equals(name)) {
                k0 = true;
            }
        }
        if (!k0) {
            System.err.println("character 添加字段");
            jdbcTemplate.update("ALTER TABLE character ADD `xp` INTEGER DEFAULT 0;");
        }
    }

    public static final String[] REQUIRED_PROPERTIES = {"language"};

    public static StarterObjectApplication APPLICATION = null;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MihDpMain.class, args);
        APPLICATION = new StarterObjectApplication(BaseComponent.class);
        APPLICATION.setMainKey(String.class);
        APPLICATION.setWaitTime(60000);
        APPLICATION.setAccessTypes(ReqDataPack.class, GameClient.class, GeneralData.class);
        APPLICATION.logger = context.getBean(Logger.class);
        APPLICATION.INSTANCE.getPRE_SCAN_RUNNABLE().add(() -> {
            final String hostKey = "spt.redis.host";
            final String portKey = "spt.redis.port";
            String host = context.getEnvironment().getProperty(hostKey);
            String port = context.getEnvironment().getProperty(portKey);
            ContextManager contextManager = APPLICATION.INSTANCE.getContextManager();
            APPLICATION.INSTANCE.getContextManager().append(String.class, host, hostKey);
            APPLICATION.INSTANCE.getContextManager().append(Integer.class, Integer.valueOf(port), portKey);
            for (String beanDefinitionName : context.getBeanDefinitionNames()) {
                Object obj = context.getBean(beanDefinitionName);
                if (obj == null) continue;
                if (obj instanceof BaseMapper) {
                    contextManager.append(obj);
                } else if (obj instanceof JSONObject) {
                    contextManager.append(obj, beanDefinitionName);
                } else if (obj instanceof LanguageConfig) {
                    contextManager.append(obj, beanDefinitionName);
                } else if (obj instanceof Gson) {
                    contextManager.append(obj, beanDefinitionName);
                }
            }
            for (String requiredProperty : REQUIRED_PROPERTIES) {
                String v = context.getEnvironment().getProperty(requiredProperty);
                if (Judge.isNotEmpty(v)) contextManager.append(v, requiredProperty);
            }
        });

        boolean p = false;
        if (p) APPLICATION.STARTED_RUNNABLE.add(() -> {
            Map<String, List<String>> map = new HashMap<>();
            BaseService.MSG2ACTION.forEach((k, v) -> {
                MapUtils.append(map, v, k);
            });
            map.forEach((k, v) -> {
                System.out.print("| " + k);
                for (String s : v) {
                    System.out.print(" |" + s);
                }
                System.out.println("  |");
            });
        });
        APPLICATION.run0(BaseComponent.class);
    }
}
