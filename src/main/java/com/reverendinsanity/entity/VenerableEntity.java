package com.reverendinsanity.entity;

import com.reverendinsanity.ReverendInsanity;
import com.reverendinsanity.client.vfx.VfxHelper;
import com.reverendinsanity.client.vfx.VfxType;
import com.reverendinsanity.combat.TerrainModifier;
import com.reverendinsanity.core.path.DaoPath;
import com.reverendinsanity.entity.ai.VenerableCombatAI;
import com.reverendinsanity.entity.ai.VenerableCombatAI.BehaviorState;
import com.reverendinsanity.entity.ai.VenerableCombatAI.ComboSequence;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 九转尊者实体重构：十尊战斗AI、领域地形、特效与阶段化杀招总控
public class VenerableEntity extends Monster {

    private static final EntityDataAccessor<String> DATA_TYPE =
        SynchedEntityData.defineId(VenerableEntity.class, EntityDataSerializers.STRING);

    private static final ResourceLocation VEN_HP_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_hp");
    private static final ResourceLocation VEN_ATK_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_atk");
    private static final ResourceLocation VEN_ARMOR_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_armor");
    private static final ResourceLocation VEN_SPEED_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_speed");
    private static final ResourceLocation SUMMON_HP_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_summon_hp");
    private static final ResourceLocation SUMMON_ATK_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_summon_atk");
    private static final ResourceLocation SUMMON_SPEED_MOD = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, "venerable_summon_speed");

    private static final String[] WUJI_MOVES = {
        "wuji_law_chisel", "wuji_madness_wave", "wuji_order_barrage", "wuji_absolute_law", "wuji_law_bind",
        "wuji_all_laws_one", "wuji_forbidden_domain", "wuji_law_chain_drag", "wuji_order_judgment", "wuji_forbidden_cage",
        "wuji_myriad_law_origin", "wuji_law_trial", "wuji_world_end", "wuji_order_spear", "wuji_boundless_cross",
        "wuji_law_reflect", "wuji_forbidden_seal", "wuji_pressure_aura", "wuji_law_shield", "wuji_void_all_laws",
        "wuji_immortal_law", "wuji_prove_by_force"
    };

    private static final String[] YOUHUN_MOVES = {
        "youhun_soul_strike", "youhun_soul_reap", "youhun_soul_devour_ultimate", "youhun_soul_split", "youhun_nine_revolution_dream",
        "youhun_shadow_clone", "youhun_soul_storm", "youhun_soul_gaze", "youhun_devour_passive", "youhun_soul_truth",
        "youhun_shadow_escape", "youhun_dream_cage", "youhun_mental_crush", "youhun_soul_puppet", "youhun_clone_detonate",
        "youhun_absorb_soul", "youhun_ghost_domain", "youhun_dream_butterfly", "youhun_night_of_ghosts", "youhun_undying_soul"
    };

    private static final String[] DAOTIAN_MOVES = {
        "daotian_space_cut", "daotian_formless_hand", "daotian_swap_heaven", "daotian_space_fold", "daotian_steal_time",
        "daotian_void_cage", "daotian_ten_thousand_hands", "daotian_space_swap", "daotian_steal_power", "daotian_void_step",
        "daotian_space_rift", "daotian_steal_memory", "daotian_no_gap_domain", "daotian_space_compress", "daotian_steal_defense",
        "daotian_void_eye", "daotian_space_exile", "daotian_same_realm_clone", "daotian_copy_art", "daotian_formless_fist"
    };

    private static final String[] KUANGMAN_MOVES = {
        "kuangman_savage_slam", "kuangman_dragon_form", "kuangman_tiger_form", "kuangman_eagle_form", "kuangman_snake_form",
        "kuangman_bear_form", "kuangman_savage_power", "kuangman_heaven_flip", "kuangman_break_all_laws", "kuangman_beast_summon",
        "kuangman_ten_thousand_beasts", "kuangman_undying_body", "kuangman_charge", "kuangman_split_earth", "kuangman_thousand_fall",
        "kuangman_savage_tornado", "kuangman_devour_heal", "kuangman_giant_ape_fist", "kuangman_shock_stomp", "kuangman_blood_awakening"
    };

    private static final String[] JUYANG_MOVES = {
        "juyang_golden_strike", "juyang_blood_sacrifice", "juyang_sun_will", "juyang_solar_judgment", "juyang_fortune_deflect",
        "juyang_blood_contract", "juyang_golden_bloodline", "juyang_sun_burst", "juyang_fortune_shift", "juyang_blood_tide",
        "juyang_sun_spear", "juyang_fortune_steal", "juyang_blood_boil", "juyang_sun_fall", "juyang_fate_hand",
        "juyang_blood_formation", "juyang_true_sun_tower", "juyang_immortal_body"
    };

    private static final String[] XINGXIU_MOVES = {
        "xingxiu_star_projection", "xingxiu_star_needle", "xingxiu_star_trap", "xingxiu_star_extinction", "xingxiu_destiny_calculation",
        "xingxiu_star_cage", "xingxiu_star_board", "xingxiu_star_chain", "xingxiu_star_clone", "xingxiu_star_armor",
        "xingxiu_destiny_mark", "xingxiu_meteor_fall", "xingxiu_star_eye", "xingxiu_star_lock", "xingxiu_predicted_counter",
        "xingxiu_star_gravity", "xingxiu_star_pressure", "xingxiu_star_end"
    };

    private static final String[] LETU_MOVES = {
        "letu_earth_spike_array", "letu_heaven_force", "letu_earth_domain", "letu_earth_barrier", "letu_heaven_earth_formation",
        "letu_all_to_earth", "letu_earth_heaven_split", "letu_heaven_punish", "letu_earth_shield", "letu_heaven_pillar",
        "letu_earth_prison", "letu_heaven_mercy", "letu_earth_quake", "letu_heaven_judgment", "letu_unbreak_wall",
        "letu_all_return_origin", "letu_earth_spirit_summon", "letu_unity_of_heaven_earth"
    };

    private static final String[] YUANSHI_MOVES = {
        "yuanshi_qi_blast", "yuanshi_qi_barrage", "yuanshi_qi_suppress", "yuanshi_three_qi_combo", "yuanshi_yinyang_swap",
        "yuanshi_qi_barrier", "yuanshi_pressure", "yuanshi_qi_tornado", "yuanshi_yinyang_strike", "yuanshi_five_forbidden_light",
        "yuanshi_qi_guard", "yuanshi_taiching_cleanse", "yuanshi_yinyang_harmony", "yuanshi_qi_pierce", "yuanshi_all_qi_absorb",
        "yuanshi_yinyang_grind", "yuanshi_primal_qi", "yuanshi_origin_strike"
    };

    private static final String[] HONGLIAN_MOVES = {
        "honglian_time_burst", "honglian_time_freeze", "honglian_fate_break", "honglian_ancient_predecessor", "honglian_spring_cicada",
        "honglian_time_rewind", "honglian_past_present", "honglian_red_lotus_fire", "honglian_time_reverse_position", "honglian_space_fracture",
        "honglian_time_haste", "honglian_red_lotus_domain", "honglian_time_paradox", "honglian_space_banish", "honglian_time_spear",
        "honglian_red_lotus_rage", "honglian_causality_reverse", "honglian_space_time_collapse"
    };

    private static final String[] YUANLIAN_MOVES = {
        "yuanlian_vine_whip", "yuanlian_vine_bind", "yuanlian_spore_rain", "yuanlian_genesis_lotus", "yuanlian_paint_prison",
        "yuanlian_regeneration", "yuanlian_vine_wall", "yuanlian_paint_clone", "yuanlian_wood_revive", "yuanlian_poison_vine",
        "yuanlian_genesis_scroll", "yuanlian_tree_of_life", "yuanlian_vine_burst", "yuanlian_paint_seal", "yuanlian_wood_spirit_summon",
        "yuanlian_genesis_grand_lotus"
    };

    private static final class TimedModifier {
        private final Holder<Attribute> attribute;
        private int ticks;

        private TimedModifier(Holder<Attribute> attribute, int ticks) {
            this.attribute = attribute;
            this.ticks = ticks;
        }
    }

    private static final class DamageMark {
        private int ticks;
        private final float multiplier;

        private DamageMark(int ticks, float multiplier) {
            this.ticks = ticks;
            this.multiplier = multiplier;
        }
    }

    private static final class AreaEffect {
        private final Vec3 center;
        private final double radius;
        private final float percentPerSecond;
        private int ticks;
        private final boolean magic;
        private final int slowAmp;
        private final int weakAmp;
        private final boolean pullToCenter;
        private final boolean randomBlink;
        private final VfxType vfxType;
        private final int color;
        private final float scale;
        private final DaoPath terrainPath;
        private final int terrainRadius;

        private AreaEffect(Vec3 center, double radius, float percentPerSecond, int ticks, boolean magic,
                           int slowAmp, int weakAmp, boolean pullToCenter, boolean randomBlink,
                           VfxType vfxType, int color, float scale, DaoPath terrainPath, int terrainRadius) {
            this.center = center;
            this.radius = radius;
            this.percentPerSecond = percentPerSecond;
            this.ticks = ticks;
            this.magic = magic;
            this.slowAmp = slowAmp;
            this.weakAmp = weakAmp;
            this.pullToCenter = pullToCenter;
            this.randomBlink = randomBlink;
            this.vfxType = vfxType;
            this.color = color;
            this.scale = scale;
            this.terrainPath = terrainPath;
            this.terrainRadius = terrainRadius;
        }
    }

    private static final class StarTrap {
        private final Vec3 pos;
        private int ticks;
        private final float percent;
        private final double radius;

        private StarTrap(Vec3 pos, int ticks, float percent, double radius) {
            this.pos = pos;
            this.ticks = ticks;
            this.percent = percent;
            this.radius = radius;
        }
    }

    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.literal("尊者"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS
    );

    private final Map<String, Integer> moveCooldownTicks = new HashMap<>();
    private final Map<String, Method> moveMethodCache = new HashMap<>();
    private final Map<ResourceLocation, TimedModifier> timedModifiers = new HashMap<>();
    private final Map<UUID, DamageMark> damageMarks = new HashMap<>();
    private final List<AreaEffect> areaEffects = new ArrayList<>();
    private final List<UUID> summonIds = new ArrayList<>();
    private final List<StarTrap> starTraps = new ArrayList<>();
    private final Map<UUID, ArrayDeque<Vec3>> positionHistory = new HashMap<>();

    private VenerableType venerableType = VenerableType.WU_JI;
    private VenerableCombatAI combatAI = new VenerableCombatAI();

    private int currentPhase = 1;
    private int abilityCooldown = 80;

    private int wujiMadnessStacks = 0;
    private int wujiShieldTicks = 0;
    private boolean wujiImmortalUsed = false;

    private boolean youHunSoulSplitUsed = false;
    private int youHunDevourStacks = 0;

    private int hongLianFreezeTicks = 0;
    private boolean hongLianCicadaUsed = false;
    private int hongLianRageTicks = 0;

    private int kuangManAwakenTicks = 0;
    private int kuangManActiveForm = 0;
    private int kuangManFormTicks = 0;

    private boolean juYangImmortalUsed = false;
    private int juYangDrainTicks = 0;

    private int absoluteInvulTicks = 0;
    private int mercyTicks = 0;
    private int yuanLianRegenTicker = 0;

    private float recentDamagePercent = 0.0f;
    private int recentDamageTicks = 0;
    private UUID recentAttacker = null;
    private boolean xingXiuPredictActive = false;
    private float hongLianSavedHealth = 0.0f;

    public VenerableEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.bossEvent.setVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 1500.0)
            .add(Attributes.ATTACK_DAMAGE, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.35)
            .add(Attributes.ARMOR, 20.0)
            .add(Attributes.ARMOR_TOUGHNESS, 10.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
            .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE, VenerableType.WU_JI.name());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void setVenerableType(VenerableType type) {
        this.venerableType = type == null ? VenerableType.WU_JI : type;
        this.entityData.set(DATA_TYPE, this.venerableType.name());
        this.bossEvent.setName(Component.literal(this.venerableType.displayNameCN));
        this.bossEvent.setColor(this.venerableType.isDemon() ? BossEvent.BossBarColor.RED : BossEvent.BossBarColor.BLUE);
        applyVenerableStats(this.venerableType);
        resetCombatState();
        initializeCombatAI();
    }

    public VenerableType getVenerableType() {
        return this.venerableType;
    }

    public String getVenerableTypeName() {
        return this.entityData.get(DATA_TYPE);
    }

    private void applyVenerableStats(VenerableType type) {
        applyAttributeModifier(Attributes.MAX_HEALTH, VEN_HP_MOD, type.maxHealth - 1500.0, AttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(Attributes.ATTACK_DAMAGE, VEN_ATK_MOD, type.attackDamage - 30.0, AttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(Attributes.ARMOR, VEN_ARMOR_MOD, type.armor - 20.0, AttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(Attributes.MOVEMENT_SPEED, VEN_SPEED_MOD, type.moveSpeed - 0.35, AttributeModifier.Operation.ADD_VALUE);
        this.setHealth(this.getMaxHealth());
    }

    private void applyAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(id);
        if (Math.abs(amount) > 0.000001d) {
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
        }
    }

    private void resetCombatState() {
        this.moveCooldownTicks.clear();
        this.moveMethodCache.clear();
        this.timedModifiers.clear();
        this.damageMarks.clear();
        this.areaEffects.clear();
        this.summonIds.clear();
        this.starTraps.clear();
        this.positionHistory.clear();
        this.currentPhase = 1;
        this.abilityCooldown = 80;
        this.wujiMadnessStacks = 0;
        this.wujiShieldTicks = 0;
        this.wujiImmortalUsed = false;
        this.youHunSoulSplitUsed = false;
        this.youHunDevourStacks = 0;
        this.hongLianFreezeTicks = 0;
        this.hongLianCicadaUsed = false;
        this.hongLianRageTicks = 0;
        this.kuangManAwakenTicks = 0;
        this.kuangManActiveForm = 0;
        this.kuangManFormTicks = 0;
        this.juYangImmortalUsed = false;
        this.juYangDrainTicks = 0;
        this.absoluteInvulTicks = 0;
        this.mercyTicks = 0;
        this.yuanLianRegenTicker = 0;
        this.recentDamagePercent = 0.0f;
        this.recentDamageTicks = 0;
        this.recentAttacker = null;
        this.xingXiuPredictActive = false;
        this.hongLianSavedHealth = 0.0f;
    }

    private void initializeCombatAI() {
        this.combatAI = new VenerableCombatAI();
        switch (this.venerableType) {
            case WU_JI -> initWujiCombos();
            case YOU_HUN -> initYouHunCombos();
            case DAO_TIAN -> initDaoTianCombos();
            case KUANG_MAN -> initKuangManCombos();
            case JU_YANG -> initJuYangCombos();
            case XING_XIU -> initXingXiuCombos();
            case LE_TU -> initLeTuCombos();
            case YUAN_SHI -> initYuanShiCombos();
            case HONG_LIAN -> initHongLianCombos();
            case YUAN_LIAN -> initYuanLianCombos();
        }
    }

    private void registerMoves(int cooldown, String... moveIds) {
        for (String moveId : moveIds) {
            this.moveCooldownTicks.put(moveId, cooldown);
        }
    }

    private void registerMoveCooldown(String moveId, int cooldown) {
        this.moveCooldownTicks.put(moveId, cooldown);
    }

    private void registerCombo(BehaviorState state, String name, String[] moveIds, int minPhase, double maxDistanceSq) {
        this.combatAI.registerCombo(state, new ComboSequence(name, moveIds, minPhase, maxDistanceSq));
    }

    private void initWujiCombos() {
        registerMoves(60, WUJI_MOVES);
        registerMoveCooldown("wuji_myriad_law_origin", 180);
        registerMoveCooldown("wuji_prove_by_force", 200);
        registerMoveCooldown("wuji_immortal_law", 220);
        registerCombo(BehaviorState.TACTICAL, "wuji_cc", new String[]{
            "wuji_law_bind", "wuji_madness_wave", "wuji_madness_wave", "wuji_madness_wave", "wuji_all_laws_one"
        }, 1, 100.0);
        registerCombo(BehaviorState.AGGRESSIVE, "wuji_ranged", new String[]{
            "wuji_order_barrage", "wuji_forbidden_domain", "wuji_absolute_law", "wuji_order_spear"
        }, 2, 400.0);
        registerCombo(BehaviorState.DEFENSIVE, "wuji_def", new String[]{
            "wuji_law_shield", "wuji_void_all_laws", "wuji_law_reflect", "wuji_law_chain_drag"
        }, 1, 225.0);
        registerCombo(BehaviorState.BERSERK, "wuji_p3", new String[]{
            "wuji_myriad_law_origin", "wuji_world_end", "wuji_boundless_cross", "wuji_prove_by_force"
        }, 3, 900.0);
    }

    private void initYouHunCombos() {
        registerMoves(58, YOUHUN_MOVES);
        registerMoveCooldown("youhun_night_of_ghosts", 170);
        registerMoveCooldown("youhun_undying_soul", 200);
        registerCombo(BehaviorState.TACTICAL, "youhun_control", new String[]{
            "youhun_soul_gaze", "youhun_dream_cage", "youhun_nine_revolution_dream", "youhun_soul_puppet"
        }, 1, 196.0);
        registerCombo(BehaviorState.AGGRESSIVE, "youhun_burst", new String[]{
            "youhun_soul_strike", "youhun_shadow_escape", "youhun_soul_truth", "youhun_absorb_soul"
        }, 2, 324.0);
        registerCombo(BehaviorState.DEFENSIVE, "youhun_clone", new String[]{
            "youhun_shadow_clone", "youhun_soul_split", "youhun_clone_detonate", "youhun_undying_soul"
        }, 1, 625.0);
        registerCombo(BehaviorState.BERSERK, "youhun_p3", new String[]{
            "youhun_night_of_ghosts", "youhun_soul_devour_ultimate", "youhun_ghost_domain", "youhun_dream_butterfly"
        }, 3, 900.0);
    }

    private void initDaoTianCombos() {
        registerMoves(56, DAOTIAN_MOVES);
        registerMoveCooldown("daotian_ten_thousand_hands", 160);
        registerMoveCooldown("daotian_copy_art", 130);
        registerCombo(BehaviorState.TACTICAL, "daotian_control", new String[]{
            "daotian_space_fold", "daotian_steal_time", "daotian_space_swap", "daotian_void_cage"
        }, 1, 225.0);
        registerCombo(BehaviorState.AGGRESSIVE, "daotian_steal", new String[]{
            "daotian_formless_hand", "daotian_swap_heaven", "daotian_steal_power", "daotian_formless_fist"
        }, 2, 625.0);
        registerCombo(BehaviorState.DEFENSIVE, "daotian_space", new String[]{
            "daotian_void_step", "daotian_space_exile", "daotian_space_compress", "daotian_steal_defense"
        }, 1, 625.0);
        registerCombo(BehaviorState.BERSERK, "daotian_p3", new String[]{
            "daotian_ten_thousand_hands", "daotian_no_gap_domain", "daotian_space_rift", "daotian_copy_art"
        }, 3, 900.0);
    }

    private void initKuangManCombos() {
        registerMoves(52, KUANGMAN_MOVES);
        registerMoveCooldown("kuangman_ten_thousand_beasts", 165);
        registerMoveCooldown("kuangman_blood_awakening", 170);
        registerCombo(BehaviorState.TACTICAL, "kuangman_combo", new String[]{
            "kuangman_tiger_form", "kuangman_charge", "kuangman_shock_stomp", "kuangman_split_earth"
        }, 1, 144.0);
        registerCombo(BehaviorState.AGGRESSIVE, "kuangman_forms", new String[]{
            "kuangman_dragon_form", "kuangman_eagle_form", "kuangman_snake_form", "kuangman_giant_ape_fist"
        }, 2, 400.0);
        registerCombo(BehaviorState.DEFENSIVE, "kuangman_survive", new String[]{
            "kuangman_bear_form", "kuangman_devour_heal", "kuangman_undying_body", "kuangman_savage_power"
        }, 1, 324.0);
        registerCombo(BehaviorState.BERSERK, "kuangman_p3", new String[]{
            "kuangman_ten_thousand_beasts", "kuangman_heaven_flip", "kuangman_thousand_fall", "kuangman_blood_awakening"
        }, 3, 900.0);
    }

    private void initJuYangCombos() {
        registerMoves(62, JUYANG_MOVES);
        registerMoveCooldown("juyang_sun_fall", 150);
        registerMoveCooldown("juyang_immortal_body", 220);
        registerCombo(BehaviorState.TACTICAL, "juyang_spear", new String[]{
            "juyang_golden_strike", "juyang_sun_spear", "juyang_fortune_shift", "juyang_fortune_steal"
        }, 1, 400.0);
        registerCombo(BehaviorState.AGGRESSIVE, "juyang_blood", new String[]{
            "juyang_blood_sacrifice", "juyang_blood_contract", "juyang_blood_tide", "juyang_blood_boil"
        }, 2, 324.0);
        registerCombo(BehaviorState.DEFENSIVE, "juyang_def", new String[]{
            "juyang_fortune_deflect", "juyang_true_sun_tower", "juyang_golden_bloodline", "juyang_immortal_body"
        }, 1, 256.0);
        registerCombo(BehaviorState.BERSERK, "juyang_p3", new String[]{
            "juyang_sun_will", "juyang_solar_judgment", "juyang_sun_burst", "juyang_sun_fall", "juyang_fate_hand"
        }, 3, 900.0);
    }

    private void initXingXiuCombos() {
        registerMoves(60, XINGXIU_MOVES);
        registerMoveCooldown("xingxiu_star_end", 170);
        registerCombo(BehaviorState.TACTICAL, "xingxiu_predict", new String[]{
            "xingxiu_star_trap", "xingxiu_star_needle", "xingxiu_destiny_calculation", "xingxiu_star_lock"
        }, 1, 256.0);
        registerCombo(BehaviorState.AGGRESSIVE, "xingxiu_chain", new String[]{
            "xingxiu_star_projection", "xingxiu_star_chain", "xingxiu_destiny_mark", "xingxiu_meteor_fall"
        }, 2, 400.0);
        registerCombo(BehaviorState.DEFENSIVE, "xingxiu_board", new String[]{
            "xingxiu_star_board", "xingxiu_star_armor", "xingxiu_star_eye", "xingxiu_predicted_counter"
        }, 1, 400.0);
        registerCombo(BehaviorState.BERSERK, "xingxiu_p3", new String[]{
            "xingxiu_star_extinction", "xingxiu_star_gravity", "xingxiu_star_pressure", "xingxiu_star_end"
        }, 3, 900.0);
    }

    private void initLeTuCombos() {
        registerMoves(64, LETU_MOVES);
        registerMoveCooldown("letu_unity_of_heaven_earth", 180);
        registerMoveCooldown("letu_unbreak_wall", 130);
        registerCombo(BehaviorState.TACTICAL, "letu_control", new String[]{
            "letu_earth_spike_array", "letu_earth_prison", "letu_heaven_pillar", "letu_earth_quake"
        }, 1, 196.0);
        registerCombo(BehaviorState.AGGRESSIVE, "letu_earth", new String[]{
            "letu_earth_domain", "letu_all_to_earth", "letu_earth_heaven_split", "letu_heaven_judgment"
        }, 2, 625.0);
        registerCombo(BehaviorState.DEFENSIVE, "letu_guard", new String[]{
            "letu_earth_barrier", "letu_heaven_earth_formation", "letu_unbreak_wall", "letu_all_return_origin"
        }, 1, 256.0);
        registerCombo(BehaviorState.BERSERK, "letu_p3", new String[]{
            "letu_heaven_punish", "letu_earth_shield", "letu_earth_spirit_summon", "letu_unity_of_heaven_earth"
        }, 3, 900.0);
    }

    private void initYuanShiCombos() {
        registerMoves(58, YUANSHI_MOVES);
        registerMoveCooldown("yuanshi_origin_strike", 170);
        registerCombo(BehaviorState.TACTICAL, "yuanshi_control", new String[]{
            "yuanshi_qi_suppress", "yuanshi_five_forbidden_light", "yuanshi_yinyang_strike", "yuanshi_qi_guard"
        }, 1, 196.0);
        registerCombo(BehaviorState.AGGRESSIVE, "yuanshi_combo", new String[]{
            "yuanshi_qi_blast", "yuanshi_qi_barrage", "yuanshi_three_qi_combo", "yuanshi_qi_pierce"
        }, 2, 400.0);
        registerCombo(BehaviorState.DEFENSIVE, "yuanshi_harmony", new String[]{
            "yuanshi_taiching_cleanse", "yuanshi_yinyang_harmony", "yuanshi_all_qi_absorb", "yuanshi_qi_barrier"
        }, 1, 324.0);
        registerCombo(BehaviorState.BERSERK, "yuanshi_p3", new String[]{
            "yuanshi_primal_qi", "yuanshi_yinyang_grind", "yuanshi_yinyang_swap", "yuanshi_origin_strike"
        }, 3, 900.0);
    }

    private void initHongLianCombos() {
        registerMoves(56, HONGLIAN_MOVES);
        registerMoveCooldown("honglian_space_time_collapse", 190);
        registerMoveCooldown("honglian_spring_cicada", 210);
        registerCombo(BehaviorState.TACTICAL, "honglian_control", new String[]{
            "honglian_time_burst", "honglian_time_reverse_position", "honglian_fate_break", "honglian_time_paradox"
        }, 1, 324.0);
        registerCombo(BehaviorState.AGGRESSIVE, "honglian_space", new String[]{
            "honglian_space_fracture", "honglian_space_banish", "honglian_time_spear", "honglian_red_lotus_fire"
        }, 2, 625.0);
        registerCombo(BehaviorState.DEFENSIVE, "honglian_recover", new String[]{
            "honglian_time_rewind", "honglian_time_haste", "honglian_spring_cicada", "honglian_ancient_predecessor"
        }, 1, 400.0);
        registerCombo(BehaviorState.BERSERK, "honglian_p3", new String[]{
            "honglian_time_freeze", "honglian_past_present", "honglian_red_lotus_domain", "honglian_red_lotus_rage", "honglian_space_time_collapse"
        }, 3, 900.0);
    }

    private void initYuanLianCombos() {
        registerMoves(62, YUANLIAN_MOVES);
        registerMoveCooldown("yuanlian_genesis_grand_lotus", 185);
        registerCombo(BehaviorState.TACTICAL, "yuanlian_control", new String[]{
            "yuanlian_vine_whip", "yuanlian_vine_bind", "yuanlian_poison_vine", "yuanlian_paint_prison"
        }, 1, 196.0);
        registerCombo(BehaviorState.AGGRESSIVE, "yuanlian_wood", new String[]{
            "yuanlian_spore_rain", "yuanlian_vine_burst", "yuanlian_tree_of_life", "yuanlian_genesis_lotus"
        }, 2, 400.0);
        registerCombo(BehaviorState.DEFENSIVE, "yuanlian_recover", new String[]{
            "yuanlian_regeneration", "yuanlian_wood_revive", "yuanlian_genesis_scroll", "yuanlian_paint_clone"
        }, 1, 400.0);
        registerCombo(BehaviorState.BERSERK, "yuanlian_p3", new String[]{
            "yuanlian_vine_wall", "yuanlian_paint_seal", "yuanlian_wood_spirit_summon", "yuanlian_genesis_grand_lotus"
        }, 3, 900.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        updatePhase();
        tickTimedModifiers();
        tickDamageMarks();
        tickAreaEffects();
        tickStarTraps();
        tickSummons();
        tickPositionHistory();
        tickStateTimers();
        tickPassives();

        if (this.abilityCooldown > 0) {
            this.abilityCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (this.abilityCooldown > 0) {
            return;
        }
        if (this.venerableType == VenerableType.LE_TU && this.mercyTicks > 0) {
            return;
        }

        performVenerableAttack(target);
    }

    private void updatePhase() {
        float healthRatio = this.getHealth() / this.getMaxHealth();
        this.currentPhase = healthRatio > 0.66f ? 1 : (healthRatio > 0.33f ? 2 : 3);
    }

    private void tickStateTimers() {
        if (this.wujiShieldTicks > 0) {
            this.wujiShieldTicks--;
        }
        if (this.hongLianFreezeTicks > 0) {
            this.hongLianFreezeTicks--;
            for (LivingEntity living : getEnemies(this.position(), 12.0)) {
                living.setDeltaMovement(0.0, 0.0, 0.0);
                living.hurtMarked = true;
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 7));
            }
        }
        if (this.hongLianRageTicks > 0) {
            this.hongLianRageTicks--;
            if (this.tickCount % 20 == 0) {
                hurtAreaPercent(this.position(), 8.0, 0.04f, true);
                spawnVfx(VfxType.TIME_DISTORTION, getX(), getY() + 1.0, getZ(), 0.0, 1.0, 0.0, 0xFFFF0033, 2.0f, 30);
            }
        }
        if (this.kuangManAwakenTicks > 0) {
            this.kuangManAwakenTicks--;
            if (this.tickCount % 20 == 0) {
                selfDamagePercent(0.02f);
            }
        }
        if (this.kuangManFormTicks > 0) {
            this.kuangManFormTicks--;
            if (this.kuangManFormTicks <= 0) {
                this.kuangManActiveForm = 0;
                resetKuangmanFormBuffs();
            }
        }
        if (this.juYangDrainTicks > 0) {
            this.juYangDrainTicks--;
            if (this.tickCount % 20 == 0) {
                selfDamagePercent(0.05f);
            }
        }
        if (this.mercyTicks > 0) {
            this.mercyTicks--;
            if (this.tickCount % 20 == 0) {
                healPercent(0.05f);
            }
        }
        if (this.absoluteInvulTicks > 0) {
            this.absoluteInvulTicks--;
        }
        if (this.recentDamageTicks > 0) {
            this.recentDamageTicks--;
        } else {
            this.recentDamagePercent = 0.0f;
            this.recentAttacker = null;
        }
    }

    private void tickPassives() {
        if (this.venerableType == VenerableType.HONG_LIAN && this.tickCount % 40 == 0) {
            this.hongLianSavedHealth = this.getHealth();
        }
        if (this.venerableType == VenerableType.WU_JI || this.venerableType == VenerableType.YUAN_SHI) {
            for (LivingEntity living : getEnemies(this.position(), 8.0)) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 1));
            }
        }
        if (this.tickCount % 100 == 0) {
            switch (this.venerableType) {
                case JU_YANG -> healPercent(0.005f);
                case LE_TU -> healPercent(0.003f);
                case YUAN_LIAN -> {
                    this.yuanLianRegenTicker++;
                    healPercent(0.01f);
                }
                default -> {}
            }
        }
    }

    private void tickSummons() {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        Iterator<UUID> iterator = this.summonIds.iterator();
        while (iterator.hasNext()) {
            UUID id = iterator.next();
            Entity entity = sl.getEntity(id);
            if (!(entity instanceof LivingEntity living) || !living.isAlive()) {
                iterator.remove();
            }
        }
    }

    private void tickPositionHistory() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        ArrayDeque<Vec3> history = this.positionHistory.computeIfAbsent(target.getUUID(), key -> new ArrayDeque<>());
        history.addLast(target.position());
        while (history.size() > 80) {
            history.removeFirst();
        }
    }

    private Vec3 getPositionAgo(LivingEntity target, int ticksAgo) {
        ArrayDeque<Vec3> history = this.positionHistory.get(target.getUUID());
        if (history == null || history.isEmpty()) {
            return target.position();
        }
        int wanted = Math.max(0, history.size() - 1 - ticksAgo);
        int index = 0;
        for (Vec3 pos : history) {
            if (index == wanted) {
                return pos;
            }
            index++;
        }
        return history.peekFirst() == null ? target.position() : history.peekFirst();
    }

    private void tickStarTraps() {
        if (this.starTraps.isEmpty()) {
            return;
        }
        Iterator<StarTrap> iterator = this.starTraps.iterator();
        while (iterator.hasNext()) {
            StarTrap trap = iterator.next();
            trap.ticks--;
            if (trap.ticks <= 0) {
                iterator.remove();
                continue;
            }
            List<LivingEntity> victims = getEnemies(trap.pos, trap.radius);
            if (victims.isEmpty()) {
                continue;
            }
            for (LivingEntity victim : victims) {
                hurtPercent(victim, trap.percent, true);
            }
            spawnVfx(VfxType.STAR_RAIN, trap.pos.x, trap.pos.y + 1.0, trap.pos.z, 0.0, 1.0, 0.0, 0xFF77BBFF, 2.0f, 25);
            this.level().playSound(null, BlockPos.containing(trap.pos), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4f, 1.0f);
            iterator.remove();
        }
    }

    private void tickDamageMarks() {
        Iterator<Map.Entry<UUID, DamageMark>> iterator = this.damageMarks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, DamageMark> entry = iterator.next();
            entry.getValue().ticks--;
            if (entry.getValue().ticks <= 0) {
                iterator.remove();
            }
        }
    }

    private void tickTimedModifiers() {
        Iterator<Map.Entry<ResourceLocation, TimedModifier>> iterator = this.timedModifiers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, TimedModifier> entry = iterator.next();
            TimedModifier timed = entry.getValue();
            timed.ticks--;
            if (timed.ticks <= 0) {
                AttributeInstance instance = this.getAttribute(timed.attribute);
                if (instance != null) {
                    instance.removeModifier(entry.getKey());
                }
                iterator.remove();
            }
        }
    }

    private void tickAreaEffects() {
        Iterator<AreaEffect> iterator = this.areaEffects.iterator();
        while (iterator.hasNext()) {
            AreaEffect area = iterator.next();
            area.ticks--;
            if (area.ticks <= 0) {
                iterator.remove();
                continue;
            }
            if (area.ticks % 20 != 0) {
                continue;
            }
            spawnVfx(area.vfxType, area.center.x, area.center.y + 1.0, area.center.z, 0.0, 1.0, 0.0, area.color, area.scale, 25);
            if (area.terrainPath != null) {
                modifyTerrain(area.center, area.terrainPath, area.terrainRadius);
            }
            for (LivingEntity living : getEnemies(area.center, area.radius)) {
                if (area.percentPerSecond > 0.0f) {
                    float damage = markedDamage(living, percentDamage(living, area.percentPerSecond));
                    living.hurt(area.magic ? this.damageSources().magic() : this.damageSources().mobAttack(this), damage);
                }
                if (area.slowAmp >= 0) {
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, area.slowAmp));
                }
                if (area.weakAmp >= 0) {
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30, area.weakAmp));
                }
                if (area.pullToCenter) {
                    Vec3 delta = area.center.subtract(living.position());
                    if (delta.lengthSqr() > 0.0001) {
                        Vec3 force = delta.normalize().scale(0.6);
                        living.setDeltaMovement(living.getDeltaMovement().add(force));
                        living.hurtMarked = true;
                    }
                }
                if (area.randomBlink && this.random.nextFloat() < 0.35f) {
                    double ox = (this.random.nextDouble() - 0.5) * 6.0;
                    double oz = (this.random.nextDouble() - 0.5) * 6.0;
                    living.teleportTo(living.getX() + ox, living.getY(), living.getZ() + oz);
                }
            }
        }
    }

    private void performVenerableAttack(LivingEntity target) {
        float healthRatio = this.getHealth() / this.getMaxHealth();
        double distanceSq = this.distanceToSqr(target);
        this.combatAI.setCombatContext(this.currentPhase, healthRatio, distanceSq);
        this.combatAI.tick();
        String moveId = this.combatAI.getNextMoveId();

        if (moveId == null || moveId.isBlank()) {
            basicAttack(target);
            this.abilityCooldown = 20;
            return;
        }

        executeMove(moveId, target);
        int cooldown = this.moveCooldownTicks.getOrDefault(moveId, 60);
        this.combatAI.setCooldown(moveId, cooldown);
        this.abilityCooldown = Math.max(10, cooldown / 3);
    }

    private void executeMove(String moveId, LivingEntity target) {
        String methodName = toMoveMethodName(moveId);
        Method method = this.moveMethodCache.get(moveId);
        if (method == null) {
            try {
                method = VenerableEntity.class.getDeclaredMethod(methodName, LivingEntity.class);
                method.setAccessible(true);
                this.moveMethodCache.put(moveId, method);
            } catch (NoSuchMethodException ignored) {
                basicAttack(target);
                return;
            }
        }
        try {
            method.invoke(this, target);
        } catch (Exception ignored) {
            basicAttack(target);
        }
    }

    private static String toMoveMethodName(String moveId) {
        String[] parts = moveId.split("_");
        StringBuilder builder = new StringBuilder("move");
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }

    private void basicAttack(LivingEntity target) {
        hurtScaled(target, 1.0f, false);
        spawnVfx(VfxType.SLASH_ARC, target.getX(), target.getY() + 1.0, target.getZ(),
            target.getX() - getX(), 0.0, target.getZ() - getZ(), this.venerableType.color, 1.1f, 16);
    }

    private void applyTimedModifier(Holder<Attribute> attribute, String key, double amount, AttributeModifier.Operation operation, int ticks) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, key);
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(id);
        if (Math.abs(amount) > 0.000001d) {
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
            this.timedModifiers.put(id, new TimedModifier(attribute, ticks));
        } else {
            this.timedModifiers.remove(id);
        }
    }

    private float baseAttackDamage() {
        return (float) (this.venerableType.attackDamage * 0.001d * (1.0d + this.youHunDevourStacks * 0.02d));
    }

    private float percentDamage(LivingEntity target, float percent) {
        return (float) (target.getMaxHealth() * percent);
    }

    private float markedDamage(LivingEntity target, float raw) {
        DamageMark mark = this.damageMarks.get(target.getUUID());
        if (mark == null) {
            return raw;
        }
        return raw * mark.multiplier;
    }

    private void addDamageMark(LivingEntity target, int ticks, float multiplier) {
        this.damageMarks.put(target.getUUID(), new DamageMark(ticks, multiplier));
    }

    private void selfDamagePercent(float percent) {
        float amount = (float) (this.getMaxHealth() * percent);
        this.setHealth(Math.max(1.0f, this.getHealth() - amount));
    }

    private void healPercent(float percent) {
        this.heal((float) (this.getMaxHealth() * percent));
    }

    private void hurtScaled(LivingEntity target, float scale, boolean magic) {
        if (target == null || !target.isAlive()) {
            return;
        }
        float damage = markedDamage(target, baseAttackDamage() * scale);
        target.hurt(magic ? this.damageSources().magic() : this.damageSources().mobAttack(this), damage);
    }

    private void hurtPercent(LivingEntity target, float percent, boolean magic) {
        if (target == null || !target.isAlive()) {
            return;
        }
        float damage = markedDamage(target, percentDamage(target, percent));
        target.hurt(magic ? this.damageSources().magic() : this.damageSources().mobAttack(this), damage);
    }

    private List<LivingEntity> getEnemies(Vec3 center, double radius) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return List.of();
        }
        AABB box = new AABB(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius
        );
        return sl.getEntitiesOfClass(LivingEntity.class, box, this::isEnemy);
    }

    private boolean isEnemy(LivingEntity entity) {
        if (entity == this || !entity.isAlive()) {
            return false;
        }
        if (entity instanceof FormlessHandEntity) {
            return false;
        }
        if (entity instanceof PhantomImmortalEntity phantom && this.getUUID().equals(phantom.getOwnerUUID())) {
            return false;
        }
        return !entity.isAlliedTo(this);
    }

    private void hurtAreaPercent(Vec3 center, double radius, float percent, boolean magic) {
        for (LivingEntity living : getEnemies(center, radius)) {
            hurtPercent(living, percent, magic);
        }
    }

    private void hurtAreaScaled(Vec3 center, double radius, float scale, boolean magic) {
        for (LivingEntity living : getEnemies(center, radius)) {
            hurtScaled(living, scale, magic);
        }
    }

    private void addAreaEffect(Vec3 center, double radius, float percentPerSecond, int ticks,
                               boolean magic, int slowAmp, int weakAmp, boolean pullToCenter,
                               boolean randomBlink, VfxType vfxType, int color, float scale,
                               DaoPath terrainPath, int terrainRadius) {
        this.areaEffects.add(new AreaEffect(
            center, radius, percentPerSecond, ticks, magic, slowAmp, weakAmp,
            pullToCenter, randomBlink, vfxType, color, scale, terrainPath, terrainRadius
        ));
    }

    private void spawnVfx(VfxType type, double x, double y, double z,
                          double dirX, double dirY, double dirZ,
                          int color, float scale, int ticks) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        for (ServerPlayer sp : sl.players()) {
            if (sp.distanceToSqr(x, y, z) <= 16384.0) {
                VfxHelper.spawn(sp, type, x, y, z, (float) dirX, (float) dirY, (float) dirZ, color, scale, ticks);
            }
        }
    }

    private void spawnCenterVfx(VfxType type, int color, float scale, int ticks) {
        spawnVfx(type, getX(), getY() + 1.0, getZ(), 0.0, 1.0, 0.0, color, scale, ticks);
    }

    private void modifyTerrain(Vec3 center, DaoPath path, int radius) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        TerrainModifier.modifyTerrain(sl, BlockPos.containing(center), path, radius);
    }

    private void clearProjectiles(double radius) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(radius);
        for (Projectile projectile : sl.getEntitiesOfClass(Projectile.class, box, projectile -> true)) {
            projectile.discard();
        }
    }

    private void knockbackTargets(Vec3 center, double radius, double strength, double yBoost) {
        for (LivingEntity living : getEnemies(center, radius)) {
            Vec3 delta = living.position().subtract(center);
            if (delta.lengthSqr() < 0.0001) {
                continue;
            }
            Vec3 push = delta.normalize().scale(strength);
            living.push(push.x, yBoost, push.z);
            living.hurtMarked = true;
        }
    }

    private void teleportBehind(LivingEntity target, double backDistance) {
        Vec3 look = target.getLookAngle().normalize();
        Vec3 back = target.position().subtract(look.scale(backDistance));
        this.teleportTo(back.x, target.getY(), back.z);
    }

    private void summonFormlessHands(ServerPlayer target, int count, int fingers) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            FormlessHandEntity hand = new FormlessHandEntity(sl, this, target, fingers);
            double angle = (Math.PI * 2.0 * i) / Math.max(1, count);
            hand.moveTo(this.getX() + Math.cos(angle) * 1.6, this.getY() + 1.0, this.getZ() + Math.sin(angle) * 1.6, this.getYRot(), this.getXRot());
            sl.addFreshEntity(hand);
        }
    }

    private void summonPhantom(PhantomImmortalEntity.ImmortalType type, LivingEntity target, double scale) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        PhantomImmortalEntity summon = new PhantomImmortalEntity(sl, this, type);
        double offsetX = (this.random.nextDouble() - 0.5) * 4.0;
        double offsetZ = (this.random.nextDouble() - 0.5) * 4.0;
        summon.moveTo(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.getYRot(), this.getXRot());

        AttributeInstance hp = summon.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance atk = summon.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speed = summon.getAttribute(Attributes.MOVEMENT_SPEED);
        if (hp != null) {
            hp.removeModifier(SUMMON_HP_MOD);
            hp.addTransientModifier(new AttributeModifier(SUMMON_HP_MOD, hp.getBaseValue() * (scale - 1.0), AttributeModifier.Operation.ADD_VALUE));
        }
        if (atk != null) {
            atk.removeModifier(SUMMON_ATK_MOD);
            atk.addTransientModifier(new AttributeModifier(SUMMON_ATK_MOD, atk.getBaseValue() * (scale - 1.0), AttributeModifier.Operation.ADD_VALUE));
        }
        if (speed != null) {
            speed.removeModifier(SUMMON_SPEED_MOD);
            speed.addTransientModifier(new AttributeModifier(SUMMON_SPEED_MOD, speed.getBaseValue() * (scale - 1.0), AttributeModifier.Operation.ADD_VALUE));
        }
        summon.setHealth(summon.getMaxHealth());
        summon.setTarget(target);
        sl.addFreshEntity(summon);
        this.summonIds.add(summon.getUUID());
    }

    private int livingSummonCount() {
        return this.summonIds.size();
    }

    private Entity getAnyLivingSummon() {
        if (!(this.level() instanceof ServerLevel sl)) {
            return null;
        }
        for (UUID id : this.summonIds) {
            Entity entity = sl.getEntity(id);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                return entity;
            }
        }
        return null;
    }

    private void playHostileSound(SoundEvent event, float volume, float pitch) {
        this.level().playSound(null, this.blockPosition(), event, SoundSource.HOSTILE, volume, pitch);
    }

    private void castSinglePercent(LivingEntity target, float percent, VfxType vfxType, int color, float scale, int ticks) {
        hurtPercent(target, percent, true);
        spawnVfx(vfxType, target.getX(), target.getY() + 1.0, target.getZ(),
            target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), color, scale, ticks);
    }

    private void castSingleScaled(LivingEntity target, float scaleDamage, VfxType vfxType, int color, float scale, int ticks) {
        hurtScaled(target, scaleDamage, false);
        spawnVfx(vfxType, target.getX(), target.getY() + 1.0, target.getZ(),
            target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), color, scale, ticks);
    }

    private void castAreaScaled(Vec3 center, double radius, float scaleDamage, VfxType vfxType, int color, float scale, int ticks) {
        hurtAreaScaled(center, radius, scaleDamage, false);
        spawnVfx(vfxType, center.x, center.y + 1.0, center.z, 0.0, 1.0, 0.0, color, scale, ticks);
    }

    private void castAreaPercent(Vec3 center, double radius, float percent, VfxType vfxType, int color, float scale, int ticks) {
        hurtAreaPercent(center, radius, percent, true);
        spawnVfx(vfxType, center.x, center.y + 1.0, center.z, 0.0, 1.0, 0.0, color, scale, ticks);
    }

    private void spawnLightning(Vec3 pos) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
        if (bolt != null) {
            bolt.moveTo(pos.x, pos.y, pos.z);
            sl.addFreshEntity(bolt);
        }
    }

    private void addRoot(LivingEntity target, int ticks) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ticks, 8));
        target.setDeltaMovement(0.0, 0.0, 0.0);
        target.hurtMarked = true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.absoluteInvulTicks > 0) {
            return false;
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            this.recentAttacker = attacker.getUUID();
        }
        this.recentDamagePercent = Math.min(0.50f, this.recentDamagePercent + amount / Math.max(1.0f, this.getMaxHealth()));
        this.recentDamageTicks = 100;

        float adjusted = amount;

        if (this.wujiShieldTicks > 0) {
            adjusted *= 0.5f;
        }

        switch (this.venerableType) {
            case WU_JI -> adjusted *= 0.65f;
            case YOU_HUN -> {
                if (this.random.nextFloat() < 0.20f) {
                    if (source.getEntity() instanceof LivingEntity attacker) {
                        hurtScaled(attacker, 300.0f, true);
                        spawnVfx(VfxType.SOUL_VORTEX, attacker.getX(), attacker.getY() + 1.0, attacker.getZ(),
                            0.0, 1.0, 0.0, 0xFF330066, 1.2f, 15);
                    }
                    return false;
                }
            }
            case DAO_TIAN -> {
                if (this.random.nextFloat() < 0.15f) {
                    if (source.getEntity() instanceof LivingEntity attacker) {
                        stealOneBuff(attacker);
                        spawnVfx(VfxType.SPATIAL_TEAR, getX(), getY() + 1.0, getZ(),
                            0.0, 1.0, 0.0, 0xFF444444, 1.0f, 12);
                    }
                    return false;
                }
            }
            case KUANG_MAN -> {
                adjusted *= 0.75f;
                if (source.getEntity() instanceof LivingEntity attacker && attacker.distanceTo(this) < 8.0) {
                    hurtScaled(attacker, 500.0f, false);
                    spawnVfx(VfxType.IMPACT_BURST, attacker.getX(), attacker.getY() + 1.0, attacker.getZ(),
                        0.0, 1.0, 0.0, 0xFFCC0000, 1.0f, 10);
                }
            }
            case JU_YANG -> {
                if (this.random.nextFloat() < 0.15f) {
                    spawnVfx(VfxType.GLOW_BURST, getX(), getY() + 1.0, getZ(),
                        0.0, 1.0, 0.0, 0xFFFF8800, 0.8f, 10);
                    return false;
                }
            }
            case XING_XIU -> {
                if (this.xingXiuPredictActive) {
                    adjusted *= 0.70f;
                    this.xingXiuPredictActive = false;
                }
                this.xingXiuPredictActive = true;
            }
            case LE_TU -> adjusted *= 0.60f;
            case YUAN_SHI -> {
                adjusted *= 0.80f;
                applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_yuanshi_qi_absorb",
                    this.venerableType.attackDamage * 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
            }
            case HONG_LIAN -> {
                if (this.random.nextFloat() < 0.20f && this.hongLianSavedHealth > 0.0f) {
                    this.setHealth(Math.max(this.getHealth(), this.hongLianSavedHealth));
                    spawnVfx(VfxType.TIME_DISTORTION, getX(), getY() + 1.0, getZ(),
                        0.0, 1.0, 0.0, 0xFFFF0033, 1.5f, 20);
                    return false;
                }
            }
            case YUAN_LIAN -> adjusted *= 0.85f;
            default -> {}
        }

        if (this.venerableType == VenerableType.KUANG_MAN && this.getHealth() < this.getMaxHealth() * 0.30f) {
            adjusted *= 0.5f;
        }

        if (!this.level().isClientSide() && adjusted >= this.getHealth()) {
            if (tryPreventDeath()) {
                return false;
            }
        }

        return super.hurt(source, adjusted);
    }

    private boolean tryPreventDeath() {

        if (this.venerableType == VenerableType.HONG_LIAN && !this.hongLianCicadaUsed) {
            this.hongLianCicadaUsed = true;
            this.setHealth(this.getMaxHealth());
            applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_honglian_cicada", this.venerableType.attackDamage, AttributeModifier.Operation.ADD_VALUE, 200);
            spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 2.5f, 45);
            return true;
        }
        if (this.venerableType == VenerableType.JU_YANG && !this.juYangImmortalUsed) {
            this.juYangImmortalUsed = true;
            this.setHealth((float) (this.getMaxHealth() * 0.30f));
            this.juYangDrainTicks = 100;
            spawnCenterVfx(VfxType.GLOW_BURST, 0xFFFFAA00, 2.2f, 35);
            return true;
        }
        if (this.venerableType == VenerableType.YOU_HUN) {
            Entity anchor = getAnyLivingSummon();
            if (anchor != null) {
                this.teleportTo(anchor.getX(), anchor.getY(), anchor.getZ());
                this.setHealth((float) (this.getMaxHealth() * 0.50f));
                spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF330066, 2.2f, 30);
                return true;
            }
        }
        return false;
    }

    @Override
    public void awardKillScore(Entity killed, int scoreValue, DamageSource damageSource) {
        super.awardKillScore(killed, scoreValue, damageSource);
        if (this.venerableType == VenerableType.YOU_HUN) {
            this.youHunDevourStacks = Math.min(20, this.youHunDevourStacks + 1);
        }
    }

    private void explodeSummons(float percent, double radius, VfxType type, int color) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        Iterator<UUID> iterator = this.summonIds.iterator();
        while (iterator.hasNext()) {
            UUID id = iterator.next();
            Entity entity = sl.getEntity(id);
            if (!(entity instanceof LivingEntity living) || !living.isAlive()) {
                iterator.remove();
                continue;
            }
            Vec3 pos = living.position();
            for (LivingEntity target : getEnemies(pos, radius)) {
                hurtPercent(target, percent, true);
            }
            spawnVfx(type, pos.x, pos.y + 1.0, pos.z, 0.0, 1.0, 0.0, color, 1.8f, 25);
            living.discard();
            iterator.remove();
        }
    }

    private void lineStrike(LivingEntity target, double maxDistance, float percent, VfxType vfxType, int color) {
        if (this.distanceTo(target) <= maxDistance) {
            castSinglePercent(target, percent, vfxType, color, 1.6f, 22);
        }
    }

    private void dashStrike(LivingEntity target, float percent, VfxType vfxType, int color) {
        teleportBehind(target, 1.6);
        castSinglePercent(target, percent, vfxType, color, 1.4f, 20);
    }

    private void transferDebuffsToTarget(LivingEntity target) {
        List<MobEffectInstance> ownEffects = new ArrayList<>(this.getActiveEffects());
        for (MobEffectInstance effect : ownEffects) {
            if (effect.getEffect().value().isBeneficial()) {
                continue;
            }
            target.addEffect(new MobEffectInstance(effect));
            this.removeEffect(effect.getEffect());
        }
    }

    private void stealOneBuff(LivingEntity target) {
        List<MobEffectInstance> beneficial = new ArrayList<>();
        for (MobEffectInstance effect : target.getActiveEffects()) {
            if (effect.getEffect().value().isBeneficial()) {
                beneficial.add(effect);
            }
        }
        if (!beneficial.isEmpty()) {
            MobEffectInstance stolen = beneficial.get(this.random.nextInt(beneficial.size()));
            target.removeEffect(stolen.getEffect());
            this.addEffect(new MobEffectInstance(stolen.getEffect(), stolen.getDuration(), stolen.getAmplifier()));
        }
    }

    private void moveWujiLawChisel(LivingEntity target) {
        castSingleScaled(target, 8000.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 1.6f, 24);
        playHostileSound(SoundEvents.ANVIL_LAND, 1.4f, 0.7f);
    }

    private void moveWujiMadnessWave(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 1080.0f, VfxType.RIPPLE, 0xFFF0F0FF, 2.0f, 25);
        this.wujiMadnessStacks++;
        if (this.wujiMadnessStacks >= 3) {
            this.wujiMadnessStacks = 0;
            castSingleScaled(target, 6000.0f, VfxType.RIPPLE, 0xFFF0F0FF, 2.2f, 25);
        }
    }

    private void moveWujiOrderBarrage(LivingEntity target) {
        for (int i = 0; i < 5; i++) {
            castSingleScaled(target, 2000.0f, VfxType.PULSE_WAVE, 0xFFF0F0FF, 1.2f, 14);
        }
    }

    private void moveWujiAbsoluteLaw(LivingEntity target) {
        castAreaScaled(this.position(), 16.0, 7200.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 2.6f, 35);
        for (LivingEntity living : getEnemies(this.position(), 16.0)) {
            addRoot(living, 60);
        }
    }

    private void moveWujiLawBind(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 2));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 2));
        castSingleScaled(target, 1.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 1.4f, 22);
    }

    private void moveWujiAllLawsOne(LivingEntity target) {
        castSingleScaled(target, 12000.0f, VfxType.PULSE_WAVE, 0xFFF0F0FF, 2.0f, 30);
        target.removeAllEffects();
    }

    private void moveWujiForbiddenDomain(LivingEntity target) {
        addAreaEffect(this.position(), 10.0, 0.03f, 200, true, 2, 1, false, false,
            VfxType.DOME_FIELD, 0xFFF0F0FF, 2.4f, DaoPath.RULE, 3);
    }

    private void moveWujiLawChainDrag(LivingEntity target) {
        target.teleportTo(this.getX(), this.getY(), this.getZ());
        castSingleScaled(target, 3200.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 1.5f, 20);
    }

    private void moveWujiOrderJudgment(LivingEntity target) {
        for (int i = 0; i < 3; i++) {
            castSingleScaled(target, 3200.0f, VfxType.SLASH_ARC, 0xFFF0F0FF, 1.3f, 12);
        }
        knockbackTargets(target.position(), 3.0, 1.1, 0.2);
    }

    private void moveWujiForbiddenCage(LivingEntity target) {
        addAreaEffect(target.position(), 8.0, 0.00f, 200, true, 7, 2, false, false,
            VfxType.LAW_CHAINS, 0xFFF0F0FF, 2.2f, null, 0);
    }

    private void moveWujiMyriadLawOrigin(LivingEntity target) {
        castAreaScaled(this.position(), 12.0, 12600.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 3.0f, 45);
        for (LivingEntity living : getEnemies(this.position(), 12.0)) {
            living.removeAllEffects();
        }
        modifyTerrain(this.position(), DaoPath.RULE, 4);
    }

    private void moveWujiLawTrial(LivingEntity target) {
        float hpRatio = target.getHealth() / Math.max(1.0f, target.getMaxHealth());
        float pct = Math.max(0.02f, Math.min(0.20f, hpRatio * 0.20f));
        castSingleScaled(target, target.getHealth() < target.getMaxHealth() * 0.30f ? 12000.0f : 3800.0f, VfxType.ENERGY_BEAM, 0xFFF0F0FF, 1.8f, 24);
    }

    private void moveWujiWorldEnd(LivingEntity target) {
        selfDamagePercent(0.05f);
        castAreaScaled(this.position(), 18.0, 9000.0f, VfxType.SKY_STRIKE, 0xFFF0F0FF, 3.0f, 40);
        modifyTerrain(this.position(), DaoPath.RULE, 6);
    }

    private void moveWujiOrderSpear(LivingEntity target) {
        if (this.distanceTo(target) <= 18.0f) {
            hurtScaled(target, 4600.0f, true);
            spawnVfx(VfxType.ENERGY_BEAM, target.getX(), target.getY() + 1.0, target.getZ(),
                target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), 0xFFF0F0FF, 1.8f, 24);
        }
        spawnVfx(VfxType.LAW_CHAINS, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFFF0F0FF, 1.5f, 22);
        this.level().playSound(null, blockPosition(), SoundEvents.TRIDENT_THROW.value(), SoundSource.HOSTILE, 1.4f, 0.7f);
    }

    private void moveWujiBoundlessCross(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 5400.0f, VfxType.SLASH_ARC, 0xFFF0F0FF, 2.2f, 30);
        knockbackTargets(this.position(), 8.0, 1.2, 0.2);
    }

    private void moveWujiLawReflect(LivingEntity target) {
        float scale = Math.max(1000.0f, Math.min(7000.0f, this.recentDamagePercent * 30000.0f));
        castSingleScaled(target, scale, VfxType.DOME_FIELD, 0xFFF0F0FF, 1.8f, 24);
        clearProjectiles(8.0);
    }

    private void moveWujiForbiddenSeal(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 1));
        castSingleScaled(target, 2400.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 1.6f, 24);
    }

    private void moveWujiPressureAura(LivingEntity target) {
        for (LivingEntity living : getEnemies(this.position(), 8.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        }
        spawnCenterVfx(VfxType.LAW_CHAINS, 0xFFF0F0FF, 1.9f, 24);
    }

    private void moveWujiLawShield(LivingEntity target) {
        this.wujiShieldTicks = 200;
        spawnCenterVfx(VfxType.LAW_CHAINS, 0xFFF0F0FF, 2.2f, 30);
    }

    private void moveWujiVoidAllLaws(LivingEntity target) {
        clearProjectiles(8.0);
        modifyTerrain(this.position(), DaoPath.RULE, 3);
        spawnCenterVfx(VfxType.PULSE_WAVE, 0xFFF0F0FF, 2.2f, 24);
    }

    private void moveWujiImmortalLaw(LivingEntity target) {
        if (!this.wujiImmortalUsed && this.getHealth() < this.getMaxHealth() * 0.20f) {
            this.wujiImmortalUsed = true;
            healPercent(0.20f);
            applyTimedModifier(Attributes.ARMOR, "venerable_wuji_immortal_armor", 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
            spawnCenterVfx(VfxType.LAW_CHAINS, 0xFFF0F0FF, 2.8f, 40);
        } else {
            castSingleScaled(target, 1.0f, VfxType.LAW_CHAINS, 0xFFF0F0FF, 1.2f, 14);
        }
    }

    private void moveWujiProveByForce(LivingEntity target) {
        castSingleScaled(target, 16000.0f, VfxType.ENERGY_BEAM, 0xFFF0F0FF, 2.6f, 45);
        spawnVfx(VfxType.LAW_CHAINS, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFFF0F0FF, 2.0f, 40);
    }

    private void moveYouhunSoulStrike(LivingEntity target) {
        castSingleScaled(target, 6000.0f, VfxType.SOUL_VORTEX, 0xFF330066, 1.6f, 22);
    }

    private void moveYouhunSoulReap(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 2880.0f, VfxType.SOUL_VORTEX, 0xFF330066, 2.2f, 30);
        healPercent(0.08f);
    }

    private void moveYouhunSoulDevourUltimate(LivingEntity target) {
        castAreaScaled(this.position(), 10.0, 9000.0f, VfxType.SOUL_VORTEX, 0xFF330066, 2.8f, 38);
    }

    private void moveYouhunSoulSplit(LivingEntity target) {
        if (!this.youHunSoulSplitUsed && this.getHealth() < this.getMaxHealth() * 0.20f) {
            this.youHunSoulSplitUsed = true;
            healPercent(0.30f);
            summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.40);
            summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.40);
            summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.40);
            spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF330066, 2.5f, 30);
        } else {
            castSingleScaled(target, 1.0f, VfxType.SOUL_VORTEX, 0xFF330066, 1.2f, 15);
        }
    }

    private void moveYouhunNineRevolutionDream(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        castSingleScaled(target, 10000.0f, VfxType.SOUL_VORTEX, 0xFF330066, 2.0f, 28);
    }

    private void moveYouhunShadowClone(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.40);
        summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.40);
        summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.40);
        spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF330066, 1.8f, 24);
    }

    private void moveYouhunSoulStorm(LivingEntity target) {
        addAreaEffect(this.position(), 12.0, 0.02f, 160, true, 1, 0, false, true,
            VfxType.SOUL_VORTEX, 0xFF330066, 2.2f, DaoPath.SOUL, 4);
    }

    private void moveYouhunSoulGaze(LivingEntity target) {
        castSingleScaled(target, 3200.0f, VfxType.SOUL_VORTEX, 0xFF330066, 1.7f, 22);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4));
    }

    private void moveYouhunDevourPassive(LivingEntity target) {
        healPercent(0.05f);
        this.youHunDevourStacks = Math.min(20, this.youHunDevourStacks + 1);
        spawnCenterVfx(VfxType.SOUL_VORTEX, 0xFF330066, 1.6f, 20);
    }

    private void moveYouhunSoulTruth(LivingEntity target) {
        castSingleScaled(target, 12000.0f, VfxType.SOUL_VORTEX, 0xFF330066, 2.3f, 30);
        spawnVfx(VfxType.SHADOW_FADE, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFF330066, 1.4f, 20);
    }

    private void moveYouhunShadowEscape(LivingEntity target) {
        teleportBehind(target, 1.2);
        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0));
        castSingleScaled(target, 4000.0f, VfxType.SHADOW_FADE, 0xFF330066, 1.6f, 22);
    }

    private void moveYouhunDreamCage(LivingEntity target) {
        addRoot(target, 80);
        castSingleScaled(target, 4800.0f, VfxType.SOUL_VORTEX, 0xFF330066, 1.8f, 26);
    }

    private void moveYouhunMentalCrush(LivingEntity target) {
        castAreaScaled(this.position(), 16.0, 1800.0f, VfxType.SOUL_VORTEX, 0xFF330066, 2.4f, 28);
        modifyTerrain(this.position(), DaoPath.SOUL, 5);
        for (LivingEntity living : getEnemies(this.position(), 16.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 1));
        }
    }

    private void moveYouhunSoulPuppet(LivingEntity target) {
        castSingleScaled(target, 2000.0f, VfxType.SHADOW_FADE, 0xFF330066, 1.6f, 22);
        if (target instanceof Mob mob) {
            mob.setTarget(this);
        }
    }

    private void moveYouhunCloneDetonate(LivingEntity target) {
        explodeSummons(0.10f, 6.0, VfxType.SHADOW_FADE, 0xFF330066);
    }

    private void moveYouhunAbsorbSoul(LivingEntity target) {
        castSingleScaled(target, 4800.0f, VfxType.SOUL_VORTEX, 0xFF330066, 1.8f, 24);
        healPercent(0.12f);
    }

    private void moveYouhunGhostDomain(LivingEntity target) {
        addAreaEffect(this.position(), 10.0, 0.02f, 200, true, 1, 1, false, false,
            VfxType.SOUL_VORTEX, 0xFF330066, 2.2f, DaoPath.SOUL, 3);
    }

    private void moveYouhunDreamButterfly(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 1));
        castSingleScaled(target, 2400.0f, VfxType.SHADOW_FADE, 0xFF330066, 1.6f, 22);
    }

    private void moveYouhunNightOfGhosts(LivingEntity target) {
        addAreaEffect(this.position(), 8.0, 0.10f, 100, true, 1, 1, false, false,
            VfxType.SOUL_VORTEX, 0xFF330066, 2.8f, DaoPath.SOUL, 4);
    }

    private void moveYouhunUndyingSoul(LivingEntity target) {
        if (livingSummonCount() > 0) {
            healPercent(0.05f);
        } else {
            summonPhantom(PhantomImmortalEntity.ImmortalType.SOUL_REAPER, target, 0.50);
        }
        spawnCenterVfx(VfxType.SOUL_VORTEX, 0xFF330066, 2.2f, 30);
    }

    private void moveDaotianSpaceCut(LivingEntity target) {
        castSingleScaled(target, 4800.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.7f, 22);
    }

    private void moveDaotianFormlessHand(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            summonFormlessHands(serverPlayer, 1, 3);
        }
        castSingleScaled(target, 1.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.4f, 20);
    }

    private void moveDaotianSwapHeaven(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            summonFormlessHands(serverPlayer, 3, 3);
        }
        spawnCenterVfx(VfxType.SPATIAL_TEAR, 0xFF444444, 2.0f, 26);
    }

    private void moveDaotianSpaceFold(LivingEntity target) {
        teleportBehind(target, 1.0);
        castSingleScaled(target, 4000.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.6f, 20);
        castSingleScaled(target, 4000.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.6f, 20);
    }

    private void moveDaotianStealTime(LivingEntity target) {
        addRoot(target, 60);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_daotian_speed", 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 60);
        spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF444444, 1.9f, 22);
    }

    private void moveDaotianVoidCage(LivingEntity target) {
        addAreaEffect(target.position(), 8.0, 0.00f, 120, true, 6, 2, false, false,
            VfxType.SPATIAL_TEAR, 0xFF444444, 2.1f, DaoPath.SPACE, 3);
    }

    private void moveDaotianTenThousandHands(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            summonFormlessHands(serverPlayer, 8, 5);
        }
        castAreaScaled(this.position(), 10.0, 3600.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 3.0f, 36);
    }

    private void moveDaotianSpaceSwap(LivingEntity target) {
        Vec3 selfPos = this.position();
        Vec3 targetPos = target.position();
        this.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        target.teleportTo(selfPos.x, selfPos.y, selfPos.z);
        castSingleScaled(target, 3200.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.6f, 20);
    }

    private void moveDaotianStealPower(LivingEntity target) {
        double stolen = target.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.20;
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_daotian_steal_power", stolen, AttributeModifier.Operation.ADD_VALUE, 200);
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
        spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF444444, 1.8f, 24);
    }

    private void moveDaotianVoidStep(LivingEntity target) {
        teleportBehind(target, 1.2);
        castSingleScaled(target, 1800.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.4f, 16);
        teleportBehind(target, 1.2);
        castSingleScaled(target, 2200.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.5f, 18);
        teleportBehind(target, 1.2);
        castSingleScaled(target, 2600.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.6f, 20);
    }

    private void moveDaotianSpaceRift(LivingEntity target) {
        Vec3 pos = target.position();
        castAreaScaled(pos, 5.0, 5400.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 2.2f, 28);
        modifyTerrain(pos, DaoPath.SPACE, 3);
    }

    private void moveDaotianStealMemory(LivingEntity target) {
        if (!target.getActiveEffects().isEmpty()) {
            MobEffectInstance pick = target.getActiveEffects().iterator().next();
            target.removeEffect(pick.getEffect());
        }
        castSingleScaled(target, 3200.0f, VfxType.SHADOW_FADE, 0xFF444444, 1.5f, 20);
    }

    private void moveDaotianNoGapDomain(LivingEntity target) {
        addAreaEffect(this.position(), 8.0, 0.05f, 160, true, 1, 1, false, true,
            VfxType.SPATIAL_TEAR, 0xFF444444, 2.3f, DaoPath.SPACE, 3);
    }

    private void moveDaotianSpaceCompress(LivingEntity target) {
        addAreaEffect(target.position(), 6.0, 0.10f, 20, true, 1, 0, true, false,
            VfxType.BLACK_HOLE, 0xFF444444, 2.0f, null, 0);
    }

    private void moveDaotianStealDefense(LivingEntity target) {
        double stolenArmor = Math.max(0.0, target.getAttributeValue(Attributes.ARMOR) * 0.50);
        applyTimedModifier(Attributes.ARMOR, "venerable_daotian_steal_armor", stolenArmor, AttributeModifier.Operation.ADD_VALUE, 200);
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
        spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF444444, 1.8f, 22);
    }

    private void moveDaotianVoidEye(LivingEntity target) {
        addDamageMark(target, 200, 1.30f);
        castSingleScaled(target, 1.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.4f, 18);
    }

    private void moveDaotianSpaceExile(LivingEntity target) {
        Vec3 dir = target.position().subtract(this.position()).normalize();
        Vec3 exile = target.position().add(dir.scale(30.0));
        target.teleportTo(exile.x, exile.y, exile.z);
        modifyTerrain(exile, DaoPath.SPACE, 3);
        castSingleScaled(target, 3200.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.7f, 20);
    }

    private void moveDaotianSameRealmClone(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.STAR_SAGE, target, 0.60);
        spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF444444, 1.9f, 22);
    }

    private void moveDaotianCopyArt(LivingEntity target) {
        castSingleScaled(target, 4800.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.8f, 24);
        castSingleScaled(target, 3200.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 1.5f, 20);
    }

    private void moveDaotianFormlessFist(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            summonFormlessHands(serverPlayer, 5, 5);
        }
        castSingleScaled(target, 8000.0f, VfxType.SPATIAL_TEAR, 0xFF444444, 2.2f, 28);
        modifyTerrain(target.position(), DaoPath.SPACE, 4);
    }

    private void applyKuangmanFormBonus(LivingEntity target, float baseDamage) {
        switch (this.kuangManActiveForm) {
            case 1 -> castAreaScaled(target.position(), 4.0, 2000.0f, VfxType.ENERGY_BEAM, 0xFFFF4400, 1.5f, 15);
            case 2 -> castSingleScaled(target, baseDamage * 0.5f, VfxType.SLASH_ARC, 0xFFFF6600, 1.2f, 10);
            case 3 -> knockbackTargets(target.position(), 4.0, 1.5, 0.6);
            case 4 -> addRoot(target, 40);
            case 5 -> healPercent(0.01f);
            default -> {
            }
        }
    }

    private void resetKuangmanFormBuffs() {
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_kuangman_form_attack", 0.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 1);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_kuangman_form_speed", 0.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 1);
        applyTimedModifier(Attributes.ARMOR, "venerable_kuangman_form_armor", 0.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 1);
        applyTimedModifier(Attributes.KNOCKBACK_RESISTANCE, "venerable_kuangman_form_knockback", 0.0, AttributeModifier.Operation.ADD_VALUE, 1);
    }

    private void moveKuangmanSavageSlam(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 5400.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 2.4f, 30);
        modifyTerrain(target.position(), DaoPath.STRENGTH, 3);
        applyKuangmanFormBonus(target, 5400.0f);
    }

    private void moveKuangmanDragonForm(LivingEntity target) {
        resetKuangmanFormBuffs();
        this.kuangManActiveForm = 1;
        this.kuangManFormTicks = 200;
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_kuangman_form_attack", 0.80, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_kuangman_form_speed", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFFF0000, 2.8f, 30);
        playHostileSound(SoundEvents.ENDER_DRAGON_GROWL, 1.4f, 0.9f);
    }

    private void moveKuangmanTigerForm(LivingEntity target) {
        resetKuangmanFormBuffs();
        this.kuangManActiveForm = 2;
        this.kuangManFormTicks = 200;
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_kuangman_form_speed", 0.60, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_kuangman_form_attack", 0.40, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFFF6600, 2.4f, 25);
    }

    private void moveKuangmanEagleForm(LivingEntity target) {
        resetKuangmanFormBuffs();
        this.kuangManActiveForm = 3;
        this.kuangManFormTicks = 200;
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_kuangman_form_speed", 1.00, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.KNOCKBACK_RESISTANCE, "venerable_kuangman_form_knockback", 1.0, AttributeModifier.Operation.ADD_VALUE, 200);
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFFFCC00, 2.2f, 25);
    }

    private void moveKuangmanSnakeForm(LivingEntity target) {
        resetKuangmanFormBuffs();
        this.kuangManActiveForm = 4;
        this.kuangManFormTicks = 200;
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFF00CC00, 2.0f, 25);
    }

    private void moveKuangmanBearForm(LivingEntity target) {
        resetKuangmanFormBuffs();
        this.kuangManActiveForm = 5;
        this.kuangManFormTicks = 200;
        applyTimedModifier(Attributes.ARMOR, "venerable_kuangman_form_armor", 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.KNOCKBACK_RESISTANCE, "venerable_kuangman_form_knockback", 0.5, AttributeModifier.Operation.ADD_VALUE, 200);
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFF884400, 2.6f, 30);
    }

    private void moveKuangmanSavagePower(LivingEntity target) {
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_kuangman_power_atk", 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_kuangman_power_speed", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFCC0000, 2.2f, 30);
        applyKuangmanFormBonus(target, 1.0f);
    }

    private void moveKuangmanHeavenFlip(LivingEntity target) {
        target.push(0.0, 2.0, 0.0);
        target.hurtMarked = true;
        castSingleScaled(target, 8000.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 2.0f, 28);
        modifyTerrain(target.position(), DaoPath.STRENGTH, 3);
        applyKuangmanFormBonus(target, 8000.0f);
    }

    private void moveKuangmanBreakAllLaws(LivingEntity target) {
        target.removeAllEffects();
        castSingleScaled(target, 4000.0f, VfxType.PULSE_WAVE, 0xFFCC0000, 1.8f, 24);
        applyKuangmanFormBonus(target, 4000.0f);
    }

    private void moveKuangmanBeastSummon(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.BLOOD_DEMON, target, 0.50);
        summonPhantom(PhantomImmortalEntity.ImmortalType.BLOOD_DEMON, target, 0.50);
        summonPhantom(PhantomImmortalEntity.ImmortalType.BLOOD_DEMON, target, 0.50);
        spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFCC0000, 2.0f, 28);
    }

    private void moveKuangmanTenThousandBeasts(LivingEntity target) {
        castAreaScaled(this.position(), 10.0, 10800.0f, VfxType.BEAST_PHANTOM, 0xFFCC0000, 3.0f, 42);
        modifyTerrain(this.position(), DaoPath.STRENGTH, 5);
        applyKuangmanFormBonus(target, 10800.0f);
    }

    private void moveKuangmanUndyingBody(LivingEntity target) {
        if (this.getHealth() < this.getMaxHealth() * 0.30f) {
            applyTimedModifier(Attributes.ARMOR, "venerable_kuangman_undying_armor", 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 120);
            spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFCC0000, 2.0f, 30);
        }
    }

    private void moveKuangmanCharge(LivingEntity target) {
        Vec3 dir = target.position().subtract(this.position()).normalize();
        this.push(dir.x * 1.6, 0.1, dir.z * 1.6);
        castSingleScaled(target, 4800.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 1.8f, 22);
        knockbackTargets(target.position(), 3.0, 1.2, 0.3);
        applyKuangmanFormBonus(target, 4800.0f);
    }

    private void moveKuangmanSplitEarth(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 7200.0f, VfxType.SLASH_ARC, 0xFFCC0000, 2.3f, 28);
        modifyTerrain(target.position(), DaoPath.STRENGTH, 4);
        applyKuangmanFormBonus(target, 7200.0f);
    }

    private void moveKuangmanThousandFall(LivingEntity target) {
        castAreaScaled(this.position(), 4.0, 2880.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 1.6f, 20);
        castAreaScaled(this.position(), 8.0, 1800.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 1.8f, 20);
        applyKuangmanFormBonus(target, 2880.0f);
    }

    private void moveKuangmanSavageTornado(LivingEntity target) {
        addAreaEffect(this.position(), 6.0, 0.08f, 80, true, 1, 0, false, false,
            VfxType.TORNADO, 0xFFCC0000, 2.2f, null, 0);
        knockbackTargets(this.position(), 6.0, 1.0, 0.2);
        applyKuangmanFormBonus(target, 1.0f);
    }

    private void moveKuangmanDevourHeal(LivingEntity target) {
        castSingleScaled(target, 1.0f, VfxType.BEAST_PHANTOM, 0xFFCC0000, 1.2f, 18);
        healPercent(0.03f);
        applyKuangmanFormBonus(target, 1.0f);
    }

    private void moveKuangmanGiantApeFist(LivingEntity target) {
        castAreaScaled(this.position(), 12.0, 9000.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 2.8f, 36);
        knockbackTargets(this.position(), 12.0, 1.4, 0.4);
        applyKuangmanFormBonus(target, 9000.0f);
    }

    private void moveKuangmanShockStomp(LivingEntity target) {
        castAreaScaled(this.position(), 6.0, 1080.0f, VfxType.IMPACT_BURST, 0xFFCC0000, 1.8f, 20);
        for (LivingEntity living : getEnemies(this.position(), 6.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4));
        }
        applyKuangmanFormBonus(target, 1080.0f);
    }

    private void moveKuangmanBloodAwakening(LivingEntity target) {
        if (this.currentPhase >= 3 && this.getHealth() < this.getMaxHealth() * 0.15f) {
            applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_kuangman_awake_atk", 3.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 100);
            applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_kuangman_awake_speed", 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 100);
            this.kuangManAwakenTicks = 100;
            spawnCenterVfx(VfxType.BEAST_PHANTOM, 0xFFCC0000, 2.8f, 40);
        } else {
            castSingleScaled(target, 1.0f, VfxType.BEAST_PHANTOM, 0xFFCC0000, 1.2f, 14);
        }
    }

    private void moveJuyangGoldenStrike(LivingEntity target) {
        selfDamagePercent(0.02f);
        castSingleScaled(target, 2800.0f, VfxType.ENERGY_BEAM, 0xFFFF8800, 1.6f, 20);
        if (this.random.nextFloat() < 0.15f) {
            castSingleScaled(target, 2800.0f, VfxType.GLOW_BURST, 0xFFFF8800, 1.7f, 20);
        }
    }

    private void moveJuyangBloodSacrifice(LivingEntity target) {
        selfDamagePercent(0.10f);
        castSingleScaled(target, 5600.0f, VfxType.BLOOD_RAIN, 0xFFFF8800, 2.0f, 24);
        healPercent(0.10f);
    }

    private void moveJuyangSunWill(LivingEntity target) {
        selfDamagePercent(0.02f);
        castAreaScaled(this.position(), 8.0, 3024.0f, VfxType.GLOW_BURST, 0xFFFF8800, 2.2f, 28);
        for (LivingEntity living : getEnemies(this.position(), 8.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        }
    }

    private void moveJuyangSolarJudgment(LivingEntity target) {
        selfDamagePercent(0.02f);
        for (int i = 0; i < 6; i++) {
            castSingleScaled(target, 1400.0f, VfxType.ENERGY_BEAM, 0xFFFF8800, 1.2f, 12);
        }
    }

    private void moveJuyangFortuneDeflect(LivingEntity target) {
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1));
        spawnCenterVfx(VfxType.GLOW_BURST, 0xFFFF8800, 1.8f, 24);
    }

    private void moveJuyangBloodContract(LivingEntity target) {
        selfDamagePercent(0.20f);
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        spawnCenterVfx(VfxType.BLOOD_RAIN, 0xFFFF8800, 2.0f, 28);
    }

    private void moveJuyangGoldenBloodline(LivingEntity target) {
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_juyang_bloodline_atk", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.ARMOR, "venerable_juyang_bloodline_armor", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_juyang_bloodline_speed", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.AURA_RING, 0xFFFF8800, 2.2f, 28);
    }

    private void moveJuyangSunBurst(LivingEntity target) {
        selfDamagePercent(0.02f);
        castAreaScaled(this.position(), 6.0, 3780.0f, VfxType.GLOW_BURST, 0xFFFF8800, 2.2f, 28);
        modifyTerrain(this.position(), DaoPath.FIRE, 3);
    }

    private void moveJuyangFortuneShift(LivingEntity target) {
        transferDebuffsToTarget(target);
        castSingleScaled(target, 1400.0f, VfxType.GLOW_BURST, 0xFFFF8800, 1.4f, 18);
    }

    private void moveJuyangBloodTide(LivingEntity target) {
        addAreaEffect(this.position(), 8.0, 0.03f, 100, true, 1, 1, false, false,
            VfxType.BLOOD_RAIN, 0xFFFF8800, 2.2f, DaoPath.BLOOD, 3);
    }

    private void moveJuyangSunSpear(LivingEntity target) {
        if (this.distanceTo(target) <= 20.0f) {
            selfDamagePercent(0.02f);
            hurtScaled(target, 2100.0f, true);
            spawnVfx(VfxType.ENERGY_BEAM, target.getX(), target.getY() + 1.0, target.getZ(),
                target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), 0xFFFF8800, 1.8f, 24);
        }
        this.level().playSound(null, blockPosition(), SoundEvents.TRIDENT_THROW.value(), SoundSource.HOSTILE, 1.3f, 1.0f);
    }

    private void moveJuyangFortuneSteal(LivingEntity target) {
        double stolenArmor = Math.max(0.0, target.getAttributeValue(Attributes.ARMOR) * 0.20);
        applyTimedModifier(Attributes.ARMOR, "venerable_juyang_steal_armor", stolenArmor, AttributeModifier.Operation.ADD_VALUE, 200);
        castSingleScaled(target, 0.7f, VfxType.GLOW_BURST, 0xFFFF8800, 1.3f, 16);
    }

    private void moveJuyangBloodBoil(LivingEntity target) {
        selfDamagePercent(0.15f);
        healPercent(0.15f);
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_juyang_blood_boil", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.BLOOD_RAIN, 0xFFFF8800, 2.4f, 30);
    }

    private void moveJuyangSunFall(LivingEntity target) {
        selfDamagePercent(0.02f);
        castAreaScaled(target.position(), 12.0, 5040.0f, VfxType.SKY_STRIKE, 0xFFFF8800, 2.8f, 36);
        castAreaScaled(target.position(), 12.0, 1260.0f, VfxType.GLOW_BURST, 0xFFFF8800, 2.4f, 28);
        modifyTerrain(target.position(), DaoPath.FIRE, 3);
    }

    private void moveJuyangFateHand(LivingEntity target) {
        selfDamagePercent(0.02f);
        if (target.getHealth() < target.getMaxHealth() * 0.10f && this.random.nextFloat() < 0.30f) {
            castSingleScaled(target, 8400.0f, VfxType.GLOW_BURST, 0xFFFF8800, 2.2f, 30);
        } else {
            castSingleScaled(target, 1400.0f, VfxType.GLOW_BURST, 0xFFFF8800, 1.3f, 16);
        }
    }

    private void moveJuyangBloodFormation(LivingEntity target) {
        selfDamagePercent(0.10f);
        addAreaEffect(this.position(), 12.0, 0.02f, 200, true, 1, 1, false, false,
            VfxType.BLOOD_RAIN, 0xFFFF8800, 2.6f, DaoPath.BLOOD, 4);
    }

    private void moveJuyangTrueSunTower(LivingEntity target) {
        applyTimedModifier(Attributes.ARMOR, "venerable_juyang_tower", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        modifyTerrain(this.position(), DaoPath.FIRE, 4);
        spawnCenterVfx(VfxType.DOME_FIELD, 0xFFFF8800, 2.4f, 34);
    }

    private void moveJuyangImmortalBody(LivingEntity target) {
        if (!this.juYangImmortalUsed) {
            this.juYangImmortalUsed = true;
            this.setHealth((float) (this.getMaxHealth() * 0.30f));
            this.juYangDrainTicks = 100;
            spawnCenterVfx(VfxType.GLOW_BURST, 0xFFFF8800, 2.6f, 36);
        } else {
            selfDamagePercent(0.02f);
            castSingleScaled(target, 0.7f, VfxType.GLOW_BURST, 0xFFFF8800, 1.2f, 14);
        }
    }

    private void moveXingxiuStarProjection(LivingEntity target) {
        castSingleScaled(target, 4800.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.6f, 20);
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
    }

    private void moveXingxiuStarNeedle(LivingEntity target) {
        addRoot(target, 60);
        castSingleScaled(target, 3200.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.5f, 20);
    }

    private void moveXingxiuStarTrap(LivingEntity target) {
        Vec3 predict = target.position().add(target.getDeltaMovement().scale(20.0));
        this.starTraps.add(new StarTrap(predict, 200, 0.10f, 2.5));
        spawnVfx(VfxType.STAR_RAIN, predict.x, predict.y + 1.0, predict.z, 0.0, 1.0, 0.0, 0xFF77BBFF, 1.8f, 24);
    }

    private void moveXingxiuStarExtinction(LivingEntity target) {
        castAreaScaled(target.position(), 12.0, 6480.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 2.5f, 34);
        modifyTerrain(target.position(), DaoPath.STAR, 4);
        spawnVfx(VfxType.SKY_STRIKE, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFF77BBFF, 2.2f, 30);
    }

    private void moveXingxiuDestinyCalculation(LivingEntity target) {
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 2));
        castSingleScaled(target, 6000.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.8f, 24);
    }

    private void moveXingxiuStarCage(LivingEntity target) {
        addAreaEffect(target.position(), 6.0, 0.03f, 200, true, 2, 1, false, false,
            VfxType.STAR_RAIN, 0xFF77BBFF, 2.1f, null, 0);
    }

    private void moveXingxiuStarBoard(LivingEntity target) {
        addAreaEffect(this.position(), 12.0, 0.00f, 200, true, 1, 1, false, false,
            VfxType.DOME_FIELD, 0xFF77BBFF, 2.4f, null, 0);
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_xingxiu_board_atk", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.ARMOR, "venerable_xingxiu_board_armor", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
    }

    private void moveXingxiuStarChain(LivingEntity target) {
        for (int i = 0; i < 5; i++) {
            castSingleScaled(target, 1200.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.1f, 10);
        }
        castSingleScaled(target, 4000.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.7f, 20);
    }

    private void moveXingxiuStarClone(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.STAR_SAGE, target, 0.60);
        spawnCenterVfx(VfxType.STAR_RAIN, 0xFF77BBFF, 1.9f, 22);
    }

    private void moveXingxiuStarArmor(LivingEntity target) {
        applyTimedModifier(Attributes.ARMOR, "venerable_xingxiu_star_armor", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.AURA_RING, 0xFF77BBFF, 2.0f, 24);
    }

    private void moveXingxiuDestinyMark(LivingEntity target) {
        addDamageMark(target, 200, 1.25f);
        castSingleScaled(target, 1.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.2f, 16);
    }

    private void moveXingxiuMeteorFall(LivingEntity target) {
        castAreaScaled(target.position(), 8.0, 5400.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 2.2f, 30);
        modifyTerrain(target.position(), DaoPath.STAR, 3);
        spawnVfx(VfxType.SKY_STRIKE, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFF77BBFF, 2.2f, 28);
    }

    private void moveXingxiuStarEye(LivingEntity target) {
        addDamageMark(target, 120, 1.15f);
        castSingleScaled(target, 3200.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.5f, 20);
    }

    private void moveXingxiuStarLock(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 4));
        addAreaEffect(target.position(), 4.0, 0.06f, 60, true, 4, 1, false, false,
            VfxType.STAR_RAIN, 0xFF77BBFF, 1.9f, null, 0);
    }

    private void moveXingxiuPredictedCounter(LivingEntity target) {
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 1));
        castSingleScaled(target, 3200.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 1.4f, 18);
    }

    private void moveXingxiuStarGravity(LivingEntity target) {
        addAreaEffect(target.position(), 8.0, 0.05f, 60, true, 2, 0, true, false,
            VfxType.BLACK_HOLE, 0xFF77BBFF, 2.0f, DaoPath.STAR, 3);
        target.push(0.0, -0.5, 0.0);
        target.hurtMarked = true;
    }

    private void moveXingxiuStarPressure(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 1800.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 2.0f, 24);
        for (LivingEntity living : getEnemies(this.position(), 8.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
        }
    }

    private void moveXingxiuStarEnd(LivingEntity target) {
        castAreaScaled(this.position(), 20.0, 9000.0f, VfxType.STAR_RAIN, 0xFF77BBFF, 3.0f, 40);
        modifyTerrain(this.position(), DaoPath.STAR, 6);
        spawnCenterVfx(VfxType.SKY_STRIKE, 0xFF77BBFF, 2.8f, 36);
    }

    private void moveLetuEarthSpikeArray(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 2880.0f, VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.0f, 24);
        for (LivingEntity living : getEnemies(this.position(), 8.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
        }
        modifyTerrain(this.position(), DaoPath.EARTH, 3);
    }

    private void moveLetuHeavenForce(LivingEntity target) {
        castSingleScaled(target, 4000.0f, VfxType.SKY_STRIKE, 0xFFC8A86E, 1.6f, 22);
        knockbackTargets(target.position(), 3.0, 1.2, 0.4);
    }

    private void moveLetuEarthDomain(LivingEntity target) {
        addAreaEffect(this.position(), 10.0, 0.02f, 200, true, 2, 0, false, false,
            VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.4f, DaoPath.EARTH, 3);
    }

    private void moveLetuEarthBarrier(LivingEntity target) {
        applyTimedModifier(Attributes.ARMOR, "venerable_letu_barrier", 1.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.2f, 28);
    }

    private void moveLetuHeavenEarthFormation(LivingEntity target) {
        addAreaEffect(this.position(), 12.0, 0.00f, 200, true, 1, 1, false, false,
            VfxType.DOME_FIELD, 0xFFC8A86E, 2.6f, DaoPath.RULE, 3);
        applyTimedModifier(Attributes.ARMOR, "venerable_letu_formation", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
    }

    private void moveLetuAllToEarth(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 7200.0f, VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.4f, 28);
        modifyTerrain(this.position(), DaoPath.EARTH, 4);
    }

    private void moveLetuEarthHeavenSplit(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 5400.0f, VfxType.SKY_STRIKE, 0xFFC8A86E, 2.3f, 28);
        modifyTerrain(target.position(), DaoPath.EARTH, 3);
    }

    private void moveLetuHeavenPunish(LivingEntity target) {
        castSingleScaled(target, 4800.0f, VfxType.SKY_STRIKE, 0xFFC8A86E, 1.8f, 24);
        spawnLightning(target.position());
        this.level().playSound(null, blockPosition(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.5f, 0.9f);
    }

    private void moveLetuEarthShield(LivingEntity target) {
        modifyTerrain(this.position(), DaoPath.EARTH, 3);
        spawnCenterVfx(VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.0f, 24);
    }

    private void moveLetuHeavenPillar(LivingEntity target) {
        castSingleScaled(target, 3200.0f, VfxType.EARTH_PILLAR, 0xFFC8A86E, 1.6f, 22);
        knockbackTargets(target.position(), 4.0, 1.2, 0.3);
    }

    private void moveLetuEarthPrison(LivingEntity target) {
        addAreaEffect(target.position(), 4.0, 0.03f, 120, true, 6, 1, false, false,
            VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.0f, DaoPath.EARTH, 2);
    }

    private void moveLetuHeavenMercy(LivingEntity target) {
        if (this.getHealth() < this.getMaxHealth() * 0.20f) {
            this.mercyTicks = 100;
            if (this.level() instanceof ServerLevel sl) {
                for (UUID id : this.summonIds) {
                    Entity entity = sl.getEntity(id);
                    if (entity instanceof LivingEntity living && living.isAlive()) {
                        living.heal((float) (living.getMaxHealth() * 0.05f));
                    }
                }
            }
            spawnCenterVfx(VfxType.HEAL_SPIRAL, 0xFFC8A86E, 2.2f, 30);
        } else {
            castSingleScaled(target, 1.0f, VfxType.SKY_STRIKE, 0xFFC8A86E, 1.2f, 14);
        }
    }

    private void moveLetuEarthQuake(LivingEntity target) {
        castAreaScaled(this.position(), 12.0, 3600.0f, VfxType.IMPACT_BURST, 0xFFC8A86E, 2.4f, 30);
        for (LivingEntity living : getEnemies(this.position(), 12.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 7));
        }
    }

    private void moveLetuHeavenJudgment(LivingEntity target) {
        if (this.distanceTo(target) <= 18.0f) {
            hurtScaled(target, 5200.0f, true);
            spawnVfx(VfxType.ENERGY_BEAM, target.getX(), target.getY() + 1.0, target.getZ(),
                target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), 0xFFC8A86E, 1.8f, 24);
        }
        addRoot(target, 40);
    }

    private void moveLetuUnbreakWall(LivingEntity target) {
        this.absoluteInvulTicks = 60;
        spawnCenterVfx(VfxType.DOME_FIELD, 0xFFC8A86E, 2.4f, 30);
    }

    private void moveLetuAllReturnOrigin(LivingEntity target) {
        healPercent(0.15f);
        for (UUID id : this.summonIds) {
            if (!(this.level() instanceof ServerLevel sl)) {
                continue;
            }
            Entity entity = sl.getEntity(id);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                living.heal((float) (living.getMaxHealth() * 0.08f));
            }
        }
        spawnCenterVfx(VfxType.HEAL_SPIRAL, 0xFFC8A86E, 2.2f, 30);
    }

    private void moveLetuEarthSpiritSummon(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.QI_MASTER, target, 0.50);
        summonPhantom(PhantomImmortalEntity.ImmortalType.QI_MASTER, target, 0.50);
        spawnCenterVfx(VfxType.EARTH_PILLAR, 0xFFC8A86E, 2.2f, 28);
    }

    private void moveLetuUnityOfHeavenEarth(LivingEntity target) {
        castAreaScaled(this.position(), 12.0, 9000.0f, VfxType.SKY_STRIKE, 0xFFC8A86E, 2.8f, 36);
        modifyTerrain(this.position(), DaoPath.EARTH, 5);
    }

    private void moveYuanshiQiBlast(LivingEntity target) {
        castSingleScaled(target, 4000.0f, VfxType.QI_STORM, 0xFFFFD700, 1.6f, 20);
        knockbackTargets(target.position(), 3.0, 1.0, 0.2);
    }

    private void moveYuanshiQiBarrage(LivingEntity target) {
        for (int i = 0; i < 8; i++) {
            castSingleScaled(target, 800.0f, VfxType.QI_STORM, 0xFFFFD700, 1.0f, 8);
        }
    }

    private void moveYuanshiQiSuppress(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 1800.0f, VfxType.QI_STORM, 0xFFFFD700, 1.8f, 24);
        for (LivingEntity living : getEnemies(this.position(), 8.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
        }
    }

    private void moveYuanshiThreeQiCombo(LivingEntity target) {
        castSingleScaled(target, 2400.0f, VfxType.QI_STORM, 0xFFFFD700, 1.2f, 10);
        castSingleScaled(target, 2400.0f, VfxType.QI_STORM, 0xFFFFD700, 1.2f, 10);
        castSingleScaled(target, 2400.0f, VfxType.QI_STORM, 0xFFFFD700, 1.2f, 10);
        knockbackTargets(target.position(), 3.0, 1.0, 0.3);
    }

    private void moveYuanshiYinyangSwap(LivingEntity target) {
        float selfRatio = this.getHealth() / this.getMaxHealth();
        float targetRatio = target.getHealth() / target.getMaxHealth();
        this.setHealth(Math.max(1.0f, this.getMaxHealth() * targetRatio));
        target.setHealth(Math.max(1.0f, target.getMaxHealth() * selfRatio));
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 2.2f, 28);
    }

    private void moveYuanshiQiBarrier(LivingEntity target) {
        clearProjectiles(8.0);
        applyTimedModifier(Attributes.ARMOR, "venerable_yuanshi_qi_barrier", 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 160);
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 2.2f, 28);
    }

    private void moveYuanshiPressure(LivingEntity target) {
        for (LivingEntity living : getEnemies(this.position(), 8.0)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        }
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 1.8f, 22);
    }

    private void moveYuanshiQiTornado(LivingEntity target) {
        addAreaEffect(this.position(), 6.0, 0.08f, 80, true, 1, 0, true, false,
            VfxType.TORNADO, 0xFFFFD700, 2.0f, null, 0);
    }

    private void moveYuanshiYinyangStrike(LivingEntity target) {
        castSingleScaled(target, 3200.0f, VfxType.QI_STORM, 0xFFFFD700, 1.4f, 16);
        target.setRemainingFireTicks(60);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
    }

    private void moveYuanshiFiveForbiddenLight(LivingEntity target) {
        for (int i = 0; i < 5; i++) {
            castSingleScaled(target, 1600.0f, VfxType.ENERGY_BEAM, 0xFFFFD700, 1.0f, 10);
        }
        modifyTerrain(target.position(), DaoPath.RULE, 3);
        addRoot(target, 40);
    }

    private void moveYuanshiQiGuard(LivingEntity target) {
        applyTimedModifier(Attributes.ARMOR, "venerable_yuanshi_qi_guard_armor", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        healPercent(0.02f);
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 2.0f, 26);
    }

    private void moveYuanshiTaichingCleanse(LivingEntity target) {
        this.removeAllEffects();
        healPercent(0.10f);
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 2.0f, 24);
    }

    private void moveYuanshiYinyangHarmony(LivingEntity target) {
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_yuanshi_harmony_atk", 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.ARMOR, "venerable_yuanshi_harmony_armor", 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_yuanshi_harmony_speed", 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 2.2f, 28);
    }

    private void moveYuanshiQiPierce(LivingEntity target) {
        if (this.distanceTo(target) <= 18.0f) {
            hurtScaled(target, 2200.0f, true);
            spawnVfx(VfxType.ENERGY_BEAM, target.getX(), target.getY() + 1.0, target.getZ(),
                target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), 0xFFFFD700, 1.7f, 22);
        }
    }

    private void moveYuanshiAllQiAbsorb(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 1800.0f, VfxType.QI_STORM, 0xFFFFD700, 2.0f, 24);
        healPercent(0.05f);
    }

    private void moveYuanshiYinyangGrind(LivingEntity target) {
        addAreaEffect(this.position(), 8.0, 0.03f, 200, true, 1, 1, true, false,
            VfxType.TORNADO, 0xFFFFD700, 2.2f, DaoPath.RULE, 3);
    }

    private void moveYuanshiPrimalQi(LivingEntity target) {
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_yuanshi_primal_atk", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.ARMOR, "venerable_yuanshi_primal_armor", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.QI_STORM, 0xFFFFD700, 2.6f, 32);
    }

    private void moveYuanshiOriginStrike(LivingEntity target) {
        castSingleScaled(target, 12000.0f, VfxType.QI_STORM, 0xFFFFD700, 2.6f, 36);
        castAreaScaled(target.position(), 8.0, 5400.0f, VfxType.SKY_STRIKE, 0xFFFFD700, 2.2f, 28);
        modifyTerrain(target.position(), DaoPath.RULE, 4);
    }

    private void moveHonglianTimeBurst(LivingEntity target) {
        castSingleScaled(target, 4000.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 1.6f, 20);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_honglian_burst_speed", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 120);
    }

    private void moveHonglianTimeFreeze(LivingEntity target) {
        this.hongLianFreezeTicks = 60;
        spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 2.6f, 32);
    }

    private void moveHonglianFateBreak(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
        castSingleScaled(target, 1.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 1.4f, 18);
    }

    private void moveHonglianAncientPredecessor(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.QI_MASTER, target, 1.00);
        spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 1.9f, 24);
    }

    private void moveHonglianSpringCicada(LivingEntity target) {
        this.hongLianCicadaUsed = false;
        spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 2.3f, 30);
    }

    private void moveHonglianTimeRewind(LivingEntity target) {
        healPercent(Math.min(0.30f, this.recentDamagePercent));
        spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 2.2f, 28);
    }

    private void moveHonglianPastPresent(LivingEntity target) {
        for (int i = 0; i < 6; i++) {
            summonPhantom(PhantomImmortalEntity.ImmortalType.QI_MASTER, target, 0.80);
        }
        addAreaEffect(this.position(), 10.0, 0.08f, 100, true, 1, 1, false, false,
            VfxType.TIME_DISTORTION, 0xFFFF0033, 2.6f, null, 0);
    }

    private void moveHonglianRedLotusFire(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 3600.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 2.0f, 24);
        addAreaEffect(this.position(), 8.0, 0.03f, 100, true, 1, 0, false, false,
            VfxType.TIME_DISTORTION, 0xFFFF0033, 1.8f, DaoPath.FIRE, 3);
    }

    private void moveHonglianTimeReversePosition(LivingEntity target) {
        Vec3 oldPos = getPositionAgo(target, 60);
        target.teleportTo(oldPos.x, oldPos.y, oldPos.z);
        castSingleScaled(target, 3200.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 1.5f, 20);
    }

    private void moveHonglianSpaceFracture(LivingEntity target) {
        castAreaScaled(target.position(), 6.0, 3600.0f, VfxType.SPATIAL_TEAR, 0xFFFF0033, 2.0f, 24);
        modifyTerrain(target.position(), DaoPath.FIRE, 3);
        spawnVfx(VfxType.TIME_DISTORTION, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFFFF0033, 1.6f, 20);
    }

    private void moveHonglianTimeHaste(LivingEntity target) {
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_honglian_haste_atk", 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        applyTimedModifier(Attributes.MOVEMENT_SPEED, "venerable_honglian_haste_speed", 0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 2.2f, 28);
    }

    private void moveHonglianRedLotusDomain(LivingEntity target) {
        addAreaEffect(this.position(), 10.0, 0.03f, 200, true, 2, 1, false, false,
            VfxType.TIME_DISTORTION, 0xFFFF0033, 2.4f, DaoPath.FIRE, 4);
    }

    private void moveHonglianTimeParadox(LivingEntity target) {
        addDamageMark(target, 60, 2.0f);
        castSingleScaled(target, 1.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 1.4f, 18);
    }

    private void moveHonglianSpaceBanish(LivingEntity target) {
        Vec3 dir = target.position().subtract(this.position()).normalize();
        Vec3 exile = target.position().add(dir.scale(20.0));
        target.teleportTo(exile.x, exile.y, exile.z);
        spawnVfx(VfxType.SPATIAL_TEAR, exile.x, exile.y + 1.0, exile.z, 0.0, 1.0, 0.0, 0xFFFF0033, 1.8f, 22);
    }

    private void moveHonglianTimeSpear(LivingEntity target) {
        if (this.distanceTo(target) <= 20.0f) {
            hurtScaled(target, 2600.0f, true);
            spawnVfx(VfxType.ENERGY_BEAM, target.getX(), target.getY() + 1.0, target.getZ(),
                target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), 0xFFFF0033, 1.8f, 24);
        }
        spawnVfx(VfxType.TIME_DISTORTION, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFFFF0033, 1.6f, 22);
    }

    private void moveHonglianRedLotusRage(LivingEntity target) {
        if (this.currentPhase >= 3) {
            this.hongLianRageTicks = 100;
            spawnCenterVfx(VfxType.TIME_DISTORTION, 0xFFFF0033, 2.6f, 34);
        } else {
            castSingleScaled(target, 1.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 1.2f, 14);
        }
    }

    private void moveHonglianCausalityReverse(LivingEntity target) {
        float pct = Math.max(0.02f, Math.min(0.30f, this.recentDamagePercent * 2.0f));
        castSingleScaled(target, 6400.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 2.0f, 26);
    }

    private void moveHonglianSpaceTimeCollapse(LivingEntity target) {
        castAreaScaled(this.position(), 12.0, 9000.0f, VfxType.TIME_DISTORTION, 0xFFFF0033, 3.0f, 42);
        modifyTerrain(this.position(), DaoPath.TIME, 5);
        this.hongLianFreezeTicks = 100;
        spawnCenterVfx(VfxType.SPATIAL_TEAR, 0xFFFF0033, 2.8f, 38);
    }

    private void moveYuanlianVineWhip(LivingEntity target) {
        castSingleScaled(target, 3200.0f, VfxType.VINE_CAGE, 0xFF33CC33, 1.4f, 18);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        knockbackTargets(target.position(), 3.0, 1.0, 0.3);
    }

    private void moveYuanlianVineBind(LivingEntity target) {
        addRoot(target, 60);
        castSingleScaled(target, 3600.0f, VfxType.VINE_CAGE, 0xFF33CC33, 1.6f, 22);
    }

    private void moveYuanlianSporeRain(LivingEntity target) {
        castAreaScaled(this.position(), 8.0, 2160.0f, VfxType.VINE_CAGE, 0xFF33CC33, 2.0f, 24);
        addAreaEffect(this.position(), 8.0, 0.02f, 100, true, 1, 0, false, false,
            VfxType.VINE_CAGE, 0xFF33CC33, 1.8f, DaoPath.WOOD, 3);
    }

    private void moveYuanlianGenesisLotus(LivingEntity target) {
        healPercent(0.20f);
        castAreaScaled(this.position(), 8.0, 3600.0f, VfxType.HEAL_SPIRAL, 0xFF33CC33, 2.4f, 30);
        spawnCenterVfx(VfxType.VINE_CAGE, 0xFF33CC33, 2.0f, 24);
    }

    private void moveYuanlianPaintPrison(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
        addAreaEffect(target.position(), 4.0, 0.05f, 100, true, 3, 1, false, false,
            VfxType.VINE_CAGE, 0xFF33CC33, 1.8f, null, 0);
    }

    private void moveYuanlianRegeneration(LivingEntity target) {
        healPercent(0.08f);
        applyTimedModifier(Attributes.MAX_HEALTH, "venerable_yuanlian_regen_max_health", 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
        spawnCenterVfx(VfxType.HEAL_SPIRAL, 0xFF33CC33, 1.8f, 22);
    }

    private void moveYuanlianVineWall(LivingEntity target) {
        modifyTerrain(this.position(), DaoPath.WOOD, 3);
        spawnCenterVfx(VfxType.VINE_CAGE, 0xFF33CC33, 2.2f, 26);
    }

    private void moveYuanlianPaintClone(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.STAR_SAGE, target, 0.30);
        summonPhantom(PhantomImmortalEntity.ImmortalType.STAR_SAGE, target, 0.30);
        spawnCenterVfx(VfxType.SHADOW_FADE, 0xFF33CC33, 1.8f, 22);
    }

    private void moveYuanlianWoodRevive(LivingEntity target) {
        healPercent(0.05f);
        for (UUID id : this.summonIds) {
            if (!(this.level() instanceof ServerLevel sl)) {
                continue;
            }
            Entity entity = sl.getEntity(id);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                living.heal((float) (living.getMaxHealth() * 0.05f));
            }
        }
        spawnCenterVfx(VfxType.HEAL_SPIRAL, 0xFF33CC33, 1.9f, 22);
    }

    private void moveYuanlianPoisonVine(LivingEntity target) {
        addAreaEffect(target.position(), 3.0, 0.02f, 200, true, 1, 0, false, false,
            VfxType.VINE_CAGE, 0xFF33CC33, 1.6f, null, 0);
    }

    private void moveYuanlianGenesisScroll(LivingEntity target) {
        addAreaEffect(this.position(), 12.0, 0.00f, 200, true, 1, 1, false, false,
            VfxType.VINE_CAGE, 0xFF33CC33, 2.2f, DaoPath.WOOD, 3);
        applyTimedModifier(Attributes.ATTACK_DAMAGE, "venerable_yuanlian_scroll_atk", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, 200);
    }

    private void moveYuanlianTreeOfLife(LivingEntity target) {
        modifyTerrain(this.position(), DaoPath.WOOD, 4);
        addAreaEffect(this.position(), 8.0, 0.00f, 200, true, -1, -1, false, false,
            VfxType.HEAL_SPIRAL, 0xFF33CC33, 2.4f, DaoPath.WOOD, 3);
        healPercent(0.10f);
    }

    private void moveYuanlianVineBurst(LivingEntity target) {
        castAreaScaled(this.position(), 6.0, 4320.0f, VfxType.VINE_CAGE, 0xFF33CC33, 2.0f, 24);
        knockbackTargets(this.position(), 6.0, 1.0, 0.3);
    }

    private void moveYuanlianPaintSeal(LivingEntity target) {
        addRoot(target, 60);
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 4));
        target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 4));
        spawnVfx(VfxType.VINE_CAGE, target.getX(), target.getY() + 1.0, target.getZ(), 0.0, 1.0, 0.0, 0xFF33CC33, 2.0f, 24);
    }

    private void moveYuanlianWoodSpiritSummon(LivingEntity target) {
        summonPhantom(PhantomImmortalEntity.ImmortalType.QI_MASTER, target, 0.50);
        summonPhantom(PhantomImmortalEntity.ImmortalType.QI_MASTER, target, 0.50);
        spawnCenterVfx(VfxType.VINE_CAGE, 0xFF33CC33, 2.0f, 24);
    }

    private void moveYuanlianGenesisGrandLotus(LivingEntity target) {
        castAreaScaled(this.position(), 12.0, 7200.0f, VfxType.VINE_CAGE, 0xFF33CC33, 2.8f, 38);
        healPercent(0.30f);
        modifyTerrain(this.position(), DaoPath.WOOD, 5);
        spawnCenterVfx(VfxType.HEAL_SPIRAL, 0xFF33CC33, 2.8f, 36);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("VenerableType", this.venerableType.name());
        tag.putInt("CurrentPhase", this.currentPhase);
        tag.putInt("AbilityCooldown", this.abilityCooldown);
        tag.putInt("WujiShieldTicks", this.wujiShieldTicks);
        tag.putBoolean("WujiImmortalUsed", this.wujiImmortalUsed);
        tag.putBoolean("YouHunSoulSplitUsed", this.youHunSoulSplitUsed);
        tag.putInt("YouHunDevourStacks", this.youHunDevourStacks);
        tag.putBoolean("HongLianCicadaUsed", this.hongLianCicadaUsed);
        tag.putBoolean("JuYangImmortalUsed", this.juYangImmortalUsed);
        tag.putInt("JuYangDrainTicks", this.juYangDrainTicks);
        tag.putInt("KuangManAwakenTicks", this.kuangManAwakenTicks);
        tag.putInt("KuangManActiveForm", this.kuangManActiveForm);
        tag.putInt("KuangManFormTicks", this.kuangManFormTicks);
        tag.putInt("HongLianFreezeTicks", this.hongLianFreezeTicks);
        tag.putInt("HongLianRageTicks", this.hongLianRageTicks);
        tag.putInt("MercyTicks", this.mercyTicks);
        tag.putInt("AbsoluteInvulTicks", this.absoluteInvulTicks);
        tag.putBoolean("XingXiuPredictActive", this.xingXiuPredictActive);
        tag.putFloat("HongLianSavedHealth", this.hongLianSavedHealth);
        tag.put("CombatAI", this.combatAI.save());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("VenerableType", Tag.TAG_STRING)) {
            setVenerableType(VenerableType.fromName(tag.getString("VenerableType")));
        }
        this.currentPhase = Math.max(1, tag.getInt("CurrentPhase"));
        this.abilityCooldown = Math.max(0, tag.getInt("AbilityCooldown"));
        this.wujiShieldTicks = Math.max(0, tag.getInt("WujiShieldTicks"));
        this.wujiImmortalUsed = tag.getBoolean("WujiImmortalUsed");
        this.youHunSoulSplitUsed = tag.getBoolean("YouHunSoulSplitUsed");
        this.youHunDevourStacks = Math.max(0, tag.getInt("YouHunDevourStacks"));
        this.hongLianCicadaUsed = tag.getBoolean("HongLianCicadaUsed");
        this.juYangImmortalUsed = tag.getBoolean("JuYangImmortalUsed");
        this.juYangDrainTicks = Math.max(0, tag.getInt("JuYangDrainTicks"));
        this.kuangManAwakenTicks = Math.max(0, tag.getInt("KuangManAwakenTicks"));
        this.kuangManActiveForm = tag.getInt("KuangManActiveForm");
        this.kuangManFormTicks = Math.max(0, tag.getInt("KuangManFormTicks"));
        this.hongLianFreezeTicks = Math.max(0, tag.getInt("HongLianFreezeTicks"));
        this.hongLianRageTicks = Math.max(0, tag.getInt("HongLianRageTicks"));
        this.mercyTicks = Math.max(0, tag.getInt("MercyTicks"));
        this.absoluteInvulTicks = Math.max(0, tag.getInt("AbsoluteInvulTicks"));
        this.xingXiuPredictActive = tag.getBoolean("XingXiuPredictActive");
        this.hongLianSavedHealth = tag.getFloat("HongLianSavedHealth");
        if (tag.contains("CombatAI", Tag.TAG_COMPOUND)) {
            this.combatAI.load(tag.getCompound("CombatAI"));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 200;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }
}
