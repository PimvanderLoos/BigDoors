package nl.pim16aap2.bigDoors.util;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;

import javax.annotation.Nullable;
import java.util.Objects;

public final class NMSUtil
{
    private NMSUtil()
    {
    }

    /**
     * Rotates a CraftBlockData instance vertically in a given direction.
     * <p>
     * Note that this directly updates the provided craftBlockData instance (if rotation is deemed necessary).
     *
     * @param openDirection
     *     The direction of the rotation to apply.
     * @param craftBlockData
     *     The CraftBlockData instance to try to rotate.
     */
    public static void rotateVerticallyInDirection(DoorDirection openDirection, Object craftBlockData)
    {
        if (!(craftBlockData instanceof Directional))
            return; // Nothing we can do

        final Directional directional = (Directional) craftBlockData;
        final BlockFace currentBlockFace = directional.getFacing();
        final @Nullable BlockFace newBlockFace;

        final BlockFace openingDirFace = openDirection.getBlockFace();
        final BlockFace oppositeDirFace =
            Objects.requireNonNull(DoorDirection.getOpposite(openDirection)).getBlockFace();

        if (craftBlockData instanceof Stairs)
            rotateVerticallyInDirection((Stairs) craftBlockData, openingDirFace, oppositeDirFace);

        if (currentBlockFace == openingDirFace)
            newBlockFace = BlockFace.DOWN;
        else if (currentBlockFace == oppositeDirFace)
            newBlockFace = BlockFace.UP;
        else if (currentBlockFace == BlockFace.UP)
            newBlockFace = openingDirFace;
        else if (currentBlockFace == BlockFace.DOWN)
            newBlockFace = oppositeDirFace;
        else
            return; // Nothing to do

        if (directional.getFaces().contains(newBlockFace))
            directional.setFacing(newBlockFace);
    }

    private static void rotateVerticallyInDirection(
        Stairs stairs,
        BlockFace openingDirFace,
        BlockFace oppositeDirFace)
    {
        final BlockFace currentBlockFace = stairs.getFacing();

        @Nullable BlockFace newBlockFace = null;
        @Nullable Stairs.Half newHalf = null;

        if (currentBlockFace == openingDirFace)
        {
            if (stairs.getHalf() == Stairs.Half.TOP)
                newHalf = Stairs.Half.BOTTOM;
            else
                newBlockFace = oppositeDirFace;
        }
        else if (currentBlockFace == oppositeDirFace)
        {
            if (stairs.getHalf() == Stairs.Half.BOTTOM)
                newHalf = Stairs.Half.TOP;
            else
                newBlockFace = openingDirFace;
        }
        else
            return; // Nothing to do

        if (newHalf != null)
            stairs.setHalf(newHalf);

        if (newBlockFace != null && stairs.getFaces().contains(newBlockFace))
            stairs.setFacing(newBlockFace);
    }
}
