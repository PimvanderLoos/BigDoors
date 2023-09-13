package net.minecraft.world.entity.ai.village;

public interface ReputationEvent {

    ReputationEvent ZOMBIE_VILLAGER_CURED = a("zombie_villager_cured");
    ReputationEvent GOLEM_KILLED = a("golem_killed");
    ReputationEvent VILLAGER_HURT = a("villager_hurt");
    ReputationEvent VILLAGER_KILLED = a("villager_killed");
    ReputationEvent TRADE = a("trade");

    static ReputationEvent a(final String s) {
        return new ReputationEvent() {
            public String toString() {
                return s;
            }
        };
    }
}
