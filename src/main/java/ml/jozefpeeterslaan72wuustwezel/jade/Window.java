package ml.jozefpeeterslaan72wuustwezel.jade;

import ml.jozefpeeterslaan72wuustwezel.graphics.FramePlotter2D;
import ml.jozefpeeterslaan72wuustwezel.graphics.Renderer3D;
import org.joml.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

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
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE,GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED,GLFW.GLFW_FALSE);
        //GLFW.glfwSetWindowIcon(glfwWindow,...);

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
        Random rand = new Random();
        Renderer3D renderer3D = new Renderer3D(new Vector3f(0),new Vector3f(0));

        float camSpeed = .02f;
        Map<Integer,Vector3f> MovementVectors = new HashMap<>();
        MovementVectors.put(90,new Vector3f(0,0,1));//Z
        MovementVectors.put(81,new Vector3f(-1,0,0));//Q
        MovementVectors.put(83,new Vector3f(0,0,-1));//S
        MovementVectors.put(68,new Vector3f(1,0,0));//D
        MovementVectors.put(69,new Vector3f(0,-1,0));//E
        MovementVectors.put(67,new Vector3f(0,1,0));//C
        Map<Integer,Vector3f> RotationVectors = new HashMap<>();
        RotationVectors.put(264,new Vector3f(-1,0,0));//UP
        RotationVectors.put(265,new Vector3f(1,0,0));//DOWN
        RotationVectors.put(263,new Vector3f(0,-1,0));//LEFT
        RotationVectors.put(262,new Vector3f(0,1,0));//RIGHT



        float i = 0;
        float factor = .2f;
        while(!GLFW.glfwWindowShouldClose(glfwWindow)){
            //Poll Events
            GLFW.glfwPollEvents();
            //glClear(GL_COLOR_BUFFER_BIT);
            FramePlotter2D plotter2D = new FramePlotter2D(Dimensions);
            Vector3f[] cubeVertices = new Vector3f[]{
                    new Vector3f(-1,-1,-1),new Vector3f(-1,-1,1),new Vector3f(-1,1,-1),new Vector3f(-1,1,1),
                    new Vector3f(1,-1,-1),new Vector3f(1,-1,1), new Vector3f(1,1,-1),new Vector3f(1,1,1)
            };
            int[][] polygons = new int[][]{
                    {1,5,7},{1,7,3},//back
                    {0,1,3},{0,3,2},//left
                    {4,0,3},{4,3,2},//front
                    {5,4,6},{5,6,7},//right
                    {3,7,6},{3,6,2},//top
                    {0,3,5},{0,5,1}//bottom
            };
            for(int[] polygon : polygons){
                Vector2f p0 = renderer3D.project3DPoint(new Vector3f(cubeVertices[polygon[0]]).add(0,0,10));
                Vector2f p1 = renderer3D.project3DPoint(new Vector3f(cubeVertices[polygon[1]]).add(0,0,10));
                Vector2f p2 = renderer3D.project3DPoint(new Vector3f(cubeVertices[polygon[2]]).add(0,0,10));
                plotter2D.plotTriangle(
                        new Vector2i((int) (p0.x*Dimensions.x), (int) (p0.y*Dimensions.y)),
                        new Vector2i((int) (p1.x*Dimensions.x), (int) (p1.y*Dimensions.y)),
                        new Vector2i((int) (p2.x*Dimensions.x), (int) (p2.y*Dimensions.y)),
                        new Vector3f(1,1,1)
                );
            }

            //plotter2D.fillTriangle(new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector3f(1,0,0));
           // plotter2D.fillTriangle(new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector3f(0,1,0));
          //  plotter2D.fillTriangle(new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector2i(rand.nextInt(Dimensions.x)-Dimensions.x/2,rand.nextInt(Dimensions.y)-Dimensions.y/2),new Vector3f(0,0,1));

            plotter2D.draw();

            GLFW.glfwSwapBuffers(glfwWindow);

            FrameEnded = (float)GLFW.glfwGetTime();
            deltaTime = FrameEnded - FrameStarted;
            FrameStarted = FrameEnded;
            if(KeyListener.keyDown(32)) //space
                System.out.println(1f/deltaTime+" FPS");
            for(Integer keyCode: MovementVectors.keySet())
                if(KeyListener.keyDown(keyCode))
                   renderer3D.camPos.add(new Vector3f(MovementVectors.get(keyCode)).mul(camSpeed));

            for(Integer keyCode: RotationVectors.keySet())
                if(KeyListener.keyDown(keyCode))
                    renderer3D.camRot.add(new Vector3f(RotationVectors.get(keyCode)).mul(camSpeed));

            i+=factor;
            if(Math.abs(i)>10)
                factor*=-1;
        }
    }
}
