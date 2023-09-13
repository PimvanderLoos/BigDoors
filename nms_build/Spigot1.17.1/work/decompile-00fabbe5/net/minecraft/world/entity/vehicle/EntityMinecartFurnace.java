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

    private static final DataWatcherObject<Boolean> DATA_ID_FUEL = DataWatcher.a(EntityMinecartFurnace.class, DataWatcherRegistry.BOOLEAN);
    public int fuel;
    public double xPush;
    public double zPush;
    private static final RecipeItemStack INGREDIENT = RecipeItemStack.a(Items.COAL, Items.CHARCOAL);

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
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityMinecartFurnace.DATA_ID_FUEL, false);
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

            this.p(this.fuel > 0);
        }

        if (this.w() && this.random.nextInt(4) == 0) {
            this.level.addParticle(Particles.LARGE_SMOKE, this.locX(), this.locY() + 0.8D, this.locZ(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected double getMaxSpeed() {
        return (this.isInWater() ? 3.0D : 4.0D) / 20.0D;
    }

    @Override
    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (!damagesource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.a((IMaterial) Blocks.FURNACE);
        }

    }

    @Override
    protected void c(BlockPosition blockposition, IBlockData iblockdata) {
        double d0 = 1.0E-4D;
        double d1 = 0.001D;

        super.c(blockposition, iblockdata);
        Vec3D vec3d = this.getMot();
        double d2 = vec3d.i();
        double d3 = this.xPush * this.xPush + this.zPush * this.zPush;

        if (d3 > 1.0E-4D && d2 > 0.001D) {
            double d4 = Math.sqrt(d2);
            double d5 = Math.sqrt(d3);

            this.xPush = vec3d.x / d4 * d5;
            this.zPush = vec3d.z / d4 * d5;
        }

    }

    @Override
    protected void decelerate() {
        double d0 = this.xPush * this.xPush + this.zPush * this.zPush;

        if (d0 > 1.0E-7D) {
            d0 = Math.sqrt(d0);
            this.xPush /= d0;
            this.zPush /= d0;
            Vec3D vec3d = this.getMot().d(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush);

            if (this.isInWater()) {
                vec3d = vec3d.a(0.1D);
            }

            this.setMot(vec3d);
        } else {
            this.setMot(this.getMot().d(0.98D, 0.0D, 0.98D));
        }

        super.decelerate();
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (EntityMinecartFurnace.INGREDIENT.test(itemstack) && this.fuel + 3600 <= 32000) {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.subtract(1);
            }

            this.fuel += 3600;
        }

        if (this.fuel > 0) {
            this.xPush = this.locX() - entityhuman.locX();
            this.zPush = this.locZ() - entityhuman.locZ();
        }

        return EnumInteractionResult.a(this.level.isClientSide);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setDouble("PushX", this.xPush);
        nbttagcompound.setDouble("PushZ", this.zPush);
        nbttagcompound.setShort("Fuel", (short) this.fuel);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.xPush = nbttagcompound.getDouble("PushX");
        this.zPush = nbttagcompound.getDouble("PushZ");
        this.fuel = nbttagcompound.getShort("Fuel");
    }

    protected boolean w() {
        return (Boolean) this.entityData.get(EntityMinecartFurnace.DATA_ID_FUEL);
    }

    protected void p(boolean flag) {
        this.entityData.set(EntityMinecartFurnace.DATA_ID_FUEL, flag);
    }

    @Override
    public IBlockData r() {
        return (IBlockData) ((IBlockData) Blocks.FURNACE.getBlockData().set(BlockFurnaceFurace.FACING, EnumDirection.NORTH)).set(BlockFurnaceFurace.LIT, this.w());
    }
}
