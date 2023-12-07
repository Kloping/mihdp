package io.github.kloping.mihdp.utils;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;

/**
 * @author github-kloping
 */
public class ImageDrawerUtils {

    public static final Font SMALL_FONT28 = new Font("楷体", Font.BOLD, 28);
    public static final Font SMALL_FONT26 = new Font("楷体", Font.BOLD, 26);
    public static final Font SMALL_FONT24 = new Font("楷体", Font.BOLD, 24);
    public static final Font SMALL_FONT22 = new Font("楷体", Font.BOLD, 22);
    public static final Font SMALL_FONT20 = new Font("楷体", Font.BOLD, 20);
    public static final Font SMALL_FONT18 = new Font("楷体", Font.BOLD, 18);
    public static final Font SMALL_FONT16 = new Font("楷体", Font.BOLD, 16);
    public static final Font SMALL_FONT32 = new Font("楷体", Font.BOLD, 32);
    public static final Font SMALL_FONT38 = new Font("楷体", Font.BOLD, 38);
    public static final Font SMALL_FONT40 = new Font("楷体", Font.BOLD, 40);
    public static final Font SMALL_FONT46 = new Font("楷体", Font.BOLD, 46);

    public static final Font BIG_FONT80 = new Font("楷体", Font.CENTER_BASELINE, 80);

    public static final Font BIG_FONT60_TYPE0 = new Font("方正舒体", Font.BOLD, 60);
    public static final Font BIG_FONT80_TYPE0 = new Font("方正舒体", Font.PLAIN, 80);

    public static final Font SMALL_FONT18_TYPE0 = new Font("方正舒体", Font.BOLD, 18);
    public static final Font SMALL_FONT22_TYPE0 = new Font("方正舒体", Font.BOLD, 22);
    public static final Font SMALL_FONT38_TYPE0 = new Font("方正舒体", Font.BOLD, 38);

    public static final Color BLACK_A90 = new Color(0, 0, 0, 230);
    public static final Color BLACK_A85 = new Color(0, 0, 0, 217);
    public static final Color YELLOW_A85 = new Color(150, 99, 4, 217);
    public static final Color GREEN_A85 = new Color(0, 150, 0, 217);
    public static final Color ORIGIN_A80 = new Color(231, 96, 12, 204);
    public static final Color WHITE_A80 = new Color(211, 211, 211, 203);
    public static final Color ORIGIN_A75 = new Color(231, 129, 12, 191);
    public static final Color BLACK_A75 = new Color(0, 0, 0, 191);
    public static final Color GREEN_A75 = new Color(2, 180, 2, 191);
    public static final Color YELLOW_A75 = new Color(185, 156, 0, 191);
    public static final Color BLUE2_A75 = new Color(0, 81, 147, 191);
    public static final Color BLUE3_A75 = new Color(101, 217, 217, 191);
    public static final Color BLUE3_A90 = new Color(101, 217, 217, 230);
    public static final Color BLUE4_A75 = new Color(183, 245, 253, 191);
    public static final Color BLUE5_A75 = new Color(221, 246, 250, 191);
    public static final Color BLUE_A75 = new Color(0, 0, 222, 191);
    public static final Color RED_A75 = new Color(231, 52, 12, 191);
    public static final Color RED_A90 = new Color(231, 52, 12, 230);
    public static final Color WHITE_A60 = new Color(211, 211, 211, 153);
    public static final Color BLACK_A60 = new Color(0, 0, 0, 153);
    public static final Color BLACK_A45 = new Color(0, 0, 0, 115);
    public static final Color BLACK_A35 = new Color(0, 0, 0, 89);
    public static final Color WHITE_A35 = new Color(211, 211, 211, 89);

    public static final Stroke STROKE1 = new BasicStroke(1.0f);
    public static final Stroke STROKE2 = new BasicStroke(2.0f);
    public static final Stroke STROKE3 = new BasicStroke(3.0f);


