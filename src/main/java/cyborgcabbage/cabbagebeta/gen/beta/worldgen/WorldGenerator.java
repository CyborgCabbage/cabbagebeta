package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public abstract class WorldGenerator {
	public abstract boolean generate(StructureWorldAccess world, Random random, int x, int y, int z);

	public void func_517_a(double d1, double d3, double d5) {
	}

	protected void setBlockWithNotify(WorldAccess world, BlockPos pos, BlockState state){
		world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
		state.updateNeighbors(world, pos, Block.NOTIFY_LISTENERS);
	}
}
