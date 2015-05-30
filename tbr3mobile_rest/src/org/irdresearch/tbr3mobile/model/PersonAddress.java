/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * 
 */

package org.irdresearch.tbr3mobile.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class PersonAddress extends AbstractModel
{
	public static final String	FIELDS	= "uuid,name,address1,address2,address3,cityVillage,stateProvince,country,latitude,longitude";
	private String				name;
	private String				address1;
	private String				address2;
	private String				address3;
	private String				cityVillage;
	private String				stateProvince;
	private String				country;
	private Double				latitude;
	private Double				longitute;

	public PersonAddress (String uuid, String name)
	{
		super (uuid);
		this.name = name;
	}

	public PersonAddress (String uuid, String name, String address1, String address2, String address3,
			String cityVillage, String country)
	{
		super (uuid);
		this.name = name;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.cityVillage = cityVillage;
		this.country = country;
	}

	public PersonAddress (String uuid, String name, String address1, String address2, String address3,
			String cityVillage, String stateProvince, String country, Double latitude, Double longitute)
	{
		super (uuid);
		this.name = name;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.cityVillage = cityVillage;
		this.stateProvince = stateProvince;
		this.country = country;
		this.latitude = latitude;
		this.longitute = longitute;
	}

	public JSONObject getJSONObject ()
	{
		JSONObject jsonObject = new JSONObject ();
		try
		{
			jsonObject.put ("uuid", super.getUuid ());
			jsonObject.put ("name", name);
			jsonObject.put ("address1", address1);
			jsonObject.put ("address2", address2);
			jsonObject.put ("address3", address3);
			jsonObject.put ("cityVillage", cityVillage);
			jsonObject.put ("stateProvince", stateProvince);
			jsonObject.put ("country", country);
			jsonObject.put ("latitude", latitude);
			jsonObject.put ("longitute", longitute);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			jsonObject = null;
		}
		return jsonObject;
	}

	public static PersonAddress parseJSONObject (JSONObject json)
	{
		PersonAddress personAddress = null;
		String uuid = "";
		String name = "";
		String address1 = "";
		String address2 = "";
		String address3 = "";
		String cityVillage = "";
		String stateProvince = "";
		String country = "";
		Double latitude = 0D;
		Double longitute = 0D;
		try
		{
			uuid = json.getString ("uuid");
			name = json.getString ("name");
			address1 = json.getString ("address1");
			address2 = json.getString ("address2");
			address3 = json.getString ("address3");
			cityVillage = json.getString ("cityVillage");
			stateProvince = json.getString ("stateProvince");
			country = json.getString ("country");
			latitude = json.getDouble ("latitude");
			longitute = json.getDouble ("longitute");
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			personAddress = null;
		}
		personAddress = new PersonAddress (uuid, name, address1, address2, address3, cityVillage, stateProvince,
				country, latitude, longitute);
		return personAddress;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public String getAddress1 ()
	{
		return address1;
	}

	public void setAddress1 (String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2 ()
	{
		return address2;
	}

	public void setAddress2 (String address2)
	{
		this.address2 = address2;
	}

	public String getAddress3 ()
	{
		return address3;
	}

	public void setAddress3 (String address3)
	{
		this.address2 = address3;
	}

	public String getCityVillage ()
	{
		return cityVillage;
	}

	public void setCityVillage (String cityVillage)
	{
		this.cityVillage = cityVillage;
	}

	public String getStateProvince ()
	{
		return stateProvince;
	}

	public void setStateProvince (String stateProvince)
	{
		this.stateProvince = stateProvince;
	}

	public String getCountry ()
	{
		return country;
	}

	public void setCountry (String country)
	{
		this.country = country;
	}

	public Double getLatitude ()
	{
		return latitude;
	}

	public void setLatitude (Double latitude)
	{
		this.latitude = latitude;
	}

	public Double getLongitute ()
	{
		return longitute;
	}

	public void setLongitute (Double longitute)
	{
		this.longitute = longitute;
	}

	@Override
	public String toString ()
	{
		return super.toString () + ", " + name + ", " + address1 + ", " + address2 + ", " + address3 + ", "
				+ cityVillage + ", " + stateProvince + ", " + country + ", " + latitude + ", " + longitute;
	}

}
