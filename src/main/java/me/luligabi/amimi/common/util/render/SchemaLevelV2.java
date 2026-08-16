package me.luligabi.amimi.common.util.render;

import brachy.modularui.drawable.schema.SchemaLevel;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SchemaLevelV2 extends SchemaLevel {

    @Override
    public Vector3fc getFocus() {
        return new Vector3f(0.5f);
    }
}
