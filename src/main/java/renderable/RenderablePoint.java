package renderable;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import renderable.RenderableBuffer;
import renderable.RenderableObject;

import java.util.ArrayList;

public class RenderablePoint extends RenderableObject {
    RenderableBuffer renderableBuffer;
    int[] vbos;
    boolean dirty;

    public RenderablePoint(float x, float y, float z) {
        super();
        this.setPosition(x, y, z);
    }
    @Override
    public void render(int program) {
        RenderableBuffer renderableBuffer = this.getBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            Matrix4f objectRotationMatrix = getTransformMatrix();
            int uObjectRotationMatrix = GL20.glGetUniformLocation(program, "uObjectRotationMatrix");
            float[] objectRotationMatrixBuffer = new float[16];
            objectRotationMatrix.get(objectRotationMatrixBuffer);

            GL20.glUniformMatrix4fv(uObjectRotationMatrix, false, objectRotationMatrixBuffer);

            int aVertexPosition = GL20.glGetAttribLocation(program, "aVertexPosition");
            int aVertexColor = GL20.glGetAttribLocation(program, "aVertexColor");

            renderableBuffer.setIndiceBind(renderableBuffer.getIndicesVbo());
            renderableBuffer.setAttribute(renderableBuffer.getPositionVbo(), aVertexPosition, 3, 0);
            renderableBuffer.setAttribute(renderableBuffer.getColorVbo(), aVertexColor, 4, 0);

            //GL20.glDrawArrays(GL20.GL_POINTS, 0, 2);
            GL20.glDrawElements(GL20.GL_POINTS, renderableBuffer.getIndicesLength(), GL20.GL_UNSIGNED_SHORT, 0);
        }
    }
    @Override
    public RenderableBuffer getBuffer() {
        if (this.renderableBuffer == null) {
            RenderableBuffer renderableBuffer = new RenderableBuffer();

            ArrayList<Short> indicesList = new ArrayList<Short>();
            ArrayList<Float> positionList = new ArrayList<Float>();
            ArrayList<Float> colorList = new ArrayList<Float>();

            positionList.add(0.0f);
            positionList.add(0.0f);
            positionList.add(0.0f);

            colorList.add(0.5f);
            colorList.add(0.5f);
            colorList.add(0.5f);
            colorList.add(0.5f);

            indicesList.add((short) 0);

            int indicesVbo = renderableBuffer.createIndicesBuffer(indicesList);
            int positionVbo = renderableBuffer.createBuffer(positionList);
            int colorVbo = renderableBuffer.createBuffer(colorList);

            renderableBuffer.setPositionVbo(positionVbo);
            renderableBuffer.setColorVbo(colorVbo);
            renderableBuffer.setIndicesVbo(indicesVbo);
            renderableBuffer.setIndicesLength(indicesList.size());
            this.renderableBuffer = renderableBuffer;
        }
        return this.renderableBuffer;
    }
}
