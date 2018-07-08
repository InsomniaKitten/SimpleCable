package net.insomniakitten.cable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
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
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
    modid = "simplecable",
    name = "Simple Cable",
    version = "%VERSION%",
    acceptedMinecraftVersions = "[1.12,1.13)"
)
public final class SimpleCable {
    @ObjectHolder("simplecable:cable")
    private static final Block BLOCK = Blocks.AIR;

    @ObjectHolder("simplecable:cable")
    private static final Item ITEM = Items.AIR;

    private static final SimpleCable INSTANCE = new SimpleCable();

    private SimpleCable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @InstanceFactory
    public static SimpleCable getInstance() {
        return SimpleCable.INSTANCE;
    }

    @SubscribeEvent
    void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new CableBlock()
            .setRegistryName("cable")
            .setUnlocalizedName("simplecable.cable")
            .setCreativeTab(CreativeTabs.REDSTONE)
        );
    }

    @SubscribeEvent
    void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(SimpleCable.BLOCK)
            .setRegistryName("cable")
        );
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(SimpleCable.ITEM, 0,
            new ModelResourceLocation("simplecable:cable", "inventory")
        );
        ModelLoader.setCustomStateMapper(SimpleCable.BLOCK, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("simplecable:cable", "normal");
            }
        });
    }
}
