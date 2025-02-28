package bbbwd.bubbleworld.content.liquid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Liquids {
    public  static Liquid testLiquid;
  public static void load(){
        testLiquid = new Liquid() {
            @Override
            public void config() {
                TextureRegion t= new TextureRegion(new Texture(Gdx.files.internal("adjusted_drop.png")));
                renderLogic= new LiquidRenderLogic(t,size) ;
            }
        };
    }
}
