package net.insomniakitten.cable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public final class CableBlock extends Block {
    private static final Map<EnumFacing, PropertyBool> SIDE_PROP_MAP = Stream.of(EnumFacing.VALUES)
        .collect(Maps.toImmutableEnumMap(Function.identity(), side -> PropertyBool.create(side.getName())));

    private static final AxisAlignedBB AABB_NONE = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D);

    private static final Map<EnumFacing, AxisAlignedBB> SIDE_AABB_MAP = Stream.of(EnumFacing.VALUES)
        .collect(Maps.toImmutableEnumMap(Function.identity(), side -> {
            final Vec3d min = new Vec3d(0.375, 0.375, 0.000);
            final Vec3d max = new Vec3d(0.625, 0.625, 0.375);
            switch (side) {
                case DOWN: return new AxisAlignedBB(1 - max.x, min.z, 1 - max.y, 1 - min.x, max.z, 1 - min.y);
                case UP: return new AxisAlignedBB(min.x, 1 - max.z, min.y, max.x, 1 - min.z, max.y);
                case NORTH: return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
                case SOUTH: return new AxisAlignedBB(1 - max.x, min.y, 1 - max.z, 1 - min.x, max.y, 1 - min.z);
                case WEST: return new AxisAlignedBB(min.z, min.y, 1 - max.x, max.z, max.y, 1 - min.x);
                default: return new AxisAlignedBB(1 - max.z, min.y, min.x, 1 - min.z, max.y, max.x);
            }
        }));

    protected CableBlock() {
        super(Material.CLOTH);
        IBlockState state = this.getDefaultState();
        for (final PropertyBool property : CableBlock.SIDE_PROP_MAP.values()) {
            state = state.withProperty(property, true);
        }
        this.setDefaultState(state);
        this.setSoundType(SoundType.CLOTH);
        this.setHardness(0.5F);
        this.setResistance(2.5F);
        this.setLightOpacity(0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos) {
        for (final EnumFacing side : EnumFacing.VALUES) {
            state = state.withProperty(CableBlock.SIDE_PROP_MAP.get(side),
                access.getBlockState(pos.offset(side)).getBlock() == this
            );
        }
        return state;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> boxes, Entity entity, boolean isActualState) {
        final IBlockState actualState = isActualState ? state : state.getActualState(world, pos);
        Block.addCollisionBoxToList(pos, entityBox, boxes, CableBlock.AABB_NONE);
        for (final EnumFacing side : EnumFacing.VALUES) {
            if (actualState.getValue(CableBlock.SIDE_PROP_MAP.get(side))) {
                final AxisAlignedBB aabb = CableBlock.SIDE_AABB_MAP.get(side);
                Block.addCollisionBoxToList(pos, entityBox, boxes, aabb);
            }
        }
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        final IBlockState actualState = state.getActualState(world, pos);
        AxisAlignedBB aabb = CableBlock.AABB_NONE;
        for (final EnumFacing side : EnumFacing.VALUES) {
            if (actualState.getValue(CableBlock.SIDE_PROP_MAP.get(side))) {
                aabb = aabb.union(CableBlock.SIDE_AABB_MAP.get(side));
            }
        }
        return aabb.offset(pos);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        final IBlockState actualState = state.getActualState(world, pos);
        final Set<AxisAlignedBB> aabbSet = Sets.newHashSet(CableBlock.AABB_NONE);
        for (final EnumFacing side : EnumFacing.VALUES) {
            if (actualState.getValue(CableBlock.SIDE_PROP_MAP.get(side))) {
                aabbSet.add(CableBlock.SIDE_AABB_MAP.get(side));
            }
        }
        final Set<RayTraceResult> results = Sets.newHashSet();
        final Vec3d min = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        final Vec3d max = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        for (final AxisAlignedBB aabb : aabbSet) {
            final RayTraceResult result = aabb.calculateIntercept(min, max);

            if (result != null) {
                final Vec3d vec = result.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ());

                results.add(new RayTraceResult(vec, result.sideHit, pos));
            }
        }
        RayTraceResult ret = null;
        double sqrDis = 0.0D;
        for (final RayTraceResult result : results) {
            final double newSqrDis = result.hitVec.squareDistanceTo(end);
            if (newSqrDis > sqrDis) {
                ret = result;
                sqrDis = newSqrDis;
            }
        }
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        final Collection<PropertyBool> properties = CableBlock.SIDE_PROP_MAP.values();
        return new BlockStateContainer(this, properties.toArray(new PropertyBool[0]));
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        return access.getBlockState(pos.offset(side)).getBlock() == this;
    }
}
