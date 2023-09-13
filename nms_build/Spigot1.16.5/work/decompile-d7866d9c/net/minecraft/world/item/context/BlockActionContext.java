package net.minecraft.world.item.context;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class BlockActionContext extends ItemActionContext {

    private final BlockPosition b;
    protected boolean a;

    public BlockActionContext(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        this(entityhuman.world, entityhuman, enumhand, itemstack, movingobjectpositionblock);
    }

    public BlockActionContext(ItemActionContext itemactioncontext) {
        this(itemactioncontext.getWorld(), itemactioncontext.getEntity(), itemactioncontext.getHand(), itemactioncontext.getItemStack(), itemactioncontext.i());
    }

    protected BlockActionContext(World world, @Nullable EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        super(world, entityhuman, enumhand, itemstack, movingobjectpositionblock);
        this.a = true;
        this.b = movingobjectpositionblock.getBlockPosition().shift(movingobjectpositionblock.getDirection());
        this.a = world.getType(movingobjectpositionblock.getBlockPosition()).a(this);
    }

    public static BlockActionContext a(BlockActionContext blockactioncontext, BlockPosition blockposition, EnumDirection enumdirection) {
        return new BlockActionContext(blockactioncontext.getWorld(), blockactioncontext.getEntity(), blockactioncontext.getHand(), blockactioncontext.getItemStack(), new MovingObjectPositionBlock(new Vec3D((double) blockposition.getX() + 0.5D + (double) enumdirection.getAdjacentX() * 0.5D, (double) blockposition.getY() + 0.5D + (double) enumdirection.getAdjacentY() * 0.5D, (double) blockposition.getZ() + 0.5D + (double) enumdirection.getAdjacentZ() * 0.5D), enumdirection, blockposition, false));
    }

    @Override
    public BlockPosition getClickPosition() {
        return this.a ? super.getClickPosition() : this.b;
    }

    public boolean b() {
        return this.a || this.getWorld().getType(this.getClickPosition()).a(this);
    }

    public boolean c() {
        return this.a;
    }

    public EnumDirection d() {
        return EnumDirection.a((Entity) this.getEntity())[0];
    }

    public EnumDirection[] e() {
        EnumDirection[] aenumdirection = EnumDirection.a((Entity) this.getEntity());

        if (this.a) {
            return aenumdirection;
        } else {
            EnumDirection enumdirection = this.getClickedFace();

            int i;

            for (i = 0; i < aenumdirection.length && aenumdirection[i] != enumdirection.opposite(); ++i) {
                ;
            }

            if (i > 0) {
                System.arraycopy(aenumdirection, 0, aenumdirection, 1, i);
                aenumdirection[0] = enumdirection.opposite();
            }

            return aenumdirection;
        }
    }
}
