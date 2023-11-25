package nl.pim16aap2.bigDoors.NMS;

import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public interface CustomCraftFallingBlock
{
    boolean teleport(Location newPos);

    void remove();

    void setVelocity(Vector vector);

    Location getLocation();

    Vector getVelocity();

    void setHeadPose(EulerAngle pose);

    void setBodyPose(EulerAngle eulerAngle);

    /**
     * Removes the entity from the world similar to {@link #remove()}.
     * <p>
     * This method may be overridden to perform additional actions when the entity is removed.
     * <p>
     * This method is 'private' because it should only be called by this plugin.
     */
    default void privateRemove()
    {
        remove();
    }

    default @Nullable ILoggableDoor getDoor()
    {
        return null;
    }
}

