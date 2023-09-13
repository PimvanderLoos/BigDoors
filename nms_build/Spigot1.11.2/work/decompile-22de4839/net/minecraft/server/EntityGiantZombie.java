package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityGiantZombie extends EntityMonster {

    public EntityGiantZombie(World world) {
        super(world);
        this.setSize(this.width * 6.0F, this.length * 6.0F);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityGiantZombie.class);
    }

    public float getHeadHeight() {
        return 10.440001F;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(50.0D);
    }

    public float a(BlockPosition blockposition) {
        return this.world.n(blockposition) - 0.5F;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.u;
    }
}
