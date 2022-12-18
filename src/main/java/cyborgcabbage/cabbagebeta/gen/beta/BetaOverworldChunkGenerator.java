package cyborgcabbage.cabbagebeta.gen.beta;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.BetaPreset;
import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import cyborgcabbage.cabbagebeta.gen.FeaturesProperty;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomes;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomesSampler;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenCaves;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.*;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class BetaOverworldChunkGenerator extends BetaChunkGenerator {
    private static <T> RecordCodecBuilder<BetaOverworldChunkGenerator, T> betaPropertyCodec(PrimitiveCodec<T> codec, String name, Function<BetaProperties, T> function) {
        return codec.fieldOf(name).orElse(function.apply(BetaPreset.FAITHFUL.getProperties())).forGetter(b -> function.apply(b.prop));
    }
    public static final Codec<BetaOverworldChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry),
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(generator -> generator.biomeRegistry),
            betaPropertyCodec(Codec.INT, "generation_height", BetaProperties::generationHeight),
            betaPropertyCodec(Codec.INT, "ocean_level", BetaProperties::oceanLevel),
            betaPropertyCodec(Codec.FLOAT, "factor", BetaProperties::factor),
            betaPropertyCodec(Codec.INT, "ground_level", BetaProperties::groundLevel),
            betaPropertyCodec(Codec.INT, "cave_lava_level", BetaProperties::caveLavaLevel),
            betaPropertyCodec(Codec.FLOAT, "mixing", BetaProperties::mixing),
            betaPropertyCodec(Codec.BOOL, "fixes", BetaProperties::fixes),
            betaPropertyCodec(Codec.INT, "features", b -> b.features().getId()),
            betaPropertyCodec(Codec.INT, "cave_rarity", BetaProperties::caveRarity),
            betaPropertyCodec(Codec.FLOAT, "decliff", BetaProperties::decliff),
            betaPropertyCodec(Codec.FLOAT, "terrain_scale", BetaProperties::terrainScale),
            betaPropertyCodec(Codec.FLOAT, "ore_range_scale", BetaProperties::oreRangeScale),
            betaPropertyCodec(Codec.BOOL, "extended", BetaProperties::extended),
            betaPropertyCodec(Codec.FLOAT, "biome_scale", BetaProperties::biomeScale)
    ).apply(instance, instance.stable(BetaOverworldChunkGenerator::new)));

    private NoiseGeneratorOctaves noise16a;
    private NoiseGeneratorOctaves noise16b;
    private NoiseGeneratorOctaves noise8a;
    private NoiseGeneratorOctaves noise4a;
    private NoiseGeneratorOctaves noise4b;
    private NoiseGeneratorOctaves noise10a;
    private NoiseGeneratorOctaves noise16c;
    private NoiseGeneratorOctaves treeNoise;
    private BetaBiomes terrainBiomes;
    private BetaBiomesSampler biomeSampler;
    private final BetaProperties prop;

    protected void init(long seed){
        if(rand == null) {
            rand = new Random(seed);
            worldSeed = seed;
            this.terrainBiomes = new BetaBiomes(seed, prop.biomeScale());
            this.biomeSampler = new BetaBiomesSampler(seed, prop.biomeScale());
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

    public BetaOverworldChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, int generationHeight, int oceanLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes, int features, int caveRarity, float decliff, float terrainScale, float oreRangeScale, boolean extended, float biomeScale) {
        this(structureSetRegistry, biomeRegistry, new BetaProperties(generationHeight, oceanLevel, factor, groundLevel, caveLavaLevel, mixing, fixes, FeaturesProperty.byId(features), caveRarity, decliff, terrainScale, oreRangeScale, extended, biomeScale));
    }

    public BetaOverworldChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, BetaProperties p) {
        super(structureSetRegistry, biomeRegistry, p.features(), Blocks.STONE.getDefaultState(), Fluids.WATER.getDefaultState().getBlockState());
        this.prop = p;
        this.caveGen = new MapGenCaves(p.caveLavaLevel(), getMinimumY(), getHeight(), p.caveRarity(), p.fixes());
        AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(getSeaLevel(), this.oceanFluid);
        fluidLevelSampler = (x,y,z) -> fluidLevel;
    }

    @Override
    public int getSeaLevel() {
        return prop.oceanLevel();
    }

    public BetaProperties getProperties() {
        return prop;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }


    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        if(prop.features().modern()) {
            super.generateFeatures(world, chunk, structureAccessor);
        }else{
            //BlockSand.fallInstantly = true;
            int chunkCoordX = chunk.getPos().x;
            int chunkCoordZ = chunk.getPos().z;
            int chunkX = chunkCoordX * 16;
            int chunkZ = chunkCoordZ * 16;
            WorldGeneratorContext context = new WorldGeneratorContext(world, chunkX, chunkZ);
            BiomeGenBase biome = biomeSampler.getBiomeAtBlock(chunkX + 16, chunkZ + 16);
            this.rand.setSeed(worldSeed);
            long randomNum1 = this.rand.nextLong() / 2L * 2L + 1L;
            long randomNum2 = this.rand.nextLong() / 2L * 2L + 1L;
            this.rand.setSeed((long) chunkCoordX * randomNum1 + (long) chunkCoordZ * randomNum2 ^ worldSeed);
            generateFeatureRare(context, new WorldGenLakes(Blocks.WATER.getDefaultState()), 4);
            if (this.rand.nextInt(8) == 0) {
                int x = chunkX + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(this.rand.nextInt(getHeight() - 8) + 8);
                int z = chunkZ + this.rand.nextInt(16) + 8;
                if (y < 64 || this.rand.nextInt(10) == 0) {
                    (new WorldGenLakes(Blocks.LAVA.getDefaultState())).generate(world, this.rand, x, y, z);
                }
            }
            generateFeature(context, new WorldGenDungeons(), 8, true);
            generateFeature(context, new WorldGenClay(32), 10, false);
            generateMineable(context, Blocks.DIRT.getDefaultState(), 32, getHeight(), (int) (20 * getHeightMultiplier()));
            generateMineable(context, Blocks.GRAVEL.getDefaultState(), 32, getHeight(), (int) (10 * getHeightMultiplier()));
            generateMineable(context, Blocks.COAL_ORE.getDefaultState(), 16, getHeight(), (int) (20 * getHeightMultiplier()));
            generateMineable(context, Blocks.IRON_ORE.getDefaultState(), 8, 64, 20);
            generateMineable(context, Blocks.GOLD_ORE.getDefaultState(), 8, 32, 2);
            generateMineable(context, Blocks.REDSTONE_ORE.getDefaultState(), 7, 16, 8);
            generateMineable(context, Blocks.DIAMOND_ORE.getDefaultState(), 7, 16, 1);
            generateMineableBinomial(context, Blocks.LAPIS_ORE.getDefaultState(), 6, 16, 1);

            double d11 = 0.5D;
            int extraTrees = (int) ((this.treeNoise.func_806_a((double) chunkX * d11, (double) chunkZ * d11) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
            int treeCount = 0;
            if (this.rand.nextInt(10) == 0) {
                ++treeCount;
            }
            if (biome.addExtraTrees) treeCount += extraTrees;
            treeCount += biome.treeCount;

            for (int i = 0; i < treeCount; ++i) {
                int x = chunkX + this.rand.nextInt(16) + 8;
                int z = chunkZ + this.rand.nextInt(16) + 8;
                WorldGenerator generator = biome.getRandomWorldGenForTrees(this.rand, getHeight());
                generator.func_517_a(1.0D, 1.0D, 1.0D);
                generator.generate(world, this.rand, x, world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z), z);
            }
            generateFeature(context, new WorldGenFlowers(Blocks.DANDELION.getDefaultState()), biome.dandelionCount, true);

            for (int i = 0; i < biome.shrubCount; ++i) {
                byte shrubs = 1;
                if (biome == BiomeGenBase.rainforest && this.rand.nextInt(3) != 0) {
                    shrubs = 2;
                }

                int x = chunkX + this.rand.nextInt(16) + 8;
                int y = this.rand.nextInt(getHeight());
                int z = chunkZ + this.rand.nextInt(16) + 8;

                (new WorldGenTallGrass(shrubs == 1 ? Blocks.GRASS.getDefaultState() : Blocks.FERN.getDefaultState())).generate(world, this.rand, x, y, z);
            }

            if (biome == BiomeGenBase.desert) {
                generateFeature(context, new WorldGenDeadBush(Blocks.DEAD_BUSH.getDefaultState()), 2, true);
            }
            generateFeatureRare(context, new WorldGenFlowers(Blocks.POPPY.getDefaultState()), 2);
            generateFeatureRare(context, new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState()), 4);
            generateFeatureRare(context, new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState()), 8);
            generateFeature(context, new WorldGenReed(), 10, true);
            generateFeatureRare(context, new WorldGenPumpkin(), 32);
            if (biome == BiomeGenBase.desert) {
                generateFeature(context, new WorldGenCactus(), 10, true);
            }
            generateFeature(context, new WorldGenLiquids(Blocks.WATER.getDefaultState()), 50, true, r -> r.nextInt(r.nextInt(getHeight() - 8) + 8));
            generateFeature(context, new WorldGenLiquids(Blocks.LAVA.getDefaultState()), 20, true, r -> r.nextInt(r.nextInt(r.nextInt(getHeight() - 16) + 8) + 8));

            double[] generatedTemperatures = terrainBiomes.getTemperatures(null, chunkX + 8, chunkZ + 8, 16, 16);

            for (int x = chunkX + 8; x < chunkX + 8 + 16; ++x) {
                for (int z = chunkZ + 8; z < chunkZ + 8 + 16; ++z) {
                    int relX = x - (chunkX + 8);
                    int relZ = z - (chunkZ + 8);
                    int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                    double temperature = generatedTemperatures[relX * 16 + relZ] - (double) (surfaceY - prop.oceanLevel()) / 64.0D * 0.3d / prop.terrainScale();
                    BlockPos.Mutable blockPosTop = new BlockPos.Mutable(x, surfaceY, z);
                    BlockState stateBelow = world.getBlockState(blockPosTop.down());
                    if (temperature < 0.5D && surfaceY > 0 && surfaceY < getHeight() && world.isAir(blockPosTop) && stateBelow.getMaterial().isSolid() && stateBelow.getMaterial() != Material.ICE) {
                        world.setBlockState(blockPosTop, Blocks.SNOW.getDefaultState(), Block.NOTIFY_ALL);
                        Blocks.SNOW.getDefaultState().updateNeighbors(world, blockPosTop, Block.NOTIFY_ALL);
                    }
                }
            }

            //BlockSand.fallInstantly = false;
        }
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if(prop.features().modern()){
            if (SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
                return;
            }
            HeightContext heightContext = new HeightContext(this, region);
            this.buildSurface(chunk, heightContext, noiseConfig, structures, region.getBiomeAccess(), biomeRegistry, Blender.getBlender(region));
        }else {
            ChunkPos pos = chunk.getPos();
            BiomeAccess biomeAccess = region.getBiomeAccess();
            double scale = 8.0D / 256D / prop.terrainScale();
            double[] sandNoise;
            double[] gravelNoise;
            double[] stoneNoise;
            if (prop.fixes()) {
                sandNoise = this.noise4a.generateNoiseOctaves(null, pos.x * 16, 0.0D, pos.z * 16, 16, 1, 16, scale, 1, scale);
                gravelNoise = this.noise4a.generateNoiseOctaves(null, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, scale, 1.0D, scale);
                stoneNoise = this.noise4b.generateNoiseOctaves(null, pos.x * 16, 0.0D, pos.z * 16, 16, 1, 16, scale * 2.0D, scale * 2.0D, scale * 2.0D);
            } else {
                sandNoise = this.noise4a.generateNoiseOctaves(null, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale, scale, 1.0D);
                gravelNoise = this.noise4a.generateNoiseOctaves(null, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, scale, 1.0D, scale);
                stoneNoise = this.noise4b.generateNoiseOctaves(null, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale * 2.0D, scale * 2.0D, scale * 2.0D);
            }
            BlockPos.Mutable chunkBlockPos = new BlockPos.Mutable(0, 0, 0);
            BlockPos.Mutable worldBlockPos = new BlockPos.Mutable(0, getHeight() - 1, 0);
            for (int z = 0; z < 16; ++z) {
                chunkBlockPos.setZ(z);
                worldBlockPos.setZ(z + pos.z * 16);
                for (int x = 0; x < 16; ++x) {
                    chunkBlockPos.setX(x);
                    worldBlockPos.setX(x + pos.x * 16);
                    BiomeGenBase biome = biomeSampler.getBiomeAtBlock(x + pos.x * 16, z + pos.z * 16);

                    boolean sand = sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2d > 0.0D;
                    boolean gravel = gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2d > 3.0D;
                    int stone = (int) (stoneNoise[z + x * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                    int surfaceDepth = -1;
                    BlockState topBlock = biome.topBlock;
                    BlockState fillerBlock = biome.fillerBlock;
                    for (int yBlock = getHeight() - 1; yBlock >= getMinimumY(); --yBlock) {
                        chunkBlockPos.setY(yBlock);
                        BlockState block = null;
                        if (yBlock <= rand.nextInt(5) + getMinimumY()) {
                            block = Blocks.BEDROCK.getDefaultState();
                        } else {
                            BlockState state = chunk.getBlockState(chunkBlockPos);
                            if (state.isAir()) {
                                surfaceDepth = -1;
                            } else if (state.isOf(Blocks.STONE)) {
                                if (surfaceDepth == -1) {
                                    if (stone <= 0) {
                                        topBlock = Blocks.AIR.getDefaultState();
                                        fillerBlock = Blocks.STONE.getDefaultState();
                                    } else if (yBlock >= prop.oceanLevel() - 4 && yBlock <= prop.oceanLevel() + 1) {
                                        topBlock = biome.topBlock;
                                        fillerBlock = biome.fillerBlock;
                                        if (gravel) {
                                            topBlock = Blocks.AIR.getDefaultState();
                                            fillerBlock = Blocks.GRAVEL.getDefaultState();
                                        }
                                        if (sand) {
                                            topBlock = Blocks.SAND.getDefaultState();
                                            fillerBlock = Blocks.SAND.getDefaultState();
                                        }
                                    }

                                    if (yBlock < prop.oceanLevel() && topBlock == Blocks.AIR.getDefaultState()) {
                                        topBlock = Blocks.WATER.getDefaultState();
                                    }

                                    surfaceDepth = stone;
                                    if (yBlock >= prop.oceanLevel() - 1) {
                                        block = topBlock;
                                    } else {
                                        block = fillerBlock;
                                    }
                                } else if (surfaceDepth > 0) {
                                    --surfaceDepth;
                                    block = fillerBlock;
                                    if (surfaceDepth == 0 && fillerBlock == Blocks.SAND.getDefaultState()) {
                                        surfaceDepth = this.rand.nextInt(4);
                                        fillerBlock = Blocks.SANDSTONE.getDefaultState();
                                    }
                                }
                            }
                        }
                        if (block != null) chunk.setBlockState(chunkBlockPos, block, false);
                    }
                }
            }
        }
    }



    protected ChunkGeneratorSettings getModernSettings(){
        return BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD).value();
    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return prop.extended() ? 384 : 256;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        GenerationShapeConfig generationShapeConfig = getModernSettings().generationShapeConfig().trimHeight(chunk.getHeightLimitView());
        int i = generationShapeConfig.minimumY();
        int j = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
        int k = MathHelper.floorDiv(generationShapeConfig.height(), generationShapeConfig.verticalBlockSize());
        if (k <= 0) {
            return CompletableFuture.completedFuture(chunk);
        } else {
            int l = chunk.getSectionIndex(k * generationShapeConfig.verticalBlockSize() - 1 + i);
            int m = chunk.getSectionIndex(i);
            Set<ChunkSection> set = Sets.newHashSet();

            for(int n = l; n >= m; --n) {
                ChunkSection chunkSection = chunk.getSection(n);
                chunkSection.lock();
                set.add(chunkSection);
            }

            return CompletableFuture.supplyAsync(
                            Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(blender, structureAccessor, noiseConfig, chunk, j, k)), Util.getMainWorkerExecutor()
                    )
                    .whenCompleteAsync((chunkx, throwable) -> {
                        for(ChunkSection chunkSectionx : set) {
                            chunkSectionx.unlock();
                        }
                    }, executor);
        }
    }

    private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
                chunkx -> this.createChunkNoiseSampler(chunkx, structureAccessor, blender, noiseConfig)
        );
        AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();

        Heightmap oceanFloor = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        ChunkPos pos = chunk.getPos();
        int chunkBlockX = pos.getStartX();
        int chunkBlockZ = pos.getStartZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        GenerationShapeConfig generationShapeConfig = getModernSettings().generationShapeConfig().trimHeight(chunk);
        int hSize = generationShapeConfig.horizontalBlockSize();
        int vSize = generationShapeConfig.verticalBlockSize();
        final int cellCountX = 16 / hSize;
        final int cellCountZ = 16 / hSize;
        this.rand.setSeed((long) pos.x * 341873128712L + (long) pos.z * 132897987541L);
        int horizontalNoiseSize = 4;
        int xNoiseSize = horizontalNoiseSize + 1;
        int yNoiseSize = getHeight()/8+1;
        int zNoiseSize = horizontalNoiseSize + 1;
        double[] terrainNoise = this.generateTerrainNoise(pos.x * horizontalNoiseSize, 0, pos.z * horizontalNoiseSize, xNoiseSize, yNoiseSize, zNoiseSize, pos.x*16, pos.z*16);
        cellHeight = Math.min(cellHeight, yNoiseSize - 1);
        chunkNoiseSampler.sampleStartNoise();
        if(prop.features().modern()) {
            for (int cellX = 0; cellX < cellCountX; ++cellX) {
                chunkNoiseSampler.sampleEndNoise(cellX);
                for (int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
                    ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);
                    for (int cellY = cellHeight - 1; cellY >= 0; --cellY) {
                        chunkNoiseSampler.sampleNoiseCorners(cellY, cellZ);
                        double yFrac = 0.125D;
                        double v000 = terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 1];
                        double v010 = terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 1];
                        double v100 = terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 1];
                        double v110 = terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 1];
                        double v001 = (terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 0] - v000) * yFrac;
                        double v011 = (terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 0] - v010) * yFrac;
                        double v101 = (terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 0] - v100) * yFrac;
                        double v111 = (terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 0] - v110) * yFrac;
                        v000 += v001;
                        v010 += v011;
                        v100 += v101;
                        v110 += v111;
                        for (int subY = vSize - 1; subY >= 0; --subY) {
                            int blockY = cellY * vSize + subY;
                            int sectionY = blockY & 15;
                            int sectionIndex = chunk.getSectionIndex(blockY);
                            if (chunk.getSectionIndex(chunkSection.getYOffset()) != sectionIndex) {
                                chunkSection = chunk.getSection(sectionIndex);
                            }
                            double cellDeltaY = (double) subY / (double) vSize;
                            chunkNoiseSampler.sampleNoiseY(blockY, cellDeltaY);
                            double xFrac = 0.25D;
                            double d35 = v000;
                            double d37 = v010;
                            double d39 = (v100 - v000) * xFrac;
                            double d41 = (v110 - v010) * xFrac;
                            for (int subX = 0; subX < hSize; ++subX) {
                                int blockX = chunkBlockX + cellX * hSize + subX;
                                int sectionX = blockX & 15;
                                double cellDeltaX = (double) subX / (double) hSize;
                                chunkNoiseSampler.sampleNoiseX(blockX, cellDeltaX);
                                double zFrac = 0.25D;
                                double density = d35;
                                double zNoiseStep = (d37 - d35) * zFrac;
                                for (int subZ = 0; subZ < hSize; ++subZ) {
                                    int blockZ = chunkBlockZ + cellZ * hSize + subZ;
                                    int sectionZ = blockZ & 15;
                                    double cellDeltaZ = (double) subZ / (double) hSize;
                                    chunkNoiseSampler.sampleNoiseZ(blockZ, cellDeltaZ);
                                    double sumDensity = ((BetaChunkNoiseSampler) chunkNoiseSampler).sampleBeard();
                                    sumDensity += density * 0.01f;
                                    BlockState blockState;
                                    if (sumDensity > 0.0) {
                                        blockState = this.defaultBlock;
                                    } else if (blockY < prop.oceanLevel()) {
                                        double temperature = biomeSampler.getTemperatureAtBlock(blockX, blockZ);
                                        if (temperature < 0.5D && blockY >= prop.oceanLevel() - 1) {
                                            blockState = Blocks.ICE.getDefaultState();
                                        } else {
                                            blockState = Blocks.WATER.getDefaultState();
                                        }
                                    } else {
                                        blockState = airBlock;
                                    }
                                    if (blockState != airBlock && !SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
                                        if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
                                            mutable.set(blockX, blockY, blockZ);
                                            ((ProtoChunk) chunk).addLightSource(mutable);
                                        }
                                        chunkSection.setBlockState(sectionX, sectionY, sectionZ, blockState, false);
                                        oceanFloor.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                        worldSurface.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                        if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
                                            mutable.set(blockX, blockY, blockZ);
                                            chunk.markBlockForPostProcessing(mutable);
                                        }
                                    }
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
                chunkNoiseSampler.swapBuffers();
            }

        }else{
            for (int cellX = 0; cellX < cellCountX; ++cellX) {
                for (int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
                    ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);
                    for (int cellY = cellHeight - 1; cellY >= 0; --cellY) {
                        double yFrac = 0.125D;
                        double v000 = terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 1];
                        double v010 = terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 1];
                        double v100 = terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 1];
                        double v110 = terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 1];
                        double v001 = (terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 0] - v000) * yFrac;
                        double v011 = (terrainNoise[((cellX + 0) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 0] - v010) * yFrac;
                        double v101 = (terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 0) * yNoiseSize + cellY + 0] - v100) * yFrac;
                        double v111 = (terrainNoise[((cellX + 1) * xNoiseSize + cellZ + 1) * yNoiseSize + cellY + 0] - v110) * yFrac;
                        v000 += v001;
                        v010 += v011;
                        v100 += v101;
                        v110 += v111;
                        for (int subY = vSize - 1; subY >= 0; --subY) {
                            int blockY = cellY * vSize + subY;
                            int sectionY = blockY & 15;
                            int sectionIndex = chunk.getSectionIndex(blockY);
                            if (chunk.getSectionIndex(chunkSection.getYOffset()) != sectionIndex) {
                                chunkSection = chunk.getSection(sectionIndex);
                            }
                            double xFrac = 0.25D;
                            double d35 = v000;
                            double d37 = v010;
                            double d39 = (v100 - v000) * xFrac;
                            double d41 = (v110 - v010) * xFrac;
                            for (int subX = 0; subX < hSize; ++subX) {
                                int blockX = chunkBlockX + cellX * hSize + subX;
                                int sectionX = blockX & 15;
                                double zFrac = 0.25D;
                                double density = d35;
                                double zNoiseStep = (d37 - d35) * zFrac;
                                for (int subZ = 0; subZ < hSize; ++subZ) {
                                    int blockZ = chunkBlockZ + cellZ * hSize + subZ;
                                    int sectionZ = blockZ & 15;
                                    BlockState blockState;
                                    if (density > 0.0) {
                                        blockState = this.defaultBlock;
                                    } else if (blockY < prop.oceanLevel()) {
                                        double temperature = biomeSampler.getTemperatureAtBlock(blockX, blockZ);
                                        if (temperature < 0.5D && blockY >= prop.oceanLevel() - 1) {
                                            blockState = Blocks.ICE.getDefaultState();
                                        } else {
                                            blockState = Blocks.WATER.getDefaultState();
                                        }
                                    } else {
                                        blockState = airBlock;
                                    }
                                    if (blockState != airBlock && !SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
                                        if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
                                            mutable.set(blockX, blockY, blockZ);
                                            ((ProtoChunk) chunk).addLightSource(mutable);
                                        }
                                        chunkSection.setBlockState(sectionX, sectionY, sectionZ, blockState, false);
                                        oceanFloor.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                        worldSurface.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                        if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
                                            mutable.set(blockX, blockY, blockZ);
                                            chunk.markBlockForPostProcessing(mutable);
                                        }
                                    }
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
        chunkNoiseSampler.stopInterpolation();
        chunkNoiseSampler.sampleStartNoise();
        if(prop.extended()) {
            if(prop.features().modern()) {
                for (int cellX = 0; cellX < cellCountX; ++cellX) {
                    chunkNoiseSampler.sampleEndNoise(cellX);
                    for (int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
                        ChunkSection chunkSection = chunk.getSection(chunk.getSectionIndex(-1));
                        for (int cellY = 64 / vSize - 1; cellY >= 0; --cellY) {
                            chunkNoiseSampler.sampleNoiseCorners(cellY, cellZ);
                            for (int subY = vSize - 1; subY >= 0; --subY) {
                                int blockY = (minimumCellY + cellY) * vSize + subY;
                                int sectionY = blockY & 15;
                                int sectionIndex = chunk.getSectionIndex(blockY);
                                if (chunk.getSectionIndex(chunkSection.getYOffset()) != sectionIndex) {
                                    chunkSection = chunk.getSection(sectionIndex);
                                }
                                double cellDeltaY = (double) subY / (double) vSize;
                                chunkNoiseSampler.sampleNoiseY(blockY, cellDeltaY);
                                for (int subX = 0; subX < hSize; ++subX) {
                                    int blockX = chunkBlockX + cellX * hSize + subX;
                                    int sectionX = blockX & 15;
                                    double cellDeltaX = (double) subX / (double) hSize;
                                    chunkNoiseSampler.sampleNoiseX(blockX, cellDeltaX);
                                    for (int subZ = 0; subZ < hSize; ++subZ) {
                                        int blockZ = chunkBlockZ + cellZ * hSize + subZ;
                                        int sectionZ = blockZ & 15;
                                        double cellDeltaZ = (double) subZ / (double) hSize;
                                        chunkNoiseSampler.sampleNoiseZ(blockZ, cellDeltaZ);
                                        double density = ((BetaChunkNoiseSampler) chunkNoiseSampler).sampleBeard()+1.0f;
                                        BlockState blockState;
                                        if (density > 0.0) {
                                            blockState = this.defaultBlock;
                                        } else if (blockY < prop.oceanLevel()) {
                                            double temperature = biomeSampler.getTemperatureAtBlock(blockX, blockZ);
                                            if (temperature < 0.5D && blockY >= prop.oceanLevel() - 1) {
                                                blockState = Blocks.ICE.getDefaultState();
                                            } else {
                                                blockState = Blocks.WATER.getDefaultState();
                                            }
                                        } else {
                                            blockState = airBlock;
                                        }
                                        if (blockState != airBlock && !SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
                                            if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
                                                mutable.set(blockX, blockY, blockZ);
                                                ((ProtoChunk) chunk).addLightSource(mutable);
                                            }
                                            chunkSection.setBlockState(sectionX, sectionY, sectionZ, blockState, false);
                                            oceanFloor.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                            worldSurface.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                            if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
                                                mutable.set(blockX, blockY, blockZ);
                                                chunk.markBlockForPostProcessing(mutable);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    chunkNoiseSampler.swapBuffers();
                }

            }else{
                for (int cellX = 0; cellX < cellCountX; ++cellX) {
                    for (int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
                        ChunkSection chunkSection = chunk.getSection(chunk.getSectionIndex(-1));
                        for (int cellY = 64 / vSize - 1; cellY >= 0; --cellY) {
                            for (int subY = vSize - 1; subY >= 0; --subY) {
                                int blockY = (minimumCellY + cellY) * vSize + subY;
                                int sectionY = blockY & 15;
                                int sectionIndex = chunk.getSectionIndex(blockY);
                                if (chunk.getSectionIndex(chunkSection.getYOffset()) != sectionIndex) {
                                    chunkSection = chunk.getSection(sectionIndex);
                                }
                                for (int subX = 0; subX < hSize; ++subX) {
                                    int blockX = chunkBlockX + cellX * hSize + subX;
                                    int sectionX = blockX & 15;
                                    for (int subZ = 0; subZ < hSize; ++subZ) {
                                        int blockZ = chunkBlockZ + cellZ * hSize + subZ;
                                        int sectionZ = blockZ & 15;
                                        BlockState blockState = this.defaultBlock;
                                        if (blockState != airBlock && !SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
                                            chunkSection.setBlockState(sectionX, sectionY, sectionZ, blockState, false);
                                            oceanFloor.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                            worldSurface.trackUpdate(sectionX, blockY, sectionZ, blockState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        chunkNoiseSampler.stopInterpolation();
        return chunk;
    }

    private double[] generateTerrainNoise(double xOffset, double yOffset, double zOffset, int xNoiseSize, int yNoiseSize, int zNoiseSize, int blockX, int blockZ) {
        double[] noiseArray = new double[xNoiseSize * yNoiseSize * zNoiseSize];


        double hScale = 684.412d;
        double vScale = 684.412d;
        double[] highFreq2d10 = this.noise10a.func_4109_a(null, xOffset, zOffset, xNoiseSize, zNoiseSize, 1.121D/prop.terrainScale(), 1.121D/prop.terrainScale(), 0.5D);
        double[] lowFreq2d16 = this.noise16c.func_4109_a(null, xOffset, zOffset, xNoiseSize, zNoiseSize, 200.0D/prop.terrainScale(), 200.0D/prop.terrainScale(), 0.5D);
        double[] highFreq3d8 = this.noise8a.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale / 80.0D/prop.terrainScale(), vScale / 160.0D/prop.terrainScale(), hScale / 80.0D/prop.terrainScale());
        double[] lowFreq3d16a = this.noise16a.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale/prop.terrainScale(), vScale/prop.terrainScale(), hScale/prop.terrainScale());
        double[] lowFreq3d16b = this.noise16b.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale/prop.terrainScale(), vScale/prop.terrainScale(), hScale/prop.terrainScale());
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
                double tempVal = biomeSampler.getTemperatureAtBlock(xSample + blockX,zSample + blockZ);
                double humidityVal = biomeSampler.getHumidityAtBlock(xSample + blockX,zSample + blockZ) * tempVal;
                humidityVal = 1.0D - humidityVal;
                humidityVal *= humidityVal;
                humidityVal *= humidityVal;
                humidityVal = 1.0D - humidityVal;
                double highFreqHumid = (highFreq2d10[noiseIndex2] + 256.0D) / 512.0D;
                highFreqHumid *= humidityVal;
                if(highFreqHumid > 1.0D) {
                    highFreqHumid = 1.0D;
                }

                double lowFreq2d3 = lowFreq2d16[noiseIndex2] / 8000.0D;
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
                    //double decliff = 0.3;//[0, 1.0] -> [0, 0.35]
                    if(prop.decliff()*0.35 <= 0.001){
                        highFreqHumid = 0.0D;
                    }else{
                        double temp2 = lowFreq2d3*(-1/(prop.decliff()*0.35));
                        highFreqHumid *= Math.max(0.9*(1-temp2), 0);
                    }


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
                    double bias = ((double)yNoiseIndex/prop.terrainScale() - groundLevelLocal) * prop.factor() / highFreqHumid;
                    if(bias < 0.0D) {
                        bias *= 4.0D;
                    }

                    double a = lowFreq3d16a[noiseIndex] / 512.0D;
                    double b = lowFreq3d16b[noiseIndex] / 512.0D;
                    double mix = highFreq3d8[noiseIndex] / 20.0D * prop.mixing() + 0.5D;
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
                    float fallOffStart = 4.f * getHeightMultiplier();
                    if(yNoiseIndex > yNoiseSize - fallOffStart) {
                        double d44 = (yNoiseIndex - (yNoiseSize - fallOffStart)) / (fallOffStart-1.f);
                        noiseValue = noiseValue * (1.0D - d44) + -10.0D * d44;
                    }

                    noiseArray[noiseIndex] = noiseValue;
                    ++noiseIndex;
                }
            }
        }
        return noiseArray;
    }

    public double[] generateTerrainNoiseColumn(int xOffset, int zOffset, int yNoiseSize) {
        return generateTerrainNoise(xOffset*0.25, 0, zOffset*0.25, 1, yNoiseSize, 1, xOffset, zOffset);
    }


    @Override
    public int getMinimumY() {
        return prop.extended() ? -64 : 0;
    }

    public int getHeight() {
        return prop.generationHeight();
    }

    public float getHeightMultiplier() {
        return prop.generationHeight() / 128.f;
    }

    public float getOreRangeScale() {
        return prop.oreRangeScale();
    }

    public BiomeGenBase getBiome(int x, int z) {
        if(rand == null) return BiomeGenBase.sky;
        return biomeSampler.getBiomeAtBlock(x, z);
    }

    public RegistryKey<Biome> getModernBiome(int x, int z) {
        if(rand == null) return BiomeKeys.WINDSWEPT_HILLS;
        return biomeSampler.getSmallBiomeAtBlock(x, z);
    }
}