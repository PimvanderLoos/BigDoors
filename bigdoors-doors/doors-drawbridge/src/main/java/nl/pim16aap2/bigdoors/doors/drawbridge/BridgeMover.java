package nl.pim16aap2.bigdoors.doors.drawbridge;

import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.api.animatedblock.IAnimatedBlock;
import nl.pim16aap2.bigdoors.doors.AbstractDoor;
import nl.pim16aap2.bigdoors.doors.DoorBase;
import nl.pim16aap2.bigdoors.doors.doorarchetypes.IHorizontalAxisAligned;
import nl.pim16aap2.bigdoors.events.dooraction.DoorActionCause;
import nl.pim16aap2.bigdoors.events.dooraction.DoorActionType;
import nl.pim16aap2.bigdoors.moveblocks.BlockMover;
import nl.pim16aap2.bigdoors.util.Cuboid;
import nl.pim16aap2.bigdoors.util.RotateDirection;
import nl.pim16aap2.bigdoors.util.Util;
import nl.pim16aap2.bigdoors.util.functional.TriFunction;
import nl.pim16aap2.bigdoors.util.vector.IVector3D;
import nl.pim16aap2.bigdoors.util.vector.Vector3Dd;

/**
 * Represents a {@link BlockMover} for {@link Drawbridge}s.
 *
 * @author Pim
 */
public class BridgeMover<T extends AbstractDoor & IHorizontalAxisAligned> extends BlockMover
{
    private final Vector3Dd rotationCenter;
    protected final boolean northSouth;
    protected final TriFunction<Vector3Dd, Vector3Dd, Double, Vector3Dd> rotator;

    private int halfEndCount;
    private double step;
    protected final double angle;

    /**
     * Constructs a {@link BlockMover}.
     *
     * @param door
     *     The {@link DoorBase}.
     * @param time
     *     The amount of time (in seconds) the door will try to toggle itself in.
     * @param skipAnimation
     *     If the door should be opened instantly (i.e. skip animation) or not.
     * @param rotateDirection
     *     The direction the {@link DoorBase} will move.
     * @param multiplier
     *     The speed multiplier.
     * @param player
     *     The player who opened this door.
     */
    public BridgeMover(
        Context context, double time, T door, RotateDirection rotateDirection, boolean skipAnimation, double multiplier,
        IPPlayer player, Cuboid newCuboid, DoorActionCause cause, DoorActionType actionType)
        throws Exception
    {
        super(context, door, time, skipAnimation, rotateDirection, player, newCuboid, cause, actionType);

        northSouth = door.isNorthSouthAligned();
        rotationCenter = door.getRotationPoint().toDouble().add(0.5, 0, 0.5);

        final int xLen = Math.abs(door.getMaximum().x() - door.getMinimum().x());
        final int yLen = Math.abs(door.getMaximum().y() - door.getMinimum().y());
        final int zLen = Math.abs(door.getMaximum().z() - door.getMinimum().z());
        final int doorSize = Math.max(xLen, Math.max(yLen, zLen)) + 1;
        final double[] vars = Util.calculateTimeAndTickRate(doorSize, time, multiplier, 5.2);
        this.time = vars[0];

        switch (rotateDirection)
        {
            case NORTH:
                angle = -Math.PI / 2;
                rotator = Vector3Dd::rotateAroundXAxis;
                break;
            case SOUTH:
                angle = Math.PI / 2;
                rotator = Vector3Dd::rotateAroundXAxis;
                break;
            case EAST:
                angle = Math.PI / 2;
                rotator = Vector3Dd::rotateAroundZAxis;
                break;
            case WEST:
                angle = -Math.PI / 2;
                rotator = Vector3Dd::rotateAroundZAxis;
                break;
            default:
                throw new IllegalArgumentException("RotateDirection \"" + rotateDirection.name() +
                                                       " is not valid for this type!");
        }

        init();
        super.startAnimation();
    }

    /**
     * Used for initializing variables such as {@link #animationDuration}.
     */
    protected void init()
    {
        super.animationDuration = (int) (20 * super.time);
        step = angle / super.animationDuration;
        halfEndCount = super.animationDuration / 2;
    }

    protected Vector3Dd getGoalPos(double angle, double x, double y, double z)
    {
        return rotator.apply(new Vector3Dd(x, y, z), rotationCenter, angle);
    }

    protected Vector3Dd getGoalPos(double angle, IAnimatedBlock animatedBlock)
    {
        return getGoalPos(angle, animatedBlock.getStartX(), animatedBlock.getStartY(), animatedBlock.getStartZ());
    }

    @Override
    protected Vector3Dd getFinalPosition(IVector3D startLocation, float radius)
    {
        return getGoalPos(angle, startLocation.xD(), startLocation.yD(), startLocation.zD());
    }

    @Override
    protected void executeAnimationStep(int ticks)
    {
        final double stepSum = step * ticks;
        final boolean replace = ticks == halfEndCount;

        if (replace)
            this.respawnBlocks();

        for (final IAnimatedBlock animatedBlock : animatedBlocks)
            movementMethod.apply(animatedBlock, getGoalPos(stepSum, animatedBlock));
    }

    @Override
    protected float getRadius(int xAxis, int yAxis, int zAxis)
    {
        // Get the current radius of a block between used axis (either x and y, or z and y).
        // When the rotation point is positioned along the NS axis, the Z values does not change.
        final double deltaA = (double) door.getRotationPoint().y() - yAxis;
        final double deltaB =
            northSouth ? (door.getRotationPoint().x() - xAxis) : (door.getRotationPoint().z() - zAxis);
        return (float) Math.sqrt(Math.pow(deltaA, 2) + Math.pow(deltaB, 2));
    }

    @Override
    protected float getStartAngle(int xAxis, int yAxis, int zAxis)
    {
        // Get the angle between the used axes (either x and y, or z and y).
        // When the rotation point is positioned along the NS axis, the Z values does not change.
        final double deltaA = northSouth ? door.getRotationPoint().x() - xAxis : door.getRotationPoint().z() - zAxis;
        final double deltaB = (double) door.getRotationPoint().y() - yAxis;
        return (float) Util.clampAngleRad(Math.atan2(deltaA, deltaB));
    }
}
