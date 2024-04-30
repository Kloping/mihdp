package io.github.kloping.mihdp;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.kloping.MySpringTool.annotations.CommentScan;
import io.github.kloping.MySpringTool.h1.impl.LoggerImpl;
import io.github.kloping.MySpringTool.interfaces.Logger;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.dao.BaseCharacterInfo;
import io.github.kloping.mihdp.game.dao.ser.BaseCharacterInfoDeserializer;
import io.github.kloping.mihdp.game.dao.CharacterInfo;
import io.github.kloping.mihdp.game.dao.ser.CharacterInfoDeserializer;
import io.github.kloping.mihdp.utils.LanguageConfig;
import io.github.kloping.mihdp.wss.GameClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author github-kloping
 * @date 2023-07-17
 */
@Configuration
@CommentScan(path = "io.github.kloping.mihdp")
public class BaseComponent implements CommandLineRunner {

    @io.github.kloping.MySpringTool.annotations.Bean
    @Bean
    public JSONObject defaultConfig() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("default.json");
            String json = ReadUtils.readAll(is, "utf-8");
            return JSONObject.parseObject(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Logger getLogger() {
        Logger l = new LoggerImpl();
        l.setPrefix("[mihdp-v1]");
        String path = String.format("logs/%s.log", new SimpleDateFormat("yyyy/MM-dd").format(new Date()));
        new File(path).getParentFile().mkdirs();
        l.setOutFile(path);
        l.info("================logger=create===================");
        return l;
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(GeneralData.class, new GeneralData.GeneralDataDeserializer());
        gsonBuilder.registerTypeAdapter(BaseCharacterInfo.class, new BaseCharacterInfoDeserializer());
        gsonBuilder.registerTypeAdapter(CharacterInfo.class, new CharacterInfoDeserializer());
        return gsonBuilder.create();
    }

    @Bean
    public LanguageConfig languagesConfig(@Value("${m-language:zh}") String local) {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("languages.json");
            String json = ReadUtils.readAll(is, "utf-8");
            JSONObject data = JSONObject.parseObject(json);
            return new LanguageConfig(local, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public String password(@Value("${wss.password:123456}") String pwd) {
        return GameClient.PASS_WORD = pwd;
    }

    /**
     * 初始化数据
     *
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

    }
}
