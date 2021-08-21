package vip.phantom.api.font;

import lombok.Getter;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Fonts {
    private static Fonts instance;

    @Getter
    private final HashMap<String, FontRenderer> fonts = new HashMap<>();

    public Fonts() {
        initFont("BrushTip", 20);
        initFont("Verdana", 17);
    }

    private void initFont(String name, float size) {
        try {
//            fonts.put(name + size, new FontRenderer(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream( name + ".ttf")).deriveFont(size)));
            fonts.put(name + size, new FontRenderer(Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("src/main/java/vip/phantom/api/font/fonts/" + name + ".ttf")).deriveFont(size)));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Fonts getInstance() {
        if (instance == null) {
            instance = new Fonts();
        }
        return instance;
    }
}
        
    