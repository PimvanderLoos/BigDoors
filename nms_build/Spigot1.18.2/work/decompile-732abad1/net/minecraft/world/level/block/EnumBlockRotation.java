package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import com.mojang.math.PointGroupO;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.EnumDirection;

public enum EnumBlockRotation {

    NONE(PointGroupO.IDENTITY), CLOCKWISE_90(PointGroupO.ROT_90_Y_NEG), CLOCKWISE_180(PointGroupO.ROT_180_FACE_XZ), COUNTERCLOCKWISE_90(PointGroupO.ROT_90_Y_POS);

    private final PointGroupO rotation;

    private EnumBlockRotation(PointGroupO pointgroupo) {
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

    public static EnumBlockRotation getRandom(Random random) {
        return (EnumBlockRotation) SystemUtils.getRandom((Object[]) values(), random);
    }

    public static List<EnumBlockRotation> getShuffled(Random random) {
        List<EnumBlockRotation> list = Lists.newArrayList(values());

        Collections.shuffle(list, random);
        return list;
    }
}
