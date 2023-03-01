package cyborgcabbage.cabbagebeta.gui;

import net.minecraft.text.Text;

public enum EditingDimension {
    OVERWORLD("overworld"),
    NETHER("nether");

    public final String name;
    EditingDimension(String name){
        this.name = name;
    }
    public Text getTranslatableName() {
        return Text.translatable("dimension."+name);
    }

}
