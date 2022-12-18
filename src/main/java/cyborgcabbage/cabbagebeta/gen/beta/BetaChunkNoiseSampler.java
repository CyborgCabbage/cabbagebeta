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
    public final DensityFunction beardFunction;
    private final BetaChunkGenerator columnSource;

    public BetaChunkNoiseSampler(int horizontalCellCount, NoiseConfig noiseConfig, int startX, int startZ, GenerationShapeConfig generationShapeConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender, BetaChunkGenerator columnSource) {
        super(horizontalCellCount, noiseConfig, startX, startZ, generationShapeConfig, beardifying, chunkGeneratorSettings, fluidLevelSampler, blender);
        this.columnSource = columnSource;
        beardFunction = DensityFunctionTypes.cacheAllInCell(DensityFunctionTypes.Beardifier.INSTANCE).apply(this::getActualDensityFunction);

    }

    public double sampleBeard() {
        return beardFunction.sample(this);
    }

    public int estimateSurfaceHeight(int blockX, int blockZ) {
        int i = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockX));
        int j = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockZ));
        return this.surfaceHeightEstimateCache.computeIfAbsent(ColumnPos.pack(i, j), this::calculateSurfaceHeightEstimate);
    }

    private int calculateSurfaceHeightEstimate(long columnPos) {
        int x = ColumnPos.getX(columnPos);
        int z = ColumnPos.getZ(columnPos);
        int yNoiseSize = columnSource.getHeight()/8+1;
        double[] noise = columnSource.generateTerrainNoiseColumn(x, z, yNoiseSize);
        for (int r = noise.length-1; r > 0; r--) {
            if (noise[r]*0.01 > 0.390625) {
                return r*8;
            }
        }
        return Integer.MAX_VALUE;
    }
}
