/*
 * Copyright (C) 2018 InsomniaKitten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
