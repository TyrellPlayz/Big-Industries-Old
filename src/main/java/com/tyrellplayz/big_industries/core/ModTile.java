package com.tyrellplayz.big_industries.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.tyrellplayz.big_industries.BigIndustries;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BigIndustries.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTile {
    private static final Logger LOGGER = BigIndustries.LOGGER;

    private static final List<TileEntityType<?>> TILES = new ArrayList<>();



    private static <T extends TileEntity> TileEntityType<T> register(ResourceLocation id, Supplier<T> factoryIn, Block... validBlocks){
        Validate.notNull(id,"Tile should have a registry name");
        Validate.notNull(factoryIn,"Tile factory is null for "+id.toString());
        TileEntityType.Builder<T> builder = TileEntityType.Builder.create(factoryIn,validBlocks);
        TileEntityType<T> tileEntityType = builder.build(getDataFixer(id));
        tileEntityType.setRegistryName(id);
        TILES.add(tileEntityType);
        return tileEntityType;
    }

    private static Type<?> getDataFixer(ResourceLocation id) {
        try{
            return DataFixesManager.getDataFixer()
                    .getSchema(DataFixUtils.makeKey(BigIndustries.DATAFIXER_VERSION))
                    .getChoiceType(TypeReferences.BLOCK_ENTITY, id.toString());
        }catch (IllegalArgumentException e){
            if(SharedConstants.developmentMode) throw e;
            LOGGER.warn("No data fixer registered for block entity {}",id);
            return null;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        TILES.forEach(type -> event.getRegistry().register(type));
    }

}
