package io.github.insomniakitten.cable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

final class CableBlock extends Block {
    private static final Map<EnumFacing, PropertyBool> SIDES = Stream.of(EnumFacing.VALUES)
        .collect(Maps.toImmutableEnumMap(Function.identity(), f -> PropertyBool.create(f.getName())));

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(
        0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D
    );

    private static final Map<EnumFacing, AxisAlignedBB> BOUNDING_BOXES = Stream.of(EnumFacing.VALUES)
        .collect(Maps.toImmutableEnumMap(Function.identity(), f -> {
            final Vec3d min = new Vec3d(0.375, 0.375, 0.000);
            final Vec3d max = new Vec3d(0.625, 0.625, 0.375);
            switch (f) {
                case DOWN: return new AxisAlignedBB(1 - max.x, min.z, 1 - max.y, 1 - min.x, max.z, 1 - min.y);
                case UP: return new AxisAlignedBB(min.x, 1 - max.z, min.y, max.x, 1 - min.z, max.y);
                case NORTH: return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
                case SOUTH: return new AxisAlignedBB(1 - max.x, min.y, 1 - max.z, 1 - min.x, max.y, 1 - min.z);
                case WEST: return new AxisAlignedBB(min.z, min.y, 1 - max.x, max.z, max.y, 1 - min.x);
                case EAST: return new AxisAlignedBB(1 - max.z, min.y, min.x, 1 - min.z, max.y, max.x);
                default: throw new IllegalArgumentException("Not a recognized facing: " + f);
            }
        }));

    CableBlock() {
        super(Material.CIRCUITS);
        IBlockState defaultState = this.getDefaultState();
        for (final PropertyBool property : CableBlock.SIDES.values()) {
            defaultState = defaultState.withProperty(property, true);
        }
        this.setDefaultState(defaultState);
        this.setSoundType(SoundType.METAL);
        this.setHardness(0.5F);
        this.setResistance(2.5F);
        this.setLightOpacity(0);
    }

    @Override
    @Deprecated
    public MapColor getMapColor(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        return state.getValue(CableColor.property()).mapColor();
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(CableColor.property(), CableColor.valueOf(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(CableColor.property()).ordinal();
    }

    @Override
    @Deprecated
    public IBlockState getActualState(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        IBlockState actualState = state;
        for (Entry<EnumFacing, PropertyBool> e : CableBlock.SIDES.entrySet()) {
            actualState = actualState.withProperty(e.getValue(), this.canConnectTo(state, access, pos, e.getKey()));
        }
        return actualState;
    }

    @Override
    @Deprecated
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess access, final BlockPos pos) {
        final IBlockState actualState = state.getActualState(access, pos);
        AxisAlignedBB box = CableBlock.BOUNDING_BOX;
        for (Entry<EnumFacing, PropertyBool> e : CableBlock.SIDES.entrySet()) {
            if (actualState.getValue(e.getValue())) {
                box = box.union(CableBlock.BOUNDING_BOXES.get(e.getKey()));
            }
        }
        return box;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> boxes, final Entity entity, final boolean isActualState) {
        final IBlockState actualState = isActualState ? state : state.getActualState(world, pos);
        Block.addCollisionBoxToList(pos, entityBox, boxes, CableBlock.BOUNDING_BOX);
        for (Entry<EnumFacing, PropertyBool> e : CableBlock.SIDES.entrySet()) {
            if (actualState.getValue(e.getValue())) {
                final AxisAlignedBB box = CableBlock.BOUNDING_BOXES.get(e.getKey());
                Block.addCollisionBoxToList(pos, entityBox, boxes, box);
            }
        }
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(CableColor.property()).ordinal();
    }

    @Override
    @Deprecated
    public RayTraceResult collisionRayTrace(final IBlockState state, final World world, final BlockPos pos, final Vec3d start, final Vec3d end) {
        final IBlockState actualState = state.getActualState(world, pos);
        final Set<AxisAlignedBB> boxes = Sets.newHashSet(CableBlock.BOUNDING_BOX);
        for (Entry<EnumFacing, PropertyBool> e : CableBlock.SIDES.entrySet()) {
            if (actualState.getValue(e.getValue())) {
                boxes.add(CableBlock.BOUNDING_BOXES.get(e.getKey()));
            }
        }
        final Set<RayTraceResult> results = Sets.newHashSet();
        final Vec3d min = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        final Vec3d max = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        for (final AxisAlignedBB box : boxes) {
            final RayTraceResult result = box.calculateIntercept(min, max);
            if (result != null) {
                final Vec3d vec = result.hitVec.add(pos.getX(), pos.getY(), pos.getZ());
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
    protected BlockStateContainer createBlockState() {
        final BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        for (final PropertyBool property : CableBlock.SIDES.values()) {
            builder.add(property);
        }
        return builder.add(CableColor.property()).build();
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        return this.canConnectTo(state, access, pos, side);
    }

    private boolean canConnectTo(final IBlockState state, final IBlockAccess access, final BlockPos pos, final EnumFacing side) {
        final BlockPos offset = pos.offset(side);
        final IBlockState other = access.getBlockState(offset);
        final Block block = other.getBlock();
        return this == block && state.getValue(CableColor.property()) == other.getValue(CableColor.property());
    }
}
