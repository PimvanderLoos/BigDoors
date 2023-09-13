package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFurnaceFurace;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityMinecartFurnace extends EntityMinecartAbstract {

    private static final DataWatcherObject<Boolean> DATA_ID_FUEL = DataWatcher.defineId(EntityMinecartFurnace.class, DataWatcherRegistry.BOOLEAN);
    public int fuel;
    public double xPush;
    public double zPush;
    private static final RecipeItemStack INGREDIENT = RecipeItemStack.of(Items.COAL, Items.CHARCOAL);

    public EntityMinecartFurnace(EntityTypes<? extends EntityMinecartFurnace> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMinecartFurnace(World world, double d0, double d1, double d2) {
        super(EntityTypes.FURNACE_MINECART, world, d0, d1, d2);
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.FURNACE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityMinecartFurnace.DATA_ID_FUEL, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            if (this.fuel > 0) {
                --this.fuel;
            }

            if (this.fuel <= 0) {
                this.xPush = 0.0D;
                this.zPush = 0.0D;
            }

            this.setHasFuel(this.fuel > 0);
        }

        if (this.hasFuel() && this.random.nextInt(4) == 0) {
            this.level.addParticle(Particles.LARGE_SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected double getMaxSpeed() {
        return (this.isInWater() ? 3.0D : 4.0D) / 20.0D;
    }

    @Override
    public void destroy(DamageSource damagesource) {
        super.destroy(damagesource);
        if (!damagesource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation((IMaterial) Blocks.FURNACE);
        }

    }

    @Override
    protected void moveAlongTrack(BlockPosition blockposition, IBlockData iblockdata) {
        double d0 = 1.0E-4D;
        double d1 = 0.001D;

        super.moveAlongTrack(blockposition, iblockdata);
        Vec3D vec3d = this.getDeltaMovement();
        double d2 = vec3d.horizontalDistanceSqr();
        double d3 = this.xPush * this.xPush + this.zPush * this.zPush;

        if (d3 > 1.0E-4D && d2 > 0.001D) {
            double d4 = Math.sqrt(d2);
            double d5 = Math.sqrt(d3);

            this.xPush = vec3d.x / d4 * d5;
            this.zPush = vec3d.z / d4 * d5;
        }

    }

    @Override
    protected void applyNaturalSlowdown() {
        double d0 = this.xPush * this.xPush + this.zPush * this.zPush;

        if (d0 > 1.0E-7D) {
            d0 = Math.sqrt(d0);
            this.xPush /= d0;
            this.zPush /= d0;
            Vec3D vec3d = this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush);

            if (this.isInWater()) {
                vec3d = vec3d.scale(0.1D);
            }

            this.setDeltaMovement(vec3d);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.0D, 0.98D));
        }

        super.applyNaturalSlowdown();
    }

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (EntityMinecartFurnace.INGREDIENT.test(itemstack) && this.fuel + 3600 <= 32000) {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            this.fuel += 3600;
        }

        if (this.fuel > 0) {
            this.xPush = this.getX() - entityhuman.getX();
            this.zPush = this.getZ() - entityhuman.getZ();
        }

        return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putDouble("PushX", this.xPush);
        nbttagcompound.putDouble("PushZ", this.zPush);
        nbttagcompound.putShort("Fuel", (short) this.fuel);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.xPush = nbttagcompound.getDouble("PushX");
        this.zPush = nbttagcompound.getDouble("PushZ");
        this.fuel = nbttagcompound.getShort("Fuel");
    }

    protected boolean hasFuel() {
        return (Boolean) this.entityData.get(EntityMinecartFurnace.DATA_ID_FUEL);
    }

    protected void setHasFuel(boolean flag) {
        this.entityData.set(EntityMinecartFurnace.DATA_ID_FUEL, flag);
    }

    @Override
    public IBlockData getDefaultDisplayBlockState() {
        return (IBlockData) ((IBlockData) Blocks.FURNACE.defaultBlockState().setValue(BlockFurnaceFurace.FACING, EnumDirection.NORTH)).setValue(BlockFurnaceFurace.LIT, this.hasFuel());
    }
}
