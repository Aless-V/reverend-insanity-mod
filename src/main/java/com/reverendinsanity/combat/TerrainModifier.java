package com.reverendinsanity.combat;

import com.reverendinsanity.core.path.DaoPath;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ThreadLocalRandom;

// Terrain Modification Utility Class
public final class TerrainModifier {

    private TerrainModifier() {
    }

    public static void modifyTerrain(ServerLevel level, BlockPos center, DaoPath path, int radius) {
        if (level == null || center == null || path == null || radius <= 0) {
            return;
        }
        int effectiveRadius = Math.max(1, radius);
        switch (path) {
            case STRENGTH -> crushPit(level, center, effectiveRadius);
            case EARTH -> raiseEarthWall(level, center, effectiveRadius);
            case ICE -> freezeAndSnow(level, center, effectiveRadius);
            case FIRE -> igniteAndBurn(level, center, effectiveRadius);
            case WOOD -> spreadVinesAndLeaves(level, center, effectiveRadius);
            case SPACE -> randomErase(level, center, effectiveRadius);
            case LIGHTNING -> shatterAndEnergize(level, center, effectiveRadius);
            case SOUL -> spreadSoulFlame(level, center, effectiveRadius);
            case RULE -> enforceBarrier(level, center, effectiveRadius);
            case BLOOD -> bloodPool(level, center, effectiveRadius);
            case STAR -> meteorImpact(level, center, effectiveRadius);
            case TIME -> temporalDecay(level, center, effectiveRadius);
            default -> {
            }
        }
    }

    private static void crushPit(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                double edge = 1.0d - (double) horizontalSq / (double) radiusSq;
                int depth = Math.max(1, (int) Math.round(edge * (radius * 0.9d)));
                for (int y = 0; y >= -depth; y--) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void raiseEarthWall(ServerLevel level, BlockPos center, int radius) {
        int outerSq = radius * radius;
        int innerSq = Math.max(0, (radius - 2) * (radius - 2));
        int height = Math.max(2, radius / 2 + 1);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > outerSq || horizontalSq < innerSq) {
                    continue;
                }
                for (int y = -1; y <= height; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void freezeAndSnow(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -2; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    if (current.is(Blocks.WATER)) {
                        level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                    } else if (current.blocksMotion() && level.isEmptyBlock(pos.above())) {
                        level.setBlock(pos, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void igniteAndBurn(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    if (isWooden(current)) {
                        level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                    } else if (current.blocksMotion() && random.nextFloat() < 0.35f) {
                        level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void spreadVinesAndLeaves(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BlockState vineState = Blocks.VINE.defaultBlockState().setValue(VineBlock.UP, Boolean.TRUE);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    if (random.nextFloat() < 0.35f) {
                        level.setBlock(pos, vineState, 3);
                    } else {
                        level.setBlock(pos, Blocks.OAK_LEAVES.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void randomErase(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        int vertical = Math.max(1, radius / 2);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -vertical; y <= vertical; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    if (random.nextFloat() < 0.45f) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void shatterAndEnergize(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    float roll = random.nextFloat();
                    if (roll < 0.35f) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (roll < 0.70f) {
                        level.setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3);
                    } else {
                        level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void spreadSoulFlame(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    if (isWooden(current)) {
                        level.setBlock(pos, Blocks.SOUL_FIRE.defaultBlockState(), 3);
                    } else if (random.nextFloat() < 0.5f) {
                        level.setBlock(pos, Blocks.SOUL_SOIL.defaultBlockState(), 3);
                    } else {
                        level.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void enforceBarrier(ServerLevel level, BlockPos center, int radius) {
        int outerSq = radius * radius;
        int innerSq = Math.max(0, (radius - 2) * (radius - 2));
        int height = Math.max(1, radius / 2);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > outerSq || horizontalSq < innerSq) {
                    continue;
                }
                for (int y = -1; y <= height; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void bloodPool(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -1; y <= 1; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    float roll = random.nextFloat();
                    if (current.blocksMotion() && level.isEmptyBlock(pos.above())) {
                        level.setBlock(pos, roll < 0.5f ? Blocks.CRIMSON_NYLIUM.defaultBlockState() : Blocks.RED_MUSHROOM_BLOCK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void meteorImpact(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                double edge = 1.0d - (double) horizontalSq / (double) radiusSq;
                int depth = Math.max(1, (int) Math.round(edge * (radius * 0.6d)));
                for (int y = 0; y >= -depth; y--) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    float roll = random.nextFloat();
                    if (roll < 0.3f) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (roll < 0.5f) {
                        level.setBlock(pos, Blocks.END_STONE.defaultBlockState(), 3);
                    } else if (roll < 0.7f) {
                        level.setBlock(pos, Blocks.CRYING_OBSIDIAN.defaultBlockState(), 3);
                    } else {
                        level.setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void temporalDecay(ServerLevel level, BlockPos center, int radius) {
        int radiusSq = radius * radius;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int horizontalSq = x * x + z * z;
                if (horizontalSq > radiusSq) {
                    continue;
                }
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (!isModifiable(current)) {
                        continue;
                    }
                    float roll = random.nextFloat();
                    if (roll < 0.35f) {
                        level.setBlock(pos, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
                    } else if (roll < 0.55f) {
                        level.setBlock(pos, Blocks.OXIDIZED_COPPER.defaultBlockState(), 3);
                    } else if (roll < 0.75f) {
                        level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static boolean isWooden(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) || state.is(BlockTags.LEAVES);
    }

    private static boolean isModifiable(BlockState state) {
        return !state.isAir() && !state.is(Blocks.BEDROCK);
    }
}
