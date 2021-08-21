package vip.phantom.api;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;


public class RenderUtil {

    public static void drawRect(float left, float top, float right, float bottom, Color color) {
        glPushMatrix();
        final boolean texture2DFlag = glIsEnabled(GL_TEXTURE_2D);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        setGLColor(color);
        glBegin(GL_QUADS);
        glVertex2f(left, top);
        glVertex2f(left, bottom);
        glVertex2f(right, bottom);
        glVertex2f(right, top);
        glEnd();
        glDisable(GL_BLEND);
//        if (texture2DFlag) {
        glEnable(GL_TEXTURE_2D);
//        }
        glPopMatrix();
    }

    public static void beginScissor(float x, float y, float width, float height) {
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) x, (int) (Display.getHeight() - y - height), (int) width, (int) height);
    }

    public static void endScissor() {
        glDisable(GL_SCISSOR_TEST);
    }

    public static boolean isHovered(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public static void drawPicture(double x, double y, double width, double height, int resource, boolean color) {
        glPushMatrix();
        final boolean texture2DFlag = glIsEnabled(GL_TEXTURE_2D);
        glEnable(GL_TEXTURE_2D);
        final boolean blendFlag = glIsEnabled(GL_BLEND);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //scale down to make picture look better
        GL11.glScalef(0.5f, 0.5f, 1);
        x *= 2;
        y *= 2;
        width *= 2;
        height *= 2;

        if (!color) {
            setGLColor(Color.white);
        }

        //bind the texture
        GL11.glBindTexture(GL_TEXTURE_2D, resource);
//        float toAddY = 0;
////        if (width >= height) {
//            toAddY = (float) (1 * (width / height));
////        }
//        float toAddX = 0;


        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2d(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2d(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2d(x + width, y);
        glTexCoord2f(0, 0);
        glVertex2d(x, y);
        glEnd();


        GL11.glBindTexture(GL_TEXTURE_2D, 0);
        if (!blendFlag) {
            glDisable(GL_BLEND);
        }
        if (!texture2DFlag) {
            glDisable(GL_TEXTURE_2D);
        }
        resetGLColor();
        glPopMatrix();
    }

    public static int createTexture(BufferedImage image) {
        // Array of all the colors in the image.
        int[] pixels = new int[image.getWidth() * image.getHeight()];

        // Fetches all the colors in the image.
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        // Buffer that will store the texture data.
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        // Puts all the pixel data into the buffer.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                // The pixel in the image.
                int pixel = pixels[y * image.getWidth() + x];

                // Puts the data into the byte buffer.
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        // Flips the byte buffer, not sure why this is needed.
        buffer.flip();

        int textureId = GL11.glGenTextures();
        // Binds the opengl texture by the texture id.
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        // Sets the texture parameter stuff.
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        // Uploads the texture to opengl.
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Binds the opengl texture 0.
        return textureId;
    }

    public static void setGLColor(Color color) {
        setGLColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void setGLColor(float red, float green, float blue, float alpha) {
        glColor4f(red / 255, green / 255, blue / 255, alpha / 255);
    }

    public static void resetGLColor() {
        glColor4f(1, 1, 1, 1);
    }


}

