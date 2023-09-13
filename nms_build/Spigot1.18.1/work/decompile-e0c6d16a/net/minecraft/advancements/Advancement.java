package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {

    @Nullable
    private final Advancement parent;
    @Nullable
    private final AdvancementDisplay display;
    private final AdvancementRewards rewards;
    private final MinecraftKey id;
    private final Map<String, Criterion> criteria;
    private final String[][] requirements;
    private final Set<Advancement> children = Sets.newLinkedHashSet();
    private final IChatBaseComponent chatComponent;

    public Advancement(MinecraftKey minecraftkey, @Nullable Advancement advancement, @Nullable AdvancementDisplay advancementdisplay, AdvancementRewards advancementrewards, Map<String, Criterion> map, String[][] astring) {
        this.id = minecraftkey;
        this.display = advancementdisplay;
        this.criteria = ImmutableMap.copyOf(map);
        this.parent = advancement;
        this.rewards = advancementrewards;
        this.requirements = astring;
        if (advancement != null) {
            advancement.addChild(this);
        }

        if (advancementdisplay == null) {
            this.chatComponent = new ChatComponentText(minecraftkey.toString());
        } else {
            IChatBaseComponent ichatbasecomponent = advancementdisplay.getTitle();
            EnumChatFormat enumchatformat = advancementdisplay.getFrame().getChatColor();
            IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.mergeStyles(ichatbasecomponent.copy(), ChatModifier.EMPTY.withColor(enumchatformat)).append("\n").append(advancementdisplay.getDescription());
            IChatMutableComponent ichatmutablecomponent1 = ichatbasecomponent.copy().withStyle((chatmodifier) -> {
                return chatmodifier.withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, ichatmutablecomponent));
            });

            this.chatComponent = ChatComponentUtils.wrapInSquareBrackets(ichatmutablecomponent1).withStyle(enumchatformat);
        }

    }

    public Advancement.SerializedAdvancement deconstruct() {
        return new Advancement.SerializedAdvancement(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
    }

    @Nullable
    public Advancement getParent() {
        return this.parent;
    }

    @Nullable
    public AdvancementDisplay getDisplay() {
        return this.display;
    }

    public AdvancementRewards getRewards() {
        return this.rewards;
    }

    public String toString() {
        MinecraftKey minecraftkey = this.getId();

        return "SimpleAdvancement{id=" + minecraftkey + ", parent=" + (this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
    }

    public Iterable<Advancement> getChildren() {
        return this.children;
    }

    public Map<String, Criterion> getCriteria() {
        return this.criteria;
    }

    public int getMaxCriteraRequired() {
        return this.requirements.length;
    }

    public void addChild(Advancement advancement) {
        this.children.add(advancement);
    }

    public MinecraftKey getId() {
        return this.id;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Advancement)) {
            return false;
        } else {
            Advancement advancement = (Advancement) object;

            return this.id.equals(advancement.id);
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String[][] getRequirements() {
        return this.requirements;
    }

    public IChatBaseComponent getChatComponent() {
        return this.chatComponent;
    }

    public static class SerializedAdvancement {

        @Nullable
        private MinecraftKey parentId;
        @Nullable
        private Advancement parent;
        @Nullable
        private AdvancementDisplay display;
        private AdvancementRewards rewards;
        private Map<String, Criterion> criteria;
        @Nullable
        private String[][] requirements;
        private AdvancementRequirements requirementsStrategy;

        SerializedAdvancement(@Nullable MinecraftKey minecraftkey, @Nullable AdvancementDisplay advancementdisplay, AdvancementRewards advancementrewards, Map<String, Criterion> map, String[][] astring) {
            this.rewards = AdvancementRewards.EMPTY;
            this.criteria = Maps.newLinkedHashMap();
            this.requirementsStrategy = AdvancementRequirements.AND;
            this.parentId = minecraftkey;
            this.display = advancementdisplay;
            this.rewards = advancementrewards;
            this.criteria = map;
            this.requirements = astring;
        }

        private SerializedAdvancement() {
            this.rewards = AdvancementRewards.EMPTY;
            this.criteria = Maps.newLinkedHashMap();
            this.requirementsStrategy = AdvancementRequirements.AND;
        }

        public static Advancement.SerializedAdvancement advancement() {
            return new Advancement.SerializedAdvancement();
        }

        public Advancement.SerializedAdvancement parent(Advancement advancement) {
            this.parent = advancement;
            return this;
        }

        public Advancement.SerializedAdvancement parent(MinecraftKey minecraftkey) {
            this.parentId = minecraftkey;
            return this;
        }

        public Advancement.SerializedAdvancement display(ItemStack itemstack, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, @Nullable MinecraftKey minecraftkey, AdvancementFrameType advancementframetype, boolean flag, boolean flag1, boolean flag2) {
            return this.display(new AdvancementDisplay(itemstack, ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2));
        }

        public Advancement.SerializedAdvancement display(IMaterial imaterial, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, @Nullable MinecraftKey minecraftkey, AdvancementFrameType advancementframetype, boolean flag, boolean flag1, boolean flag2) {
            return this.display(new AdvancementDisplay(new ItemStack(imaterial.asItem()), ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2));
        }

        public Advancement.SerializedAdvancement display(AdvancementDisplay advancementdisplay) {
            this.display = advancementdisplay;
            return this;
        }

        public Advancement.SerializedAdvancement rewards(AdvancementRewards.a advancementrewards_a) {
            return this.rewards(advancementrewards_a.build());
        }

        public Advancement.SerializedAdvancement rewards(AdvancementRewards advancementrewards) {
            this.rewards = advancementrewards;
            return this;
        }

        public Advancement.SerializedAdvancement addCriterion(String s, CriterionInstance criterioninstance) {
            return this.addCriterion(s, new Criterion(criterioninstance));
        }

        public Advancement.SerializedAdvancement addCriterion(String s, Criterion criterion) {
            if (this.criteria.containsKey(s)) {
                throw new IllegalArgumentException("Duplicate criterion " + s);
            } else {
                this.criteria.put(s, criterion);
                return this;
            }
        }

        public Advancement.SerializedAdvancement requirements(AdvancementRequirements advancementrequirements) {
            this.requirementsStrategy = advancementrequirements;
            return this;
        }

        public Advancement.SerializedAdvancement requirements(String[][] astring) {
            this.requirements = astring;
            return this;
        }

        public boolean canBuild(Function<MinecraftKey, Advancement> function) {
            if (this.parentId == null) {
                return true;
            } else {
                if (this.parent == null) {
                    this.parent = (Advancement) function.apply(this.parentId);
                }

                return this.parent != null;
            }
        }

        public Advancement build(MinecraftKey minecraftkey) {
            if (!this.canBuild((minecraftkey1) -> {
                return null;
            })) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            } else {
                if (this.requirements == null) {
                    this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
                }

                return new Advancement(minecraftkey, this.parent, this.display, this.rewards, this.criteria, this.requirements);
            }
        }

        public Advancement save(Consumer<Advancement> consumer, String s) {
            Advancement advancement = this.build(new MinecraftKey(s));

            consumer.accept(advancement);
            return advancement;
        }

        public JsonObject serializeToJson() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            JsonObject jsonobject = new JsonObject();

            if (this.parent != null) {
                jsonobject.addProperty("parent", this.parent.getId().toString());
            } else if (this.parentId != null) {
                jsonobject.addProperty("parent", this.parentId.toString());
            }

            if (this.display != null) {
                jsonobject.add("display", this.display.serializeToJson());
            }

            jsonobject.add("rewards", this.rewards.serializeToJson());
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator = this.criteria.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Criterion> entry = (Entry) iterator.next();

                jsonobject1.add((String) entry.getKey(), ((Criterion) entry.getValue()).serializeToJson());
            }

            jsonobject.add("criteria", jsonobject1);
            JsonArray jsonarray = new JsonArray();
            String[][] astring = this.requirements;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String[] astring1 = astring[j];
                JsonArray jsonarray1 = new JsonArray();
                String[] astring2 = astring1;
                int k = astring1.length;

                for (int l = 0; l < k; ++l) {
                    String s = astring2[l];

                    jsonarray1.add(s);
                }

                jsonarray.add(jsonarray1);
            }

            jsonobject.add("requirements", jsonarray);
            return jsonobject;
        }

        public void serializeToNetwork(PacketDataSerializer packetdataserializer) {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            if (this.parentId == null) {
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.writeResourceLocation(this.parentId);
            }

            if (this.display == null) {
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(true);
                this.display.serializeToNetwork(packetdataserializer);
            }

            Criterion.serializeToNetwork(this.criteria, packetdataserializer);
            packetdataserializer.writeVarInt(this.requirements.length);
            String[][] astring = this.requirements;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String[] astring1 = astring[j];

                packetdataserializer.writeVarInt(astring1.length);
                String[] astring2 = astring1;
                int k = astring1.length;

                for (int l = 0; l < k; ++l) {
                    String s = astring2[l];

                    packetdataserializer.writeUtf(s);
                }
            }

        }

        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
        }

        public static Advancement.SerializedAdvancement fromJson(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
            MinecraftKey minecraftkey = jsonobject.has("parent") ? new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "parent")) : null;
            AdvancementDisplay advancementdisplay = jsonobject.has("display") ? AdvancementDisplay.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "display")) : null;
            AdvancementRewards advancementrewards = jsonobject.has("rewards") ? AdvancementRewards.deserialize(ChatDeserializer.getAsJsonObject(jsonobject, "rewards")) : AdvancementRewards.EMPTY;
            Map<String, Criterion> map = Criterion.criteriaFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "criteria"), lootdeserializationcontext);

            if (map.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            } else {
                JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "requirements", new JsonArray());
                String[][] astring = new String[jsonarray.size()][];

                int i;
                int j;

                for (i = 0; i < jsonarray.size(); ++i) {
                    JsonArray jsonarray1 = ChatDeserializer.convertToJsonArray(jsonarray.get(i), "requirements[" + i + "]");

                    astring[i] = new String[jsonarray1.size()];

                    for (j = 0; j < jsonarray1.size(); ++j) {
                        astring[i][j] = ChatDeserializer.convertToString(jsonarray1.get(j), "requirements[" + i + "][" + j + "]");
                    }
                }

                if (astring.length == 0) {
                    astring = new String[map.size()][];
                    i = 0;

                    String s;

                    for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); astring[i++] = new String[]{s}) {
                        s = (String) iterator.next();
                    }
                }

                String[][] astring1 = astring;
                int k = astring.length;

                int l;

                for (j = 0; j < k; ++j) {
                    String[] astring2 = astring1[j];

                    if (astring2.length == 0 && map.isEmpty()) {
                        throw new JsonSyntaxException("Requirement entry cannot be empty");
                    }

                    String[] astring3 = astring2;

                    l = astring2.length;

                    for (int i1 = 0; i1 < l; ++i1) {
                        String s1 = astring3[i1];

                        if (!map.containsKey(s1)) {
                            throw new JsonSyntaxException("Unknown required criterion '" + s1 + "'");
                        }
                    }
                }

                Iterator iterator1 = map.keySet().iterator();

                while (iterator1.hasNext()) {
                    String s2 = (String) iterator1.next();
                    boolean flag = false;
                    String[][] astring4 = astring;
                    int j1 = astring.length;

                    l = 0;

                    while (true) {
                        if (l < j1) {
                            String[] astring5 = astring4[l];

                            if (!ArrayUtils.contains(astring5, s2)) {
                                ++l;
                                continue;
                            }

                            flag = true;
                        }

                        if (!flag) {
                            throw new JsonSyntaxException("Criterion '" + s2 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                        }
                        break;
                    }
                }

                return new Advancement.SerializedAdvancement(minecraftkey, advancementdisplay, advancementrewards, map, astring);
            }
        }

        public static Advancement.SerializedAdvancement fromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readBoolean() ? packetdataserializer.readResourceLocation() : null;
            AdvancementDisplay advancementdisplay = packetdataserializer.readBoolean() ? AdvancementDisplay.fromNetwork(packetdataserializer) : null;
            Map<String, Criterion> map = Criterion.criteriaFromNetwork(packetdataserializer);
            String[][] astring = new String[packetdataserializer.readVarInt()][];

            for (int i = 0; i < astring.length; ++i) {
                astring[i] = new String[packetdataserializer.readVarInt()];

                for (int j = 0; j < astring[i].length; ++j) {
                    astring[i][j] = packetdataserializer.readUtf();
                }
            }

            return new Advancement.SerializedAdvancement(minecraftkey, advancementdisplay, AdvancementRewards.EMPTY, map, astring);
        }

        public Map<String, Criterion> getCriteria() {
            return this.criteria;
        }
    }
}
