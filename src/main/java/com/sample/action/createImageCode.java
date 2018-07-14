package com.sample.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class createImageCode extends ActionSupport {
    private static int width = 60;
    private static int height = 20;
    private ByteArrayInputStream inputStream;

    public ByteArrayInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(ByteArrayInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private static String mkRandomStr() {
        String str = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] array = new char[4];
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            array[i] = str.charAt(random.nextInt(62));
        }
        return new String(array);
    }

    private void drawBackground(Graphics g) {
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        for (int i = 0; i < 120; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int red = (int) Math.random() * 255;
            int green = (int) Math.random() * 255;
            int blue = (int) Math.random() * 255;
            g.setColor(new Color(red, green, blue));
            g.drawOval(x, y, 1, 0);
        }
    }

    private void drawRands(Graphics g, String rands) {
        g.setColor(Color.BLACK);
        g.setFont(new Font(null, Font.ITALIC | Font.BOLD, 18));
        g.drawString("" + rands.charAt(0), 1, 18);
        g.drawString("" + rands.charAt(1), 16, 15);
        g.drawString("" + rands.charAt(2), 31, 17);
        g.drawString("" + rands.charAt(3), 46, 16);
    }

    public String getCodeImage() {
        HttpServletResponse response = ServletActionContext.getResponse();
        //PrintWriter writer=response.getWriter();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        //response.setContentType("image/jpeg");
        String rands = mkRandomStr();
        ServletActionContext.getContext().getSession().put("imageCode", rands);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        this.drawBackground(g);
        this.drawRands(g, rands);
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        try{
            out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", out);
            in = new ByteArrayInputStream(out.toByteArray());
            this.setInputStream(in);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SUCCESS;
    }
}
