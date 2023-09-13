package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class BehaviorVillageHeroGift extends Behavior<EntityVillager> {

    private static final int THROW_GIFT_AT_DISTANCE = 5;
    private static final int MIN_TIME_BETWEEN_GIFTS = 600;
    private static final int MAX_TIME_BETWEEN_GIFTS = 6600;
    private static final int TIME_TO_DELAY_FOR_HEAD_TO_FINISH_TURNING = 20;
    private static final Map<VillagerProfession, MinecraftKey> GIFTS = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(VillagerProfession.ARMORER, LootTables.ARMORER_GIFT);
        hashmap.put(VillagerProfession.BUTCHER, LootTables.BUTCHER_GIFT);
        hashmap.put(VillagerProfession.CARTOGRAPHER, LootTables.CARTOGRAPHER_GIFT);
        hashmap.put(VillagerProfession.CLERIC, LootTables.CLERIC_GIFT);
        hashmap.put(VillagerProfession.FARMER, LootTables.FARMER_GIFT);
        hashmap.put(VillagerProfession.FISHERMAN, LootTables.FISHERMAN_GIFT);
        hashmap.put(VillagerProfession.FLETCHER, LootTables.FLETCHER_GIFT);
        hashmap.put(VillagerProfession.LEATHERWORKER, LootTables.LEATHERWORKER_GIFT);
        hashmap.put(VillagerProfession.LIBRARIAN, LootTables.LIBRARIAN_GIFT);
        hashmap.put(VillagerProfession.MASON, LootTables.MASON_GIFT);
        hashmap.put(VillagerProfession.SHEPHERD, LootTables.SHEPHERD_GIFT);
        hashmap.put(VillagerProfession.TOOLSMITH, LootTables.TOOLSMITH_GIFT);
        hashmap.put(VillagerProfession.WEAPONSMITH, LootTables.WEAPONSMITH_GIFT);
    });
    private static final float SPEED_MODIFIER = 0.5F;
    private int timeUntilNextGift = 600;
    private boolean giftGivenDuringThisRun;
    private long timeSinceStart;

    public BehaviorVillageHeroGift(int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), i);
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        if (!this.b(entityvillager)) {
            return false;
        } else if (this.timeUntilNextGift > 0) {
            --this.timeUntilNextGift;
            return false;
        } else {
            return true;
        }
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.giftGivenDuringThisRun = false;
        this.timeSinceStart = i;
        EntityHuman entityhuman = (EntityHuman) this.c(entityvillager).get();

        entityvillager.getBehaviorController().setMemory(MemoryModuleType.INTERACTION_TARGET, (Object) entityhuman);
        BehaviorUtil.a((EntityLiving) entityvillager, (EntityLiving) entityhuman);
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.b(entityvillager) && !this.giftGivenDuringThisRun;
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityHuman entityhuman = (EntityHuman) this.c(entityvillager).get();

        BehaviorUtil.a((EntityLiving) entityvillager, (EntityLiving) entityhuman);
        if (this.a(entityvillager, entityhuman)) {
            if (i - this.timeSinceStart > 20L) {
                this.a(entityvillager, (EntityLiving) entityhuman);
                this.giftGivenDuringThisRun = true;
            }
        } else {
            BehaviorUtil.a(entityvillager, (Entity) entityhuman, 0.5F, 5);
        }

    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.timeUntilNextGift = a(worldserver);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.INTERACTION_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    private void a(EntityVillager entityvillager, EntityLiving entityliving) {
        List<ItemStack> list = this.a(entityvillager);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            BehaviorUtil.a((EntityLiving) entityvillager, itemstack, entityliving.getPositionVector());
        }

    }

    private List<ItemStack> a(EntityVillager entityvillager) {
        if (entityvillager.isBaby()) {
            return ImmutableList.of(new ItemStack(Items.POPPY));
        } else {
            VillagerProfession villagerprofession = entityvillager.getVillagerData().getProfession();

            if (BehaviorVillageHeroGift.GIFTS.containsKey(villagerprofession)) {
                LootTable loottable = entityvillager.level.getMinecraftServer().getLootTableRegistry().getLootTable((MinecraftKey) BehaviorVillageHeroGift.GIFTS.get(villagerprofession));
                LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) entityvillager.level)).set(LootContextParameters.ORIGIN, entityvillager.getPositionVector()).set(LootContextParameters.THIS_ENTITY, entityvillager).a(entityvillager.getRandom());

                return loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.GIFT));
            } else {
                return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
            }
        }
    }

    private boolean b(EntityVillager entityvillager) {
        return this.c(entityvillager).isPresent();
    }

    private Optional<EntityHuman> c(EntityVillager entityvillager) {
        return entityvillager.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::a);
    }

    private boolean a(EntityHuman entityhuman) {
        return entityhuman.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
    }

    private boolean a(EntityVillager entityvillager, EntityHuman entityhuman) {
        BlockPosition blockposition = entityhuman.getChunkCoordinates();
        BlockPosition blockposition1 = entityvillager.getChunkCoordinates();

        return blockposition1.a((BaseBlockPosition) blockposition, 5.0D);
    }

    private static int a(WorldServer worldserver) {
        return 600 + worldserver.random.nextInt(6001);
    }
}
