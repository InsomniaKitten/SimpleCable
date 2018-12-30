package io.github.insomniakitten.cable;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

enum CableColor implements IStringSerializable {
    RED(MapColor.RED), GREEN(MapColor.GREEN), BLUE(MapColor.BLUE);

    private static final CableColor[] VALUES = CableColor.values();

    private static final ImmutableSet<CableColor> COLORS = ImmutableSet.copyOf(CableColor.VALUES);

    private static final IProperty<CableColor> COLOR = PropertyEnum.create(
        "color", CableColor.class, CableColor.colors()
    );

    private final ResourceLocation path = new ResourceLocation(SimpleCable.ID, this.getName() + "_cable");
    private final MapColor mapColor;

    CableColor(final MapColor mapColor) {
        this.mapColor = mapColor;
    }

    static IProperty<CableColor> property() {
        return CableColor.COLOR;
    }

    static ImmutableSet<CableColor> colors() {
        return CableColor.COLORS;
    }

    static CableColor valueOf(final int ordinal) {
        return CableColor.VALUES[ordinal % CableColor.VALUES.length];
    }

    @Override
    public final String getName() {
        return this.toString();
    }

    @Override
    public final String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    final ResourceLocation path() {
        return this.path;
    }

    final MapColor mapColor() {
        return this.mapColor;
    }
}
