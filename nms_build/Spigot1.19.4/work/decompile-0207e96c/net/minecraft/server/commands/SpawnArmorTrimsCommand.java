package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.EnumArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.World;

public class SpawnArmorTrimsCommand {

    private static final Map<Pair<ArmorMaterial, EnumItemSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put(Pair.of(EnumArmorMaterial.CHAIN, EnumItemSlot.HEAD), Items.CHAINMAIL_HELMET);
        hashmap.put(Pair.of(EnumArmorMaterial.CHAIN, EnumItemSlot.CHEST), Items.CHAINMAIL_CHESTPLATE);
        hashmap.put(Pair.of(EnumArmorMaterial.CHAIN, EnumItemSlot.LEGS), Items.CHAINMAIL_LEGGINGS);
        hashmap.put(Pair.of(EnumArmorMaterial.CHAIN, EnumItemSlot.FEET), Items.CHAINMAIL_BOOTS);
        hashmap.put(Pair.of(EnumArmorMaterial.IRON, EnumItemSlot.HEAD), Items.IRON_HELMET);
        hashmap.put(Pair.of(EnumArmorMaterial.IRON, EnumItemSlot.CHEST), Items.IRON_CHESTPLATE);
        hashmap.put(Pair.of(EnumArmorMaterial.IRON, EnumItemSlot.LEGS), Items.IRON_LEGGINGS);
        hashmap.put(Pair.of(EnumArmorMaterial.IRON, EnumItemSlot.FEET), Items.IRON_BOOTS);
        hashmap.put(Pair.of(EnumArmorMaterial.GOLD, EnumItemSlot.HEAD), Items.GOLDEN_HELMET);
        hashmap.put(Pair.of(EnumArmorMaterial.GOLD, EnumItemSlot.CHEST), Items.GOLDEN_CHESTPLATE);
        hashmap.put(Pair.of(EnumArmorMaterial.GOLD, EnumItemSlot.LEGS), Items.GOLDEN_LEGGINGS);
        hashmap.put(Pair.of(EnumArmorMaterial.GOLD, EnumItemSlot.FEET), Items.GOLDEN_BOOTS);
        hashmap.put(Pair.of(EnumArmorMaterial.NETHERITE, EnumItemSlot.HEAD), Items.NETHERITE_HELMET);
        hashmap.put(Pair.of(EnumArmorMaterial.NETHERITE, EnumItemSlot.CHEST), Items.NETHERITE_CHESTPLATE);
        hashmap.put(Pair.of(EnumArmorMaterial.NETHERITE, EnumItemSlot.LEGS), Items.NETHERITE_LEGGINGS);
        hashmap.put(Pair.of(EnumArmorMaterial.NETHERITE, EnumItemSlot.FEET), Items.NETHERITE_BOOTS);
        hashmap.put(Pair.of(EnumArmorMaterial.DIAMOND, EnumItemSlot.HEAD), Items.DIAMOND_HELMET);
        hashmap.put(Pair.of(EnumArmorMaterial.DIAMOND, EnumItemSlot.CHEST), Items.DIAMOND_CHESTPLATE);
        hashmap.put(Pair.of(EnumArmorMaterial.DIAMOND, EnumItemSlot.LEGS), Items.DIAMOND_LEGGINGS);
        hashmap.put(Pair.of(EnumArmorMaterial.DIAMOND, EnumItemSlot.FEET), Items.DIAMOND_BOOTS);
        hashmap.put(Pair.of(EnumArmorMaterial.TURTLE, EnumItemSlot.HEAD), Items.TURTLE_HELMET);
    });
    private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE);
    private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
    private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER = SystemUtils.createIndexLookup(SpawnArmorTrimsCommand.VANILLA_TRIM_PATTERNS);
    private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER = SystemUtils.createIndexLookup(SpawnArmorTrimsCommand.VANILLA_TRIM_MATERIALS);

    public SpawnArmorTrimsCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("spawn_armor_trims").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2) && commandlistenerwrapper.getLevel().enabledFeatures().contains(FeatureFlags.UPDATE_1_20);
        })).executes((commandcontext) -> {
            return spawnArmorTrims((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException());
        }));
    }

    private static int spawnArmorTrims(CommandListenerWrapper commandlistenerwrapper, EntityHuman entityhuman) {
        World world = entityhuman.getLevel();
        NonNullList<ArmorTrim> nonnulllist = NonNullList.create();
        IRegistry<TrimPattern> iregistry = world.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
        IRegistry<TrimMaterial> iregistry1 = world.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);

        iregistry.stream().sorted(Comparator.comparing((trimpattern) -> {
            return SpawnArmorTrimsCommand.TRIM_PATTERN_ORDER.applyAsInt((ResourceKey) iregistry.getResourceKey(trimpattern).orElse((Object) null));
        })).forEachOrdered((trimpattern) -> {
            iregistry1.stream().sorted(Comparator.comparing((trimmaterial) -> {
                return SpawnArmorTrimsCommand.TRIM_MATERIAL_ORDER.applyAsInt((ResourceKey) iregistry1.getResourceKey(trimmaterial).orElse((Object) null));
            })).forEachOrdered((trimmaterial) -> {
                nonnulllist.add(new ArmorTrim(iregistry1.wrapAsHolder(trimmaterial), iregistry.wrapAsHolder(trimpattern)));
            });
        });
        BlockPosition blockposition = entityhuman.blockPosition().relative(entityhuman.getDirection(), 5);
        int i = EnumArmorMaterial.values().length - 1;
        double d0 = 3.0D;
        int j = 0;
        int k = 0;

        for (Iterator iterator = nonnulllist.iterator(); iterator.hasNext(); ++j) {
            ArmorTrim armortrim = (ArmorTrim) iterator.next();
            EnumArmorMaterial[] aenumarmormaterial = EnumArmorMaterial.values();
            int l = aenumarmormaterial.length;

            for (int i1 = 0; i1 < l; ++i1) {
                EnumArmorMaterial enumarmormaterial = aenumarmormaterial[i1];

                if (enumarmormaterial != EnumArmorMaterial.LEATHER) {
                    double d1 = (double) blockposition.getX() + 0.5D - (double) (j % iregistry1.size()) * 3.0D;
                    double d2 = (double) blockposition.getY() + 0.5D + (double) (k % i) * 3.0D;
                    double d3 = (double) blockposition.getZ() + 0.5D + (double) (j / iregistry1.size() * 10);
                    EntityArmorStand entityarmorstand = new EntityArmorStand(world, d1, d2, d3);

                    entityarmorstand.setYRot(180.0F);
                    entityarmorstand.setNoGravity(true);
                    EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
                    int j1 = aenumitemslot.length;

                    for (int k1 = 0; k1 < j1; ++k1) {
                        EnumItemSlot enumitemslot = aenumitemslot[k1];
                        Item item = (Item) SpawnArmorTrimsCommand.MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of(enumarmormaterial, enumitemslot));

                        if (item != null) {
                            ItemStack itemstack = new ItemStack(item);

                            ArmorTrim.setTrim(world.registryAccess(), itemstack, armortrim);
                            entityarmorstand.setItemSlot(enumitemslot, itemstack);
                            if (item instanceof ItemArmor) {
                                ItemArmor itemarmor = (ItemArmor) item;

                                if (itemarmor.getMaterial() == EnumArmorMaterial.TURTLE) {
                                    entityarmorstand.setCustomName(((TrimPattern) armortrim.pattern().value()).copyWithStyle(armortrim.material()).copy().append(" ").append(((TrimMaterial) armortrim.material().value()).description()));
                                    entityarmorstand.setCustomNameVisible(true);
                                    continue;
                                }
                            }

                            entityarmorstand.setInvisible(true);
                        }
                    }

                    world.addFreshEntity(entityarmorstand);
                    ++k;
                }
            }
        }

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.literal("Armorstands with trimmed armor spawned around you"), true);
        return 1;
    }
}
