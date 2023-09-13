package net.minecraft.world.entity.animal;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.Biomes;

public class EntityTropicalFish extends EntityFishSchool {

    public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
    private static final DataWatcherObject<Integer> DATA_ID_TYPE_VARIANT = DataWatcher.defineId(EntityTropicalFish.class, DataWatcherRegistry.INT);
    public static final int BASE_SMALL = 0;
    public static final int BASE_LARGE = 1;
    private static final int BASES = 2;
    private static final MinecraftKey[] BASE_TEXTURE_LOCATIONS = new MinecraftKey[]{new MinecraftKey("textures/entity/fish/tropical_a.png"), new MinecraftKey("textures/entity/fish/tropical_b.png")};
    private static final MinecraftKey[] PATTERN_A_TEXTURE_LOCATIONS = new MinecraftKey[]{new MinecraftKey("textures/entity/fish/tropical_a_pattern_1.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_2.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_3.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_4.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_5.png"), new MinecraftKey("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final MinecraftKey[] PATTERN_B_TEXTURE_LOCATIONS = new MinecraftKey[]{new MinecraftKey("textures/entity/fish/tropical_b_pattern_1.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_2.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_3.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_4.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_5.png"), new MinecraftKey("textures/entity/fish/tropical_b_pattern_6.png")};
    private static final int PATTERNS = 6;
    private static final int COLORS = 15;
    public static final int[] COMMON_VARIANTS = new int[]{calculateVariant(EntityTropicalFish.Variant.STRIPEY, EnumColor.ORANGE, EnumColor.GRAY), calculateVariant(EntityTropicalFish.Variant.FLOPPER, EnumColor.GRAY, EnumColor.GRAY), calculateVariant(EntityTropicalFish.Variant.FLOPPER, EnumColor.GRAY, EnumColor.BLUE), calculateVariant(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.GRAY), calculateVariant(EntityTropicalFish.Variant.SUNSTREAK, EnumColor.BLUE, EnumColor.GRAY), calculateVariant(EntityTropicalFish.Variant.KOB, EnumColor.ORANGE, EnumColor.WHITE), calculateVariant(EntityTropicalFish.Variant.SPOTTY, EnumColor.PINK, EnumColor.LIGHT_BLUE), calculateVariant(EntityTropicalFish.Variant.BLOCKFISH, EnumColor.PURPLE, EnumColor.YELLOW), calculateVariant(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.RED), calculateVariant(EntityTropicalFish.Variant.SPOTTY, EnumColor.WHITE, EnumColor.YELLOW), calculateVariant(EntityTropicalFish.Variant.GLITTER, EnumColor.WHITE, EnumColor.GRAY), calculateVariant(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.ORANGE), calculateVariant(EntityTropicalFish.Variant.DASHER, EnumColor.CYAN, EnumColor.PINK), calculateVariant(EntityTropicalFish.Variant.BRINELY, EnumColor.LIME, EnumColor.LIGHT_BLUE), calculateVariant(EntityTropicalFish.Variant.BETTY, EnumColor.RED, EnumColor.WHITE), calculateVariant(EntityTropicalFish.Variant.SNOOPER, EnumColor.GRAY, EnumColor.RED), calculateVariant(EntityTropicalFish.Variant.BLOCKFISH, EnumColor.RED, EnumColor.WHITE), calculateVariant(EntityTropicalFish.Variant.FLOPPER, EnumColor.WHITE, EnumColor.YELLOW), calculateVariant(EntityTropicalFish.Variant.KOB, EnumColor.RED, EnumColor.WHITE), calculateVariant(EntityTropicalFish.Variant.SUNSTREAK, EnumColor.GRAY, EnumColor.WHITE), calculateVariant(EntityTropicalFish.Variant.DASHER, EnumColor.CYAN, EnumColor.YELLOW), calculateVariant(EntityTropicalFish.Variant.FLOPPER, EnumColor.YELLOW, EnumColor.YELLOW)};
    private boolean isSchool = true;

    private static int calculateVariant(EntityTropicalFish.Variant entitytropicalfish_variant, EnumColor enumcolor, EnumColor enumcolor1) {
        return entitytropicalfish_variant.getBase() & 255 | (entitytropicalfish_variant.getIndex() & 255) << 8 | (enumcolor.getId() & 255) << 16 | (enumcolor1.getId() & 255) << 24;
    }

    public EntityTropicalFish(EntityTypes<? extends EntityTropicalFish> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static String getPredefinedName(int i) {
        return "entity.minecraft.tropical_fish.predefined." + i;
    }

    public static EnumColor getBaseColor(int i) {
        return EnumColor.byId(getBaseColorIdx(i));
    }

    public static EnumColor getPatternColor(int i) {
        return EnumColor.byId(getPatternColorIdx(i));
    }

    public static String getFishTypeName(int i) {
        int j = getBaseVariant(i);
        int k = getPatternVariant(i);
        String s = EntityTropicalFish.Variant.getPatternName(j, k);

        return "entity.minecraft.tropical_fish.type." + s;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityTropicalFish.DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
    }

    public void setVariant(int i) {
        this.entityData.set(EntityTropicalFish.DATA_ID_TYPE_VARIANT, i);
    }

    @Override
    public boolean isMaxGroupSizeReached(int i) {
        return !this.isSchool;
    }

    public int getVariant() {
        return (Integer) this.entityData.get(EntityTropicalFish.DATA_ID_TYPE_VARIANT);
    }

    @Override
    public void saveToBucketTag(ItemStack itemstack) {
        super.saveToBucketTag(itemstack);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.putInt("BucketVariantTag", this.getVariant());
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEffect getFlopSound() {
        return SoundEffects.TROPICAL_FISH_FLOP;
    }

    private static int getBaseColorIdx(int i) {
        return (i & 16711680) >> 16;
    }

    public float[] getBaseColor() {
        return EnumColor.byId(getBaseColorIdx(this.getVariant())).getTextureDiffuseColors();
    }

    private static int getPatternColorIdx(int i) {
        return (i & -16777216) >> 24;
    }

    public float[] getPatternColor() {
        return EnumColor.byId(getPatternColorIdx(this.getVariant())).getTextureDiffuseColors();
    }

    public static int getBaseVariant(int i) {
        return Math.min(i & 255, 1);
    }

    public int getBaseVariant() {
        return getBaseVariant(this.getVariant());
    }

    private static int getPatternVariant(int i) {
        return Math.min((i & '\uff00') >> 8, 5);
    }

    public MinecraftKey getPatternTextureLocation() {
        return getBaseVariant(this.getVariant()) == 0 ? EntityTropicalFish.PATTERN_A_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())] : EntityTropicalFish.PATTERN_B_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())];
    }

