package ml.jozefpeeterslaan72wuustwezel.jade;

import org.lwjgl.glfw.GLFW;

public class KeyListener {
    private static KeyListener keyListener;
    private final boolean[] keyPressed = new boolean[350];
    private KeyListener(){}
    public static KeyListener get(){
        if(keyListener==null)
            keyListener = new KeyListener();
        return keyListener;

    }
    public static void KeyCallback(long window, int key, int scancode,int action,int modifiers){
        if(action== GLFW.GLFW_PRESS)
            get().keyPressed[key] = true;
        else if (action==GLFW.GLFW_RELEASE)
            get().keyPressed[key] = false;
    }

    public static boolean keyDown(int keyCode){
        if(keyCode < get().keyPressed.length)
            return get().keyPressed[keyCode];
        throw new NullPointerException("No Such Keycode: "+ keyCode);
    }
}
