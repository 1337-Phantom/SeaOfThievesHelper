package vip.phantom.helper;

import lombok.Getter;
import vip.phantom.api.TimeUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class AntiAFK {
    private AntiAFKThread actionThread;
    private boolean walkCircles = false;
    private boolean useStaringWheel = false;
    private boolean moveMouse = false;
    private boolean hitWithMouse = false;
    private int keyCycle = 0;
    @Getter
    private long startDelay = 0;
    private final TimeUtil keyTimer = new TimeUtil(), mouseMoveTimer = new TimeUtil(), clickTimer = new TimeUtil();
    private final Robot robot;

    public AntiAFK() throws AWTException {
        robot = new Robot();
    }

    public void startAntiAFK(boolean walkCircles, boolean useStaringWheel, boolean moveMouse, boolean hitWithMouse) {
        this.walkCircles = walkCircles;
        this.useStaringWheel = useStaringWheel;
        this.moveMouse = moveMouse;
        this.hitWithMouse = hitWithMouse;
        actionThread = new AntiAFKThread();
    }

    public boolean isActive() {
        return actionThread == null || actionThread.isAlive();
    }

    public int randomInRange(int min, int max) {
        if (min > max) {
            System.err.println("The minimal value cannot be higher than the max value");
            return min;
        }
        max -= min;
        return (int) Math.round(Math.random() * (max)) + min;
    }

    public void stopAntiAFK() {
        actionThread.stop();
        robot.keyRelease(KeyEvent.VK_W);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_D);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private class AntiAFKThread implements Runnable {
        private boolean exit;
        private final Thread thread;

        public boolean isAlive() {
            return !exit;
        }

        public AntiAFKThread() {
            thread = new Thread(this, "AntiAFKActionThread");
            exit = false;
            thread.start();
        }

        public void stop() {
            exit = true;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(startDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!this.exit) {
                if (walkCircles) {
                    if (keyTimer.hasReached(randomInRange(900, 1100))) {
                        switch (keyCycle) {
                            case 0:
                                robot.keyRelease(KeyEvent.VK_D);
                                robot.keyPress(KeyEvent.VK_W);
                                break;
                            case 1:
                                robot.keyRelease(KeyEvent.VK_W);
                                robot.keyPress(KeyEvent.VK_A);
                                break;
                            case 2:
                                robot.keyRelease(KeyEvent.VK_A);
                                robot.keyPress(KeyEvent.VK_S);
                                break;
                            case 3:
                                robot.keyRelease(KeyEvent.VK_S);
                                robot.keyPress(KeyEvent.VK_D);
                                keyCycle = -1;
                                break;
                            default:
                                keyCycle = -1;
                        }
                        keyCycle++;
                        keyTimer.reset();
                    }
                } else if (useStaringWheel) {
                    if (keyTimer.hasReached(randomInRange(900, 1100))) {
                        switch (keyCycle) {
                            case 0:
                                robot.keyRelease(KeyEvent.VK_D);
                                robot.keyPress(KeyEvent.VK_A);
                                break;
                            case 1:
                                robot.keyRelease(KeyEvent.VK_A);
                                robot.keyPress(KeyEvent.VK_D);
                                keyCycle = -1;
                                break;
                            default:
                                keyCycle = -1;
                        }
                        keyCycle++;
                        keyTimer.reset();
                    }
                }
                if (moveMouse) {
                    if (mouseMoveTimer.hasReached(randomInRange(1, 50))) {
                        final Point pointerInfo = MouseInfo.getPointerInfo().getLocation();
                        robot.mouseMove(pointerInfo.x + randomInRange(-3, 3), pointerInfo.y + randomInRange(-2, 2));
                        mouseMoveTimer.reset();
                    }
                }
                if (hitWithMouse) {
                    if (clickTimer.hasReached(randomInRange(500, 3000))) {
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        clickTimer.reset();
                    }
                }
            }
        }
    }
}
