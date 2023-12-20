package io.github.kloping.mihdp.p0;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.date.DateUtils;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.dao.UsersResources;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
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
            JSONObject data = null;
            try {
                data = JSON.parseObject(jsonD);
            } catch (com.alibaba.fastjson.JSONException je) {
                System.err.println("跳过");
            }
            if (data != null) {
                BufferedImage image = ACTION2DRAWER.get(action).draw(data, dataPack);
                if (image != null) {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        return new GeneralData.ResDataImage(baos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else return null;
        }
        return null;
    }

    {
        ACTION2DRAWER.put("info", (o, p) -> info(o, "信息获取成功!", true));
        ACTION2DRAWER.put("sign", (o, p) -> info(o, o.getString("tips"), o.getBoolean("t")));
        ACTION2DRAWER.put("work0", (o, p) -> info(o, o.getString("tips"), o.getBoolean("t")));
        ACTION2DRAWER.put("get0", (o, p) -> info(o, o.getString("tips"), o.getBoolean("t")));
        ACTION2DRAWER.put("put0", (o, p) -> info(o, o.getString("tips"), o.getBoolean("t")));
        ACTION2DRAWER.put("put0", (o, p) -> info(o, o.getString("tips"), o.getBoolean("t")));
        ACTION2DRAWER.put("trans0", (o, p) -> info(o, o.getString("tips"), o.getBoolean("t")));
    }


    /**
     * @param o
     * @param tips 成功或失败的提示
     * @param t    成功或失败
     * @return
     */
    public static final BufferedImage info(JSONObject o, String tips, Boolean t) {
        try {
            int xp = o.getInteger("xp");
            int max = o.getInteger("max");
            String icon = o.getString("icon");
            String name = o.getString("name");
            User user = o.toJavaObject(User.class);
            UsersResources resources = o.toJavaObject(UsersResources.class);

            ClassPathResource classPathResource = new ClassPathResource("bg3.jpg");
            byte[] bytes = ReadUtils.readAll(classPathResource.getInputStream());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            image = ImageDrawerUtils.resizeImage(image, 800, 1000);
            BufferedImage icon0;
            if (Judge.isEmpty(icon)) {
                ClassPathResource iconr = new ClassPathResource("sources/0.jpg");
                icon0 = ImageIO.read(iconr.getInputStream());
                icon0 = ImageDrawerUtils.resizeImage(icon0, 180, 180);
            } else icon0 = ImageDrawerUtils.readImage(new URL(icon), 180, 180);

            icon0 = ImageDrawerUtils.roundImage(icon0, 999);
            image = ImageDrawerUtils.putImage(image, icon0, 310, 50);
            Graphics graphics = image.getGraphics();

            graphics.setColor(ImageDrawerUtils.BLACK_A75);
            graphics.drawString(String.format("%s/%s", xp, max), 150, 220);
            graphics.setColor(Color.WHITE);
            graphics.drawRoundRect(150, 240, 500, 20, 5, 5);
            int r0 = NumberUtils.toPercent(xp, max);
            if (r0 < 30) graphics.setColor(ImageDrawerUtils.YELLOW_A75);
            else if (r0 < 60) graphics.setColor(Color.GREEN);
            else graphics.setColor(ImageDrawerUtils.ORIGIN_A75);
            graphics.fillRoundRect(150, 240, r0 * 5, 20, 5, 5);

            graphics.setColor(ImageDrawerUtils.BLACK_A45);
            graphics.drawRoundRect(280, 270, 240, 40, 40, 20);

            graphics.setFont(ImageDrawerUtils.SMALL_FONT22);
            graphics.drawString(name, (image.getWidth() - graphics.getFontMetrics().stringWidth(name)) / 2, 300);

            int x = 0, y = 0;
            //========line-1-start====
            y = 380;
            graphics.setFont(ImageDrawerUtils.SMALL_FONT24);

            graphics.setColor(ImageDrawerUtils.GREEN_A75);
            graphics.drawString("当前积分: " + resources.getScore(), 30, y);

            graphics.setColor(ImageDrawerUtils.BLUE_A75);
            graphics.drawString("存储积分: " + resources.getScore0(), 300, y);

            graphics.setColor(ImageDrawerUtils.BLACK_A45);
            graphics.drawString("预计利息:", 560, 380);
            int r1 = (resources.getScore0() >= 10000 ? (int) (resources.getScore0() / 10000 * 4) : 0);
            if (r1 == 0) graphics.setColor(ImageDrawerUtils.GREEN_A85);
            else if (r1 < 100) graphics.setColor(ImageDrawerUtils.BLUE_A75);
            else graphics.setColor(ImageDrawerUtils.RED_A75);
            graphics.drawString(String.valueOf(r1), 700, 380);
            //========line-1-end=========

            //========line-2-start===========
            y += 100;
            graphics.setColor(ImageDrawerUtils.BLACK_A75);
            graphics.drawString("签到状态: ", 30, y);
            graphics.drawString("签到次数: ", 300, y);
            graphics.drawString("积分加成: ", 560, y);

            if (resources.getDay() == DateUtils.getDay()) {
                graphics.setColor(ImageDrawerUtils.GREEN_A75);
                graphics.drawString("√", 150, y);
            } else {
                graphics.setColor(ImageDrawerUtils.RED_A75);
                graphics.drawString("×", 150, y);
            }

            int r2 = resources.getDays();
            if (r2 < 100) graphics.setColor(ImageDrawerUtils.GREEN_A85);
            else if (r2 < 300) graphics.setColor(ImageDrawerUtils.BLUE_A75);
            else graphics.setColor(ImageDrawerUtils.RED_A75);
            graphics.drawString(String.valueOf(r2), 420, y);

            int r3 = user.getLevel();
            if (r3 < 3) graphics.setColor(ImageDrawerUtils.GREEN_A85);
            else if (r3 < 7) graphics.setColor(ImageDrawerUtils.BLUE_A75);
            else graphics.setColor(ImageDrawerUtils.RED_A75);
            graphics.drawString(r3 + "%", 700, y);
            //========line-2-end===========

            //========line-3-start===========
            y += 100;
            graphics.setColor(ImageDrawerUtils.BLACK_A75);
            graphics.drawString("打工状态: ", 30, y);
            graphics.drawString("打劫次数: ", 300, y);

            if (resources.getK() > System.currentTimeMillis()) {
                graphics.setColor(ImageDrawerUtils.GREEN_A75);
                graphics.drawString("进行中.", 150, y);
            } else {
                graphics.setColor(ImageDrawerUtils.RED_A75);
                graphics.drawString("空闲中.", 150, y);
            }

            if (resources.getFz() >= 12) graphics.setColor(ImageDrawerUtils.GREEN_A75);
            else graphics.setColor(ImageDrawerUtils.ORIGIN_A75);

            graphics.drawString(String.valueOf(12 - resources.getFz()), 420, y);

            graphics.setColor(ImageDrawerUtils.RED_A75);
            graphics.setFont(ImageDrawerUtils.SMALL_FONT26);
            graphics.drawString("等级: " + user.getLevel().toString(), 560, y);
            //========line-3-end===========

            //========line-4-start===========
            y += 100;
            ImageDrawerUtils.drawStringContinuousDiscoloration(graphics, 30, y,
                    "特殊币: ", ImageDrawerUtils.BLACK_A85, resources.getGold().toString(), ImageDrawerUtils.RED_A75);
            ImageDrawerUtils.drawStringContinuousDiscoloration(graphics, 300, y,
                    "灵力: ", ImageDrawerUtils.BLACK_A85, String.valueOf(resources.getEnergy()), ImageDrawerUtils.RED_A75);
            ImageDrawerUtils.drawStringContinuousDiscoloration(graphics, 560, y,
                    "魂角数: ", ImageDrawerUtils.BLACK_A85, String.valueOf(o.getJSONArray("characters").size()), ImageDrawerUtils.RED_A75);
            //========line-4-end===========


            //========line-tips-start===========
            y = 750;
            graphics.setFont(ImageDrawerUtils.SMALL_FONT28);
            graphics.setColor(ImageDrawerUtils.BLACK_A75);
            graphics.drawRoundRect(180, y, 440, 60, 30, 30);
            if (t) graphics.setColor(ImageDrawerUtils.GREEN_A85);
            else graphics.setColor(Color.RED);
            graphics.drawString(tips, (image.getWidth() - graphics.getFontMetrics().stringWidth(tips)) / 2, y + 40);
            //========line-tips-end===========

            graphics.setFont(ImageDrawerUtils.SMALL_FONT18);
            graphics.setColor(ImageDrawerUtils.BLACK_A75);
            String dt = DateUtils.getFormat();
            graphics.drawString(dt, (image.getWidth() - graphics.getFontMetrics().stringWidth(dt)) / 2, y + 130);
            graphics.drawString(String.format("%s - %s", user.getId(), user.getUid()), 5, 20);


            graphics.setFont(ImageDrawerUtils.SMALL_FONT18_TYPE0);
            dt = "create by github@kloping";
            graphics.drawString(dt, image.getWidth() - graphics.getFontMetrics().stringWidth(dt) - 5, image.getHeight() - graphics.getFontMetrics().getHeight());
            graphics.dispose();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
