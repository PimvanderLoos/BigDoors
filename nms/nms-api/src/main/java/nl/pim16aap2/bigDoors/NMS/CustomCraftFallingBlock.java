package nl.pim16aap2.bigDoors.NMS;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public interface CustomCraftFallingBlock extends Entity
{
    boolean teleport(@Nonnull Location newPos);

    void remove();

    void setVelocity(@Nonnull Vector vector);

    @Nonnull Location getLocation();

    @Nonnull Vector getVelocity();

    void setHeadPose(EulerAngle pose);

    void setBodyPose(EulerAngle eulerAngle);
}

