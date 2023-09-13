package net.minecraft.world.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.World;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityExperienceOrb extends Entity {

    private static final int LIFETIME = 6000;
    private static final int ENTITY_SCAN_PERIOD = 20;
    private static final int MAX_FOLLOW_DIST = 8;
    private static final int ORB_GROUPS_PER_AREA = 40;
    private static final double ORB_MERGE_DISTANCE = 0.5D;
    private int age;
    private int health;
    public int value;
    private int count;
    private EntityHuman followingPlayer;

    public EntityExperienceOrb(World world, double d0, double d1, double d2, int i) {
        this(EntityTypes.EXPERIENCE_ORB, world);
        this.setPos(d0, d1, d2);
        this.setYRot((float) (this.random.nextDouble() * 360.0D));
        this.setDeltaMovement((this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
        this.value = i;
    }

    public EntityExperienceOrb(EntityTypes<? extends EntityExperienceOrb> entitytypes, World world) {
        super(entitytypes, world);
        this.health = 5;
        this.count = 1;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.isEyeInFluid(TagsFluid.WATER)) {
            this.setUnderwaterMovement();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level.getFluidState(this.blockPosition()).is((Tag) TagsFluid.LAVA)) {
            this.setDeltaMovement((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
        }

        if (!this.level.noCollision(this.getBoundingBox())) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }

        if (this.tickCount % 20 == 1) {
            this.scanForEntities();
        }

        if (this.followingPlayer != null && (this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying())) {
            this.followingPlayer = null;
        }

        if (this.followingPlayer != null) {
            Vec3D vec3d = new Vec3D(this.followingPlayer.getX() - this.getX(), this.followingPlayer.getY() + (double) this.followingPlayer.getEyeHeight() / 2.0D - this.getY(), this.followingPlayer.getZ() - this.getZ());
            double d0 = vec3d.lengthSqr();

            if (d0 < 64.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 8.0D;

                this.setDeltaMovement(this.getDeltaMovement().add(vec3d.normalize().scale(d1 * d1 * 0.1D)));
            }
        }

        this.move(EnumMoveType.SELF, this.getDeltaMovement());
        float f = 0.98F;

        if (this.onGround) {
            f = this.level.getBlockState(new BlockPosition(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.98F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply((double) f, 0.98D, (double) f));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
        }

        ++this.age;
        if (this.age >= 6000) {
            this.discard();
        }

    }

    private void scanForEntities() {
        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr((Entity) this) > 64.0D) {
            this.followingPlayer = this.level.getNearestPlayer(this, 8.0D);
        }

        if (this.level instanceof WorldServer) {
            List<EntityExperienceOrb> list = this.level.getEntities(EntityTypeTest.forClass(EntityExperienceOrb.class), this.getBoundingBox().inflate(0.5D), this::canMerge);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityExperienceOrb entityexperienceorb = (EntityExperienceOrb) iterator.next();

                this.merge(entityexperienceorb);
            }
        }

    }

    public static void award(WorldServer worldserver, Vec3D vec3d, int i) {
        while (i > 0) {
            int j = getExperienceValue(i);

            i -= j;
            if (!tryMergeToExisting(worldserver, vec3d, j)) {
                worldserver.addFreshEntity(new EntityExperienceOrb(worldserver, vec3d.x(), vec3d.y(), vec3d.z(), j));
            }
        }

    }

    private static boolean tryMergeToExisting(WorldServer worldserver, Vec3D vec3d, int i) {
        AxisAlignedBB axisalignedbb = AxisAlignedBB.ofSize(vec3d, 1.0D, 1.0D, 1.0D);
        int j = worldserver.getRandom().nextInt(40);
        List<EntityExperienceOrb> list = worldserver.getEntities(EntityTypeTest.forClass(EntityExperienceOrb.class), axisalignedbb, (entityexperienceorb) -> {
            return canMerge(entityexperienceorb, j, i);
        });

        if (!list.isEmpty()) {
            EntityExperienceOrb entityexperienceorb = (EntityExperienceOrb) list.get(0);

            ++entityexperienceorb.count;
            entityexperienceorb.age = 0;
            return true;
        } else {
            return false;
        }
    }

    private boolean canMerge(EntityExperienceOrb entityexperienceorb) {
        return entityexperienceorb != this && canMerge(entityexperienceorb, this.getId(), this.value);
    }

    private static boolean canMerge(EntityExperienceOrb entityexperienceorb, int i, int j) {
        return !entityexperienceorb.isRemoved() && (entityexperienceorb.getId() - i) % 40 == 0 && entityexperienceorb.value == j;
    }

    private void merge(EntityExperienceOrb entityexperienceorb) {
        this.count += entityexperienceorb.count;
        this.age = Math.min(this.age, entityexperienceorb.age);
        entityexperienceorb.discard();
    }

    private void setUnderwaterMovement() {
        Vec3D vec3d = this.getDeltaMovement();

        this.setDeltaMovement(vec3d.x * 0.9900000095367432D, Math.min(vec3d.y + 5.000000237487257E-4D, 0.05999999865889549D), vec3d.z * 0.9900000095367432D);
    }

    @Override
    protected void doWaterSplashEffect() {}

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            this.markHurt();
            this.health = (int) ((float) this.health - f);
            if (this.health <= 0) {
                this.discard();
            }

            return true;
        }
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putShort("Health", (short) this.health);
        nbttagcompound.putShort("Age", (short) this.age);
        nbttagcompound.putShort("Value", (short) this.value);
        nbttagcompound.putInt("Count", this.count);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.health = nbttagcompound.getShort("Health");
        this.age = nbttagcompound.getShort("Age");
        this.value = nbttagcompound.getShort("Value");
        this.count = Math.max(nbttagcompound.getInt("Count"), 1);
    }

    @Override
    public void playerTouch(EntityHuman entityhuman) {
        if (!this.level.isClientSide) {
            if (entityhuman.takeXpDelay == 0) {
                entityhuman.takeXpDelay = 2;
                entityhuman.take(this, 1);
                int i = this.repairPlayerItems(entityhuman, this.value);

                if (i > 0) {
                    entityhuman.giveExperiencePoints(i);
                }

                --this.count;
                if (this.count == 0) {
                    this.discard();
                }
            }

        }
    }

    private int repairPlayerItems(EntityHuman entityhuman, int i) {
        Entry<EnumItemSlot, ItemStack> entry = EnchantmentManager.getRandomItemWith(Enchantments.MENDING, entityhuman, ItemStack::isDamaged);

        if (entry != null) {
            ItemStack itemstack = (ItemStack) entry.getValue();
            int j = Math.min(this.xpToDurability(this.value), itemstack.getDamageValue());

            itemstack.setDamageValue(itemstack.getDamageValue() - j);
            int k = i - this.durabilityToXp(j);

            return k > 0 ? this.repairPlayerItems(entityhuman, k) : 0;
        } else {
            return i;
        }
    }

    private int durabilityToXp(int i) {
        return i / 2;
    }

    private int xpToDurability(int i) {
        return i * 2;
    }

    public int getValue() {
        return this.value;
    }

    public int getIcon() {
        return this.value >= 2477 ? 10 : (this.value >= 1237 ? 9 : (this.value >= 617 ? 8 : (this.value >= 307 ? 7 : (this.value >= 149 ? 6 : (this.value >= 73 ? 5 : (this.value >= 37 ? 4 : (this.value >= 17 ? 3 : (this.value >= 7 ? 2 : (this.value >= 3 ? 1 : 0)))))))));
    }

    public static int getExperienceValue(int i) {
        return i >= 2477 ? 2477 : (i >= 1237 ? 1237 : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1)))))))));
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntityExperienceOrb(this);
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.AMBIENT;
    }
}
