package io.github.kloping.mihdp.game.v.v1;

import io.github.kloping.judge.Judge;
import io.github.kloping.mihdp.dao.User;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author github.kloping
 */
public class ReqDataPackUtils {
    public static BufferedImage getIcon(ReqDataPack pack, User user) throws IOException {
        if (pack.containsArgs(BaseService.BASE_ICON_ARGS))
            return ImageIO.read(new URL(pack.getArgValue(BaseService.BASE_ICON_ARGS).toString()));
        else if (Judge.isNotEmpty(user.getIcon())) return ImageIO.read(new URL(user.getIcon()));
        else return ImageIO.read(new ClassPathResource("sources/0.jpg").getInputStream());
    }
}
