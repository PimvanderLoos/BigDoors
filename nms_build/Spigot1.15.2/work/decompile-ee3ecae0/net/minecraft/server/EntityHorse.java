package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityHorse extends EntityHorseAbstract {

    private static final UUID bF = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataWatcherObject<Integer> bG = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final String[] bH = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] bI = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] bJ = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] bK = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
    @Nullable
    private String bL;
    private final String[] bM = new String[2];

    public EntityHorse(EntityTypes<? extends EntityHorse> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityHorse.bG, 0);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
        if (!this.inventoryChest.getItem(1).isEmpty()) {
            nbttagcompound.set("ArmorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

    }

    public ItemStack eq() {
        return this.getEquipment(EnumItemSlot.CHEST);
    }

    private void k(ItemStack itemstack) {
        this.setSlot(EnumItemSlot.CHEST, itemstack);
        this.a(EnumItemSlot.CHEST, 0.0F);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && this.j(itemstack)) {
                this.inventoryChest.setItem(1, itemstack);
            }
        }

        this.eI();
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityHorse.bG, i);
        this.eX();
    }

    public int getVariant() {
        return (Integer) this.datawatcher.get(EntityHorse.bG);
    }

    private void eX() {
        this.bL = null;
    }

    @Override
    protected void eI() {
        super.eI();
        this.l(this.inventoryChest.getItem(1));
        this.a(EnumItemSlot.CHEST, 0.0F);
    }

    private void l(ItemStack itemstack) {
        this.k(itemstack);
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.ARMOR).b(EntityHorse.bF);
            if (this.j(itemstack)) {
                int i = ((ItemHorseArmor) itemstack.getItem()).g();

                if (i != 0) {
                    this.getAttributeInstance(GenericAttributes.ARMOR).addModifier((new AttributeModifier(EntityHorse.bF, "Horse armor bonus", (double) i, AttributeModifier.Operation.ADDITION)).a(false));
                }
            }
        }

    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.eq();

        super.a(iinventory);
        ItemStack itemstack1 = this.eq();

        if (this.ticksLived > 20 && this.j(itemstack1) && itemstack != itemstack1) {
            this.a(SoundEffects.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    @Override
    protected void a(SoundEffectType soundeffecttype) {
        super.a(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.a(SoundEffects.ENTITY_HORSE_BREATHE, soundeffecttype.a() * 0.6F, soundeffecttype.b());
        }

    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) this.eS());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.eU());
        this.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(this.eT());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClientSide && this.datawatcher.a()) {
            this.datawatcher.e();
            this.eX();
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return SoundEffects.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.ENTITY_HORSE_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEffect getSoundAngry() {
        super.getSoundAngry();
        return SoundEffects.ENTITY_HORSE_ANGRY;
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!this.isBaby()) {
                if (this.isTamed() && entityhuman.dT()) {
                    this.e(entityhuman);
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
                    this.eP();
                    return true;
                }

                boolean flag1 = !this.isBaby() && !this.eL() && itemstack.getItem() == Items.SADDLE;

                if (this.j(itemstack) || flag1) {
                    this.e(entityhuman);
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

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.eR() && ((EntityHorseAbstract) entityanimal).eR());
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        EntityHorseAbstract entityhorseabstract;

        if (entityageable instanceof EntityHorseDonkey) {
            entityhorseabstract = (EntityHorseAbstract) EntityTypes.MULE.a(this.world);
        } else {
            EntityHorse entityhorse = (EntityHorse) entityageable;

            entityhorseabstract = (EntityHorseAbstract) EntityTypes.HORSE.a(this.world);
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

            ((EntityHorse) entityhorseabstract).setVariant(j);
        }

        this.a(entityageable, entityhorseabstract);
        return entityhorseabstract;
    }

    @Override
    public boolean eV() {
        return true;
    }

    @Override
    public boolean j(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemHorseArmor;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i;

        if (groupdataentity instanceof EntityHorse.a) {
            i = ((EntityHorse.a) groupdataentity).a;
        } else {
            i = this.random.nextInt(7);
            groupdataentity = new EntityHorse.a(i);
        }

        this.setVariant(i | this.random.nextInt(5) << 8);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public static class a extends EntityAgeable.a {

        public final int a;

        public a(int i) {
            this.a = i;
        }
    }
}
