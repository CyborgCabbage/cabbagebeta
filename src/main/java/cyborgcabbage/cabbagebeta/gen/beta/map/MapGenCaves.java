package cyborgcabbage.cabbagebeta.gen.beta.map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class MapGenCaves extends MapGenBase {
	private final int lavaLevel;
	private final int heightRange;
	private final int caveRarity;
	private final boolean fixes;

	public MapGenCaves(int lavaLevel, int heightRange, int caveRarity, boolean fixes) {
		this.lavaLevel = lavaLevel;
		this.heightRange = heightRange;
		this.caveRarity = caveRarity;
		this.fixes = fixes;
	}

	protected void tunnel(int originX, int originZ, Chunk chunk, double blockX, double blockY, double blockZ) {
		this.tunnel(originX, originZ, chunk, blockX, blockY, blockZ, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void tunnel(int originX, int originZ, Chunk chunk, double blockX, double blockY, double blockZ, float radiusFactor, float yaw, float pitch, int segment, int maxSegments, double aspectRatio) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		int chunkCentreX = chunk.getPos().x * 16 + 8;
		int chunkCentreZ = chunk.getPos().z * 16 + 8;
		float yawDelta = 0.0F;
		float pitchDelta = 0.0F;
		Random random = new Random(this.rand.nextLong());
		if(maxSegments <= 0) {
			int temp = this.range * 16 - 16;
			maxSegments = temp - random.nextInt(temp / 4);
		}

		boolean isTunnelEnd = false;
		if(segment == -1) {
			segment = maxSegments / 2;
			isTunnelEnd = true;
		}

		int splittingSegment = random.nextInt(maxSegments / 2) + maxSegments / 4;

		for(boolean turn = random.nextInt(6) == 0; segment < maxSegments; ++segment) {
			double radius = 1.5D + Math.sin((float)segment * (float)Math.PI / (float)maxSegments) * radiusFactor * 1.0F;
			double verticalRadius = radius * aspectRatio;
			float cosPitch = (float)Math.cos(pitch);
			float sinPitch = (float)Math.sin(pitch);
			blockX += Math.cos(yaw) * cosPitch;
			blockY += sinPitch;
			blockZ += Math.sin(yaw) * cosPitch;
			if(turn) {
				pitch *= 0.92F;
			} else {
				pitch *= 0.7F;
			}

			pitch += pitchDelta * 0.1F;
			yaw += yawDelta * 0.1F;
			pitchDelta *= 0.9F;
			yawDelta *= 0.75F;
			pitchDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			yawDelta += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if(!isTunnelEnd && segment == splittingSegment && radiusFactor > 1.0F) {
				this.tunnel(originX, originZ, chunk, blockX, blockY, blockZ, random.nextFloat() * 0.5F + 0.5F, yaw - (float)Math.PI / 2F, pitch / 3.0F, segment, maxSegments, 1.0D);
				this.tunnel(originX, originZ, chunk, blockX, blockY, blockZ, random.nextFloat() * 0.5F + 0.5F, yaw + (float)Math.PI / 2F, pitch / 3.0F, segment, maxSegments, 1.0D);
				return;
			}

			if(isTunnelEnd || random.nextInt(4) != 0) {
				double deltaOriginX = blockX - (fixes ? originX : chunkCentreX);
				double deltaOriginZ = blockZ - (fixes ? originZ : chunkCentreZ);
				double d37 = maxSegments - segment;
				double d39 = radiusFactor + 2.0F + 16.0F;
				if(deltaOriginX * deltaOriginX + deltaOriginZ * deltaOriginZ - d37 * d37 > d39 * d39) {
					return;
				}

				if(blockX >= chunkCentreX - 16.0D - radius * 2.0D && blockZ >= chunkCentreZ - 16.0D - radius * 2.0D && blockX <= chunkCentreX + 16.0D + radius * 2.0D && blockZ <= chunkCentreZ + 16.0D + radius * 2.0D) {
					//Create bounds
					int x1 = (int)Math.floor(blockX - radius) - chunkX * 16 - 1;
					int x2 = (int)Math.floor(blockX + radius) - chunkX * 16 + 1;
					int y1 = (int)Math.floor(blockY - verticalRadius) - 1;
					int y2 = (int)Math.floor(blockY + verticalRadius) + 1;
					int z1 = (int)Math.floor(blockZ - radius) - chunkZ * 16 - 1;
					int z2 = (int)Math.floor(blockZ + radius) - chunkZ * 16 + 1;
					if(x1 < 0) {
						x1 = 0;
					}

					if(x2 > 16) {
						x2 = 16;
					}

					if(y1 < 1) {
						y1 = 1;
					}
					if(fixes){
						if(y2 > heightRange) {
							y2 = heightRange;
						}
					}else{
						if(y2 > (heightRange-8)) {
							y2 = heightRange-8;
						}
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
							double dx = ((double)(xi + chunkX * 16) + 0.5D - blockX) / radius;
							for(int zi = z1; zi < z2; ++zi) {
								double dz = ((double)(zi + chunkZ * 16) + 0.5D - blockZ) / radius;
								BlockPos.Mutable pos = new BlockPos.Mutable(xi, y2, zi);
								boolean hitGrass = false;
								if(dx * dx + dz * dz < 1.0D) {
									for(int yi = y2 - 1; yi >= y1; --yi) {
										double dy = ((double)yi + 0.5D - blockY) / verticalRadius;
										if(dy > -0.7D && dx * dx + dy * dy + dz * dz < 1.0D) {
											BlockState block = chunk.getBlockState(pos);
											if(block.isOf(Blocks.GRASS_BLOCK)) {
												hitGrass = true;
											}

											if(block.isOf(Blocks.STONE) || block.isOf(Blocks.DIRT) || block.isOf(Blocks.GRASS_BLOCK)) {
												if(yi < lavaLevel) {
													chunk.setBlockState(pos, Blocks.LAVA.getDefaultState(), false);
												} else {
													chunk.setBlockState(pos, Blocks.AIR.getDefaultState(), false);
													if(hitGrass && chunk.getBlockState(pos.down()).isOf(Blocks.DIRT)) {
														chunk.setBlockState(pos.down(), Blocks.GRASS_BLOCK.getDefaultState(), false);
													}
												}
											}
										}

										pos.move(Direction.DOWN);
									}
								}
							}
						}

						if(isTunnelEnd) {
							break;
						}
					}
				}
			}
		}
	}

	protected void generateFromChunk(Chunk chunk, int xBlock, int zBlock) {
		int loops = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
		if(this.rand.nextInt(caveRarity) != 0) {
			loops = 0;
		}

		for(int l = 0; l < loops; ++l) {
			int x = xBlock * 16 + this.rand.nextInt(16);
			int y = this.rand.nextInt(this.rand.nextInt(heightRange-8) + 8);
			int z = zBlock * 16 + this.rand.nextInt(16);
			int iters = 1;
			if(this.rand.nextInt(4) == 0) {
				this.tunnel(x, z, chunk, x, y, z);
				iters += this.rand.nextInt(4);
			}

			for(int i = 0; i < iters; ++i) {
				float yaw = this.rand.nextFloat() * (float)Math.PI * 2.0F;//[0, 2*pi]
				float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;//[-0.125, 0.125]
				float radiusFactor = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
				this.tunnel(x, z, chunk, x, y, z, radiusFactor, yaw, pitch, 0, 0, 1.0D);
			}
		}

	}
}
