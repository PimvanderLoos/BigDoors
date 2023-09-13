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
        this(entityhuman.level, entityhuman, enumhand, entityhuman.b(enumhand), movingobjectpositionblock);
    }

    public ItemActionContext(World world, @Nullable EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        this.player = entityhuman;
        this.hand = enumhand;
        this.hitResult = movingobjectpositionblock;
        this.itemStack = itemstack;
        this.level = world;
    }

    protected final MovingObjectPositionBlock j() {
        return this.hitResult;
    }

    public BlockPosition getClickPosition() {
        return this.hitResult.getBlockPosition();
    }

    public EnumDirection getClickedFace() {
        return this.hitResult.getDirection();
    }

    public Vec3D getPos() {
        return this.hitResult.getPos();
    }

    public boolean m() {
        return this.hitResult.d();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Nullable
    public EntityHuman getEntity() {
        return this.player;
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public World getWorld() {
        return this.level;
    }

    public EnumDirection g() {
        return this.player == null ? EnumDirection.NORTH : this.player.getDirection();
    }

    public boolean isSneaking() {
        return this.player != null && this.player.eZ();
    }

    public float i() {
        return this.player == null ? 0.0F : this.player.getYRot();
    }
}
