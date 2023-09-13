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
        this.setPosition(d0, d1, d2);
        this.setYRot((float) (this.random.nextDouble() * 360.0D));
        this.setMot((this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
        this.value = i;
    }

    public EntityExperienceOrb(EntityTypes<? extends EntityExperienceOrb> entitytypes, World world) {
        super(entitytypes, world);
        this.health = 5;
        this.count = 1;
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public void tick() {
        super.tick();
        this.xo = this.locX();
        this.yo = this.locY();
        this.zo = this.locZ();
        if (this.a((Tag) TagsFluid.WATER)) {
            this.l();
        } else if (!this.isNoGravity()) {
            this.setMot(this.getMot().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level.getFluid(this.getChunkCoordinates()).a((Tag) TagsFluid.LAVA)) {
            this.setMot((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
        }

        if (!this.level.b(this.getBoundingBox())) {
            this.l(this.locX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.locZ());
        }

        if (this.tickCount % 20 == 1) {
            this.j();
        }

        if (this.followingPlayer != null && (this.followingPlayer.isSpectator() || this.followingPlayer.dV())) {
            this.followingPlayer = null;
        }

        if (this.followingPlayer != null) {
            Vec3D vec3d = new Vec3D(this.followingPlayer.locX() - this.locX(), this.followingPlayer.locY() + (double) this.followingPlayer.getHeadHeight() / 2.0D - this.locY(), this.followingPlayer.locZ() - this.locZ());
            double d0 = vec3d.g();

            if (d0 < 64.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 8.0D;

                this.setMot(this.getMot().e(vec3d.d().a(d1 * d1 * 0.1D)));
            }
        }

        this.move(EnumMoveType.SELF, this.getMot());
        float f = 0.98F;

        if (this.onGround) {
            f = this.level.getType(new BlockPosition(this.locX(), this.locY() - 1.0D, this.locZ())).getBlock().getFrictionFactor() * 0.98F;
        }

        this.setMot(this.getMot().d((double) f, 0.98D, (double) f));
        if (this.onGround) {
            this.setMot(this.getMot().d(1.0D, -0.9D, 1.0D));
        }

        ++this.age;
        if (this.age >= 6000) {
            this.die();
        }

    }

    private void j() {
        if (this.followingPlayer == null || this.followingPlayer.f((Entity) this) > 64.0D) {
            this.followingPlayer = this.level.findNearbyPlayer(this, 8.0D);
        }

        if (this.level instanceof WorldServer) {
            List<EntityExperienceOrb> list = this.level.a(EntityTypeTest.a(EntityExperienceOrb.class), this.getBoundingBox().g(0.5D), this::a);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityExperienceOrb entityexperienceorb = (EntityExperienceOrb) iterator.next();

                this.b(entityexperienceorb);
            }
        }

    }

    public static void a(WorldServer worldserver, Vec3D vec3d, int i) {
        while (i > 0) {
            int j = getOrbValue(i);

            i -= j;
            if (!b(worldserver, vec3d, j)) {
                worldserver.addEntity(new EntityExperienceOrb(worldserver, vec3d.getX(), vec3d.getY(), vec3d.getZ(), j));
            }
        }

    }

    private static boolean b(WorldServer worldserver, Vec3D vec3d, int i) {
        AxisAlignedBB axisalignedbb = AxisAlignedBB.a(vec3d, 1.0D, 1.0D, 1.0D);
        int j = worldserver.getRandom().nextInt(40);
        List<EntityExperienceOrb> list = worldserver.a(EntityTypeTest.a(EntityExperienceOrb.class), axisalignedbb, (entityexperienceorb) -> {
            return a(entityexperienceorb, j, i);
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

    private boolean a(EntityExperienceOrb entityexperienceorb) {
        return entityexperienceorb != this && a(entityexperienceorb, this.getId(), this.value);
    }

    private static boolean a(EntityExperienceOrb entityexperienceorb, int i, int j) {
        return !entityexperienceorb.isRemoved() && (entityexperienceorb.getId() - i) % 40 == 0 && entityexperienceorb.value == j;
    }

    private void b(EntityExperienceOrb entityexperienceorb) {
        this.count += entityexperienceorb.count;
        this.age = Math.min(this.age, entityexperienceorb.age);
        entityexperienceorb.die();
    }

    private void l() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x * 0.9900000095367432D, Math.min(vec3d.y + 5.000000237487257E-4D, 0.05999999865889549D), vec3d.z * 0.9900000095367432D);
    }

    @Override
    protected void aT() {}

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.velocityChanged();
            this.health = (int) ((float) this.health - f);
            if (this.health <= 0) {
                this.die();
            }

            return true;
        }
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) this.health);
        nbttagcompound.setShort("Age", (short) this.age);
        nbttagcompound.setShort("Value", (short) this.value);
        nbttagcompound.setInt("Count", this.count);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        this.health = nbttagcompound.getShort("Health");
        this.age = nbttagcompound.getShort("Age");
        this.value = nbttagcompound.getShort("Value");
        this.count = Math.max(nbttagcompound.getInt("Count"), 1);
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (!this.level.isClientSide) {
            if (entityhuman.takeXpDelay == 0) {
                entityhuman.takeXpDelay = 2;
                entityhuman.receive(this, 1);
                int i = this.a(entityhuman, this.value);

                if (i > 0) {
                    entityhuman.giveExp(i);
                }

                --this.count;
                if (this.count == 0) {
                    this.die();
                }
            }

        }
    }

    private int a(EntityHuman entityhuman, int i) {
        Entry<EnumItemSlot, ItemStack> entry = EnchantmentManager.a(Enchantments.MENDING, (EntityLiving) entityhuman, ItemStack::g);

        if (entry != null) {
            ItemStack itemstack = (ItemStack) entry.getValue();
            int j = Math.min(this.c(this.value), itemstack.getDamage());

            itemstack.setDamage(itemstack.getDamage() - j);
            int k = i - this.b(j);

            return k > 0 ? this.a(entityhuman, k) : 0;
        } else {
            return i;
        }
    }

    private int b(int i) {
        return i / 2;
    }

    private int c(int i) {
        return i * 2;
    }

    public int h() {
        return this.value;
    }

    public int i() {
        return this.value >= 2477 ? 10 : (this.value >= 1237 ? 9 : (this.value >= 617 ? 8 : (this.value >= 307 ? 7 : (this.value >= 149 ? 6 : (this.value >= 73 ? 5 : (this.value >= 37 ? 4 : (this.value >= 17 ? 3 : (this.value >= 7 ? 2 : (this.value >= 3 ? 1 : 0)))))))));
    }

    public static int getOrbValue(int i) {
        return i >= 2477 ? 2477 : (i >= 1237 ? 1237 : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1)))))))));
    }

    @Override
    public boolean ca() {
        return false;
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntityExperienceOrb(this);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.AMBIENT;
    }
}
