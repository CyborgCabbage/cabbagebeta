package cyborgcabbage.cabbagebeta.gen.beta;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Collections;
import java.util.List;
import java.util.Random;
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
            betaPropertyCodec(Codec.BOOL, "ceiling", BetaNetherProperties::ceiling)
    ).apply(instance, instance.stable(BetaNetherChunkGenerator::new)));
    private final Registry<Biome> biomeRegistry;

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

    public BetaNetherChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, int generationHeight, int oceanLevel, float terrainScale, boolean ceiling) {
        this(structureSetRegistry, biomeRegistry, new BetaNetherProperties(generationHeight, oceanLevel, terrainScale, ceiling));
    }

    public BetaNetherChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, BetaNetherProperties netherProperties) {
        super(structureSetRegistry, new BetaOverworldBiomeSource(biomeRegistry, FeaturesProperty.BETA));
        this.biomeRegistry = biomeRegistry;
        this.caveGen = new MapGenCavesHell();
        if(biomeSource instanceof BetaOverworldBiomeSource bobs){
            bobs.setGenerator(this);
        }
        this.prop = netherProperties;
    }

    @Override
    public int getSeaLevel() {
        return prop.oceanLevel();
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        WorldGeneratorContext context = new WorldGeneratorContext(world, blockX, blockZ);
        generateFeature(context, new WorldGenHellLava(Blocks.LAVA.getDefaultState()), 8, true, r -> r.nextInt(getHeight()-8)+4);
        int fireCount = this.rand.nextInt(this.rand.nextInt(10) + 1) + 1;
        generateFeature(context, new WorldGenFire(), fireCount, true, r -> r.nextInt(getHeight()-8)+4);
        int glowStoneCount = this.rand.nextInt(this.rand.nextInt(10) + 1);
        generateFeature(context, new WorldGenGlowStone1(), glowStoneCount, true, r -> r.nextInt(getHeight()-8)+4);
        generateFeature(context, new WorldGenGlowStone2(), 10, true);
        generateFeatureRare(context, new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState()), 1);
        generateFeatureRare(context, new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState()), 1);
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
        this.rand.setSeed((long)pos.x * 341873128712L + (long)pos.z * 132897987541L);
        this.generateTerrain(chunk);
        this.replaceBlocks(chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte horizontalSize = 4;
        int xSize = horizontalSize + 1;
        int ySize = getHeight()/8+1;
        int zSize = horizontalSize + 1;
        double[] field_4163_o = this.generateTerrainNoise(null, pos.x * horizontalSize, 0, pos.z * horizontalSize, xSize, ySize, zSize);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int xi = 0; xi < horizontalSize; ++xi) {
            for(int zi = 0; zi < horizontalSize; ++zi) {
                for(int yi = 0; yi < ySize-1; ++yi) {
                    double d12 = 0.125D;
                    double d000 = field_4163_o[((xi + 0) * zSize + zi + 0) * ySize + yi + 0];
                    double d010 = field_4163_o[((xi + 0) * zSize + zi + 1) * ySize + yi + 0];
                    double d100 = field_4163_o[((xi + 1) * zSize + zi + 0) * ySize + yi + 0];
                    double d110 = field_4163_o[((xi + 1) * zSize + zi + 1) * ySize + yi + 0];
                    double d001 = (field_4163_o[((xi + 0) * zSize + zi + 0) * ySize + yi + 1] - d000) * d12;
                    double d011 = (field_4163_o[((xi + 0) * zSize + zi + 1) * ySize + yi + 1] - d010) * d12;
                    double d101 = (field_4163_o[((xi + 1) * zSize + zi + 0) * ySize + yi + 1] - d100) * d12;
                    double d111 = (field_4163_o[((xi + 1) * zSize + zi + 1) * ySize + yi + 1] - d110) * d12;

                    for(int i30 = 0; i30 < 8; ++i30) {
                        blockPos.setY(yi * 8 + i30);
                        double d31 = 0.25D;
                        double d33 = d000;
                        double d35 = d010;
                        double d37 = (d100 - d000) * d31;
                        double d39 = (d110 - d010) * d31;

                        for(int i41 = 0; i41 < 4; ++i41) {
                            blockPos.setX(i41 + xi * 4);
                            double d44 = 0.25D;
                            double d46 = d33;
                            double d48 = (d35 - d33) * d44;

                            for(int i50 = 0; i50 < 4; ++i50) {
                                blockPos.setZ(zi * 4 + i50);
                                BlockState blockState = Blocks.AIR.getDefaultState();
                                if(yi * 8 + i30 < prop.oceanLevel()) {
                                    blockState = Blocks.LAVA.getDefaultState();
                                }

                                if(d46 > 0.0D) {
                                    blockState = Blocks.NETHERRACK.getDefaultState();
                                }

                                chunk.setBlockState(blockPos, blockState, false);
                                d46 += d48;
                            }

                            d33 += d37;
                            d35 += d39;
                        }

                        d000 += d001;
                        d010 += d011;
                        d100 += d101;
                        d110 += d111;
                    }
                }
            }
        }

    }

    private double[] generateTerrainNoise(double[] output, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize) {
        if(output == null) {
            output = new double[xSize * ySize * zSize];
        }

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

                    output[index] = noiseValue;
                    ++index;
                }
            }
        }

        return output;
    }

    public void replaceBlocks(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte b4 = 64;
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
                                } else if(yBlock >= b4 - 4 && yBlock <= b4 + 1) {
                                    topBlock = Blocks.NETHERRACK.getDefaultState();
                                    fillerBlock = Blocks.NETHERRACK.getDefaultState();
                                    if(gravel) {
                                        topBlock = Blocks.GRAVEL.getDefaultState();
                                        fillerBlock = Blocks.NETHERRACK.getDefaultState();
                                    }

                                    if(sand) {
                                        topBlock = Blocks.SOUL_SAND.getDefaultState();
                                        fillerBlock = Blocks.SOUL_SAND.getDefaultState();
                                    }
                                }

                                if(yBlock < b4 && topBlock.isAir()) {
                                    topBlock = Blocks.LAVA.getDefaultState();
                                }

                                i12 = stone;
                                if(yBlock >= b4 - 1) {
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

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return getHeight() / 2;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), Collections.nCopies(64, Blocks.STONE.getDefaultState()).toArray(new BlockState[]{}));
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    public int getHeight(){
        return prop.generationHeight();
    }

    @Override
    public float getHeightMultiplier() {
        return prop.generationHeight() / 128.f;
    }

    public BiomeGenBase getBiome(int x, int z) {
        return BiomeGenBase.hell;
    }

    public RegistryKey<Biome> getSmallBiome(int x, int z) {
        return BiomeKeys.NETHER_WASTES;
    }

    public BetaNetherProperties getProperties() {
        return prop;
    }
}
