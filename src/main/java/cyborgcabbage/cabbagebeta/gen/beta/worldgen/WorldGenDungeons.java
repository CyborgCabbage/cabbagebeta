package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Optional;
import java.util.Random;

public class WorldGenDungeons extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		byte b6 = 3;
		int i7 = random.nextInt(2) + 2;
		int i8 = random.nextInt(2) + 2;
		int i9 = 0;

		int xBlock;
		int yBlock;
		int zBlock;
		for(xBlock = x - i7 - 1; xBlock <= x + i7 + 1; ++xBlock) {
			for(yBlock = y - 1; yBlock <= y + b6 + 1; ++yBlock) {
				for(zBlock = z - i8 - 1; zBlock <= z + i8 + 1; ++zBlock) {
					var pos = new BlockPos(xBlock, yBlock, zBlock);
					Material material = world.getBlockState(pos).getMaterial();
					if(yBlock == y - 1 && !material.isSolid()) {
						return false;
					}

					if(yBlock == y + b6 + 1 && !material.isSolid()) {
						return false;
					}

					if((xBlock == x - i7 - 1 || xBlock == x + i7 + 1 || zBlock == z - i8 - 1 || zBlock == z + i8 + 1) && yBlock == y && world.isAir(pos) && world.isAir(pos.up())) {
						++i9;
					}
				}
			}
		}

		if(i9 >= 1 && i9 <= 5) {
			for(xBlock = x - i7 - 1; xBlock <= x + i7 + 1; ++xBlock) {
				for(yBlock = y + b6; yBlock >= y - 1; --yBlock) {
					for(zBlock = z - i8 - 1; zBlock <= z + i8 + 1; ++zBlock) {
						var pos = new BlockPos(xBlock, yBlock, zBlock);
						if(xBlock != x - i7 - 1 && yBlock != y - 1 && zBlock != z - i8 - 1 && xBlock != x + i7 + 1 && yBlock != y + b6 + 1 && zBlock != z + i8 + 1) {
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
						} else if(yBlock >= 0 && !world.getBlockState(pos.down()).getMaterial().isSolid()) {
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
						} else if(world.getBlockState(pos).getMaterial().isSolid()) {
							if(yBlock == y - 1 && random.nextInt(4) != 0) {
								world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
							} else {
								world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
							}
						}
					}
				}
			}

			label110:
			for(xBlock = 0; xBlock < 2; ++xBlock) {
				for(yBlock = 0; yBlock < 3; ++yBlock) {
					int xBlock2 = x + random.nextInt(i7 * 2 + 1) - i7;
					int zBlock2 = z + random.nextInt(i8 * 2 + 1) - i8;
					if(world.isAir(new BlockPos(xBlock2, y, zBlock2))) {
						int i15 = 0;
						if(world.getBlockState(new BlockPos(xBlock2 - 1, y, zBlock2)).getMaterial().isSolid()) {
							++i15;
						}

						if(world.getBlockState(new BlockPos(xBlock2 + 1, y, zBlock2)).getMaterial().isSolid()) {
							++i15;
						}

						if(world.getBlockState(new BlockPos(xBlock2, y, zBlock2 - 1)).getMaterial().isSolid()) {
							++i15;
						}

						if(world.getBlockState(new BlockPos(xBlock2, y, zBlock2 + 1)).getMaterial().isSolid()) {
							++i15;
						}

						if(i15 == 1) {
							var chestPos = new BlockPos(xBlock2, y, zBlock2);
							world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), Block.NOTIFY_LISTENERS);
							Optional<ChestBlockEntity> blockEntity = world.getBlockEntity(chestPos, BlockEntityType.CHEST);
							if(blockEntity.isEmpty()) continue label110;
							ChestBlockEntity chestBlockEntity = blockEntity.get();
							int itemIndex = 0;
							while(true) {
								if(itemIndex >= 8) {
									continue label110;
								}

								ItemStack stack = this.pickCheckLootItem(random);
								if(stack != null) {
									chestBlockEntity.setStack(random.nextInt(chestBlockEntity.size()), stack);
								}
								++itemIndex;
							}
						}
					}
				}
			}
			var spawnerPos = new BlockPos(x, y, z);
			world.setBlockState(spawnerPos, Blocks.SPAWNER.getDefaultState(), Block.NOTIFY_LISTENERS);
			Optional<MobSpawnerBlockEntity> spawner = world.getBlockEntity(spawnerPos, BlockEntityType.MOB_SPAWNER);
			spawner.ifPresent((be) -> be.getLogic().setEntityId(pickMobSpawner(random)));
			return true;
		} else {
			return false;
		}
	}

	private ItemStack pickCheckLootItem(Random random) {
		int i2 = random.nextInt(11);
		return i2 == 0 ? new ItemStack(Items.SADDLE) :
				(i2 == 1 ? new ItemStack(Items.IRON_INGOT, random.nextInt(4) + 1) :
				(i2 == 2 ? new ItemStack(Items.BREAD) :
				(i2 == 3 ? new ItemStack(Items.WHEAT, random.nextInt(4) + 1) :
				(i2 == 4 ? new ItemStack(Items.GUNPOWDER, random.nextInt(4) + 1) :
				(i2 == 5 ? new ItemStack(Items.STRING, random.nextInt(4) + 1) :
				(i2 == 6 ? new ItemStack(Items.BUCKET) :
				(i2 == 7 && random.nextInt(100) == 0 ? new ItemStack(Items.GOLDEN_APPLE) :
				(i2 == 8 && random.nextInt(2) == 0 ? new ItemStack(Items.REDSTONE, random.nextInt(4) + 1) :
				(i2 == 9 && random.nextInt(10) == 0 ? new ItemStack( random.nextInt(2) == 0 ? Items.MUSIC_DISC_13 : Items.MUSIC_DISC_CAT) :
				(i2 == 10 ? new ItemStack(Items.COCOA_BEANS) : null))))))))));
	}

	private EntityType<?> pickMobSpawner(Random random1) {
		int r = random1.nextInt(4);
		return r == 0 ? EntityType.SKELETON : r == 1 ? EntityType.ZOMBIE : r == 2 ? EntityType.ZOMBIE : EntityType.SPIDER;
	}
}
