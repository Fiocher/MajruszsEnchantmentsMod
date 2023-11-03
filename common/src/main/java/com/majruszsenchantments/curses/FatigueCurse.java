package com.majruszsenchantments.curses;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnBreakSpeedGet;
import com.mlib.contexts.OnItemEquipped;
import com.mlib.contexts.OnItemSwingDurationGet;
import com.mlib.contexts.OnItemUseTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.entity.AttributeHandler;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Random;
import com.mlib.math.Range;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class FatigueCurse extends Handler {
	static final Range< Float > MULTIPLIER = Range.of( 0.0f, 1.0f );
	final AttributeHandler attackSpeed;
	final AttributeHandler movementSpeed;
	float miningMultiplier = 0.8f;
	float attackMultiplier = 0.8f;
	float movingMultiplier = 0.95f;
	float usingMultiplier = 0.8f;
	float swingingMultiplier = 0.8f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.maxLevel( 3 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.compatibility( enchantment->!( enchantment instanceof DiggingEnchantment ) );
	}

	public FatigueCurse() {
		super( MajruszsEnchantments.FATIGUE, true );

		this.attackSpeed = new AttributeHandler( "%s_attack_speed".formatted( this.enchantment.getId() ), ()->Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
		this.movementSpeed = new AttributeHandler( "%s_movement_speed".formatted( this.enchantment.getId() ), ()->Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );

		OnBreakSpeedGet.listen( this::reduceMiningSpeed )
			.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) );

		OnItemEquipped.listen( this::reduceAttackSpeed );

		OnItemEquipped.listen( this::reduceMovementSpeed );

		OnItemUseTicked.listen( this::reduceUseSpeed )
			.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
			.addCondition( data->Random.check( 1.0f - this.getItemMultiplier( this.usingMultiplier, data.entity ) ) );

		OnItemSwingDurationGet.listen( this::increaseSwingDuration )
			.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) );

		this.config.defineCustom( "speed_multiplier_per_level", subconfig->{
			subconfig.defineFloat( "mining", ()->this.miningMultiplier, x->this.miningMultiplier = MULTIPLIER.clamp( x ) );
			subconfig.defineFloat( "attacking", ()->this.attackMultiplier, x->this.attackMultiplier = MULTIPLIER.clamp( x ) );
			subconfig.defineFloat( "moving", ()->this.movingMultiplier, x->this.movingMultiplier = MULTIPLIER.clamp( x ) );
			subconfig.defineFloat( "item_using", ()->this.usingMultiplier, x->this.usingMultiplier = MULTIPLIER.clamp( x ) );
			subconfig.defineFloat( "item_swinging", ()->this.swingingMultiplier, x->this.swingingMultiplier = MULTIPLIER.clamp( x ) );
		} );
	}

	private void reduceMiningSpeed( OnBreakSpeedGet data ) {
		data.speed *= Math.max( this.getItemMultiplier( this.miningMultiplier, data.player ), 0.01f );
	}

	private void reduceAttackSpeed( OnItemEquipped data ) {
		attackSpeed.setValue( this.getItemMultiplier( this.attackMultiplier, data.entity ) - 1.0f ).apply( data.entity );
	}

	private void reduceMovementSpeed( OnItemEquipped data ) {
		movementSpeed.setValue( this.getArmorMultiplier( this.movingMultiplier, data.entity ) - 1.0f ).apply( data.entity );
	}

	private void reduceUseSpeed( OnItemUseTicked data ) {
		data.duration += 1;
	}

	private void increaseSwingDuration( OnItemSwingDurationGet data ) {
		data.duration += data.original * ( 1.0f - this.getItemMultiplier( this.swingingMultiplier, data.entity ) );
	}

	private float getItemMultiplier( float multiplier, LivingEntity entity ) {
		return ( float )Math.pow( multiplier, EnchantmentHelper.getLevel( this.enchantment, entity.getMainHandItem() ) );
	}

	private float getArmorMultiplier( float multiplier, LivingEntity entity ) {
		return ( float )Math.pow( multiplier, EnchantmentHelper.getLevelSum( this.enchantment, entity ) );
	}
}
