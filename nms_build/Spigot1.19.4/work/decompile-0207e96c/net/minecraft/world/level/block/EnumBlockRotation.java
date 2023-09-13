package net.minecraft.world.level.block;

import com.mojang.math.PointGroupO;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.INamable;
import net.minecraft.util.RandomSource;

public enum EnumBlockRotation implements INamable {

    NONE("none", PointGroupO.IDENTITY), CLOCKWISE_90("clockwise_90", PointGroupO.ROT_90_Y_NEG), CLOCKWISE_180("180", PointGroupO.ROT_180_FACE_XZ), COUNTERCLOCKWISE_90("counterclockwise_90", PointGroupO.ROT_90_Y_POS);

    public static final Codec<EnumBlockRotation> CODEC = INamable.fromEnum(EnumBlockRotation::values);
    private final String id;
    private final PointGroupO rotation;

    private EnumBlockRotation(String s, PointGroupO pointgroupo) {
        this.id = s;
        this.rotation = pointgroupo;
    }

    public EnumBlockRotation getRotated(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                switch (this) {
                    case NONE:
                        return EnumBlockRotation.CLOCKWISE_180;
                    case CLOCKWISE_90:
                        return EnumBlockRotation.COUNTERCLOCKWISE_90;
                    case CLOCKWISE_180:
                        return EnumBlockRotation.NONE;
                    case COUNTERCLOCKWISE_90:
                        return EnumBlockRotation.CLOCKWISE_90;
                }
            case COUNTERCLOCKWISE_90:
                switch (this) {
                    case NONE:
                        return EnumBlockRotation.COUNTERCLOCKWISE_90;
                    case CLOCKWISE_90:
                        return EnumBlockRotation.NONE;
                    case CLOCKWISE_180:
                        return EnumBlockRotation.CLOCKWISE_90;
                    case COUNTERCLOCKWISE_90:
                        return EnumBlockRotation.CLOCKWISE_180;
                }
            case CLOCKWISE_90:
                switch (this) {
                    case NONE:
                        return EnumBlockRotation.CLOCKWISE_90;
                    case CLOCKWISE_90:
                        return EnumBlockRotation.CLOCKWISE_180;
                    case CLOCKWISE_180:
                        return EnumBlockRotation.COUNTERCLOCKWISE_90;
                    case COUNTERCLOCKWISE_90:
                        return EnumBlockRotation.NONE;
                }
            default:
                return this;
        }
    }

    public PointGroupO rotation() {
        return this.rotation;
    }

    public EnumDirection rotate(EnumDirection enumdirection) {
        if (enumdirection.getAxis() == EnumDirection.EnumAxis.Y) {
            return enumdirection;
        } else {
            switch (this) {
                case CLOCKWISE_90:
                    return enumdirection.getClockWise();
                case CLOCKWISE_180:
                    return enumdirection.getOpposite();
                case COUNTERCLOCKWISE_90:
                    return enumdirection.getCounterClockWise();
                default:
                    return enumdirection;
            }
        }
    }

    public int rotate(int i, int j) {
        switch (this) {
            case CLOCKWISE_90:
                return (i + j / 4) % j;
            case CLOCKWISE_180:
                return (i + j / 2) % j;
            case COUNTERCLOCKWISE_90:
                return (i + j * 3 / 4) % j;
            default:
                return i;
        }
    }

    public static EnumBlockRotation getRandom(RandomSource randomsource) {
        return (EnumBlockRotation) SystemUtils.getRandom((Object[]) values(), randomsource);
    }

    public static List<EnumBlockRotation> getShuffled(RandomSource randomsource) {
        return SystemUtils.shuffledCopy((Object[]) values(), randomsource);
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
