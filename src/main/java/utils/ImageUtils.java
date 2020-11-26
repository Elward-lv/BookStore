package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageUtils {
    private static final String charSequences = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm123456789";
    private static int width = 100;
    private static int height = 50;
    public static Object getImageCode(){
        char[] code = getRandomCode(4);

        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        Graphics graphics = image.getGraphics();
        //设置边框
        graphics.setColor(Color.PINK);
        graphics.drawRect(0,0,width,height);
        //设置背景色
        graphics.setColor(Color.black);
        return image;
    }

    public static char[] getRandomCode(int length){
        char[] res = new char[length];
        Random r = new Random(System.currentTimeMillis());
        int index = 0;
        for (int i = 0; i < length; i++) {
            int nextIndex = r.nextInt(charSequences.length());
            char c = charSequences.charAt(nextIndex);
            res[index++] = c;
        }
        return res;
    }

    public static void main(String[] args) {
        char[] randomCode = getRandomCode(5);
        for (int i = 0; i < randomCode.length; i++) {
            System.out.println(randomCode[i]);
        }
    }
}
