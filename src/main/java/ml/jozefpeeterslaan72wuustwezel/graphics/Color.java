package ml.jozefpeeterslaan72wuustwezel.graphics;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Color {
    public static Vector3fc BLACK = new Vector3f(0);
    public static Vector3fc WHITE = new Vector3f(1);
    public static Vector3fc RED = new Vector3f(1, 0, 0);
    public static Vector3fc GREEN = new Vector3f(0, 1, 0);
    public static Vector3fc BLUE = new Vector3f(0, 0, 1);
    public static Vector3fc YELLOW = new Vector3f(1, 1, 0);
    public static Vector3fc PINK = new Vector3f(1, 0, 1);
    public static Vector3fc CYAN = new Vector3f(0, 1, 1);


    public static Vector3f invert(Vector3fc color){
       Vector3f newColor = new Vector3f(color);
       return newColor.sub(new Vector3f(1)).absolute();
    }
    
    public static Vector3f fromRawRGB(byte R, byte G, byte B){
        return new Vector3f((float) R /255, (float) G /255, (float) B /255);
    }
    public static Vector3f fromRawRGBA(int RGBA){
        byte R = (byte) (RGBA >> 16 & 0xFF);
        byte G = (byte) (RGBA >> 8 & 0xFF);
        byte B = (byte) (RGBA  & 0xFF);
        return new Vector3f((float) R /255, (float) G /255, (float) B /255);
    }

    public static byte[] toRawRGB(Vector3fc color){
        byte[] RGB = new byte[3];
        RGB[0] = (byte) (color.x()*255);
        RGB[1] = (byte) (color.y()*255);
        RGB[2] = (byte) (color.z()*255);
        return RGB;
    }

    public static int toRawRGBA(Vector3fc color){
        byte[] RGB = toRawRGB(color);
        return (0xFF<<24)+(RGB[0]<<16)+(RGB[1]<<8)+RGB[2];
    }
    
}
