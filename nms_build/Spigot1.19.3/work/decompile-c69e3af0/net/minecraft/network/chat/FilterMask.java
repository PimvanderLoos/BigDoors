package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.INamable;
import org.apache.commons.lang3.StringUtils;

public class FilterMask {

    public static final Codec<FilterMask> CODEC = INamable.fromEnum(FilterMask.a::values).dispatch(FilterMask::type, FilterMask.a::codec);
    public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.a.FULLY_FILTERED);
    public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.a.PASS_THROUGH);
    public static final ChatModifier FILTERED_STYLE = ChatModifier.EMPTY.withColor(EnumChatFormat.DARK_GRAY).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.translatable("chat.filtered")));
    static final Codec<FilterMask> PASS_THROUGH_CODEC = Codec.unit(FilterMask.PASS_THROUGH);
    static final Codec<FilterMask> FULLY_FILTERED_CODEC = Codec.unit(FilterMask.FULLY_FILTERED);
    static final Codec<FilterMask> PARTIALLY_FILTERED_CODEC = ExtraCodecs.BIT_SET.xmap(FilterMask::new, FilterMask::mask);
    private static final char HASH = '#';
    private final BitSet mask;
    private final FilterMask.a type;

    private FilterMask(BitSet bitset, FilterMask.a filtermask_a) {
        this.mask = bitset;
        this.type = filtermask_a;
    }

    private FilterMask(BitSet bitset) {
        this.mask = bitset;
        this.type = FilterMask.a.PARTIALLY_FILTERED;
    }

    public FilterMask(int i) {
        this(new BitSet(i), FilterMask.a.PARTIALLY_FILTERED);
    }

    private FilterMask.a type() {
        return this.type;
    }

    private BitSet mask() {
        return this.mask;
    }

    public static FilterMask read(PacketDataSerializer packetdataserializer) {
        FilterMask.a filtermask_a = (FilterMask.a) packetdataserializer.readEnum(FilterMask.a.class);
        FilterMask filtermask;

        switch (filtermask_a) {
            case PASS_THROUGH:
                filtermask = FilterMask.PASS_THROUGH;
                break;
            case FULLY_FILTERED:
                filtermask = FilterMask.FULLY_FILTERED;
                break;
            case PARTIALLY_FILTERED:
                filtermask = new FilterMask(packetdataserializer.readBitSet(), FilterMask.a.PARTIALLY_FILTERED);
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return filtermask;
    }

    public static void write(PacketDataSerializer packetdataserializer, FilterMask filtermask) {
        packetdataserializer.writeEnum(filtermask.type);
        if (filtermask.type == FilterMask.a.PARTIALLY_FILTERED) {
            packetdataserializer.writeBitSet(filtermask.mask);
        }

    }

    public void setFiltered(int i) {
        this.mask.set(i);
    }

    @Nullable
    public String apply(String s) {
        String s1;

        switch (this.type) {
            case PASS_THROUGH:
                s1 = s;
                break;
            case FULLY_FILTERED:
                s1 = null;
                break;
            case PARTIALLY_FILTERED:
                char[] achar = s.toCharArray();

                for (int i = 0; i < achar.length && i < this.mask.length(); ++i) {
                    if (this.mask.get(i)) {
                        achar[i] = '#';
                    }
                }

                s1 = new String(achar);
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return s1;
    }

    @Nullable
    public IChatBaseComponent applyWithFormatting(String s) {
        IChatMutableComponent ichatmutablecomponent;

        switch (this.type) {
            case PASS_THROUGH:
                ichatmutablecomponent = IChatBaseComponent.literal(s);
                break;
            case FULLY_FILTERED:
                ichatmutablecomponent = null;
                break;
            case PARTIALLY_FILTERED:
                IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.empty();
                int i = 0;
                boolean flag = this.mask.get(0);

                while (true) {
                    int j = flag ? this.mask.nextClearBit(i) : this.mask.nextSetBit(i);

                    j = j < 0 ? s.length() : j;
                    if (j == i) {
                        ichatmutablecomponent = ichatmutablecomponent1;
                        return ichatmutablecomponent;
                    }

                    if (flag) {
                        ichatmutablecomponent1.append((IChatBaseComponent) IChatBaseComponent.literal(StringUtils.repeat('#', j - i)).withStyle(FilterMask.FILTERED_STYLE));
                    } else {
                        ichatmutablecomponent1.append(s.substring(i, j));
                    }

                    flag = !flag;
                    i = j;
                }
            default:
                throw new IncompatibleClassChangeError();
        }

        return ichatmutablecomponent;
    }

    public boolean isEmpty() {
        return this.type == FilterMask.a.PASS_THROUGH;
    }

    public boolean isFullyFiltered() {
        return this.type == FilterMask.a.FULLY_FILTERED;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            FilterMask filtermask = (FilterMask) object;

            return this.mask.equals(filtermask.mask) && this.type == filtermask.type;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.mask.hashCode();

        i = 31 * i + this.type.hashCode();
        return i;
    }

    private static enum a implements INamable {

        PASS_THROUGH("pass_through", () -> {
            return FilterMask.PASS_THROUGH_CODEC;
        }), FULLY_FILTERED("fully_filtered", () -> {
            return FilterMask.FULLY_FILTERED_CODEC;
        }), PARTIALLY_FILTERED("partially_filtered", () -> {
            return FilterMask.PARTIALLY_FILTERED_CODEC;
        });

        private final String serializedName;
        private final Supplier<Codec<FilterMask>> codec;

        private a(String s, Supplier supplier) {
            this.serializedName = s;
            this.codec = supplier;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }

        private Codec<FilterMask> codec() {
            return (Codec) this.codec.get();
        }
    }
}
