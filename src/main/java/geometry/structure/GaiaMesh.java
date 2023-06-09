package geometry.structure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GaiaMesh {
    private ArrayList<GaiaPrimitive> primitives = new ArrayList<>();

    // getTotalIndices
    public ArrayList<Short> getIndices() {
        ArrayList<Short> totalIndices = new ArrayList<>();
        for (GaiaPrimitive primitive : primitives) {
            for (Integer indices : primitive.getIndices()) {
                totalIndices.add(indices.shortValue());
            }
        }
        return totalIndices;
    }

    // getTotalVerticesCount
    public int getPositionsCount() {
        int count = 0;
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector3d position = vertex.getPosition();
                if (position != null) {
                    count+=3;
                }
            }
        }
        return count;
    }

    // getTotalVertices
    public ArrayList<Float> getPositions() {
        ArrayList<Float> totalVertices = new ArrayList<>();
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector3d position = vertex.getPosition();
                if (position != null) {
                    totalVertices.add((float) vertex.getPosition().x());
                    totalVertices.add((float) vertex.getPosition().y());
                    totalVertices.add((float) vertex.getPosition().z());
                }
            }
        }
        return totalVertices;
    }

    // getTotalNormalsCount
    public int getNormalsCount() {
        int count = 0;
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector3d normal = vertex.getNormal();
                if (normal != null) {
                    count+=3;
                }
            }
        }
        return count;
    }

    // getTotalNormals
    public ArrayList<Float> getNormals() {
        ArrayList<Float> totalNormals = new ArrayList<>();
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector3d normal = vertex.getNormal();
                if (normal != null) {
                    totalNormals.add((float) vertex.getNormal().x());
                    totalNormals.add((float) vertex.getNormal().y());
                    totalNormals.add((float) vertex.getNormal().z());
                }
            }
        }
        return totalNormals;
    }

    // getTotalTexCoordsCount
    public int getTextureCoordinatesCount() {
        int count = 0;
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector2d textureCoordinates = vertex.getTextureCoordinates();
                if (textureCoordinates != null) {
                    count+=2;
                }
            }
        }
        return count;
    }

    // getTotalTexCoords
    public ArrayList<Float> getTextureCoordinates() {
        ArrayList<Float> totalTexCoords = new ArrayList<>();
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector2d textureCoordinates = vertex.getTextureCoordinates();
                if (textureCoordinates != null) {
                    totalTexCoords.add((float) vertex.getTextureCoordinates().x());
                    totalTexCoords.add((float) vertex.getTextureCoordinates().y());
                }
            }
        }
        return totalTexCoords;
    }

    public int getColorsCount() {
        int count = 0;
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector4d color = vertex.getColor();
                if (color != null) {
                    count+=4;
                }
            }
        }
        return count;
    }

    public ArrayList<Float> getColors() {
        ArrayList<Float> totalColors = new ArrayList<>();
        for (GaiaPrimitive primitive : primitives) {
            for (GaiaVertex vertex : primitive.getVertices()) {
                Vector4d color = vertex.getColor();
                if (color != null) {
                    totalColors.add((float) vertex.getColor().x());
                    totalColors.add((float) vertex.getColor().y());
                    totalColors.add((float) vertex.getColor().z());
                    totalColors.add((float) vertex.getColor().w());
                }
            }
        }
        return totalColors;
    }

    public int getIndicesCount() {
        int totalIndices = 0;
        for (GaiaPrimitive primitive : primitives) {
            totalIndices += primitive.getIndices().size();
        }
        return totalIndices;
    }

    public int getVerticesCount() {
        int vertexCount = 0;
        for (GaiaPrimitive primitive : primitives) {
            vertexCount += primitive.getVertices().size();
        }
        return vertexCount;
    }
}
