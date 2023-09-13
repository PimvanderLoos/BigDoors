package net.minecraft.util;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class SpawnUtil {

    public SpawnUtil() {}

    public static <T extends EntityInsentient> Optional<T> trySpawnMob(EntityTypes<T> entitytypes, EnumMobSpawn enummobspawn, WorldServer worldserver, BlockPosition blockposition, int i, int j, int k, SpawnUtil.a spawnutil_a) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int l = 0; l < i; ++l) {
            int i1 = MathHelper.randomBetweenInclusive(worldserver.random, -j, j);
            int j1 = MathHelper.randomBetweenInclusive(worldserver.random, -j, j);

            blockposition_mutableblockposition.setWithOffset(blockposition, i1, k, j1);
            if (worldserver.getWorldBorder().isWithinBounds((BlockPosition) blockposition_mutableblockposition) && moveToPossibleSpawnPosition(worldserver, k, blockposition_mutableblockposition, spawnutil_a)) {
                T t0 = (EntityInsentient) entitytypes.create(worldserver, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition_mutableblockposition, enummobspawn, false, false);

                if (t0 != null) {
                    if (t0.checkSpawnRules(worldserver, enummobspawn) && t0.checkSpawnObstruction(worldserver)) {
                        worldserver.addFreshEntityWithPassengers(t0);
                        return Optional.of(t0);
                    }

                    t0.discard();
                }
            }
        }

        return Optional.empty();
    }

    private static boolean moveToPossibleSpawnPosition(WorldServer worldserver, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, SpawnUtil.a spawnutil_a) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = (new BlockPosition.MutableBlockPosition()).set(blockposition_mutableblockposition);
        IBlockData iblockdata = worldserver.getBlockState(blockposition_mutableblockposition1);

        for (int j = i; j >= -i; --j) {
            blockposition_mutableblockposition.move(EnumDirection.DOWN);
            blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.UP);
            IBlockData iblockdata1 = worldserver.getBlockState(blockposition_mutableblockposition);

            if (spawnutil_a.canSpawnOn(worldserver, blockposition_mutableblockposition, iblockdata1, blockposition_mutableblockposition1, iblockdata)) {
                blockposition_mutableblockposition.move(EnumDirection.UP);
                return true;
            }

            iblockdata = iblockdata1;
        }

        return false;
    }

    public interface a {

        SpawnUtil.a LEGACY_IRON_GOLEM = (worldserver, blockposition, iblockdata, blockposition1, iblockdata1) -> {
            return (iblockdata1.isAir() || iblockdata1.getMaterial().isLiquid()) && iblockdata.getMaterial().isSolidBlocking();
        };
        SpawnUtil.a ON_TOP_OF_COLLIDER = (worldserver, blockposition, iblockdata, blockposition1, iblockdata1) -> {
            return iblockdata1.getCollisionShape(worldserver, blockposition1).isEmpty() && Block.isFaceFull(iblockdata.getCollisionShape(worldserver, blockposition), EnumDirection.UP);
        };

        boolean canSpawnOn(WorldServer worldserver, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1);
    }
}
