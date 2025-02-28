package bbbwd.bubbleworld.core.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

import java.nio.Buffer;

/** Draws batched quads using indices.
 * @see Batch
 * @author mzechner
 * @author Nathan Sweet */
public class ScreenDrawer {

    private final Mesh mesh;

    final float[] vertices;
    private Texture lastTexture = null;
    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();

    private boolean blendingDisabled = false;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
    private int blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
    private int blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;

    private final ShaderProgram shader;
    private ShaderProgram customShader = null;
    private final boolean ownsShader;

    private final Color color = new Color(1, 1, 1, 1);
    float colorPacked = Color.WHITE_FLOAT_BITS;

    /** The maximum number of sprites rendered in one batch so far. **/

    private final VertexDataType vertexDataType;

    public ScreenDrawer() {
        // 32767 is max vertex index, so 32767 / 4 vertices per sprite = 8191 sprites max.

        vertexDataType = (Gdx.gl30 != null) ? VertexDataType.VertexBufferObjectWithVAO : VertexDataType.VertexBufferObject;

        mesh = new Mesh(vertexDataType, false, 4, 6,
                new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        vertices = new float[20];

        short[] indices = new short[6];
            indices[0] = 0;
            indices[1] = 1;
            indices[2] = 2;
            indices[3] = 2;
            indices[4] = 3;
            indices[5] = 0;
        mesh.setIndices(indices);

        shader = createDefaultShader();
        ownsShader = true;

        // Pre bind the mesh to force the upload of indices data.
        mesh.getIndexData().bind();
        mesh.getIndexData().unbind();
    }

    /** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified. */
    static public ShaderProgram createDefaultShader() {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    public void begin() {
        Gdx.gl.glDepthMask(false);
        if (customShader != null)
            customShader.bind();
        else
            shader.bind();
        setupMatrices();

    }

    public void end() {
        flush();
        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        if (isBlendingEnabled()) gl.glDisable(GL20.GL_BLEND);
    }

    public void setColor(Color tint) {
        color.set(tint);
        colorPacked = tint.toFloatBits();
    }

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        colorPacked = color.toFloatBits();
    }

    public Color getColor() {
        return color;
    }

    public void setPackedColor(float packedColor) {
        Color.abgr8888ToColor(color, packedColor);
        this.colorPacked = packedColor;
    }

    public float getPackedColor() {
        return colorPacked;
    }

    public void flush() {
        lastTexture.bind();
        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, 20);

        // Only upload indices for the vertex array type
        if (vertexDataType == VertexDataType.VertexArray) {
            Buffer indicesBuffer = mesh.getIndicesBuffer(true);
            indicesBuffer.position(0);
            indicesBuffer.limit(6);
        }

        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1)
                Gdx.gl.glBlendFuncSeparate(blendSrcFunc, blendDstFunc, blendSrcFuncAlpha, blendDstFuncAlpha);
        }

        mesh.render(customShader != null ? customShader : shader, GL20.GL_TRIANGLES, 0, 6);

    }

    private void draw(Texture texture) {
        float[] vertices = this.vertices;
        lastTexture = texture;

        float fx2 =  Gdx.graphics.getWidth();
        float fy2 =  Gdx.graphics.getHeight();
        float color1 = this.colorPacked;
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = color1;
        vertices[3] =  0;
        vertices[4] =  1;

        vertices[5] =  0;
        vertices[6] = fy2;
        vertices[7] = color1;
        vertices[8] =  0;
        vertices[9] =  0;

        vertices[10] = fx2;
        vertices[11] = fy2;
        vertices[12] = color1;
        vertices[13] =  1;
        vertices[14] =  0;

        vertices[15] = fx2;
        vertices[16] =  0;
        vertices[17] = color1;
        vertices[18] =  1;
        vertices[19] =  1;
    }


    public void disableBlending() {
        if (blendingDisabled) return;
        blendingDisabled = true;
    }

    public void enableBlending() {
        if (!blendingDisabled) return;
        blendingDisabled = false;
    }

    public void setBlendFunction(int srcFunc, int dstFunc) {
        setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
    }

    public void setBlendFunctionSeparate(int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
        if (blendSrcFunc == srcFuncColor && blendDstFunc == dstFuncColor && blendSrcFuncAlpha == srcFuncAlpha
                && blendDstFuncAlpha == dstFuncAlpha) return;
        blendSrcFunc = srcFuncColor;
        blendDstFunc = dstFuncColor;
        blendSrcFuncAlpha = srcFuncAlpha;
        blendDstFuncAlpha = dstFuncAlpha;
    }

    public int getBlendSrcFunc() {
        return blendSrcFunc;
    }

    public int getBlendDstFunc() {
        return blendDstFunc;
    }

    public int getBlendSrcFuncAlpha() {
        return blendSrcFuncAlpha;
    }

    public int getBlendDstFuncAlpha() {
        return blendDstFuncAlpha;
    }

    public void dispose() {
        mesh.dispose();
        if (ownsShader && shader != null) shader.dispose();
    }

    protected void setupMatrices() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        if (customShader != null) {
            customShader.setUniformMatrix("u_projTrans", combinedMatrix);
            customShader.setUniformi("u_texture", 0);
        } else {
            shader.setUniformMatrix("u_projTrans", combinedMatrix);
            shader.setUniformi("u_texture", 0);
        }
    }

    public void setShader(ShaderProgram shader) {
        if (shader == customShader) // avoid unnecessary flushing in case we are drawing
            return;
        customShader = shader;
    }

    public ShaderProgram getShader() {
        if (customShader == null) {
            return shader;
        }
        return customShader;
    }

    public boolean isBlendingEnabled() {
        return !blendingDisabled;
    }

    public boolean isDrawing() {
        return false;
    }

    public void drawScreen(Texture texture) {
        begin();
        draw(texture);
        end();
    }
}
