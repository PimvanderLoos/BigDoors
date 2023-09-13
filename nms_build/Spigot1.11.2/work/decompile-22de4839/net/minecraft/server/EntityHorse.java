package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityHorse extends EntityHorseAbstract {

    private static final UUID bG = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataWatcherObject<Integer> bH = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bI = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final String[] bJ = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] bK = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] bL = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] bM = new String[] { "", "wo_", "wmo", "wdo", "bdo"};
    private String bN;
    private final String[] bO = new String[3];

    public EntityHorse(World world) {
        super(world);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityHorse.bH, Integer.valueOf(0));
        this.datawatcher.register(EntityHorse.bI, Integer.valueOf(EnumHorseArmor.NONE.a()));
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

        this.dy();
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityHorse.bH, Integer.valueOf(i));
        this.dM();
    }

    public int getVariant() {
        return ((Integer) this.datawatcher.get(EntityHorse.bH)).intValue();
    }

    private void dM() {
        this.bN = null;
    }

    protected void dy() {
        super.dy();
        this.g(this.inventoryChest.getItem(1));
    }

    public void g(ItemStack itemstack) {
        EnumHorseArmor enumhorsearmor = EnumHorseArmor.a(itemstack);

        this.datawatcher.set(EntityHorse.bI, Integer.valueOf(enumhorsearmor.a()));
        this.dM();
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.g).b(EntityHorse.bG);
            int i = enumhorsearmor.c();

            if (i != 0) {
                this.getAttributeInstance(GenericAttributes.g).b((new AttributeModifier(EntityHorse.bG, "Horse armor bonus", (double) i, 0)).a(false));
            }
        }

    }

    public EnumHorseArmor dL() {
        return EnumHorseArmor.a(((Integer) this.datawatcher.get(EntityHorse.bI)).intValue());
    }

    public void a(IInventory iinventory) {
        EnumHorseArmor enumhorsearmor = this.dL();

        super.a(iinventory);
        EnumHorseArmor enumhorsearmor1 = this.dL();

        if (this.ticksLived > 20 && enumhorsearmor != enumhorsearmor1 && enumhorsearmor1 != EnumHorseArmor.NONE) {
            this.a(SoundEffects.cB, 0.5F, 1.0F);
        }

    }

    protected void a(SoundEffectType soundeffecttype) {
        super.a(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.a(SoundEffects.cC, soundeffecttype.a() * 0.6F, soundeffecttype.b());
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) this.dH());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.dJ());
        this.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(this.dI());
    }

    public void A_() {
        super.A_();
        if (this.world.isClientSide && this.datawatcher.a()) {
            this.datawatcher.e();
            this.dM();
        }

    }

    protected SoundEffect G() {
        super.G();
        return SoundEffects.cz;
    }

    protected SoundEffect bX() {
        super.bX();
        return SoundEffects.cD;
    }

    protected SoundEffect bW() {
        super.bW();
        return SoundEffects.cG;
    }

    protected SoundEffect dj() {
        super.dj();
        return SoundEffects.cA;
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
                    this.f(entityhuman);
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
                    this.dF();
                    return true;
                }

                boolean flag1 = EnumHorseArmor.a(itemstack) != EnumHorseArmor.NONE;
                boolean flag2 = !this.isBaby() && !this.dB() && itemstack.getItem() == Items.SADDLE;

                if (flag1 || flag2) {
                    this.f(entityhuman);
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
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.dG() && ((EntityHorseAbstract) entityanimal).dG());
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

    public boolean dK() {
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
