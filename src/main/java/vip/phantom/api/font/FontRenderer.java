package vip.phantom.api.font;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class FontRenderer {

    private final Font font;

    private final boolean debugMode = false;
    private final File outputFile = new File("C:\\Users\\hanyo\\Desktop\\XeniaGB\\src\\fontrenderer\\chars");

    private final Graphics2D graphicsContext;
    private final FontMetrics fontMetrics;

    private boolean antiAliasing;

    private int[] mcColorCodes = new int[32];

//    private int noCharFoundTextureID = createTexture(new BufferedImage());


    private HashMap<Character, CharacterData> plainCharacters = new HashMap<>();
    private HashMap<Character, CharacterData> boldCharacters = new HashMap<>();
    private HashMap<Character, CharacterData> italicCharacters = new HashMap<>();

    private static final int MARGIN = 4;
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzüöäABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ÜÖÄ!\"§$%&/()=?{[]}@`´'#*+-~|<>^°;,:._ ";
//    private static final String CHARS = "Y";

    // https://learnopengl.com/In-Practice/Text-Rendering
    // https://open.gl/textures
    // https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image (answer: https://stackoverflow.com/a/9470843)

    public FontRenderer(Font font) {
        this(font, true);
    }

    public FontRenderer(Font font, boolean antiAliasing) {
        this.font = font.deriveFont(Font.PLAIN);
        this.antiAliasing = antiAliasing;
        if (debugMode) {
            System.out.println("Setting up " + font.getName() + "(size: " + font.getSize() + ")...");
        }
        final long time = System.currentTimeMillis();
        genMcColorCodes();

        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        graphicsContext = image.createGraphics();
        graphicsContext.setFont(font);
        fontMetrics = graphicsContext.getFontMetrics();
        if (debugMode) {
            if (!outputFile.exists()) {
                System.out.println(outputFile.mkdir());
                System.out.println(outputFile.isDirectory());
            }
        }
        for (char character : CHARS.toCharArray()) {
            setupCharacter(character);
        }
        if (debugMode) {
            System.out.println(font.getName() + "(size: " + font.getSize() + ") got set up successfully. It took: " + (System.currentTimeMillis() - time) + "ms");
        }
    }

    private void setupFonts(boolean plain, boolean italic, boolean bold) {

    }

    private void setupCharacter(Character character) {
        final Rectangle2D characterBounds = fontMetrics.getStringBounds(Character.toString(character), graphicsContext);
        double width = characterBounds.getWidth();
        double height = characterBounds.getHeight();
        //adding the margin (only for the picture)
        width += MARGIN * 2;
        height += MARGIN;
        final BufferedImage characterImage = new BufferedImage(ceiling_double_int(width), ceiling_double_int(height), BufferedImage.TYPE_INT_ARGB);

        final Graphics2D characterGraphic = characterImage.createGraphics();

        characterGraphic.setFont(font);
        characterGraphic.setColor(debugMode ? Color.RED : new Color(255, 255, 255, 0));
        characterGraphic.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());
        characterGraphic.setColor(Color.white);

        if (antiAliasing) {
            characterGraphic.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            characterGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            characterGraphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        characterGraphic.drawString(Character.toString(character), MARGIN, fontMetrics.getAscent());
        if (debugMode) {
            characterGraphic.setColor(Color.green);
            characterGraphic.draw(new Rectangle(0, fontMetrics.getAscent(), characterImage.getWidth(), 1));
            characterGraphic.setColor(Color.blue);
            characterGraphic.draw(new Rectangle(0, characterImage.getHeight() - MARGIN * 2, characterImage.getWidth(), 1));
            try {
                ImageIO.write(characterImage, "png", new File(outputFile, character + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        plainCharacters.put(character, new CharacterData(characterImage.getWidth(), characterImage.getHeight(), createTextureID(characterImage)));
    }

    private int ceiling_double_int(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    public void drawString(String text, float x, float y, Color color) {
        renderString(text, x, y, false, 100, 100, true, color);
    }

    public void renderString(String text, float x, float y, boolean mirror, float sizeWidthPercentage, float sizeHeightPercentage, boolean colorCodes, Color color) {
        if (text == null || text.length() == 0) {
            return;
        }
//        sizeWidthPercentage = Math.max(0, Math.min(sizeWidthPercentage, 100));
//        sizeHeightPercentage = Math.max(0, Math.min(sizeHeightPercentage, 100));
        GL11.glPushMatrix();
        final boolean blendFlag = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final boolean texture2DFlag = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        final boolean lightingFlag = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        x -= MARGIN / 2f;

        float charX = x;

        for (char character : text.toCharArray()) {
            if (!plainCharacters.containsKey(character)) {
                setupCharacter(character);
            }

            final CharacterData characterData = plainCharacters.get(character);
            final float cWidth = characterData.getWidth() * (sizeWidthPercentage / 100);
            final float cHeight = characterData.getHeight() * (sizeHeightPercentage / 100);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, characterData.getTextureID());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL11.glBegin(GL11.GL_QUADS);

            GL11.glTexCoord2d(0, mirror ? 1 : 0);
            GL11.glVertex2d(charX, y);

            GL11.glTexCoord2d(0, mirror ? 0 : 1);
            GL11.glVertex2d(charX, y + cHeight);

            GL11.glTexCoord2d(1, mirror ? 0 : 1);
            GL11.glVertex2d(charX + cWidth, y + cHeight);

            GL11.glTexCoord2d(1, mirror ? 1 : 0);
            GL11.glVertex2d(charX + cWidth, y);

            GL11.glEnd();
            charX += cWidth - MARGIN * 2 * (sizeWidthPercentage / 100);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        if (!blendFlag) {
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (!texture2DFlag) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        if (lightingFlag) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public float getWidth(String text) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        float width = 0;
        width -= MARGIN / 2f;

        for (char character : text.toCharArray()) {
            if (!plainCharacters.containsKey(character)) {
                setupCharacter(character);
            }
            final CharacterData characterData = plainCharacters.get(character);
            width += characterData.getWidth() - 2 * MARGIN;
        }
        return width;
    }

    public float getHeight(String text) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        float height = 0;
        for (char character : text.toCharArray()) {
            if (!plainCharacters.containsKey(character)) {
                setupCharacter(character);
            }
            final CharacterData characterData = plainCharacters.get(character);
            if (characterData.getHeight() > height) {
                height = characterData.getHeight();
            }
        }
        return height;
    }

    public float getHeight() {
        return this.getHeight(CHARS);
    }

    // minecraft`s method to generate the color codes they use
    private void genMcColorCodes() {
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            mcColorCodes[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }

    private int createTextureID(BufferedImage image) {
        // Array of all the colors in the image.
//        int[][] pixels = new int[image.getWidth()][image.getHeight()];
        int[] pixels = new int[image.getWidth() * image.getHeight()];

        // Fetches all the colors in the image.
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
//        pixels = getRGBOfPicture(image);
//        pixels = convertTo2DWithoutUsingGetRGB(image);

        // Buffer that will store the texture data.
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        // Puts all the pixel data into the buffer.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                // The pixel in the image.
                int pixel = pixels[y * image.getWidth() + x];
//                int pixel = pixels[y][x];

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


    private class CharacterData {
        private final float width, height;
        private final int textureID;

        public CharacterData(float width, float height, int textureID) {
            this.width = width;
            this.height = height;
            this.textureID = textureID;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public int getTextureID() {
            return textureID;
        }
    }
}
