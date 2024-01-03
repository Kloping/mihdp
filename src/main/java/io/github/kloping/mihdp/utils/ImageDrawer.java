package io.github.kloping.mihdp.utils;

import io.github.kloping.io.ReadUtils;
import io.github.kloping.rand.RandomUtils;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * @author github.kloping
 */
@Getter
public class ImageDrawer {
    private BufferedImage src;

    public ImageDrawer(BufferedImage src) {
        this.src = src;
    }

    public ImageDrawer(byte[] bytes) throws IOException {
        this(ImageIO.read(new ByteArrayInputStream(bytes)));
    }

    public ImageDrawer(File file) throws IOException {
        this(ImageIO.read(file));
    }

    public ImageDrawer(String url) throws IOException {
        this(ImageIO.read(new URL(url)));
    }

    public ImageDrawer(URL url) throws IOException {
        this(ImageIO.read(url));
    }


    public static final ImageDrawer createOnRandomBg() {
        return createOnBg(RandomUtils.RANDOM.nextInt(4));
    }

    public static final ImageDrawer createOnBg(Integer id) {
        try {
            byte[] bytes = ReadUtils.readAll(new ClassPathResource(String.format("bg%s.jpg", id)).getInputStream());
            return new ImageDrawer(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 基图重置大小
     *
     * @param w
     * @param h
     * @return
     */
    public ImageDrawer size(int w, int h) throws IOException {
        src = ImageDrawerUtils.resizeImage(src, w, h);
        return this;
    }

    public ImageDrawer draw(BufferedImage des, int w, int h, int x, int y, int radius, float rotate) throws IOException {
        if (radius > 0) des = ImageDrawerUtils.roundImage(des, radius);
        if (rotate > 0) des = (BufferedImage) ImageDrawerUtils.rotateImage(des, rotate);
        Graphics graphics = getSrc().getGraphics();
        graphics.drawImage(des, x, y, w, h, null);
        graphics.dispose();
        return this;
    }

    public ImageDrawer draw(BufferedImage des, int w, int h, int x, int y, int radius) throws IOException {
        return draw(des, w, h, x, y, radius, -1);
    }

    public ImageDrawer draw(BufferedImage des, int w, int h, int x, int y) throws IOException {
        return draw(des, w, h, x, y, -1);
    }

    public ImageDrawer draw(InputStream is, int w, int h, int x, int y) throws IOException {
        return draw(ImageIO.read(is), w, h, x, y, -1);
    }

    public ImageDrawer draw(String url, int w, int h, int x, int y) throws IOException {
        return draw(ImageIO.read(new URL(url)), w, h, x, y, -1);
    }

    public ImageDrawer draw(File file, int w, int h, int x, int y, int radius) throws IOException {
        return draw(ImageIO.read(file), w, h, x, y, radius);
    }

    public ImageDrawer draw(File file, int w, int h, int x, int y) throws IOException {
        return draw(ImageIO.read(file), w, h, x, y, -1);
    }

    public ImageDrawer draw(File file, int w, int h, int x, int y, float rotate) throws IOException {
        return draw(ImageIO.read(file), w, h, x, y, -1, rotate);
    }

    public ImageDrawer fillRoundRect(Color color, int x, int y, int w, int h, int rw, int rh) {
        Graphics graphics = getSrc().getGraphics();
        if (color != null) graphics.setColor(color);
        graphics.fillRoundRect(x, y, w, h, rw, rh);
        graphics.dispose();
        return this;
    }

    public ImageDrawer drawRoundRect(Color color, int x, int y, int w, int h, int rw, int rh) {
        Graphics graphics = getSrc().getGraphics();
        if (color != null) graphics.setColor(color);
        graphics.drawRoundRect(x, y, w, h, rw, rh);
        graphics.dispose();
        return this;
    }

    public class ImageDrawerString {
        private Integer gaps = 1;
        private int layout = 0;
        private int ox = -1, oy = -1;
        private ImageDrawer drawer;
        private Graphics graphics;

        private final Graphics getGraphics() {
            return graphics == null ? graphics = getSrc().getGraphics() : graphics;
        }

        public ImageDrawer finish() {
            graphics.dispose();
            return drawer;
        }

        public ImageDrawerString finishAndStartDrawString() {
            graphics.dispose();
            return drawer.startDrawString();
        }

        public ImageDrawerString finishAndStartDrawString(Font font, Color color, String s, int x, int y) {
            graphics.dispose();
            return drawer.startDrawString(font, color, s, x, y);
        }

        public ImageDrawerString finishAndStartDrawStringDown(Font font, Color color, String s, int x, int gaps) {
            int y = graphics.getFontMetrics().getHeight() + gaps + oy;
            graphics.dispose();
            return drawer.startDrawString(font, color, s, x, y);
        }

        public ImageDrawerString finishAndStartDrawStringDown(String s, int x, int gaps) {
            int y = graphics.getFontMetrics().getHeight() + gaps + oy;
            ImageDrawerString dr = drawer.startDrawString(graphics.getFont(), graphics.getColor(), s, x, y);
            graphics.dispose();
            return dr;
        }

        /**
         * 写字布局 默认横 0
         *
         * @param layout 0横 1竖
         * @return
         */
        public ImageDrawerString layout(int layout) {
            return layout(layout, null, null);
        }

        public ImageDrawerString layout(int layout, Color color) {
            return layout(layout, color, null);
        }

        public ImageDrawerString layout(int layout, Color color, Font font) {
            this.layout = layout;
            if (color != null) getGraphics().setColor(color);
            if (font != null) getGraphics().setFont(font);
            return this;
        }

        /**
         * 写字布局间隙 默认 1
         *
         * @param gaps
         * @return
         */
        public ImageDrawerString layoutGaps(int gaps) {
            this.gaps = gaps;
            return this;
        }

        public ImageDrawerString drawString(Font font, Color color, String s, int x, int y) {
            Graphics graphics = getGraphics();
            if (font != null) {
                graphics.setFont(font);
            }
            if (color != null) graphics.setColor(color);
            graphics.drawString(s, x, y);
            if (layout == 0) {
                this.ox = graphics.getFontMetrics().stringWidth(s) + x + gaps;
                this.oy = y;
            } else {
                this.oy = graphics.getFontMetrics().getHeight() + y + gaps;
                this.ox = x;
            }
            return this;
        }

        public ImageDrawerString drawString(String s, Color color, int x, int y) {
            return drawString(null, color, s, x, y);
        }

        public ImageDrawerString drawString(String s, int x, int y) {
            return drawString(null, null, s, x, y);
        }

        public ImageDrawerString drawString(String s, Color color, Font font) {
            if (ox < 0 || oy < 0) {
                throw new RuntimeException("draw string Coordinates must be required if not previously set");
            }
            Graphics graphics = getGraphics();
            if (color != null) graphics.setColor(color);
            if (font != null) graphics.setFont(font);
            graphics.drawString(s, ox, oy);
            if (layout == 0) {
                this.ox = graphics.getFontMetrics().stringWidth(s) + ox + gaps;
            } else {
                this.oy = graphics.getFontMetrics().getHeight() + oy + gaps;
            }
            return this;
        }

        public ImageDrawerString drawString(String s, Color color) {
            return drawString(s, color, null);
        }

        public ImageDrawerString drawString(Object o, Color color) {
            return drawString(o.toString(), color, null);
        }

        public ImageDrawerString drawString(String s) {
            return drawString(s, null, null);
        }

        public ImageDrawerString drawString(Object o) {
            return drawString(o.toString(), null, null);
        }

        /**
         * 画字符并超过maxW换行
         *
         * @param text
         * @param max
         * @return
         */
        public ImageDrawerString drawString(String text, int max) {
            Graphics graphics = getGraphics();
            int ep;
            if (layout == 0) {
                ep = ox;
            } else {
                ep = oy;
            }
            for (int i = 0; i < text.length(); i++) {
                String e = String.valueOf(text.charAt(i));
                if (layout == 0) {
                    int e1 = graphics.getFontMetrics().stringWidth(e);
                    ep += e1;
                    if (ep - ox > max) {
                        ep = ox + e1;
                        oy += graphics.getFontMetrics().getHeight();
                    }
                    graphics.drawString(e, ep - e1, oy);
                } else {
                    int e1 = graphics.getFontMetrics().getHeight();
                    ep += e1;
                    if (ep - oy > max) {
                        ep = oy + e1;
                        ox += graphics.getFontMetrics().stringWidth(e);
                    }
                    graphics.drawString(e, ox, ep - e1);
                }
            }
            return this;
        }
    }

    public ImageDrawerString startDrawString() {
        ImageDrawerString drawerString = new ImageDrawerString();
        drawerString.drawer = this;
        return drawerString;
    }

    public ImageDrawerString startDrawString(Font font, Color color, String s, int x, int y) {
        ImageDrawerString drawerString = new ImageDrawerString();
        drawerString.drawer = this;
        drawerString.drawString(font, color, s, x, y);
        return drawerString;
    }

    public ImageDrawer execute(Execute0 execute0) {
        Graphics graphics = getSrc().getGraphics();
        execute0.draw(graphics);
        graphics.dispose();
        return this;
    }

    /**
     * 数据获取
     *
     * @return
     * @throws IOException
     */
    public byte[] bytes() throws IOException {
        return bytes("jpg");
    }

    /**
     * 格式 数据获取
     *
     * @param format
     * @return
     * @throws IOException
     */
    public byte[] bytes(String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(src, format, baos);
        return baos.toByteArray();
    }

    public interface Execute0 {
        void draw(Graphics graphics);
    }
}
