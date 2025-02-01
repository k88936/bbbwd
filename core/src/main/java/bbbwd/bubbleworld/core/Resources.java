package bbbwd.bubbleworld.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Resources {
    public  AssetManager assetManager;
    public  TextureAtlas atlas;

    public void load() {
        assetManager = new AssetManager();
        assetManager.load("packed/assets.atlas", TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get("packed/assets.atlas", TextureAtlas.class);
    }
}
