package com.majruszsenchantments.particles;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.MajruszsEnchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = MajruszsEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ParticleUtil {
	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void registerParticles( RegisterParticleProvidersEvent event ) {
		event.register( Registries.DODGE_PARTICLE.get(), DodgeParticle.Factory::new );
		event.register( Registries.TELEKINESIS_PARTICLE.get(), TelekinesisParticle.Factory::new );
	}
}
