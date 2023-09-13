package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenEnder;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;

public enum EnumDragonRespawn {

    START {
        @Override
        public void tick(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            BlockPosition blockposition1 = new BlockPosition(0, 128, 0);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) iterator.next();

                entityendercrystal.setBeamTarget(blockposition1);
            }

            enderdragonbattle.setRespawnStage(null.PREPARING_TO_SUMMON_PILLARS);
        }
    },
    PREPARING_TO_SUMMON_PILLARS {
        @Override
        public void tick(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            if (i < 100) {
                if (i == 0 || i == 50 || i == 51 || i == 52 || i >= 95) {
                    worldserver.levelEvent(3001, new BlockPosition(0, 128, 0), 0);
                }
            } else {
                enderdragonbattle.setRespawnStage(null.SUMMONING_PILLARS);
            }

        }
    },
    SUMMONING_PILLARS {
        @Override
        public void tick(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            boolean flag = true;
            boolean flag1 = i % 40 == 0;
            boolean flag2 = i % 40 == 39;

            if (flag1 || flag2) {
                List<WorldGenEnder.Spike> list1 = WorldGenEnder.getSpikesForLevel(worldserver);
                int j = i / 40;

                if (j < list1.size()) {
                    WorldGenEnder.Spike worldgenender_spike = (WorldGenEnder.Spike) list1.get(j);

                    if (flag1) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) iterator.next();

                            entityendercrystal.setBeamTarget(new BlockPosition(worldgenender_spike.getCenterX(), worldgenender_spike.getHeight() + 1, worldgenender_spike.getCenterZ()));
                        }
                    } else {
                        boolean flag3 = true;
                        Iterator iterator1 = BlockPosition.betweenClosed(new BlockPosition(worldgenender_spike.getCenterX() - 10, worldgenender_spike.getHeight() - 10, worldgenender_spike.getCenterZ() - 10), new BlockPosition(worldgenender_spike.getCenterX() + 10, worldgenender_spike.getHeight() + 10, worldgenender_spike.getCenterZ() + 10)).iterator();

                        while (iterator1.hasNext()) {
                            BlockPosition blockposition1 = (BlockPosition) iterator1.next();

                            worldserver.removeBlock(blockposition1, false);
                        }

                        worldserver.explode((Entity) null, (double) ((float) worldgenender_spike.getCenterX() + 0.5F), (double) worldgenender_spike.getHeight(), (double) ((float) worldgenender_spike.getCenterZ() + 0.5F), 5.0F, World.a.BLOCK);
                        WorldGenFeatureEndSpikeConfiguration worldgenfeatureendspikeconfiguration = new WorldGenFeatureEndSpikeConfiguration(true, ImmutableList.of(worldgenender_spike), new BlockPosition(0, 128, 0));

                        WorldGenerator.END_SPIKE.place(worldgenfeatureendspikeconfiguration, worldserver, worldserver.getChunkSource().getGenerator(), RandomSource.create(), new BlockPosition(worldgenender_spike.getCenterX(), 45, worldgenender_spike.getCenterZ()));
                    }
                } else if (flag1) {
                    enderdragonbattle.setRespawnStage(null.SUMMONING_DRAGON);
                }
            }

        }
    },
    SUMMONING_DRAGON {
        @Override
        public void tick(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            Iterator iterator;
            EntityEnderCrystal entityendercrystal;

            if (i >= 100) {
                enderdragonbattle.setRespawnStage(null.END);
                enderdragonbattle.resetSpikeCrystals();
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityendercrystal = (EntityEnderCrystal) iterator.next();
                    entityendercrystal.setBeamTarget((BlockPosition) null);
                    worldserver.explode(entityendercrystal, entityendercrystal.getX(), entityendercrystal.getY(), entityendercrystal.getZ(), 6.0F, World.a.NONE);
                    entityendercrystal.discard();
                }
            } else if (i >= 80) {
                worldserver.levelEvent(3001, new BlockPosition(0, 128, 0), 0);
            } else if (i == 0) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityendercrystal = (EntityEnderCrystal) iterator.next();
                    entityendercrystal.setBeamTarget(new BlockPosition(0, 128, 0));
                }
            } else if (i < 5) {
                worldserver.levelEvent(3001, new BlockPosition(0, 128, 0), 0);
            }

        }
    },
    END {
        @Override
        public void tick(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {}
    };

    EnumDragonRespawn() {}

    public abstract void tick(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition);
}
