package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityTropicalFish extends EntityFish {

    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityTropicalFish.class, DataWatcherRegistry.b);
    private static final MinecraftKey[] bC = new MinecraftKey[] { new MinecraftKey("textures/entity/fish/tropical_a.png"), new MinecraftKey("textures/entity/fish/tropical_b.png")};
    private static final MinecraftKey[] bD = new MinecraftKey[] { new MinecraftKey("textures/entity/fish/tropical_a_pattern_1.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_2.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_3.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_4.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_5.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final MinecraftKey[] bE = new MinecraftKey[] { new MinecraftKey("textures/entity/fish/tropical_b_pattern_1.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_2.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_3.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_4.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_5.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_6.png")};
    public static final int[] b = new int[] { a(EntityTropicalFish.Variant.STRIPEY, EnumColor.ORANGE, EnumColor.GRAY), a(EntityTropicalFish.Variant.FLOPPER, EnumColor.GRAY, EnumColor.GRAY), a(EntityTropicalFish.Variant.FLOPPER, EnumColor.GRAY, EnumColor.BLUE), a(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.GRAY), a(EntityTropicalFish.Variant.SUNSTREAK, EnumColor.BLUE, EnumColor.GRAY), a(EntityTropicalFish.Variant.KOB, EnumColor.ORANGE, EnumColor.WHITE), a(EntityTropicalFish.Variant.SPOTTY, EnumColor.PINK, EnumColor.LIGHT_BLUE), a(EntityTropicalFish.Variant.BLOCKFISH, EnumColor.PURPLE, EnumColor.YELLOW), a(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.RED), a(EntityTropicalFish.Variant.SPOTTY, EnumColor.WHITE, EnumColor.YELLOW), a(EntityTropicalFish.Variant.GLITTER, EnumColor.WHITE, EnumColor.GRAY), a(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.ORANGE), a(EntityTropicalFish.Variant.DASHER, EnumColor.CYAN, EnumColor.PINK), a(EntityTropicalFish.Variant.BRINELY, EnumColor.LIME, EnumColor.LIGHT_BLUE), a(EntityTropicalFish.Variant.BETTY, EnumColor.RED, EnumColor.WHITE), a(EntityTropicalFish.Variant.SNOOPER, EnumColor.GRAY, EnumColor.RED), a(EntityTropicalFish.Variant.BLOCKFISH, EnumColor.RED, EnumColor.WHITE), a(EntityTropicalFish.Variant.FLOPPER, EnumColor.WHITE, EnumColor.YELLOW), a(EntityTropicalFish.Variant.KOB, EnumColor.RED, EnumColor.WHITE), a(EntityTropicalFish.Variant.SUNSTREAK, EnumColor.GRAY, EnumColor.WHITE), a(EntityTropicalFish.Variant.DASHER, EnumColor.CYAN, EnumColor.YELLOW), a(EntityTropicalFish.Variant.FLOPPER, EnumColor.YELLOW, EnumColor.YELLOW)};
    private boolean bF = true;

    private static int a(EntityTropicalFish.Variant entitytropicalfish_variant, EnumColor enumcolor, EnumColor enumcolor1) {
        return entitytropicalfish_variant.a() & 255 | (entitytropicalfish_variant.b() & 255) << 8 | (enumcolor.getColorIndex() & 255) << 16 | (enumcolor1.getColorIndex() & 255) << 24;
    }

    public EntityTropicalFish(World world) {
        super(EntityTypes.TROPICAL_FISH, world);
        this.setSize(0.5F, 0.4F);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityTropicalFish.c, Integer.valueOf(0));
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
        this.datawatcher.set(EntityTropicalFish.c, Integer.valueOf(i));
    }

    public boolean c(int i) {
        return !this.bF;
    }

    public int getVariant() {
        return ((Integer) this.datawatcher.get(EntityTropicalFish.c)).intValue();
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

    protected ItemStack dA() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
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

    protected SoundEffect dC() {
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

            if (object instanceof EntityTropicalFish.b) {
                EntityTropicalFish.b entitytropicalfish_b = (EntityTropicalFish.b) object;

                i = entitytropicalfish_b.a;
                j = entitytropicalfish_b.b;
                k = entitytropicalfish_b.c;
                l = entitytropicalfish_b.d;
            } else if ((double) this.random.nextFloat() < 0.9D) {
                int i1 = EntityTropicalFish.b[this.random.nextInt(EntityTropicalFish.b.length)];

                i = i1 & 255;
                j = (i1 & '\uff00') >> 8;
                k = (i1 & 16711680) >> 16;
                l = (i1 & -16777216) >> 24;
                object = new EntityTropicalFish.b(i, j, k, l, null);
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

    static class b implements GroupDataEntity {

        private final int a;
        private final int b;
        private final int c;
        private final int d;

        private b(int i, int j, int k, int l) {
            this.a = i;
            this.b = j;
            this.c = k;
            this.d = l;
        }

        b(int i, int j, int k, int l, Object object) {
            this(i, j, k, l);
        }
    }

    static enum Variant {

        KOB(0, 0), SUNSTREAK(0, 1), SNOOPER(0, 2), DASHER(0, 3), BRINELY(0, 4), SPOTTY(0, 5), FLOPPER(1, 0), STRIPEY(1, 1), GLITTER(1, 2), BLOCKFISH(1, 3), BETTY(1, 4), CLAYFISH(1, 5);

        private final int m;
        private final int n;
        private static final EntityTropicalFish.Variant[] o = values();

        private Variant(int i, int j) {
            this.m = i;
            this.n = j;
        }

        public int a() {
            return this.m;
        }

        public int b() {
            return this.n;
        }
    }
}
