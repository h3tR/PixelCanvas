package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Mesh {
    int[][] polygons;
    Vector3f[] vertices;

    public Mesh(int[][] polygons, Vector3f[] vertices) {
        this.polygons = polygons;
        this.vertices = vertices;
    }

    public void render(Camera3D cam, FramePlotter2D plotter2D, Vector3f color,Transform3D transform) {
        //TODO sort for cam distance and render in order
        Vector2i dimensions = plotter2D.getDimensions();
        for(int[] polygon : polygons){
            //TODO: rotation :C
            Vector2f p0 = cam.project3DPoint(new Vector3f(vertices[polygon[0]]).mul(transform.scale).add(transform.position));
            Vector2f p1 = cam.project3DPoint(new Vector3f(vertices[polygon[1]]).mul(transform.scale).add(transform.position));
            Vector2f p2 = cam.project3DPoint(new Vector3f(vertices[polygon[2]]).mul(transform.scale).add(transform.position));
            plotter2D.fillTriangle(
                    new Vector2i((int) (p0.x*dimensions.x), (int) (p0.y*dimensions.y)),
                    new Vector2i((int) (p1.x*dimensions.x), (int) (p1.y*dimensions.y)),
                    new Vector2i((int) (p2.x*dimensions.x), (int) (p2.y*dimensions.y)),
                    color
            );
        }
    }

    public void renderWireFrame(Camera3D cam, FramePlotter2D plotter2D, Vector3f color,Transform3D transform){
        //TODO sort for cam distance and render in order
        Vector2i dimensions = plotter2D.getDimensions();
        for(int[] polygon : polygons){
            //TODO: rotation :C
            Vector2f p0 = cam.project3DPoint(new Vector3f(vertices[polygon[0]]).mul(transform.scale).add(transform.position));
            Vector2f p1 = cam.project3DPoint(new Vector3f(vertices[polygon[1]]).mul(transform.scale).add(transform.position));
            Vector2f p2 = cam.project3DPoint(new Vector3f(vertices[polygon[2]]).mul(transform.scale).add(transform.position));
            plotter2D.plotTriangle(
                    new Vector2i((int) (p0.x*dimensions.x), (int) (p0.y*dimensions.y)),
                    new Vector2i((int) (p1.x*dimensions.x), (int) (p1.y*dimensions.y)),
                    new Vector2i((int) (p2.x*dimensions.x), (int) (p2.y*dimensions.y)),
                    color
            );
        }
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
}
