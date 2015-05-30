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
public class Users extends AbstractModel
{
	public static final String	FIELDS	= "uuid,username";
	private String				name;

	public Users (String uuid, String username)
	{
		super (uuid);
		this.name = username;
	}

	public JSONObject getJSONObject ()
	{
		JSONObject jsonObject = new JSONObject ();
		try
		{
			jsonObject.put ("uuid", super.getUuid ());
			jsonObject.put ("username", name);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			jsonObject = null;
		}
		return jsonObject;
	}

	public static Users parseJSONObject (JSONObject json)
	{
		Users user = null;
		String uuid = "";
		String username = "";
		try
		{
			uuid = json.getString ("uuid");
			username = json.getString ("username");
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			user = null;
		}
		user = new Users (uuid, username);
		return user;
	}

	public String getUsername ()
	{
		return name;
	}

	public void setUsername (String username)
	{
		this.name = username;
	}

	@Override
	public String toString ()
	{
		return super.toString () + ", " + name;
	}
}
