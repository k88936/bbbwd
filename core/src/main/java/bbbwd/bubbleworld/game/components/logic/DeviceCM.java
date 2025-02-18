package bbbwd.bubbleworld.game.components.logic;

import bbbwd.bubbleworld.game.systems.device.JointDeviceUpdateSystem;
import com.artemis.PooledComponent;

import java.util.Arrays;

public class DeviceCM extends PooledComponent {
     public  double[] memory = new double[64];
     public JointDeviceUpdateSystem.PhysicsUpdateLogic logic;

    @Override
    protected void reset() {
        Arrays.fill(memory, 0);
    }
}
