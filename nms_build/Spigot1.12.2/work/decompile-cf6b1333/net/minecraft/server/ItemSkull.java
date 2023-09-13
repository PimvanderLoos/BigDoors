package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class ItemSkull extends Item {

    private static final String[] a = new String[] { "skeleton", "wither", "zombie", "char", "creeper", "dragon"};

    public ItemSkull() {
        this.b(CreativeModeTab.c);
        this.setMaxDurability(0);
        this.a(true);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection == EnumDirection.DOWN) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();
            boolean flag = block.a((IBlockAccess) world, blockposition);

            if (!flag) {
                if (!world.getType(blockposition).getMaterial().isBuildable()) {
                    return EnumInteractionResult.FAIL;
                }

                blockposition = blockposition.shift(enumdirection);
            }

            ItemStack itemstack = entityhuman.b(enumhand);

            if (entityhuman.a(blockposition, enumdirection, itemstack) && Blocks.SKULL.canPlace(world, blockposition)) {
                if (world.isClientSide) {
                    return EnumInteractionResult.SUCCESS;
                } else {
                    world.setTypeAndData(blockposition, Blocks.SKULL.getBlockData().set(BlockSkull.FACING, enumdirection), 11);
                    int i = 0;

                    if (enumdirection == EnumDirection.UP) {
                        i = MathHelper.floor((double) (entityhuman.yaw * 16.0F / 360.0F) + 0.5D) & 15;
                    }

                    TileEntity tileentity = world.getTileEntity(blockposition);

                    if (tileentity instanceof TileEntitySkull) {
                        TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;

                        if (itemstack.getData() == 3) {
                            GameProfile gameprofile = null;

                            if (itemstack.hasTag()) {
                                NBTTagCompound nbttagcompound = itemstack.getTag();

                                if (nbttagcompound.hasKeyOfType("SkullOwner", 10)) {
                                    gameprofile = GameProfileSerializer.deserialize(nbttagcompound.getCompound("SkullOwner"));
                                } else if (nbttagcompound.hasKeyOfType("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
                                    gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));
                                }
                            }

                            tileentityskull.setGameProfile(gameprofile);
                        } else {
                            tileentityskull.setSkullType(itemstack.getData());
                        }

                        tileentityskull.setRotation(i);
                        Blocks.SKULL.a(world, blockposition, tileentityskull);
                    }

                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition, itemstack);
                    }

                    itemstack.subtract(1);
                    return EnumInteractionResult.SUCCESS;
                }
            } else {
                return EnumInteractionResult.FAIL;
            }
        }
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            for (int i = 0; i < ItemSkull.a.length; ++i) {
                nonnulllist.add(new ItemStack(this, 1, i));
            }
        }

    }

    public int filterData(int i) {
        return i;
    }

    public String a(ItemStack itemstack) {
        int i = itemstack.getData();

        if (i < 0 || i >= ItemSkull.a.length) {
            i = 0;
        }

        return super.getName() + "." + ItemSkull.a[i];
    }

    public String b(ItemStack itemstack) {
        if (itemstack.getData() == 3 && itemstack.hasTag()) {
            if (itemstack.getTag().hasKeyOfType("SkullOwner", 8)) {
                return LocaleI18n.a("item.skull.player.name", new Object[] { itemstack.getTag().getString("SkullOwner")});
            }

            if (itemstack.getTag().hasKeyOfType("SkullOwner", 10)) {
                NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("SkullOwner");

                if (nbttagcompound.hasKeyOfType("Name", 8)) {
                    return LocaleI18n.a("item.skull.player.name", new Object[] { nbttagcompound.getString("Name")});
                }
            }
        }

        return super.b(itemstack);
    }

    public boolean a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
            GameProfile gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));

            gameprofile = TileEntitySkull.b(gameprofile);
            nbttagcompound.set("SkullOwner", GameProfileSerializer.serialize(new NBTTagCompound(), gameprofile));
            return true;
        } else {
            return false;
        }
    }
}
