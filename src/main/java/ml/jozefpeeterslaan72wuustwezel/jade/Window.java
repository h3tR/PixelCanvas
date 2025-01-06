package ml.jozefpeeterslaan72wuustwezel.jade;

import ml.jozefpeeterslaan72wuustwezel.graphics.FramePlotter2D;
import org.joml.Random;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;


public class Window {
    private Vector2i Dimensions;
    private final String Title;
    private static Window window = null;
    private long glfwWindow = 0;
    private Window(){
        this.Dimensions = new Vector2i(800,600);
        this.Title = "Joe Game";
    }


    public static Window get(){
        if(window==null)
            window = new Window();
        return window;
    }

    public void run(){
        System.out.println("Game Running.  LWJGL version: "+ Version.getVersion());
        init();
        loop();

        //Free Memory
        //GLFW.glfwFreeCallbacks <- doesn't exist?
        GLFW.glfwDestroyWindow(glfwWindow);
        //End Process
        GLFW.glfwTerminate();
        //noinspection DataFlowIssue
        GLFW.glfwSetErrorCallback(null).free();
    }
    public void init(){
        //Setup Error callback Stream
        GLFWErrorCallback.createPrint(System.err).set();

        //Init GLFW
        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        //Config
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE,GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED,GLFW.GLFW_FALSE);

        //Create Window
        glfwWindow = GLFW.glfwCreateWindow(Dimensions.x, Dimensions.y, Title, MemoryUtil.NULL,MemoryUtil.NULL);
        if(glfwWindow == MemoryUtil.NULL)
            throw new IllegalStateException("Failed to create GLFW Window");

        GLFW.glfwSetCursorPosCallback(glfwWindow,MouseListener::mousePosCallback);
        GLFW.glfwSetScrollCallback(glfwWindow,MouseListener::MouseScrollCallback);
        GLFW.glfwSetMouseButtonCallback(glfwWindow,MouseListener::MouseButtonCallback);
        GLFW.glfwSetKeyCallback(glfwWindow,KeyListener::KeyCallback);
       //TODO GLFW.glfwSetWindowSizeCallback(glfwWindow,(w,newWidth,newHeight)->{width = newWidth; height = newHeight; GL30.glViewport(0, 0, width, height);});
        //Make OpenGL context current
        GLFW.glfwMakeContextCurrent(glfwWindow);
        //Enable V-Sync
        GLFW.glfwSwapInterval(1);

        //Make Window Visible
        GLFW.glfwShowWindow(glfwWindow);

        //Enable bindings
        GL.createCapabilities();
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

    }
    public void loop(){
        float FrameStarted = (float)GLFW.glfwGetTime();
        float FrameEnded = FrameStarted;
        float deltaTime = -1.0f;

        FramePlotter2D plotter2D = new FramePlotter2D(Dimensions);
        Random rand = new Random();




        GLFW.glfwSwapBuffers(glfwWindow);
        while(!GLFW.glfwWindowShouldClose(glfwWindow)){
            //Poll Events
            GLFW.glfwPollEvents();
            //glClear(GL_COLOR_BUFFER_BIT);

            plotter2D.plotTriangle(new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector3f(1,0,0));
            plotter2D.plotTriangle(new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector3f(0,1,0));
            plotter2D.plotTriangle(new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector3f(0,0,1));

            plotter2D.draw();

            GLFW.glfwSwapBuffers(glfwWindow);

            FrameEnded = (float)GLFW.glfwGetTime();
            deltaTime = FrameEnded - FrameStarted;
            FrameStarted = FrameEnded;
            if(KeyListener.keyDown(32)) //space
                System.out.println(1f/deltaTime+" FPS");
        }
    }
}
