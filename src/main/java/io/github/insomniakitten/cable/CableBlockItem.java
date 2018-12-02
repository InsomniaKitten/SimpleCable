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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

final class CableBlockItem extends ItemBlock {
    CableBlockItem(final CableBlock block) {
        super(block);
    }

    @Override
    public String getTranslationKey(final ItemStack stack) {
        final String key = this.getTranslationKey();
        switch (stack.getMetadata()) {
            case 0: return key + ".red";
            case 1: return key + ".green";
            case 2: return key + ".blue";
        }
        return key;
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
            items.add(new ItemStack(this, 1, 2));
        }
    }

    @Override
    public int getMetadata(final int damage) {
        return damage;
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }
}
