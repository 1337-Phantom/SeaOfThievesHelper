package vip.phantom.helper.food;

import lombok.Getter;
import lombok.Setter;
import vip.phantom.api.RenderUtil;
import vip.phantom.api.font.FontRenderer;
import vip.phantom.api.font.Fonts;
import vip.phantom.helper.Helper;

import java.awt.*;

public class FoodButton {
    @Getter
    @Setter
    private float x, y, width, height;
    @Getter
    private final Food food;
    private final String timeString;
    private final FontRenderer fr = Fonts.getInstance().getFonts().get("BrushTip20.0");
    private long hoveringSwitchTime;
    private boolean lastHoveringState = false;
    private int alpha = 0;

    public FoodButton(Food food) {
        this.food = food;
        this.timeString = "(" + food.getCooked() / 1000 + "s)";
    }

    public void drawScreen(int mouseX, int mouseY) {
        final boolean hovered = isHovered(mouseX, mouseY);
        RenderUtil.drawPicture(x, y, width, height, food.getTextureID(), false);
        if (lastHoveringState != hovered) {
            hoveringSwitchTime = System.currentTimeMillis();
        }
        if (hovered) {
            alpha = (int) (255 * Math.max(0, Math.min(1, (System.currentTimeMillis() - hoveringSwitchTime) / 400f)));
            RenderUtil.drawRect(x, y + height / 2f - fr.getHeight() / 2f, x + width, y + height / 2f + fr.getHeight() / 2f, new Color(15, 15, 15, (int) (130 * (alpha / 255f))));
            fr.drawString(timeString, x + width / 2f - fr.getWidth(timeString) / 2f, y + height / 2f - fr.getHeight() / 3f, Color.white);
        }
        lastHoveringState = hovered;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            Helper.getInstance().addFoodToCookList(food);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public void keyTyped(char character, int key) {

    }

    private boolean isHovered(float mouseX, float mouseY) {
        return RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
    }


}
