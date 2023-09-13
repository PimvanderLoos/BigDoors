package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.feature.WorldGenEnder;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;

public enum EnumDragonRespawn {

    START {
        @Override
        public void a(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            BlockPosition blockposition1 = new BlockPosition(0, 128, 0);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) iterator.next();

                entityendercrystal.setBeamTarget(blockposition1);
            }

            enderdragonbattle.setRespawnPhase(null.PREPARING_TO_SUMMON_PILLARS);
        }
    },
    PREPARING_TO_SUMMON_PILLARS {
        @Override
        public void a(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            if (i < 100) {
                if (i == 0 || i == 50 || i == 51 || i == 52 || i >= 95) {
                    worldserver.triggerEffect(3001, new BlockPosition(0, 128, 0), 0);
                }
            } else {
                enderdragonbattle.setRespawnPhase(null.SUMMONING_PILLARS);
            }

        }
    },
    SUMMONING_PILLARS {
        @Override
        public void a(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            boolean flag = true;
            boolean flag1 = i % 40 == 0;
            boolean flag2 = i % 40 == 39;

            if (flag1 || flag2) {
                List<WorldGenEnder.Spike> list1 = WorldGenEnder.a((GeneratorAccessSeed) worldserver);
                int j = i / 40;

                if (j < list1.size()) {
                    WorldGenEnder.Spike worldgenender_spike = (WorldGenEnder.Spike) list1.get(j);

                    if (flag1) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) iterator.next();

                            entityendercrystal.setBeamTarget(new BlockPosition(worldgenender_spike.a(), worldgenender_spike.d() + 1, worldgenender_spike.b()));
                        }
                    } else {
                        boolean flag3 = true;
                        Iterator iterator1 = BlockPosition.a(new BlockPosition(worldgenender_spike.a() - 10, worldgenender_spike.d() - 10, worldgenender_spike.b() - 10), new BlockPosition(worldgenender_spike.a() + 10, worldgenender_spike.d() + 10, worldgenender_spike.b() + 10)).iterator();

                        while (iterator1.hasNext()) {
                            BlockPosition blockposition1 = (BlockPosition) iterator1.next();

                            worldserver.a(blockposition1, false);
                        }

                        worldserver.explode((Entity) null, (double) ((float) worldgenender_spike.a() + 0.5F), (double) worldgenender_spike.d(), (double) ((float) worldgenender_spike.b() + 0.5F), 5.0F, Explosion.Effect.DESTROY);
                        WorldGenFeatureEndSpikeConfiguration worldgenfeatureendspikeconfiguration = new WorldGenFeatureEndSpikeConfiguration(true, ImmutableList.of(worldgenender_spike), new BlockPosition(0, 128, 0));

                        WorldGenerator.END_SPIKE.b((WorldGenFeatureConfiguration) worldgenfeatureendspikeconfiguration).a(worldserver, worldserver.getChunkProvider().getChunkGenerator(), new Random(), new BlockPosition(worldgenender_spike.a(), 45, worldgenender_spike.b()));
                    }
                } else if (flag1) {
                    enderdragonbattle.setRespawnPhase(null.SUMMONING_DRAGON);
                }
            }

        }
    },
    SUMMONING_DRAGON {
        @Override
        public void a(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {
            Iterator iterator;
            EntityEnderCrystal entityendercrystal;

            if (i >= 100) {
                enderdragonbattle.setRespawnPhase(null.END);
                enderdragonbattle.resetCrystals();
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityendercrystal = (EntityEnderCrystal) iterator.next();
                    entityendercrystal.setBeamTarget((BlockPosition) null);
                    worldserver.explode(entityendercrystal, entityendercrystal.locX(), entityendercrystal.locY(), entityendercrystal.locZ(), 6.0F, Explosion.Effect.NONE);
                    entityendercrystal.die();
                }
            } else if (i >= 80) {
                worldserver.triggerEffect(3001, new BlockPosition(0, 128, 0), 0);
            } else if (i == 0) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityendercrystal = (EntityEnderCrystal) iterator.next();
                    entityendercrystal.setBeamTarget(new BlockPosition(0, 128, 0));
                }
            } else if (i < 5) {
                worldserver.triggerEffect(3001, new BlockPosition(0, 128, 0), 0);
            }

        }
    },
    END {
        @Override
        public void a(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition) {}
    };

    private EnumDragonRespawn() {}

    public abstract void a(WorldServer worldserver, EnderDragonBattle enderdragonbattle, List<EntityEnderCrystal> list, int i, BlockPosition blockposition);
}
