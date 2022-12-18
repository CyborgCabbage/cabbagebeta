package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.datafixers.util.Pair;
import cyborgcabbage.cabbagebeta.gen.FeaturesProperty;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenMinable;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.*;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class BetaChunkGenerator extends ChunkGenerator {
    protected final Registry<Biome> biomeRegistry;
    protected MapGenBase caveGen;
    protected Random rand;
    long worldSeed;
    protected AquiferSampler.FluidLevelSampler fluidLevelSampler;
    protected final BlockState defaultBlock;
    protected final BlockState oceanFluid;
    protected final BlockState airBlock;

    public BetaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, FeaturesProperty features, BlockState defaultBlock, BlockState oceanFluid) {
        super(structureSetRegistry, Optional.empty(), new BetaOverworldBiomeSource(biomeRegistry, features));
        this.biomeRegistry = biomeRegistry;
        this.defaultBlock = defaultBlock;
        this.oceanFluid = oceanFluid;
        this.airBlock = Blocks.AIR.getDefaultState();
        if(biomeSource instanceof BetaOverworldBiomeSource bobs){
            bobs.setGenerator(this);
        }
    }

    abstract BiomeGenBase getBiome(int x, int z);
    abstract RegistryKey<Biome> getModernBiome(int x, int z);

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

    abstract public double[] generateTerrainNoiseColumn(int xOffset, int zOffset, int yNoiseSize);

    protected float getOreRangeScale() {
        return 1.f;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return switch(heightmap) {
            case WORLD_SURFACE_WG, WORLD_SURFACE -> Math.max(getSolidHeight(x, z, world), getSeaLevel());
            case OCEAN_FLOOR_WG, OCEAN_FLOOR, MOTION_BLOCKING, MOTION_BLOCKING_NO_LEAVES -> getSolidHeight(x, z, world);
        };
    }

    public int getSolidHeight(int x, int z, HeightLimitView world) {
        int yNoiseSize = getHeight()/8+1;
        double[] noise = generateTerrainNoiseColumn(x, z, yNoiseSize);
        for (int i = noise.length-1; i > 0; i--) {
            double above = noise[i];
            double below = noise[i-1];
            if(above < 0 && below > 0){
                //Found ground
                double delta = -above/(below-above);
                return Math.min(i*8-(int)Math.floor(8*delta), getHeight());
            }
        }
        return world.getBottomY();
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    public void buildSurface(Chunk chunk, HeightContext heightContext, NoiseConfig noiseConfig, StructureAccessor structureAccessor, BiomeAccess biomeAccess, Registry<Biome> biomeRegistry, Blender blender) {
        ChunkNoiseSampler chunkNoiseSampler = getOrCreateChunkNoiseSampler(chunk, noiseConfig, structureAccessor, blender);
        noiseConfig.getSurfaceBuilder().buildSurface(noiseConfig, biomeAccess, biomeRegistry, getModernSettings().usesLegacyRandom(), heightContext, chunk, chunkNoiseSampler, getModernSettings().surfaceRule());
    }

    protected BetaChunkNoiseSampler getOrCreateChunkNoiseSampler(Chunk chunk, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Blender blender) {
        return (BetaChunkNoiseSampler)chunk.getOrCreateChunkNoiseSampler(ch -> this.createChunkNoiseSampler(ch, structureAccessor, blender, noiseConfig));
    }

    protected BetaChunkNoiseSampler createChunkNoiseSampler(Chunk chunk, StructureAccessor world, Blender blender, NoiseConfig noiseConfig) {
        GenerationShapeConfig generationShapeConfig = getModernSettings().generationShapeConfig().trimHeight(chunk);
        ChunkPos chunkPos = chunk.getPos();
        int i = 16 / generationShapeConfig.horizontalBlockSize();
        return new BetaChunkNoiseSampler(
                i, noiseConfig, chunkPos.getStartX(), chunkPos.getStartZ(), generationShapeConfig, StructureWeightSampler.createStructureWeightSampler(world, chunk.getPos()), getModernSettings(), fluidLevelSampler, blender, this
        );
    }

    protected abstract ChunkGeneratorSettings getModernSettings();

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] blocks = new BlockState[getWorldHeight()];
        int yNoiseSize = getHeight()/8+1;
        double[] noise = generateTerrainNoiseColumn(x, z, yNoiseSize);
        int index = 0;
        if(getWorldHeight() == 384) {
            while(index < 64){
                blocks[index] = defaultBlock;
                index++;
            }
        }
        for (int i = 0; i < noise.length-1; i++) {
            double above = noise[i+1];
            double below = noise[i];
            for (int j = 0; j < 8; j++) {
                //Found ground
                double density = MathHelper.lerp(j/8.f, below, above);
                if (density > 0.0) {
                   blocks[index] = defaultBlock;
                }else if(index+getMinimumY() < getSeaLevel()){
                    blocks[index] = oceanFluid;
                }else {
                    blocks[index] = airBlock;
                }
                index++;
            }
        }
        while(index < getWorldHeight()) {
            blocks[index] = airBlock;
            index++;
        }
        return new VerticalBlockSample(getMinimumY(), blocks);
    }
}
