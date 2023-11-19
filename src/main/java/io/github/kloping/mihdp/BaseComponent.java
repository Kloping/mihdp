package io.github.kloping.mihdp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import io.github.kloping.MySpringTool.annotations.CommentScan;
import io.github.kloping.MySpringTool.h1.impl.LoggerImpl;
import io.github.kloping.MySpringTool.interfaces.Logger;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.DataDeserializer;
import io.github.kloping.mihdp.wss.data.ResData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author github-kloping
 * @date 2023-07-17
 */
@Configuration
@CommentScan(path = "io.github.kloping.mihdp.game")
public class BaseComponent {
    @Bean
    public Logger getLogger() {
        Logger l = new LoggerImpl();
        String path = String.format("logs/%s.log", new SimpleDateFormat("yyyy/MM-dd").format(new Date()));
        new File(path).getParentFile().mkdirs();
        l.setOutFile(path);
        l.info("================logger=create===================");
        return l;
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<ResData> deserializer = new DataDeserializer();
        gsonBuilder.registerTypeAdapter(ResData.class, deserializer);
        return gsonBuilder.create();
    }

    @Bean
    public String password(@Value("${wss.password:123456}") String pwd) {
        return GameClient.PASS_WORD = pwd;
    }
}