    public static void drawStringContinuousDiscoloration(Graphics graphics, int x, int y,
                                                         String t1, Color c1, String t2, Color c2) {
        graphics.setColor(c1);
        graphics.drawString(t1, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t1);

        graphics.setColor(c2);
        graphics.drawString(t2, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t2);
    }

    public static void drawStringContinuousDiscoloration(Graphics graphics, int x, int y
            , String t1, Color c1
            , String t2, Color c2
            , String t3, Color c3) {
        graphics.setColor(c1);
        graphics.drawString(t1, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t1);

        graphics.setColor(c2);
        graphics.drawString(t2, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t2);

        graphics.setColor(c3);
        graphics.drawString(t3, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t3);
    }

    public static void drawStringContinuousDiscoloration(Graphics graphics, int x, int y,
                                                         String t1, Color c1, String t2, Color c2,
                                                         String t3, Color c3, String t4, Color c4) {
        graphics.setColor(c1);
        graphics.drawString(t1, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t1);

        graphics.setColor(c2);
        graphics.drawString(t2, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t2);

        graphics.setColor(c3);
        graphics.drawString(t3, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t3);

        graphics.setColor(c4);
        graphics.drawString(t4, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t4);

    }

    public static void drawStringContinuousDiscoloration(Graphics graphics, int x, int y,
                                                         String t1, Color c1,
                                                         String t2, Color c2,
                                                         String t3, Color c3,
                                                         String t4, Color c4,
                                                         String t5, Color c5
    ) {
        graphics.setColor(c1);
        graphics.drawString(t1, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t1);

        graphics.setColor(c2);
        graphics.drawString(t2, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t2);

        graphics.setColor(c3);
        graphics.drawString(t3, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t3);

        graphics.setColor(c4);
        graphics.drawString(t4, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t4);

        graphics.setColor(c5);
        graphics.drawString(t5, x, y);
        x = x + graphics.getFontMetrics().stringWidth(t5);

    }

