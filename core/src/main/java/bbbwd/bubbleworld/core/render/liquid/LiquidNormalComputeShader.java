package bbbwd.bubbleworld.core.render.liquid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class LiquidNormalComputeShader {
    public static ShaderProgram createLiquidNormalComputeShader() {
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
                        "uniform vec2 u_resolution;\n" +
                        "uniform vec2 u_world;\n" +
                        "void main() {\n" +
//                        "    gl_FragColor = texture(u_texture, v_texCoords);\n" +
                        "    float height = texture(u_texture, v_texCoords).a;\n" +
                        "    if(height<=0)discard;\n" +
                        "    float dHeightX = dFdx(height);\n" +
                        "    float dHeightY = dFdy(height);\n" +
//                        "    vec2 v=-vec2(dHeightX,dHeightY);\n" +
                        "    vec2 v=-1324*vec2(dHeightX,dHeightY)*u_world/u_resolution;\n" +
                        "    vec3 normal = normalize(vec3(v.x,v.y, 1));\n" +
                        "    gl_FragColor = vec4(normal*0.5+0.5, 0.9);\n" +
//                        "    gl_FragColor = vec4(height,height,height, 1.0);"+
                        "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            Gdx.app.log("ERROR", shader.getLog());
        }
        return shader;
    }
}
