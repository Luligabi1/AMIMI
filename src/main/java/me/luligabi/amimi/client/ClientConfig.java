package me.luligabi.amimi.client;

import me.luligabi.amimi.common.util.AMIMIConfigBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final ClientConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        var builder = new AMIMIConfigBuilder();
        INSTANCE = new ClientConfig(builder);
        SPEC = builder.build();
    }

    public final ModConfigSpec.BooleanValue uniqueHatchColors;
    public final ModConfigSpec.BooleanValue enableOpenGuidePageButton;

    private ClientConfig(final AMIMIConfigBuilder builder) {
        builder.pushSection("rendering", "Rendering");
        this.uniqueHatchColors = builder.start("uniqueHatchColors",
                        "Render Unique Hatch Colors",
                        "Whether when a hatch position only has one type allowed (i.e. only fluid I/O), an unique color should be used to highlight it. Otherwise, uses the default green.")
                .define("uniqueHatchColors", true);
        builder.popSection();

        builder.pushSection("misc", "Miscellaneous");
        this.enableOpenGuidePageButton = builder.start("enableOpenGuidePageButton",
                "Enable open guide page button",
                "Whether users can open a multiblock's related GuideME page, if available. Note that users don't need to have the corresponding book in their inventory and can open other guide pages.")
                .define("enableOpenGuidePageButton", true);
        builder.popSection();
    }
}