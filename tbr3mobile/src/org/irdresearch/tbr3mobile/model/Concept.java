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

import org.irdresearch.tbr3mobile.util.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class Concept extends AbstractModel
{
	public static final String	FIELDS		= "uuid,name,datatype";
	private String				name		= "";
	private String				dataType	= "";

	public Concept (String uuid, String name, String dataType)
	{
		super (uuid);
		this.name = name;
		this.dataType = dataType;
	}

	public JSONObject getJSONObject ()
	{
		JSONObject jsonObject = new JSONObject ();
		try
		{
			jsonObject.put ("uuid", super.getUuid ());
			jsonObject.put ("name", name);
			jsonObject.put ("datatype", dataType);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			jsonObject = null;
		}
		return jsonObject;
	}

	public static Concept parseJSONObject (JSONObject json)
	{
		Concept concept = null;
		String uuid = "";
		String name = "";
		String dataType = "";
		try
		{
			uuid = json.getString ("uuid");
			name = json.getString ("name");
			// Since name itself is JSON Object, parse it more
			JSONObject nameObj = JSONParser.getJSONObject (name);
			name = nameObj.getString ("name");
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			concept = null;
		}
		concept = new Concept (uuid, name, dataType);
		return concept;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public String getDataType ()
	{
		return dataType;
	}

	public void setDataType (String dataType)
	{
		this.dataType = dataType;
	}

	@Override
	public String toString ()
	{
		return super.toString () + ", " + name + ", " + dataType;
	}
}
