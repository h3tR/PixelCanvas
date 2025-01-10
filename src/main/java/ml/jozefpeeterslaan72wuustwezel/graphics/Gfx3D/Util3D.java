package ml.jozefpeeterslaan72wuustwezel.graphics.Gfx3D;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.ArrayList;
import java.util.List;

public class Util3D {
    private static boolean isInside(Vector4fc point, int planeIndex) {
        float coord = switch (planeIndex) {
            case 0 -> point.x(); // Left
            case 1 -> -point.x(); // Right
            case 2 -> point.y(); // Bottom
            case 3 -> -point.y(); // Top
            case 4 -> point.z(); // Near
            case 5 -> -point.z(); // Far
            default -> throw new IllegalArgumentException("Invalid plane index");
        };
        return coord <= point.w();
    }

    // Compute the intersection of an edge with a clip plane
    private static Vector4fc intersect(Vector4fc p1, Vector4fc p2, int planeIndex) {
        float t = switch (planeIndex) {
            case 0 -> (p1.w() + p1.x()) / ((p1.w() + p1.x()) - (p2.w() + p2.x()));
            case 1 -> (p1.w() - p1.x()) / ((p1.w() - p1.x()) - (p2.w() - p2.x()));
            case 2 -> (p1.w() + p1.y()) / ((p1.w() + p1.y()) - (p2.w() + p2.y()));
            case 3 -> (p1.w() - p1.y()) / ((p1.w() - p1.y()) - (p2.w() - p2.y()));
            case 4 -> (p1.w() + p1.z()) / ((p1.w() + p1.z()) - (p2.w() + p2.z()));
            case 5 -> (p1.w() - p1.z()) / ((p1.w() - p1.z()) - (p2.w() - p2.z()));
            default -> throw new IllegalArgumentException("Invalid plane index");
        };
        return new Vector4f(
                p1.x() + t * (p2.x() - p1.x()),
                p1.y() + t * (p2.y() - p1.y()),
                p1.z() + t * (p2.z() - p1.z()),
                p1.w() + t * (p2.w() - p1.w())
        );
    }

    private static Vector4fc[] clipPolygonAgainstPlane(Vector4fc[] polygon, int planeIndex) {
        List<Vector4fc> clippedVertices = new ArrayList<>();
        for (int i = 0; i < polygon.length; i++) {
            Vector4fc current = polygon[i];
            Vector4fc previous = polygon[(i + polygon.length - 1) % polygon.length];
            boolean currentInside = isInside(current, planeIndex);
            boolean previousInside = isInside(previous, planeIndex);

            if (currentInside) {
                if (!previousInside)
                    clippedVertices.add(intersect(previous, current, planeIndex));
                clippedVertices.add(current);
            } else if (previousInside)
                clippedVertices.add(intersect(previous, current, planeIndex));
        }
        return clippedVertices.toArray(new Vector4fc[0]);
    }

    /**
     * clips polygon against all frustrum planes
     @return clipped polygons or null when fully clipped
     */
    public static  Vector4fc[] clipPolygon(Vector4fc[] polygon) {
        for (int i = 0; i < 6; i++) {
            polygon = clipPolygonAgainstPlane(polygon, i);
            if(polygon.length<3)
                return null;
        }
        return polygon;
    }

    public static Vector3f cropVector4fc(Vector4fc vector4fc){
        return new Vector3f(vector4fc.x(), vector4fc.y(), vector4fc.z());
    }

    public static Vector4fc[] getPolygonData(int[] polygon, Vector3fc[] vertices, Transform3D transform) {
        Vector4fc[] polygonData = new Vector4fc[5];
        for (int i = 0; i < 3; i++)
            polygonData[i] = new Vector4f(vertices[polygon[i]],1).mul(transform.getModelMatrix());
        polygonData[3] = new Vector4f(polygonData[0]).add(polygonData[1]).add(polygonData[2]).div(3);
        Vector3f normal = cropVector4fc(polygonData[0]).sub(cropVector4fc(polygonData[3])).cross(cropVector4fc(polygonData[1]).sub(cropVector4fc(polygonData[3])));
        polygonData[4] = new Vector4f(normal.normalize(),1);
        return polygonData;
    }
}
