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

    private final PointGroupO e;

    private EnumBlockRotation(PointGroupO pointgroupo) {
        this.e = pointgroupo;
    }

    public EnumBlockRotation a(EnumBlockRotation enumblockrotation) {
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

    public PointGroupO a() {
        return this.e;
    }

    public EnumDirection a(EnumDirection enumdirection) {
        if (enumdirection.n() == EnumDirection.EnumAxis.Y) {
            return enumdirection;
        } else {
            switch (this) {
                case CLOCKWISE_90:
                    return enumdirection.g();
                case CLOCKWISE_180:
                    return enumdirection.opposite();
                case COUNTERCLOCKWISE_90:
                    return enumdirection.h();
                default:
                    return enumdirection;
            }
        }
    }

    public int a(int i, int j) {
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

    public static EnumBlockRotation a(Random random) {
        return (EnumBlockRotation) SystemUtils.a((Object[]) values(), random);
    }

    public static List<EnumBlockRotation> b(Random random) {
        List<EnumBlockRotation> list = Lists.newArrayList(values());

        Collections.shuffle(list, random);
        return list;
    }
}
