package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityHorse extends EntityHorseAbstract {

    private static final UUID bH = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataWatcherObject<Integer> bI = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bJ = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final String[] bK = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] bL = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] bM = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] bN = new String[] { "", "wo_", "wmo", "wdo", "bdo"};
    private String bO;
    private final String[] bP = new String[3];

    public EntityHorse(World world) {
        super(world);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityHorse.bI, Integer.valueOf(0));
        this.datawatcher.register(EntityHorse.bJ, Integer.valueOf(EnumHorseArmor.NONE.a()));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityHorseAbstract.c(dataconvertermanager, EntityHorse.class);
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItem(EntityHorse.class, new String[] { "ArmorItem"})));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
        if (!this.inventoryChest.getItem(1).isEmpty()) {
            nbttagcompound.set("ArmorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            ItemStack itemstack = new ItemStack(nbttagcompound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && EnumHorseArmor.b(itemstack.getItem())) {
                this.inventoryChest.setItem(1, itemstack);
            }
        }

        this.dD();
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityHorse.bI, Integer.valueOf(i));
        this.dQ();
    }

    public int getVariant() {
        return ((Integer) this.datawatcher.get(EntityHorse.bI)).intValue();
    }

    private void dQ() {
        this.bO = null;
    }

    protected void dD() {
        super.dD();
        this.g(this.inventoryChest.getItem(1));
    }

    public void g(ItemStack itemstack) {
        EnumHorseArmor enumhorsearmor = EnumHorseArmor.a(itemstack);

        this.datawatcher.set(EntityHorse.bJ, Integer.valueOf(enumhorsearmor.a()));
        this.dQ();
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.h).b(EntityHorse.bH);
            int i = enumhorsearmor.c();

            if (i != 0) {
                this.getAttributeInstance(GenericAttributes.h).b((new AttributeModifier(EntityHorse.bH, "Horse armor bonus", (double) i, 0)).a(false));
            }
        }

    }

    public EnumHorseArmor dt() {
        return EnumHorseArmor.a(((Integer) this.datawatcher.get(EntityHorse.bJ)).intValue());
    }

    public void a(IInventory iinventory) {
        EnumHorseArmor enumhorsearmor = this.dt();

        super.a(iinventory);
        EnumHorseArmor enumhorsearmor1 = this.dt();

        if (this.ticksLived > 20 && enumhorsearmor != enumhorsearmor1 && enumhorsearmor1 != EnumHorseArmor.NONE) {
            this.a(SoundEffects.cH, 0.5F, 1.0F);
        }

    }

    protected void a(SoundEffectType soundeffecttype) {
        super.a(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.a(SoundEffects.cI, soundeffecttype.a() * 0.6F, soundeffecttype.b());
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) this.dM());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.dO());
        this.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(this.dN());
    }

    public void B_() {
        super.B_();
        if (this.world.isClientSide && this.datawatcher.a()) {
            this.datawatcher.e();
            this.dQ();
        }

    }

    protected SoundEffect F() {
        super.F();
        return SoundEffects.cF;
    }

    protected SoundEffect cf() {
        super.cf();
        return SoundEffects.cJ;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.cM;
    }

    protected SoundEffect do_() {
        super.do_();
        return SoundEffects.cG;
    }

    protected MinecraftKey J() {
        return LootTables.G;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() == Items.SPAWN_EGG) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!this.isBaby()) {
                if (this.isTamed() && entityhuman.isSneaking()) {
                    this.c(entityhuman);
                    return true;
                }

                if (this.isVehicle()) {
                    return super.a(entityhuman, enumhand);
                }
            }

            if (flag) {
                if (this.b(entityhuman, itemstack)) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return true;
                }

                if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                    return true;
                }

                if (!this.isTamed()) {
                    this.dK();
                    return true;
                }

                boolean flag1 = EnumHorseArmor.a(itemstack) != EnumHorseArmor.NONE;
                boolean flag2 = !this.isBaby() && !this.dG() && itemstack.getItem() == Items.SADDLE;

                if (flag1 || flag2) {
                    this.c(entityhuman);
                    return true;
                }
            }

            if (this.isBaby()) {
                return super.a(entityhuman, enumhand);
            } else {
                this.g(entityhuman);
                return true;
            }
        }
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.dL() && ((EntityHorseAbstract) entityanimal).dL());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        Object object;

        if (entityageable instanceof EntityHorseDonkey) {
            object = new EntityHorseMule(this.world);
        } else {
            EntityHorse entityhorse = (EntityHorse) entityageable;

            object = new EntityHorse(this.world);
            int i = this.random.nextInt(9);
            int j;

            if (i < 4) {
                j = this.getVariant() & 255;
            } else if (i < 8) {
                j = entityhorse.getVariant() & 255;
            } else {
                j = this.random.nextInt(7);
            }

            int k = this.random.nextInt(5);

            if (k < 2) {
                j |= this.getVariant() & '\uff00';
            } else if (k < 4) {
                j |= entityhorse.getVariant() & '\uff00';
            } else {
                j |= this.random.nextInt(5) << 8 & '\uff00';
            }

            ((EntityHorse) object).setVariant(j);
        }

        this.a(entityageable, (EntityHorseAbstract) object);
        return (EntityAgeable) object;
    }

    public boolean dP() {
        return true;
    }

    public boolean f(ItemStack itemstack) {
        return EnumHorseArmor.b(itemstack.getItem());
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity);
        int i;

        if (object instanceof EntityHorse.a) {
            i = ((EntityHorse.a) object).a;
        } else {
            i = this.random.nextInt(7);
            object = new EntityHorse.a(i);
        }

        this.setVariant(i | this.random.nextInt(5) << 8);
        return (GroupDataEntity) object;
    }

    public static class a implements GroupDataEntity {

        public int a;

        public a(int i) {
            this.a = i;
        }
    }
}
