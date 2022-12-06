package cyborgcabbage.cabbagebeta.gen.beta.map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class MapGenCaves extends MapGenBase {
	protected void caveSegment(int chunkX, int chunkZ, Chunk chunk, double blockX, double blockY, double blockZ) {
		this.caveSegment(chunkX, chunkZ, chunk, blockX, blockY, blockZ, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void caveSegment(int chunkX, int chunkZ, Chunk chunk, double blockX, double blockY, double blockZ, float foo, float yaw, float pitch, int iRoo, int iRan, double dWan) {
		double chunkCentreX = chunkX * 16 + 8;
		double chunkCentreZ = chunkZ * 16 + 8;
		float f21 = 0.0F;
		float f22 = 0.0F;
		Random random = new Random(this.rand.nextLong());
		if(iRan <= 0) {
			int i24 = this.range * 16 - 16;
			iRan = i24 - random.nextInt(i24 / 4);
		}

		boolean z52 = false;
		if(iRoo == -1) {
			iRoo = iRan / 2;
			z52 = true;
		}

		int i25 = random.nextInt(iRan / 2) + iRan / 4;

		for(boolean z26 = random.nextInt(6) == 0; iRoo < iRan; ++iRoo) {
			double d27 = 1.5D + Math.sin((float)iRoo * (float)Math.PI / (float)iRan) * foo * 1.0F;
			double d29 = d27 * dWan;
			float f31 = (float)Math.cos(pitch);
			float f32 = (float)Math.sin(pitch);
			blockX += Math.cos(yaw) * f31;
			blockY += f32;
			blockZ += Math.sin(yaw) * f31;
			if(z26) {
				pitch *= 0.92F;
			} else {
				pitch *= 0.7F;
			}

			pitch += f22 * 0.1F;
			yaw += f21 * 0.1F;
			f22 *= 0.9F;
			f21 *= 0.75F;
			f22 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f21 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if(!z52 && iRoo == i25 && foo > 1.0F) {
				this.caveSegment(chunkX, chunkZ, chunk, blockX, blockY, blockZ, random.nextFloat() * 0.5F + 0.5F, yaw - (float)Math.PI / 2F, pitch / 3.0F, iRoo, iRan, 1.0D);
				this.caveSegment(chunkX, chunkZ, chunk, blockX, blockY, blockZ, random.nextFloat() * 0.5F + 0.5F, yaw + (float)Math.PI / 2F, pitch / 3.0F, iRoo, iRan, 1.0D);
				return;
			}

			if(z52 || random.nextInt(4) != 0) {
				double d33 = blockX - chunkCentreX;
				double d35 = blockZ - chunkCentreZ;
				double d37 = iRan - iRoo;
				double d39 = foo + 2.0F + 16.0F;
				if(d33 * d33 + d35 * d35 - d37 * d37 > d39 * d39) {
					return;
				}

				if(blockX >= chunkCentreX - 16.0D - d27 * 2.0D && blockZ >= chunkCentreZ - 16.0D - d27 * 2.0D && blockX <= chunkCentreX + 16.0D + d27 * 2.0D && blockZ <= chunkCentreZ + 16.0D + d27 * 2.0D) {
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

					boolean hitWater = false;
					BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
					for(int xi = x1; !hitWater && xi < x2; ++xi) {
						blockPos.setX(xi);
						for(int zi = z1; !hitWater && zi < z2; ++zi) {
							blockPos.setZ(zi);
							for(int yi = y2 + 1; !hitWater && yi >= y1 - 1; --yi) {
								blockPos.setY(yi);
								if(chunk.getBlockState(new BlockPos(xi, yi, zi)).isOf(Blocks.WATER)) {
									hitWater = true;
								}

								if(yi != y1 - 1 && xi != x1 && xi != x2 - 1 && zi != z1 && zi != z2 - 1) {
									yi = y1;
								}
							}
						}
					}

					if(!hitWater) {
						for(int xi = x1; xi < x2; ++xi) {
							double d57 = ((double)(xi + chunkX * 16) + 0.5D - blockX) / d27;

							for(int zi = z1; zi < z2; ++zi) {
								double d44 = ((double)(zi + chunkZ * 16) + 0.5D - blockZ) / d27;
								BlockPos.Mutable i46 = new BlockPos.Mutable(xi, y2, zi);
								boolean z47 = false;
								if(d57 * d57 + d44 * d44 < 1.0D) {
									for(int i48 = y2 - 1; i48 >= y1; --i48) {
										double d49 = ((double)i48 + 0.5D - blockY) / d29;
										if(d49 > -0.7D && d57 * d57 + d49 * d49 + d44 * d44 < 1.0D) {
											BlockState b51 = chunk.getBlockState(i46);
											if(b51.isOf(Blocks.GRASS_BLOCK)) {
												z47 = true;
											}

											if(b51.isOf(Blocks.STONE) || b51.isOf(Blocks.DIRT) || b51.isOf(Blocks.GRASS_BLOCK)) {
												if(i48 < 10) {
													chunk.setBlockState(i46, Blocks.LAVA.getDefaultState(), false);
												} else {
													chunk.setBlockState(i46, Blocks.AIR.getDefaultState(), false);
													if(z47 && chunk.getBlockState(i46.down()).isOf(Blocks.DIRT)) {
														chunk.setBlockState(i46.down(), Blocks.GRASS_BLOCK.getDefaultState(), false);
													}
												}
											}
										}

										i46.move(Direction.DOWN);
									}
								}
							}
						}

						if(z52) {
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
		int loops = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
		if(this.rand.nextInt(15) != 0) {
			loops = 0;
		}

		for(int l = 0; l < loops; ++l) {
			double xCoord = xBlock * 16 + this.rand.nextInt(16);
			double yCoord = this.rand.nextInt(this.rand.nextInt(120) + 8);
			double zCoord = zBlock * 16 + this.rand.nextInt(16);
			int i15 = 1;
			if(this.rand.nextInt(4) == 0) {
				this.caveSegment(xChunk, zChunk, chunk, xCoord, yCoord, zCoord);
				i15 += this.rand.nextInt(4);
			}

			for(int i16 = 0; i16 < i15; ++i16) {
				float f17 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				float f18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float f19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
				this.caveSegment(xChunk, zChunk, chunk, xCoord, yCoord, zCoord, f19, f17, f18, 0, 0, 1.0D);
			}
		}

	}
}