    /**
     * 图片圆角
     *
     * @param image        图片
     * @param cornerRadius 幅度
     * @return
     */
    public static BufferedImage roundImage(BufferedImage image, int cornerRadius) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return outputImage;
    }

    /**
     * 压缩指定宽、高
     *
     * @param bimg
     * @param width
     * @param height
     * @param tagFilePath
     * @return
     */
    public static Image image2Size(BufferedImage bimg, int width, int height) throws IOException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp1", ".png");
            Thumbnails.of(bimg).size(width, height).outputQuality(1F).toFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage image = ImageIO.read(tempFile);
        tempFile.delete();
        return image;
    }

    /**
     * 旋转图片
     *
     * @param image
     * @param rotate
     * @return
     * @throws IOException
     */
    public static Image rotateImage(BufferedImage image, float rotate) throws IOException {
        File tempFile = null;
        try {
            int _w = image.getWidth();
            int _h = image.getHeight();
            tempFile = File.createTempFile("temp2", ".png");
            Thumbnails.of(image).scale(1F).rotate(rotate).toFile(tempFile);
            BufferedImage i1 = ImageIO.read(tempFile);
            Thumbnails.of(i1).sourceRegion(Positions.CENTER, _w, _h).size(_w, _h).keepAspectRatio(false).toFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image = ImageIO.read(tempFile);
        tempFile.delete();
        return image;
    }

    /**
     * 将 一张图片放到 一张图片上
     *
     * @param image
     * @param im
     * @param x
     * @param y
     * @return
     */
    public static BufferedImage putImage(BufferedImage image, BufferedImage im, int x, int y) {
        Graphics graphics = image.getGraphics();
        graphics.drawImage(im, x, y, null);
        graphics.dispose();
        return image;
    }

    public static Image getImageByUrl2Size(URL url, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(url);
            return image2Size(image, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image getImageByColor2Size(Color color, int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D g2d = image.createGraphics();
            g2d.setClip(0, 0, width, height);
            g2d.setColor(color);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在 图片 上 绕指定点 旋转 图片
     *
     * @param image
     * @param o
     * @param rotate
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    public static Image rotateImage(BufferedImage image, BufferedImage o, float rotate, int x, int y) throws IOException {

        return image;
    }

    public static String image2gift(int delay, File outFile, String... images) {
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outFile.getAbsolutePath());
        encoder.setRepeat(0);
        encoder.setQuality(5);
        encoder.setFrameRate(delay);
        for (int i = 0; i < images.length; i++) {
            String u0 = images[i];
            encoder.setDelay(delay);
            BufferedImage main = null;
            try {
                if (u0 == null) continue;
                else if (u0.startsWith("http")) {
                    main = ImageIO.read(new URL(u0));
                } else {
                    main = ImageIO.read(new File(u0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            encoder.addFrame(main);
        }
        encoder.finish();
        return outFile.getAbsolutePath();
    }

    public static String image2giftIncrease(int delay, File outFile, String... images) {
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outFile.getAbsolutePath());
        encoder.setRepeat(0);
        encoder.setQuality(5);
        encoder.setFrameRate(delay);
        float f = 1.1f;
        for (int i = 0; i < images.length; i++) {
            String u0 = images[i];
            f += 0.1;
            int d0 = (int) (f * delay);
            encoder.setDelay(d0);
            BufferedImage main = null;
            try {
                if (u0 == null) continue;
                else if (u0.startsWith("http")) {
                    main = ImageIO.read(new URL(u0));
                } else {
                    main = ImageIO.read(new File(u0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            encoder.addFrame(main);
        }
        encoder.finish();
        return outFile.getAbsolutePath();
    }

    /**
     * 重置图形的边长大小
     *
     * @param src
     * @param width
     * @param height
     * @throws IOException
     */
    public static BufferedImage resizeImage(BufferedImage src, int width, int height) throws IOException {
        FileOutputStream out = null;
        try {
            // 放大边长
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //绘制放大后的图片
            tag.getGraphics().drawImage(src, 0, 0, width, height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
            encoder.encode(tag);
            return ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对图片裁剪，并把裁剪新图片保存
     *
     * @param bytes            读取图片二进制
     * @param position         图片切割位置
     * @param readImageFormat  读取图片格式
     * @param writeImageFormat 写入图片格式
     * @throws IOException
     */
    public static byte[] cropImage(byte[] bytes, int[] position, String readImageFormat, String writeImageFormat) throws IOException {
        FileInputStream fis = null;
        ImageInputStream iis = null;
        InputStream is = null;
        ByteArrayOutputStream out = null;
        int x = position[0];
        int y = position[1];
        int width = position[2];
        int height = position[3];
        try {
            //读取图片文件
            //fis = new FileInputStream();
            is = new ByteArrayInputStream(bytes);
            Iterator it = ImageIO.getImageReadersByFormatName(readImageFormat);
            ImageReader reader = (ImageReader) it.next();
            //获取图片流
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            //定义一个矩形
            Rectangle rect = new Rectangle(x, y, width, height);
            //提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            //保存新图片
            out = new ByteArrayOutputStream();
            ImageIO.write(bi, writeImageFormat, out);
            return out.toByteArray();
        } finally {
            if (fis != null) fis.close();
            if (iis != null) iis.close();
        }
    }


    public static BufferedImage readImage(String file, int w, int h) {
        try {
            BufferedImage bi = ImageIO.read(new File(file));
            return (BufferedImage) image2Size(bi, w, h);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage readImage(URL url, int w, int h) throws IOException {
        try {
            BufferedImage bi = ImageIO.read(url);
            return (BufferedImage) image2Size(bi, w, h);
        } catch (IOException e) {
            System.err.println(url.toString());
            throw e;
        }
    }
}