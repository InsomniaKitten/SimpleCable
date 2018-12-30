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
