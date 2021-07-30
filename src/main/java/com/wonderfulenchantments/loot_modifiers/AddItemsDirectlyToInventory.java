package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.mlib.loot_modifiers.LootHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Functionality of Telekinesis enchantment. */
public class AddItemsDirectlyToInventory extends LootModifier {
	private final String TELEKINESIS_TIME_TAG = "TelekinesisLastTimeTag";
	private final String TELEKINESIS_POSITION_TAG = "TelekinesisLastPositionTag";

	public AddItemsDirectlyToInventory( LootItemCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = LootHelper.getParameter( context, LootContextParams.TOOL );
		Vec3 position = LootHelper.getParameter( context, LootContextParams.ORIGIN );
		Entity entity = LootHelper.getParameter( context, LootContextParams.THIS_ENTITY );
		if( tool == null || position == null || !( entity instanceof Player ) )
			return generatedLoot;

		Player player = ( Player )entity;
		if( isSameTimeAsPreviousTelekinesisTick( player ) && isSamePosition( player, position ) ) {
			generatedLoot.clear();
			return generatedLoot;
		}
		updateLastTelekinesisTime( player );
		updateLastBlockPosition( player, position );

		int harvesterLevel = EnchantmentHelper.getItemEnchantmentLevel( Instances.HARVESTER, player.getMainHandItem() );
		Item seedItem = getSeedItem( entity.level, LootHelper.getParameter( context, LootContextParams.ORIGIN ), LootHelper.getParameter( context, LootContextParams.BLOCK_STATE ) );
		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			if( harvesterLevel > 0 && itemStack.getItem() == seedItem ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				boolean didGivingItemSucceeded = player.getInventory().add( itemStack );

				output.add( new ItemStack( seedItem, ( !didGivingItemSucceeded ? itemStack.getCount() : 0 ) + 1 ) );
			} else if( !player.getInventory().add( itemStack ) ) {
				output.add( itemStack );
			}
		}

		return output;
	}

	@Nullable
	private Item getSeedItem( Level world, Vec3 position, BlockState blockState ) {
		if( blockState == null || position == null )
			return null;

		if( !( blockState.getBlock() instanceof CropBlock ) )
			return null;

		CropBlock crops = ( CropBlock )blockState.getBlock();
		ItemStack seeds = crops.getCloneItemStack( world, new BlockPos( position ), blockState );
		return seeds.getItem();
	}

	private void updateLastTelekinesisTime( Player player ) {
		Level world = player.level;
		CompoundTag data = player.getPersistentData();
		data.putLong( TELEKINESIS_TIME_TAG, world.getDayTime() );
	}

	private boolean isSameTimeAsPreviousTelekinesisTick( Player player ) {
		Level world = player.level;
		CompoundTag data = player.getPersistentData();

		return data.getLong( TELEKINESIS_TIME_TAG ) == world.getDayTime();
	}

	private void updateLastBlockPosition( Player player, Vec3 position ) {
		CompoundTag data = player.getPersistentData();
		data.putString( TELEKINESIS_POSITION_TAG, position.toString() );
	}

	private boolean isSamePosition( Player player, Vec3 position ) {
		CompoundTag data = player.getPersistentData();

		return data.getString( TELEKINESIS_POSITION_TAG )
			.equals( position.toString() );
	}

	public static class Serializer extends GlobalLootModifierSerializer< AddItemsDirectlyToInventory > {
		@Override
		public AddItemsDirectlyToInventory read( ResourceLocation name, JsonObject object, LootItemCondition[] conditions ) {
			return new AddItemsDirectlyToInventory( conditions );
		}

		@Override
		public JsonObject write( AddItemsDirectlyToInventory instance ) {
			return null;
		}
	}
}
