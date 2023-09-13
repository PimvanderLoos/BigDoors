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
    private final EntityHuman a;
    private final EnumHand b;
    private final MovingObjectPositionBlock c;
    private final World d;
    private final ItemStack e;

    public ItemActionContext(EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        this(entityhuman.world, entityhuman, enumhand, entityhuman.b(enumhand), movingobjectpositionblock);
    }

    public ItemActionContext(World world, @Nullable EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        this.a = entityhuman;
        this.b = enumhand;
        this.c = movingobjectpositionblock;
        this.e = itemstack;
        this.d = world;
    }

    protected final MovingObjectPositionBlock i() {
        return this.c;
    }

    public BlockPosition getClickPosition() {
        return this.c.getBlockPosition();
    }

    public EnumDirection getClickedFace() {
        return this.c.getDirection();
    }

    public Vec3D getPos() {
        return this.c.getPos();
    }

    public boolean l() {
        return this.c.d();
    }

    public ItemStack getItemStack() {
        return this.e;
    }

    @Nullable
    public EntityHuman getEntity() {
        return this.a;
    }

    public EnumHand getHand() {
        return this.b;
    }

    public World getWorld() {
        return this.d;
    }

    public EnumDirection f() {
        return this.a == null ? EnumDirection.NORTH : this.a.getDirection();
    }

    public boolean isSneaking() {
        return this.a != null && this.a.eq();
    }

    public float h() {
        return this.a == null ? 0.0F : this.a.yaw;
    }
}
