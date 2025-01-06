package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Renderer3D {
    Vector3f camPos;
    Vector3f camRot;
    Vector3f DisplaySurfaceRelativePosition = new Vector3f(0, 0, 1);

    public Renderer3D(Vector3f camPos, Vector3f camRot) {
        this.camPos = camPos;
        this.camRot = camRot;
    }

    public Vector2f project3DPoint(Vector3f point){
        Vector3f camCoordDistance = new Vector3f(
                (float) (Math.cos(camRot.y)*(Math.sin(camRot.z)*point.y+Math.cos(camRot.z)*point.x)-Math.sin(camRot.y)*point.z),
                (float) (Math.sin(camRot.x)*(Math.cos(camRot.y)*point.z+Math.sin(camRot.y)*(Math.sin(camRot.z)*point.y+Math.cos(camRot.x)*point.x))+Math.cos(camRot.x)*(Math.cos(camRot.z)*point.y-Math.sin(camRot.z)* point.x)),
                (float) (Math.cos(camRot.x)*(Math.cos(camRot.y)*point.z+Math.sin(camRot.y)*(Math.sin(camRot.z)*point.y+Math.cos(camRot.x)*point.x))-Math.sin(camRot.x)*(Math.cos(camRot.z)*point.y-Math.sin(camRot.z)* point.x))
        );
        return new Vector2f(
                DisplaySurfaceRelativePosition.z/camCoordDistance.z*camCoordDistance.x+DisplaySurfaceRelativePosition.x,
                DisplaySurfaceRelativePosition.z/camCoordDistance.z*camCoordDistance.y+DisplaySurfaceRelativePosition.y

                );
    }
}
