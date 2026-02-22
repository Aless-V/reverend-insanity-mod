package com.reverendinsanity.entity.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// 尊者战斗AI组件
public class VenerableCombatAI {

    public enum BehaviorState {
        AGGRESSIVE,
        DEFENSIVE,
        TACTICAL,
        BERSERK,
        HUNTING
    }

    public static class ComboSequence {
        private final String name;
        private final String[] moveIds;
        private final int minPhase;
        private final double maxDistanceSq;

        public ComboSequence(String name, String[] moveIds, int minPhase, double maxDistanceSq) {
            this.name = name == null ? "" : name;
            this.moveIds = moveIds == null ? new String[0] : moveIds.clone();
            this.minPhase = Math.max(1, minPhase);
            this.maxDistanceSq = Math.max(0.0d, maxDistanceSq);
        }

        public String getName() {
            return name;
        }

        public String[] getMoveIds() {
            return moveIds.clone();
        }

        public int getMinPhase() {
            return minPhase;
        }

        public double getMaxDistanceSq() {
            return maxDistanceSq;
        }

        private boolean matches(int currentPhase, double currentDistanceSq) {
            return currentPhase >= minPhase && currentDistanceSq <= maxDistanceSq;
        }
    }

    private final Map<BehaviorState, List<ComboSequence>> comboRegistry = new EnumMap<>(BehaviorState.class);
    private final Map<String, Integer> cooldowns = new HashMap<>();

    private BehaviorState currentState = BehaviorState.TACTICAL;
    private ComboSequence activeCombo;
    private int comboIndex;
    private String nextMoveId = "";

    private int phase = 1;
    private float healthRatio = 1.0f;
    private double targetDistanceSq;

    public VenerableCombatAI() {
        for (BehaviorState state : BehaviorState.values()) {
            comboRegistry.put(state, new ArrayList<>());
        }
    }

    public void setCombatContext(int phase, float healthRatio, double targetDistanceSq) {
        this.phase = Math.max(1, phase);
        this.healthRatio = clamp01(healthRatio);
        this.targetDistanceSq = Math.max(0.0d, targetDistanceSq);
    }

    public BehaviorState getCurrentState() {
        return currentState;
    }

    public void tick() {
        updateCooldowns();
        evaluateAndSwitchState();
        advanceCombo();
    }

    public String getNextMoveId() {
        return nextMoveId;
    }

    public void registerCombo(BehaviorState state, ComboSequence combo) {
        if (state == null || combo == null || combo.moveIds.length == 0) {
            return;
        }
        List<ComboSequence> combos = comboRegistry.computeIfAbsent(state, key -> new ArrayList<>());
        combos.removeIf(existing -> existing.getName().equals(combo.getName()));
        combos.add(combo);
    }

    public void setCooldown(String moveId, int ticks) {
        if (moveId == null || moveId.isBlank()) {
            return;
        }
        if (ticks <= 0) {
            cooldowns.remove(moveId);
        } else {
            cooldowns.put(moveId, ticks);
        }
    }

    public boolean isAvailable(String moveId) {
        if (moveId == null || moveId.isBlank()) {
            return false;
        }
        return cooldowns.getOrDefault(moveId, 0) <= 0;
    }

    public void forceState(BehaviorState state) {
        if (state == null) {
            return;
        }
        if (currentState != state) {
            currentState = state;
            activeCombo = null;
            comboIndex = 0;
        }
    }

    public BehaviorState evaluateAndSwitchState() {
        BehaviorState evaluated;
        if (phase >= 3 && healthRatio < 0.15f) {
            evaluated = BehaviorState.BERSERK;
        } else if (targetDistanceSq > 256.0d) {
            evaluated = BehaviorState.HUNTING;
        } else if (healthRatio < 0.40f) {
            evaluated = BehaviorState.DEFENSIVE;
        } else if (phase >= 2) {
            evaluated = BehaviorState.AGGRESSIVE;
        } else {
            evaluated = BehaviorState.TACTICAL;
        }

        if (evaluated != currentState) {
            currentState = evaluated;
            activeCombo = null;
            comboIndex = 0;
        }
        return currentState;
    }

