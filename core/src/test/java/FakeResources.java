import bbbwd.bubbleworld.core.Resources;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2Hull;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue;

import java.io.File;

public class FakeResources extends Resources {

    @Override
    public void load() {
        String root="../assets/";
        String workingDir = System.getProperty("user.dir");
        File dir = new File(workingDir);
        JsonValue polygonsVertices = jsonReader.parse(new FileHandle(root+"packed/polygons.json"));
        JsonValue polygons = polygonsVertices.get("polygons");
        for (JsonValue polygon : polygons) {

            polygonVerticesData.put(polygon.name(), Box2dPlus.b2ComputeHull(polygon.asFloatArray()));
        }
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
