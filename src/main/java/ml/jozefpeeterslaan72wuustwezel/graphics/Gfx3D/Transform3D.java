package ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform3D {
    private Matrix4f modelMatrix;
    private Vector3fc position;
    private Vector3fc rotation;
    private Vector3fc scale;
    public Transform3D(Vector3fc pos,Vector3fc rot,Vector3fc scale) {
        position = new Vector3f(pos);
        rotation = new Vector3f(rot);
        this.scale = new Vector3f(scale);
        genMatrix();
    }

    public void setPosition(Vector3fc position) {
        this.position = position;
        genMatrix();
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation = rotation;
        genMatrix();
    }

    public void setScale(Vector3fc scale) {
        this.scale = scale;
        genMatrix();
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f(modelMatrix);
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }

    public Vector3f getScale() {
        return new Vector3f(scale);
    }

    private void genMatrix(){
        modelMatrix = new Matrix4f().identity();
        modelMatrix.rotateXYZ(rotation);
        modelMatrix.translate(position);
        modelMatrix.scale(scale);
    }
}
