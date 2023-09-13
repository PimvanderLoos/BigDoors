package net.minecraft.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.Random;
import javax.annotation.Nullable;

public final class ItemStack {

    public static final ItemStack a = new ItemStack((Item) null);
    public static final DecimalFormat b = new DecimalFormat("#.##");
    private int count;
    private int d;
    private Item item;
    private NBTTagCompound tag;
    private boolean g;
    private int damage;
    private EntityItemFrame i;
    private Block j;
    private boolean k;
    private Block l;
    private boolean m;

    public ItemStack(Block block) {
        this(block, 1);
    }

    public ItemStack(Block block, int i) {
        this(block, i, 0);
    }

    public ItemStack(Block block, int i, int j) {
        this(Item.getItemOf(block), i, j);
    }

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int i) {
        this(item, i, 0);
    }

    public ItemStack(Item item, int i, int j) {
        this.item = item;
        this.damage = j;
        this.count = i;
        if (this.damage < 0) {
            this.damage = 0;
        }

        this.F();
    }

    private void F() {
        this.g = this.isEmpty();
    }

    public ItemStack(NBTTagCompound nbttagcompound) {
        this.item = Item.b(nbttagcompound.getString("id"));
        this.count = nbttagcompound.getByte("Count");
        this.damage = Math.max(0, nbttagcompound.getShort("Damage"));
        if (nbttagcompound.hasKeyOfType("tag", 10)) {
            this.tag = nbttagcompound.getCompound("tag");
            if (this.item != null) {
                this.item.a(nbttagcompound);
            }
        }

        this.F();
    }

    public boolean isEmpty() {
        return this == ItemStack.a ? true : (this.item != null && this.item != Item.getItemOf(Blocks.AIR) ? (this.count <= 0 ? true : this.damage < -32768 || this.damage > '\uffff') : true);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.ITEM_INSTANCE, (DataInspector) (new DataInspectorBlockEntity()));
        dataconvertermanager.a(DataConverterTypes.ITEM_INSTANCE, (DataInspector) (new DataInspectorEntity()));
    }

    public ItemStack cloneAndSubtract(int i) {
        int j = Math.min(i, this.count);
        ItemStack itemstack = this.cloneItemStack();

        itemstack.setCount(j);
        this.subtract(j);
        return itemstack;
    }

    public Item getItem() {
        return this.g ? Item.getItemOf(Blocks.AIR) : this.item;
    }

    public EnumInteractionResult placeItem(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        EnumInteractionResult enuminteractionresult = this.getItem().a(entityhuman, world, blockposition, enumhand, enumdirection, f, f1, f2);

        if (enuminteractionresult == EnumInteractionResult.SUCCESS) {
            entityhuman.b(StatisticList.b(this.item));
        }

        return enuminteractionresult;
    }

    public float a(IBlockData iblockdata) {
        return this.getItem().getDestroySpeed(this, iblockdata);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return this.getItem().a(world, entityhuman, enumhand);
    }

    public ItemStack a(World world, EntityLiving entityliving) {
        return this.getItem().a(this, world, entityliving);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = (MinecraftKey) Item.REGISTRY.b(this.item);

        nbttagcompound.setString("id", minecraftkey == null ? "minecraft:air" : minecraftkey.toString());
        nbttagcompound.setByte("Count", (byte) this.count);
        nbttagcompound.setShort("Damage", (short) this.damage);
        if (this.tag != null) {
            nbttagcompound.set("tag", this.tag);
        }

        return nbttagcompound;
    }

    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.f() || !this.h());
    }

    public boolean f() {
        return this.g ? false : (this.item.getMaxDurability() <= 0 ? false : !this.hasTag() || !this.getTag().getBoolean("Unbreakable"));
    }

    public boolean usesData() {
        return this.getItem().k();
    }

    public boolean h() {
        return this.f() && this.damage > 0;
    }

    public int i() {
        return this.damage;
    }

    public int getData() {
        return this.damage;
    }

    public void setData(int i) {
        this.damage = i;
        if (this.damage < 0) {
            this.damage = 0;
        }

    }

    public int k() {
        return this.getItem().getMaxDurability();
    }

    public boolean isDamaged(int i, Random random, @Nullable EntityPlayer entityplayer) {
        if (!this.f()) {
            return false;
        } else {
            if (i > 0) {
                int j = EnchantmentManager.getEnchantmentLevel(Enchantments.DURABILITY, this);
                int k = 0;

                for (int l = 0; j > 0 && l < i; ++l) {
                    if (EnchantmentDurability.a(this, j, random)) {
                        ++k;
                    }
                }

                i -= k;
                if (i <= 0) {
                    return false;
                }
            }

            if (entityplayer != null && i != 0) {
                CriterionTriggers.s.a(entityplayer, this, this.damage + i);
            }

            this.damage += i;
            return this.damage > this.k();
        }
    }

    public void damage(int i, EntityLiving entityliving) {
        if (!(entityliving instanceof EntityHuman) || !((EntityHuman) entityliving).abilities.canInstantlyBuild) {
            if (this.f()) {
                if (this.isDamaged(i, entityliving.getRandom(), entityliving instanceof EntityPlayer ? (EntityPlayer) entityliving : null)) {
                    entityliving.b(this);
                    this.subtract(1);
                    if (entityliving instanceof EntityHuman) {
                        EntityHuman entityhuman = (EntityHuman) entityliving;

                        entityhuman.b(StatisticList.c(this.item));
                    }

                    this.damage = 0;
                }

            }
        }
    }

    public void a(EntityLiving entityliving, EntityHuman entityhuman) {
        boolean flag = this.item.a(this, entityliving, (EntityLiving) entityhuman);

        if (flag) {
            entityhuman.b(StatisticList.b(this.item));
        }

    }

    public void a(World world, IBlockData iblockdata, BlockPosition blockposition, EntityHuman entityhuman) {
        boolean flag = this.getItem().a(this, world, iblockdata, blockposition, entityhuman);

        if (flag) {
            entityhuman.b(StatisticList.b(this.item));
        }

    }

    public boolean b(IBlockData iblockdata) {
        return this.getItem().canDestroySpecialBlock(iblockdata);
    }

    public boolean a(EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        return this.getItem().a(this, entityhuman, entityliving, enumhand);
    }

    public ItemStack cloneItemStack() {
        ItemStack itemstack = new ItemStack(this.item, this.count, this.damage);

        itemstack.d(this.D());
        if (this.tag != null) {
            itemstack.tag = this.tag.g();
        }

        return itemstack;
    }

    public static boolean equals(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.isEmpty() && itemstack1.isEmpty() ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? (itemstack.tag == null && itemstack1.tag != null ? false : itemstack.tag == null || itemstack.tag.equals(itemstack1.tag)) : false);
    }

    public static boolean matches(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.isEmpty() && itemstack1.isEmpty() ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.d(itemstack1) : false);
    }

    private boolean d(ItemStack itemstack) {
        return this.count != itemstack.count ? false : (this.getItem() != itemstack.getItem() ? false : (this.damage != itemstack.damage ? false : (this.tag == null && itemstack.tag != null ? false : this.tag == null || this.tag.equals(itemstack.tag))));
    }

    public static boolean c(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == itemstack1 ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.doMaterialsMatch(itemstack1) : false);
    }

    public static boolean d(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == itemstack1 ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.b(itemstack1) : false);
    }

    public boolean doMaterialsMatch(ItemStack itemstack) {
        return !itemstack.isEmpty() && this.item == itemstack.item && this.damage == itemstack.damage;
    }

    public boolean b(ItemStack itemstack) {
        return !this.f() ? this.doMaterialsMatch(itemstack) : !itemstack.isEmpty() && this.item == itemstack.item;
    }

    public String a() {
        return this.getItem().a(this);
    }

    public String toString() {
        return this.count + "x" + this.getItem().getName() + "@" + this.damage;
    }

    public void a(World world, Entity entity, int i, boolean flag) {
        if (this.d > 0) {
            --this.d;
        }

        if (this.item != null) {
            this.item.a(this, world, entity, i, flag);
        }

    }

    public void a(World world, EntityHuman entityhuman, int i) {
        entityhuman.a(StatisticList.a(this.item), i);
        this.getItem().b(this, world, entityhuman);
    }

    public int m() {
        return this.getItem().e(this);
    }

    public EnumAnimation n() {
        return this.getItem().f(this);
    }

    public void a(World world, EntityLiving entityliving, int i) {
        this.getItem().a(this, world, entityliving, i);
    }

    public boolean hasTag() {
        return !this.g && this.tag != null;
    }

    @Nullable
    public NBTTagCompound getTag() {
        return this.tag;
    }

    public NBTTagCompound c(String s) {
        if (this.tag != null && this.tag.hasKeyOfType(s, 10)) {
            return this.tag.getCompound(s);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            this.a(s, (NBTBase) nbttagcompound);
            return nbttagcompound;
        }
    }

    @Nullable
    public NBTTagCompound d(String s) {
        return this.tag != null && this.tag.hasKeyOfType(s, 10) ? this.tag.getCompound(s) : null;
    }

    public void e(String s) {
        if (this.tag != null && this.tag.hasKeyOfType(s, 10)) {
            this.tag.remove(s);
        }

    }

    public NBTTagList getEnchantments() {
        return this.tag != null ? this.tag.getList("ench", 10) : new NBTTagList();
    }

    public void setTag(@Nullable NBTTagCompound nbttagcompound) {
        this.tag = nbttagcompound;
    }

    public String getName() {
        NBTTagCompound nbttagcompound = this.d("display");

        if (nbttagcompound != null) {
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return nbttagcompound.getString("Name");
            }

            if (nbttagcompound.hasKeyOfType("LocName", 8)) {
                return LocaleI18n.get(nbttagcompound.getString("LocName"));
            }
        }

        return this.getItem().b(this);
    }

    public ItemStack f(String s) {
        this.c("display").setString("LocName", s);
        return this;
    }

    public ItemStack g(String s) {
        this.c("display").setString("Name", s);
        return this;
    }

    public void s() {
        NBTTagCompound nbttagcompound = this.d("display");

        if (nbttagcompound != null) {
            nbttagcompound.remove("Name");
            if (nbttagcompound.isEmpty()) {
                this.e("display");
            }
        }

        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }

    }

    public boolean hasName() {
        NBTTagCompound nbttagcompound = this.d("display");

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("Name", 8);
    }

    public EnumItemRarity v() {
        return this.getItem().g(this);
    }

    public boolean canEnchant() {
        return !this.getItem().g_(this) ? false : !this.hasEnchantments();
    }

    public void addEnchantment(Enchantment enchantment, int i) {
        if (this.tag == null) {
            this.setTag(new NBTTagCompound());
        }

        if (!this.tag.hasKeyOfType("ench", 9)) {
            this.tag.set("ench", new NBTTagList());
        }

        NBTTagList nbttaglist = this.tag.getList("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setShort("id", (short) Enchantment.getId(enchantment));
        nbttagcompound.setShort("lvl", (short) ((byte) i));
        nbttaglist.add(nbttagcompound);
    }

    public boolean hasEnchantments() {
        return this.tag != null && this.tag.hasKeyOfType("ench", 9) ? !this.tag.getList("ench", 10).isEmpty() : false;
    }

    public void a(String s, NBTBase nbtbase) {
        if (this.tag == null) {
            this.setTag(new NBTTagCompound());
        }

        this.tag.set(s, nbtbase);
    }

    public boolean y() {
        return this.getItem().s();
    }

    public boolean z() {
        return this.i != null;
    }

    public void a(EntityItemFrame entityitemframe) {
        this.i = entityitemframe;
    }

    @Nullable
    public EntityItemFrame A() {
        return this.g ? null : this.i;
    }

    public int getRepairCost() {
        return this.hasTag() && this.tag.hasKeyOfType("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
    }

    public void setRepairCost(int i) {
        if (!this.hasTag()) {
            this.tag = new NBTTagCompound();
        }

        this.tag.setInt("RepairCost", i);
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Object object;

        if (this.hasTag() && this.tag.hasKeyOfType("AttributeModifiers", 9)) {
            object = HashMultimap.create();
            NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.get(i);
                AttributeModifier attributemodifier = GenericAttributes.a(nbttagcompound);

                if (attributemodifier != null && (!nbttagcompound.hasKeyOfType("Slot", 8) || nbttagcompound.getString("Slot").equals(enumitemslot.d())) && attributemodifier.a().getLeastSignificantBits() != 0L && attributemodifier.a().getMostSignificantBits() != 0L) {
                    ((Multimap) object).put(nbttagcompound.getString("AttributeName"), attributemodifier);
                }
            }
        } else {
            object = this.getItem().a(enumitemslot);
        }

        return (Multimap) object;
    }

    public void a(String s, AttributeModifier attributemodifier, @Nullable EnumItemSlot enumitemslot) {
        if (this.tag == null) {
            this.tag = new NBTTagCompound();
        }

        if (!this.tag.hasKeyOfType("AttributeModifiers", 9)) {
            this.tag.set("AttributeModifiers", new NBTTagList());
        }

        NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);
        NBTTagCompound nbttagcompound = GenericAttributes.a(attributemodifier);

        nbttagcompound.setString("AttributeName", s);
        if (enumitemslot != null) {
            nbttagcompound.setString("Slot", enumitemslot.d());
        }

        nbttaglist.add(nbttagcompound);
    }

    public IChatBaseComponent C() {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.getName());

        if (this.hasName()) {
            chatcomponenttext.getChatModifier().setItalic(Boolean.valueOf(true));
        }

        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("[")).addSibling(chatcomponenttext).a("]");

        if (!this.g) {
            NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

            ichatbasecomponent.getChatModifier().setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ITEM, new ChatComponentText(nbttagcompound.toString())));
            ichatbasecomponent.getChatModifier().setColor(this.v().e);
        }

        return ichatbasecomponent;
    }

    public boolean a(Block block) {
        if (block == this.j) {
            return this.k;
        } else {
            this.j = block;
            if (this.hasTag() && this.tag.hasKeyOfType("CanDestroy", 9)) {
                NBTTagList nbttaglist = this.tag.getList("CanDestroy", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    Block block1 = Block.getByName(nbttaglist.getString(i));

                    if (block1 == block) {
                        this.k = true;
                        return true;
                    }
                }
            }

            this.k = false;
            return false;
        }
    }

    public boolean b(Block block) {
        if (block == this.l) {
            return this.m;
        } else {
            this.l = block;
            if (this.hasTag() && this.tag.hasKeyOfType("CanPlaceOn", 9)) {
                NBTTagList nbttaglist = this.tag.getList("CanPlaceOn", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    Block block1 = Block.getByName(nbttaglist.getString(i));

                    if (block1 == block) {
                        this.m = true;
                        return true;
                    }
                }
            }

            this.m = false;
            return false;
        }
    }

    public int D() {
        return this.d;
    }

    public void d(int i) {
        this.d = i;
    }

    public int getCount() {
        return this.g ? 0 : this.count;
    }

    public void setCount(int i) {
        this.count = i;
        this.F();
    }

    public void add(int i) {
        this.setCount(this.count + i);
    }

    public void subtract(int i) {
        this.add(-i);
    }
}
