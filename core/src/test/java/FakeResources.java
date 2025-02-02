import bbbwd.bubbleworld.core.Resources;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FakeResources extends Resources {

    @Override
    public void load() {
    }

    @Override
    public AssetManager getAssetManager() {
        return null;
    }

    @Override
    public TextureRegion getTexureRegion(String name) {
        return null;
    }
}
