package vip.phantom.helper.food;

import lombok.Getter;
import vip.phantom.api.ConnectionUtil;
import vip.phantom.api.RenderUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Getter
public class Food {

    private final String name;
    private final long undercooked, cooked, burnt;
    private URL pictureURL;
    private final int textureID;
    private final int[] pictureSize;

    public Food(String name, long undercooked, long cooked, long burnt, String pictureURL) throws IOException {
        this.name = name;
        this.pictureURL = new URL(pictureURL);
        final BufferedImage bufferedImage = ConnectionUtil.getBufferedImageWithUserAgent(this.pictureURL);
        pictureSize = new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
        textureID = RenderUtil.createTexture(bufferedImage);
        this.undercooked = undercooked * 1000;
        this.cooked = cooked * 1000;
        this.burnt = burnt * 1000;
    }

    public Food(String name, long undercooked, long cooked, long burnt, File file) throws IOException {
        this.name = name;
        final BufferedImage bufferedImage = ImageIO.read(file);
        pictureSize = new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
        textureID = RenderUtil.createTexture(bufferedImage);
        this.undercooked = undercooked * 1000;
        this.cooked = cooked * 1000;
        this.burnt = burnt * 1000;
    }
}
