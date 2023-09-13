package net.minecraft.world.level.block;

import com.mojang.math.PointGroupO;
import com.mojang.serialization.Codec;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.INamable;

public enum EnumBlockMirror implements INamable {

    NONE("none", PointGroupO.IDENTITY), LEFT_RIGHT("left_right", PointGroupO.INVERT_Z), FRONT_BACK("front_back", PointGroupO.INVERT_X);

    public static final Codec<EnumBlockMirror> CODEC = INamable.fromEnum(EnumBlockMirror::values);
    private final String id;
    private final IChatBaseComponent symbol;
    private final PointGroupO rotation;

    private EnumBlockMirror(String s, PointGroupO pointgroupo) {
        this.id = s;
        this.symbol = IChatBaseComponent.translatable("mirror." + s);
        this.rotation = pointgroupo;
    }

    public int mirror(int i, int j) {
        int k = j / 2;
        int l = i > k ? i - j : i;

        switch (this) {
            case FRONT_BACK:
                return (j - l) % j;
            case LEFT_RIGHT:
                return (k - l + j) % j;
            default:
                return i;
        }
    }

    public EnumBlockRotation getRotation(EnumDirection enumdirection) {
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();

        return (this != EnumBlockMirror.LEFT_RIGHT || enumdirection_enumaxis != EnumDirection.EnumAxis.Z) && (this != EnumBlockMirror.FRONT_BACK || enumdirection_enumaxis != EnumDirection.EnumAxis.X) ? EnumBlockRotation.NONE : EnumBlockRotation.CLOCKWISE_180;
    }

    public EnumDirection mirror(EnumDirection enumdirection) {
        return this == EnumBlockMirror.FRONT_BACK && enumdirection.getAxis() == EnumDirection.EnumAxis.X ? enumdirection.getOpposite() : (this == EnumBlockMirror.LEFT_RIGHT && enumdirection.getAxis() == EnumDirection.EnumAxis.Z ? enumdirection.getOpposite() : enumdirection);
    }

    public PointGroupO rotation() {
        return this.rotation;
    }

    public IChatBaseComponent symbol() {
        return this.symbol;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
