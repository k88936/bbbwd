package bbbwd.bubbleworld.core.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class LiquidSpriteShader {
      public static ShaderProgram createLiquidSpriteShader() {
        String vertexShader =
                "#version 330 core\n" +
                        "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                        "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                        "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                        "uniform mat4 u_projTrans;\n" +
                        "varying vec2 v_texCoords;\n" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "   v_texCoords = vec2(" + ShaderProgram.TEXCOORD_ATTRIBUTE + "0.x,1-"+ShaderProgram.TEXCOORD_ATTRIBUTE+"0.y);\n" +
                        "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                        "}\n";
        String fragmentShader =
                "#version 330 core\n" +
                        "varying vec2 v_texCoords;\n" +
                        "uniform sampler2D u_texture;\n" +
                        "void main() {\n" +
                        "    vec4 rgba = texture(u_texture, v_texCoords);\n" +
                        "    rgba.a *= 1.5;"+
                        "   rgba*=4;"+
                        "    gl_FragColor = vec4(rgba);"+
                        "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            Gdx.app.log("ERROR", shader.getLog());
        }
        return shader;
    }
}
