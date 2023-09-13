package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MaterialMapColor;
import net.minecraft.world.level.saveddata.maps.WorldMap;

public class ItemWorldMap extends ItemWorldMapBase {

    public static final int IMAGE_WIDTH = 128;
    public static final int IMAGE_HEIGHT = 128;
    private static final int DEFAULT_MAP_COLOR = -12173266;
    private static final String TAG_MAP = "map";

    public ItemWorldMap(Item.Info item_info) {
        super(item_info);
    }

    public static ItemStack createFilledMapView(World world, int i, int j, byte b0, boolean flag, boolean flag1) {
        ItemStack itemstack = new ItemStack(Items.FILLED_MAP);

        a(itemstack, world, i, j, b0, flag, flag1, world.getDimensionKey());
        return itemstack;
    }

    @Nullable
    public static WorldMap a(@Nullable Integer integer, World world) {
        return integer == null ? null : world.a(a(integer));
    }

    @Nullable
    public static WorldMap getSavedMap(ItemStack itemstack, World world) {
        Integer integer = d(itemstack);

        return a(integer, world);
    }

    @Nullable
    public static Integer d(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("map", 99) ? nbttagcompound.getInt("map") : null;
    }

    public static int createNewSavedData(World world, int i, int j, int k, boolean flag, boolean flag1, ResourceKey<World> resourcekey) {
        WorldMap worldmap = WorldMap.a((double) i, (double) j, (byte) k, flag, flag1, resourcekey);
        int l = world.getWorldMapCount();

        world.a(a(l), worldmap);
        return l;
    }

    private static void a(ItemStack itemstack, int i) {
        itemstack.getOrCreateTag().setInt("map", i);
    }

    private static void a(ItemStack itemstack, World world, int i, int j, int k, boolean flag, boolean flag1, ResourceKey<World> resourcekey) {
        int l = createNewSavedData(world, i, j, k, flag, flag1, resourcekey);

        a(itemstack, l);
    }

    public static String a(int i) {
        return "map_" + i;
    }

