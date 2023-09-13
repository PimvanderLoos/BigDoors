package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ShapeDetectorBuilder {

    private static final Joiner COMMA_JOINED = Joiner.on(",");
    private final List<String[]> pattern = Lists.newArrayList();
    private final Map<Character, Predicate<ShapeDetectorBlock>> lookup = Maps.newHashMap();
    private int height;
    private int width;

    private ShapeDetectorBuilder() {
        this.lookup.put(' ', (shapedetectorblock) -> {
            return true;
        });
    }

    public ShapeDetectorBuilder aisle(String... astring) {
        if (!ArrayUtils.isEmpty(astring) && !StringUtils.isEmpty(astring[0])) {
            if (this.pattern.isEmpty()) {
                this.height = astring.length;
                this.width = astring[0].length();
            }

            if (astring.length != this.height) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + astring.length + ")");
            } else {
                String[] astring1 = astring;
                int i = astring.length;

                for (int j = 0; j < i; ++j) {
                    String s = astring1[j];

                    if (s.length() != this.width) {
                        throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + s.length() + ")");
                    }

                    char[] achar = s.toCharArray();
                    int k = achar.length;

                    for (int l = 0; l < k; ++l) {
                        char c0 = achar[l];

                        if (!this.lookup.containsKey(c0)) {
                            this.lookup.put(c0, (Object) null);
                        }
                    }
                }

                this.pattern.add(astring);
                return this;
            }
        } else {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
    }

    public static ShapeDetectorBuilder start() {
        return new ShapeDetectorBuilder();
    }

    public ShapeDetectorBuilder where(char c0, Predicate<ShapeDetectorBlock> predicate) {
        this.lookup.put(c0, predicate);
        return this;
    }

    public ShapeDetector build() {
        return new ShapeDetector(this.createPattern());
    }

    private Predicate<ShapeDetectorBlock>[][][] createPattern() {
        this.ensureAllCharactersMatched();
        Predicate<ShapeDetectorBlock>[][][] apredicate = (Predicate[][][]) Array.newInstance(Predicate.class, new int[]{this.pattern.size(), this.height, this.width});

        for (int i = 0; i < this.pattern.size(); ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.width; ++k) {
                    apredicate[i][j][k] = (Predicate) this.lookup.get(((String[]) this.pattern.get(i))[j].charAt(k));
                }
            }
        }

        return apredicate;
    }

    private void ensureAllCharactersMatched() {
        List<Character> list = Lists.newArrayList();
        Iterator iterator = this.lookup.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Character, Predicate<ShapeDetectorBlock>> entry = (Entry) iterator.next();

            if (entry.getValue() == null) {
                list.add((Character) entry.getKey());
            }
        }

        if (!list.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + ShapeDetectorBuilder.COMMA_JOINED.join(list) + " are missing");
        }
    }
}
