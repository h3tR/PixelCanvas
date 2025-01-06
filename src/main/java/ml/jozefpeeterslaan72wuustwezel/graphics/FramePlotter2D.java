package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class FramePlotter2D {
    private final byte[] framebuffer;
    private final Vector2i dimensions;
    public FramePlotter2D(Vector2i dimensions) {
        this.framebuffer = new byte[dimensions.x * dimensions.y*3];
        this.dimensions = dimensions;
    }


    public void plotPixel(Vector2i position, Vector3f color) {
        Vector2i realPos = new Vector2i(position.x+dimensions.x/2, -position.y+dimensions.y/2);
        if (realPos.x < 0 || realPos.x >= dimensions.x || realPos.y < 0 || realPos.y >= dimensions.y) {
            return;
        }

        int index = 3* (realPos.y*dimensions.x+realPos.x);
        Vector3f byteColor = new Vector3f(color).mul(255).round();
        framebuffer[index++] = (byte)byteColor.x;
        framebuffer[index++] = (byte)byteColor.y;
        framebuffer[index] = (byte)byteColor.z;
    }

    public void plotLine(Vector2i p1,Vector2i p2, Vector3f color) {
        int dx = p1.x-p2.x;
        int dy = p1.y-p2.y;

        if(dx==0&&dy==0) {
            plotPixel(p1, color);
            return;
        }
        // x = f(y)
        if(dx==0||Math.abs(dy)>Math.abs(dx)){
            int ysign = -dy/Math.abs(dy);

            for (int y = 0; y < Math.abs(dy); y++)
                plotPixel(new Vector2i((int)((float)dx/dy*y*ysign), ysign * y).add(p1), color);
            return;
        }
        // y=f(x)
        int xsign = -dx/Math.abs(dx);
        for (int x = 0; x < Math.abs(dx); x++)
            plotPixel(new Vector2i(x*xsign,xsign*x*dy/dx).add(p1), color);
    }

    public void plotTriangle(Vector2i p1, Vector2i p2, Vector2i p3, Vector3f color) {
        plotLine(p1,p2,color);
        plotLine(p3,p1,color);
        plotLine(p3,p2,color);
    }

    public void plotSquare(Vector2i p1, Vector2i p2, Vector3f color) {
        plotLine(p1,new Vector2i(p2.x,p1.y),color);
        plotLine(p1,new Vector2i(p2.x,p2.y),color);
        plotLine(p2,new Vector2i(p1.x,p2.y),color);
        plotLine(p2,new Vector2i(p2.x,p1.y),color);
    }
    public void fillSquare(Vector2i position, Vector2i size, Vector3f color) {
        for (int x = -size.x/2; x < size.x/2; x++)
            for (int y = -size.y/2; y < size.y/2; y++)
                plotPixel(new Vector2i(position.x+x,position.y+y), color);
    }



    public void draw(){
        glRasterPos2i(-1,-1);
        ByteBuffer buffer = BufferUtils.createByteBuffer(framebuffer.length);
        buffer.put(framebuffer).flip();
        GL30.glDrawPixels(dimensions.x,dimensions.y, GL11.GL_RGB,GL11.GL_UNSIGNED_BYTE, buffer);
    }
}
