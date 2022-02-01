package vip.phantom.helper;

import lombok.Getter;
import lombok.Setter;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.lwjgl.opengl.Display;
import vip.phantom.api.ConnectionUtil;
import vip.phantom.api.TimeUtil;
import vip.phantom.api.font.Fonts;
import vip.phantom.helper.food.Food;
import vip.phantom.helper.food.FoodButton;
import vip.phantom.helper.food.FoodLabel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Helper {
    private static Helper instance;
    @Getter
    @Setter
    private float width, height;
    private AntiAFK antiAFK;
    private boolean doAntiAFK = false, minusDown = false;

    private TrayIcon hotbarIcon;

    private TimeUtil timeUtil = new TimeUtil();
    private float timeOfKeyUp = 0;

    private final List<Food> foodList = new ArrayList<>();
    private final List<FoodButton> foodButtonList = new ArrayList<>();
    private final List<FoodLabel> foodLabels = new ArrayList<>(), labelsToRemove = new ArrayList<>();

    public Helper() {
        /* initializing all fonts */
        Fonts.getInstance();
        start();
        try {
            antiAFK = new AntiAFK();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        /* adding all the food there is in Sea of Thieves */
        try {
            foodList.add(new Food("Megalodon", 100, 120, 240, "https://static.wikia.nocookie.net/seaofthieves_gamepedia/images/3/34/Megalodon_%28meat%29.png/revision/latest/scale-to-width-down/250?cb=20200607213216"));
            foodList.add(new Food("Kraken", 100, 120, 240, "https://static.wikia.nocookie.net/seaofthieves_gamepedia/images/f/f4/Kraken_%28meat%29.png/revision/latest/scale-to-width-down/250?cb=20201001021131"));
            foodList.add(new Food("Pork", 50, 60, 120, "https://static.wikia.nocookie.net/seaofthieves_gamepedia/images/2/22/Pork.png/revision/latest/scale-to-width-down/250?cb=20200518230717"));
            foodList.add(new Food("Chicken", 50, 60, 120, "https://static.wikia.nocookie.net/seaofthieves_gamepedia/images/c/cc/Chicken_%28meat%29.png/revision/latest/scale-to-width-down/250?cb=20200602055543"));
//            foodList.add(new Food("Fish", 30, 45, 60, new File("src/main/resources/Fishi Mc Fish.png")));
            foodList.add(new Food("Fish", 5, 10, 20, "https://phantom.vip/images/SOT/FishiMcFish.png"));
            foodList.add(new Food("Snake", 50, 60, 120, "https://static.wikia.nocookie.net/seaofthieves_gamepedia/images/7/78/Snake_%28meat%29.png/revision/latest/scale-to-width-down/250?cb=20200602053030"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Food food : foodList) {
            foodButtonList.add(new FoodButton(food));
        }
    }

    public void updatePositions(float screenWidth, float screenHeight) {
        this.width = screenWidth;
        this.height = screenHeight;
        float foodX = 5;
        float foodY = 25;
        for (FoodButton foodButton : foodButtonList) {
            foodButton.setX(foodX);
            foodButton.setY(foodY);
            foodButton.setWidth(foodButton.getFood().getPictureSize()[0] / 1.5f);
            foodButton.setHeight(foodButton.getWidth() / foodButton.getFood().getPictureSize()[0] * foodButton.getFood().getPictureSize()[1]);
            foodX += foodButton.getWidth() + 5;
        }
    }

    public void drawScreen(int mouseX, int mouseY) {
        for (FoodButton foodButton : foodButtonList) {
            foodButton.drawScreen(mouseX, mouseY);
        }
        float x = 5;
        for (FoodLabel foodLabel : foodLabels) {
            foodLabel.setX(x);
            foodLabel.drawLabel(mouseX, mouseY);
            x += foodLabel.getWidth() + 5;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (FoodButton foodButton : foodButtonList) {
            foodButton.mouseClicked(mouseX, mouseY, mouseButton);
        }
        labelsToRemove.clear();
        for (FoodLabel foodLabel : foodLabels) {
            foodLabel.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (!labelsToRemove.isEmpty()) {
            for (FoodLabel foodLabel : labelsToRemove) {
                foodLabels.remove(foodLabel);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (FoodButton foodButton : foodButtonList) {
            foodButton.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public void keyTyped(char character, int key) {
        for (FoodButton foodButton : foodButtonList) {
            foodButton.keyTyped(character, key);
        }
    }

    public void start() {
        try {
            hotbarIcon = new TrayIcon(ConnectionUtil.getBufferedImageWithUserAgent(new URL("https://phantom.vip/images/SOT/logo.png")), "Tray Demo");

            hotbarIcon.setImageAutoSize(true);

            hotbarIcon.setToolTip("Sea of Thieves Helper");
            try {
                SystemTray.getSystemTray().add(hotbarIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        SystemTray.getSystemTray().remove(hotbarIcon);
    }

    public void enableOrDisableAntiAFK() {
        doAntiAFK = !doAntiAFK;
        if (doAntiAFK) {
            System.out.println("[AntiAFK] Activated AntiAFK");
            antiAFK.startAntiAFK(false, true, true, true);

//            Helper.getInstance().sendWindowsNotification("AntiAFK", "activating in " + antiAFK.getStartDelay() / 1000 + " seconds");
        } else {
            System.out.println("[AntiAFK] Deactivated AntiAFK");
            antiAFK.stopAntiAFK();
//            Helper.getInstance().sendWindowsNotification("AntiAFK", "was disabled");
        }
    }

    public void sendWindowsNotification(String title, String text) {
        hotbarIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }

    public void globalKeyTyped(NativeKeyEvent e) {
        if (e.getKeyChar() == "-".charAt(0)) {
            if (!minusDown) {
                minusDown = true;
                timeOfKeyUp = System.currentTimeMillis();
                timeUtil.reset();
            }
            System.out.println("[AntiAFK] " + (antiAFK.isActive() ? "Deactivating" : "Activating") + " in " + (timeUtil.getCurrentTime() + 2000 - System.currentTimeMillis()));
            if (timeUtil.hasReached(2000)) {
                enableOrDisableAntiAFK();
                timeUtil.reset();
            }
        }

    }

    public void globalKeyPressed(NativeKeyEvent e) {
    }

    public void globalKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_MINUS) {
//            if (timeUtil.hasReached(2000)) {
//
//            }
            minusDown = false;
        }
    }

    public void addFoodToCookList(Food food) {
        final FoodLabel newLabel = new FoodLabel(food);
        final FoodLabel prevLabel = foodLabels.isEmpty() ? null : foodLabels.get(foodLabels.size() - 1);
        newLabel.setX(prevLabel == null ? 5 : prevLabel.getX() + prevLabel.getWidth() + 5);
        newLabel.setY(Display.getHeight() - newLabel.getHeight() - 5);
        foodLabels.add(newLabel);
        foodLabels.sort(new Comparator<FoodLabel>() {
            @Override
            public int compare(FoodLabel o1, FoodLabel o2) {
                return Long.compare(o1.getTimer().getCurrentTime() + o1.getFood().getCooked(), o2.getTimer().getCurrentTime() + o2.getFood().getCooked());
            }
        });
    }

    public void removeFoodFromCookList(FoodLabel foodLabel) {
        labelsToRemove.add(foodLabel);
    }


    public static Helper getInstance() {
        if (instance == null) {
            instance = new Helper();
        }
        return instance;
    }
}
        
    