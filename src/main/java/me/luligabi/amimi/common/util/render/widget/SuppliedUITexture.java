package me.luligabi.amimi.common.util.render.widget;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.drawable.UITexture;
import brachy.modularui.screen.viewport.GuiContext;
import brachy.modularui.theme.WidgetTheme;

import java.util.function.Supplier;

public class SuppliedUITexture implements IDrawable {

    protected final Supplier<UITexture> supplier;

    public SuppliedUITexture(final Supplier<UITexture> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        supplier.get().draw(context, x, y, width, height, widgetTheme);
    }
}