    public ComboSequence selectBestCombo() {
        ComboSequence best = selectBestFrom(comboRegistry.getOrDefault(currentState, List.of()));
        if (best != null) {
            return best;
        }
        if (currentState != BehaviorState.TACTICAL) {
            best = selectBestFrom(comboRegistry.getOrDefault(BehaviorState.TACTICAL, List.of()));
            if (best != null) {
                return best;
            }
        }
        if (currentState != BehaviorState.AGGRESSIVE) {
            return selectBestFrom(comboRegistry.getOrDefault(BehaviorState.AGGRESSIVE, List.of()));
        }
        return null;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("State", currentState.name());
        tag.putInt("Phase", phase);
        tag.putFloat("HealthRatio", healthRatio);
        tag.putDouble("TargetDistanceSq", targetDistanceSq);
        tag.putInt("ComboIndex", comboIndex);
        tag.putString("ActiveCombo", activeCombo == null ? "" : activeCombo.getName());
        tag.putString("NextMoveId", nextMoveId);

        ListTag cooldownList = new ListTag();
        for (Map.Entry<String, Integer> entry : cooldowns.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null || entry.getValue() <= 0) {
                continue;
            }
            CompoundTag cdTag = new CompoundTag();
            cdTag.putString("MoveId", entry.getKey());
            cdTag.putInt("Ticks", entry.getValue());
            cooldownList.add(cdTag);
        }
        tag.put("Cooldowns", cooldownList);
        return tag;
    }

    public void load(CompoundTag tag) {
        if (tag == null) {
            return;
        }

        if (tag.contains("State", Tag.TAG_STRING)) {
            String stateName = tag.getString("State");
            if (!stateName.isBlank()) {
                try {
                    currentState = BehaviorState.valueOf(stateName);
                } catch (IllegalArgumentException ignored) {
                    currentState = BehaviorState.TACTICAL;
                }
            }
        }

        phase = Math.max(1, tag.getInt("Phase"));
        healthRatio = clamp01(tag.getFloat("HealthRatio"));
        targetDistanceSq = Math.max(0.0d, tag.getDouble("TargetDistanceSq"));
        comboIndex = Math.max(0, tag.getInt("ComboIndex"));
        nextMoveId = tag.getString("NextMoveId");

        cooldowns.clear();
        if (tag.contains("Cooldowns", Tag.TAG_LIST)) {
            ListTag cooldownList = tag.getList("Cooldowns", Tag.TAG_COMPOUND);
            for (int i = 0; i < cooldownList.size(); i++) {
                CompoundTag cdTag = cooldownList.getCompound(i);
                String moveId = cdTag.getString("MoveId");
                int ticks = cdTag.getInt("Ticks");
                if (moveId != null && !moveId.isBlank() && ticks > 0) {
                    cooldowns.put(moveId, ticks);
                }
            }
        }

        String activeComboName = tag.getString("ActiveCombo");
        activeCombo = findComboByName(activeComboName);
        if (activeCombo != null && activeCombo.moveIds.length > 0) {
            comboIndex %= activeCombo.moveIds.length;
        } else {
            activeCombo = null;
            comboIndex = 0;
        }

        if (nextMoveId == null) {
            nextMoveId = "";
        }
    }

    private void updateCooldowns() {
        Iterator<Map.Entry<String, Integer>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }

    private void advanceCombo() {
        nextMoveId = "";

        if (activeCombo == null || !activeCombo.matches(phase, targetDistanceSq)) {
            activeCombo = selectBestCombo();
            comboIndex = 0;
        }
        if (activeCombo == null) {
            return;
        }

        String picked = pickAvailableMove(activeCombo);
        if (!picked.isEmpty()) {
            nextMoveId = picked;
            return;
        }

        activeCombo = selectBestCombo();
        comboIndex = 0;
        if (activeCombo == null) {
            return;
        }

        nextMoveId = pickAvailableMove(activeCombo);
    }

    private String pickAvailableMove(ComboSequence combo) {
        if (combo == null || combo.moveIds.length == 0) {
            return "";
        }

        int size = combo.moveIds.length;
        int startIndex = Math.floorMod(comboIndex, size);
        for (int offset = 0; offset < size; offset++) {
            int idx = (startIndex + offset) % size;
            String moveId = combo.moveIds[idx];
            if (!isAvailable(moveId)) {
                continue;
            }
            comboIndex = (idx + 1) % size;
            return moveId;
        }
        return "";
    }

    private ComboSequence selectBestFrom(List<ComboSequence> combos) {
        ComboSequence best = null;
        int bestScore = -1;
        for (ComboSequence combo : combos) {
            int score = scoreCombo(combo);
            if (score > bestScore) {
                best = combo;
                bestScore = score;
            } else if (score == bestScore && best != null && combo.moveIds.length < best.moveIds.length) {
                best = combo;
            }
        }
        return best;
    }

    private int scoreCombo(ComboSequence combo) {
        if (combo == null || !combo.matches(phase, targetDistanceSq) || combo.moveIds.length == 0) {
            return -1;
        }

        int readyCount = 0;
        for (String moveId : combo.moveIds) {
            if (isAvailable(moveId)) {
                readyCount++;
            }
        }
        if (readyCount <= 0) {
            return -1;
        }

        int phaseAdvantage = Math.max(0, phase - combo.minPhase);
        int distanceAdvantage = (int) Math.min(200.0d, Math.max(0.0d, combo.maxDistanceSq - targetDistanceSq));
        return readyCount * 100 + phaseAdvantage * 10 + distanceAdvantage;
    }

    private ComboSequence findComboByName(String comboName) {
        if (comboName == null || comboName.isBlank()) {
            return null;
        }
        for (List<ComboSequence> combos : comboRegistry.values()) {
            for (ComboSequence combo : combos) {
                if (combo.getName().equals(comboName)) {
                    return combo;
                }
            }
        }
        return null;
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
