package ml.jozefpeeterslaan72wuustwezel.graphics;



import org.joml.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Texture {

    Vector3fc[] image;
    public final Vector2ic dimensions;
    public Texture(Vector3fc[] image, Vector2i dimensions) {
        this.image = image;
        this.dimensions = dimensions;
    }


    /**
     * 0,0 => top left
     */
    public Vector3f sample(Vector2ic pixel) {
        if(isOutOfBounds(pixel))
            throw new IllegalArgumentException(String.format("Pixel %s out of bounds for image with dimensions: %s",pixel.toString(),dimensions.toString()));
        return new Vector3f(image[pixel.y()*dimensions.x()+pixel.x()]);
    }
    public Vector3f[] sample(Vector2ic origin, Vector2ic size){
        if(isOutOfBounds(origin))
            throw new IllegalArgumentException(String.format("Pixel %s out of bounds for image with dimensions: %s",origin.toString(),dimensions.toString()));
        Vector2ic dest = new Vector2i(origin).add(size).sub(1,1);
        if(isOutOfBounds(dest))
            throw new IllegalArgumentException(String.format("Pixel %s out of bounds for image with dimensions: %s",dest.toString(),dimensions.toString()));

        Vector3f[] result = new Vector3f[size.x()*size.y()];

        for (int i = 0; i < result.length; i++) {
            int x = i%size.x();
            int y = (i-x)/size.x();
            int yOffset = (y+origin.y())*dimensions.x();
            int xOffset = x+origin.x();
            result[(size.y()-1-y)*size.x()+x] = new Vector3f(image[yOffset+xOffset]);
        }
        return result;
    }


    private boolean isOutOfBounds(Vector2ic ... positions) {
        boolean outcome = true;
        for(Vector2ic position : positions)
            outcome&=!(position.x() < 0 || position.x() >= dimensions.x() || position.y() < 0 || position.y() >= dimensions.y());
        return !outcome;
    }


    public static Texture fromFile(String filePath) {
        BufferedImage bufImg;
        try {
            InputStream in = Texture.class.getClassLoader().getResourceAsStream(filePath);
            if(in==null)
                throw new FileNotFoundException("File not found");
            bufImg =  ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = bufImg.getWidth();
        int height = bufImg.getHeight();
        int[] intImage = bufImg.getRGB(0,0,width,height,null,0,width);
        Vector3fc[] image = new Vector3fc[intImage.length];
        for (int i = 0; i < image.length; i++)
           image[i] = Color.fromRawRGBA(intImage[i]);

        return new Texture(image,new Vector2i(width,height));
    }
}
