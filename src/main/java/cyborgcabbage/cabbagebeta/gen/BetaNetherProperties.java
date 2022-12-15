package cyborgcabbage.cabbagebeta.gen;

public record BetaNetherProperties(int generationHeight, int oceanLevel, float terrainScale, boolean ceiling){
    public boolean match(BetaNetherProperties other){
        if(generationHeight != other.generationHeight()) return false;
        if(oceanLevel != other.oceanLevel) return false;
        if(terrainScale != other.terrainScale) return false;
        if(ceiling != other.ceiling()) return false;
        return true;
    }

    @Override
    public BetaNetherProperties clone() {
        return new BetaNetherProperties(generationHeight, oceanLevel, terrainScale, ceiling);
    }
}
