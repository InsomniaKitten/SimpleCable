package net.insomniakitten.cable.client;

import net.insomniakitten.cable.SimpleCable;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = SimpleCable.ID, value = Side.CLIENT)
public final class CableColor {

    public static final IBlockColor BLOCK_COLOR = (state, world, pos, i) -> i == 0 ? 0x00A8FF : 0xFFFFFFFF;

    public static final IItemColor ITEM_COLOR = (stack, i) -> i == 0 ? 0x00A8FF : 0xFFFFFFFF;

    private CableColor() {}

    @SubscribeEvent
    public static void onItemColorHandler(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(ITEM_COLOR, SimpleCable.CABLE_ITEM);
    }

    @SubscribeEvent
    public static void onBlockColorHandler(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(BLOCK_COLOR, SimpleCable.CABLE_BLOCK);
    }

}
