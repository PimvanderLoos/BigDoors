package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public class EntitySheep extends EntityAnimal {

    private static final DataWatcherObject<Byte> bx = DataWatcher.a(EntitySheep.class, DataWatcherRegistry.a);
    private final InventoryCrafting container = new InventoryCrafting(new Container() {
        public boolean canUse(EntityHuman entityhuman) {
            return false;
        }
    }, 2, 1);
    private static final Map<EnumColor, float[]> bz = Maps.newEnumMap(EnumColor.class);
    private int bB;
    private PathfinderGoalEatTile bC;

    private static float[] c(EnumColor enumcolor) {
        float[] afloat = enumcolor.f();
        float f = 0.75F;

        return new float[] { afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
    }

    public EntitySheep(World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
        this.container.setItem(0, new ItemStack(Items.DYE));
        this.container.setItem(1, new ItemStack(Items.DYE));
    }

    protected void r() {
        this.bC = new PathfinderGoalEatTile(this);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.1D, Items.WHEAT, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, this.bC);
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected void M() {
        this.bB = this.bC.f();
        super.M();
    }

    public void n() {
        if (this.world.isClientSide) {
            this.bB = Math.max(0, this.bB - 1);
        }

        super.n();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntitySheep.bx, Byte.valueOf((byte) 0));
    }

    @Nullable
    protected MinecraftKey J() {
        if (this.isSheared()) {
            return LootTables.P;
        } else {
            switch (this.getColor()) {
            case WHITE:
            default:
                return LootTables.Q;

            case ORANGE:
                return LootTables.R;

            case MAGENTA:
                return LootTables.S;

            case LIGHT_BLUE:
                return LootTables.T;

            case YELLOW:
                return LootTables.U;

            case LIME:
                return LootTables.V;

            case PINK:
                return LootTables.W;

            case GRAY:
                return LootTables.X;

            case SILVER:
                return LootTables.Y;

            case CYAN:
                return LootTables.Z;

            case PURPLE:
                return LootTables.aa;

            case BLUE:
                return LootTables.ab;

            case BROWN:
                return LootTables.ac;

            case GREEN:
                return LootTables.ad;

            case RED:
                return LootTables.ae;

            case BLACK:
                return LootTables.af;
            }
        }
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
            if (!this.world.isClientSide) {
                this.setSheared(true);
                int i = 1 + this.random.nextInt(3);

                for (int j = 0; j < i; ++j) {
                    EntityItem entityitem = this.a(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, this.getColor().getColorIndex()), 1.0F);

                    entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                    entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                }
            }

            itemstack.damage(1, entityhuman);
            this.a(SoundEffects.gv, 1.0F, 1.0F);
        }

        return super.a(entityhuman, enumhand);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySheep.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Sheared", this.isSheared());
        nbttagcompound.setByte("Color", (byte) this.getColor().getColorIndex());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSheared(nbttagcompound.getBoolean("Sheared"));
        this.setColor(EnumColor.fromColorIndex(nbttagcompound.getByte("Color")));
    }

    protected SoundEffect F() {
        return SoundEffects.gs;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.gu;
    }

    protected SoundEffect cf() {
        return SoundEffects.gt;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.gw, 0.15F, 1.0F);
    }

    public EnumColor getColor() {
        return EnumColor.fromColorIndex(((Byte) this.datawatcher.get(EntitySheep.bx)).byteValue() & 15);
    }

    public void setColor(EnumColor enumcolor) {
        byte b0 = ((Byte) this.datawatcher.get(EntitySheep.bx)).byteValue();

        this.datawatcher.set(EntitySheep.bx, Byte.valueOf((byte) (b0 & 240 | enumcolor.getColorIndex() & 15)));
    }

    public boolean isSheared() {
        return (((Byte) this.datawatcher.get(EntitySheep.bx)).byteValue() & 16) != 0;
    }

    public void setSheared(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntitySheep.bx)).byteValue();

        if (flag) {
            this.datawatcher.set(EntitySheep.bx, Byte.valueOf((byte) (b0 | 16)));
        } else {
            this.datawatcher.set(EntitySheep.bx, Byte.valueOf((byte) (b0 & -17)));
        }

    }

    public static EnumColor a(Random random) {
        int i = random.nextInt(100);

        return i < 5 ? EnumColor.BLACK : (i < 10 ? EnumColor.GRAY : (i < 15 ? EnumColor.SILVER : (i < 18 ? EnumColor.BROWN : (random.nextInt(500) == 0 ? EnumColor.PINK : EnumColor.WHITE))));
    }

    public EntitySheep b(EntityAgeable entityageable) {
        EntitySheep entitysheep = (EntitySheep) entityageable;
        EntitySheep entitysheep1 = new EntitySheep(this.world);

        entitysheep1.setColor(this.a((EntityAnimal) this, (EntityAnimal) entitysheep));
        return entitysheep1;
    }

    public void A() {
        this.setSheared(false);
        if (this.isBaby()) {
            this.setAge(60);
        }

    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
        this.setColor(a(this.world.random));
        return groupdataentity;
    }

    private EnumColor a(EntityAnimal entityanimal, EntityAnimal entityanimal1) {
        int i = ((EntitySheep) entityanimal).getColor().getInvColorIndex();
        int j = ((EntitySheep) entityanimal1).getColor().getInvColorIndex();

        this.container.getItem(0).setData(i);
        this.container.getItem(1).setData(j);
        ItemStack itemstack = CraftingManager.craft(this.container, ((EntitySheep) entityanimal).world);
        int k;

        if (itemstack.getItem() == Items.DYE) {
            k = itemstack.getData();
        } else {
            k = this.world.random.nextBoolean() ? i : j;
        }

        return EnumColor.fromInvColorIndex(k);
    }

    public float getHeadHeight() {
        return 0.95F * this.length;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }

    static {
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            EntitySheep.bz.put(enumcolor, c(enumcolor));
        }

        EntitySheep.bz.put(EnumColor.WHITE, new float[] { 0.9019608F, 0.9019608F, 0.9019608F});
    }
}
