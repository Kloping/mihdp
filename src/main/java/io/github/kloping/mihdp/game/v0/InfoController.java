package io.github.kloping.mihdp.game.v0;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.services.BaseService;
import io.github.kloping.mihdp.mapper.UserMapper;
import io.github.kloping.mihdp.utils.ImageDrawerUtils;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.spt.RedisOperate;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author github.kloping
 */
@Controller
public class InfoController {
    @AutoStand
    UserMapper userMapper;

    @AutoStand
    JSONObject config;

    {
        BaseService.MSG2ACTION.put("信息", "info");
        BaseService.MSG2ACTION.put("修炼", "xl");
    }

    @Action("info")
    public Object info(ReqDataPack dataPack, GameClient client) {
        String sid = dataPack.getSender_id();
        User user = userMapper.selectById(sid);
        if (user == null) return "您当前仍未'注册';请先进行'注册;";
        else {
            Integer level = user.getLevel();
            Integer max = config.getJSONArray("xp_list").getInteger(level - 1);

            try {
                //start draw
                ClassPathResource classPathResource = new ClassPathResource("bg0.jpg");
                byte[] bytes = ReadUtils.readAll(classPathResource.getInputStream());
                bytes = ImageDrawerUtils.cropImage(bytes, new int[]{0, 0, 600, 300}, "jpg", "jpg");
                BufferedImage bg = ImageIO.read(new ByteArrayInputStream(bytes));
                BufferedImage icon0 = null;
                if (Judge.isEmpty(user.getIcon())) {
                    icon0 = ImageIO.read(new ClassPathResource("0.jpg").getInputStream());
                } else {
                    icon0 = ImageIO.read(new URL(user.getIcon()));
                }
                icon0 = ImageDrawerUtils.resizeImage(icon0, 220, 220);
                icon0 = ImageDrawerUtils.roundImage(icon0, 9999);
                bg = ImageDrawerUtils.putImage(bg, icon0, 15, 15);


                int x = 265, y = 50;

                Graphics2D g2 = (Graphics2D) bg.getGraphics();
                g2.setStroke(ImageDrawerUtils.STROKE2);
                g2.setFont(ImageDrawerUtils.SMALL_FONT38);
                g2.setColor(ImageDrawerUtils.ORIGIN_A75);
                g2.drawString("uid:" + user.getUid(), x, y);
                ImageDrawerUtils.drawStringContinuousDiscoloration(g2, x, y + 53, user.getName(), ImageDrawerUtils.YELLOW_A85, " Lv.", ImageDrawerUtils.BLUE2_A75, String.valueOf(user.getLevel()), ImageDrawerUtils.RED_A75);


                y = 160;
                g2.setFont(ImageDrawerUtils.SMALL_FONT20);
                ImageDrawerUtils.drawStringContinuousDiscoloration(g2, x, y - 20, "经验:", ImageDrawerUtils.BLACK_A60, String.format("%s/%s", user.getXp(), max), Color.WHITE);

                g2.setColor(ImageDrawerUtils.BLACK_A85);
                g2.fillRoundRect(x - 5, y - 5, 310, 70, 30, 30);
                g2.setColor(ImageDrawerUtils.WHITE_A80);
                g2.fillRoundRect(x, y, 300, 60, 25, 25);


                int b = NumberUtils.toPercent(user.getXp(), max);

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

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bg, "jpg", baos);
                return new GeneralData.ResDataImage(baos.toByteArray());
            } catch (IOException e) {
                e.getMessage();
                return e.getMessage();
            }
        }
    }

    @AutoStand
    public RedisOperate<Boolean> redisOperate0;

    @Action("xl")
    public Object xl(ReqDataPack pack, GameClient client) {
        String key = pack.getSender_id() + "-sign";
        Boolean k = redisOperate0.getValue(key);
        if (k == null || !k) {
            redisOperate0.setValue(key, true);
            return "ok";
        } else return "Duplicate check-ins";
    }
}
