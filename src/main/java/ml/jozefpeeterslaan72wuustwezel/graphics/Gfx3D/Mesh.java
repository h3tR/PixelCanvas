package ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.ObjReader;
import ml.jozefpeeterslaan72wuustwezel.graphics.FramePlotter2D;

import org.joml.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D.Util3D.*;

public class Mesh {
    int[][] polygons;
    Vector3fc[] vertices;

    public Mesh(int[][] polygons, Vector3fc[] vertices) {
        this.polygons = polygons;
        this.vertices = vertices;
    }

    public void render(Camera3D cam, FramePlotter2D plotter2D, Vector3fc color, Transform3D transform) {
        Vector2ic dimensions = plotter2D.getDimensions();
        Vector3f lookVector = cam.getLookVector();
        Vector3f cameraPosition = cam.getPosition();
        int[][] sortedPolygons = polygons.clone();
        Arrays.sort(sortedPolygons, (p1,p2)->{
           float d1 = cameraPosition.distance(cropVector4fc(getPolygonData(p1,vertices,transform)[3]));
           float d2 = cameraPosition.distance(cropVector4fc(getPolygonData(p2,vertices,transform)[3]));
           return Float.compare(d1,d2);
        });
        for(int[] polygon : sortedPolygons){
            Vector4fc[] polygonData = getPolygonData(polygon,vertices,transform);

            //backface culling
            if(cropVector4fc(polygonData[4]).dot(lookVector)>0)
                continue;

            //TODO: clipping (WIP)
            Vector4fc[] clippedPolygon = polygonData;
            /*clipPolygon(new Vector4fc[]{polygonData[0],polygonData[1],polygonData[2]});
            if(clippedPolygon==null)
                continue;*/


            plotter2D.fillTriangle(
                    projectPoint(cam,dimensions, cropVector4fc(clippedPolygon[0])),
                    projectPoint(cam,dimensions, cropVector4fc(clippedPolygon[1])),
                    projectPoint(cam,dimensions, cropVector4fc(clippedPolygon[2])),
                    color
            );
        }
    }

    public void renderWireFrame(Camera3D cam, FramePlotter2D plotter2D, Vector3fc color,Transform3D transform){
        Vector2ic dimensions = plotter2D.getDimensions();
        for(int[] polygon : polygons){
            Vector4fc[] polygonData = getPolygonData(polygon,vertices,transform);
            plotter2D.plotTriangle(
                    projectPoint(cam,dimensions, cropVector4fc(polygonData[0])),
                    projectPoint(cam,dimensions, cropVector4fc(polygonData[1])),
                    projectPoint(cam,dimensions, cropVector4fc(polygonData[2])),
                    color
            );
        }
    }

    private Vector2i projectPoint(Camera3D cam, Vector2ic dimensions, Vector3fc point){
        Vector2f projectedNDC = cam.project3DPoint(new Vector4f(point,1));
        return new Vector2i((int) (projectedNDC.x*dimensions.x()), (int) (projectedNDC.y*dimensions.y()));
    }

    public static Mesh CUBE = new Mesh(
            new int[][]{
                    {1,5,7},{1,7,3},//back
                    {0,1,3},{0,3,2},//left
                    {4,0,2},{4,2,6},//front
                    {5,4,6},{5,6,7},//right
                    {3,7,6},{3,6,2},//top
                    {0,4,5},{0,5,1}//bottom
            }, new Vector3f[]{
                new Vector3f(-1,-1,-1),new Vector3f(-1,-1,1),new Vector3f(-1,1,-1),new Vector3f(-1,1,1),
                new Vector3f(1,-1,-1),new Vector3f(1,-1,1), new Vector3f(1,1,-1),new Vector3f(1,1,1)
            }
    );




    public static Mesh fromObj(String filePath){
        Obj obj;
        ArrayList<int[]> polygons = new ArrayList<>();
        try {
            InputStream objStream = Mesh.class.getClassLoader().getResourceAsStream(filePath);
            if(objStream == null){
                throw new FileNotFoundException(filePath);
            }
            obj = ObjReader.read(objStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < obj.getNumFaces(); i++) {
            ObjFace face = obj.getFace(i);
            if(face.getNumVertices()==4){
                polygons.add(new int[]{face.getVertexIndex(0),face.getVertexIndex(1),face.getVertexIndex(2)});
                polygons.add(new int[]{face.getVertexIndex(0),face.getVertexIndex(2),face.getVertexIndex(3)});
                continue;
            }
            polygons.add(new int[]{face.getVertexIndex(0),face.getVertexIndex(1),face.getVertexIndex(2)});
        }
        ArrayList<Vector3fc> vertices = new ArrayList<>();
        for (int i = 0; i < obj.getNumVertices(); i++)
            vertices.add(new Vector3f(obj.getVertex(i).getX(),obj.getVertex(i).getY(),obj.getVertex(i).getZ()));
        int[][] polygonArray = polygons.toArray(new int[polygons.size()][]);
        Vector3fc[] vertexArray = vertices.toArray(new Vector3fc[0]);
        return new Mesh(polygonArray,vertexArray);
    }
}
