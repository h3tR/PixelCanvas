package ml.jozefpeeterslaan72wuustwezel.jade;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class MouseListener {
    private static MouseListener mouseListener = null;
    private double ScrollX, ScrollY, xPos, yPos, lastX, lastY;
    private final boolean[] mouseButtonPressed = new boolean[3];
    private boolean isDragging;
    public static Vector2f ViewportWindowPos = new Vector2f();
    public static Vector2f ViewportWindowSize = new Vector2f();


    private MouseListener(){
        this.ScrollX = 0.0d;
        this.ScrollY = 0.0d;
        this.xPos = 0.0d;
        this.yPos = 0.0d;
        this.lastX = 0.0d;
        this.lastY = 0.0d;
    }

    public static MouseListener get(){
        if(mouseListener==null)
            mouseListener = new MouseListener();
        return mouseListener;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void MouseButtonCallback(long window, int button, int action, int modifiers){
        if(button < get().mouseButtonPressed.length) {
            if (action == GLFW.GLFW_PRESS) {
                get().mouseButtonPressed[button] = true;
            } else if (action == GLFW.GLFW_RELEASE) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void MouseScrollCallback(long window, double xOffset, double yOffset){
        get().ScrollX = xOffset;
        get().ScrollY = yOffset;
    }

    public static void endFrame() {
        get().ScrollX = 0;
        get().ScrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX(){
        return (float)get().xPos;
    }
    public static float getY(){
        return (float)get().yPos;
    }
   /*TODO:
      public static float getOrthoX() {
        float normalX = ((float)get().xPos/(float) Window.get().getWidth())*2f-1f;
        Vector4f vec = new Vector4f(normalX,0,0,1);
        Camera camera = Window.getCurrentScene().getCamera();
        vec.mul(camera.getInverseProjection()).mul(camera.getInverseView());
        return vec.x;
    }
    public static float getOrthoY() {
        float normalY = -((float)(Window.get().getHeight()-get().yPos)/(float) Window.get().getHeight())*2f-1f;
        Vector4f vec = new Vector4f(0,normalY,0,1);
        Camera camera = Window.getCurrentScene().getCamera();
        vec.mul(camera.getInverseProjection()).mul(camera.getInverseView());
        return vec.y;
    }*/

    public static float getDX(){
        return (float)(get().lastX-get().xPos);
    }
    public static float getDY(){
        return (float)(get().lastY-get().yPos);
    }
    public static float getScrollX(){
        return (float)get().ScrollX;
    }
    public static float getScrollY(){
        return (float)get().ScrollY;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if(button<get().mouseButtonPressed.length)
            return get().mouseButtonPressed[button];
        return false;
    }
}
