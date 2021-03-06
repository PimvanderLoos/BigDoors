package nl.pim16aap2.bigdoors.api.animatedblock;

import nl.pim16aap2.bigdoors.util.RotateDirection;
import nl.pim16aap2.bigdoors.util.vector.Vector3Dd;
import nl.pim16aap2.bigdoors.util.vector.Vector3Di;

/**
 * Represents an NMS block.
 *
 * @author Pim
 */
public interface IAnimatedBlockData
{
    /**
     * Checks if this block can rotate.
     *
     * @return True if this block can rotate.
     */
    boolean canRotate();

    /**
     * Rotates this block in a provided {@link RotateDirection}.
     *
     * @param rotDir
     *     The {@link RotateDirection} to rotate this block in.
     * @return True if the block was rotated.
     */
    boolean rotateBlock(RotateDirection rotDir);

    /**
     * @param loc
     *     The position where the block will be placed.
     */
    void putBlock(Vector3Di loc);

    /**
     * @param loc
     *     The position where the block will be placed.
     */
    void putBlock(Vector3Dd loc);

    /**
     * Deletes the block at the original location.
     *
     * @param applyPhysics
     *     True to apply physics when removing this block. When this is false, stuff like torches that are attached to
     *     this block will not be broken upon removal of this block.
     */
    void deleteOriginalBlock(boolean applyPhysics);
}
