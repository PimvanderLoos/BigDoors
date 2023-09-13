package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityArmorStand extends EntityLiving {

    private static final Vector3f br = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3f bs = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3f bt = new Vector3f(-10.0F, 0.0F, -10.0F);
    private static final Vector3f bu = new Vector3f(-15.0F, 0.0F, 10.0F);
    private static final Vector3f bv = new Vector3f(-1.0F, 0.0F, -1.0F);
    private static final Vector3f bw = new Vector3f(1.0F, 0.0F, 1.0F);
    public static final DataWatcherObject<Byte> a = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Vector3f> b = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.i);
    public static final DataWatcherObject<Vector3f> c = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.i);
    public static final DataWatcherObject<Vector3f> d = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.i);
    public static final DataWatcherObject<Vector3f> e = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.i);
    public static final DataWatcherObject<Vector3f> f = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.i);
    public static final DataWatcherObject<Vector3f> g = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.i);
    private static final Predicate<Entity> bx = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof EntityMinecartAbstract && ((EntityMinecartAbstract) entity).v() == EntityMinecartAbstract.EnumMinecartType.RIDEABLE;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    private final NonNullList<ItemStack> by;
    private final NonNullList<ItemStack> bz;
    private boolean bA;
    public long h;
    private int bB;
    private boolean bC;
    public Vector3f headPose;
    public Vector3f bodyPose;
    public Vector3f leftArmPose;
    public Vector3f rightArmPose;
    public Vector3f leftLegPose;
    public Vector3f rightLegPose;

    public EntityArmorStand(World world) {
        super(world);
        this.by = NonNullList.a(2, ItemStack.a);
        this.bz = NonNullList.a(4, ItemStack.a);
        this.headPose = EntityArmorStand.br;
        this.bodyPose = EntityArmorStand.bs;
        this.leftArmPose = EntityArmorStand.bt;
        this.rightArmPose = EntityArmorStand.bu;
        this.leftLegPose = EntityArmorStand.bv;
        this.rightLegPose = EntityArmorStand.bw;
        this.noclip = this.isNoGravity();
        this.setSize(0.5F, 1.975F);
    }

    public EntityArmorStand(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    public final void setSize(float f, float f1) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;
        float f2 = this.isMarker() ? 0.0F : (this.isBaby() ? 0.5F : 1.0F);

        super.setSize(f * f2, f1 * f2);
        this.setPosition(d0, d1, d2);
    }

    public boolean cC() {
        return super.cC() && !this.isNoGravity();
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityArmorStand.a, Byte.valueOf((byte) 0));
        this.datawatcher.register(EntityArmorStand.b, EntityArmorStand.br);
        this.datawatcher.register(EntityArmorStand.c, EntityArmorStand.bs);
        this.datawatcher.register(EntityArmorStand.d, EntityArmorStand.bt);
        this.datawatcher.register(EntityArmorStand.e, EntityArmorStand.bu);
        this.datawatcher.register(EntityArmorStand.f, EntityArmorStand.bv);
        this.datawatcher.register(EntityArmorStand.g, EntityArmorStand.bw);
    }

    public Iterable<ItemStack> aO() {
        return this.by;
    }

    public Iterable<ItemStack> getArmorItems() {
        return this.bz;
    }

    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        switch (enumitemslot.a()) {
        case HAND:
            return (ItemStack) this.by.get(enumitemslot.b());

        case ARMOR:
            return (ItemStack) this.bz.get(enumitemslot.b());

        default:
            return ItemStack.a;
        }
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        switch (enumitemslot.a()) {
        case HAND:
            this.a_(itemstack);
            this.by.set(enumitemslot.b(), itemstack);
            break;

        case ARMOR:
            this.a_(itemstack);
            this.bz.set(enumitemslot.b(), itemstack);
        }

    }

    public boolean c(int i, ItemStack itemstack) {
        EnumItemSlot enumitemslot;

        if (i == 98) {
            enumitemslot = EnumItemSlot.MAINHAND;
        } else if (i == 99) {
            enumitemslot = EnumItemSlot.OFFHAND;
        } else if (i == 100 + EnumItemSlot.HEAD.b()) {
            enumitemslot = EnumItemSlot.HEAD;
        } else if (i == 100 + EnumItemSlot.CHEST.b()) {
            enumitemslot = EnumItemSlot.CHEST;
        } else if (i == 100 + EnumItemSlot.LEGS.b()) {
            enumitemslot = EnumItemSlot.LEGS;
        } else {
            if (i != 100 + EnumItemSlot.FEET.b()) {
                return false;
            }

            enumitemslot = EnumItemSlot.FEET;
        }

        if (!itemstack.isEmpty() && !EntityInsentient.b(enumitemslot, itemstack) && enumitemslot != EnumItemSlot.HEAD) {
            return false;
        } else {
            this.setSlot(enumitemslot, itemstack);
            return true;
        }
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItemList(EntityArmorStand.class, new String[] { "ArmorItems", "HandItems"})));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.bz.iterator(); iterator.hasNext(); nbttaglist.add(nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.set("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.by.iterator(); iterator1.hasNext(); nbttaglist1.add(nbttagcompound2)) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();

            nbttagcompound2 = new NBTTagCompound();
            if (!itemstack1.isEmpty()) {
                itemstack1.save(nbttagcompound2);
            }
        }

        nbttagcompound.set("HandItems", nbttaglist1);
        nbttagcompound.setBoolean("Invisible", this.isInvisible());
        nbttagcompound.setBoolean("Small", this.isSmall());
        nbttagcompound.setBoolean("ShowArms", this.hasArms());
        nbttagcompound.setInt("DisabledSlots", this.bB);
        nbttagcompound.setBoolean("NoBasePlate", this.hasBasePlate());
        if (this.isMarker()) {
            nbttagcompound.setBoolean("Marker", this.isMarker());
        }

        nbttagcompound.set("Pose", this.C());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.hasKeyOfType("ArmorItems", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorItems", 10);

            for (i = 0; i < this.bz.size(); ++i) {
                this.bz.set(i, new ItemStack(nbttaglist.get(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.by.size(); ++i) {
                this.by.set(i, new ItemStack(nbttaglist.get(i)));
            }
        }

        this.setInvisible(nbttagcompound.getBoolean("Invisible"));
        this.setSmall(nbttagcompound.getBoolean("Small"));
        this.setArms(nbttagcompound.getBoolean("ShowArms"));
        this.bB = nbttagcompound.getInt("DisabledSlots");
        this.setBasePlate(nbttagcompound.getBoolean("NoBasePlate"));
        this.setMarker(nbttagcompound.getBoolean("Marker"));
        this.bC = !this.isMarker();
        this.noclip = this.isNoGravity();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Pose");

        this.g(nbttagcompound1);
    }

    private void g(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.getList("Head", 5);

        this.setHeadPose(nbttaglist.isEmpty() ? EntityArmorStand.br : new Vector3f(nbttaglist));
        NBTTagList nbttaglist1 = nbttagcompound.getList("Body", 5);

        this.setBodyPose(nbttaglist1.isEmpty() ? EntityArmorStand.bs : new Vector3f(nbttaglist1));
        NBTTagList nbttaglist2 = nbttagcompound.getList("LeftArm", 5);

        this.setLeftArmPose(nbttaglist2.isEmpty() ? EntityArmorStand.bt : new Vector3f(nbttaglist2));
        NBTTagList nbttaglist3 = nbttagcompound.getList("RightArm", 5);

        this.setRightArmPose(nbttaglist3.isEmpty() ? EntityArmorStand.bu : new Vector3f(nbttaglist3));
        NBTTagList nbttaglist4 = nbttagcompound.getList("LeftLeg", 5);

        this.setLeftLegPose(nbttaglist4.isEmpty() ? EntityArmorStand.bv : new Vector3f(nbttaglist4));
        NBTTagList nbttaglist5 = nbttagcompound.getList("RightLeg", 5);

        this.setRightLegPose(nbttaglist5.isEmpty() ? EntityArmorStand.bw : new Vector3f(nbttaglist5));
    }

    private NBTTagCompound C() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (!EntityArmorStand.br.equals(this.headPose)) {
            nbttagcompound.set("Head", this.headPose.a());
        }

        if (!EntityArmorStand.bs.equals(this.bodyPose)) {
            nbttagcompound.set("Body", this.bodyPose.a());
        }

        if (!EntityArmorStand.bt.equals(this.leftArmPose)) {
            nbttagcompound.set("LeftArm", this.leftArmPose.a());
        }

        if (!EntityArmorStand.bu.equals(this.rightArmPose)) {
            nbttagcompound.set("RightArm", this.rightArmPose.a());
        }

        if (!EntityArmorStand.bv.equals(this.leftLegPose)) {
            nbttagcompound.set("LeftLeg", this.leftLegPose.a());
        }

        if (!EntityArmorStand.bw.equals(this.rightLegPose)) {
            nbttagcompound.set("RightLeg", this.rightLegPose.a());
        }

        return nbttagcompound;
    }

    public boolean isCollidable() {
        return false;
    }

    protected void C(Entity entity) {}

    protected void cB() {
        List list = this.world.getEntities(this, this.getBoundingBox(), EntityArmorStand.bx);

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (this.h(entity) <= 0.2D) {
                entity.collide(this);
            }
        }

    }

    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isMarker() && itemstack.getItem() != Items.NAME_TAG) {
            if (!this.world.isClientSide && !entityhuman.isSpectator()) {
                EnumItemSlot enumitemslot = EntityInsentient.d(itemstack);

                if (itemstack.isEmpty()) {
                    EnumItemSlot enumitemslot1 = this.a(vec3d);
                    EnumItemSlot enumitemslot2 = this.c(enumitemslot1) ? enumitemslot : enumitemslot1;

                    if (this.a(enumitemslot2)) {
                        this.a(entityhuman, enumitemslot2, itemstack, enumhand);
                    }
                } else {
                    if (this.c(enumitemslot)) {
                        return EnumInteractionResult.FAIL;
                    }

                    if (enumitemslot.a() == EnumItemSlot.Function.HAND && !this.hasArms()) {
                        return EnumInteractionResult.FAIL;
                    }

                    this.a(entityhuman, enumitemslot, itemstack, enumhand);
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.SUCCESS;
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    protected EnumItemSlot a(Vec3D vec3d) {
        EnumItemSlot enumitemslot = EnumItemSlot.MAINHAND;
        boolean flag = this.isSmall();
        double d0 = flag ? vec3d.y * 2.0D : vec3d.y;
        EnumItemSlot enumitemslot1 = EnumItemSlot.FEET;

        if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.a(enumitemslot1)) {
            enumitemslot = EnumItemSlot.FEET;
        } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.a(EnumItemSlot.CHEST)) {
            enumitemslot = EnumItemSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.a(EnumItemSlot.LEGS)) {
            enumitemslot = EnumItemSlot.LEGS;
        } else if (d0 >= 1.6D && this.a(EnumItemSlot.HEAD)) {
            enumitemslot = EnumItemSlot.HEAD;
        }

        return enumitemslot;
    }

    private boolean c(EnumItemSlot enumitemslot) {
        return (this.bB & 1 << enumitemslot.c()) != 0;
    }

    private void a(EntityHuman entityhuman, EnumItemSlot enumitemslot, ItemStack itemstack, EnumHand enumhand) {
        ItemStack itemstack1 = this.getEquipment(enumitemslot);

        if (itemstack1.isEmpty() || (this.bB & 1 << enumitemslot.c() + 8) == 0) {
            if (!itemstack1.isEmpty() || (this.bB & 1 << enumitemslot.c() + 16) == 0) {
                ItemStack itemstack2;

                if (entityhuman.abilities.canInstantlyBuild && itemstack1.isEmpty() && !itemstack.isEmpty()) {
                    itemstack2 = itemstack.cloneItemStack();
                    itemstack2.setCount(1);
                    this.setSlot(enumitemslot, itemstack2);
                } else if (!itemstack.isEmpty() && itemstack.getCount() > 1) {
                    if (itemstack1.isEmpty()) {
                        itemstack2 = itemstack.cloneItemStack();
                        itemstack2.setCount(1);
                        this.setSlot(enumitemslot, itemstack2);
                        itemstack.subtract(1);
                    }
                } else {
                    this.setSlot(enumitemslot, itemstack);
                    entityhuman.a(enumhand, itemstack1);
                }
            }
        }
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.world.isClientSide && !this.dead) {
            if (DamageSource.OUT_OF_WORLD.equals(damagesource)) {
                this.die();
                return false;
            } else if (!this.isInvulnerable(damagesource) && !this.bA && !this.isMarker()) {
                if (damagesource.isExplosion()) {
                    this.F();
                    this.die();
                    return false;
                } else if (DamageSource.FIRE.equals(damagesource)) {
                    if (this.isBurning()) {
                        this.a(0.15F);
                    } else {
                        this.setOnFire(5);
                    }

                    return false;
                } else if (DamageSource.BURN.equals(damagesource) && this.getHealth() > 0.5F) {
                    this.a(4.0F);
                    return false;
                } else {
                    boolean flag = "arrow".equals(damagesource.p());
                    boolean flag1 = "player".equals(damagesource.p());

                    if (!flag1 && !flag) {
                        return false;
                    } else {
                        if (damagesource.i() instanceof EntityArrow) {
                            damagesource.i().die();
                        }

                        if (damagesource.getEntity() instanceof EntityHuman && !((EntityHuman) damagesource.getEntity()).abilities.mayBuild) {
                            return false;
                        } else if (damagesource.u()) {
                            this.H();
                            this.D();
                            this.die();
                            return false;
                        } else {
                            long i = this.world.getTime();

                            if (i - this.h > 5L && !flag) {
                                this.world.broadcastEntityEffect(this, (byte) 32);
                                this.h = i;
                            } else {
                                this.E();
                                this.D();
                                this.die();
                            }

                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void D() {
        if (this.world instanceof WorldServer) {
            ((WorldServer) this.world).a(EnumParticle.BLOCK_DUST, this.locX, this.locY + (double) this.length / 1.5D, this.locZ, 10, (double) (this.width / 4.0F), (double) (this.length / 4.0F), (double) (this.width / 4.0F), 0.05D, new int[] { Block.getCombinedId(Blocks.PLANKS.getBlockData())});
        }

    }

    private void a(float f) {
        float f1 = this.getHealth();

        f1 -= f;
        if (f1 <= 0.5F) {
            this.F();
            this.die();
        } else {
            this.setHealth(f1);
        }

    }

    private void E() {
        Block.a(this.world, new BlockPosition(this), new ItemStack(Items.ARMOR_STAND));
        this.F();
    }

    private void F() {
        this.H();

        int i;
        ItemStack itemstack;

        for (i = 0; i < this.by.size(); ++i) {
            itemstack = (ItemStack) this.by.get(i);
            if (!itemstack.isEmpty()) {
                Block.a(this.world, (new BlockPosition(this)).up(), itemstack);
                this.by.set(i, ItemStack.a);
            }
        }

        for (i = 0; i < this.bz.size(); ++i) {
            itemstack = (ItemStack) this.bz.get(i);
            if (!itemstack.isEmpty()) {
                Block.a(this.world, (new BlockPosition(this)).up(), itemstack);
                this.bz.set(i, ItemStack.a);
            }
        }

    }

    private void H() {
        this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.j, this.bK(), 1.0F, 1.0F);
    }

    protected float g(float f, float f1) {
        this.aO = this.lastYaw;
        this.aN = this.yaw;
        return 0.0F;
    }

    public float getHeadHeight() {
        return this.isBaby() ? this.length * 0.5F : this.length * 0.9F;
    }

    public double aF() {
        return this.isMarker() ? 0.0D : 0.10000000149011612D;
    }

    public void a(float f, float f1, float f2) {
        if (!this.isNoGravity()) {
            super.a(f, f1, f2);
        }
    }

    public void h(float f) {
        this.aO = this.lastYaw = f;
        this.aQ = this.aP = f;
    }

    public void setHeadRotation(float f) {
        this.aO = this.lastYaw = f;
        this.aQ = this.aP = f;
    }

    public void B_() {
        super.B_();
        Vector3f vector3f = (Vector3f) this.datawatcher.get(EntityArmorStand.b);

        if (!this.headPose.equals(vector3f)) {
            this.setHeadPose(vector3f);
        }

        Vector3f vector3f1 = (Vector3f) this.datawatcher.get(EntityArmorStand.c);

        if (!this.bodyPose.equals(vector3f1)) {
            this.setBodyPose(vector3f1);
        }

        Vector3f vector3f2 = (Vector3f) this.datawatcher.get(EntityArmorStand.d);

        if (!this.leftArmPose.equals(vector3f2)) {
            this.setLeftArmPose(vector3f2);
        }

        Vector3f vector3f3 = (Vector3f) this.datawatcher.get(EntityArmorStand.e);

        if (!this.rightArmPose.equals(vector3f3)) {
            this.setRightArmPose(vector3f3);
        }

        Vector3f vector3f4 = (Vector3f) this.datawatcher.get(EntityArmorStand.f);

        if (!this.leftLegPose.equals(vector3f4)) {
            this.setLeftLegPose(vector3f4);
        }

        Vector3f vector3f5 = (Vector3f) this.datawatcher.get(EntityArmorStand.g);

        if (!this.rightLegPose.equals(vector3f5)) {
            this.setRightLegPose(vector3f5);
        }

        boolean flag = this.isMarker();

        if (this.bC != flag) {
            this.a(flag);
            this.i = !flag;
            this.bC = flag;
        }

    }

    private void a(boolean flag) {
        if (flag) {
            this.setSize(0.0F, 0.0F);
        } else {
            this.setSize(0.5F, 1.975F);
        }

    }

    protected void G() {
        this.setInvisible(this.bA);
    }

    public void setInvisible(boolean flag) {
        this.bA = flag;
        super.setInvisible(flag);
    }

    public boolean isBaby() {
        return this.isSmall();
    }

    public void killEntity() {
        this.die();
    }

    public boolean bB() {
        return this.isInvisible();
    }

    public EnumPistonReaction getPushReaction() {
        return this.isMarker() ? EnumPistonReaction.IGNORE : super.getPushReaction();
    }

    public void setSmall(boolean flag) {
        this.datawatcher.set(EntityArmorStand.a, Byte.valueOf(this.a(((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue(), 1, flag)));
        this.setSize(0.5F, 1.975F);
    }

    public boolean isSmall() {
        return (((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue() & 1) != 0;
    }

    public void setArms(boolean flag) {
        this.datawatcher.set(EntityArmorStand.a, Byte.valueOf(this.a(((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue(), 4, flag)));
    }

    public boolean hasArms() {
        return (((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue() & 4) != 0;
    }

    public void setBasePlate(boolean flag) {
        this.datawatcher.set(EntityArmorStand.a, Byte.valueOf(this.a(((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue(), 8, flag)));
    }

    public boolean hasBasePlate() {
        return (((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue() & 8) != 0;
    }

    public void setMarker(boolean flag) {
        this.datawatcher.set(EntityArmorStand.a, Byte.valueOf(this.a(((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue(), 16, flag)));
        this.setSize(0.5F, 1.975F);
    }

    public boolean isMarker() {
        return (((Byte) this.datawatcher.get(EntityArmorStand.a)).byteValue() & 16) != 0;
    }

    private byte a(byte b0, int i, boolean flag) {
        if (flag) {
            b0 = (byte) (b0 | i);
        } else {
            b0 = (byte) (b0 & ~i);
        }

        return b0;
    }

    public void setHeadPose(Vector3f vector3f) {
        this.headPose = vector3f;
        this.datawatcher.set(EntityArmorStand.b, vector3f);
    }

    public void setBodyPose(Vector3f vector3f) {
        this.bodyPose = vector3f;
        this.datawatcher.set(EntityArmorStand.c, vector3f);
    }

    public void setLeftArmPose(Vector3f vector3f) {
        this.leftArmPose = vector3f;
        this.datawatcher.set(EntityArmorStand.d, vector3f);
    }

    public void setRightArmPose(Vector3f vector3f) {
        this.rightArmPose = vector3f;
        this.datawatcher.set(EntityArmorStand.e, vector3f);
    }

    public void setLeftLegPose(Vector3f vector3f) {
        this.leftLegPose = vector3f;
        this.datawatcher.set(EntityArmorStand.f, vector3f);
    }

    public void setRightLegPose(Vector3f vector3f) {
        this.rightLegPose = vector3f;
        this.datawatcher.set(EntityArmorStand.g, vector3f);
    }

    public Vector3f u() {
        return this.headPose;
    }

    public Vector3f w() {
        return this.bodyPose;
    }

    public boolean isInteractable() {
        return super.isInteractable() && !this.isMarker();
    }

    public EnumMainHand getMainHand() {
        return EnumMainHand.RIGHT;
    }

    protected SoundEffect e(int i) {
        return SoundEffects.k;
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.l;
    }

    @Nullable
    protected SoundEffect cf() {
        return SoundEffects.j;
    }

    public void onLightningStrike(EntityLightning entitylightning) {}

    public boolean cR() {
        return false;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityArmorStand.a.equals(datawatcherobject)) {
            this.setSize(0.5F, 1.975F);
        }

        super.a(datawatcherobject);
    }

    public boolean cS() {
        return false;
    }
}
