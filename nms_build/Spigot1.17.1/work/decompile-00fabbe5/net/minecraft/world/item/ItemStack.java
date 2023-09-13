package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.commands.arguments.blocks.ArgumentBlockPredicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.ITagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentDurability;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {

    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IRegistry.ITEM.fieldOf("id").forGetter((itemstack) -> {
            return itemstack.item;
        }), Codec.INT.fieldOf("Count").forGetter((itemstack) -> {
            return itemstack.count;
        }), NBTTagCompound.CODEC.optionalFieldOf("tag").forGetter((itemstack) -> {
            return Optional.ofNullable(itemstack.tag);
        })).apply(instance, ItemStack::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ItemStack EMPTY = new ItemStack((Item) null);
    public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = (DecimalFormat) SystemUtils.a((Object) (new DecimalFormat("#.##")), (decimalformat) -> {
        decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });
    public static final String TAG_ENCH = "Enchantments";
    public static final String TAG_DISPLAY = "display";
    public static final String TAG_DISPLAY_NAME = "Name";
    public static final String TAG_LORE = "Lore";
    public static final String TAG_DAMAGE = "Damage";
    public static final String TAG_COLOR = "color";
    private static final String TAG_UNBREAKABLE = "Unbreakable";
    private static final String TAG_REPAIR_COST = "RepairCost";
    private static final String TAG_CAN_DESTROY_BLOCK_LIST = "CanDestroy";
    private static final String TAG_CAN_PLACE_ON_BLOCK_LIST = "CanPlaceOn";
    private static final String TAG_HIDE_FLAGS = "HideFlags";
    private static final int DONT_HIDE_TOOLTIP = 0;
    private static final ChatModifier LORE_STYLE = ChatModifier.EMPTY.setColor(EnumChatFormat.DARK_PURPLE).setItalic(true);
    private int count;
    private int popTime;
    @Deprecated
    private Item item;
    private NBTTagCompound tag;
    private boolean emptyCacheFlag;
    private Entity entityRepresentation;
    private ShapeDetectorBlock cachedBreakBlock;
    private boolean cachedBreakBlockResult;
    private ShapeDetectorBlock cachedPlaceBlock;
    private boolean cachedPlaceBlockResult;

    public Optional<TooltipComponent> a() {
        return this.getItem().h(this);
    }

    public ItemStack(IMaterial imaterial) {
        this(imaterial, 1);
    }

    private ItemStack(IMaterial imaterial, int i, Optional<NBTTagCompound> optional) {
        this(imaterial, i);
        optional.ifPresent(this::setTag);
    }

    public ItemStack(IMaterial imaterial, int i) {
        this.item = imaterial == null ? null : imaterial.getItem();
        this.count = i;
        if (this.item != null && this.item.usesDurability()) {
            this.setDamage(this.getDamage());
        }

        this.checkEmpty();
    }

    private void checkEmpty() {
        this.emptyCacheFlag = false;
        this.emptyCacheFlag = this.isEmpty();
    }

    private ItemStack(NBTTagCompound nbttagcompound) {
        this.item = (Item) IRegistry.ITEM.get(new MinecraftKey(nbttagcompound.getString("id")));
        this.count = nbttagcompound.getByte("Count");
        if (nbttagcompound.hasKeyOfType("tag", 10)) {
            this.tag = nbttagcompound.getCompound("tag");
            this.getItem().b(this.tag);
        }

        if (this.getItem().usesDurability()) {
            this.setDamage(this.getDamage());
        }

        this.checkEmpty();
    }

    public static ItemStack a(NBTTagCompound nbttagcompound) {
        try {
            return new ItemStack(nbttagcompound);
        } catch (RuntimeException runtimeexception) {
            ItemStack.LOGGER.debug("Tried to load invalid item: {}", nbttagcompound, runtimeexception);
            return ItemStack.EMPTY;
        }
    }

    public boolean isEmpty() {
        return this == ItemStack.EMPTY ? true : (this.getItem() != null && !this.a(Items.AIR) ? this.count <= 0 : true);
    }

    public ItemStack cloneAndSubtract(int i) {
        int j = Math.min(i, this.count);
        ItemStack itemstack = this.cloneItemStack();

        itemstack.setCount(j);
        this.subtract(j);
        return itemstack;
    }

    public Item getItem() {
        return this.emptyCacheFlag ? Items.AIR : this.item;
    }

    public boolean a(Tag<Item> tag) {
        return tag.isTagged(this.getItem());
    }

    public boolean a(Item item) {
        return this.getItem() == item;
    }

    public EnumInteractionResult placeItem(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getEntity();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(itemactioncontext.getWorld(), blockposition, false);

        if (entityhuman != null && !entityhuman.getAbilities().mayBuild && !this.b(itemactioncontext.getWorld().r(), shapedetectorblock)) {
            return EnumInteractionResult.PASS;
        } else {
            Item item = this.getItem();
            EnumInteractionResult enuminteractionresult = item.a(itemactioncontext);

            if (entityhuman != null && enuminteractionresult.c()) {
                entityhuman.b(StatisticList.ITEM_USED.b(item));
            }

            return enuminteractionresult;
        }
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
        MinecraftKey minecraftkey = IRegistry.ITEM.getKey(this.getItem());

        nbttagcompound.setString("id", minecraftkey == null ? "minecraft:air" : minecraftkey.toString());
        nbttagcompound.setByte("Count", (byte) this.count);
        if (this.tag != null) {
            nbttagcompound.set("tag", this.tag.clone());
        }

        return nbttagcompound;
    }

    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.f() || !this.g());
    }

    public boolean f() {
        if (!this.emptyCacheFlag && this.getItem().getMaxDurability() > 0) {
            NBTTagCompound nbttagcompound = this.getTag();

            return nbttagcompound == null || !nbttagcompound.getBoolean("Unbreakable");
        } else {
            return false;
        }
    }

    public boolean g() {
        return this.f() && this.getDamage() > 0;
    }

    public int getDamage() {
        return this.tag == null ? 0 : this.tag.getInt("Damage");
    }

    public void setDamage(int i) {
        this.getOrCreateTag().setInt("Damage", Math.max(0, i));
    }

    public int i() {
        return this.getItem().getMaxDurability();
    }

    public boolean isDamaged(int i, Random random, @Nullable EntityPlayer entityplayer) {
        if (!this.f()) {
            return false;
        } else {
            int j;

            if (i > 0) {
                j = EnchantmentManager.getEnchantmentLevel(Enchantments.UNBREAKING, this);
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
                CriterionTriggers.ITEM_DURABILITY_CHANGED.a(entityplayer, this, this.getDamage() + i);
            }

            j = this.getDamage() + i;
            this.setDamage(j);
            return j >= this.i();
        }
    }

    public <T extends EntityLiving> void damage(int i, T t0, Consumer<T> consumer) {
        if (!t0.level.isClientSide && (!(t0 instanceof EntityHuman) || !((EntityHuman) t0).getAbilities().instabuild)) {
            if (this.f()) {
                if (this.isDamaged(i, t0.getRandom(), t0 instanceof EntityPlayer ? (EntityPlayer) t0 : null)) {
                    consumer.accept(t0);
                    Item item = this.getItem();

                    this.subtract(1);
                    if (t0 instanceof EntityHuman) {
                        ((EntityHuman) t0).b(StatisticList.ITEM_BROKEN.b(item));
                    }

                    this.setDamage(0);
                }

            }
        }
    }

    public boolean j() {
        return this.item.e(this);
    }

    public int k() {
        return this.item.f(this);
    }

    public int l() {
        return this.item.g(this);
    }

    public boolean a(Slot slot, ClickAction clickaction, EntityHuman entityhuman) {
        return this.getItem().a(this, slot, clickaction, entityhuman);
    }

    public boolean a(ItemStack itemstack, Slot slot, ClickAction clickaction, EntityHuman entityhuman, SlotAccess slotaccess) {
        return this.getItem().a(this, itemstack, slot, clickaction, entityhuman, slotaccess);
    }

    public void a(EntityLiving entityliving, EntityHuman entityhuman) {
        Item item = this.getItem();

        if (item.a(this, entityliving, (EntityLiving) entityhuman)) {
            entityhuman.b(StatisticList.ITEM_USED.b(item));
        }

    }

    public void a(World world, IBlockData iblockdata, BlockPosition blockposition, EntityHuman entityhuman) {
        Item item = this.getItem();

        if (item.a(this, world, iblockdata, blockposition, entityhuman)) {
            entityhuman.b(StatisticList.ITEM_USED.b(item));
        }

    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return this.getItem().canDestroySpecialBlock(iblockdata);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        return this.getItem().a(this, entityhuman, entityliving, enumhand);
    }

    public ItemStack cloneItemStack() {
        if (this.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = new ItemStack(this.getItem(), this.count);

            itemstack.d(this.H());
            if (this.tag != null) {
                itemstack.tag = this.tag.clone();
            }

            return itemstack;
        }
    }

    public static boolean equals(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.isEmpty() && itemstack1.isEmpty() ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? (itemstack.tag == null && itemstack1.tag != null ? false : itemstack.tag == null || itemstack.tag.equals(itemstack1.tag)) : false);
    }

    public static boolean matches(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.isEmpty() && itemstack1.isEmpty() ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.c(itemstack1) : false);
    }

    private boolean c(ItemStack itemstack) {
        return this.count != itemstack.count ? false : (!this.a(itemstack.getItem()) ? false : (this.tag == null && itemstack.tag != null ? false : this.tag == null || this.tag.equals(itemstack.tag)));
    }

    public static boolean c(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == itemstack1 ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.doMaterialsMatch(itemstack1) : false);
    }

    public static boolean d(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == itemstack1 ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.b(itemstack1) : false);
    }

    public boolean doMaterialsMatch(ItemStack itemstack) {
        return !itemstack.isEmpty() && this.a(itemstack.getItem());
    }

    public boolean b(ItemStack itemstack) {
        return !this.f() ? this.doMaterialsMatch(itemstack) : !itemstack.isEmpty() && this.a(itemstack.getItem());
    }

    public static boolean e(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.a(itemstack1.getItem()) && equals(itemstack, itemstack1);
    }

    public String n() {
        return this.getItem().j(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void a(World world, Entity entity, int i, boolean flag) {
        if (this.popTime > 0) {
            --this.popTime;
        }

        if (this.getItem() != null) {
            this.getItem().a(this, world, entity, i, flag);
        }

    }

    public void a(World world, EntityHuman entityhuman, int i) {
        entityhuman.a(StatisticList.ITEM_CRAFTED.b(this.getItem()), i);
        this.getItem().b(this, world, entityhuman);
    }

    public int o() {
        return this.getItem().b(this);
    }

    public EnumAnimation p() {
        return this.getItem().c(this);
    }

    public void a(World world, EntityLiving entityliving, int i) {
        this.getItem().a(this, world, entityliving, i);
    }

    public boolean q() {
        return this.getItem().l(this);
    }

    public boolean hasTag() {
        return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public NBTTagCompound getTag() {
        return this.tag;
    }

    public NBTTagCompound getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new NBTTagCompound());
        }

        return this.tag;
    }

    public NBTTagCompound a(String s) {
        if (this.tag != null && this.tag.hasKeyOfType(s, 10)) {
            return this.tag.getCompound(s);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            this.a(s, (NBTBase) nbttagcompound);
            return nbttagcompound;
        }
    }

    @Nullable
    public NBTTagCompound b(String s) {
        return this.tag != null && this.tag.hasKeyOfType(s, 10) ? this.tag.getCompound(s) : null;
    }

    public void removeTag(String s) {
        if (this.tag != null && this.tag.hasKey(s)) {
            this.tag.remove(s);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }

    }

    public NBTTagList getEnchantments() {
        return this.tag != null ? this.tag.getList("Enchantments", 10) : new NBTTagList();
    }

    public void setTag(@Nullable NBTTagCompound nbttagcompound) {
        this.tag = nbttagcompound;
        if (this.getItem().usesDurability()) {
            this.setDamage(this.getDamage());
        }

        if (nbttagcompound != null) {
            this.getItem().b(nbttagcompound);
        }

    }

    public IChatBaseComponent getName() {
        NBTTagCompound nbttagcompound = this.b("display");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Name", 8)) {
            try {
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("Name"));

                if (ichatmutablecomponent != null) {
                    return ichatmutablecomponent;
                }

                nbttagcompound.remove("Name");
            } catch (JsonParseException jsonparseexception) {
                nbttagcompound.remove("Name");
            }
        }

        return this.getItem().m(this);
    }

    public ItemStack a(@Nullable IChatBaseComponent ichatbasecomponent) {
        NBTTagCompound nbttagcompound = this.a("display");

        if (ichatbasecomponent != null) {
            nbttagcompound.setString("Name", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
        } else {
            nbttagcompound.remove("Name");
        }

        return this;
    }

    public void w() {
        NBTTagCompound nbttagcompound = this.b("display");

        if (nbttagcompound != null) {
            nbttagcompound.remove("Name");
            if (nbttagcompound.isEmpty()) {
                this.removeTag("display");
            }
        }

        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }

    }

    public boolean hasName() {
        NBTTagCompound nbttagcompound = this.b("display");

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("Name", 8);
    }

    public List<IChatBaseComponent> a(@Nullable EntityHuman entityhuman, TooltipFlag tooltipflag) {
        List<IChatBaseComponent> list = Lists.newArrayList();
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("")).addSibling(this.getName()).a(this.z().color);

        if (this.hasName()) {
            ichatmutablecomponent.a(EnumChatFormat.ITALIC);
        }

        list.add(ichatmutablecomponent);
        if (!tooltipflag.a() && !this.hasName() && this.a(Items.FILLED_MAP)) {
            Integer integer = ItemWorldMap.d(this);

            if (integer != null) {
                list.add((new ChatComponentText("#" + integer)).a(EnumChatFormat.GRAY));
            }
        }

        int i = this.O();

        if (a(i, ItemStack.HideFlags.ADDITIONAL)) {
            this.getItem().a(this, entityhuman == null ? null : entityhuman.level, (List) list, tooltipflag);
        }

        int j;

        if (this.hasTag()) {
            if (a(i, ItemStack.HideFlags.ENCHANTMENTS)) {
                a((List) list, this.getEnchantments());
            }

            if (this.tag.hasKeyOfType("display", 10)) {
                NBTTagCompound nbttagcompound = this.tag.getCompound("display");

                if (a(i, ItemStack.HideFlags.DYE) && nbttagcompound.hasKeyOfType("color", 99)) {
                    if (tooltipflag.a()) {
                        list.add((new ChatMessage("item.color", new Object[]{String.format("#%06X", nbttagcompound.getInt("color"))})).a(EnumChatFormat.GRAY));
                    } else {
                        list.add((new ChatMessage("item.dyed")).a(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}));
                    }
                }

                if (nbttagcompound.d("Lore") == 9) {
                    NBTTagList nbttaglist = nbttagcompound.getList("Lore", 8);

                    for (j = 0; j < nbttaglist.size(); ++j) {
                        String s = nbttaglist.getString(j);

                        try {
                            IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.ChatSerializer.a(s);

                            if (ichatmutablecomponent1 != null) {
                                list.add(ChatComponentUtils.a(ichatmutablecomponent1, ItemStack.LORE_STYLE));
                            }
                        } catch (JsonParseException jsonparseexception) {
                            nbttagcompound.remove("Lore");
                        }
                    }
                }
            }
        }

        int k;

        if (a(i, ItemStack.HideFlags.MODIFIERS)) {
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();

            k = aenumitemslot.length;

            for (j = 0; j < k; ++j) {
                EnumItemSlot enumitemslot = aenumitemslot[j];
                Multimap<AttributeBase, AttributeModifier> multimap = this.a(enumitemslot);

                if (!multimap.isEmpty()) {
                    list.add(ChatComponentText.EMPTY);
                    list.add((new ChatMessage("item.modifiers." + enumitemslot.getSlotName())).a(EnumChatFormat.GRAY));
                    Iterator iterator = multimap.entries().iterator();

                    while (iterator.hasNext()) {
                        Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator.next();
                        AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                        double d0 = attributemodifier.getAmount();
                        boolean flag = false;

                        if (entityhuman != null) {
                            if (attributemodifier.getUniqueId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                                d0 += entityhuman.c(GenericAttributes.ATTACK_DAMAGE);
                                d0 += (double) EnchantmentManager.a(this, EnumMonsterType.UNDEFINED);
                                flag = true;
                            } else if (attributemodifier.getUniqueId() == Item.BASE_ATTACK_SPEED_UUID) {
                                d0 += entityhuman.c(GenericAttributes.ATTACK_SPEED);
                                flag = true;
                            }
                        }

                        double d1;

                        if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (((AttributeBase) entry.getKey()).equals(GenericAttributes.KNOCKBACK_RESISTANCE)) {
                                d1 = d0 * 10.0D;
                            } else {
                                d1 = d0;
                            }
                        } else {
                            d1 = d0 * 100.0D;
                        }

                        if (flag) {
                            list.add((new ChatComponentText(" ")).addSibling(new ChatMessage("attribute.modifier.equals." + attributemodifier.getOperation().a(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new ChatMessage(((AttributeBase) entry.getKey()).getName())})).a(EnumChatFormat.DARK_GREEN));
                        } else if (d0 > 0.0D) {
                            list.add((new ChatMessage("attribute.modifier.plus." + attributemodifier.getOperation().a(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new ChatMessage(((AttributeBase) entry.getKey()).getName())})).a(EnumChatFormat.BLUE));
                        } else if (d0 < 0.0D) {
                            d1 *= -1.0D;
                            list.add((new ChatMessage("attribute.modifier.take." + attributemodifier.getOperation().a(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new ChatMessage(((AttributeBase) entry.getKey()).getName())})).a(EnumChatFormat.RED));
                        }
                    }
                }
            }
        }

        if (this.hasTag()) {
            if (a(i, ItemStack.HideFlags.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                list.add((new ChatMessage("item.unbreakable")).a(EnumChatFormat.BLUE));
            }

            NBTTagList nbttaglist1;

            if (a(i, ItemStack.HideFlags.CAN_DESTROY) && this.tag.hasKeyOfType("CanDestroy", 9)) {
                nbttaglist1 = this.tag.getList("CanDestroy", 8);
                if (!nbttaglist1.isEmpty()) {
                    list.add(ChatComponentText.EMPTY);
                    list.add((new ChatMessage("item.canBreak")).a(EnumChatFormat.GRAY));

                    for (k = 0; k < nbttaglist1.size(); ++k) {
                        list.addAll(d(nbttaglist1.getString(k)));
                    }
                }
            }

            if (a(i, ItemStack.HideFlags.CAN_PLACE) && this.tag.hasKeyOfType("CanPlaceOn", 9)) {
                nbttaglist1 = this.tag.getList("CanPlaceOn", 8);
                if (!nbttaglist1.isEmpty()) {
                    list.add(ChatComponentText.EMPTY);
                    list.add((new ChatMessage("item.canPlace")).a(EnumChatFormat.GRAY));

                    for (k = 0; k < nbttaglist1.size(); ++k) {
                        list.addAll(d(nbttaglist1.getString(k)));
                    }
                }
            }
        }

        if (tooltipflag.a()) {
            if (this.g()) {
                list.add(new ChatMessage("item.durability", new Object[]{this.i() - this.getDamage(), this.i()}));
            }

            list.add((new ChatComponentText(IRegistry.ITEM.getKey(this.getItem()).toString())).a(EnumChatFormat.DARK_GRAY));
            if (this.hasTag()) {
                list.add((new ChatMessage("item.nbt_tags", new Object[]{this.tag.getKeys().size()})).a(EnumChatFormat.DARK_GRAY));
            }
        }

        return list;
    }

    private static boolean a(int i, ItemStack.HideFlags itemstack_hideflags) {
        return (i & itemstack_hideflags.a()) == 0;
    }

    private int O() {
        return this.hasTag() && this.tag.hasKeyOfType("HideFlags", 99) ? this.tag.getInt("HideFlags") : 0;
    }

    public void a(ItemStack.HideFlags itemstack_hideflags) {
        NBTTagCompound nbttagcompound = this.getOrCreateTag();

        nbttagcompound.setInt("HideFlags", nbttagcompound.getInt("HideFlags") | itemstack_hideflags.a());
    }

    public static void a(List<IChatBaseComponent> list, NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);

            IRegistry.ENCHANTMENT.getOptional(EnchantmentManager.b(nbttagcompound)).ifPresent((enchantment) -> {
                list.add(enchantment.d(EnchantmentManager.a(nbttagcompound)));
            });
        }

    }

    private static Collection<IChatBaseComponent> d(String s) {
        try {
            ArgumentBlock argumentblock = (new ArgumentBlock(new StringReader(s), true)).a(true);
            IBlockData iblockdata = argumentblock.getBlockData();
            MinecraftKey minecraftkey = argumentblock.d();
            boolean flag = iblockdata != null;
            boolean flag1 = minecraftkey != null;

            if (flag || flag1) {
                if (flag) {
                    return Lists.newArrayList(new IChatBaseComponent[]{iblockdata.getBlock().g().a(EnumChatFormat.DARK_GRAY)});
                }

                Tag<Block> tag = TagsBlock.a().a(minecraftkey);

                if (tag != null) {
                    Collection<Block> collection = tag.getTagged();

                    if (!collection.isEmpty()) {
                        return (Collection) collection.stream().map(Block::g).map((ichatmutablecomponent) -> {
                            return ichatmutablecomponent.a(EnumChatFormat.DARK_GRAY);
                        }).collect(Collectors.toList());
                    }
                }
            }
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return Lists.newArrayList(new IChatBaseComponent[]{(new ChatComponentText("missingno")).a(EnumChatFormat.DARK_GRAY)});
    }

    public boolean y() {
        return this.getItem().i(this);
    }

    public EnumItemRarity z() {
        return this.getItem().n(this);
    }

    public boolean canEnchant() {
        return !this.getItem().a(this) ? false : !this.hasEnchantments();
    }

    public void addEnchantment(Enchantment enchantment, int i) {
        this.getOrCreateTag();
        if (!this.tag.hasKeyOfType("Enchantments", 9)) {
            this.tag.set("Enchantments", new NBTTagList());
        }

        NBTTagList nbttaglist = this.tag.getList("Enchantments", 10);

        nbttaglist.add(EnchantmentManager.a(EnchantmentManager.a(enchantment), (byte) i));
    }

    public boolean hasEnchantments() {
        return this.tag != null && this.tag.hasKeyOfType("Enchantments", 9) ? !this.tag.getList("Enchantments", 10).isEmpty() : false;
    }

    public void a(String s, NBTBase nbtbase) {
        this.getOrCreateTag().set(s, nbtbase);
    }

    public boolean C() {
        return this.entityRepresentation instanceof EntityItemFrame;
    }

    public void a(@Nullable Entity entity) {
        this.entityRepresentation = entity;
    }

    @Nullable
    public EntityItemFrame D() {
        return this.entityRepresentation instanceof EntityItemFrame ? (EntityItemFrame) this.E() : null;
    }

    @Nullable
    public Entity E() {
        return !this.emptyCacheFlag ? this.entityRepresentation : null;
    }

    public int getRepairCost() {
        return this.hasTag() && this.tag.hasKeyOfType("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
    }

    public void setRepairCost(int i) {
        this.getOrCreateTag().setInt("RepairCost", i);
    }

    public Multimap<AttributeBase, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Object object;

        if (this.hasTag() && this.tag.hasKeyOfType("AttributeModifiers", 9)) {
            object = HashMultimap.create();
            NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);

                if (!nbttagcompound.hasKeyOfType("Slot", 8) || nbttagcompound.getString("Slot").equals(enumitemslot.getSlotName())) {
                    Optional<AttributeBase> optional = IRegistry.ATTRIBUTE.getOptional(MinecraftKey.a(nbttagcompound.getString("AttributeName")));

                    if (optional.isPresent()) {
                        AttributeModifier attributemodifier = AttributeModifier.a(nbttagcompound);

                        if (attributemodifier != null && attributemodifier.getUniqueId().getLeastSignificantBits() != 0L && attributemodifier.getUniqueId().getMostSignificantBits() != 0L) {
                            ((Multimap) object).put((AttributeBase) optional.get(), attributemodifier);
                        }
                    }
                }
            }
        } else {
            object = this.getItem().a(enumitemslot);
        }

        return (Multimap) object;
    }

    public void a(AttributeBase attributebase, AttributeModifier attributemodifier, @Nullable EnumItemSlot enumitemslot) {
        this.getOrCreateTag();
        if (!this.tag.hasKeyOfType("AttributeModifiers", 9)) {
            this.tag.set("AttributeModifiers", new NBTTagList());
        }

        NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);
        NBTTagCompound nbttagcompound = attributemodifier.save();

        nbttagcompound.setString("AttributeName", IRegistry.ATTRIBUTE.getKey(attributebase).toString());
        if (enumitemslot != null) {
            nbttagcompound.setString("Slot", enumitemslot.getSlotName());
        }

        nbttaglist.add(nbttagcompound);
    }

    public IChatBaseComponent G() {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("")).addSibling(this.getName());

        if (this.hasName()) {
            ichatmutablecomponent.a(EnumChatFormat.ITALIC);
        }

        IChatMutableComponent ichatmutablecomponent1 = ChatComponentUtils.a((IChatBaseComponent) ichatmutablecomponent);

        if (!this.emptyCacheFlag) {
            ichatmutablecomponent1.a(this.z().color).format((chatmodifier) -> {
                return chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ITEM, new ChatHoverable.c(this)));
            });
        }

        return ichatmutablecomponent1;
    }

    private static boolean a(ShapeDetectorBlock shapedetectorblock, @Nullable ShapeDetectorBlock shapedetectorblock1) {
        return shapedetectorblock1 != null && shapedetectorblock.a() == shapedetectorblock1.a() ? (shapedetectorblock.b() == null && shapedetectorblock1.b() == null ? true : (shapedetectorblock.b() != null && shapedetectorblock1.b() != null ? Objects.equals(shapedetectorblock.b().save(new NBTTagCompound()), shapedetectorblock1.b().save(new NBTTagCompound())) : false)) : false;
    }

    public boolean a(ITagRegistry itagregistry, ShapeDetectorBlock shapedetectorblock) {
        if (a(shapedetectorblock, this.cachedBreakBlock)) {
            return this.cachedBreakBlockResult;
        } else {
            this.cachedBreakBlock = shapedetectorblock;
            if (this.hasTag() && this.tag.hasKeyOfType("CanDestroy", 9)) {
                NBTTagList nbttaglist = this.tag.getList("CanDestroy", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);

                    try {
                        Predicate<ShapeDetectorBlock> predicate = ArgumentBlockPredicate.a().parse(new StringReader(s)).create(itagregistry);

                        if (predicate.test(shapedetectorblock)) {
                            this.cachedBreakBlockResult = true;
                            return true;
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ;
                    }
                }
            }

            this.cachedBreakBlockResult = false;
            return false;
        }
    }

    public boolean b(ITagRegistry itagregistry, ShapeDetectorBlock shapedetectorblock) {
        if (a(shapedetectorblock, this.cachedPlaceBlock)) {
            return this.cachedPlaceBlockResult;
        } else {
            this.cachedPlaceBlock = shapedetectorblock;
            if (this.hasTag() && this.tag.hasKeyOfType("CanPlaceOn", 9)) {
                NBTTagList nbttaglist = this.tag.getList("CanPlaceOn", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);

                    try {
                        Predicate<ShapeDetectorBlock> predicate = ArgumentBlockPredicate.a().parse(new StringReader(s)).create(itagregistry);

                        if (predicate.test(shapedetectorblock)) {
                            this.cachedPlaceBlockResult = true;
                            return true;
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ;
                    }
                }
            }

            this.cachedPlaceBlockResult = false;
            return false;
        }
    }

    public int H() {
        return this.popTime;
    }

    public void d(int i) {
        this.popTime = i;
    }

    public int getCount() {
        return this.emptyCacheFlag ? 0 : this.count;
    }

    public void setCount(int i) {
        this.count = i;
        this.checkEmpty();
    }

    public void add(int i) {
        this.setCount(this.count + i);
    }

    public void subtract(int i) {
        this.add(-i);
    }

    public void b(World world, EntityLiving entityliving, int i) {
        this.getItem().a(world, entityliving, this, i);
    }

    public void a(EntityItem entityitem) {
        this.getItem().a(entityitem);
    }

    public boolean J() {
        return this.getItem().isFood();
    }

    public SoundEffect K() {
        return this.getItem().O_();
    }

    public SoundEffect L() {
        return this.getItem().h();
    }

    @Nullable
    public SoundEffect M() {
        return this.getItem().g();
    }

    public static enum HideFlags {

        ENCHANTMENTS, MODIFIERS, UNBREAKABLE, CAN_DESTROY, CAN_PLACE, ADDITIONAL, DYE;

        private final int mask = 1 << this.ordinal();

        private HideFlags() {}

        public int a() {
            return this.mask;
        }
    }
}
