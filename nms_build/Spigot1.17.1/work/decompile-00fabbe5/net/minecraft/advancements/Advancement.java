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

    private final Advancement parent;
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
            advancement.a(this);
        }

        if (advancementdisplay == null) {
            this.chatComponent = new ChatComponentText(minecraftkey.toString());
        } else {
            IChatBaseComponent ichatbasecomponent = advancementdisplay.a();
            EnumChatFormat enumchatformat = advancementdisplay.e().c();
            IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.a(ichatbasecomponent.mutableCopy(), ChatModifier.EMPTY.setColor(enumchatformat)).c("\n").addSibling(advancementdisplay.b());
            IChatMutableComponent ichatmutablecomponent1 = ichatbasecomponent.mutableCopy().format((chatmodifier) -> {
                return chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, ichatmutablecomponent));
            });

            this.chatComponent = ChatComponentUtils.a((IChatBaseComponent) ichatmutablecomponent1).a(enumchatformat);
        }

    }

    public Advancement.SerializedAdvancement a() {
        return new Advancement.SerializedAdvancement(this.parent == null ? null : this.parent.getName(), this.display, this.rewards, this.criteria, this.requirements);
    }

    @Nullable
    public Advancement b() {
        return this.parent;
    }

    @Nullable
    public AdvancementDisplay c() {
        return this.display;
    }

    public AdvancementRewards d() {
        return this.rewards;
    }

    public String toString() {
        MinecraftKey minecraftkey = this.getName();

        return "SimpleAdvancement{id=" + minecraftkey + ", parent=" + (this.parent == null ? "null" : this.parent.getName()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
    }

    public Iterable<Advancement> e() {
        return this.children;
    }

    public Map<String, Criterion> getCriteria() {
        return this.criteria;
    }

    public int g() {
        return this.requirements.length;
    }

    public void a(Advancement advancement) {
        this.children.add(advancement);
    }

    public MinecraftKey getName() {
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

    public String[][] i() {
        return this.requirements;
    }

    public IChatBaseComponent j() {
        return this.chatComponent;
    }

    public static class SerializedAdvancement {

        private MinecraftKey parentId;
        private Advancement parent;
        private AdvancementDisplay display;
        private AdvancementRewards rewards;
        private Map<String, Criterion> criteria;
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

        public static Advancement.SerializedAdvancement a() {
            return new Advancement.SerializedAdvancement();
        }

        public Advancement.SerializedAdvancement a(Advancement advancement) {
            this.parent = advancement;
            return this;
        }

        public Advancement.SerializedAdvancement a(MinecraftKey minecraftkey) {
            this.parentId = minecraftkey;
            return this;
        }

        public Advancement.SerializedAdvancement a(ItemStack itemstack, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, @Nullable MinecraftKey minecraftkey, AdvancementFrameType advancementframetype, boolean flag, boolean flag1, boolean flag2) {
            return this.a(new AdvancementDisplay(itemstack, ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2));
        }

        public Advancement.SerializedAdvancement a(IMaterial imaterial, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, @Nullable MinecraftKey minecraftkey, AdvancementFrameType advancementframetype, boolean flag, boolean flag1, boolean flag2) {
            return this.a(new AdvancementDisplay(new ItemStack(imaterial.getItem()), ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2));
        }

        public Advancement.SerializedAdvancement a(AdvancementDisplay advancementdisplay) {
            this.display = advancementdisplay;
            return this;
        }

        public Advancement.SerializedAdvancement a(AdvancementRewards.a advancementrewards_a) {
            return this.a(advancementrewards_a.a());
        }

        public Advancement.SerializedAdvancement a(AdvancementRewards advancementrewards) {
            this.rewards = advancementrewards;
            return this;
        }

        public Advancement.SerializedAdvancement a(String s, CriterionInstance criterioninstance) {
            return this.a(s, new Criterion(criterioninstance));
        }

        public Advancement.SerializedAdvancement a(String s, Criterion criterion) {
            if (this.criteria.containsKey(s)) {
                throw new IllegalArgumentException("Duplicate criterion " + s);
            } else {
                this.criteria.put(s, criterion);
                return this;
            }
        }

        public Advancement.SerializedAdvancement a(AdvancementRequirements advancementrequirements) {
            this.requirementsStrategy = advancementrequirements;
            return this;
        }

        public Advancement.SerializedAdvancement a(String[][] astring) {
            this.requirements = astring;
            return this;
        }

        public boolean a(Function<MinecraftKey, Advancement> function) {
            if (this.parentId == null) {
                return true;
            } else {
                if (this.parent == null) {
                    this.parent = (Advancement) function.apply(this.parentId);
                }

                return this.parent != null;
            }
        }

        public Advancement b(MinecraftKey minecraftkey) {
            if (!this.a((minecraftkey1) -> {
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

        public Advancement a(Consumer<Advancement> consumer, String s) {
            Advancement advancement = this.b(new MinecraftKey(s));

            consumer.accept(advancement);
            return advancement;
        }

        public JsonObject b() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            JsonObject jsonobject = new JsonObject();

            if (this.parent != null) {
                jsonobject.addProperty("parent", this.parent.getName().toString());
            } else if (this.parentId != null) {
                jsonobject.addProperty("parent", this.parentId.toString());
            }

            if (this.display != null) {
                jsonobject.add("display", this.display.k());
            }

            jsonobject.add("rewards", this.rewards.b());
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator = this.criteria.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Criterion> entry = (Entry) iterator.next();

                jsonobject1.add((String) entry.getKey(), ((Criterion) entry.getValue()).b());
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

        public void a(PacketDataSerializer packetdataserializer) {
            if (this.parentId == null) {
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.a(this.parentId);
            }

            if (this.display == null) {
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(true);
                this.display.a(packetdataserializer);
            }

            Criterion.a(this.criteria, packetdataserializer);
            packetdataserializer.d(this.requirements.length);
            String[][] astring = this.requirements;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String[] astring1 = astring[j];

                packetdataserializer.d(astring1.length);
                String[] astring2 = astring1;
                int k = astring1.length;

                for (int l = 0; l < k; ++l) {
                    String s = astring2[l];

                    packetdataserializer.a(s);
                }
            }

        }

        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
        }

        public static Advancement.SerializedAdvancement a(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
            MinecraftKey minecraftkey = jsonobject.has("parent") ? new MinecraftKey(ChatDeserializer.h(jsonobject, "parent")) : null;
            AdvancementDisplay advancementdisplay = jsonobject.has("display") ? AdvancementDisplay.a(ChatDeserializer.t(jsonobject, "display")) : null;
            AdvancementRewards advancementrewards = jsonobject.has("rewards") ? AdvancementRewards.a(ChatDeserializer.t(jsonobject, "rewards")) : AdvancementRewards.EMPTY;
            Map<String, Criterion> map = Criterion.b(ChatDeserializer.t(jsonobject, "criteria"), lootdeserializationcontext);

            if (map.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            } else {
                JsonArray jsonarray = ChatDeserializer.a(jsonobject, "requirements", new JsonArray());
                String[][] astring = new String[jsonarray.size()][];

                int i;
                int j;

                for (i = 0; i < jsonarray.size(); ++i) {
                    JsonArray jsonarray1 = ChatDeserializer.n(jsonarray.get(i), "requirements[" + i + "]");

                    astring[i] = new String[jsonarray1.size()];

                    for (j = 0; j < jsonarray1.size(); ++j) {
                        astring[i][j] = ChatDeserializer.a(jsonarray1.get(j), "requirements[" + i + "][" + j + "]");
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

        public static Advancement.SerializedAdvancement b(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readBoolean() ? packetdataserializer.q() : null;
            AdvancementDisplay advancementdisplay = packetdataserializer.readBoolean() ? AdvancementDisplay.b(packetdataserializer) : null;
            Map<String, Criterion> map = Criterion.c(packetdataserializer);
            String[][] astring = new String[packetdataserializer.j()][];

            for (int i = 0; i < astring.length; ++i) {
                astring[i] = new String[packetdataserializer.j()];

                for (int j = 0; j < astring[i].length; ++j) {
                    astring[i][j] = packetdataserializer.p();
                }
            }

            return new Advancement.SerializedAdvancement(minecraftkey, advancementdisplay, AdvancementRewards.EMPTY, map, astring);
        }

        public Map<String, Criterion> c() {
            return this.criteria;
        }
    }
}