    public void a(World world, Entity entity, WorldMap worldmap) {
        if (world.getDimensionKey() == worldmap.dimension && entity instanceof EntityHuman) {
            int i = 1 << worldmap.scale;
            int j = worldmap.x;
            int k = worldmap.z;
            int l = MathHelper.floor(entity.locX() - (double) j) / i + 64;
            int i1 = MathHelper.floor(entity.locZ() - (double) k) / i + 64;
            int j1 = 128 / i;

            if (world.getDimensionManager().hasCeiling()) {
                j1 /= 2;
            }

            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = worldmap.a((EntityHuman) entity);

            ++worldmap_worldmaphumantracker.step;
            boolean flag = false;

            for (int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
                if ((k1 & 15) == (worldmap_worldmaphumantracker.step & 15) || flag) {
                    flag = false;
                    double d0 = 0.0D;

                    for (int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                        if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                            int i2 = k1 - l;
                            int j2 = l1 - i1;
                            boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                            int k2 = (j / i + k1 - 64) * i;
                            int l2 = (k / i + l1 - 64) * i;
                            Multiset<MaterialMapColor> multiset = LinkedHashMultiset.create();
                            Chunk chunk = world.getChunkAtWorldCoords(new BlockPosition(k2, 0, l2));

                            if (!chunk.isEmpty()) {
                                ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
                                int i3 = k2 & 15;
                                int j3 = l2 & 15;
                                int k3 = 0;
                                double d1 = 0.0D;

                                if (world.getDimensionManager().hasCeiling()) {
                                    int l3 = k2 + l2 * 231871;

                                    l3 = l3 * l3 * 31287121 + l3 * 11;
                                    if ((l3 >> 20 & 1) == 0) {
                                        multiset.add(Blocks.DIRT.getBlockData().d(world, BlockPosition.ZERO), 10);
                                    } else {
                                        multiset.add(Blocks.STONE.getBlockData().d(world, BlockPosition.ZERO), 100);
                                    }

                                    d1 = 100.0D;
                                } else {
                                    BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                                    BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                                    for (int i4 = 0; i4 < i; ++i4) {
                                        for (int j4 = 0; j4 < i; ++j4) {
                                            int k4 = chunk.getHighestBlock(HeightMap.Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1;
                                            IBlockData iblockdata;

                                            if (k4 > world.getMinBuildHeight() + 1) {
                                                do {
                                                    --k4;
                                                    blockposition_mutableblockposition.d(chunkcoordintpair.d() + i4 + i3, k4, chunkcoordintpair.e() + j4 + j3);
                                                    iblockdata = chunk.getType(blockposition_mutableblockposition);
                                                } while (iblockdata.d(world, blockposition_mutableblockposition) == MaterialMapColor.NONE && k4 > world.getMinBuildHeight());

                                                if (k4 > world.getMinBuildHeight() && !iblockdata.getFluid().isEmpty()) {
                                                    int l4 = k4 - 1;

                                                    blockposition_mutableblockposition1.g(blockposition_mutableblockposition);

                                                    IBlockData iblockdata1;

                                                    do {
                                                        blockposition_mutableblockposition1.t(l4--);
                                                        iblockdata1 = chunk.getType(blockposition_mutableblockposition1);
                                                        ++k3;
                                                    } while (l4 > world.getMinBuildHeight() && !iblockdata1.getFluid().isEmpty());

                                                    iblockdata = this.a(world, iblockdata, (BlockPosition) blockposition_mutableblockposition);
                                                }
                                            } else {
                                                iblockdata = Blocks.BEDROCK.getBlockData();
                                            }

                                            worldmap.a(world, chunkcoordintpair.d() + i4 + i3, chunkcoordintpair.e() + j4 + j3);
                                            d1 += (double) k4 / (double) (i * i);
                                            multiset.add(iblockdata.d(world, blockposition_mutableblockposition));
                                        }
                                    }
                                }

                                k3 /= i * i;
                                double d2 = (d1 - d0) * 4.0D / (double) (i + 4) + ((double) (k1 + l1 & 1) - 0.5D) * 0.4D;
                                byte b0 = 1;

                                if (d2 > 0.6D) {
                                    b0 = 2;
                                }

                                if (d2 < -0.6D) {
                                    b0 = 0;
                                }

                                MaterialMapColor materialmapcolor = (MaterialMapColor) Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialMapColor.NONE);

                                if (materialmapcolor == MaterialMapColor.WATER) {
                                    d2 = (double) k3 * 0.1D + (double) (k1 + l1 & 1) * 0.2D;
                                    b0 = 1;
                                    if (d2 < 0.5D) {
                                        b0 = 2;
                                    }

                                    if (d2 > 0.9D) {
                                        b0 = 0;
                                    }
                                }

                                d0 = d1;
                                if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                                    flag |= worldmap.a(k1, l1, (byte) (materialmapcolor.id * 4 + b0));
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private IBlockData a(World world, IBlockData iblockdata, BlockPosition blockposition) {
        Fluid fluid = iblockdata.getFluid();

        return !fluid.isEmpty() && !iblockdata.d(world, blockposition, EnumDirection.UP) ? fluid.getBlockData() : iblockdata;
    }

    private static boolean a(BiomeBase[] abiomebase, int i, int j, int k) {
        return abiomebase[j * i + k * i * 128 * i].h() >= 0.0F;
    }

    public static void applySepiaFilter(WorldServer worldserver, ItemStack itemstack) {
        WorldMap worldmap = getSavedMap(itemstack, worldserver);

        if (worldmap != null) {
            if (worldserver.getDimensionKey() == worldmap.dimension) {
                int i = 1 << worldmap.scale;
                int j = worldmap.x;
                int k = worldmap.z;
                BiomeBase[] abiomebase = new BiomeBase[128 * i * 128 * i];

                int l;
                int i1;

                for (l = 0; l < 128 * i; ++l) {
                    for (i1 = 0; i1 < 128 * i; ++i1) {
                        abiomebase[l * 128 * i + i1] = worldserver.getBiome(new BlockPosition((j / i - 64) * i + i1, 0, (k / i - 64) * i + l));
                    }
                }

                for (l = 0; l < 128; ++l) {
                    for (i1 = 0; i1 < 128; ++i1) {
                        if (l > 0 && i1 > 0 && l < 127 && i1 < 127) {
                            BiomeBase biomebase = abiomebase[l * i + i1 * i * 128 * i];
                            int j1 = 8;

                            if (a(abiomebase, i, l - 1, i1 - 1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l - 1, i1 + 1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l - 1, i1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l + 1, i1 - 1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l + 1, i1 + 1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l + 1, i1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l, i1 - 1)) {
                                --j1;
                            }

                            if (a(abiomebase, i, l, i1 + 1)) {
                                --j1;
                            }

                            int k1 = 3;
                            MaterialMapColor materialmapcolor = MaterialMapColor.NONE;

                            if (biomebase.h() < 0.0F) {
                                materialmapcolor = MaterialMapColor.COLOR_ORANGE;
                                if (j1 > 7 && i1 % 2 == 0) {
                                    k1 = (l + (int) (MathHelper.sin((float) i1 + 0.0F) * 7.0F)) / 8 % 5;
                                    if (k1 == 3) {
                                        k1 = 1;
                                    } else if (k1 == 4) {
                                        k1 = 0;
                                    }
                                } else if (j1 > 7) {
                                    materialmapcolor = MaterialMapColor.NONE;
                                } else if (j1 > 5) {
                                    k1 = 1;
                                } else if (j1 > 3) {
                                    k1 = 0;
                                } else if (j1 > 1) {
                                    k1 = 0;
                                }
                            } else if (j1 > 0) {
                                materialmapcolor = MaterialMapColor.COLOR_BROWN;
                                if (j1 > 3) {
                                    k1 = 1;
                                } else {
                                    k1 = 3;
                                }
                            }

                            if (materialmapcolor != MaterialMapColor.NONE) {
                                worldmap.b(l, i1, (byte) (materialmapcolor.id * 4 + k1));
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (!world.isClientSide) {
            WorldMap worldmap = getSavedMap(itemstack, world);

            if (worldmap != null) {
                if (entity instanceof EntityHuman) {
                    EntityHuman entityhuman = (EntityHuman) entity;

                    worldmap.a(entityhuman, itemstack);
                }

                if (!worldmap.locked && (flag || entity instanceof EntityHuman && ((EntityHuman) entity).getItemInOffHand() == itemstack)) {
                    this.a(world, entity, worldmap);
                }

            }
        }
    }

    @Nullable
    @Override
    public Packet<?> a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        Integer integer = d(itemstack);
        WorldMap worldmap = a(integer, world);

        return worldmap != null ? worldmap.a(integer, entityhuman) : null;
    }

    @Override
    public void b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("map_scale_direction", 99)) {
            a(itemstack, world, nbttagcompound.getInt("map_scale_direction"));
            nbttagcompound.remove("map_scale_direction");
        } else if (nbttagcompound != null && nbttagcompound.hasKeyOfType("map_to_lock", 1) && nbttagcompound.getBoolean("map_to_lock")) {
            a(world, itemstack);
            nbttagcompound.remove("map_to_lock");
        }

    }

    private static void a(ItemStack itemstack, World world, int i) {
        WorldMap worldmap = getSavedMap(itemstack, world);

        if (worldmap != null) {
            int j = world.getWorldMapCount();

            world.a(a(j), worldmap.a(i));
            a(itemstack, j);
        }

    }

    public static void a(World world, ItemStack itemstack) {
        WorldMap worldmap = getSavedMap(itemstack, world);

        if (worldmap != null) {
            int i = world.getWorldMapCount();
            String s = a(i);
            WorldMap worldmap1 = worldmap.a();

            world.a(s, worldmap1);
            a(itemstack, i);
        }

    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        Integer integer = d(itemstack);
        WorldMap worldmap = world == null ? null : a(integer, world);

        if (worldmap != null && worldmap.locked) {
            list.add((new ChatMessage("filled_map.locked", new Object[]{integer})).a(EnumChatFormat.GRAY));
        }

        if (tooltipflag.a()) {
            if (worldmap != null) {
                list.add((new ChatMessage("filled_map.id", new Object[]{integer})).a(EnumChatFormat.GRAY));
                list.add((new ChatMessage("filled_map.scale", new Object[]{1 << worldmap.scale})).a(EnumChatFormat.GRAY));
                list.add((new ChatMessage("filled_map.level", new Object[]{worldmap.scale, 4})).a(EnumChatFormat.GRAY));
            } else {
                list.add((new ChatMessage("filled_map.unknown")).a(EnumChatFormat.GRAY));
            }
        }

    }

    public static int k(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("display");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("MapColor", 99)) {
            int i = nbttagcompound.getInt("MapColor");

            return -16777216 | i & 16777215;
        } else {
            return -12173266;
        }
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        IBlockData iblockdata = itemactioncontext.getWorld().getType(itemactioncontext.getClickPosition());

        if (iblockdata.a((Tag) TagsBlock.BANNERS)) {
            if (!itemactioncontext.getWorld().isClientSide) {
                WorldMap worldmap = getSavedMap(itemactioncontext.getItemStack(), itemactioncontext.getWorld());

                if (worldmap != null && !worldmap.a((GeneratorAccess) itemactioncontext.getWorld(), itemactioncontext.getClickPosition())) {
                    return EnumInteractionResult.FAIL;
                }
            }

            return EnumInteractionResult.a(itemactioncontext.getWorld().isClientSide);
        } else {
            return super.a(itemactioncontext);
        }
    }
}
