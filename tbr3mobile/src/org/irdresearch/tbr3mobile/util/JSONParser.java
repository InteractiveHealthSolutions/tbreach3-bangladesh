/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class reads and writes JSON objects/arrays
 */

package org.irdresearch.tbr3mobile.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class JSONParser
{
	private static final String	TAG	= "JSONParser";

	public static JSONObject getJSONObject (String jsonText)
	{
		// try parse the string to a JSON object
		try
		{
			JSONObject jsonObj = new JSONObject (jsonText);
			return jsonObj;
		}
		catch (JSONException e)
		{
			Log.e (TAG, "Error parsing data. " + e.getMessage ());
			return null;
		}
	}

	public static JSONObject[] getJSONArrayFromObject (JSONObject jsonObj, String arrayElement)
	{
		try
		{
			JSONArray jsonArray = jsonObj.getJSONArray (arrayElement);
			JSONObject[] jsonObjects = new JSONObject[jsonArray.length ()];
			for (int i = 0; i < jsonArray.length (); i++)
			{
				jsonObjects[i] = JSONParser.getJSONObject (jsonArray.getString (i));
			}
			return jsonObjects;
		}
		catch (JSONException e)
		{
			Log.e (TAG,
					"Error parsing array from JSON Object using element \'" + arrayElement + "\'. " + e.getMessage ());
			return null;
		}
	}
}
