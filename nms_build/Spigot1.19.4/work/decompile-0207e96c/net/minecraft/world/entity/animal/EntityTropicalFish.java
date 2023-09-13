package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.INamable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;

public class EntityTropicalFish extends EntityFishSchool implements VariantHolder<EntityTropicalFish.Variant> {

    public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
    private static final DataWatcherObject<Integer> DATA_ID_TYPE_VARIANT = DataWatcher.defineId(EntityTropicalFish.class, DataWatcherRegistry.INT);
    public static final List<EntityTropicalFish.d> COMMON_VARIANTS = List.of(new EntityTropicalFish.d(EntityTropicalFish.Variant.STRIPEY, EnumColor.ORANGE, EnumColor.GRAY), new EntityTropicalFish.d(EntityTropicalFish.Variant.FLOPPER, EnumColor.GRAY, EnumColor.GRAY), new EntityTropicalFish.d(EntityTropicalFish.Variant.FLOPPER, EnumColor.GRAY, EnumColor.BLUE), new EntityTropicalFish.d(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.GRAY), new EntityTropicalFish.d(EntityTropicalFish.Variant.SUNSTREAK, EnumColor.BLUE, EnumColor.GRAY), new EntityTropicalFish.d(EntityTropicalFish.Variant.KOB, EnumColor.ORANGE, EnumColor.WHITE), new EntityTropicalFish.d(EntityTropicalFish.Variant.SPOTTY, EnumColor.PINK, EnumColor.LIGHT_BLUE), new EntityTropicalFish.d(EntityTropicalFish.Variant.BLOCKFISH, EnumColor.PURPLE, EnumColor.YELLOW), new EntityTropicalFish.d(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.RED), new EntityTropicalFish.d(EntityTropicalFish.Variant.SPOTTY, EnumColor.WHITE, EnumColor.YELLOW), new EntityTropicalFish.d(EntityTropicalFish.Variant.GLITTER, EnumColor.WHITE, EnumColor.GRAY), new EntityTropicalFish.d(EntityTropicalFish.Variant.CLAYFISH, EnumColor.WHITE, EnumColor.ORANGE), new EntityTropicalFish.d(EntityTropicalFish.Variant.DASHER, EnumColor.CYAN, EnumColor.PINK), new EntityTropicalFish.d(EntityTropicalFish.Variant.BRINELY, EnumColor.LIME, EnumColor.LIGHT_BLUE), new EntityTropicalFish.d(EntityTropicalFish.Variant.BETTY, EnumColor.RED, EnumColor.WHITE), new EntityTropicalFish.d(EntityTropicalFish.Variant.SNOOPER, EnumColor.GRAY, EnumColor.RED), new EntityTropicalFish.d(EntityTropicalFish.Variant.BLOCKFISH, EnumColor.RED, EnumColor.WHITE), new EntityTropicalFish.d(EntityTropicalFish.Variant.FLOPPER, EnumColor.WHITE, EnumColor.YELLOW), new EntityTropicalFish.d(EntityTropicalFish.Variant.KOB, EnumColor.RED, EnumColor.WHITE), new EntityTropicalFish.d(EntityTropicalFish.Variant.SUNSTREAK, EnumColor.GRAY, EnumColor.WHITE), new EntityTropicalFish.d(EntityTropicalFish.Variant.DASHER, EnumColor.CYAN, EnumColor.YELLOW), new EntityTropicalFish.d(EntityTropicalFish.Variant.FLOPPER, EnumColor.YELLOW, EnumColor.YELLOW));
    private boolean isSchool = true;

