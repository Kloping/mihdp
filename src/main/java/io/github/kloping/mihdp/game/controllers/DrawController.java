package io.github.kloping.mihdp.game.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.services.BaseService;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
import io.github.kloping.mihdp.utils.Utils;
import io.github.kloping.mihdp.wss.data.ResDataPack;
import io.github.kloping.number.NumberUtils;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author github.kloping
 */
@Controller
public class DrawController {
    public interface Drawer {
        BufferedImage draw(JSONObject o, ResDataPack pack);
    }

    public final Map<String, Drawer> ACTION2DRAWER = new HashMap<>();

    @Action("draw")
    public GeneralData.ResDataImage draw(ResDataPack dataPack) {
        String action = dataPack.getAction();
        if (ACTION2DRAWER.containsKey(action)) {
            GeneralData.ResDataText text = (GeneralData.ResDataText) dataPack.getData();
            String jsonD = text.getContent();
            BufferedImage image = ACTION2DRAWER.get(action).draw(JSON.parseObject(jsonD), dataPack);
            if (image != null) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    return new GeneralData.ResDataImage(baos.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    {
        ACTION2DRAWER.put("info", (o, p) -> {
            try {
                ClassPathResource classPathResource = new ClassPathResource("bg0.jpg");
                byte[] bytes = ReadUtils.readAll(classPathResource.getInputStream());
                bytes = ImageDrawerUtils.cropImage(bytes, new int[]{0, 0, 600, 300}, "jpg", "jpg");
                BufferedImage bg = ImageIO.read(new ByteArrayInputStream(bytes));
                BufferedImage icon0 = null;
                if (Judge.isEmpty(o.getString("icon"))) {
                    icon0 = ImageIO.read(new ClassPathResource("0.jpg").getInputStream());
                } else if (p.containsArgs(BaseService.BASE_ICON_ARGS)) {
                    icon0 = ImageIO.read(new URL(p.getArgValue(BaseService.BASE_ICON_ARGS).toString()));
                } else icon0 = ImageIO.read(new URL(o.getString("icon")));
                icon0 = ImageDrawerUtils.resizeImage(icon0, 220, 220);
                icon0 = ImageDrawerUtils.roundImage(icon0, 9999);
                bg = ImageDrawerUtils.putImage(bg, icon0, 15, 15);

                int x = 265, y = 50;

                Graphics2D g2 = (Graphics2D) bg.getGraphics();
                g2.setStroke(ImageDrawerUtils.STROKE2);
                g2.setFont(ImageDrawerUtils.SMALL_FONT38);
                g2.setColor(ImageDrawerUtils.ORIGIN_A75);
                g2.drawString("uid:" + o.get("uid"), x, y);
                ImageDrawerUtils.drawStringContinuousDiscoloration(g2, x, y + 53, o.getString("name"), ImageDrawerUtils.YELLOW_A85
                        , " Lv.", ImageDrawerUtils.BLUE2_A75, String.valueOf(o.get("level")), ImageDrawerUtils.RED_A75);

                y = 160;
                g2.setFont(ImageDrawerUtils.SMALL_FONT20);
                ImageDrawerUtils.drawStringContinuousDiscoloration(g2, x, y - 20, "经验:", ImageDrawerUtils.BLACK_A60, String.format("%s/%s", o.get("xp"), o.get("max")), Color.WHITE);

                g2.setColor(ImageDrawerUtils.BLACK_A85);
                g2.fillRoundRect(x - 5, y - 5, 310, 70, 30, 30);
                g2.setColor(ImageDrawerUtils.WHITE_A80);
                g2.fillRoundRect(x, y, 300, 60, 25, 25);

                int b = NumberUtils.toPercent(o.getInteger("xp"), o.getInteger("max"));

                if (b <= 25) g2.setColor(ImageDrawerUtils.YELLOW_A85);
                else if (b <= 50) g2.setColor(ImageDrawerUtils.BLUE3_A75);
                else if (b <= 75) g2.setColor(ImageDrawerUtils.GREEN_A85);
                else g2.setColor(ImageDrawerUtils.RED_A75);

                g2.fillRoundRect(x, y, b * 3, 60, 25, 25);

                g2.setColor(ImageDrawerUtils.WHITE_A80);
                g2.setFont(ImageDrawerUtils.SMALL_FONT22);
                g2.fillRoundRect(10, bg.getHeight() - 40, bg.getWidth() - 20, 37, 15, 15);
                String tips = "create by github@kloping";
                g2.setColor(ImageDrawerUtils.BLUE_A75);
                int tw = g2.getFontMetrics().stringWidth(tips);
                g2.drawString(tips, (bg.getWidth() - 10) / 2 - (tw / 2), bg.getHeight() - 15);
                g2.dispose();
                return bg;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
        ACTION2DRAWER.put("resource", (o, p) -> {
            try {
                ClassPathResource classPathResource = new ClassPathResource("bg0.jpg");
                BufferedImage image = ImageIO.read(classPathResource.getInputStream());
                image = ImageDrawerUtils.resizeImage(image, 500, 700);
                Graphics2D g2 = (Graphics2D) image.getGraphics();
                Graphics graphics = g2;

                int x = 5, y = 160;

                graphics.setColor(ImageDrawerUtils.BLACK_A35);
                graphics.drawRoundRect(x - 3, y + 2, 496, 56, 10, 10);

                g2.setFont(ImageDrawerUtils.SMALL_FONT38_TYPE0);
                ImageDrawerUtils.drawStringContinuousDiscoloration(g2, x, y + g2.getFont().getSize()
                        , "uid:" + o.get("uid"), ImageDrawerUtils.ORIGIN_A75
                        , "/", ImageDrawerUtils.BLACK_A85
                        , "lv." + o.get("level"), ImageDrawerUtils.BLUE2_A75
                        , "/", ImageDrawerUtils.BLACK_A85
                );
                x = 335;
                g2.setFont(ImageDrawerUtils.SMALL_FONT32);
                g2.drawImage(ImageIO.read(new ClassPathResource("gold.png").getInputStream()),
                        x, y, 45, 45, null);
                g2.setColor(ImageDrawerUtils.ORIGIN_A80);
                g2.drawString(":" + Utils.filterOverW(Integer.valueOf(o.get("gold").toString())), x + 50, y + g2.getFont().getSize() + 10);
                //line1 = end
                x = 5;
                y += 62;
                graphics.setColor(ImageDrawerUtils.BLACK_A35);
                graphics.drawRoundRect(x - 3, y, 496, 56, 10, 10);
                //==
                g2.setFont(ImageDrawerUtils.SMALL_FONT22);

                int y0 = y + g2.getFont().getSize();
                g2.setColor(ImageDrawerUtils.YELLOW_A85);
                g2.drawString("灵力:" + o.get("energy"), x, y0);
                g2.setColor(ImageDrawerUtils.BLACK_A75);
                g2.drawString(String.format("积分[存]:%s[%s]", Utils.filterOverW(o.getInteger("score")), Utils.filterOverW(o.getInteger("score0"))), x + 150, y0);
                y0 = y0 + g2.getFont().getSize() + 5;
                g2.setColor(ImageDrawerUtils.BLUE2_A75);
                g2.drawString(String.format("背包:%s", o.getJSONArray("bags").size()), x, y0);
                g2.setColor(ImageDrawerUtils.GREEN_A85);
                g2.drawString(String.format("角色:%s个", o.getJSONArray("characters").size()), x + 150, y0);

                g2.dispose();
                return image;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
