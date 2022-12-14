package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomeProvider;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenCavesHell;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BetaNetherChunkGenerator extends ChunkGenerator implements BetaBiomeProvider {
    public static final Codec<BetaNetherChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry),
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(generator -> generator.biomeRegistry)
    ).apply(instance, instance.stable(BetaNetherChunkGenerator::new)));
    private final Registry<Biome> biomeRegistry;

    private NoiseGeneratorOctaves terrainNoiseA;
    private NoiseGeneratorOctaves terrainNoiseB;
    private NoiseGeneratorOctaves mixingNoise;
    private NoiseGeneratorOctaves field_4166_l;
    private NoiseGeneratorOctaves field_4165_m;
    public NoiseGeneratorOctaves field_4177_a;
    public NoiseGeneratorOctaves field_4176_b;
    private final MapGenBase caveGen;
    protected Random rand;
    long worldSeed;

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

    public BetaNetherChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry) {
        super(structureSetRegistry, Optional.empty(), new BetaOverworldBiomeSource(biomeRegistry, false));
        this.biomeRegistry = biomeRegistry;
        this.caveGen = new MapGenCavesHell();
        if(biomeSource instanceof BetaOverworldBiomeSource bobs){
            bobs.setGenerator(this);
        }
    }

        @Override
    public int getSeaLevel() {
        return 32;
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
        int i2 = chunk.getPos().x;
        int i3 = chunk.getPos().z;
        int blockX = i2 * 16;
        int blockZ = i3 * 16;

        for(int i = 0; i < 8; ++i) {
            int x = blockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(120) + 4;
            int z = blockZ + this.rand.nextInt(16) + 8;
            (new WorldGenHellLava(Blocks.LAVA.getDefaultState())).generate(world, this.rand, x, y, z);
        }

        int fireCount = this.rand.nextInt(this.rand.nextInt(10) + 1) + 1;
        for(int i = 0; i < fireCount; ++i) {
            int x = blockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(120) + 4;
            int z = blockZ + this.rand.nextInt(16) + 8;
            (new WorldGenFire()).generate(world, this.rand, x, y, z);
        }

        int glowStoneCount = this.rand.nextInt(this.rand.nextInt(10) + 1);
        for(int i = 0; i < glowStoneCount; ++i) {
            int x = blockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(120) + 4;
            int z = blockZ + this.rand.nextInt(16) + 8;
            (new WorldGenGlowStone1()).generate(world, this.rand, x, y, z);
        }

        for(int i = 0; i < 10; ++i) {
            int x = blockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(128);
            int z = blockZ + this.rand.nextInt(16) + 8;
            (new WorldGenGlowStone2()).generate(world, this.rand, x, y, z);
        }

        this.rand.nextInt(1);
        {
            int x = blockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(128);
            int z = blockZ + this.rand.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState())).generate(world, this.rand, x, y, z);
        }
        this.rand.nextInt(1);
        {
            int x = blockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(128);
            int z = blockZ + this.rand.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState())).generate(world, this.rand, x, y, z);
        }
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
        this.rand.setSeed((long)pos.x * 341873128712L + (long)pos.z * 132897987541L);
        this.generateTerrain(chunk);
        this.replaceBlocks(chunk);
        this.caveGen.generate(chunk, worldSeed);
        return CompletableFuture.completedFuture(chunk);
    }

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte horizontalSize = 4;
        byte lavaHeight = 32;
        int xSize = horizontalSize + 1;
        byte ySize = 17;
        int zSize = horizontalSize + 1;
        double[] field_4163_o = this.generateTerrainNoise(null, pos.x * horizontalSize, 0, pos.z * horizontalSize, xSize, ySize, zSize);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int xi = 0; xi < horizontalSize; ++xi) {
            for(int zi = 0; zi < horizontalSize; ++zi) {
                for(int yi = 0; yi < 16; ++yi) {
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
                                if(yi * 8 + i30 < lavaHeight) {
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

        double d8 = 684.412D;
        double d10 = 2053.236D;
        double[] mixingNoiseArray = this.mixingNoise.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, d8 / 80.0D, d10 / 60.0D, d8 / 80.0D);
        double[] terrainNoiseArrayA = this.terrainNoiseA.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, d8, d10, d8);
        double[] terrainNoiseArrayB = this.terrainNoiseB.generateNoiseOctaves(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, d8, d10, d8);
        int index = 0;
        double[] wave = new double[ySize];

        for(int yi = 0; yi < ySize; ++yi) {
            wave[yi] = Math.cos((double)yi * Math.PI * 6.0D / (double)ySize) * 2.0D;
            double d16 = yi;
            if(yi > ySize / 2) {
                d16 = ySize - 1 - yi;
            }

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
                        double fallOff = (float)(yi - (ySize - 4)) / 3.0F;
                        noiseValue = noiseValue * (1.0D - fallOff) + -10.0D * fallOff;
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
        double d5 = 8.0D / 256D;
        double[] soulSandNoiseArray = this.field_4166_l.generateNoiseOctaves(null, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d5, d5, 1.0D);
        double[] gravelNoiseArray = this.field_4166_l.generateNoiseOctaves(null, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, d5, 1.0D, d5);
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

                for(int yBlock = 127; yBlock >= 0; --yBlock) {
                    blockPos.setY(yBlock);
                    BlockState block = null;
                    if(yBlock >= 127 - this.rand.nextInt(5)) {
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
        return 128;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), Collections.nCopies(64, Blocks.STONE.getDefaultState()).toArray(new BlockState[]{}));
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    public int getHeight(){
        return 128;
    }

    public BiomeGenBase getBiome(int x, int z) {
        return BiomeGenBase.hell;
    }
}
