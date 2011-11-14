package me.FatherTime.RiftCraft;

import java.util.Date;

public class RiftCraftP2PRequest
{
	public enum RCTravelType
	{
		Rift,
		Portal
	}
	
	public String Requester;
	public String Requested;
	public RCTravelType TravelType;
	public Long TimeRequested;
	
	public RiftCraftP2PRequest( String requester, String requested, RCTravelType traveltype )
	{
		this.Requester = requester;
		this.Requested = requested;
		this.TravelType = traveltype;
		this.TimeRequested = new Date().getTime();
	}
	
	public boolean HasExpired()
	{
		Long Now = new Date().getTime();
		if( (Now - TimeRequested) > 30000 )
			return true;
		
		return false;
	}
}
