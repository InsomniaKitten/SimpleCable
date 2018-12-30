package io.github.insomniakitten.cable;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@EventBusSubscriber
@Mod(modid = SimpleCable.ID, useMetadata = true, acceptedMinecraftVersions = "[1.12,1.13)")
public final class SimpleCable {
    public static final String ID = "simplecable";
    public static final String CABLE = SimpleCable.ID + ":cable";

    private static final SimpleCable INSTANCE = new SimpleCable();

    @Nullable
    @ObjectHolder(SimpleCable.CABLE)
    private static CableBlock cableBlock;

    @Nullable
    @ObjectHolder(SimpleCable.CABLE)
    private static CableBlockItem cableBlockItem;

    private SimpleCable() {}

    @InstanceFactory
    public static SimpleCable instance() {
        return SimpleCable.INSTANCE;
    }

    public static CableBlock block() {
        @Nullable final CableBlock block = SimpleCable.cableBlock;
        Preconditions.checkState(block != null, "ObjectHolder value not present");
        return block;
    }

    public static CableBlockItem item() {
        @Nullable final CableBlockItem item = SimpleCable.cableBlockItem;
        Preconditions.checkState(item != null, "ObjectHolder value not present");
        return item;
    }

    @SubscribeEvent
    static void onRegisterBlockEvent(final RegistryEvent.Register<Block> event) {
        final Block block = new CableBlock();
        block.setRegistryName(SimpleCable.CABLE);
        block.setTranslationKey(SimpleCable.ID + ".cable");
        block.setCreativeTab(CreativeTabs.REDSTONE);
        event.getRegistry().register(block);
    }

    @SubscribeEvent
    static void onRegisterItemEvent(final RegistryEvent.Register<Item> event) {
        final Item item = new CableBlockItem(SimpleCable.block());
        item.setRegistryName(SimpleCable.CABLE);
        event.getRegistry().register(item);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    static void onModelRegistryEvent(final ModelRegistryEvent event) {
        final Item item = SimpleCable.item();
        for (final CableColor color : CableColor.colors()) {
            final ModelResourceLocation model = new ModelResourceLocation(color.path(), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, color.ordinal(), model);
        }
        ModelLoader.setCustomStateMapper(SimpleCable.block(), new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(final IBlockState state) {
                return new ModelResourceLocation(state.getValue(CableColor.property()).path(), "normal");
            }
        });
    }

    @Override
    public String toString() {
        return "SimpleCable";
    }
}
