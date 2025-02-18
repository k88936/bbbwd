package bbbwd.bubbleworld.game.systems.device;

import bbbwd.bubbleworld.game.components.logic.DeviceCM;
import bbbwd.bubbleworld.game.components.physics.JointCM;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;

import java.util.Arrays;

@All({DeviceCM.class, JointCM.class})
public class JointDeviceUpdateSystem extends BaseEntitySystem {
    ComponentMapper<DeviceCM> deviceCMComponentMapper;
    ComponentMapper<JointCM> jointCMComponentMapper;

    @Override
    protected void processSystem() {
        Arrays.stream(subscription.getEntities().getData(), 0, subscription.getEntities().size()).parallel().forEach(entityId -> {
            DeviceCM deviceCM = deviceCMComponentMapper.get(entityId);
            JointCM jointCM = jointCMComponentMapper.get(entityId);
            deviceCM.logic.update(deviceCM, jointCM);
        });
    }

    public interface PhysicsUpdateLogic {
        void update(DeviceCM deviceCM, JointCM jointCM);
    }
}
