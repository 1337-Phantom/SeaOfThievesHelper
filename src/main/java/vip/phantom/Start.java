package vip.phantom;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import vip.phantom.api.GlobalKeyListener;
import vip.phantom.helper.Helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

public class Start {

    //java -jar -Djava.library.path="C:\Users\hanyo\Desktop\SeaOfThievesHelper1\workspace\natives" SeaOfThievesHelper.jar

    private float oldDisplayWidth, oldDisplayHeight;

    private static Helper helper;

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        new Start().run(args);
    }

    public void run(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        try {
            Display.setDisplayMode(new DisplayMode(720, 400));
            Display.setTitle("Sea of Thieves Helper by Phantom");
            Display.setResizable(true);
            Display.setInitialBackground(0, 0, 0);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        /*replaced this with the updateDisplay method which does this every time when the window gets resized
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL_MODELVIEW);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW); */

        helper = Helper.getInstance();

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            /* doing all which has to do with mouse */
            if (Mouse.isCreated()) {
                while (Mouse.next()) {
                    if (Mouse.getEventButtonState()) {
                        mouseClicked(Mouse.getX(), Display.getHeight() - Mouse.getY(), Mouse.getEventButton());
                    } else if (Mouse.getEventButton() != -1) {
                        mouseReleased(Mouse.getX(), Display.getHeight() - Mouse.getY(), Mouse.getEventButton());
                    }
                }
            }
            /* doing all which has to do with keyboard */
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
                }
            }
            /* doing all which has to do with rendering */
            drawScreen(Mouse.getX(), Display.getHeight() - Mouse.getY());
            updateDisplay();
        }
        Helper.getInstance().stop();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException nativeHookException) {
            nativeHookException.printStackTrace();
        }
    }

    private void drawScreen(int mouseX, int mouseY) {
        helper.drawScreen(mouseX, mouseY);
    }

    private void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        helper.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        helper.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void keyTyped(char character, int key) {
        helper.keyTyped(character, key);
    }

    private void updateDisplay() {
        if (oldDisplayWidth != Display.getWidth() || oldDisplayHeight != Display.getHeight()) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glMatrixMode(GL_MODELVIEW);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
            glMatrixMode(GL_MODELVIEW);

            helper.updatePositions(Display.getWidth(), Display.getHeight());
        }
        oldDisplayWidth = Display.getWidth();
        oldDisplayHeight = Display.getHeight();
        Display.update();
    }
}
