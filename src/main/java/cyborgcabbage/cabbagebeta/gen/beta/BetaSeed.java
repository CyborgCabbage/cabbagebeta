package cyborgcabbage.cabbagebeta.gen.beta;

public class BetaSeed {
    public long seed;

    public BetaSeed(long n){
        seed = n;
    }

    public BetaSeed(String string){
        seed = string.hashCode();
    }
}
