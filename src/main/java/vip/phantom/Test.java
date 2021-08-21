package vip.phantom;

import vip.phantom.api.TimeUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Test {
    public static Robot robot;

    public static void main(String[] args) throws AWTException, InterruptedException {
        robot = new Robot();
        Thread.sleep(5000);
        run1();
    }

    public static void run1() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
    }

    public static void run2() {
        final long time = System.currentTimeMillis();
        final TimeUtil timeUtil1 = new TimeUtil();
        final TimeUtil timeUtil = new TimeUtil();
        System.out.println("starting keypress");
        robot.keyPress(KeyEvent.VK_W);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        while (true) {
            if (timeUtil.hasReached(5000)) {
                System.out.println("ending keypress");
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_W);
                System.exit(0);
            }
        }
    }
}
