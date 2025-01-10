package ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera3D {
    Vector3f position;
    Vector3f rotation;
    float fov;
    float aspect;

    Matrix4f projectionMatrix;
    Matrix4f viewMatrix;
    Vector3f lookVector;

    public Camera3D(Vector3f camPos, Vector3f camRot, float fov, float aspectRatio) {
        this.position = new Vector3f(camPos);
        this.rotation = new Vector3f(camRot);
        this.fov = fov;
        this.aspect = aspectRatio;

        genLookVector();
        genViewMatrix();
        genProjectionMatrix();
    }

    public Vector2f project3DPoint(Vector4f point){
        Vector4f affectedPoint = new Vector4f(point).mul(viewMatrix).mul(projectionMatrix);
        affectedPoint.div(affectedPoint.w);
        return new Vector2f(affectedPoint.x, -affectedPoint.y);
    }

    private void genViewMatrix() {
        viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        viewMatrix.translate(-position.x, -position.y, -position.z);
    }

    private void genProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(fov,aspect,0.1f, 100.0f);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = new Vector3f(rotation);
        genViewMatrix();
        genLookVector();
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
        genViewMatrix();
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
        genProjectionMatrix();
    }

    public void setFov(float fov) {
        this.fov = fov;
        genProjectionMatrix();
    }

    public float getAspect() {
        return aspect;
    }

    public float getFov() {
        return fov;
    }

    private void genLookVector(){
        float x = (float) -(Math.cos(Math.toRadians(rotation.x)) * Math.sin(Math.toRadians(rotation.y)));
        float y = (float) (Math.sin(Math.toRadians(rotation.x)));
        float z = (float) (Math.cos(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.y)));
        lookVector = new Vector3f(x, y, z).normalize();
    }

    public Vector3f getLookVector() {
        return lookVector;
    }
}
