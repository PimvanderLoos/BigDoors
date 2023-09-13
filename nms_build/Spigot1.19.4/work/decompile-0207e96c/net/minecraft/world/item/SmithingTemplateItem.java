package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.World;

public class SmithingTemplateItem extends Item {

    private static final EnumChatFormat TITLE_FORMAT = EnumChatFormat.GRAY;
    private static final EnumChatFormat DESCRIPTION_FORMAT = EnumChatFormat.BLUE;
    private static final String DESCRIPTION_ID = SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template"));
    private static final IChatBaseComponent INGREDIENTS_TITLE = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.ingredients"))).withStyle(SmithingTemplateItem.TITLE_FORMAT);
    private static final IChatBaseComponent APPLIES_TO_TITLE = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.applies_to"))).withStyle(SmithingTemplateItem.TITLE_FORMAT);
    private static final IChatBaseComponent NETHERITE_UPGRADE = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("upgrade", new MinecraftKey("netherite_upgrade"))).withStyle(SmithingTemplateItem.TITLE_FORMAT);
    private static final IChatBaseComponent ARMOR_TRIM_APPLIES_TO = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.armor_trim.applies_to"))).withStyle(SmithingTemplateItem.DESCRIPTION_FORMAT);
    private static final IChatBaseComponent ARMOR_TRIM_INGREDIENTS = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.armor_trim.ingredients"))).withStyle(SmithingTemplateItem.DESCRIPTION_FORMAT);
    private static final IChatBaseComponent ARMOR_TRIM_BASE_SLOT_DESCRIPTION = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.armor_trim.base_slot_description")));
    private static final IChatBaseComponent ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.armor_trim.additions_slot_description")));
    private static final IChatBaseComponent NETHERITE_UPGRADE_APPLIES_TO = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.netherite_upgrade.applies_to"))).withStyle(SmithingTemplateItem.DESCRIPTION_FORMAT);
    private static final IChatBaseComponent NETHERITE_UPGRADE_INGREDIENTS = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.netherite_upgrade.ingredients"))).withStyle(SmithingTemplateItem.DESCRIPTION_FORMAT);
    private static final IChatBaseComponent NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.netherite_upgrade.base_slot_description")));
    private static final IChatBaseComponent NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.netherite_upgrade.additions_slot_description")));
    private static final MinecraftKey EMPTY_SLOT_HELMET = new MinecraftKey("item/empty_armor_slot_helmet");
    private static final MinecraftKey EMPTY_SLOT_CHESTPLATE = new MinecraftKey("item/empty_armor_slot_chestplate");
    private static final MinecraftKey EMPTY_SLOT_LEGGINGS = new MinecraftKey("item/empty_armor_slot_leggings");
    private static final MinecraftKey EMPTY_SLOT_BOOTS = new MinecraftKey("item/empty_armor_slot_boots");
    private static final MinecraftKey EMPTY_SLOT_HOE = new MinecraftKey("item/empty_slot_hoe");
    private static final MinecraftKey EMPTY_SLOT_AXE = new MinecraftKey("item/empty_slot_axe");
    private static final MinecraftKey EMPTY_SLOT_SWORD = new MinecraftKey("item/empty_slot_sword");
    private static final MinecraftKey EMPTY_SLOT_SHOVEL = new MinecraftKey("item/empty_slot_shovel");
    private static final MinecraftKey EMPTY_SLOT_PICKAXE = new MinecraftKey("item/empty_slot_pickaxe");
    private static final MinecraftKey EMPTY_SLOT_INGOT = new MinecraftKey("item/empty_slot_ingot");
    private static final MinecraftKey EMPTY_SLOT_REDSTONE_DUST = new MinecraftKey("item/empty_slot_redstone_dust");
    private static final MinecraftKey EMPTY_SLOT_QUARTZ = new MinecraftKey("item/empty_slot_quartz");
    private static final MinecraftKey EMPTY_SLOT_EMERALD = new MinecraftKey("item/empty_slot_emerald");
    private static final MinecraftKey EMPTY_SLOT_DIAMOND = new MinecraftKey("item/empty_slot_diamond");
    private static final MinecraftKey EMPTY_SLOT_LAPIS_LAZULI = new MinecraftKey("item/empty_slot_lapis_lazuli");
    private static final MinecraftKey EMPTY_SLOT_AMETHYST_SHARD = new MinecraftKey("item/empty_slot_amethyst_shard");
    private final IChatBaseComponent appliesTo;
    private final IChatBaseComponent ingredients;
    private final IChatBaseComponent upgradeDescription;
    private final IChatBaseComponent baseSlotDescription;
    private final IChatBaseComponent additionsSlotDescription;
    private final List<MinecraftKey> baseSlotEmptyIcons;
    private final List<MinecraftKey> additionalSlotEmptyIcons;

    public SmithingTemplateItem(IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, IChatBaseComponent ichatbasecomponent2, IChatBaseComponent ichatbasecomponent3, IChatBaseComponent ichatbasecomponent4, List<MinecraftKey> list, List<MinecraftKey> list1) {
        super((new Item.Info()).requiredFeatures(FeatureFlags.UPDATE_1_20));
        this.appliesTo = ichatbasecomponent;
        this.ingredients = ichatbasecomponent1;
        this.upgradeDescription = ichatbasecomponent2;
        this.baseSlotDescription = ichatbasecomponent3;
        this.additionsSlotDescription = ichatbasecomponent4;
        this.baseSlotEmptyIcons = list;
        this.additionalSlotEmptyIcons = list1;
    }

    public static SmithingTemplateItem createArmorTrimTemplate(ResourceKey<TrimPattern> resourcekey) {
        return createArmorTrimTemplate(resourcekey.location());
    }

    public static SmithingTemplateItem createArmorTrimTemplate(MinecraftKey minecraftkey) {
        return new SmithingTemplateItem(SmithingTemplateItem.ARMOR_TRIM_APPLIES_TO, SmithingTemplateItem.ARMOR_TRIM_INGREDIENTS, IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("trim_pattern", minecraftkey)).withStyle(SmithingTemplateItem.TITLE_FORMAT), SmithingTemplateItem.ARMOR_TRIM_BASE_SLOT_DESCRIPTION, SmithingTemplateItem.ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION, createTrimmableArmorIconList(), createTrimmableMaterialIconList());
    }

    public static SmithingTemplateItem createNetheriteUpgradeTemplate() {
        return new SmithingTemplateItem(SmithingTemplateItem.NETHERITE_UPGRADE_APPLIES_TO, SmithingTemplateItem.NETHERITE_UPGRADE_INGREDIENTS, SmithingTemplateItem.NETHERITE_UPGRADE, SmithingTemplateItem.NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION, SmithingTemplateItem.NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION, createNetheriteUpgradeIconList(), createNetheriteUpgradeMaterialList());
    }

    private static List<MinecraftKey> createTrimmableArmorIconList() {
        return List.of(SmithingTemplateItem.EMPTY_SLOT_HELMET, SmithingTemplateItem.EMPTY_SLOT_CHESTPLATE, SmithingTemplateItem.EMPTY_SLOT_LEGGINGS, SmithingTemplateItem.EMPTY_SLOT_BOOTS);
    }

    private static List<MinecraftKey> createTrimmableMaterialIconList() {
        return List.of(SmithingTemplateItem.EMPTY_SLOT_INGOT, SmithingTemplateItem.EMPTY_SLOT_REDSTONE_DUST, SmithingTemplateItem.EMPTY_SLOT_LAPIS_LAZULI, SmithingTemplateItem.EMPTY_SLOT_QUARTZ, SmithingTemplateItem.EMPTY_SLOT_DIAMOND, SmithingTemplateItem.EMPTY_SLOT_EMERALD, SmithingTemplateItem.EMPTY_SLOT_AMETHYST_SHARD);
    }

    private static List<MinecraftKey> createNetheriteUpgradeIconList() {
        return List.of(SmithingTemplateItem.EMPTY_SLOT_HELMET, SmithingTemplateItem.EMPTY_SLOT_SWORD, SmithingTemplateItem.EMPTY_SLOT_CHESTPLATE, SmithingTemplateItem.EMPTY_SLOT_PICKAXE, SmithingTemplateItem.EMPTY_SLOT_LEGGINGS, SmithingTemplateItem.EMPTY_SLOT_AXE, SmithingTemplateItem.EMPTY_SLOT_BOOTS, SmithingTemplateItem.EMPTY_SLOT_HOE, SmithingTemplateItem.EMPTY_SLOT_SHOVEL);
    }

    private static List<MinecraftKey> createNetheriteUpgradeMaterialList() {
        return List.of(SmithingTemplateItem.EMPTY_SLOT_INGOT);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        super.appendHoverText(itemstack, world, list, tooltipflag);
        list.add(this.upgradeDescription);
        list.add(CommonComponents.EMPTY);
        list.add(SmithingTemplateItem.APPLIES_TO_TITLE);
        list.add(CommonComponents.space().append(this.appliesTo));
        list.add(SmithingTemplateItem.INGREDIENTS_TITLE);
        list.add(CommonComponents.space().append(this.ingredients));
    }

    public IChatBaseComponent getBaseSlotDescription() {
        return this.baseSlotDescription;
    }

    public IChatBaseComponent getAdditionSlotDescription() {
        return this.additionsSlotDescription;
    }

    public List<MinecraftKey> getBaseSlotEmptyIcons() {
        return this.baseSlotEmptyIcons;
    }

    public List<MinecraftKey> getAdditionalSlotEmptyIcons() {
        return this.additionalSlotEmptyIcons;
    }

    @Override
    public String getDescriptionId() {
        return SmithingTemplateItem.DESCRIPTION_ID;
    }
}
