package me.FatherTime.RiftCraft;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.awt.Desktop;

import me.FatherTime.RiftCraft.RiftCraftP2PRequest.RCTravelType;

public class RiftCraft extends JavaPlugin
{	
	//Riftcraft configuration variables
	public static boolean ShowRiftInLog = true;
	public static int ExportInterval = 15;
	public static boolean ShowDBInfoInLog = false;
	public static int RiftDelay = 3;
	public static int PortalLife = 30;
	public static int MaxLocations = 10;
	public static int RiftResourceID = 348;
	public static int RiftConsumption = 1;
	public static int MarkResourceID = 348;
	public static int MarkConsumption = 1;
	public static int PortalResourceID = 348;
	public static int PortalConsumption = 3;
	public static int DestroyMarkResourceID = 348;
	public static int DestroyMarkFailConsumption = 1;
	public static int DestroyMarkSucceedConsumption = 2;

	//RiftCraft Serialization/Deserialization
	public static Map<String, RiftBook> RiftCraftDB = new HashMap<String, RiftBook>();
	public static Logger log = Logger.getLogger("Minecraft");
	public RCDatabaseHandler DatabaseHandler = new RCDatabaseHandler();
	
	//RiftCraft EventListeners
	private final RiftCraftBlockListener BlockListener = new RiftCraftBlockListener();
	private final RiftCraftPlayerListener PlayerListener = new RiftCraftPlayerListener();
	
	//RiftCraft Portal variables
	public static Map<Block, RiftCraftPortal> ActivePortals = new HashMap<Block, RiftCraftPortal>();
	
	public void onEnable()
	{
		PluginDescriptionFile file = this.getDescription();
		String version = file.getVersion();
		log.info("RiftCraft: " + version + " enabled!" );
		PluginManager manager = getServer().getPluginManager();
        manager.registerEvent(Event.Type.BLOCK_PHYSICS, BlockListener, Priority.Highest, this);
        manager.registerEvent(Event.Type.PLAYER_PORTAL, PlayerListener, Priority.Monitor, this);
        manager.registerEvent(Event.Type.PLAYER_INTERACT, PlayerListener, Priority.Monitor, this);
        
        DatabaseHandler.GenerateDatabase( "RC", "RiftCraftDB", "./plugins/RiftCraft/");
        DatabaseHandler.SetJustLoaded();
        DatabaseHandler.RegisterDatabaseTimer( this );
        
		InitializeConfig();

        RiftCraftP2PHandler.InitiateRequestPurgeTimer();
	}

	public void onDisable()
	{
		RiftCraftPortal.DispelAllGates();
		PluginDescriptionFile file = this.getDescription();
		String version = file.getVersion();
		log.info("RiftCraft: " + version + " disabled!" );
	}
	
	public void InitializeConfig()
	{
		GenerateConfig();
		LoadConfig();
	}
	
