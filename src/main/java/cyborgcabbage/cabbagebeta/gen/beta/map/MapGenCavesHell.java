package cyborgcabbage.cabbagebeta.gen.beta.map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class MapGenCavesHell extends MapGenBase {
    protected void caveSegment(int chunkX, int chunkZ, Chunk chunk, double blockX, double blockY, double blockZ) {
        this.caveSegment(chunkX, chunkZ, chunk, blockX, blockY, blockZ, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void caveSegment(int chunkX, int chunkZ, Chunk chunk, double blockX, double blockY, double blockZ, float foo, float yaw, float pitch, int iRoo, int iRan, double dWan) {
        double d17 = chunkX * 16 + 8;
        double d19 = chunkZ * 16 + 8;
        float f21 = 0.0F;
        float f22 = 0.0F;
        Random random23 = new Random(this.rand.nextLong());
        if(iRan <= 0) {
            int i24 = this.range * 16 - 16;
            iRan = i24 - random23.nextInt(i24 / 4);
        }

        boolean z51 = false;
        if(iRoo == -1) {
            iRoo = iRan / 2;
            z51 = true;
        }

        int i25 = random23.nextInt(iRan / 2) + iRan / 4;

        for(boolean z26 = random23.nextInt(6) == 0; iRoo < iRan; ++iRoo) {
            double d27 = 1.5D + (double)(MathHelper.sin((float)iRoo * (float)Math.PI / (float)iRan) * foo * 1.0F);
            double d29 = d27 * dWan;
            float f31 = MathHelper.cos(pitch);
            float f32 = MathHelper.sin(pitch);
            blockX += MathHelper.cos(yaw) * f31;
            blockY += f32;
            blockZ += MathHelper.sin(yaw) * f31;
            if(z26) {
                pitch *= 0.92F;
            } else {
                pitch *= 0.7F;
            }

            pitch += f22 * 0.1F;
            yaw += f21 * 0.1F;
            f22 *= 0.9F;
            f21 *= 0.75F;
            f22 += (random23.nextFloat() - random23.nextFloat()) * random23.nextFloat() * 2.0F;
            f21 += (random23.nextFloat() - random23.nextFloat()) * random23.nextFloat() * 4.0F;
            if(!z51 && iRoo == i25 && foo > 1.0F) {
                this.caveSegment(chunkX, chunkZ, chunk, blockX, blockY, blockZ, random23.nextFloat() * 0.5F + 0.5F, yaw - (float)Math.PI / 2F, pitch / 3.0F, iRoo, iRan, 1.0D);
                this.caveSegment(chunkX, chunkZ, chunk, blockX, blockY, blockZ, random23.nextFloat() * 0.5F + 0.5F, yaw + (float)Math.PI / 2F, pitch / 3.0F, iRoo, iRan, 1.0D);
                return;
            }

            if(z51 || random23.nextInt(4) != 0) {
                double d33 = blockX - d17;
                double d35 = blockZ - d19;
                double d37 = iRan - iRoo;
                double d39 = foo + 2.0F + 16.0F;
                if(d33 * d33 + d35 * d35 - d37 * d37 > d39 * d39) {
                    return;
                }

                if(blockX >= d17 - 16.0D - d27 * 2.0D && blockZ >= d19 - 16.0D - d27 * 2.0D && blockX <= d17 + 16.0D + d27 * 2.0D && blockZ <= d19 + 16.0D + d27 * 2.0D) {
                    int x1 = (int)Math.floor(blockX - d27) - chunkX * 16 - 1;
                    int x2 = (int)Math.floor(blockX + d27) - chunkX * 16 + 1;
                    int y1 = (int)Math.floor(blockY - d29) - 1;
                    int y2 = (int)Math.floor(blockY + d29) + 1;
                    int z1 = (int)Math.floor(blockZ - d27) - chunkZ * 16 - 1;
                    int z2 = (int)Math.floor(blockZ + d27) - chunkZ * 16 + 1;
                    if(x1 < 0) {
                        x1 = 0;
                    }

                    if(x2 > 16) {
                        x2 = 16;
                    }

                    if(y1 < 1) {
                        y1 = 1;
                    }

                    if(y2 > 120) {
                        y2 = 120;
                    }

                    if(z1 < 0) {
                        z1 = 0;
                    }

                    if(z2 > 16) {
                        z2 = 16;
                    }

                    boolean hitLava = false;
                    BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
                    for(int xi = x1; !hitLava && xi < x2; ++xi) {
                        blockPos.setX(xi);
                        for(int zi = z1; !hitLava && zi < z2; ++zi) {
                            blockPos.setZ(zi);
                            for(int yi = y2 + 1; !hitLava && yi >= y1 - 1; --yi) {
                                blockPos.setY(yi);
                                if(chunk.getBlockState(blockPos).isOf(Blocks.LAVA)) {
                                    hitLava = true;
                                }
                                if(yi != y1 - 1 && xi != x1 && xi != x2 - 1 && zi != z1 && zi != z2 - 1) {
                                    yi = y1;
                                }
                            }
                        }
                    }

                    if(!hitLava) {
                        for(int xi = x1; xi < x2; ++xi) {
                            blockPos.setX(xi);
                            double d56 = ((double)(xi + chunkX * 16) + 0.5D - blockX) / d27;
                            for(int zi = z1; zi < z2; ++zi) {
                                blockPos.setZ(zi);
                                double d44 = ((double)(zi + chunkZ * 16) + 0.5D - blockZ) / d27;
                                for(int yi = y2 - 1; yi >= y1; --yi) {
                                    blockPos.setY(yi);
                                    double d48 = ((double)yi + 0.5D - blockY) / d29;
                                    if(d48 > -0.7D && d56 * d56 + d48 * d48 + d44 * d44 < 1.0D) {
                                        BlockState block = chunk.getBlockState(blockPos);
                                        if(block.isOf(Blocks.NETHERRACK) || block.isOf(Blocks.DIRT) || block.isOf(Blocks.GRASS_BLOCK)) {
                                            chunk.setBlockState(blockPos, Blocks.AIR.getDefaultState(), false);
                                        }
                                    }
                                }
                            }
                        }

                        if(z51) {
                            break;
                        }
                    }
                }
            }
        }

    }

    protected void generateFromChunk(Chunk chunk, int xBlock, int zBlock) {
        int xChunk = chunk.getPos().x;
        int zChunk = chunk.getPos().z;
        int loops = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
        if(this.rand.nextInt(5) != 0) {
            loops = 0;
        }

        for(int l = 0; l < loops; ++l) {
            double xCoord = (double)(xBlock * 16 + this.rand.nextInt(16));
            double yCoord = (double)this.rand.nextInt(128);
            double zCoord = (double)(zBlock * 16 + this.rand.nextInt(16));
            int i15 = 1;
            if(this.rand.nextInt(4) == 0) {
                this.caveSegment(xChunk, zChunk, chunk, xCoord, yCoord, zCoord);
                i15 += this.rand.nextInt(4);
            }

            for(int i16 = 0; i16 < i15; ++i16) {
                float f17 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float f18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                this.caveSegment(xChunk, zChunk, chunk, xCoord, yCoord, zCoord, f19 * 2.0F, f17, f18, 0, 0, 0.5D);
            }
        }

    }
}

