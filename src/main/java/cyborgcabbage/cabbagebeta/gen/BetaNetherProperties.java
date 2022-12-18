package cyborgcabbage.cabbagebeta.gen;

public record BetaNetherProperties(int generationHeight, int oceanLevel, float terrainScale, boolean ceiling, FeaturesProperty features){
    public boolean match(BetaNetherProperties other){
        if(generationHeight != other.generationHeight()) return false;
        if(oceanLevel != other.oceanLevel()) return false;
        if(terrainScale != other.terrainScale()) return false;
        if(ceiling != other.ceiling()) return false;
        if(features != other.features()) return false;
        return true;
    }

    @Override
    public BetaNetherProperties clone() {
        return new BetaNetherProperties(generationHeight, oceanLevel, terrainScale, ceiling, features);
    }
}
