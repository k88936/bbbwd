package bbbwd.bubbleworld.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.box2d.Box2dPlus;
import com.badlogic.gdx.box2d.structs.b2Hull;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class Resources {
    protected final JsonReader jsonReader = new JsonReader();
    protected final ObjectMap<String, b2Hull> polygonVerticesData = new ObjectMap<>();
    private AssetManager assetManager;
    private TextureAtlas atlas;

    public void load() {
        this.assetManager = new AssetManager();
        assetManager.load("packed/assets.atlas", TextureAtlas.class);
        assetManager.finishLoading();
        this.atlas = assetManager.get("packed/assets.atlas", TextureAtlas.class);

        JsonValue polygonsVertices = jsonReader.parse(Gdx.files.internal("packed/polygons.json"));
        JsonValue polygons = polygonsVertices.get("polygons");
        for (JsonValue polygon : polygons) {

            polygonVerticesData.put(polygon.name(), Box2dPlus.b2ComputeHull(polygon.asFloatArray()));
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureRegion getTexureRegion(String name) {
        return atlas.findRegion(name);
    }

    public b2Hull getHull(String name) {
        return polygonVerticesData.get(name);
    }
}
