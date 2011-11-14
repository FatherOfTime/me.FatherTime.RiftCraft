package me.FatherTime.RiftCraft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class RCDatabaseHandler extends RCBaseDatabaseHandler
{
	@Override
	public void ImportDatabase()
	{
    	if( RiftCraft.ShowRiftInLog )
    		DBInfo("Importing RiftCraft database...");
    	
    	GenerateRiftbookTable();
    	PushDatabaseImport();
		
    	if( RiftCraft.ShowRiftInLog )
    		DBInfo("RiftCraft Database import complete.");
	}
	
	public static void PushDatabaseImport()
	{
		if ( ContainsTable("RiftBookLocations"))
		{
			try
			{
    			PreparedStatement importstatement = GetConnection().prepareStatement("SELECT * FROM RiftBookLocations");
    			ResultSet results = importstatement.executeQuery();
				while(results.next())
			    {
					String playername = results.getString("PlayerName");
			        		
			       	if( !RiftCraft.RiftCraftDB.containsKey(playername))
			       		RiftCraft.GenerateBook( playername );
			        			
			       	RiftBook book = RiftCraft.RiftCraftDB.get(playername);
			       	World world = Bukkit.getWorld(results.getString("World"));
			       	Double XCoord = results.getDouble("XCoord");
			       	Double YCoord = results.getDouble("YCoord");
			       	Double ZCoord = results.getDouble("ZCoord");
			       	String locname = results.getString("LocationName");
			       	Location loc = new Location( world, XCoord, YCoord, ZCoord);
			       	book.MarkLocation( loc, locname);
			    }
			}
			catch( Exception e )
			{
    			RiftCraft.log.info("RiftCraft: Database Error - " + e.toString() );
			}
		}
		else
			DBError("RiftCraft database import couldn't locate RiftBookLocations table");
	}

	@Override
	public void ExportDatabase()
	{
    	if( RiftCraft.ShowRiftInLog )
    		DBInfo("Exporting RiftCraft database...");
    	
		RCDatabaseHandler.GenerateRiftbookTable();
		RCDatabaseHandler.WipeRiftbookTable();
		RCDatabaseHandler.PushDatabaseExport();

    	if( RiftCraft.ShowRiftInLog )
    		DBInfo("RiftCraft database export complete.");
	}
	
	public static void PushDatabaseExport()
	{
		if ( ContainsTable("RiftBookLocations"))
		{
			for (Map.Entry<String, RiftBook> DBEntry : RiftCraft.RiftCraftDB.entrySet())
		    {
				String playername = DBEntry.getKey();
		    	RiftBook book = DBEntry.getValue();

		    	for (Map.Entry<String, Location>RiftBookEntry : book.Inscriptions.entrySet())
		    	{
		    		String locname = RiftBookEntry.getKey();
		    		Location loc = RiftBookEntry.getValue();
		    		String world = loc.getWorld().getName();
		    		double xcoord = loc.getX();
		    		double ycoord = loc.getY();
		    		double zcoord = loc.getZ();

		    		try
		    		{
		    			PreparedStatement insertstatement = GetConnection().prepareStatement("INSERT INTO RiftBookLocations(PlayerName, LocationName, World, XCoord, YCoord, ZCoord) VALUES(?, ?, ?, ?, ?, ?)");
		    			insertstatement.setString(1, playername.toLowerCase());
		    			insertstatement.setString(2, locname);
		    			insertstatement.setString(3, world);
			    	       
		    			insertstatement.setDouble(4, xcoord);
		    			insertstatement.setDouble(5, ycoord);
		    			insertstatement.setDouble(6, zcoord);
		    			insertstatement.executeUpdate();
		    		}
		    		catch(Exception e)
		    		{
		    			DBError("RiftCraft database export error: " + e.toString() );
		    		}
		    	}
			}
		}
		else
			DBError("RiftCraft database import couldn't locate RiftBookLocations table");
	}
	
	public static void WipeRiftbookTable()
	{
		WipeTable( "RiftBookLocations" );
	}
	
	public static void GenerateRiftbookTable()
	{
		if ( !ContainsTable("RiftBookLocations"))
		{
        	if( RiftCraft.ShowRiftInLog )
        		DBInfo("RiftCraft generating table \"RiftBookLocations\"");
        	
			String[] fields = new String[7];
			fields[0] = "RecordID INTEGER PRIMARY KEY";
			fields[1] = "PlayerName VARCHAR(75)";
			fields[2] = "LocationName VARCHAR(75)";
			fields[3] = "World VARCHAR(75)";
			fields[4] = "XCoord DECIMAL(50)";
			fields[5] = "YCoord DECIMAL(50)";
			fields[6] = "ZCoord DECIMAL(50)";
			CreateTable( "RiftBookLocations", fields );
			
        	if( RiftCraft.ShowRiftInLog )
        		RiftCraft.log.info("RiftCraft table generation complete.");
        }
	}	
}
