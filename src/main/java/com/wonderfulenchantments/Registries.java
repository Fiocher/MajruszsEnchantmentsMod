package com.wonderfulenchantments;

import com.mlib.gamemodifiers.GameModifier;
import com.mlib.items.ItemHelper;
import com.mlib.registries.DeferredRegisterHelper;
import com.mlib.triggers.BasicTrigger;
import com.wonderfulenchantments.curses.BreakingCurse;
import com.wonderfulenchantments.curses.CorrosionCurse;
import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.IncompatibilityCurse;
import com.wonderfulenchantments.enchantments.*;
import com.wonderfulenchantments.items.DyeableHorseArmorItemReplacement;
import com.wonderfulenchantments.items.HorseArmorItemReplacement;
import com.wonderfulenchantments.items.ShieldItemReplacement;
import com.wonderfulenchantments.lootmodifiers.AddItemsDirectlyToInventory;
import com.wonderfulenchantments.lootmodifiers.Replant;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class Registries {
	private static final DeferredRegisterHelper HELPER = new DeferredRegisterHelper( WonderfulEnchantments.MOD_ID );
	private static final DeferredRegisterHelper MINECRAFT_HELPER = new DeferredRegisterHelper( "minecraft" );
	public static final List< GameModifier > GAME_MODIFIERS = new ArrayList<>();

	// Groups
	static final DeferredRegister< Enchantment > ENCHANTMENTS = HELPER.create( ForgeRegistries.Keys.ENCHANTMENTS );
	static final DeferredRegister< Item > ITEMS_TO_REPLACE = MINECRAFT_HELPER.create( ForgeRegistries.Keys.ITEMS );
	static final DeferredRegister< GlobalLootModifierSerializer< ? > > LOOT_MODIFIERS = HELPER.create( ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS );
	static final DeferredRegister< ParticleType< ? > > PARTICLE_TYPES = HELPER.create( ForgeRegistries.Keys.PARTICLE_TYPES );

	// Enchantment Categories
	public static final EnchantmentCategory SHIELD = EnchantmentCategory.create( "shield", item->item instanceof ShieldItem );
	public static final EnchantmentCategory HORSE_ARMOR = EnchantmentCategory.create( "horse_armor", item->item instanceof HorseArmorItem );
	public static final EnchantmentCategory BOW_AND_CROSSBOW = EnchantmentCategory.create( "bow_and_crossbow", item->item instanceof BowItem || item instanceof CrossbowItem );
	public static final EnchantmentCategory MELEE_WEAPON = EnchantmentCategory.create( "melee_weapon", item->item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem );
	public static final EnchantmentCategory HOE = EnchantmentCategory.create( "hoe", item->item instanceof HoeItem );
	public static final EnchantmentCategory GOLDEN = EnchantmentCategory.create( "golden", item->item instanceof DiggerItem diggerItem && diggerItem.getTier() == Tiers.GOLD || item instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.GOLD );

	// Enchantments
	public static final RegistryObject< DodgeEnchantment > DODGE = ENCHANTMENTS.register( "dodge", DodgeEnchantment.create() );
	public static final RegistryObject< DeathWishEnchantment > DEATH_WISH = ENCHANTMENTS.register( "death_wish", DeathWishEnchantment.create() );
	public static final RegistryObject< EnlightenmentEnchantment > ENLIGHTENMENT = ENCHANTMENTS.register( "enlightenment", EnlightenmentEnchantment.create() );
	public static final RegistryObject< FishingFanaticEnchantment > FISHING_FANATIC = ENCHANTMENTS.register( "fishing_fanatic", FishingFanaticEnchantment.create() );
	public static final RegistryObject< FuseCutterEnchantment > FUSE_CUTTER = ENCHANTMENTS.register( "fuse_cutter", FuseCutterEnchantment.create() );
	public static final RegistryObject< GoldFuelledEnchantment > GOLD_FUELLED = ENCHANTMENTS.register( "gold_fuelled", GoldFuelledEnchantment.create() );
	public static final RegistryObject< HunterEnchantment > HUNTER = ENCHANTMENTS.register( "hunter", HunterEnchantment.create() );
	public static final RegistryObject< HarvesterEnchantment > HARVESTER = ENCHANTMENTS.register( "harvester", HarvesterEnchantment.create() );

	// Curses
	public static final RegistryObject< BreakingCurse > BREAKING = ENCHANTMENTS.register( "breaking_curse", BreakingCurse.create() );
	public static final RegistryObject< CorrosionCurse > CORROSION = ENCHANTMENTS.register( "corrosion_curse", CorrosionCurse.create() );
	public static final RegistryObject< FatigueCurse > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", FatigueCurse.create() );
	public static final RegistryObject< IncompatibilityCurse > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", IncompatibilityCurse.create() );

	// Item Replacements
	static {
		ITEMS_TO_REPLACE.register( "shield", ShieldItemReplacement::new );
		ITEMS_TO_REPLACE.register( "leather_horse_armor", ()->new DyeableHorseArmorItemReplacement( 3, "leather" ) );
		ITEMS_TO_REPLACE.register( "iron_horse_armor", ()->new HorseArmorItemReplacement( 5, "iron" ) );
		ITEMS_TO_REPLACE.register( "golden_horse_armor", ()->new HorseArmorItemReplacement( 7, "gold" ) );
		ITEMS_TO_REPLACE.register( "diamond_horse_armor", ()->new HorseArmorItemReplacement( 11, "diamond" ) );
	}

	// Loot Modifiers
	static {
		LOOT_MODIFIERS.register( "telekinesis_enchantment", AddItemsDirectlyToInventory.Serializer::new );
		LOOT_MODIFIERS.register( "harvester_enchantment", Replant.Serializer::new );
	}

	// Particles
	public static final RegistryObject< SimpleParticleType > DODGE_PARTICLE = PARTICLE_TYPES.register( "dodge_particle", ()->new SimpleParticleType( true ) );

	// Triggers
	public static final BasicTrigger BASIC_TRIGGER = BasicTrigger.createRegisteredInstance( WonderfulEnchantments.MOD_ID );

	public static ResourceLocation getLocation( String register ) {
		return HELPER.getLocation( register );
	}

	public static String getLocationString( String register ) {
		return getLocation( register ).toString();
	}

	public static void initialize() {
		FMLJavaModLoadingContext modLoadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = modLoadingContext.getModEventBus();

		addEnchantmentTypesToItemGroups();
		HELPER.registerAll();
		MINECRAFT_HELPER.registerAll();
		modEventBus.addListener( Registries::doClientSetup );
		modEventBus.addListener( PacketHandler::registerPacket );
		DistExecutor.safeRunWhenOn( Dist.CLIENT, ()->RegistriesClient::createConfig );

		WonderfulEnchantments.CONFIG_HANDLER.register( ModLoadingContext.get() );
		WonderfulEnchantments.CONFIG_HANDLER_CLIENT.register( ModLoadingContext.get() );
	}

	private static void addEnchantmentTypesToItemGroups() {
		ItemHelper.addEnchantmentTypesToItemGroup( CreativeModeTab.TAB_COMBAT, SHIELD, BOW_AND_CROSSBOW, MELEE_WEAPON );
		ItemHelper.addEnchantmentTypesToItemGroup( CreativeModeTab.TAB_TOOLS, HOE, GOLDEN );
		ItemHelper.addEnchantmentTypeToItemGroup( CreativeModeTab.TAB_MISC, HORSE_ARMOR );
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistriesClient.setup();
	}
}
