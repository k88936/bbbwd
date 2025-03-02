package bbbwd.bubbleworld.core.render.liquid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class LiquidBlurShader {
    public static ShaderProgram createLiquidBlurShader() {
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
                "   v_texCoords = vec2(" + ShaderProgram.TEXCOORD_ATTRIBUTE + "0.x,1-" + ShaderProgram.TEXCOORD_ATTRIBUTE + "0.y);\n" +
                "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                "}\n";
        String fragmentShader =
            "#version 330 core\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform vec2 u_resolution;\n" +
                "uniform int u_range;\n" +
                "uniform float u_kernel[19];\n" +
                "uniform int u_step;\n" +
                "const float threshold = 0.0;\n" +
                "void main() {\n" +
                "   vec3 sum = vec3(0.0);\n" +
                "   vec2 texelSize = 1.0 / u_resolution;\n" +
                "   int k = 0;\n" +
                "   if(u_step%2==0)   " +
                "       for(int y = -u_range; y <= u_range; ++y) {\n" +
                "           vec2 offset = vec2(0, texelSize.y * y);\n" +
                "           vec3 sample = texture(u_texture, v_texCoords+ offset).rgb;\n" +
                "           sum += u_kernel[k++] * sample;\n" +
                "       }\n" +
                "   else\n" +
                "       for(int x = -u_range; x <= u_range; ++x) {\n" +
                "           vec2 offset = vec2(texelSize.x * x,0);\n" +
                "           vec3 sample = texture(u_texture, v_texCoords+ offset).rgb;\n" +
                "           sum += u_kernel[k++] * sample;\n" +
                "       }\n" +
                "   sum-=threshold;" +
                "   sum/=1-threshold;" +
                "   float gray = dot(sum, vec3(0.299, 0.587, 0.114));\n" +
//                        "float gray =( sum.x+sum.y+sum.z)/;\n"+
                "   gl_FragColor = vec4(sum,gray);\n" +
                "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            Gdx.app.log("ERROR", shader.getLog());
        }
        return shader;
    }

}
