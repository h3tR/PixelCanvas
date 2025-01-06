package ml.jozefpeeterslaan72wuustwezel.graphics;

import ml.jozefpeeterslaan72wuustwezel.wrapper.ImmutablePair;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;

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
        List<Vector2i> Pixels = getLineData(p1,p2);
        for (int i = 0; i < Pixels.size(); i++)
            plotPixel(Pixels.get(i), color);
    }

    public void plotTriangle(Vector2i p1, Vector2i p2, Vector2i p3, Vector3f color) {
        plotLine(p1,p2,color);
        plotLine(p3,p1,color);
        plotLine(p3,p2,color);
    }
    public void fillTriangle(Vector2i p1, Vector2i p2, Vector2i p3, Vector3f color){
        Set<Vector2i> Edges = new HashSet<>(getLineData(p1,p2));
        Edges.addAll(getLineData(p2,p3));
        Edges.addAll(getLineData(p3,p1));
        //remove duplicate pixels
        List<Vector2i> filteredEdges = new ArrayList<>(Edges.stream().toList());
        filteredEdges.sort(Comparator.comparingInt(v -> v.x));

        int minX = filteredEdges.get(0).x;
        int maxX = filteredEdges.get(filteredEdges.size()-1).x;
        int dx = maxX - minX+1;
        int[] maxY = new int[dx];
        Arrays.fill(maxY, -dimensions.y/2);
        int[] minY = new int[dx];
        Arrays.fill(minY, dimensions.y/2);

        for (int i = 0; i < filteredEdges.size(); i++){
            Vector2i pixel = filteredEdges.get(i);
            if(maxY[pixel.x-minX]<pixel.y)
                maxY[pixel.x-minX]=pixel.y;
            if(minY[pixel.x-minX]>pixel.y)
                minY[pixel.x-minX]=pixel.y;
        }
        for (int x = minX; x < maxX; x++)
            for (int y = minY[x-minX]; y < maxY[x-minX]; y++)
                plotPixel(new Vector2i(x, y), color);



    }

    public void plotSquare(Vector2i p1, Vector2i p2, Vector3f color) {
        plotLine(p1,new Vector2i(p2.x,p1.y),color);
        plotLine(p1,new Vector2i(p2.x,p2.y),color);
        plotLine(p2,new Vector2i(p1.x,p2.y),color);
        plotLine(p2,new Vector2i(p2.x,p1.y),color);
    }
    public void fillSquare(Vector2i p1, Vector2i p2, Vector3f color) {
        Vector2i size = new Vector2i(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
        for (int x = 0; x < size.x; x++)
            for (int y = 0; y < size.y; y++)
                plotPixel(new Vector2i(p1.x+x,p2.x+x), color);
    }

    public void plotFunction(Function<Double,Double> f, Vector2d zoomFactor, Vector3f color){
        for (int x = 0; x < dimensions.x; x++) {
            double y = f.apply((x-dimensions.x/2)*zoomFactor.x);
            if(!Double.isNaN(y))
                plotPixel(new Vector2i(x-dimensions.x/2, (int) (-y*zoomFactor.y)),color);
        }
    }


    public void draw(){
        glRasterPos2i(-1,-1);
        ByteBuffer buffer = BufferUtils.createByteBuffer(framebuffer.length);
        buffer.put(framebuffer).flip();
        GL30.glDrawPixels(dimensions.x,dimensions.y, GL11.GL_RGB,GL11.GL_UNSIGNED_BYTE, buffer);
    }


    private List<Vector2i> getLineData(Vector2i p1, Vector2i p2){
        List<Vector2i> PixelList = new ArrayList<>();
        int dx = p1.x-p2.x;
        int dy = p1.y-p2.y;

        if(dx==0&&dy==0) {
            PixelList.add(p1);
            return PixelList;
        }
        // x = f(y)
        if(dx==0||Math.abs(dy)>Math.abs(dx)){
            int ysign = -dy/Math.abs(dy);

            for (int y = 0; y < Math.abs(dy); y++)
                PixelList.add(new Vector2i((int)((float)dx/dy*y*ysign), ysign * y).add(p1));
            return PixelList;
        }
        // y=f(x)
        int xsign = -dx/Math.abs(dx);
        for (int x = 0; x < Math.abs(dx); x++)
            PixelList.add(new Vector2i(x*xsign,xsign*x*dy/dx).add(p1));
        return PixelList;
    }
}
