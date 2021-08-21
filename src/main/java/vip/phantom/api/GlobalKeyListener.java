package vip.phantom.api;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import vip.phantom.helper.Helper;

public class GlobalKeyListener implements NativeKeyListener {

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        Helper.getInstance().globalKeyTyped(e);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        Helper.getInstance().globalKeyPressed(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        Helper.getInstance().globalKeyReleased(e);
    }
}
