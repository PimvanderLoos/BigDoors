package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.util.MathHelper;
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

    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityMinecartFurnace.class, DataWatcherRegistry.i);
    public int fuel;
    public double b;
    public double c;
    private static final RecipeItemStack f = RecipeItemStack.a(Items.COAL, Items.CHARCOAL);

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
        this.datawatcher.register(EntityMinecartFurnace.d, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.s_()) {
            if (this.fuel > 0) {
                --this.fuel;
            }

            if (this.fuel <= 0) {
                this.b = 0.0D;
                this.c = 0.0D;
            }

            this.o(this.fuel > 0);
        }

        if (this.u() && this.random.nextInt(4) == 0) {
            this.world.addParticle(Particles.LARGE_SMOKE, this.locX(), this.locY() + 0.8D, this.locZ(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected double getMaxSpeed() {
        return 0.2D;
    }

    @Override
    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (!damagesource.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.a((IMaterial) Blocks.FURNACE);
        }

    }

    @Override
    protected void c(BlockPosition blockposition, IBlockData iblockdata) {
        double d0 = 1.0E-4D;
        double d1 = 0.001D;

        super.c(blockposition, iblockdata);
        Vec3D vec3d = this.getMot();
        double d2 = c(vec3d);
        double d3 = this.b * this.b + this.c * this.c;

        if (d3 > 1.0E-4D && d2 > 0.001D) {
            double d4 = (double) MathHelper.sqrt(d2);
            double d5 = (double) MathHelper.sqrt(d3);

            this.b = vec3d.x / d4 * d5;
            this.c = vec3d.z / d4 * d5;
        }

    }

    @Override
    protected void decelerate() {
        double d0 = this.b * this.b + this.c * this.c;

        if (d0 > 1.0E-7D) {
            d0 = (double) MathHelper.sqrt(d0);
            this.b /= d0;
            this.c /= d0;
            this.setMot(this.getMot().d(0.8D, 0.0D, 0.8D).add(this.b, 0.0D, this.c));
        } else {
            this.setMot(this.getMot().d(0.98D, 0.0D, 0.98D));
        }

        super.decelerate();
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (EntityMinecartFurnace.f.test(itemstack) && this.fuel + 3600 <= 32000) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            this.fuel += 3600;
        }

        if (this.fuel > 0) {
            this.b = this.locX() - entityhuman.locX();
            this.c = this.locZ() - entityhuman.locZ();
        }

        return EnumInteractionResult.a(this.world.isClientSide);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setDouble("PushX", this.b);
        nbttagcompound.setDouble("PushZ", this.c);
        nbttagcompound.setShort("Fuel", (short) this.fuel);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.b = nbttagcompound.getDouble("PushX");
        this.c = nbttagcompound.getDouble("PushZ");
        this.fuel = nbttagcompound.getShort("Fuel");
    }

    protected boolean u() {
        return (Boolean) this.datawatcher.get(EntityMinecartFurnace.d);
    }

    protected void o(boolean flag) {
        this.datawatcher.set(EntityMinecartFurnace.d, flag);
    }

    @Override
    public IBlockData q() {
        return (IBlockData) ((IBlockData) Blocks.FURNACE.getBlockData().set(BlockFurnaceFurace.FACING, EnumDirection.NORTH)).set(BlockFurnaceFurace.LIT, this.u());
    }
}
