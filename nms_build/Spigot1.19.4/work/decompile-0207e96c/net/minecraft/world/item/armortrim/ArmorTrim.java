package net.minecraft.world.item.armortrim;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.EnumArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class ArmorTrim {

    public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)).apply(instance, ArmorTrim::new);
    });
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_TRIM_ID = "Trim";
    private static final IChatBaseComponent UPGRADE_TITLE = IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("item", new MinecraftKey("smithing_template.upgrade"))).withStyle(EnumChatFormat.GRAY);
    private final Holder<TrimMaterial> material;
    private final Holder<TrimPattern> pattern;
    private final Function<ArmorMaterial, MinecraftKey> innerTexture;
    private final Function<ArmorMaterial, MinecraftKey> outerTexture;

    public ArmorTrim(Holder<TrimMaterial> holder, Holder<TrimPattern> holder1) {
        this.material = holder;
        this.pattern = holder1;
        this.innerTexture = SystemUtils.memoize((armormaterial) -> {
            MinecraftKey minecraftkey = ((TrimPattern) holder1.value()).assetId();
            String s = this.getColorPaletteSuffix(armormaterial);

            return minecraftkey.withPath((s1) -> {
                return "trims/models/armor/" + s1 + "_leggings_" + s;
            });
        });
        this.outerTexture = SystemUtils.memoize((armormaterial) -> {
            MinecraftKey minecraftkey = ((TrimPattern) holder1.value()).assetId();
            String s = this.getColorPaletteSuffix(armormaterial);

            return minecraftkey.withPath((s1) -> {
                return "trims/models/armor/" + s1 + "_" + s;
            });
        });
    }

    private String getColorPaletteSuffix(ArmorMaterial armormaterial) {
        Map<EnumArmorMaterial, String> map = ((TrimMaterial) this.material.value()).overrideArmorMaterials();

        return armormaterial instanceof EnumArmorMaterial && map.containsKey(armormaterial) ? (String) map.get(armormaterial) : ((TrimMaterial) this.material.value()).assetName();
    }

    public boolean hasPatternAndMaterial(Holder<TrimPattern> holder, Holder<TrimMaterial> holder1) {
        return holder == this.pattern && holder1 == this.material;
    }

    public Holder<TrimPattern> pattern() {
        return this.pattern;
    }

    public Holder<TrimMaterial> material() {
        return this.material;
    }

    public MinecraftKey innerTexture(ArmorMaterial armormaterial) {
        return (MinecraftKey) this.innerTexture.apply(armormaterial);
    }

    public MinecraftKey outerTexture(ArmorMaterial armormaterial) {
        return (MinecraftKey) this.outerTexture.apply(armormaterial);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ArmorTrim)) {
            return false;
        } else {
            ArmorTrim armortrim = (ArmorTrim) object;

            return armortrim.pattern == this.pattern && armortrim.material == this.material;
        }
    }

    public static boolean setTrim(IRegistryCustom iregistrycustom, ItemStack itemstack, ArmorTrim armortrim) {
        if (itemstack.is(TagsItem.TRIMMABLE_ARMOR)) {
            itemstack.getOrCreateTag().put("Trim", (NBTBase) ArmorTrim.CODEC.encodeStart(RegistryOps.create(DynamicOpsNBT.INSTANCE, (HolderLookup.b) iregistrycustom), armortrim).result().orElseThrow());
            return true;
        } else {
            return false;
        }
    }

    public static Optional<ArmorTrim> getTrim(IRegistryCustom iregistrycustom, ItemStack itemstack) {
        if (itemstack.is(TagsItem.TRIMMABLE_ARMOR) && itemstack.getTag() != null && itemstack.getTag().contains("Trim")) {
            NBTTagCompound nbttagcompound = itemstack.getTagElement("Trim");
            DataResult dataresult = ArmorTrim.CODEC.parse(RegistryOps.create(DynamicOpsNBT.INSTANCE, (HolderLookup.b) iregistrycustom), nbttagcompound);
            Logger logger = ArmorTrim.LOGGER;

            Objects.requireNonNull(logger);
            ArmorTrim armortrim = (ArmorTrim) dataresult.resultOrPartial(logger::error).orElse((Object) null);

            return Optional.ofNullable(armortrim);
        } else {
            return Optional.empty();
        }
    }

    public static void appendUpgradeHoverText(ItemStack itemstack, IRegistryCustom iregistrycustom, List<IChatBaseComponent> list) {
        Optional<ArmorTrim> optional = getTrim(iregistrycustom, itemstack);

        if (optional.isPresent()) {
            ArmorTrim armortrim = (ArmorTrim) optional.get();

            list.add(ArmorTrim.UPGRADE_TITLE);
            list.add(CommonComponents.space().append(((TrimPattern) armortrim.pattern().value()).copyWithStyle(armortrim.material())));
            list.add(CommonComponents.space().append(((TrimMaterial) armortrim.material().value()).description()));
        }

    }
}
