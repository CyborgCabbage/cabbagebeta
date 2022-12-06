package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenCaves;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class ChunkProviderGenerate extends BetaChunkProvider{
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
    final protected MapGenBase caveGen = new MapGenCaves();
    private BetaBiomes terrainBiomes;
    private BetaBiomesSampler externalBiomes;

    //record BiomeSample(BiomeGenBase biome, double temp, double humid){};

    public ChunkProviderGenerate() {
        //Calculate Average temperature and humidity for each biome
        /*ArrayList<BiomeSample> samples = new ArrayList<>();
        int k = 797;
        for (int x = 0; x < 1000; x++) {
            for (int z = 0; z < 1000; z++) {
                samples.add(new BiomeSample(
                        externalBiomes.getBiomeAtBlock(x*k,z*k),
                        externalBiomes.getTemperatureAtBlock(x*k,z*k),
                        externalBiomes.getHumidityAtBlock(x*k,z*k)
                ));
            }
        }
        Map<BiomeGenBase, List<BiomeSample>> byBiome = samples.stream().collect(Collectors.groupingBy(BiomeSample::biome));
        byBiome.forEach((biome, list) -> {
            OptionalDouble averageTemp = list.stream().mapToDouble(BiomeSample::temp).average();
            OptionalDouble averageHumid = list.stream().mapToDouble(BiomeSample::humid).average();
            System.out.println(biome.biomeName+":"+String.format("%.2f",averageTemp.orElse(0.9))+":"+String.format("%.2f",averageHumid.orElse(0.5)));
        });*/
    }

    protected void init(long seed) {
        if(rand != null) return;
        super.init(seed);
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

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte horizontalNoiseSize = 4;
        byte seaLevel = 64;
        int xNoiseSize = horizontalNoiseSize + 1;
        byte yNoiseSize = 17;
        int zNoiseSIze = horizontalNoiseSize + 1;
        this.terrainNoiseValues = this.generateTerrainNoise(this.terrainNoiseValues, pos.x * horizontalNoiseSize, 0, pos.z * horizontalNoiseSize, xNoiseSize, yNoiseSize, zNoiseSIze);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int xNoiseIndex = 0; xNoiseIndex < horizontalNoiseSize; ++xNoiseIndex) {
            for(int zNoiseIndex = 0; zNoiseIndex < horizontalNoiseSize; ++zNoiseIndex) {
                for(int yNoiseIndex = 0; yNoiseIndex < 16; ++yNoiseIndex) {
                    double yFrac = 0.125D;
                    double v000 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double v010 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double v100 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double v110 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double v001 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - v000) * yFrac;
                    double v011 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - v010) * yFrac;
                    double v101 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - v100) * yFrac;
                    double v111 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - v110) * yFrac;

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
                                if(yNoiseIndex * 8 + ySub < seaLevel) {
                                    if(d53 < 0.5D && yNoiseIndex * 8 + ySub >= seaLevel - 1) {
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

    public void replaceBlocksForBiome(Chunk chunk, BiomeGenBase[] biomeGenBase4) {
        ChunkPos pos = chunk.getPos();
        byte b5 = 64;
        double scale = 8.0D / 256D;
        this.sandNoise = this.noise4a.generateNoiseOctaves(this.sandNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale, scale, 1.0D);
        this.gravelNoise = this.noise4a.generateNoiseOctaves(this.gravelNoise, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, scale, 1.0D, scale);
        this.stoneNoise = this.noise4b.generateNoiseOctaves(this.stoneNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale * 2.0D, scale * 2.0D, scale * 2.0D);
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
                for(int yBlock = 127; yBlock >= 0; --yBlock) {
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
                                } else if(yBlock >= b5 - 4 && yBlock <= b5 + 1) {
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

                                if(yBlock < b5 && topBlock == Blocks.AIR.getDefaultState()) {
                                    topBlock = Blocks.WATER.getDefaultState();
                                }

                                i14 = stone;
                                if(yBlock >= b5 - 1) {
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
    public void fillChunk(Chunk chunk, long seed) {
        init(seed);
        var pos = chunk.getPos();
        this.rand.setSeed((long) pos.x * 341873128712L + (long) pos.z * 132897987541L);
        this.terrainBiomes.biomes = terrainBiomes.generateBiomes(this.terrainBiomes.biomes, pos.x * 16, pos.z * 16, 16, 16);
        this.generateTerrain(chunk);
        this.replaceBlocksForBiome(chunk, this.terrainBiomes.biomes);
        this.caveGen.generate(chunk, worldSeed);
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
            int xSample = xNoiseIndex * samplePeriod + samplePeriod / 2;
            for(int zNoiseIndex = 0; zNoiseIndex < zNoiseSize; ++zNoiseIndex) {
                int zSample = zNoiseIndex * samplePeriod + samplePeriod / 2;
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
                lowFreq2d3 = lowFreq2d3 * (double)yNoiseSize / 16.0D;
                double d31 = (double)yNoiseSize / 2.0D + lowFreq2d3 * 4.0D;
                ++noiseIndex2;

                for(int yNoiseIndex = 0; yNoiseIndex < yNoiseSize; ++yNoiseIndex) {
                    double bias = ((double)yNoiseIndex - d31) * 12.0D / highFreqHumid;
                    if(bias < 0.0D) {
                        bias *= 4.0D;
                    }

                    double a = this.lowFreq3d16a[noiseIndex] / 512.0D;
                    double b = this.lowFreq3d16b[noiseIndex] / 512.0D;
                    double mix = this.highFreq3d8[noiseIndex] / 20.0D + 0.5D;
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
    @Override
    public void populate(StructureWorldAccess world, Chunk chunk, long seed) {
        init(seed);
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
            int i14 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            int i15 = chunkZ + this.rand.nextInt(16) + 8;
            if(i14 < 64 || this.rand.nextInt(10) == 0) {
                (new WorldGenLakes(Blocks.LAVA.getDefaultState())).generate(world, this.rand, i13, i14, i15);
            }
        }
        generateFeature(context, new WorldGenDungeons(), 8, true);
        generateFeature(context, new WorldGenClay(32), 10, false);
        generateMineable(context, Blocks.DIRT.getDefaultState(), 32, 128, 20);
        generateMineable(context, Blocks.GRAVEL.getDefaultState(), 32, 128, 10);
        generateMineable(context, Blocks.COAL_ORE.getDefaultState(), 16, 128, 20);
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
            WorldGenerator generator = biome.getRandomWorldGenForTrees(this.rand);
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
            int i20 = this.rand.nextInt(128);
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
        generateFeature(context, new WorldGenLiquids(Blocks.WATER.getDefaultState()), 50, true, r -> r.nextInt(r.nextInt(120) + 8));
        generateFeature(context, new WorldGenLiquids(Blocks.LAVA.getDefaultState()), 20, true, r -> r.nextInt(r.nextInt(r.nextInt(112) + 8) + 8));

        this.generatedTemperatures = terrainBiomes.getTemperatures(this.generatedTemperatures, chunkX + 8, chunkZ + 8, 16, 16);

        for(int x = chunkX + 8; x < chunkX + 8 + 16; ++x) {
            for(int z = chunkZ + 8; z < chunkZ + 8 + 16; ++z) {
                int relX = x - (chunkX + 8);
                int relZ = z - (chunkZ + 8);
                int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                double d23 = this.generatedTemperatures[relX * 16 + relZ] - (double)(topY - 64) / 64.0D * 0.3d;
                if(d23 < 0.5D && topY > 0 && topY < 128 && world.isAir(new BlockPos(x, topY, z)) && world.getBlockState(new BlockPos(x, topY - 1, z)).getMaterial().isSolid() && world.getBlockState(new BlockPos(x, topY - 1, z)).getMaterial() != Material.ICE) {
                    world.setBlockState(new BlockPos(x, topY, z), Blocks.SNOW.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        //BlockSand.fallInstantly = false;
    }

    record WorldGeneratorContext(StructureWorldAccess world, int x, int z){}

    private void generateFeature(WorldGeneratorContext context, WorldGenerator generator, int count, boolean offset) {
        generateFeature(context, generator, count, offset, (r) -> r.nextInt(128));
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
            int y = this.rand.nextInt(128);
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
    public BiomeGenBase getBiome(int x, int z) {
        return externalBiomes.getBiomeAtBlock(x, z);
    }
}
