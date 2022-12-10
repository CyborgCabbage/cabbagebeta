package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomeProvider;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomes;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomesSampler;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenCaves;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BetaChunkGenerator extends ChunkGenerator implements BetaBiomeProvider {
    public static final Codec<BetaChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry),
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(generator -> generator.biomeRegistry),
            Codec.BOOL.fieldOf("use_full_height").orElse(false).forGetter(b -> b.prop.useFullHeight()),
            Codec.INT.fieldOf("sea_level").orElse(64).forGetter(b -> b.prop.seaLevel()),
            Codec.FLOAT.fieldOf("factor").orElse(12.0f).forGetter(b -> b.prop.factor()),
            Codec.INT.fieldOf("ground_level").orElse(68).forGetter(b -> b.prop.groundLevel()),
            Codec.INT.fieldOf("cave_lava_level").orElse(10).forGetter(b -> b.prop.caveLavaLevel()),
            Codec.FLOAT.fieldOf("mixing").orElse(1.0f).forGetter(b -> b.prop.mixing()),
            Codec.BOOL.fieldOf("fixes").orElse(false).forGetter(b -> b.prop.fixes()),
            Codec.BOOL.fieldOf("substitute_biomes").orElse(false).forGetter(b -> b.prop.substituteBiomes())
    ).apply(instance, instance.stable(BetaChunkGenerator::new)));
    private final Registry<Biome> biomeRegistry;

    private NoiseGeneratorOctaves noise16a;
    private NoiseGeneratorOctaves noise16b;
    private NoiseGeneratorOctaves noise8a;
    private NoiseGeneratorOctaves noise4a;
    private NoiseGeneratorOctaves noise4b;
    private NoiseGeneratorOctaves noise10a;
    private NoiseGeneratorOctaves noise16c;
    private NoiseGeneratorOctaves treeNoise;
    private double[] terrainNoiseValues;
    private double[] sandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] stoneNoise = new double[256];
    double[] highFreq3d8;
    double[] lowFreq3d16a;
    double[] lowFreq3d16b;
    double[] highFreq2d10;
    double[] lowFreq2d16;
    private double[] generatedTemperatures;
    final protected MapGenBase caveGen;
    private BetaBiomes terrainBiomes;
    private BetaBiomesSampler externalBiomes;
    private final BetaProperties prop;
    protected Random rand;
    long worldSeed;

    protected void init(long seed){
        if(rand == null) {
            rand = new Random(seed);
            worldSeed = seed;
            this.terrainBiomes = new BetaBiomes(seed);
            this.externalBiomes = new BetaBiomesSampler(seed);
            this.noise16a = new NoiseGeneratorOctaves(this.rand, 16);
            this.noise16b = new NoiseGeneratorOctaves(this.rand, 16);
            this.noise8a = new NoiseGeneratorOctaves(this.rand, 8);
            this.noise4a = new NoiseGeneratorOctaves(this.rand, 4);
            this.noise4b = new NoiseGeneratorOctaves(this.rand, 4);
            this.noise10a = new NoiseGeneratorOctaves(this.rand, 10);
            this.noise16c = new NoiseGeneratorOctaves(this.rand, 16);
            this.treeNoise = new NoiseGeneratorOctaves(this.rand, 8);
        }
    }

    public BetaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, boolean useFullHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes, boolean substituteBiomes) {
        this(structureSetRegistry, biomeRegistry, new BetaProperties(useFullHeight, seaLevel, factor, groundLevel, caveLavaLevel, mixing, fixes, substituteBiomes));

    }

    public BetaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, BetaProperties p) {
        super(structureSetRegistry, Optional.empty(), new BetaOverworldBiomeSource(biomeRegistry, p.substituteBiomes()));
        this.biomeRegistry = biomeRegistry;
        this.prop = p;
        this.caveGen = new MapGenCaves(10, 256, 16, 100);


        if(biomeSource instanceof BetaOverworldBiomeSource bobs){
            bobs.setGenerator(this);
        }
    }

        @Override
    public int getSeaLevel() {
        return prop.seaLevel();
    }

    public BetaProperties getBetaProperties() {
        return prop;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        /*
        //BlockSand.fallInstantly = true;
        int i2 = chunk.getPos().x;
        int i3 = chunk.getPos().z;
        int chunkX = i2 * 16;
        int chunkZ = i3 * 16;
        WorldGeneratorContext context = new WorldGeneratorContext(world, chunkX, chunkZ);
        BiomeGenBase biome = terrainBiomes.getBiomeAtBlock(chunkX + 16, chunkZ + 16);
        this.rand.setSeed(worldSeed);
        long j7 = this.rand.nextLong() / 2L * 2L + 1L;
        long j9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)i2 * j7 + (long)i3 * j9 ^ worldSeed);
        generateFeatureRare(context, new WorldGenLakes(Blocks.WATER.getDefaultState()), 4);
        if(this.rand.nextInt(8) == 0) {
            int i13 = chunkX + this.rand.nextInt(16) + 8;
            int i14 = this.rand.nextInt(this.rand.nextInt(getHeight()-8) + 8);
            int i15 = chunkZ + this.rand.nextInt(16) + 8;
            if(i14 < 64 || this.rand.nextInt(10) == 0) {
                (new WorldGenLakes(Blocks.LAVA.getDefaultState())).generate(world, this.rand, i13, i14, i15);
            }
        }
        generateFeature(context, new WorldGenDungeons(), 8, true);
        generateFeature(context, new WorldGenClay(32), 10, false);
        generateMineable(context, Blocks.DIRT.getDefaultState(), 32, getHeight(), 20);
        generateMineable(context, Blocks.GRAVEL.getDefaultState(), 32, getHeight(), 10);
        generateMineable(context, Blocks.COAL_ORE.getDefaultState(), 16, getHeight(), 20);
        generateMineable(context, Blocks.IRON_ORE.getDefaultState(), 8, 64, 20);
        generateMineable(context, Blocks.GOLD_ORE.getDefaultState(), 8, 32, 2);
        generateMineable(context, Blocks.REDSTONE_ORE.getDefaultState(), 7, 16, 8);
        generateMineable(context, Blocks.DIAMOND_ORE.getDefaultState(), 7, 16, 1);
        generateMineableBinomial(context, Blocks.LAPIS_ORE.getDefaultState(), 6, 16, 1);

        double d11 = 0.5D;
        int extraTrees = (int)((this.treeNoise.func_806_a((double)chunkX * d11, (double)chunkZ * d11) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
        int treeCount = 0;
        if(this.rand.nextInt(10) == 0) {
            ++treeCount;
        }
        if(biome.addExtraTrees) treeCount += extraTrees;
        treeCount += biome.treeCount;

        for(int i = 0; i < treeCount; ++i) {
            int x = chunkX + this.rand.nextInt(16) + 8;
            int z = chunkZ + this.rand.nextInt(16) + 8;
            WorldGenerator generator = biome.getRandomWorldGenForTrees(this.rand, getHeight());
            generator.func_517_a(1.0D, 1.0D, 1.0D);
            generator.generate(world, this.rand, x, world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z), z);
        }
        generateFeature(context, new WorldGenFlowers(Blocks.DANDELION.getDefaultState()), biome.dandelionCount, true);

        for(int i17 = 0; i17 < biome.shrubCount; ++i17) {
            byte b26 = 1;
            if(biome == BiomeGenBase.rainforest && this.rand.nextInt(3) != 0) {
                b26 = 2;
            }

            int i19 = chunkX + this.rand.nextInt(16) + 8;
            int i20 = this.rand.nextInt(getHeight());
            int i21 = chunkZ + this.rand.nextInt(16) + 8;

            (new WorldGenTallGrass(b26 == 1 ? Blocks.GRASS.getDefaultState() : Blocks.FERN.getDefaultState())).generate(world, this.rand, i19, i20, i21);
        }

        if(biome == BiomeGenBase.desert) {
            generateFeature(context, new WorldGenDeadBush(Blocks.DEAD_BUSH.getDefaultState()), 2, true);
        }
        generateFeatureRare(context, new WorldGenFlowers(Blocks.POPPY.getDefaultState()), 2);
        generateFeatureRare(context, new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState()), 4);
        generateFeatureRare(context, new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState()), 8);
        generateFeature(context, new WorldGenReed(), 10, true);
        generateFeatureRare(context, new WorldGenPumpkin(), 32);
        if(biome == BiomeGenBase.desert) {
            generateFeature(context, new WorldGenCactus(), 10, true);
        }
        generateFeature(context, new WorldGenLiquids(Blocks.WATER.getDefaultState()), 50, true, r -> r.nextInt(r.nextInt(getHeight()-8) + 8));
        generateFeature(context, new WorldGenLiquids(Blocks.LAVA.getDefaultState()), 20, true, r -> r.nextInt(r.nextInt(r.nextInt(getHeight()-16) + 8) + 8));

        this.generatedTemperatures = terrainBiomes.getTemperatures(this.generatedTemperatures, chunkX + 8, chunkZ + 8, 16, 16);

        for(int x = chunkX + 8; x < chunkX + 8 + 16; ++x) {
            for(int z = chunkZ + 8; z < chunkZ + 8 + 16; ++z) {
                int relX = x - (chunkX + 8);
                int relZ = z - (chunkZ + 8);
                int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                double temperature = this.generatedTemperatures[relX * 16 + relZ] - (double)(surfaceY - 64) / 64.0D * 0.3d;
                BlockPos.Mutable blockPosTop = new BlockPos.Mutable(x, surfaceY, z);
                BlockState stateBelow = world.getBlockState(blockPosTop.down());
                if(temperature < 0.5D && surfaceY > 0 && surfaceY < getHeight() && world.isAir(blockPosTop) && stateBelow.getMaterial().isSolid() && stateBelow.getMaterial() != Material.ICE) {
                    world.setBlockState(blockPosTop, Blocks.SNOW.getDefaultState(), Block.NOTIFY_ALL);
                    Blocks.SNOW.getDefaultState().updateNeighbors(world, blockPosTop, Block.NOTIFY_ALL);
                }
            }
        }

        //BlockSand.fallInstantly = false;
        */
    }

    record WorldGeneratorContext(StructureWorldAccess world, int x, int z){}

    private void generateFeature(WorldGeneratorContext context, WorldGenerator generator, int count, boolean offset) {
        generateFeature(context, generator, count, offset, (r) -> r.nextInt(getHeight()));
    }

    private void generateFeature(WorldGeneratorContext context, WorldGenerator generator, int count, boolean offset, GenerateCoordinate generateCoordinate){
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

    private void generateFeatureRare(WorldGeneratorContext context, WorldGenerator generator, int scarcity){
        if(rand.nextInt(scarcity) == 0) {
            int x = context.x() + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(getHeight());
            int z = context.z() + this.rand.nextInt(16) + 8;
            generator.generate(context.world(), this.rand, x, y, z);
        }
    }

    private void generateMineable(WorldGeneratorContext context, BlockState block, int veinSize, int bound, int count){
        for(int r = 0; r < count; ++r) {
            int x = context.x() + this.rand.nextInt(16);
            int y = this.rand.nextInt(bound);
            int z = context.z() + this.rand.nextInt(16);
            (new WorldGenMinable(block, veinSize)).generate(context.world(), this.rand, x, y, z);
        }
    }

    private void generateMineableBinomial(WorldGeneratorContext context, BlockState block, int veinSize, int bound, int count){
        for(int r = 0; r < count; ++r) {
            int x = context.x() + this.rand.nextInt(16);
            int y = this.rand.nextInt(bound)+this.rand.nextInt(bound);
            int z = context.z() + this.rand.nextInt(16);
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

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        var pos = chunk.getPos();
        this.rand.setSeed((long) pos.x * 341873128712L + (long) pos.z * 132897987541L);
        //this.terrainBiomes.biomes = terrainBiomes.generateBiomes(this.terrainBiomes.biomes, pos.x * 16, pos.z * 16, 16, 16);
        //this.generateTerrain(chunk);
        //this.replaceBlocksForBiome(chunk, this.terrainBiomes.biomes);
        BlockState state = Blocks.STONE.getDefaultState();
        for (ChunkSection chunkSection : chunk.getSectionArray()) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        chunkSection.setBlockState(x, y, z, state, false);
                    }
                }
            }
        }
        this.caveGen.generate(chunk, worldSeed);
        return CompletableFuture.completedFuture(chunk);
    }

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        int horizontalNoiseSize = 4;
        int xNoiseSize = horizontalNoiseSize + 1;
        int yNoiseSize = getHeight()/8+1;
        int zNoiseSize = horizontalNoiseSize + 1;
        this.terrainNoiseValues = this.generateTerrainNoise(this.terrainNoiseValues, pos.x * horizontalNoiseSize, 0, pos.z * horizontalNoiseSize, xNoiseSize, yNoiseSize, zNoiseSize);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int xNoiseIndex = 0; xNoiseIndex < horizontalNoiseSize; ++xNoiseIndex) {
            for(int zNoiseIndex = 0; zNoiseIndex < horizontalNoiseSize; ++zNoiseIndex) {
                for(int yNoiseIndex = 0; yNoiseIndex < getHeight()/8; ++yNoiseIndex) {
                    double yFrac = 0.125D;
                    double v000 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSize + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double v010 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSize + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double v100 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSize + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double v110 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSize + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double v001 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSize + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - v000) * yFrac;
                    double v011 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSize + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - v010) * yFrac;
                    double v101 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSize + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - v100) * yFrac;
                    double v111 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSize + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - v110) * yFrac;

                    for(int ySub = 0; ySub < 8; ++ySub) {
                        blockPos.setY(yNoiseIndex * 8 + ySub);
                        double xFrac = 0.25D;
                        double d35 = v000;
                        double d37 = v010;
                        double d39 = (v100 - v000) * xFrac;
                        double d41 = (v110 - v010) * xFrac;

                        for(int xSub = 0; xSub < 4; ++xSub) {
                            blockPos.setX(xNoiseIndex * 4 + xSub);
                            double zFrac = 0.25D;
                            double density = d35;
                            double zNoiseStep = (d37 - d35) * zFrac;

                            for(int zSub = 0; zSub < 4; ++zSub) {
                                blockPos.setZ(zNoiseIndex * 4 + zSub);
                                double d53 = terrainBiomes.temperature[(xNoiseIndex * 4 + xSub) * 16 + zNoiseIndex * 4 + zSub];
                                Block blockState = Blocks.AIR;
                                if(yNoiseIndex * 8 + ySub < prop.seaLevel()) {
                                    if(d53 < 0.5D && yNoiseIndex * 8 + ySub >= prop.seaLevel() - 1) {
                                        blockState = Blocks.ICE;
                                    } else {
                                        blockState = Blocks.WATER;
                                    }
                                }

                                if(density > 0.0D) {
                                    blockState = Blocks.STONE;
                                }
                                chunk.setBlockState(blockPos, blockState.getDefaultState(), false);
                                density += zNoiseStep;
                            }
                            d35 += d39;
                            d37 += d41;
                        }
                        v000 += v001;
                        v010 += v011;
                        v100 += v101;
                        v110 += v111;
                    }
                }
            }
        }
    }

    private double[] generateTerrainNoise(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xNoiseSize, int yNoiseSize, int zNoiseSize) {
        if(noiseArray == null) {
            noiseArray = new double[xNoiseSize * yNoiseSize * zNoiseSize];
        }

        double hScale = 684.412d;
        double vScale = 684.412d;
        double[] temp = this.terrainBiomes.temperature;
        double[] humidity = this.terrainBiomes.humidity;
        this.highFreq2d10 = this.noise10a.func_4109_a(this.highFreq2d10, xOffset, zOffset, xNoiseSize, zNoiseSize, 1.121D, 1.121D, 0.5D);
        this.lowFreq2d16 = this.noise16c.func_4109_a(this.lowFreq2d16, xOffset, zOffset, xNoiseSize, zNoiseSize, 200.0D, 200.0D, 0.5D);
        this.highFreq3d8 = this.noise8a.generateNoiseOctaves(this.highFreq3d8, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale / 80.0D, vScale / 160.0D, hScale / 80.0D);
        this.lowFreq3d16a = this.noise16a.generateNoiseOctaves(this.lowFreq3d16a, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale, vScale, hScale);
        this.lowFreq3d16b = this.noise16b.generateNoiseOctaves(this.lowFreq3d16b, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale, vScale, hScale);
        int noiseIndex = 0;
        int noiseIndex2 = 0;
        int samplePeriod = 16 / xNoiseSize;

        for(int xNoiseIndex = 0; xNoiseIndex < xNoiseSize; ++xNoiseIndex) {
            int xSample;
            if(prop.fixes()){
                xSample = xNoiseIndex * 4;
                if(xSample > 15) xSample = 15;
            }else{
                xSample = xNoiseIndex * samplePeriod + samplePeriod / 2;
            }

            for(int zNoiseIndex = 0; zNoiseIndex < zNoiseSize; ++zNoiseIndex) {
                int zSample;
                if(prop.fixes()){
                    zSample = zNoiseIndex * 4;
                    if(zSample > 15) zSample = 15;
                }else{
                    zSample = zNoiseIndex * samplePeriod + samplePeriod / 2;
                }
                double tempVal = temp[xSample * 16 + zSample];
                double humidityVal = humidity[xSample * 16 + zSample] * tempVal;
                humidityVal = 1.0D - humidityVal;
                humidityVal *= humidityVal;
                humidityVal *= humidityVal;
                humidityVal = 1.0D - humidityVal;
                double highFreqHumid = (this.highFreq2d10[noiseIndex2] + 256.0D) / 512.0D;
                highFreqHumid *= humidityVal;
                if(highFreqHumid > 1.0D) {
                    highFreqHumid = 1.0D;
                }

                double lowFreq2d3 = this.lowFreq2d16[noiseIndex2] / 8000.0D;
                if(lowFreq2d3 < 0.0D) {
                    lowFreq2d3 = -lowFreq2d3 * 0.3d;
                }

                lowFreq2d3 = lowFreq2d3 * 3.0D - 2.0D;
                if(lowFreq2d3 < 0.0D) {
                    lowFreq2d3 /= 2.0D;
                    if(lowFreq2d3 < -1.0D) {
                        lowFreq2d3 = -1.0D;
                    }

                    lowFreq2d3 /= 1.4D;
                    lowFreq2d3 /= 2.0D;
                    highFreqHumid = 0.0D;
                } else {
                    if(lowFreq2d3 > 1.0D) {
                        lowFreq2d3 = 1.0D;
                    }

                    lowFreq2d3 /= 8.0D;
                }

                if(highFreqHumid < 0.0D) {
                    highFreqHumid = 0.0D;
                }

                highFreqHumid += 0.5D;
                lowFreq2d3 = lowFreq2d3 * (double) 17 / 16.0D;
                double groundLevelLocal = (double)prop.groundLevel()/8.0 + lowFreq2d3 * 4.0D;
                ++noiseIndex2;

                for(int yNoiseIndex = 0; yNoiseIndex < yNoiseSize; ++yNoiseIndex) {
                    double bias = ((double)yNoiseIndex - groundLevelLocal) * prop.factor() / highFreqHumid;
                    if(bias < 0.0D) {
                        bias *= 4.0D;
                    }

                    double a = this.lowFreq3d16a[noiseIndex] / 512.0D;
                    double b = this.lowFreq3d16b[noiseIndex] / 512.0D;
                    double mix = this.highFreq3d8[noiseIndex] / 20.0D * prop.mixing() + 0.5D;
                    double noiseValue;
                    if(mix < 0.0D) {
                        noiseValue = a;
                    } else if(mix > 1.0D) {
                        noiseValue = b;
                    } else {
                        noiseValue = a + (b - a) * mix;
                    }
                    noiseValue -= bias;
                    //Fall-off
                    if(yNoiseIndex > yNoiseSize - 4) {
                        double d44 = (float)(yNoiseIndex - (yNoiseSize - 4)) / 3.0F;
                        noiseValue = noiseValue * (1.0D - d44) + -10.0D * d44;
                    }

                    noiseArray[noiseIndex] = noiseValue;
                    ++noiseIndex;
                }
            }
        }
        return noiseArray;
    }

    public void replaceBlocksForBiome(Chunk chunk, BiomeGenBase[] biomeGenBase4) {
        ChunkPos pos = chunk.getPos();
        double scale = 8.0D / 256D;
        if(prop.fixes()){
            this.sandNoise = this.noise4a.generateNoiseOctaves(this.sandNoise, pos.x * 16, 0.0D, pos.z * 16, 16, 1, 16, scale, 1, scale);
            this.gravelNoise = this.noise4a.generateNoiseOctaves(this.gravelNoise, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, scale, 1.0D, scale);
            this.stoneNoise = this.noise4b.generateNoiseOctaves(this.stoneNoise, pos.x * 16, 0.0D, pos.z * 16, 16, 1, 16, scale * 2.0D, scale * 2.0D, scale * 2.0D);
        }else {
            this.sandNoise = this.noise4a.generateNoiseOctaves(this.sandNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale, scale, 1.0D);
            this.gravelNoise = this.noise4a.generateNoiseOctaves(this.gravelNoise, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, scale, 1.0D, scale);
            this.stoneNoise = this.noise4b.generateNoiseOctaves(this.stoneNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale * 2.0D, scale * 2.0D, scale * 2.0D);
        }
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int h1 = 0; h1 < 16; ++h1) {
            blockPos.setZ(h1);
            for(int h2 = 0; h2 < 16; ++h2) {
                blockPos.setX(h2);
                BiomeGenBase biomeGenBase10 = biomeGenBase4[h1 + h2 * 16];
                boolean sand = this.sandNoise[h1 + h2 * 16] + this.rand.nextDouble() * 0.2d > 0.0D;
                boolean gravel = this.gravelNoise[h1 + h2 * 16] + this.rand.nextDouble() * 0.2d > 3.0D;
                int stone = (int)(this.stoneNoise[h1 + h2 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int i14 = -1;
                BlockState topBlock = biomeGenBase10.topBlock;
                BlockState fillerBlock = biomeGenBase10.fillerBlock;
                for(int yBlock = getHeight()-1; yBlock >= 0; --yBlock) {
                    blockPos.setY(yBlock);
                    BlockState block = null;
                    if(yBlock <= rand.nextInt(5)) {
                        block = Blocks.BEDROCK.getDefaultState();
                    } else {
                        BlockState state = chunk.getBlockState(blockPos);
                        if(state.isAir()) {
                            i14 = -1;
                        } else if(state.isOf(Blocks.STONE)) {
                            if(i14 == -1) {
                                if(stone <= 0) {
                                    topBlock = Blocks.AIR.getDefaultState();
                                    fillerBlock = Blocks.STONE.getDefaultState();
                                } else if(yBlock >= prop.seaLevel() - 4 && yBlock <= prop.seaLevel() + 1) {
                                    topBlock = biomeGenBase10.topBlock;
                                    fillerBlock = biomeGenBase10.fillerBlock;
                                    if(gravel) {
                                        topBlock = Blocks.AIR.getDefaultState();
                                        fillerBlock = Blocks.GRAVEL.getDefaultState();
                                    }
                                    if(sand) {
                                        topBlock = Blocks.SAND.getDefaultState();
                                        fillerBlock = Blocks.SAND.getDefaultState();
                                    }
                                }

                                if(yBlock < prop.seaLevel() && topBlock == Blocks.AIR.getDefaultState()) {
                                    topBlock = Blocks.WATER.getDefaultState();
                                }

                                i14 = stone;
                                if(yBlock >= prop.seaLevel() - 1) {
                                    block = topBlock;
                                } else {
                                    block = fillerBlock;
                                }
                            } else if(i14 > 0) {
                                --i14;
                                block = fillerBlock;
                                if(i14 == 0 && fillerBlock == Blocks.SAND.getDefaultState()) {
                                    i14 = this.rand.nextInt(4);
                                    fillerBlock = Blocks.SANDSTONE.getDefaultState();
                                }
                            }
                        }
                    }
                    if(block != null) chunk.setBlockState(blockPos, block, false);
                }
            }
        }
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 64;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), Collections.nCopies(64, Blocks.STONE.getDefaultState()).toArray(new BlockState[]{}));
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    public int getHeight(){
        return prop.useFullHeight() ? 256 : 128;
    }

    public BiomeGenBase getBiome(int x, int z) {
        if(rand == null) return BiomeGenBase.iceDesert;
        return externalBiomes.getBiomeAtBlock(x, z);
    }
}
