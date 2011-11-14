package me.FatherTime.RiftCraft;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

abstract class RCBaseDatabaseHandler
{
	private static String DatabaseName;
	private static String FileLocation;
	private static File SQLFile;
	private static Connection DBConnection;
	private static Logger DBLog = Logger.getLogger("Minecraft");
	private static boolean JustLoaded;
	
	public void GenerateDatabase( String prefix, String databasename, String filelocation )
	{
		DatabaseName = databasename;
		FileLocation = filelocation;
		File folder = new File(FileLocation);
			
		if (!folder.exists())
			folder.mkdir();
		
		SQLFile = new File(folder.getAbsolutePath() + File.separator + DatabaseName + ".db");
	}
	
	public void SetJustLoaded()
	{
		JustLoaded = true;
	}
	
	protected static boolean initialize()
	{
		try
		{
		  Class.forName("org.sqlite.JDBC");
		  return true;
		} 
		catch (ClassNotFoundException e)
		{
			DBError("You need the SQLite library " + e );
			return false;
		}
	}
	
	protected static Connection Open()
	{
		if (initialize() )
		{
			try
			{
			  DBConnection = DriverManager.getConnection("jdbc:sqlite:" + SQLFile.getAbsolutePath());
			} 
			catch (SQLException e)
			{
				DBError("SQLite exception on initialize " + e );
			}
		}
		return null;
	}

	public static void Close()
	{
		if( DBConnection != null )
		{
			try
			{
				DBConnection.close();
			}
			catch (SQLException ex)
			{
				DBError("Error on Connection close: " + ex );
			}
		}
	}

	public static Connection GetConnection()
	{
		if( DBConnection == null)
			return Open();
		
		return DBConnection;
	}
	
	public static boolean ContainsTable( String tablename )
	{
		GetConnection();
		DatabaseMetaData dbm = null;
		try
		{
			dbm = DBConnection.getMetaData();
			ResultSet tables = dbm.getTables(null, null, tablename, null);
			if (tables.next())
			  return true;
			else
			  return false;
		}
		catch (SQLException e)
		{
			DBError("Unable to perform ContainsTable check on \"" + tablename + "\": " + e.getMessage() );
			return false;
		}
	}

	public static ResultSet QueryTable( String tablename, String query )
	{
		ResultSet results = null;
		GetConnection();
		try
		{
			PreparedStatement querystatement = DBConnection.prepareStatement( query );
			results = querystatement.executeQuery();
		}
		catch(SQLException e)
		{
			DBError( "Query failed: " + query );
		}
		return results;					
	}

	public static void UpdateTable( String tablename, String update )
	{
		GetConnection();
		try
		{
			PreparedStatement updatestatement = DBConnection.prepareStatement( update );
			updatestatement.executeUpdate();
		}
		catch(SQLException e)
		{
			DBError( "Query failed: " + update );
		}					
	}
	
	// example usage: CreateTable( "DatabaseExampleTable", { "'RecordID' VARCHAR(100) NOT NULL", "'Player' VARCHAR(100) NOT NULL" } )
	public static void CreateTable( String tablename, String[] fields ) 
	{
		GetConnection();
		try
		{
			String queryfields = "";
			for( int i = 0; i < fields.length; i++ )
				queryfields +=  queryfields == "" ? "" : ", " + fields[i];

			String query = "CREATE TABLE IF NOT EXISTS '" + tablename + "' (" + queryfields + ")";
			DBInfo("Creating table: " + query );
			PreparedStatement tablequery = DBConnection.prepareStatement(query);
			tablequery.execute();
		}
		catch(SQLException e)
		{
			DBError( tablename + " table generation failed.");
		}
	}

	public static void WipeTable( String tablename )
	{
		GetConnection();			
		try
		{
			DBInfo("Wiping " + tablename + "...");
			PreparedStatement tablequery = DBConnection.prepareStatement( "DELETE FROM " + tablename );
			tablequery.execute();
		}
		catch(SQLException e)
		{
			DBError( tablename + " table wipe failed.");
		}
	}

	public static void DBError( String error )
	{
		String header = "DBError: ";
		DBLog.info( header + error );
	}

	public static void DBInfo( String info )
	{
		String header = "DBInfo: ";
		DBLog.info( header + info );
	}

	public void ImportDatabase()
	{
	}
	
	public void ExportDatabase()
	{
	}
	
	public void RegisterDatabaseTimer( JavaPlugin plugin )
	{
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask( plugin, new Runnable(){public void run() 
		{
			if( JustLoaded )
			{
				JustLoaded = false;
				ImportDatabase();
			}
			else
			{
				ExportDatabase();
			}
		} }, 20L * 5L, 1200L * 10L );
	}
}