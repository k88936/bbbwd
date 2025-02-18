import bbbwd.bubbleworld.utils.Utils;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class TestUtils {
    @Test
    void testGridize() {
        Vector2 v = new Vector2(-0.99f, 0.249f);
        Utils.gridOf(v);
        Logger.getGlobal().info(v.toString());
        assert v.x == -1;
        assert v.y == 0.25f;
    }
}
