package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that causes shield to work like Totem of Undying. */
@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends WonderfulEnchantment {
	protected static final int DAMAGE_ON_USE = 9001;

	public ImmortalityEnchantment() {
		super( "immortality", Rarity.RARE, RegistryHandler.SHIELD, EquipmentSlots.BOTH_HANDS, "Immortality" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->20 );
	}

	/** Event on which enchantment effect is applied if it is possible. */
	@SubscribeEvent
	public static void onEntityHurt( LivingDamageEvent event ) {
		LivingEntity target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) < 1.0f ) {
			if( tryCheatDeath( target, target.getMainHandItem() ) )
				event.setCanceled( true );
			else if( tryCheatDeath( target, target.getOffhandItem() ) )
				event.setCanceled( true );
		}
	}

	/**
	 Cheating death when players is holding shield and it has this enchantment.

	 @param target    Entity which will receive full health on death.
	 @param itemStack Item stack to check.

	 @return Returns whether player successfully cheated death.
	 */
	protected static boolean tryCheatDeath( LivingEntity target, ItemStack itemStack ) {
		if( itemStack.getItem() instanceof ShieldItem && EnchantmentHelper.getItemEnchantmentLevel( Instances.IMMORTALITY, itemStack ) > 0 ) {
			target.setHealth( target.getMaxHealth() );

			spawnParticlesAndPlaySounds( target );
			itemStack.hurtAndBreak( DAMAGE_ON_USE, target, ( entity )->entity.broadcastBreakEvent( EquipmentSlot.OFFHAND ) );

			return true;
		}

		return false;
	}

	/**
	 Spawning particles and playing sound on cheating death.

	 @param livingEntity Entity where the effects will be generated.
	 */
	protected static void spawnParticlesAndPlaySounds( LivingEntity livingEntity ) {
		ServerLevel world = ( ServerLevel )livingEntity.level;
		world.sendParticles( ParticleTypes.TOTEM_OF_UNDYING, livingEntity.getX(), livingEntity.getY( 0.75 ), livingEntity.getZ(), 64,
			0.25, 0.5, 0.25, 0.5
		);
		world.playSound( null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.TOTEM_USE,
			SoundSource.AMBIENT, 1.0f, 1.0f
		);
	}
}
