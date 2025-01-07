package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera3D {
    public Vector3f camPos;
    public Vector3f camRot;
    Vector3f DisplaySurfaceRelativePosition = new Vector3f(0, 0, 1);

    public Camera3D(Vector3f camPos, Vector3f camRot) {
        this.camPos = camPos;
        this.camRot = camRot;
    }

    public Vector2f project3DPoint(Vector3f point){
        Vector3f affectedPoint = new Vector3f(point).sub(camPos);
        Vector3f camCoordDistance = new Vector3f(
                (float) (Math.cos(camRot.y)*(Math.sin(camRot.z)*affectedPoint.y+Math.cos(camRot.z)*affectedPoint.x)-Math.sin(camRot.y)*affectedPoint.z),
                (float) (Math.sin(camRot.x)*(Math.cos(camRot.y)*affectedPoint.z+Math.sin(camRot.y)*(Math.sin(camRot.z)*affectedPoint.y+Math.cos(camRot.x)*affectedPoint.x))+Math.cos(camRot.x)*(Math.cos(camRot.z)*affectedPoint.y-Math.sin(camRot.z)* affectedPoint.x)),
                (float) (Math.cos(camRot.x)*(Math.cos(camRot.y)*affectedPoint.z+Math.sin(camRot.y)*(Math.sin(camRot.z)*affectedPoint.y+Math.cos(camRot.x)*affectedPoint.x))-Math.sin(camRot.x)*(Math.cos(camRot.z)*affectedPoint.y-Math.sin(camRot.z)* affectedPoint.x))
        );
        return new Vector2f(
                DisplaySurfaceRelativePosition.z/camCoordDistance.z*camCoordDistance.x+DisplaySurfaceRelativePosition.x,
                DisplaySurfaceRelativePosition.z/camCoordDistance.z*camCoordDistance.y+DisplaySurfaceRelativePosition.y
                );
    }
    public void setFOV(double fov) {
        DisplaySurfaceRelativePosition.z = (float) Math.tan(fov);
    }
}