	public void GenerateConfig()
	{
		//RiftCraft.Core
		getConfig().addDefault("RiftCraft.Core.ShowRiftInLog", true );
		getConfig().addDefault("RiftCraft.Core.ExportInterval", 15 );
		getConfig().addDefault("RiftCraft.Core.ShowDBInfoInLog", false );
		//RiftCraft.Casting
		getConfig().addDefault("RiftCraft.Casting.RiftDelay", 3 );
		getConfig().addDefault("RiftCraft.Casting.PortalLife", 30 );
		//RiftCraft.Riftbook
		getConfig().addDefault("RiftCraft.Riftbook.MaxLocations", 10 );
		//RiftCraft.Resources
		getConfig().addDefault("RiftCraft.Resources.RiftResourceID", 348 );
		getConfig().addDefault("RiftCraft.Resources.RiftConsumption", 1 );
		getConfig().addDefault("RiftCraft.Resources.MarkResourceID", 348 );
		getConfig().addDefault("RiftCraft.Resources.MarkConsumption", 1 );
		getConfig().addDefault("RiftCraft.Resources.PortalResourceID", 348 );
		getConfig().addDefault("RiftCraft.Resources.PortalConsumption", 3 );
		getConfig().addDefault("RiftCraft.Resources.DestroyMarkResourceID", 348 );
		getConfig().addDefault("RiftCraft.Resources.DestroyMarkFailConsumption", 1 );
		getConfig().addDefault("RiftCraft.Resources.DestroyMarkSucceedConsumption", 2 );
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void LoadConfig()
	{
		ShowRiftInLog = getConfig().getBoolean("RiftCraft.Core.ShowRiftInLog");
		ExportInterval = getConfig().getInt("RiftCraft.Core.ExportInterval");
		ShowDBInfoInLog = getConfig().getBoolean("RiftCraft.Core.ShowDBInfoInLog");
		RiftDelay = getConfig().getInt("RiftCraft.Casting.RiftDelay");
		PortalLife = getConfig().getInt("RiftCraft.Casting.PortalLife");
		MaxLocations = getConfig().getInt("RiftCraft.Riftbook.MaxLocations");
		RiftResourceID = getConfig().getInt("RiftCraft.Resources.RiftResourceID");
		RiftConsumption = getConfig().getInt("RiftCraft.Resources.RiftConsumption");
		MarkResourceID = getConfig().getInt("RiftCraft.Resources.MarkResourceID");
		MarkConsumption = getConfig().getInt("RiftCraft.Resources.MarkConsumption");
		PortalResourceID = getConfig().getInt("RiftCraft.Resources.PortalResourceID");
		PortalConsumption = getConfig().getInt("RiftCraft.Resources.PortalConsumption");
		DestroyMarkResourceID = getConfig().getInt("RiftCraft.Resources.DestroyMarkResourceID");
		DestroyMarkFailConsumption = getConfig().getInt("RiftCraft.Resources.DestroyMarkFailConsumption");
		DestroyMarkSucceedConsumption = getConfig().getInt("RiftCraft.Resources.DestroyMarkSucceedConsumption");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		String playername = sender.getName().toLowerCase();
		Player player = (Player)sender;
		
		if(cmd.getName().equalsIgnoreCase("rift"))
		{
			if(!PlayerHasBook( playername ))
				GenerateBook( playername );
			
			if(player.hasPermission("riftcraft.rift"))
			{
				if(args.length == 1)
				{
					String inscriptionarg = args[0].toLowerCase();
					InitiateRift( playername, inscriptionarg );
				}
	
				else
				player.sendMessage(ChatColor.RED + "You must enter an inscription name to rift.");
			}
			else
			player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft rift." );
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("riftto"))
		{
			if(!PlayerHasBook( playername ))
				GenerateBook( playername );
			
			if(player.hasPermission("riftcraft.riftto"))
			{
				if(args.length == 1)
				{
					String playernamearg = args[0];
					RiftCraftP2PHandler.InitiateP2PRequest( playername, playernamearg, RCTravelType.Rift );
				}
	
				else
				player.sendMessage(ChatColor.RED + "You must enter a player name to rift to.");
			}
			else
			player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft riftto." );
			return true;
		}

		if(cmd.getName().equalsIgnoreCase("riftcraft"))
		{				
			if(!PlayerHasBook( playername ))
				GenerateBook( playername );
				
			if(args.length > 0)
			{
				String Arg1 = args[0].toLowerCase();

				if( Arg1.equalsIgnoreCase("save"))
				{
					if(player.hasPermission("riftcraft.save"))
					{
						if( args.length == 1 )
						{
							player.sendMessage( ChatColor.RED + "Saving RiftCraft data to RiftCraft Database...");
							RCDatabaseHandler.GenerateRiftbookTable();
							RCDatabaseHandler.WipeRiftbookTable();
							RCDatabaseHandler.PushDatabaseExport();
						}
							
						else
						player.sendMessage( ChatColor.RED + "You have entered an invalid RiftCraft argument.");
					}
					
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft save." );
				}
				
				else if( Arg1.equalsIgnoreCase("load"))
				{
					if(player.hasPermission("riftcraft.load"))
					{
						if( args.length == 1 )
						{
							log.info("Loading RiftCraft data from RiftCraft Database...");
							player.sendMessage( ChatColor.RED + "Loading RiftCraft data from RiftCraft Database...");
							RCDatabaseHandler.GenerateRiftbookTable();
							RCDatabaseHandler.PushDatabaseImport();
						}
							
						else
						player.sendMessage( ChatColor.RED + "You have entered an invalid RiftCraft argument.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft load." );
				}

				else if( Arg1.equalsIgnoreCase("mark"))
				{
					if(player.hasPermission("riftcraft.mark"))
					{
						if( args.length == 2 )
						{
							String markarg1 = args[1];
							InitiateMark( playername, player.getLocation(), markarg1.toLowerCase() );
						}
							
						else
						player.sendMessage( ChatColor.RED + "You must enter a name for the inscription.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft mark." );
				}
				else if( Arg1.equalsIgnoreCase("rift"))
				{
					if(player.hasPermission("riftcraft.rift"))
					{
						if( args.length == 2 )
						{
							String riftarg1 = args[1];
							InitiateRift( playername, riftarg1 );
						}
							
						else
						player.sendMessage(ChatColor.RED + "You must enter a name for the inscription.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft rift." );
				}
				else if( Arg1.equalsIgnoreCase("portal"))
				{
					if(player.hasPermission("riftcraft.portal"))
					{
						if( args.length == 2 )
						{
							String riftarg1 = args[1];
							InitiatePortal( playername, riftarg1 );
						}
							
						else
						player.sendMessage(ChatColor.RED + "You must enter a name for the inscription.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft portal." );
				}
				else if( Arg1.equalsIgnoreCase("find"))
				{
					if(player.hasPermission("riftcraft.find"))
					{
						if( args.length == 1 )
						{
							InitiateScanForMarks( playername );
						}
						else if( args.length == 2 )
						{
							String findarg = args[1].toLowerCase();
							InitiateMarkFind( playername, findarg );
						}
						else
						player.sendMessage(ChatColor.RED + "You must enter a players name.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft find." );
				}
				else if( Arg1.equalsIgnoreCase("accept"))
				{
					if(player.hasPermission("riftcraft.rifttoaccept"))
					{
						if( args.length == 1 )
							RiftCraftP2PHandler.InitiateP2PAccept( playername );
							
						else
						player.sendMessage(ChatColor.RED + "You have entered invalid arguments.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft riftto accept." );
				}
				else if( Arg1.equalsIgnoreCase("deny"))
				{
					if(player.hasPermission("riftcraft.rifttodeny"))
					{
						if( args.length == 1 )
							RiftCraftP2PHandler.InitiateP2PDeny( playername );
							
						else
						player.sendMessage(ChatColor.RED + "You have entered invalid arguments.");
					}
					else
					player.sendMessage( ChatColor.RED + "Warning" + ChatColor.WHITE + ": You do not have permission to use RiftCraft riftto deny." );
				}
				else if( Arg1.equalsIgnoreCase("book"))
				{
					if( args.length == 1 )
						DisplayRiftBook( playername );
					
					else if( args.length == 2 )
					{
						String bookarg1 = args[1].toLowerCase();
						if( bookarg1.equalsIgnoreCase("display"))
							DisplayRiftBook( playername );
						
						else
						player.sendMessage( ChatColor.RED + "You have entered an invalid RiftCraft arguement.");
					}
					
					else if( args.length == 3 )
					{
						String bookarg1 = args[1].toLowerCase();
						String bookarg2 = args[2].toLowerCase();
						if( bookarg1.equalsIgnoreCase("remove"))
							InitiateInscriptionRemoval( playername, playername, bookarg2 );
						
						else
						player.sendMessage( ChatColor.RED + "You have entered an invalid RiftCraft arguement.");
					}
					
					else
					player.sendMessage(ChatColor.RED + "You must enter an inscription name to delete.");

				}
				else
				player.sendMessage(ChatColor.RED + "you have entered an invalid RiftCraft arguement.");
			}
			else
			InitiateRiftCraftHelp( sender );
			
			return true;
		}
		return false;
	}

	public static boolean PlayerHasBook( String player )
	{
		return RiftCraftDB.containsKey(player);
	}

	public static void GenerateBook( String player )
	{
		RiftCraftDB.put(player, new RiftBook());
		log.info("RiftBook created for player: " + player);
	}
	
	// Move this to riftbook
	private void DisplayRiftBook( String playername )
	{
		RiftBook book = RiftCraftDB.get(playername);
		Player player = Bukkit.getPlayer(playername);
		player.sendMessage( ChatColor.YELLOW + "You RiftBook contains the following locations:");
		
		for (Map.Entry<String, Location> entry : book.Inscriptions.entrySet())
		{
			String name = entry.getKey();
			Location loc = entry.getValue();
			String message =   ChatColor.YELLOW + RiftCraftUtilities.Capitalize(name) + ChatColor.WHITE + " - " + ChatColor.GRAY + RiftCraftUtilities.LocationToCoords(loc);
			player.sendMessage( message );
		}
	}
	
	public void InitiateMark( String playername, Location loc, String name )
	{
		Player player = Bukkit.getPlayer(playername);
		if(!PlayerHasBook( playername ))
			GenerateBook( playername );
		
		RiftBook book = RiftCraftDB.get(playername);
		Inventory inv = player.getInventory();
		
		if( book.Inscriptions.size() >= RiftCraft.MaxLocations )
			player.sendMessage( ChatColor.RED + "You Riftbook is full, you must first remove an inscription.");

		else if( !inv.contains(RiftCraft.MarkResourceID, RiftCraft.MarkConsumption))
			player.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.YELLOW + "glowstone dust " + ChatColor.RED + "to inscribe your RiftBook.");
		
		else
		{
			inv.removeItem(new ItemStack(RiftCraft.MarkResourceID, RiftCraft.MarkConsumption));
			book.MarkLocation( loc, name );
			player.sendMessage( ChatColor.YELLOW + "You inscribe " + name + " into your Riftbook.");
		}
	}
	
	public static void InitiateRiftTo( final String requestername, final String requestedname )
	{
		Player requester = Bukkit.getPlayer(requestername);
		Player requested = Bukkit.getPlayer(requestedname);
		final Location destination = requested.getLocation();
		
		final Inventory inv = requester.getInventory();
		if( inv.contains(RiftCraft.RiftResourceID, RiftCraft.RiftConsumption))
		{
			requester.playEffect(requester.getLocation(), Effect.SMOKE, 0);
			requester.sendMessage( ChatColor.YELLOW + requestedname + " has accepted, you begin opening a rift in space...");
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask( Bukkit.getPluginManager().getPlugin("RiftCraft"), new Runnable(){public void run() { FinishRiftTo( requestername, requestedname, inv, destination );} }, 20 * RiftCraft.RiftDelay );
		}
	}
	
	public static void FinishRiftTo( String requestername, String requestedname, Inventory inv, Location destination )
	{
		Player requester = Bukkit.getPlayer(requestername);
		inv.removeItem(new ItemStack(RiftCraft.RiftResourceID, RiftCraft.RiftConsumption));
		requester.teleport(destination);
		requester.playEffect(requester.getLocation(), Effect.SMOKE, 0);
		requester.sendMessage( ChatColor.YELLOW + "You have rifted to " + requestedname + ".");
		
		if( ShowRiftInLog )
			log.info("RiftCraft: " + requestername + " has rifted to " + requestedname + " - " + RiftCraftUtilities.LocationToCoords(destination));
	}
	
	public void InitiateRift( final String playername, final String inscription )
	{
		final RiftBook book = RiftCraftDB.get(playername);
		Player player = Bukkit.getPlayer(playername);
		if( book.HasInscription(inscription) )
		{
			final Inventory inv = player.getInventory();
			if( inv.contains(RiftCraft.RiftResourceID, RiftCraft.RiftConsumption))
			{
				player.playEffect(player.getLocation(), Effect.SMOKE, 0);
				player.sendMessage( ChatColor.YELLOW + "You begin opening a rift in space...");
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){public void run() { FinishRift( playername, book, inscription, inv );} }, 20 * RiftCraft.RiftDelay );
				// TODO make it so if the player moves or gets hit during this pause that it cancels the rift				
			}
			else
			player.sendMessage( ChatColor.RED + "You do not have enough " + ChatColor.YELLOW + "glowstone dust " + ChatColor.RED + "to create a rift.");
		}
		
		else
		player.sendMessage( ChatColor.RED +"Your Riftbook does not contain an inscription to " + inscription  + ".");
	}
	
	public void FinishRift( String playername, RiftBook book, String inscription, Inventory inv )
	{
		Player player = Bukkit.getPlayer(playername);
		inv.removeItem(new ItemStack(RiftCraft.RiftResourceID, RiftCraft.RiftConsumption));
		book.RiftToLocation( playername, inscription );
		player.playEffect(player.getLocation(), Effect.SMOKE, 0);
		player.sendMessage( ChatColor.YELLOW + "You rift to your " + inscription + " inscription.");
		
		if( ShowRiftInLog )
			log.info("RiftCraft: " + player.getName().toString() + " rift to: " + inscription + " - " + RiftCraftUtilities.LocationToCoords(book.GetLocation(inscription)) );
	}
	
	public static void InitiateInscriptionRemoval( String destroyer, String playername, String inscription )
	{
		Player PlayerDestroying = Bukkit.getPlayer(destroyer);
		RiftBook book = RiftCraftDB.get(playername);
		
		if(book.HasInscription(inscription))
		{
			book.RemoveInscription(inscription);
			PlayerDestroying.sendMessage( ChatColor.YELLOW + "The " + inscription + " inscription has been removed from " + playername + "'s RiftBook.");
		}
		
		else
			PlayerDestroying.sendMessage( ChatColor.RED + playername + "'s riftbook does not contain an inscription named " + inscription + ".");
	}
	
	public void InitiateMarkFind( String castername, String targetname )
	{		
		Player Caster = Bukkit.getPlayer(castername);
		if( RiftCraftDB.containsKey(targetname) )
		{
			final RiftBook book = RiftCraftDB.get(targetname);
			Location nearest = book.GetClosestMark(castername,  book);
			Caster.setCompassTarget(nearest);
			Caster.sendMessage(ChatColor.YELLOW + "Your compass is now pointing to " + targetname + " nearest rift mark.");
		}
		else
		Caster.sendMessage(ChatColor.YELLOW + "No RiftCraft marks have been created by " + targetname + ".");
	}
	
	public void InitiatePortal( String playername, String inscription )
	{
		final RiftBook book = RiftCraftDB.get(playername);
		Player player = Bukkit.getPlayer(playername);
		if( book.HasInscription(inscription) )
		{
			final Inventory inv = player.getInventory();
			if( inv.contains(RiftCraft.PortalResourceID, RiftCraft.PortalConsumption))
			{
				inv.removeItem(new ItemStack(RiftCraft.PortalResourceID, RiftCraft.PortalConsumption));
				player.playEffect(player.getLocation(), Effect.SMOKE, 0);
				player.sendMessage( ChatColor.YELLOW + "You tear a rift in Time and Space...");
				Location dest = book.GetLocation( inscription );
				
				//origin to destination
				RiftCraftPortal.GenerateRiftCraftPortal( this, player.getLocation(), dest );
				
				//destination to origin
				RiftCraftPortal.GenerateRiftCraftPortal( this, dest, player.getLocation() );
			}
			else
			player.sendMessage( ChatColor.RED + "You do not have enough " + ChatColor.YELLOW + "glowstone dust " + ChatColor.RED + "to create a rift.");
		}
		
		else
		player.sendMessage( ChatColor.RED +"Your Riftbook does not contain an inscription to " + inscription  + ".");
	}
	
	public void InitiateRiftCraftHelp( CommandSender sender )
	{
		PluginDescriptionFile file = this.getDescription();
		String version = file.getVersion();
		String description = file.getDescription();
		sender.sendMessage( ChatColor.YELLOW + "RiftCraft Version: " + ChatColor.WHITE + version );
		sender.sendMessage( ChatColor.YELLOW + "RiftCraft Description: " + ChatColor.WHITE + description );
		sender.sendMessage( ChatColor.YELLOW + "Command: " + ChatColor.WHITE + "/riftcraft (" + ChatColor.YELLOW + "Alias: " + ChatColor.WHITE + "/rc)");
		sender.sendMessage( ChatColor.YELLOW + "/rc " + ChatColor.WHITE + "mark "  + ChatColor.GRAY + "<locname>");
		sender.sendMessage( ChatColor.YELLOW + "/rc " + ChatColor.WHITE + "rift "  + ChatColor.GRAY + "<locname>");
		sender.sendMessage( ChatColor.YELLOW + "/rc " + ChatColor.WHITE + "portal "  + ChatColor.GRAY + "<locname>");
		sender.sendMessage( ChatColor.YELLOW + "/rc " + ChatColor.WHITE + "find "  + ChatColor.GRAY + "<playername>");
		sender.sendMessage( ChatColor.YELLOW + "Command: " + ChatColor.WHITE + "/rift");
		sender.sendMessage( ChatColor.YELLOW + "/rift " + ChatColor.GRAY + "<playername>");
	}
	
	public static void LaunchBrowser( String playername, String URL )
	{
		Player player = Bukkit.getPlayer(playername);
		try
		{
			player.sendMessage( ChatColor.YELLOW + "Opening browser: " + URL );
	        Desktop desktop = Desktop.getDesktop();
	        java.net.URI uri = new java.net.URI( URL );
	        desktop.browse( uri );
		}
        catch ( Exception e )
        {
			player.sendMessage( ChatColor.RED + "Failed to open browser: " + URL );
            System.err.println( e.getMessage() );
        }
	}

	public static void InitiateMarkDestroy(PlayerInteractEvent event, Block targetblock )
	{
		Player player = event.getPlayer();
		final Inventory inv = player.getInventory();
		String MarkInscription = null;
		String MarkOwner = null;
		
		if( inv.contains(RiftCraft.DestroyMarkResourceID, RiftCraft.DestroyMarkSucceedConsumption))
		{
			for (Map.Entry<String, RiftBook> KPV : RiftCraftDB.entrySet() )
			{
				String owner = KPV.getKey();
				RiftBook book = KPV.getValue();
				String results = book.BlockIsInscription(targetblock);
				if( results != null )
				{
					MarkInscription = results;
					MarkOwner = owner;
					continue;
				}
			}
			
			if( MarkInscription != null && MarkOwner != null )
			{
				inv.removeItem(new ItemStack(RiftCraft.DestroyMarkResourceID, RiftCraft.DestroyMarkSucceedConsumption));
				player.sendMessage( ChatColor.YELLOW + "You destroyed " + MarkOwner + "'s mark to " + MarkInscription + ".");
				InitiateInscriptionRemoval( player.getName(), MarkOwner, MarkInscription );
			}
			
			else
			{
				player.sendMessage( ChatColor.RED + "You attempt to destroy a Rift mark location, but fail...");
				inv.removeItem(new ItemStack(RiftCraft.DestroyMarkResourceID, RiftCraft.DestroyMarkFailConsumption));
			}
		}
		else
		player.sendMessage( ChatColor.RED + "You do not have enough " + ChatColor.YELLOW + "glowstone dust " + ChatColor.RED + "to destroy that mark.");
	}
	
	public static void InitiateScanForMarks( String playername )
	{
		Player player = Bukkit.getPlayer(playername);
		List<String> NearbyMarkOwners = new ArrayList<String>();
		for (Map.Entry<String, RiftBook> KPV : RiftCraftDB.entrySet() )
		{
			String owner = KPV.getKey();
			RiftBook book = KPV.getValue();
			boolean HasNearby = book.HasMarkNearby( playername );
			if( HasNearby )
				NearbyMarkOwners.add( owner );
		}
		
		String output = "Nearby marks:";
		for( int i = 0; i < NearbyMarkOwners.size(); i++ )
		{
			output += " " + NearbyMarkOwners.get(i);
			if( (i + 1) != NearbyMarkOwners.size() ) 
				output += ",";
			else
				output += ".";
		}
		player.sendMessage(output);
	}
}