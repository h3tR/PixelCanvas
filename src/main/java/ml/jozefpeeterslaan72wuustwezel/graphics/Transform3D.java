package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.Vector3f;

public class Transform3D {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;
    public Transform3D(Vector3f pos,Vector3f rot,Vector3f scale) {
        position = pos;
        rotation = rot;
        this.scale = scale;
    }
}