    public EntityTropicalFish(EntityTypes<? extends EntityTropicalFish> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static String getPredefinedName(int i) {
        return "entity.minecraft.tropical_fish.predefined." + i;
    }

    static int packVariant(EntityTropicalFish.Variant entitytropicalfish_variant, EnumColor enumcolor, EnumColor enumcolor1) {
        return entitytropicalfish_variant.getPackedId() & '\uffff' | (enumcolor.getId() & 255) << 16 | (enumcolor1.getId() & 255) << 24;
    }

    public static EnumColor getBaseColor(int i) {
        return EnumColor.byId(i >> 16 & 255);
    }

    public static EnumColor getPatternColor(int i) {
        return EnumColor.byId(i >> 24 & 255);
    }

    public static EntityTropicalFish.Variant getPattern(int i) {
        return EntityTropicalFish.Variant.byId(i & '\uffff');
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityTropicalFish.DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Variant", this.getPackedVariant());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setPackedVariant(nbttagcompound.getInt("Variant"));
    }

    public void setPackedVariant(int i) {
        this.entityData.set(EntityTropicalFish.DATA_ID_TYPE_VARIANT, i);
    }

    @Override
    public boolean isMaxGroupSizeReached(int i) {
        return !this.isSchool;
    }

    public int getPackedVariant() {
        return (Integer) this.entityData.get(EntityTropicalFish.DATA_ID_TYPE_VARIANT);
    }

    public EnumColor getBaseColor() {
        return getBaseColor(this.getPackedVariant());
    }

    public EnumColor getPatternColor() {
        return getPatternColor(this.getPackedVariant());
    }

    @Override
    public EntityTropicalFish.Variant getVariant() {
        return getPattern(this.getPackedVariant());
    }

    public void setVariant(EntityTropicalFish.Variant entitytropicalfish_variant) {
        int i = this.getPackedVariant();
        EnumColor enumcolor = getBaseColor(i);
        EnumColor enumcolor1 = getPatternColor(i);

        this.setPackedVariant(packVariant(entitytropicalfish_variant, enumcolor, enumcolor1));
    }

    @Override
    public void saveToBucketTag(ItemStack itemstack) {
        super.saveToBucketTag(itemstack);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.putInt("BucketVariantTag", this.getPackedVariant());
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

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);

        if (enummobspawn == EnumMobSpawn.BUCKET && nbttagcompound != null && nbttagcompound.contains("BucketVariantTag", 3)) {
            this.setPackedVariant(nbttagcompound.getInt("BucketVariantTag"));
            return (GroupDataEntity) object;
        } else {
            RandomSource randomsource = worldaccess.getRandom();
            EntityTropicalFish.d entitytropicalfish_d;

            if (object instanceof EntityTropicalFish.c) {
                EntityTropicalFish.c entitytropicalfish_c = (EntityTropicalFish.c) object;

                entitytropicalfish_d = entitytropicalfish_c.variant;
            } else if ((double) randomsource.nextFloat() < 0.9D) {
                entitytropicalfish_d = (EntityTropicalFish.d) SystemUtils.getRandom(EntityTropicalFish.COMMON_VARIANTS, randomsource);
                object = new EntityTropicalFish.c(this, entitytropicalfish_d);
            } else {
                this.isSchool = false;
                EntityTropicalFish.Variant[] aentitytropicalfish_variant = EntityTropicalFish.Variant.values();
                EnumColor[] aenumcolor = EnumColor.values();
                EntityTropicalFish.Variant entitytropicalfish_variant = (EntityTropicalFish.Variant) SystemUtils.getRandom((Object[]) aentitytropicalfish_variant, randomsource);
                EnumColor enumcolor = (EnumColor) SystemUtils.getRandom((Object[]) aenumcolor, randomsource);
                EnumColor enumcolor1 = (EnumColor) SystemUtils.getRandom((Object[]) aenumcolor, randomsource);

                entitytropicalfish_d = new EntityTropicalFish.d(entitytropicalfish_variant, enumcolor, enumcolor1);
            }

            this.setPackedVariant(entitytropicalfish_d.getPackedId());
            return (GroupDataEntity) object;
        }
    }

    public static boolean checkTropicalFishSpawnRules(EntityTypes<EntityTropicalFish> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, RandomSource randomsource) {
        return generatoraccess.getFluidState(blockposition.below()).is(TagsFluid.WATER) && generatoraccess.getBlockState(blockposition.above()).is(Blocks.WATER) && (generatoraccess.getBiome(blockposition).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || EntityWaterAnimal.checkSurfaceWaterAnimalSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, randomsource));
    }

    public static enum Variant implements INamable {

        KOB("kob", EntityTropicalFish.Base.SMALL, 0), SUNSTREAK("sunstreak", EntityTropicalFish.Base.SMALL, 1), SNOOPER("snooper", EntityTropicalFish.Base.SMALL, 2), DASHER("dasher", EntityTropicalFish.Base.SMALL, 3), BRINELY("brinely", EntityTropicalFish.Base.SMALL, 4), SPOTTY("spotty", EntityTropicalFish.Base.SMALL, 5), FLOPPER("flopper", EntityTropicalFish.Base.LARGE, 0), STRIPEY("stripey", EntityTropicalFish.Base.LARGE, 1), GLITTER("glitter", EntityTropicalFish.Base.LARGE, 2), BLOCKFISH("blockfish", EntityTropicalFish.Base.LARGE, 3), BETTY("betty", EntityTropicalFish.Base.LARGE, 4), CLAYFISH("clayfish", EntityTropicalFish.Base.LARGE, 5);

        public static final Codec<EntityTropicalFish.Variant> CODEC = INamable.fromEnum(EntityTropicalFish.Variant::values);
        private static final IntFunction<EntityTropicalFish.Variant> BY_ID = ByIdMap.sparse(EntityTropicalFish.Variant::getPackedId, values(), EntityTropicalFish.Variant.KOB);
        private final String name;
        private final IChatBaseComponent displayName;
        private final EntityTropicalFish.Base base;
        private final int packedId;

        private Variant(String s, EntityTropicalFish.Base entitytropicalfish_base, int i) {
            this.name = s;
            this.base = entitytropicalfish_base;
            this.packedId = entitytropicalfish_base.id | i << 8;
            this.displayName = IChatBaseComponent.translatable("entity.minecraft.tropical_fish.type." + this.name);
        }

        public static EntityTropicalFish.Variant byId(int i) {
            return (EntityTropicalFish.Variant) EntityTropicalFish.Variant.BY_ID.apply(i);
        }

        public EntityTropicalFish.Base base() {
            return this.base;
        }

        public int getPackedId() {
            return this.packedId;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public IChatBaseComponent displayName() {
            return this.displayName;
        }
    }

    private static class c extends EntityFishSchool.a {

        final EntityTropicalFish.d variant;

        c(EntityTropicalFish entitytropicalfish, EntityTropicalFish.d entitytropicalfish_d) {
            super(entitytropicalfish);
            this.variant = entitytropicalfish_d;
        }
    }

    public static record d(EntityTropicalFish.Variant pattern, EnumColor baseColor, EnumColor patternColor) {

        public int getPackedId() {
            return EntityTropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
        }
    }

    public static enum Base {

        SMALL(0), LARGE(1);

        final int id;

        private Base(int i) {
            this.id = i;
        }
    }
}
