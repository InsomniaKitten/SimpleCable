package net.insomniakitten.cable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

@Mod(modid = "simplecable", name = "Simple Cable", version = "%VERSION%")
public final class SimpleCable {
    @ObjectHolder("simplecable:cable")
    private static final Block BLOCK = null;

    @ObjectHolder("simplecable:cable")
    private static final Item ITEM = null;

    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    protected void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new CableBlock().setRegistryName("cable")
                .setUnlocalizedName("simplecable.cable")
                .setCreativeTab(CreativeTabs.REDSTONE)
        );
    }

    @SubscribeEvent
    protected void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(requireNotEmpty(BLOCK)).setRegistryName("cable"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    protected void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(requireNotEmpty(ITEM), 0,
                new ModelResourceLocation("simplecable:cable", "inventory")
        );

        final ModelResourceLocation mrl = new ModelResourceLocation("simplecable:cable", "normal");
        ModelLoader.setCustomStateMapper(requireNotEmpty(BLOCK), block ->
                block.getBlockState().getValidStates().stream().collect(
                        ImmutableMap.toImmutableMap(it -> it, it -> mrl)
                )
        );
    }

    private static <T extends IForgeRegistryEntry<T>> T requireNotEmpty(@Nullable T entry) {
        requireNonNull(entry, "Registry entry cannot be null!");
        requireNonNull(entry.getRegistryName(), "Registry name cannot be null!");
        if (entry == Blocks.AIR || entry == Items.AIR) {
            throw new IllegalStateException("ObjectHolder field was not populated correctly!");
        }
        return entry;
    }
}
