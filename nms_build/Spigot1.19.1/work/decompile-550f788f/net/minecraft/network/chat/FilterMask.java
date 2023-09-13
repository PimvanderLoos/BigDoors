package net.minecraft.network.chat;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.network.PacketDataSerializer;

public class FilterMask {

    public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.a.FULLY_FILTERED);
    public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.a.PASS_THROUGH);
    private static final char HASH = '#';
    private final BitSet mask;
    private final FilterMask.a type;

    private FilterMask(BitSet bitset, FilterMask.a filtermask_a) {
        this.mask = bitset;
        this.type = filtermask_a;
    }

    public FilterMask(int i) {
        this(new BitSet(i), FilterMask.a.PARTIALLY_FILTERED);
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
    public IChatBaseComponent apply(ChatMessageContent chatmessagecontent) {
        String s = chatmessagecontent.plain();

        return (IChatBaseComponent) SystemUtils.mapNullable(this.apply(s), IChatBaseComponent::literal);
    }

    public boolean isEmpty() {
        return this.type == FilterMask.a.PASS_THROUGH;
    }

    public boolean isFullyFiltered() {
        return this.type == FilterMask.a.FULLY_FILTERED;
    }

    private static enum a {

        PASS_THROUGH, FULLY_FILTERED, PARTIALLY_FILTERED;

        private a() {}
    }
}
