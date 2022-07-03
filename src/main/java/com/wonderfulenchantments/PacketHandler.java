package com.wonderfulenchantments;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	public static void registerPacket( final FMLCommonSetupEvent event ) {
		CHANNEL = NetworkRegistry.newSimpleChannel( new ResourceLocation( "wonderfulenchantments", "main" ), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals );
		// CHANNEL.registerMessage( 0, MultiplierMessage.class, MultiplierMessage::encode, MultiplierMessage::new, MultiplierMessage::handle );
		// CHANNEL.registerMessage( 1, VelocityMessage.class, VelocityMessage::encode, VelocityMessage::new, VelocityMessage::handle );
	}
}