    public MinecraftKey getBaseTextureLocation() {
        return EntityTropicalFish.BASE_TEXTURE_LOCATIONS[getBaseVariant(this.getVariant())];
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);

        if (enummobspawn == EnumMobSpawn.BUCKET && nbttagcompound != null && nbttagcompound.contains("BucketVariantTag", 3)) {
            this.setVariant(nbttagcompound.getInt("BucketVariantTag"));
            return (GroupDataEntity) object;
        } else {
            int i;
            int j;
            int k;
            int l;

            if (object instanceof EntityTropicalFish.b) {
                EntityTropicalFish.b entitytropicalfish_b = (EntityTropicalFish.b) object;

                i = entitytropicalfish_b.base;
                j = entitytropicalfish_b.pattern;
                k = entitytropicalfish_b.baseColor;
                l = entitytropicalfish_b.patternColor;
            } else if ((double) this.random.nextFloat() < 0.9D) {
                int i1 = SystemUtils.getRandom(EntityTropicalFish.COMMON_VARIANTS, this.random);

                i = i1 & 255;
                j = (i1 & '\uff00') >> 8;
                k = (i1 & 16711680) >> 16;
                l = (i1 & -16777216) >> 24;
                object = new EntityTropicalFish.b(this, i, j, k, l);
            } else {
                this.isSchool = false;
                i = this.random.nextInt(2);
                j = this.random.nextInt(6);
                k = this.random.nextInt(15);
                l = this.random.nextInt(15);
            }

            this.setVariant(i | j << 8 | k << 16 | l << 24);
            return (GroupDataEntity) object;
        }
    }

    public static boolean checkTropicalFishSpawnRules(EntityTypes<EntityTropicalFish> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getFluidState(blockposition.below()).is((Tag) TagsFluid.WATER) && (Objects.equals(generatoraccess.getBiomeName(blockposition), Optional.of(Biomes.LUSH_CAVES)) || EntityWaterAnimal.checkSurfaceWaterAnimalSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, random));
    }

    private static enum Variant {

        KOB(0, 0), SUNSTREAK(0, 1), SNOOPER(0, 2), DASHER(0, 3), BRINELY(0, 4), SPOTTY(0, 5), FLOPPER(1, 0), STRIPEY(1, 1), GLITTER(1, 2), BLOCKFISH(1, 3), BETTY(1, 4), CLAYFISH(1, 5);

        private final int base;
        private final int index;
        private static final EntityTropicalFish.Variant[] VALUES = values();

        private Variant(int i, int j) {
            this.base = i;
            this.index = j;
        }

        public int getBase() {
            return this.base;
        }

        public int getIndex() {
            return this.index;
        }

        public static String getPatternName(int i, int j) {
            return EntityTropicalFish.Variant.VALUES[j + 6 * i].getName();
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    private static class b extends EntityFishSchool.a {

        final int base;
        final int pattern;
        final int baseColor;
        final int patternColor;

        b(EntityTropicalFish entitytropicalfish, int i, int j, int k, int l) {
            super(entitytropicalfish);
            this.base = i;
            this.pattern = j;
            this.baseColor = k;
            this.patternColor = l;
        }
    }
}
