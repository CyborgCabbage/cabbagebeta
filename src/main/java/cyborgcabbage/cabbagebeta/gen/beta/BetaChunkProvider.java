package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public abstract class BetaChunkProvider {
    protected Random rand;
    long worldSeed;
    public BetaChunkProvider() {
    }
    protected void init(long seed){
        rand = new Random(seed);
        worldSeed = seed;
    }
    public abstract void fillChunk(Chunk chunk, long seed);
    public abstract void populate(StructureWorldAccess world, Chunk chunk, long seed);
    public abstract BiomeGenBase getBiome(int x, int z);
    public boolean isInitialised(){
        return rand != null;
    }
}
