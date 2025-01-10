package ml.jozefpeeterslaan72wuustwezel.jade;

import ml.jozefpeeterslaan72wuustwezel.graphics.*;
import ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D.Camera3D;
import ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D.Mesh;
import ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D.Transform3D;
import org.joml.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;


public class Window {
    private Vector2i dimensions;
    private final String Title;
    private static Window window = null;
    private long glfwWindow = 0;
    private Window(){
        this.dimensions = new Vector2i(800,600);
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
        glfwWindow = GLFW.glfwCreateWindow(dimensions.x, dimensions.y, Title, MemoryUtil.NULL,MemoryUtil.NULL);
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
        Camera3D camera3D = new Camera3D(new Vector3f(0,0,0),new Vector3f(0),70, (float) dimensions.x /dimensions.y);
        //Camera3D amera3D = new Camera3D(new Vector3f(0),new Vector3f(0),70, (float) dimensions.x /dimensions.y);

        float camSpeed = 1/10f;
        float camRotationSpeed = 3.141592f/180*30; //10°
        Map<Integer,Vector3f> MovementVectors = new HashMap<>();
        MovementVectors.put(87,new Vector3f(0,0,1));//Z=>W
        MovementVectors.put(65,new Vector3f(1,0,0));//Q=>A
        MovementVectors.put(83,new Vector3f(0,0,-1));//S
        MovementVectors.put(68,new Vector3f(-1,0,0));//D
        MovementVectors.put(69,new Vector3f(0,1,0));//E
        MovementVectors.put(67,new Vector3f(0,-1,0));//C
        Map<Integer,Vector3f> RotationVectors = new HashMap<>();
        RotationVectors.put(265,new Vector3f(1,0,0));//UP
        RotationVectors.put(264,new Vector3f(-1,0,0));//DOWN
        RotationVectors.put(263,new Vector3f(0,-1,0));//LEFT
        RotationVectors.put(262,new Vector3f(0,1,0));//RIGHT

        Mesh teapot = Mesh.fromObj("assets/meshes/teapot.obj");
        Texture barak = Texture.fromFile("assets/textures/barak.png");
        Vector3f[] samplebarak = barak.sample(new Vector2i(0,0),new Vector2i(680,600));
        float i = 2;
        float factor = .02f;
        while(!GLFW.glfwWindowShouldClose(glfwWindow)){
            //Poll Events
            GLFW.glfwPollEvents();
            FramePlotter2D plotter2D = new FramePlotter2D(dimensions);
            Transform3D transform3D = new Transform3D(new Vector3f(0,0,10),new Vector3f(0),new Vector3f(1));


            plotter2D.plotPixels(new Vector2i(-400,-300),new Vector2i(680,600),samplebarak);


            plotter2D.fillCircle(new Vector2i(-200,-200),4,Color.RED);
            plotter2D.fillCircle(new Vector2i(200,-200),4,Color.GREEN);
            plotter2D.fillCircle(new Vector2i(-200,200),4,Color.YELLOW);
            plotter2D.fillCircle(new Vector2i(200,200),4,Color.CYAN);

            teapot.render(camera3D,plotter2D,new Vector3f(Color.WHITE),transform3D);
           // teapot.renderWireFrame(camera3D,plotter2D,Color.PINK,transform3D);


            FrameEnded = (float)GLFW.glfwGetTime();
            deltaTime = FrameEnded - FrameStarted;
            FrameStarted = FrameEnded;
            if(KeyListener.keyDown(32)) //space
                System.out.println(1f/deltaTime+" FPS");

            for(Integer keyCode: RotationVectors.keySet())
                if(KeyListener.keyDown(keyCode))
                    camera3D.setRotation(camera3D.getRotation().add(new Vector3f(RotationVectors.get(keyCode)).mul(camRotationSpeed)));
            for(Integer keyCode: MovementVectors.keySet())
                if(KeyListener.keyDown(keyCode))//{
                    camera3D.setPosition(camera3D.getPosition().add(new Vector3f(MovementVectors.get(keyCode)).mul(camSpeed)));

/*
                    Vector3f rotatedMovement = new Vector3f();
                    MovementVectors.get(keyCode).mul(camera3D.getLookVector(),rotatedMovement);
                    camera3D.setPosition(camera3D.getPosition().add(rotatedMovement));
                }*/
/*
            System.out.println(camera3D.getPosition());
            Vector2i cVec = new Vector2i((int) (amera3D.project3DPoint(new Vector4f(camera3D.getPosition(),1)).x*dimensions.x), (int) (amera3D.project3DPoint(new Vector4f(camera3D.getPosition(),1)).y*dimensions.y));
            Vector2i cLVec = new Vector2i((int) (amera3D.project3DPoint(new Vector4f(camera3D.getLookVector().add(camera3D.getPosition()),1)).x*dimensions.x), (int) (amera3D.project3DPoint(new Vector4f(camera3D.getLookVector().add(camera3D.getPosition()),1)).y*dimensions.y));
            plotter2D.fillCircle(cVec,3,Color.CYAN);
            plotter2D.plotLine(cLVec,cVec,Color.BLUE);*/
            plotter2D.draw();
            GLFW.glfwSwapBuffers(glfwWindow);

            i+=factor;
            if(i>200||i==1)
                factor*=-1;
        }
    }
}
