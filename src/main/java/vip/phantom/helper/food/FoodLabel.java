package vip.phantom.helper.food;

import lombok.Getter;
import lombok.Setter;
import vip.phantom.api.RenderUtil;
import vip.phantom.api.SoundPlayer;
import vip.phantom.api.TimeUtil;
import vip.phantom.api.font.FontRenderer;
import vip.phantom.api.font.Fonts;
import vip.phantom.helper.Helper;

import java.awt.*;

public class FoodLabel {
    @Getter
    @Setter
    private float x, y, width, height;
    @Getter
    private final Food food;
    @Getter
    private final TimeUtil timer = new TimeUtil();
    private final SoundPlayer player = new SoundPlayer();
    private final FontRenderer fr = Fonts.getInstance().getFonts().get("BrushTip20.0");
    private final FontRenderer fr1 = Fonts.getInstance().getFonts().get("Verdana17.0");

    private final long addTime = System.currentTimeMillis();

    public FoodLabel(Food food) {
        this.food = food;
        width = 3 + fr.getWidth(food.getName()) + 3;
        height = fr.getHeight();
        timer.reset();
    }

    public void drawLabel(int mouseX, int mouseY) {
        final boolean hovered = isHovered(mouseX, mouseY);
        final Color cookingColor = timer.hasReached(food.getUndercooked()) ? timer.hasReached(food.getCooked()) ? timer.hasReached(food.getBurnt()) ? Color.black : Color.green : Color.orange : Color.pink;
        RenderUtil.beginScissor(x, y, width * Math.max(0, Math.min(1, (System.currentTimeMillis() - addTime) / 300f)), height);
        RenderUtil.drawRect(x, y, x + width, y + height, hovered ? cookingColor.darker() : cookingColor);
        fr.drawString(food.getName(), x + 5, y + height / 2f - fr.getHeight(food.getName()) / 2f, timer.hasReached(food.getBurnt()) ? Color.white : Color.black);
        final String leftTimeTillCooked = "(" + String.format("%.1f", (timer.getCurrentTime() + food.getCooked() - System.currentTimeMillis()) / 1000f) + "s)";
        fr1.drawString(leftTimeTillCooked, x + 5 + fr.getWidth(food.getName()) + 3, y + height / 2f + fr.getHeight(food.getName()) / 2f - fr1.getHeight(leftTimeTillCooked), timer.hasReached(food.getBurnt()) ? Color.white : Color.black);
        RenderUtil.endScissor();
        width = 5 + fr.getWidth(food.getName()) + 3 + fr1.getWidth(leftTimeTillCooked) + 5;
        if (timer.hasReached(food.getCooked()) && !player.isPlaying()) {
            System.out.println("starting to play");
            player.start("src/main/sounds/ticktock.wav");
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 1) {
                Helper.getInstance().removeFoodFromCookList(this);
                player.stop();
            } else {
                player.start("src/main/sounds/ticktock.wav");
            }
        }
    }

    private boolean isHovered(float mouseX, float mouseY) {
        return RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
    }
}
