package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.map.MapGenCavesHell;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

public class ChunkProviderHell extends BetaChunkProvider {
    private NoiseGeneratorOctaves field_4169_i;
    private NoiseGeneratorOctaves field_4168_j;
    private NoiseGeneratorOctaves field_4167_k;
    private NoiseGeneratorOctaves field_4166_l;
    private NoiseGeneratorOctaves field_4165_m;
    public NoiseGeneratorOctaves field_4177_a;
    public NoiseGeneratorOctaves field_4176_b;
    private double[] field_4163_o;
    private double[] field_4162_p = new double[256];
    private double[] field_4161_q = new double[256];
    private double[] field_4160_r = new double[256];
    private final MapGenBase caveGen = new MapGenCavesHell();
    double[] field_4175_c;
    double[] field_4174_d;
    double[] field_4173_e;
    double[] field_4172_f;
    double[] field_4171_g;

    public ChunkProviderHell() {

    }

    @Override
    protected void init(long seed) {
        if(rand != null) return;
        super.init(seed);
        this.field_4169_i = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_4168_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_4167_k = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_4166_l = new NoiseGeneratorOctaves(this.rand, 4);
        this.field_4165_m = new NoiseGeneratorOctaves(this.rand, 4);
        this.field_4177_a = new NoiseGeneratorOctaves(this.rand, 10);
        this.field_4176_b = new NoiseGeneratorOctaves(this.rand, 16);
    }

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte horizontalSize = 4;
        byte lavaHeight = 32;
        int xSize = horizontalSize + 1;
        byte ySize = 17;
        int zSize = horizontalSize + 1;
        this.field_4163_o = this.generateTerrainNoise(this.field_4163_o, pos.x * horizontalSize, 0, pos.z * horizontalSize, xSize, ySize, zSize);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int xi = 0; xi < horizontalSize; ++xi) {
            for(int zi = 0; zi < horizontalSize; ++zi) {
                for(int yi = 0; yi < 16; ++yi) {
                    double d12 = 0.125D;
                    double d000 = this.field_4163_o[((xi + 0) * zSize + zi + 0) * ySize + yi + 0];
                    double d010 = this.field_4163_o[((xi + 0) * zSize + zi + 1) * ySize + yi + 0];
                    double d100 = this.field_4163_o[((xi + 1) * zSize + zi + 0) * ySize + yi + 0];
                    double d110 = this.field_4163_o[((xi + 1) * zSize + zi + 1) * ySize + yi + 0];
                    double d001 = (this.field_4163_o[((xi + 0) * zSize + zi + 0) * ySize + yi + 1] - d000) * d12;
                    double d011 = (this.field_4163_o[((xi + 0) * zSize + zi + 1) * ySize + yi + 1] - d010) * d12;
                    double d101 = (this.field_4163_o[((xi + 1) * zSize + zi + 0) * ySize + yi + 1] - d100) * d12;
                    double d111 = (this.field_4163_o[((xi + 1) * zSize + zi + 1) * ySize + yi + 1] - d110) * d12;

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

    public void replaceBlocks(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte b4 = 64;
        double d5 = 8.0D / 256D;
        this.field_4162_p = this.field_4166_l.generateNoiseOctaves(this.field_4162_p, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d5, d5, 1.0D);
        this.field_4161_q = this.field_4166_l.generateNoiseOctaves(this.field_4161_q, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, d5, 1.0D, d5);
        this.field_4160_r = this.field_4165_m.generateNoiseOctaves(this.field_4160_r, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d5 * 2.0D, d5 * 2.0D, d5 * 2.0D);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int h1 = 0; h1 < 16; ++h1) {
            blockPos.setZ(h1);
            for(int h2 = 0; h2 < 16; ++h2) {
                blockPos.setX(h2);
                boolean sand = this.field_4162_p[h1 + h2 * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                boolean gravel = this.field_4161_q[h1 + h2 * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                int stone = (int)(this.field_4160_r[h1 + h2 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
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
    public void fillChunk(Chunk chunk, long seed) {
        this.worldSeed = seed;
        var pos = chunk.getPos();
        this.rand.setSeed((long)pos.x * 341873128712L + (long)pos.z * 132897987541L);
        this.generateTerrain(chunk);
        this.replaceBlocks(chunk);
        this.caveGen.generate(chunk, worldSeed);
    }

    private double[] generateTerrainNoise(double[] d1, int i2, int i3, int i4, int i5, int i6, int i7) {
        if(d1 == null) {
            d1 = new double[i5 * i6 * i7];
        }

        double d8 = 684.412D;
        double d10 = 2053.236D;
        this.field_4172_f = this.field_4177_a.generateNoiseOctaves(this.field_4172_f, i2, i3, i4, i5, 1, i7, 1.0D, 0.0D, 1.0D);
        this.field_4171_g = this.field_4176_b.generateNoiseOctaves(this.field_4171_g, i2, i3, i4, i5, 1, i7, 100.0D, 0.0D, 100.0D);
        this.field_4175_c = this.field_4167_k.generateNoiseOctaves(this.field_4175_c, i2, i3, i4, i5, i6, i7, d8 / 80.0D, d10 / 60.0D, d8 / 80.0D);
        this.field_4174_d = this.field_4169_i.generateNoiseOctaves(this.field_4174_d, i2, i3, i4, i5, i6, i7, d8, d10, d8);
        this.field_4173_e = this.field_4168_j.generateNoiseOctaves(this.field_4173_e, i2, i3, i4, i5, i6, i7, d8, d10, d8);
        int i12 = 0;
        int i13 = 0;
        double[] d14 = new double[i6];

        int i15;
        for(i15 = 0; i15 < i6; ++i15) {
            d14[i15] = Math.cos((double)i15 * Math.PI * 6.0D / (double)i6) * 2.0D;
            double d16 = i15;
            if(i15 > i6 / 2) {
                d16 = i6 - 1 - i15;
            }

            if(d16 < 4.0D) {
                d16 = 4.0D - d16;
                d14[i15] -= d16 * d16 * d16 * 10.0D;
            }
        }

        for(i15 = 0; i15 < i5; ++i15) {
            for(int i36 = 0; i36 < i7; ++i36) {
                double d17 = (this.field_4172_f[i13] + 256.0D) / 512.0D;
                if(d17 > 1.0D) {
                    d17 = 1.0D;
                }

                double d19 = 0.0D;
                double d21 = this.field_4171_g[i13] / 8000.0D;
                if(d21 < 0.0D) {
                    d21 = -d21;
                }

                d21 = d21 * 3.0D - 3.0D;
                if(d21 < 0.0D) {
                    d21 /= 2.0D;
                    if(d21 < -1.0D) {
                        d21 = -1.0D;
                    }

                    d21 /= 1.4D;
                    d21 /= 2.0D;
                    d17 = 0.0D;
                } else {
                    if(d21 > 1.0D) {
                        d21 = 1.0D;
                    }

                    d21 /= 6.0D;
                }

                d17 += 0.5D;
                d21 = d21 * (double)i6 / 16.0D;
                ++i13;

                for(int i23 = 0; i23 < i6; ++i23) {
                    double d24 = 0.0D;
                    double d26 = d14[i23];
                    double d28 = this.field_4174_d[i12] / 512.0D;
                    double d30 = this.field_4173_e[i12] / 512.0D;
                    double d32 = (this.field_4175_c[i12] / 10.0D + 1.0D) / 2.0D;
                    if(d32 < 0.0D) {
                        d24 = d28;
                    } else if(d32 > 1.0D) {
                        d24 = d30;
                    } else {
                        d24 = d28 + (d30 - d28) * d32;
                    }

                    d24 -= d26;
                    double d34;
                    if(i23 > i6 - 4) {
                        d34 = (float)(i23 - (i6 - 4)) / 3.0F;
                        d24 = d24 * (1.0D - d34) + -10.0D * d34;
                    }

                    if((double)i23 < d19) {
                        d34 = (d19 - (double)i23) / 4.0D;
                        if(d34 < 0.0D) {
                            d34 = 0.0D;
                        }

                        if(d34 > 1.0D) {
                            d34 = 1.0D;
                        }

                        d24 = d24 * (1.0D - d34) + -10.0D * d34;
                    }

                    d1[i12] = d24;
                    ++i12;
                }
            }
        }

        return d1;
    }

    @Override
    public void populate(StructureWorldAccess world, Chunk chunk, long seed) {
        int i2 = chunk.getPos().x;
        int i3 = chunk.getPos().z;
        int i4 = i2 * 16;
        int i5 = i3 * 16;

        int i6;
        int i7;
        int i8;
        int i9;
        for(i6 = 0; i6 < 8; ++i6) {
            i7 = i4 + this.rand.nextInt(16) + 8;
            i8 = this.rand.nextInt(120) + 4;
            i9 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenHellLava(Blocks.LAVA.getDefaultState())).generate(world, this.rand, i7, i8, i9);
        }

        i6 = this.rand.nextInt(this.rand.nextInt(10) + 1) + 1;

        int i10;
        for(i7 = 0; i7 < i6; ++i7) {
            i8 = i4 + this.rand.nextInt(16) + 8;
            i9 = this.rand.nextInt(120) + 4;
            i10 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenFire()).generate(world, this.rand, i8, i9, i10);
        }

        i6 = this.rand.nextInt(this.rand.nextInt(10) + 1);

        for(i7 = 0; i7 < i6; ++i7) {
            i8 = i4 + this.rand.nextInt(16) + 8;
            i9 = this.rand.nextInt(120) + 4;
            i10 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenGlowStone1()).generate(world, this.rand, i8, i9, i10);
        }

        for(i7 = 0; i7 < 10; ++i7) {
            i8 = i4 + this.rand.nextInt(16) + 8;
            i9 = this.rand.nextInt(128);
            i10 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenGlowStone2()).generate(world, this.rand, i8, i9, i10);
        }

        this.rand.nextInt(1);
        i7 = i4 + this.rand.nextInt(16) + 8;
        i8 = this.rand.nextInt(128);
        i9 = i5 + this.rand.nextInt(16) + 8;
        (new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState())).generate(world, this.rand, i7, i8, i9);

        this.rand.nextInt(1);
        i7 = i4 + this.rand.nextInt(16) + 8;
        i8 = this.rand.nextInt(128);
        i9 = i5 + this.rand.nextInt(16) + 8;
        (new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState())).generate(world, this.rand, i7, i8, i9);
    }

    @Override
    public BiomeGenBase getBiome(int x, int z) {
        return BiomeGenBase.hell;
    }
}

