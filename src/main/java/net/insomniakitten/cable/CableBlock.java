package net.insomniakitten.cable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class CableBlock extends Block {
    private static final Map<EnumFacing, PropertyBool> SIDE_PROPERTIES = Arrays.stream(EnumFacing.VALUES)
            .collect(Maps.toImmutableEnumMap(Function.identity(), it -> PropertyBool.create(it.getName())));

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D);

    private static final Map<EnumFacing, AxisAlignedBB> BOUNDING_BOXES = Arrays.stream(EnumFacing.VALUES)
            .collect(Maps.toImmutableEnumMap(Function.identity(), it -> {
                final Vec3d min = new Vec3d(0.375, 0.375, 0.000);
                final Vec3d max = new Vec3d(0.625, 0.625, 0.375);
                switch (it) {
                    case DOWN:  return new AxisAlignedBB(1 - max.x, min.z, 1 - max.y, 1 - min.x, max.z, 1 - min.y);
                    case UP:    return new AxisAlignedBB(min.x, 1 - max.z, min.y, max.x, 1 - min.z, max.y);
                    case NORTH: return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
                    case SOUTH: return new AxisAlignedBB(1 - max.x, min.y, 1 - max.z, 1 - min.x, max.y, 1 - min.z);
                    case WEST:  return new AxisAlignedBB(min.z, min.y, 1 - max.x, max.z, max.y, 1 - min.x);
                    default:    return new AxisAlignedBB(1 - max.z, min.y, min.x, 1 - min.z, max.y, max.x);
                }
            }));

    protected CableBlock() {
        super(Material.CLOTH);
        IBlockState state = getDefaultState();
        for (PropertyBool it : SIDE_PROPERTIES.values()) {
            state = state.withProperty(it, true);
        }
        setDefaultState(state);
        setSoundType(SoundType.CLOTH);
        setHardness(0.5F);
        setResistance(2.5F);
        setLightOpacity(0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        return access.getBlockState(pos.offset(side)).getBlock() == this;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos) {
        for (EnumFacing side : EnumFacing.VALUES) {
            final Block block = access.getBlockState(pos.offset(side)).getBlock();
            state = state.withProperty(SIDE_PROPERTIES.get(side), block == this);
        }
        return state;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, BlockPos pos) {
        final IBlockState actualState = state.getActualState(world, pos);
        AxisAlignedBB box = BOUNDING_BOX;
        for (EnumFacing side : EnumFacing.VALUES) {
            if (actualState.getValue(SIDE_PROPERTIES.get(side))) {
                box = box.union(BOUNDING_BOXES.get(side));
            }
        }
        return box.offset(pos);
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> boxes, Entity entity, boolean isActualState) {
        final IBlockState actualState = isActualState ? state : state.getActualState(world, pos);
        addCollisionBoxToList(pos, entityBox, boxes, BOUNDING_BOX);
        for (EnumFacing side : EnumFacing.VALUES) {
            if (actualState.getValue(SIDE_PROPERTIES.get(side))) {
                addCollisionBoxToList(pos, entityBox, boxes, BOUNDING_BOXES.get(side));
            }
        }
    }

    @Override
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, Vec3d start, Vec3d end) {
        final IBlockState actualState = state.getActualState(world, pos);
        final List<AxisAlignedBB> boxes = Lists.newArrayList(BOUNDING_BOX);

        final List<RayTraceResult> results = new ArrayList<>();
        final Vec3d a = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        final Vec3d b = end.subtract(pos.getX(), pos.getY(), pos.getZ());

        for (EnumFacing side : EnumFacing.VALUES) {
            if (actualState.getValue(SIDE_PROPERTIES.get(side))) {
                boxes.add(BOUNDING_BOXES.get(side));
            }
        }

        for (AxisAlignedBB box : boxes) {
            RayTraceResult result = box.calculateIntercept(a, b);
            if (result != null) {
                Vec3d vec = result.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ());
                results.add(new RayTraceResult(vec, result.sideHit, pos));
            }
        }

        RayTraceResult ret = null;
        double sqrDis = 0.0D;

        for (RayTraceResult result : results) {
            double newSqrDis = result.hitVec.squareDistanceTo(end);

            if (newSqrDis > sqrDis) {
                ret = result;
                sqrDis = newSqrDis;
            }
        }

        return ret;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        final Builder builder = new Builder(this);

        for (PropertyBool it : SIDE_PROPERTIES.values()) {
            builder.add(it);
        }

        return builder.build();
    }
}
