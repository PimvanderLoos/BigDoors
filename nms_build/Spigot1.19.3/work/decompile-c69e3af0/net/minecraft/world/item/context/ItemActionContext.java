package net.minecraft.world.item.context;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class ItemActionContext {

    @Nullable
    private final EntityHuman player;
    private final EnumHand hand;
    private final MovingObjectPositionBlock hitResult;
    private final World level;
    private final ItemStack itemStack;

    public ItemActionContext(EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        this(entityhuman.level, entityhuman, enumhand, entityhuman.getItemInHand(enumhand), movingobjectpositionblock);
    }

    public ItemActionContext(World world, @Nullable EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        this.player = entityhuman;
        this.hand = enumhand;
        this.hitResult = movingobjectpositionblock;
        this.itemStack = itemstack;
        this.level = world;
    }

    protected final MovingObjectPositionBlock getHitResult() {
        return this.hitResult;
    }

    public BlockPosition getClickedPos() {
        return this.hitResult.getBlockPos();
    }

    public EnumDirection getClickedFace() {
        return this.hitResult.getDirection();
    }

    public Vec3D getClickLocation() {
        return this.hitResult.getLocation();
    }

    public boolean isInside() {
        return this.hitResult.isInside();
    }

    public ItemStack getItemInHand() {
        return this.itemStack;
    }

    @Nullable
    public EntityHuman getPlayer() {
        return this.player;
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public World getLevel() {
        return this.level;
    }

    public EnumDirection getHorizontalDirection() {
        return this.player == null ? EnumDirection.NORTH : this.player.getDirection();
    }

    public boolean isSecondaryUseActive() {
        return this.player != null && this.player.isSecondaryUseActive();
    }

    public float getRotation() {
        return this.player == null ? 0.0F : this.player.getYRot();
    }
}
