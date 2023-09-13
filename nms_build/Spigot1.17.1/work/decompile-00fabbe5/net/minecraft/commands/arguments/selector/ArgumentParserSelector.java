package net.minecraft.commands.arguments.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.CriterionConditionRange;
import net.minecraft.advancements.critereon.CriterionConditionValue;
import net.minecraft.commands.arguments.selector.options.PlayerSelector;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class ArgumentParserSelector {

    public static final char SYNTAX_SELECTOR_START = '@';
    private static final char SYNTAX_OPTIONS_START = '[';
    private static final char SYNTAX_OPTIONS_END = ']';
    public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
    private static final char SYNTAX_OPTIONS_SEPARATOR = ',';
    public static final char SYNTAX_NOT = '!';
    public static final char SYNTAX_TAG = '#';
    private static final char SELECTOR_NEAREST_PLAYER = 'p';
    private static final char SELECTOR_ALL_PLAYERS = 'a';
    private static final char SELECTOR_RANDOM_PLAYERS = 'r';
    private static final char SELECTOR_CURRENT_ENTITY = 's';
    private static final char SELECTOR_ALL_ENTITIES = 'e';
    public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(new ChatMessage("argument.entity.invalid"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.selector.unknown", new Object[]{object});
    });
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new ChatMessage("argument.entity.selector.not_allowed"));
    public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(new ChatMessage("argument.entity.selector.missing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.unterminated"));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.valueless", new Object[]{object});
    });
    public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_ARBITRARY = (vec3d, list) -> {
    };
    public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_NEAREST = (vec3d, list) -> {
        list.sort((entity, entity1) -> {
            return Doubles.compare(entity.e(vec3d), entity1.e(vec3d));
        });
    };
    public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_FURTHEST = (vec3d, list) -> {
        list.sort((entity, entity1) -> {
            return Doubles.compare(entity1.e(vec3d), entity.e(vec3d));
        });
    };
    public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_RANDOM = (vec3d, list) -> {
        Collections.shuffle(list);
    };
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (suggestionsbuilder, consumer) -> {
        return suggestionsbuilder.buildFuture();
    };
    private final StringReader reader;
    private final boolean allowSelectors;
    private int maxResults;
    private boolean includesEntities;
    private boolean worldLimited;
    private CriterionConditionValue.DoubleRange distance;
    private CriterionConditionValue.IntegerRange level;
    @Nullable
    private Double x;
    @Nullable
    private Double y;
    @Nullable
    private Double z;
    @Nullable
    private Double deltaX;
    @Nullable
    private Double deltaY;
    @Nullable
    private Double deltaZ;
    private CriterionConditionRange rotX;
    private CriterionConditionRange rotY;
    private Predicate<Entity> predicate;
    private BiConsumer<Vec3D, List<? extends Entity>> order;
    private boolean currentEntity;
    @Nullable
    private String playerName;
    private int startPosition;
    @Nullable
    private UUID entityUUID;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;
    private boolean hasNameEquals;
    private boolean hasNameNotEquals;
    private boolean isLimited;
    private boolean isSorted;
    private boolean hasGamemodeEquals;
    private boolean hasGamemodeNotEquals;
    private boolean hasTeamEquals;
    private boolean hasTeamNotEquals;
    @Nullable
    private EntityTypes<?> type;
    private boolean typeInverse;
    private boolean hasScores;
    private boolean hasAdvancements;
    private boolean usesSelectors;

    public ArgumentParserSelector(StringReader stringreader) {
        this(stringreader, true);
    }

    public ArgumentParserSelector(StringReader stringreader, boolean flag) {
        this.distance = CriterionConditionValue.DoubleRange.ANY;
        this.level = CriterionConditionValue.IntegerRange.ANY;
        this.rotX = CriterionConditionRange.ANY;
        this.rotY = CriterionConditionRange.ANY;
        this.predicate = (entity) -> {
            return true;
        };
        this.order = ArgumentParserSelector.ORDER_ARBITRARY;
        this.suggestions = ArgumentParserSelector.SUGGEST_NOTHING;
        this.reader = stringreader;
        this.allowSelectors = flag;
    }

    public EntitySelector a() {
        AxisAlignedBB axisalignedbb;

        if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
            if (this.distance.b() != null) {
                double d0 = (Double) this.distance.b();

                axisalignedbb = new AxisAlignedBB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            } else {
                axisalignedbb = null;
            }
        } else {
            axisalignedbb = this.a(this.deltaX == null ? 0.0D : this.deltaX, this.deltaY == null ? 0.0D : this.deltaY, this.deltaZ == null ? 0.0D : this.deltaZ);
        }

        Function function;

        if (this.x == null && this.y == null && this.z == null) {
            function = (vec3d) -> {
                return vec3d;
            };
        } else {
            function = (vec3d) -> {
                return new Vec3D(this.x == null ? vec3d.x : this.x, this.y == null ? vec3d.y : this.y, this.z == null ? vec3d.z : this.z);
            };
        }

        return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, function, axisalignedbb, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
    }

    private AxisAlignedBB a(double d0, double d1, double d2) {
        boolean flag = d0 < 0.0D;
        boolean flag1 = d1 < 0.0D;
        boolean flag2 = d2 < 0.0D;
        double d3 = flag ? d0 : 0.0D;
        double d4 = flag1 ? d1 : 0.0D;
        double d5 = flag2 ? d2 : 0.0D;
        double d6 = (flag ? 0.0D : d0) + 1.0D;
        double d7 = (flag1 ? 0.0D : d1) + 1.0D;
        double d8 = (flag2 ? 0.0D : d2) + 1.0D;

        return new AxisAlignedBB(d3, d4, d5, d6, d7, d8);
    }

    private void I() {
        if (this.rotX != CriterionConditionRange.ANY) {
            this.predicate = this.predicate.and(this.a(this.rotX, Entity::getXRot));
        }

        if (this.rotY != CriterionConditionRange.ANY) {
            this.predicate = this.predicate.and(this.a(this.rotY, Entity::getYRot));
        }

        if (!this.level.c()) {
            this.predicate = this.predicate.and((entity) -> {
                return !(entity instanceof EntityPlayer) ? false : this.level.d(((EntityPlayer) entity).experienceLevel);
            });
        }

    }

    private Predicate<Entity> a(CriterionConditionRange criterionconditionrange, ToDoubleFunction<Entity> todoublefunction) {
        double d0 = (double) MathHelper.g(criterionconditionrange.a() == null ? 0.0F : criterionconditionrange.a());
        double d1 = (double) MathHelper.g(criterionconditionrange.b() == null ? 359.0F : criterionconditionrange.b());

        return (entity) -> {
            double d2 = MathHelper.f(todoublefunction.applyAsDouble(entity));

            return d0 > d1 ? d2 >= d0 || d2 <= d1 : d2 >= d0 && d2 <= d1;
        };
    }

    protected void parseSelector() throws CommandSyntaxException {
        this.usesSelectors = true;
        this.suggestions = this::d;
        if (!this.reader.canRead()) {
            throw ArgumentParserSelector.ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();
            char c0 = this.reader.read();

            if (c0 == 'p') {
                this.maxResults = 1;
                this.includesEntities = false;
                this.order = ArgumentParserSelector.ORDER_NEAREST;
                this.a(EntityTypes.PLAYER);
            } else if (c0 == 'a') {
                this.maxResults = Integer.MAX_VALUE;
                this.includesEntities = false;
                this.order = ArgumentParserSelector.ORDER_ARBITRARY;
                this.a(EntityTypes.PLAYER);
            } else if (c0 == 'r') {
                this.maxResults = 1;
                this.includesEntities = false;
                this.order = ArgumentParserSelector.ORDER_RANDOM;
                this.a(EntityTypes.PLAYER);
            } else if (c0 == 's') {
                this.maxResults = 1;
                this.includesEntities = true;
                this.currentEntity = true;
            } else {
                if (c0 != 'e') {
                    this.reader.setCursor(i);
                    throw ArgumentParserSelector.ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + String.valueOf(c0));
                }

                this.maxResults = Integer.MAX_VALUE;
                this.includesEntities = true;
                this.order = ArgumentParserSelector.ORDER_ARBITRARY;
                this.predicate = Entity::isAlive;
            }

            this.suggestions = this::e;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.reader.skip();
                this.suggestions = this::f;
                this.d();
            }

        }
    }

    protected void c() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestions = this::c;
        }

        int i = this.reader.getCursor();
        String s = this.reader.readString();

        try {
            this.entityUUID = UUID.fromString(s);
            this.includesEntities = true;
        } catch (IllegalArgumentException illegalargumentexception) {
            if (s.isEmpty() || s.length() > 16) {
                this.reader.setCursor(i);
                throw ArgumentParserSelector.ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
            }

            this.includesEntities = false;
            this.playerName = s;
        }

        this.maxResults = 1;
    }

    protected void d() throws CommandSyntaxException {
        this.suggestions = this::g;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != ']') {
                this.reader.skipWhitespace();
                int i = this.reader.getCursor();
                String s = this.reader.readString();
                PlayerSelector.a playerselector_a = PlayerSelector.a(this, s, i);

                this.reader.skipWhitespace();
                if (!this.reader.canRead() || this.reader.peek() != '=') {
                    this.reader.setCursor(i);
                    throw ArgumentParserSelector.ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = ArgumentParserSelector.SUGGEST_NOTHING;
                playerselector_a.handle(this);
                this.reader.skipWhitespace();
                this.suggestions = this::h;
                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestions = this::g;
                    continue;
                }

                if (this.reader.peek() != ']') {
                    throw ArgumentParserSelector.ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                this.suggestions = ArgumentParserSelector.SUGGEST_NOTHING;
                return;
            }

            throw ArgumentParserSelector.ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
        }
    }

    public boolean e() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '!') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    public boolean f() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    public StringReader g() {
        return this.reader;
    }

    public void a(Predicate<Entity> predicate) {
        this.predicate = this.predicate.and(predicate);
    }

    public void h() {
        this.worldLimited = true;
    }

    public CriterionConditionValue.DoubleRange i() {
        return this.distance;
    }

    public void a(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
        this.distance = criterionconditionvalue_doublerange;
    }

    public CriterionConditionValue.IntegerRange j() {
        return this.level;
    }

    public void a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        this.level = criterionconditionvalue_integerrange;
    }

    public CriterionConditionRange k() {
        return this.rotX;
    }

    public void a(CriterionConditionRange criterionconditionrange) {
        this.rotX = criterionconditionrange;
    }

    public CriterionConditionRange l() {
        return this.rotY;
    }

    public void b(CriterionConditionRange criterionconditionrange) {
        this.rotY = criterionconditionrange;
    }

    @Nullable
    public Double m() {
        return this.x;
    }

    @Nullable
    public Double n() {
        return this.y;
    }

    @Nullable
    public Double o() {
        return this.z;
    }

    public void a(double d0) {
        this.x = d0;
    }

    public void b(double d0) {
        this.y = d0;
    }

    public void c(double d0) {
        this.z = d0;
    }

    public void d(double d0) {
        this.deltaX = d0;
    }

    public void e(double d0) {
        this.deltaY = d0;
    }

    public void f(double d0) {
        this.deltaZ = d0;
    }

    @Nullable
    public Double p() {
        return this.deltaX;
    }

    @Nullable
    public Double q() {
        return this.deltaY;
    }

    @Nullable
    public Double r() {
        return this.deltaZ;
    }

    public void a(int i) {
        this.maxResults = i;
    }

    public void a(boolean flag) {
        this.includesEntities = flag;
    }

    public BiConsumer<Vec3D, List<? extends Entity>> s() {
        return this.order;
    }

    public void a(BiConsumer<Vec3D, List<? extends Entity>> biconsumer) {
        this.order = biconsumer;
    }

    public EntitySelector parse() throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.suggestions = this::b;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.allowSelectors) {
                throw ArgumentParserSelector.ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
            }

            this.reader.skip();
            this.parseSelector();
        } else {
            this.c();
        }

        this.I();
        return this.a();
    }

    private static void a(SuggestionsBuilder suggestionsbuilder) {
        suggestionsbuilder.suggest("@p", new ChatMessage("argument.entity.selector.nearestPlayer"));
        suggestionsbuilder.suggest("@a", new ChatMessage("argument.entity.selector.allPlayers"));
        suggestionsbuilder.suggest("@r", new ChatMessage("argument.entity.selector.randomPlayer"));
        suggestionsbuilder.suggest("@s", new ChatMessage("argument.entity.selector.self"));
        suggestionsbuilder.suggest("@e", new ChatMessage("argument.entity.selector.allEntities"));
    }

    private CompletableFuture<Suggestions> b(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(suggestionsbuilder);
        if (this.allowSelectors) {
            a(suggestionsbuilder);
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> c(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsbuilder1 = suggestionsbuilder.createOffset(this.startPosition);

        consumer.accept(suggestionsbuilder1);
        return suggestionsbuilder.add(suggestionsbuilder1).buildFuture();
    }

    private CompletableFuture<Suggestions> d(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsbuilder1 = suggestionsbuilder.createOffset(suggestionsbuilder.getStart() - 1);

        a(suggestionsbuilder1);
        suggestionsbuilder.add(suggestionsbuilder1);
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> e(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf('['));
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> f(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf(']'));
        PlayerSelector.a(this, suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> g(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        PlayerSelector.a(this, suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> h(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf(','));
        suggestionsbuilder.suggest(String.valueOf(']'));
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> i(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf('='));
        return suggestionsbuilder.buildFuture();
    }

    public boolean u() {
        return this.currentEntity;
    }

    public void a(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> bifunction) {
        this.suggestions = bifunction;
    }

    public CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        return (CompletableFuture) this.suggestions.apply(suggestionsbuilder.createOffset(this.reader.getCursor()), consumer);
    }

    public boolean v() {
        return this.hasNameEquals;
    }

    public void b(boolean flag) {
        this.hasNameEquals = flag;
    }

    public boolean w() {
        return this.hasNameNotEquals;
    }

    public void c(boolean flag) {
        this.hasNameNotEquals = flag;
    }

    public boolean x() {
        return this.isLimited;
    }

    public void d(boolean flag) {
        this.isLimited = flag;
    }

    public boolean y() {
        return this.isSorted;
    }

    public void e(boolean flag) {
        this.isSorted = flag;
    }

    public boolean z() {
        return this.hasGamemodeEquals;
    }

    public void f(boolean flag) {
        this.hasGamemodeEquals = flag;
    }

    public boolean A() {
        return this.hasGamemodeNotEquals;
    }

    public void g(boolean flag) {
        this.hasGamemodeNotEquals = flag;
    }

    public boolean B() {
        return this.hasTeamEquals;
    }

    public void h(boolean flag) {
        this.hasTeamEquals = flag;
    }

    public boolean C() {
        return this.hasTeamNotEquals;
    }

    public void i(boolean flag) {
        this.hasTeamNotEquals = flag;
    }

    public void a(EntityTypes<?> entitytypes) {
        this.type = entitytypes;
    }

    public void D() {
        this.typeInverse = true;
    }

    public boolean E() {
        return this.type != null;
    }

    public boolean F() {
        return this.typeInverse;
    }

    public boolean G() {
        return this.hasScores;
    }

    public void j(boolean flag) {
        this.hasScores = flag;
    }

    public boolean H() {
        return this.hasAdvancements;
    }

    public void k(boolean flag) {
        this.hasAdvancements = flag;
    }
}
