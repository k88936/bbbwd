package bbbwd.bubbleworld.content.liquid;

import bbbwd.bubbleworld.Vars;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Liquids {
    public  static Liquid testLiquid;
  public static void load(){
        testLiquid = new Liquid() {
            @Override
            public void config() {
                TextureRegion t = Vars.resources.getTexureRegion("adjusted_drop.png");
                renderLogic= new LiquidRenderLogic(t,size) ;
            }
        };
    }
}
