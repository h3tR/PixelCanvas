package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.lang.Math;
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


    public void plotPixel(Vector2ic position, Vector3fc color) {
        Vector2i realPos = getRealcoords(position);
        if (realPos.x < 0 || realPos.x >= dimensions.x || realPos.y < 0 || realPos.y >= dimensions.y) {
            return;
        }

        int index = 3* (realPos.y*dimensions.x+realPos.x);
        byte[] RGB = Color.toRawRGB(color);

        framebuffer[index++] = RGB[0];
        framebuffer[index++] = RGB[1];
        framebuffer[index] = RGB[2];
    }

    public void plotLine(Vector2ic p1,Vector2ic p2, Vector3fc color) {
        List<Vector2ic> Pixels = getLineData(p1,p2);
        for (Vector2ic pixel : Pixels) plotPixel(pixel, color);
    }

    public void plotTriangle(Vector2i p1, Vector2i p2, Vector2i p3, Vector3fc color) {
        plotLine(p1,p2,color);
        plotLine(p3,p1,color);
        plotLine(p3,p2,color);
    }
    public void fillTriangle(Vector2i p1, Vector2i p2, Vector2i p3, Vector3fc color){
        if(!inBoundaries(p1,p2,p3))
            return;
        Set<Vector2ic> Edges = new HashSet<>(getLineData(p1,p2));
        Edges.addAll(getLineData(p2,p3));
        Edges.addAll(getLineData(p3,p1));
        //remove duplicate pixels
        List<Vector2ic> filteredEdges = new ArrayList<>(Edges.stream().toList());
        filteredEdges.sort(Comparator.comparingInt(Vector2ic::x));

        int minX = filteredEdges.get(0).x();
        int maxX = filteredEdges.get(filteredEdges.size()-1).x();
        int dx = maxX - minX+1;
        int[] maxY = new int[dx];
        Arrays.fill(maxY, -dimensions.y/2);
        int[] minY = new int[dx];
        Arrays.fill(minY, dimensions.y/2);

        for (Vector2ic pixel : filteredEdges) {
            if (maxY[pixel.x() - minX] < pixel.y())
                maxY[pixel.x() - minX] = pixel.y();
            if (minY[pixel.x() - minX] > pixel.y())
                minY[pixel.x() - minX] = pixel.y();
        }
        for (int x = minX; x < maxX; x++)
            for (int y = minY[x-minX]; y < maxY[x-minX]; y++)
                plotPixel(new Vector2i(x, y), color);



    }

    public void plotSquare(Vector2ic p1, Vector2ic p2, Vector3fc color) {
        plotLine(p1,new Vector2i(p2.x(),p1.y()),color);
        plotLine(p1,new Vector2i(p2.x(),p2.y()),color);
        plotLine(p2,new Vector2i(p1.x(),p2.y()),color);
        plotLine(p2,new Vector2i(p2.x(),p1.y()),color);
    }
    public void fillSquare(Vector2ic p1, Vector2ic p2, Vector3fc color) {
        Vector2i size = new Vector2i(Math.abs(p1.x()-p2.x()),Math.abs(p1.y()-p2.y()));
        for (int x = 0; x < size.x; x++)
            for (int y = 0; y < size.y; y++)
                plotPixel(new Vector2i(p1.x()+x,p2.x()+x), color);
    }

    public void plotCircle(Vector2ic center, int radius, Vector3fc color) {
        for (int i = -radius; i < radius; i++) {
            plotPixel(new Vector2i(i,(int)(Math.sqrt(1-Math.pow(i/(float)radius,2))*radius)).add(center),color);
            plotPixel(new Vector2i(i,(int)(-Math.sqrt(1-Math.pow(i/(float)radius,2))*radius)).add(center),color);
            plotPixel(new Vector2i((int)(Math.sqrt(1-Math.pow(i/(float)radius,2))*radius),i).add(center),color);
            plotPixel(new Vector2i((int)(-Math.sqrt(1-Math.pow(i/(float)radius,2))*radius),i).add(center),color);
        }
    }

    public void fillCircle(Vector2ic center, int radius, Vector3fc color) {
        for (int i = -radius; i < radius; i++) {
            Vector2i v1 = new Vector2i(i,(int)(Math.sqrt(1-Math.pow(i/(float)radius,2))*radius)).add(center);
            Vector2i v2 = new Vector2i(i,-(int)(Math.sqrt(1-Math.pow(i/(float)radius,2))*radius)).add(center);
            for (int y = v2.y; y < v1.y; y++)
                plotPixel(new Vector2i(center.x()+i,y),color);
        }
    }

    public void plotFunction(Function<Double,Double> f, Vector2dc zoomFactor, Vector3fc color){
        for (int x = 0; x < dimensions.x; x++) {
            double y = f.apply((x-dimensions.x/2)*zoomFactor.x());
            if(Double.isNaN(y))
                continue;
            plotPixel(new Vector2i(x-dimensions.x/2, (int) (-y*zoomFactor.y())),color);
            if(x==0)
                continue;
            double prevY = f.apply((x-1-dimensions.x/2)*zoomFactor.x());
            plotLine(new Vector2i(x-dimensions.x/2, (int) (-y*zoomFactor.y())),new Vector2i(x-dimensions.x/2, (int) (-prevY*zoomFactor.y())),color);
        }
    }

    public void plotPixels(Vector2ic origin, Vector2ic size, Vector3f[] pixels){
        if(!(inBoundaries(origin)||inBoundaries(new Vector2i(origin).add(size))))
            return;
        Vector2i realOrigin = getRealcoords(origin);

        for (int x = 0; x < size.x(); x++)
            for (int y = 0; y < size.y(); y++) {
                byte[] RGB = Color.toRawRGB(pixels[y*size.x()+x]);
                int index = 3* ((realOrigin.y+y)*dimensions.x+realOrigin.x+x);
                framebuffer[index++] = RGB[0];
                framebuffer[index++] = RGB[1];
                framebuffer[index] = RGB[2];
            }
    }

    public void draw(){
        glRasterPos2i(-1,-1);
        ByteBuffer buffer = BufferUtils.createByteBuffer(framebuffer.length);
        buffer.put(framebuffer).flip();
        GL30.glDrawPixels(dimensions.x,dimensions.y, GL11.GL_RGB,GL11.GL_UNSIGNED_BYTE, buffer);
    }


    private List<Vector2ic> getLineData(Vector2ic p1, Vector2ic p2){
        if(!(inBoundaries(p1,p2)))
            return Collections.emptyList();
        List<Vector2ic> PixelList = new ArrayList<>();
        int dx = p1.x()-p2.x();
        int dy = p1.y()-p2.y();

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

    private boolean inBoundaries(Vector2ic ... positions){
        boolean outcome = true;
        for(Vector2ic position : positions) {
            Vector2i realPos = new Vector2i(position.x()+dimensions.x/2, position.y()+dimensions.y/2);
            outcome&=!(realPos.x < 0 || realPos.x >= dimensions.x || realPos.y < 0 || realPos.y >= dimensions.y);
        }
        return outcome;
    }

    private Vector2i getRealcoords(Vector2ic position){
        return new Vector2i(position.x()+dimensions.x/2, position.y()+dimensions.y/2);
    }

    public Vector2ic getDimensions(){
        return new Vector2i(dimensions);
    }
}
