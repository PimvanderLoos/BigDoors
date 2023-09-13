package net.minecraft.world.level.block;

import com.mojang.math.PointGroupO;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public enum EnumBlockMirror {

    NONE(new ChatMessage("mirror.none"), PointGroupO.IDENTITY), LEFT_RIGHT(new ChatMessage("mirror.left_right"), PointGroupO.INVERT_Z), FRONT_BACK(new ChatMessage("mirror.front_back"), PointGroupO.INVERT_X);

    private final IChatBaseComponent symbol;
    private final PointGroupO rotation;

    private EnumBlockMirror(IChatBaseComponent ichatbasecomponent, PointGroupO pointgroupo) {
        this.symbol = ichatbasecomponent;
        this.rotation = pointgroupo;
    }

    public int a(int i, int j) {
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

    public EnumBlockRotation a(EnumDirection enumdirection) {
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();

        return (this != EnumBlockMirror.LEFT_RIGHT || enumdirection_enumaxis != EnumDirection.EnumAxis.Z) && (this != EnumBlockMirror.FRONT_BACK || enumdirection_enumaxis != EnumDirection.EnumAxis.X) ? EnumBlockRotation.NONE : EnumBlockRotation.CLOCKWISE_180;
    }

    public EnumDirection b(EnumDirection enumdirection) {
        return this == EnumBlockMirror.FRONT_BACK && enumdirection.n() == EnumDirection.EnumAxis.X ? enumdirection.opposite() : (this == EnumBlockMirror.LEFT_RIGHT && enumdirection.n() == EnumDirection.EnumAxis.Z ? enumdirection.opposite() : enumdirection);
    }

    public PointGroupO a() {
        return this.rotation;
    }

    public IChatBaseComponent b() {
        return this.symbol;
    }
}
