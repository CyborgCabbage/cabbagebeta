package cyborgcabbage.cabbagebeta.gen.beta;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.gen.BetaNetherPreset;
import cyborgcabbage.cabbagebeta.gen.BetaNetherProperties;
import cyborgcabbage.cabbagebeta.gen.FeaturesProperty;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenCavesHell;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.*;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class BetaNetherChunkGenerator extends BetaChunkGenerator {
    private static <T> RecordCodecBuilder<BetaNetherChunkGenerator, T> betaPropertyCodec(PrimitiveCodec<T> codec, String name, Function<BetaNetherProperties, T> function) {
        return codec.fieldOf(name).orElse(function.apply(BetaNetherPreset.FAITHFUL.getProperties())).forGetter(b -> function.apply(b.prop));
    }
    public static final Codec<BetaNetherChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry),
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(generator -> generator.biomeRegistry),
            betaPropertyCodec(Codec.INT, "generation_height", BetaNetherProperties::generationHeight),
            betaPropertyCodec(Codec.INT, "ocean_level", BetaNetherProperties::oceanLevel),
            betaPropertyCodec(Codec.FLOAT, "terrain_scale", BetaNetherProperties::terrainScale),
            betaPropertyCodec(Codec.BOOL, "ceiling", BetaNetherProperties::ceiling),
            betaPropertyCodec(Codec.INT, "features", b -> b.features().getId())
    ).apply(instance, instance.stable(BetaNetherChunkGenerator::new)));

    private NoiseGeneratorOctaves terrainNoiseA;
    private NoiseGeneratorOctaves terrainNoiseB;
    private NoiseGeneratorOctaves mixingNoise;
    private NoiseGeneratorOctaves field_4166_l;
    private NoiseGeneratorOctaves field_4165_m;
    public NoiseGeneratorOctaves field_4177_a;
    public NoiseGeneratorOctaves field_4176_b;
    private final BetaNetherProperties prop;

    protected void init(long seed){
        if(rand == null) {
            rand = new Random(seed);
            worldSeed = seed;
            this.terrainNoiseA = new NoiseGeneratorOctaves(this.rand, 16);
            this.terrainNoiseB = new NoiseGeneratorOctaves(this.rand, 16);
            this.mixingNoise = new NoiseGeneratorOctaves(this.rand, 8);
            this.field_4166_l = new NoiseGeneratorOctaves(this.rand, 4);
            this.field_4165_m = new NoiseGeneratorOctaves(this.rand, 4);
            this.field_4177_a = new NoiseGeneratorOctaves(this.rand, 10);
            this.field_4176_b = new NoiseGeneratorOctaves(this.rand, 16);
        }
    }

    public BetaNetherChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, int generationHeight, int oceanLevel, float terrainScale, boolean ceiling, int features) {
        this(structureSetRegistry, biomeRegistry, new BetaNetherProperties(generationHeight, oceanLevel, terrainScale, ceiling, FeaturesProperty.byId(features)));
    }

    public BetaNetherChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, BetaNetherProperties netherProperties) {
        super(structureSetRegistry, biomeRegistry, netherProperties.features(), Blocks.NETHERRACK.getDefaultState(), Fluids.LAVA.getDefaultState().getBlockState());
        this.caveGen = new MapGenCavesHell();
        this.prop = netherProperties;
        AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(getSeaLevel(), this.oceanFluid);
        fluidLevelSampler = (x,y,z) -> fluidLevel;
    }

    @Override
    public int getSeaLevel() {
        return prop.oceanLevel();
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        if(prop.features().modern()) {
            super.generateFeatures(world, chunk, structureAccessor);
        }else {
            int chunkX = chunk.getPos().x;
            int chunkZ = chunk.getPos().z;
            int blockX = chunkX * 16;
            int blockZ = chunkZ * 16;
            WorldGeneratorContext context = new WorldGeneratorContext(world, blockX, blockZ);
            generateFeature(context, new WorldGenHellLava(Blocks.LAVA.getDefaultState()), 8, true, r -> r.nextInt(getHeight() - 8) + 4);
            int fireCount = this.rand.nextInt(this.rand.nextInt(10) + 1) + 1;
            generateFeature(context, new WorldGenFire(), fireCount, true, r -> r.nextInt(getHeight() - 8) + 4);
            int glowStoneCount = this.rand.nextInt(this.rand.nextInt(10) + 1);
            generateFeature(context, new WorldGenGlowStone1(), glowStoneCount, true, r -> r.nextInt(getHeight() - 8) + 4);
            generateFeature(context, new WorldGenGlowStone2(), 10, true);
            generateFeatureRare(context, new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState()), 1);
            generateFeatureRare(context, new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState()), 1);
        }
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if(prop.features().modern()) {
            if (SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
                return;
            }
            HeightContext heightContext = new HeightContext(this, region);
            this.buildSurface(chunk, heightContext, noiseConfig, structures, region.getBiomeAccess(), biomeRegistry, Blender.getBlender(region));
        }else{
            ChunkPos pos = chunk.getPos();
            byte oceanLevel = 64;
            double d5 = 8.0D / 256D / prop.terrainScale();
            double d9 = 1.0 / prop.terrainScale();
            double[] soulSandNoiseArray = this.field_4166_l.generateNoiseOctaves(null, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d5, d5, d9);
            double[] gravelNoiseArray = this.field_4166_l.generateNoiseOctaves(null, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, d5, d9, d5);
            double[] field_4160_r = this.field_4165_m.generateNoiseOctaves(null, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d5 * 2.0D, d5 * 2.0D, d5 * 2.0D);
            BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
            for(int h1 = 0; h1 < 16; ++h1) {
                blockPos.setZ(h1);
                for(int h2 = 0; h2 < 16; ++h2) {
                    blockPos.setX(h2);
                    boolean sand = soulSandNoiseArray[h1 + h2 * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                    boolean gravel = gravelNoiseArray[h1 + h2 * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                    int stone = (int)(field_4160_r[h1 + h2 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                    int i12 = -1;
                    BlockState topBlock = Blocks.NETHERRACK.getDefaultState();
                    BlockState fillerBlock = Blocks.NETHERRACK.getDefaultState();

                    for(int yBlock = getHeight() - 1; yBlock >= 0; --yBlock) {
                        blockPos.setY(yBlock);
                        BlockState block = null;
                        if(yBlock >= (getHeight()-1) - this.rand.nextInt(5) && prop.ceiling()) {
                            block = Blocks.BEDROCK.getDefaultState();
                        } else if(yBlock <= this.rand.nextInt(5)) {
                            block = Blocks.BEDROCK.getDefaultState();
                        } else {
                            BlockState state = chunk.getBlockState(blockPos);
                            if(state.isAir()) {
                                i12 = -1;
                            } else if(state.isOf(Blocks.NETHERRACK)) {
                                if(i12 == -1) {
                                    if(stone <= 0) {
                                        topBlock = Blocks.AIR.getDefaultState();
                                        fillerBlock = Blocks.NETHERRACK.getDefaultState();
                                    } else if(yBlock >= oceanLevel - 4 && yBlock <= oceanLevel + 1) {
                                        if(sand) {
                                            topBlock = Blocks.SOUL_SAND.getDefaultState();
                                            fillerBlock = Blocks.SOUL_SAND.getDefaultState();
                                        } else if(gravel) {
                                            topBlock = Blocks.GRAVEL.getDefaultState();
                                            fillerBlock = Blocks.NETHERRACK.getDefaultState();
                                        } else {
                                            topBlock = Blocks.NETHERRACK.getDefaultState();
                                            fillerBlock = Blocks.NETHERRACK.getDefaultState();
                                        }
                                    }

                                    if(yBlock < oceanLevel && topBlock.isAir()) {
                                        topBlock = Blocks.LAVA.getDefaultState();
                                    }

                                    i12 = stone;
                                    if(yBlock >= oceanLevel - 1) {
                                        block = topBlock;
                                    } else {
                                        block = fillerBlock;
                                    }
                                } else if(i12 > 0) {
                                    --i12;
                                    block = fillerBlock;
                                }
                            }
                        }
                        if(block != null) chunk.setBlockState(blockPos, block, false);
                    }
                }
            }
        }
    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        ChunkGeneratorSettings settings = getModernSettings();

        GenerationShapeConfig generationShapeConfig = settings.generationShapeConfig().trimHeight(chunk.getHeightLimitView());
        int i = generationShapeConfig.minimumY();
        int j = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
        int k = MathHelper.floorDiv(Math.max(generationShapeConfig.height(), prop.generationHeight()), generationShapeConfig.verticalBlockSize());
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
                                        blockState = Blocks.LAVA.getDefaultState();
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
                                        blockState = Blocks.LAVA.getDefaultState();
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
        return chunk;
    }

    private double[] generateTerrainNoise(double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, int blockX, int blockZ) {
        double[] noiseArray = new double[xSize * ySize * zSize];

        double d8 = 684.412D / prop.terrainScale();
        double d10 = 2053.236D / prop.terrainScale();
        double[] mixingNoiseArray = this.mixingNoise.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, d8 / 80.0D, d10 / 60.0D, d8 / 80.0D);
        double[] terrainNoiseArrayA = this.terrainNoiseA.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, d8, d10, d8);
        double[] terrainNoiseArrayB = this.terrainNoiseB.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, d8, d10, d8);
        int index = 0;
        double[] wave = new double[ySize];

        for(int yi = 0; yi < ySize; ++yi) {
            wave[yi] = Math.cos((double)yi * Math.PI * 6.0D / (double)ySize) * 2.0D;
            double d16 = yi;
            if(prop.ceiling()){
                if(yi > ySize / 2) {
                    d16 = ySize - 1 - yi;
                }
            }
            d16 *= ySize / 17.0;
            if(d16 < 4.0D) {
                d16 = 4.0D - d16;
                wave[yi] -= d16 * d16 * d16 * 10.0D;
            }
        }

        for(int xi = 0; xi < xSize; ++xi) {
            for(int zi = 0; zi < zSize; ++zi) {
                for(int yi = 0; yi < ySize; ++yi) {
                    double bias = wave[yi];
                    double a = terrainNoiseArrayA[index] / 512.0D;
                    double b = terrainNoiseArrayB[index] / 512.0D;
                    double mixing = (mixingNoiseArray[index] / 10.0D + 1.0D) / 2.0D;
                    double noiseValue;
                    if(mixing < 0.0D) {
                        noiseValue = a;
                    } else if(mixing > 1.0D) {
                        noiseValue = b;
                    } else {
                        noiseValue = a + (b - a) * mixing;
                    }

                    noiseValue -= bias;
                    if(yi > ySize - 4) {
                        double d44 = (float)(yi - (ySize - 4)) / 3.0F;
                        noiseValue = noiseValue * (1.0D - d44) + -10.0D * d44;
                    }

                    noiseArray[index] = noiseValue;
                    ++index;
                }
            }
        }

        return noiseArray;
    }

    protected ChunkGeneratorSettings getModernSettings(){
        return BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.NETHER).value();
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    public int getHeight(){
        return prop.generationHeight();
    }

    @Override
    public float getHeightMultiplier() {
        return prop.generationHeight() / 128.f;
    }

    @Override
    public double[] generateTerrainNoiseColumn(int xOffset, int zOffset, int ySize) {
        return generateTerrainNoise(xOffset*0.25, 0, zOffset*0.25, 1, ySize, 1, xOffset, zOffset);
    }

    public BiomeGenBase getBiome(int x, int z) {
        return BiomeGenBase.hell;
    }

    public RegistryKey<Biome> getModernBiome(int x, int z) {
        return BiomeKeys.NETHER_WASTES;
    }

    public BetaNetherProperties getProperties() {
        return prop;
    }
}
