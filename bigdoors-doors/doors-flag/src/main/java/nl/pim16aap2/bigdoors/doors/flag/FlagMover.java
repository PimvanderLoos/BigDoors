package nl.pim16aap2.bigdoors.doors.flag;

import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.api.animatedblock.IAnimatedBlock;
import nl.pim16aap2.bigdoors.events.dooraction.DoorActionCause;
import nl.pim16aap2.bigdoors.events.dooraction.DoorActionType;
import nl.pim16aap2.bigdoors.moveblocks.BlockMover;
import nl.pim16aap2.bigdoors.util.RotateDirection;
import nl.pim16aap2.bigdoors.util.vector.IVector3D;
import nl.pim16aap2.bigdoors.util.vector.Vector3Dd;

import java.util.function.BiFunction;

/**
 * Represents a {@link BlockMover} for {@link Flag}s.
 *
 * @author Pim
 */
@SuppressWarnings({"FieldCanBeLocal", "unused", "squid:S1172", "CommentedOutCode", "PMD"})
public class FlagMover extends BlockMover
{
    private final BiFunction<IAnimatedBlock, Integer, Vector3Dd> getGoalPos;
    private final boolean NS;
    private final double period;
    private final double amplitude;
    private final double waveSpeed;

    public FlagMover(
        Context context, double time, Flag door, double multiplier, IPPlayer player, DoorActionCause cause,
        DoorActionType actionType)
        throws Exception
    {
        super(context, door, time, false, RotateDirection.NONE, player, door.getCuboid(), cause, actionType);

        final int xLen = Math.abs(xMax - xMin) + 1;
        final int zLen = Math.abs(zMax - zMin) + 1;
        NS = door.isNorthSouthAligned();
        getGoalPos = NS ? this::getGoalPosNS : this::getGoalPosEW;

        final int length = NS ? zLen : xLen;
        period = length * 2.0f;
        amplitude = length / 4.0;

        this.time = time;
        waveSpeed = 10.0f;

        init();
        super.startAnimation();
    }

    /**
     * Used for initializing variables such as {@link #animationDuration}.
     */
    protected void init()
    {
        super.animationDuration = 200;
    }

    /**
     * Gets the maximum offset of a animatedBlock.
     *
     * @param counter
     * @param radius
     * @return
     */
    private double getOffset(int counter, float radius)
    {
//        double baseOffset = Math.sin(0.5 * Math.PI * (counter * tickRate / 20) + radius);
//        double maxVal = 0.25 * radius;
//        maxVal = Math.min(maxVal, 0.75);
//        return Math.min(baseOffset, maxVal);

//        // The idea here is that blocks should never lose contact with other blocks.
//        // Especially the blocks with radius 1 should never lose contact with the pole.
//        double maxAmplitude = radius * 0.4;


        return Math.min(0.3 * radius, 3.2) * Math.sin(radius / 3.0 + (counter / 4.0));

//        double offset;
//        try
//        {
//            offset = JCalculator
//                .getResult(BigDoors.get().getPlatform().getConfigLoader().flagFormula(),
//                           new String[]{"radius", "counter"},
//                           new double[]{radius, counter});
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            offset = 0;
//        }
//        return offset;
    }

    private Vector3Dd getGoalPosNS(IAnimatedBlock animatedBlock, int counter)
    {
        double xOff = 0;
        if (animatedBlock.getRadius() > 0)
            xOff = getOffset(counter, animatedBlock.getRadius());
        return new Vector3Dd(animatedBlock.getStartX() + xOff, animatedBlock.getStartY(), animatedBlock.getStartZ());
    }

    private Vector3Dd getGoalPosEW(IAnimatedBlock animatedBlock, int counter)
    {
        double zOff = 0;
        if (animatedBlock.getRadius() > 0)
            zOff = getOffset(counter, animatedBlock.getRadius());
        return new Vector3Dd(animatedBlock.getStartX(), animatedBlock.getStartY(), animatedBlock.getStartZ() + zOff);
    }

    @Override
    protected Vector3Dd getFinalPosition(IVector3D startLocation, float radius)
    {
        return Vector3Dd.of(startLocation);
    }

    @Override
    protected void executeAnimationStep(int ticks)
    {
        for (final IAnimatedBlock animatedBlock : animatedBlocks)
            movementMethod.apply(animatedBlock, getGoalPos.apply(animatedBlock, ticks));
    }

    @Override
    protected float getRadius(int xAxis, int yAxis, int zAxis)
    {
        if (NS)
            return Math.abs((float) zAxis - door.getRotationPoint().z());
        return Math.abs((float) xAxis - door.getRotationPoint().x());
    }
}
