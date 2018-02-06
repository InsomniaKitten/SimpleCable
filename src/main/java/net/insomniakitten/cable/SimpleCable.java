package net.insomniakitten.cable;

import net.insomniakitten.cable.block.CableBlock;
import net.insomniakitten.cable.block.CableTile;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = SimpleCable.ID, name = SimpleCable.NAME, version = SimpleCable.VERSION)
@Mod.EventBusSubscriber(modid = SimpleCable.ID)
public final class SimpleCable {

    public static final String ID = "simplecable";
    public static final String NAME = "Simple Cable";
    public static final String VERSION = "%VERSION%";

    @GameRegistry.ObjectHolder(ID + ":cable")
    public static final Block CABLE_BLOCK = Blocks.AIR;

    @GameRegistry.ObjectHolder(ID + ":cable")
    public static final Item CABLE_ITEM = Items.AIR;

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(CableTile.class, ID + ":cable_tile");
        event.getRegistry().register(new CableBlock()
                .setRegistryName("cable")
                .setUnlocalizedName(ID + ".cable")
                .setCreativeTab(CreativeTabs.REDSTONE));
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(CABLE_BLOCK)
                .setRegistryName("cable"));
    }

}
