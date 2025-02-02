package bbbwd.bubbleworld.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public  class Resources {
    private AssetManager assetManager;
    private TextureAtlas atlas;

    public void load() {
        this.assetManager = new AssetManager();
        assetManager.load("packed/assets.atlas", TextureAtlas.class);
        assetManager.finishLoading();
        this.atlas = assetManager.get("packed/assets.atlas", TextureAtlas.class);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureRegion getTexureRegion(String name) {
        return atlas.findRegion(name);
    }

}
