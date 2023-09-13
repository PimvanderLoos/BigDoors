package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityTropicalFish extends EntityFish {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityTropicalFish.class, DataWatcherRegistry.b);
    private static final MinecraftKey[] c = new MinecraftKey[] { new MinecraftKey("textures/entity/fish/tropical_a.png"), new MinecraftKey("textures/entity/fish/tropical_b.png")};
    private static final MinecraftKey[] bC = new MinecraftKey[] { new MinecraftKey("textures/entity/fish/tropical_a_pattern_1.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_2.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_3.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_4.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_5.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final MinecraftKey[] bD = new MinecraftKey[] { new MinecraftKey("textures/entity/fish/tropical_b_pattern_1.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_2.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_3.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_4.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_5.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_6.png")};
    private static final int[] bE = new int[] { a(1, 1, 1, 7), a(1, 0, 7, 7), a(0, 1, 7, 3), a(0, 4, 0, 7), a(0, 1, 11, 7), a(0, 0, 1, 0), a(0, 5, 6, 3), a(1, 3, 10, 4), a(1, 5, 0, 14), a(0, 5, 0, 4), a(1, 2, 0, 7), a(1, 5, 0, 1), a(0, 3, 9, 6), a(0, 4, 5, 3), a(1, 4, 14, 0), a(0, 2, 7, 14), a(1, 3, 14, 0), a(1, 0, 0, 4), a(0, 1, 0, 0), a(0, 0, 14, 0), a(1, 1, 4, 4), a(0, 3, 0, 7)};
    private boolean bF = true;

    private static int a(int i, int j, int k, int l) {
        return i & 255 | (j & 255) << 8 | (k & 255) << 16 | (l & 255) << 24;
    }

    public EntityTropicalFish(World world) {
        super(EntityTypes.TROPICAL_FISH, world);
        this.setSize(0.5F, 0.4F);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityTropicalFish.b, Integer.valueOf(0));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityTropicalFish.b, Integer.valueOf(i));
    }

    public boolean c(int i) {
        return !this.bF;
    }

    public int getVariant() {
        return ((Integer) this.datawatcher.get(EntityTropicalFish.b)).intValue();
    }

    protected void n() {
        super.n();
        this.goalSelector.a(5, new PathfinderGoalFishSchool(this));
    }

    protected void f(ItemStack itemstack) {
        super.f(itemstack);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.setInt("BucketVariantTag", this.getVariant());
    }

    protected ItemStack dB() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Nullable
    protected MinecraftKey G() {
        return LootTables.aL;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_TROPICAL_FISH_AMBIENT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_TROPICAL_FISH_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_TROPICAL_FISH_HURT;
    }

    protected SoundEffect dD() {
        return SoundEffects.ENTITY_TROPICAL_FISH_FLOP;
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("BucketVariantTag", 3)) {
            this.setVariant(nbttagcompound.getInt("BucketVariantTag"));
            return (GroupDataEntity) object;
        } else {
            int i;
            int j;
            int k;
            int l;

            if (object instanceof EntityTropicalFish.a) {
                EntityTropicalFish.a entitytropicalfish_a = (EntityTropicalFish.a) object;

                i = entitytropicalfish_a.a;
                j = entitytropicalfish_a.b;
                k = entitytropicalfish_a.c;
                l = entitytropicalfish_a.d;
            } else if ((double) this.random.nextFloat() < 0.9D) {
                int i1 = EntityTropicalFish.bE[this.random.nextInt(EntityTropicalFish.bE.length)];

                i = i1 & 255;
                j = (i1 & '\uff00') >> 8;
                k = (i1 & 16711680) >> 16;
                l = (i1 & -16777216) >> 24;
                object = new EntityTropicalFish.a(i, j, k, l, null);
            } else {
                this.bF = false;
                i = this.random.nextInt(2);
                j = this.random.nextInt(6);
                k = this.random.nextInt(15);
                l = this.random.nextInt(15);
            }

            this.setVariant(i | j << 8 | k << 16 | l << 24);
            return (GroupDataEntity) object;
        }
    }

    static class a implements GroupDataEntity {

        private final int a;
        private final int b;
        private final int c;
        private final int d;

        private a(int i, int j, int k, int l) {
            this.a = i;
            this.b = j;
            this.c = k;
            this.d = l;
        }

        a(int i, int j, int k, int l, Object object) {
            this(i, j, k, l);
        }
    }
}
