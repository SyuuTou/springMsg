package com.scienjus.controller;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

@RestController
public class MobileAuthCodeGenerator {
    private static Logger logger = LoggerFactory.getLogger(MobileAuthCodeGenerator.class);


    // 默认验证码6位
    private static  int DEFAULT_CODE_NUMBERS = 6;


    private static Random random = new SecureRandom();


    public static String generateCode(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i< DEFAULT_CODE_NUMBERS; i++){
            sb.append(random.nextInt(10));
        }


        return sb.toString();
    }


    public static String generateCode(int numbers){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i< numbers; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


    /**
     * 使用系统默认字符源生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @return
     */
    public static String generateVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, VERIFY_CODES);
    }


    /**
     * 使用指定源生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @param sources
     *            验证码字符源
     * @return
     */


    public static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    public static String generateVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codesLen = sources.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++) {
            verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
        }
        return verifyCode.toString();
    }


    /**
     * 输出指定验证码图片流
     *
     * @throws IOException
     */
    @RequestMapping(value = "outputImage")
    @ApiOperation(value = "短信发送")
    public static String outputImage() {
        int w =100;
        int h = 30;
        String code = "VM8Y";
        int verifySize = code.length();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[] { Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW };
        float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);


        g2.setColor(Color.GRAY);// 设置边框色
        g2.fillRect(0, 0, w, h);


        Color c = getRandColor(200, 250);
        g2.setColor(c);// 设置背景色
        g2.fillRect(0, 2, w, h - 4);


        // 绘制干扰线
        Random random = new Random();
        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }


        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        shear(g2, w, h, c);// 使图片扭曲

        g2.setColor(getRandColor(100, 160));
        int fontSize = h - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (w / verifySize) * i + fontSize / 2, h / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + fontSize / 2 - 10);
        }


        g2.dispose();
        String result = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            boolean flag = ImageIO.write(image, "GIF", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = out.toByteArray();
        BASE64Encoder base64Encoder = new BASE64Encoder();
//        result = base64Encoder.encode(b);
        result = Base64.encodeBase64String(b);
        return result;
    }


    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }


    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }


    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }


    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }


    private static void shearX(Graphics g, int w1, int h1, Color color) {


        int period = random.nextInt(2);


        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);


        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40) + 10; // 50;
        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }
        }
    }




    public   static void main(String arg[]){


//        for (int i =0;i<2000;i++){
//            String s = MobileAuthCodeGenerator.generateCode();
//            logger.info(s);
//        }
        String code = MobileAuthCodeGenerator.generateVerifyCode(4);
        String data = "";
//        try {
//            data = MobileAuthCodeGenerator.outputImage(100,30,code);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(code);
//        System.out.println(data);
    }
}
