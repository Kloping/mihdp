package io.github.kloping.mihdp.utils;

import lombok.Getter;

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

    public ImageDrawer draw(BufferedImage des, int w, int h, int x, int y, int radius) throws IOException {
        if (radius > 0) des = ImageDrawerUtils.roundImage(des, radius);
        Graphics graphics = getSrc().getGraphics();
        graphics.drawImage(des, x, y, w, h, null);
        graphics.dispose();
        return this;
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

    /**
     * 写字布局 默认横 0
     *
     * @param layout 0横 1竖
     * @return
     */
    public ImageDrawer layout(int layout) {
        this.layout = layout;
        return this;
    }

    /**
     * 写字布局间隙 默认 1
     *
     * @param gaps
     * @return
     */
    public ImageDrawer layoutGaps(int gaps) {
        this.gaps = gaps;
        return this;
    }

    private Integer gaps = 1;
    private int layout = 0;
    private int ox = -1, oy = -1;

    public ImageDrawer drawString(Font font, Color color, String s, int x, int y) {
        Graphics graphics = getSrc().getGraphics();
        graphics.setFont(font);
        graphics.setColor(color);
        graphics.drawString(s, x, y);
        if (layout == 0) this.ox = graphics.getFontMetrics().stringWidth(s) + x + gaps;
        else this.oy = graphics.getFontMetrics().stringWidth(s) + y + gaps;
        graphics.dispose();
        return this;
    }

    public ImageDrawer drawString(String s, Color color, int x, int y) {
        Graphics graphics = getSrc().getGraphics();
        graphics.setColor(color);
        graphics.drawString(s, x, y);
        if (layout == 0) this.ox = graphics.getFontMetrics().stringWidth(s) + x + gaps;
        else this.oy = graphics.getFontMetrics().stringWidth(s) + y + gaps;
        graphics.dispose();
        return this;
    }

    public ImageDrawer drawString(String s, int x, int y) {
        Graphics graphics = getSrc().getGraphics();
        graphics.drawString(s, x, y);
        if (layout == 0) this.ox = graphics.getFontMetrics().stringWidth(s) + x + gaps;
        else this.oy = graphics.getFontMetrics().stringWidth(s) + y + gaps;
        graphics.dispose();
        return this;
    }

    public ImageDrawer drawString(String s, Color color, Font font) {
        if (ox < 0 || oy < 0) {
            throw new RuntimeException("draw string Coordinates must be required if not previously set");
        }
        Graphics graphics = getSrc().getGraphics();
        if (color != null) graphics.setColor(color);
        if (font != null) graphics.setFont(font);
        graphics.drawString(s, ox, oy);
        graphics.dispose();
        return this;
    }

    public ImageDrawer drawString(String s, Color color) {
        return drawString(s, color, null);
    }

    public ImageDrawer drawString(String s) {
        return drawString(s, null, null);
    }

    public ImageDrawer execute(Execute0 execute0) {
        execute0.draw(getSrc().getGraphics());
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
