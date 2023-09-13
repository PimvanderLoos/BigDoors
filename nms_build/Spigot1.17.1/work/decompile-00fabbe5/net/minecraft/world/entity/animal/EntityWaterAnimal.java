package net.minecraft.world.entity.animal;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class EntityWaterAnimal extends EntityCreature {

    protected EntityWaterAnimal(EntityTypes<? extends EntityWaterAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.WATER, 0.0F);
    }

    @Override
    public boolean dr() {
        return true;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.WATER;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.f((Entity) this);
    }

    @Override
    public int J() {
        return 120;
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.level.random.nextInt(3);
    }

    protected void a(int i) {
        if (this.isAlive() && !this.aO()) {
            this.setAirTicks(i - 1);
            if (this.getAirTicks() == -20) {
                this.setAirTicks(0);
                this.damageEntity(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirTicks(300);
        }

    }

    @Override
    public void entityBaseTick() {
        int i = this.getAirTicks();

        super.entityBaseTick();
        this.a(i);
    }

    @Override
    public boolean ck() {
        return false;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    public static boolean a(EntityTypes<? extends EntityLiving> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() < worldaccess.getSeaLevel() && blockposition.getY() < worldaccess.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX(), blockposition.getZ()) && a(worldaccess, blockposition) && a(blockposition, worldaccess);
    }

    public static boolean a(BlockPosition blockposition, WorldAccess worldaccess) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i = 0; i < 5; ++i) {
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
            IBlockData iblockdata = worldaccess.getType(blockposition_mutableblockposition);

            if (iblockdata.a((Tag) TagsBlock.BASE_STONE_OVERWORLD)) {
                return true;
            }

            if (!iblockdata.a(Blocks.WATER)) {
                return false;
            }
        }

        return false;
    }

    public static boolean a(WorldAccess worldaccess, BlockPosition blockposition) {
        int i = worldaccess.getLevel().Y() ? worldaccess.c(blockposition, 10) : worldaccess.getLightLevel(blockposition);

        return i == 0;
    }
}
