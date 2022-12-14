package cyborgcabbage.cabbagebeta.gui;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class OptionsListWidget extends ElementListWidget<OptionsListWidget.Entry> {
    final CustomizeBetaLevelScreen parent;

    public OptionsListWidget(CustomizeBetaLevelScreen parent, MinecraftClient client, List<RetroOption> options, boolean activity) {
        super(client, parent.width, parent.height, 50, parent.height - 32, 35);
        this.parent = parent;
        for (RetroOption option : options) {
            ClickableWidget w = option.getWidget();
            w.active = activity;
            if(w instanceof TextFieldWidget t){
                t.setEditable(activity);
                if(!activity) t.setTextFieldFocused(false);
                t.setFocusUnlocked(activity);
            }
            this.addEntry(new Entry(w));
        }
    }

    public void tick() {
        for (Entry child : children()) {
            if(child.widget instanceof TextFieldWidget t){
                t.tick();
                if(child != getFocused()) {
                    t.setTextFieldFocused(false);
                }
            }
        }
    }

    @Override
    public int getRowWidth() {
        return 150;
    }

    @Environment(EnvType.CLIENT)
    public static class Entry extends ElementListWidget.Entry<OptionsListWidget.Entry> {
        private final ClickableWidget widget;

        Entry(ClickableWidget widget) {
            this.widget = widget;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            widget.x = x;
            widget.y = y+10;
            if(widget instanceof TextFieldWidget field){
                drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, field.getMessage(), x+75, y, 0xA0A0A0);
            }
            widget.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.widget);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(this.widget);
        }
    }
}
