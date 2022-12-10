package cyborgcabbage.cabbagebeta.gen.beta.map;

import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class MapGenBase {
	protected int range = 8;
	protected final int caveRange;
	protected Random rand = new Random();

	MapGenBase(int caveRange){
		this.caveRange = caveRange;
	}

	public void generate(Chunk chunk, long worldSeed) {
		var p = chunk.getPos();
		int r = this.caveRange;
		this.rand.setSeed(worldSeed);
		long rNum1 = this.rand.nextLong() / 2L * 2L + 1L;
		long rNum2 = this.rand.nextLong() / 2L * 2L + 1L;

		for(int x = p.x - r; x <= p.x + r; ++x) {
			for(int z = p.z - r; z <= p.z + r; ++z) {
				this.rand.setSeed((long)x * rNum1 + (long)z * rNum2 ^ worldSeed);
				this.generateFromChunk(chunk, x, z);
			}
		}

	}

	protected void generateFromChunk(Chunk chunk, int x, int z) {
	}
}
