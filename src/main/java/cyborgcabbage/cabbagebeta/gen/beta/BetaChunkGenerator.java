package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.datafixers.util.Pair;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenMinable;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.*;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public abstract class BetaChunkGenerator extends ChunkGenerator {
    public BetaChunkGenerator(Registry<StructureSet> structureSetRegistry, BiomeSource biomeSource) {
        super(structureSetRegistry, Optional.empty(), biomeSource);
    }

    protected MapGenBase caveGen;
    protected Random rand;
    long worldSeed;

    abstract BiomeGenBase getBiome(int x, int z);
    abstract RegistryKey<Biome> getSmallBiome(int x, int z);

    @Override
    public void computeStructurePlacementsIfNeeded(NoiseConfig noiseConfig) {
        init(noiseConfig.getLegacyWorldSeed());
        super.computeStructurePlacementsIfNeeded(noiseConfig);
    }

    @Nullable
    @Override
    public Pair<BlockPos, RegistryEntry<Structure>> locateStructure(ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        init(world.getSeed());
        return super.locateStructure(world, structures, center, radius, skipReferencedStructures);
    }


    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
        this.caveGen.generate(chunk, worldSeed);
    }

    record WorldGeneratorContext(StructureWorldAccess world, int x, int z){}

    protected void generateFeature(WorldGeneratorContext context, WorldGenerator generator, int count, boolean offset) {
        generateFeature(context, generator, count, offset, (r) -> r.nextInt(getHeight()));
    }

    protected void generateFeature(WorldGeneratorContext context, WorldGenerator generator, int count, boolean offset, GenerateCoordinate generateCoordinate){
        for(int r = 0; r < count; ++r) {
            int x = context.x() + this.rand.nextInt(16) + (offset ? 8 : 0);
            int y = generateCoordinate.gen(this.rand);
            int z = context.z() + this.rand.nextInt(16) + (offset ? 8 : 0);
            generator.generate(context.world(), this.rand, x, y, z);
        }
    }

    interface GenerateCoordinate{
        int gen(Random rand);
    }

    protected void generateFeatureRare(WorldGeneratorContext context, WorldGenerator generator, int scarcity){
        if(rand.nextInt(scarcity) == 0) {
            int x = context.x() + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(getHeight());
            int z = context.z() + this.rand.nextInt(16) + 8;
            generator.generate(context.world(), this.rand, x, y, z);
        }
    }

    protected void generateMineable(WorldGeneratorContext context, BlockState block, int veinSize, int bound, int count){
        float density = count/(float)bound;
        int actualBound = Math.min(getHeight(), (int)(bound*getOreRangeScale()));
        int actualCount = (int)(density*actualBound);
        for(int r = 0; r < actualCount; ++r) {
            int x = context.x() + this.rand.nextInt(16);
            int y = this.rand.nextInt(actualBound);
            int z = context.z() + this.rand.nextInt(16);
            (new WorldGenMinable(block, veinSize)).generate(context.world(), this.rand, x, y, z);
        }
    }

    protected void generateMineableBinomial(WorldGeneratorContext context, BlockState block, int veinSize, int bound, int count){
        float density = count/(float)bound;
        int actualBound = Math.min(getHeight(), (int)(bound*getOreRangeScale()));
        int actualCount = (int)(density*actualBound);
        for(int r = 0; r < actualCount; ++r) {
            int x = context.x() + this.rand.nextInt(16);
            int y = this.rand.nextInt(actualBound)+this.rand.nextInt(actualBound);
            int z = context.z() + this.rand.nextInt(16);
            if(y >= getHeight()) continue;
            (new WorldGenMinable(block, veinSize)).generate(context.world(), this.rand, x, y, z);
        }
    }

    @Override
    public void setStructureStarts(DynamicRegistryManager registryManager, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager, long seed) {
        init(seed);
        super.setStructureStarts(registryManager, noiseConfig, structureAccessor, chunk, structureTemplateManager, seed);
    }

    @Override
    public void addStructureReferences(StructureWorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
        init(world.getSeed());
        super.addStructureReferences(world, structureAccessor, chunk);
    }

    abstract protected void init(long seed);

    abstract public int getHeight();

    abstract public float getHeightMultiplier();

    protected float getOreRangeScale() {
        return 1.f;
    }
}
