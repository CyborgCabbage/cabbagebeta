package cyborgcabbage.cabbagebeta.gen.beta;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;

public class BetaChunkNoiseSampler extends ChunkNoiseSampler {
    private final Long2IntMap surfaceHeightEstimateCache = new Long2IntOpenHashMap();
    private final DensityFunctionTypes.Beardifying beardifying;

    public BetaChunkNoiseSampler(int horizontalCellCount, NoiseConfig noiseConfig, int startX, int startZ, GenerationShapeConfig generationShapeConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
        super(horizontalCellCount, noiseConfig, startX, startZ, generationShapeConfig, beardifying, chunkGeneratorSettings, fluidLevelSampler, blender);
        this.beardifying = beardifying;
    }

    @Override
    public int estimateSurfaceHeight(int blockX, int blockZ) {
        int i = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockX));
        int j = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockZ));
        return this.surfaceHeightEstimateCache.computeIfAbsent(ColumnPos.pack(i, j), this::calculateSurfaceHeightEstimate);
    }

    private int calculateSurfaceHeightEstimate(long columnPos) {
        int i = ColumnPos.getX(columnPos);
        int j = ColumnPos.getZ(columnPos);
        /*int k = this.generationShapeConfig.minimumY();

        for(int l = k + this.generationShapeConfig.height(); l >= k; l -= this.verticalBlockSize) {
            if (this.initialDensityWithoutJaggedness.sample(new DensityFunction.UnblendedNoisePos(i, l, j)) > 0.390625) {
                return l;
            }
        }*/

        return 64;
    }

    public double getBeard(int x, int y, int z){
        bsp.set(x,y,z);
        return beardifying.sample(bsp);
    }

    private final BeardSamplePoint bsp = new BeardSamplePoint();

    static class BeardSamplePoint implements DensityFunction.NoisePos {
        int x = 0;
        int y = 0;
        int z = 0;

        public void set(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int blockX() {
            return x;
        }

        @Override
        public int blockY() {
            return y;
        }

        @Override
        public int blockZ() {
            return z;
        }
    }
}
