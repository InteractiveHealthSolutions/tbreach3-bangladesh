/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class handles metadata-related functions
 */

package org.irdresearch.tbr3mobile.util;

import java.util.ArrayList;
import org.irdresearch.tbr3mobile.App;
import org.irdresearch.tbr3mobile.model.Concept;
import org.irdresearch.tbr3mobile.model.EncounterType;
import org.irdresearch.tbr3mobile.model.IdentifierType;
import org.irdresearch.tbr3mobile.model.Location;
import org.irdresearch.tbr3mobile.model.PersonAttributeType;
import org.irdresearch.tbr3mobile.model.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MetadataUtil
{
	private static final String	TAG						= "MetadataUtil";
	public static final String	METADATA_TABLE			= "identifiers";
	public static final String	USER					= "user";
	public static final String	PROVIDER				= "provider";
	public static final String	LOCATION				= "location";
	public static final String	ENCOUNTER_TYPE			= "encountertype";
	public static final String	IDENTIFIER_TYPE			= "patientidentifiertype";
	public static final String	PERSON_ATTRIBUTE_TYPE	= "personattributetype";
	public static final String	CONCEPT					= "concept";

	private String				httpsUri;
	private HttpsClient			client;
	private DatabaseUtil		util;

	public MetadataUtil (Context context)
	{
		httpsUri = App.getServer () + "/ws/rest/v1/";
		client = new HttpsClient (context);
		util = new DatabaseUtil (context);
	}

	public String getUuid (String type, String name)
	{
		String uuid = util.getObject (METADATA_TABLE, "uuid", "type='" + type + "' AND name='" + name + "'");
		// Try to fetch concept from server
		if (type == CONCEPT && uuid == null)
		{
			String response = client.clientGet (httpsUri + "concept?name=" + name.replace (" ", "%20"));
			if (response != null)
			{
				try
				{
					JSONObject jsonObj = JSONParser.getJSONObject (response);
					JSONObject[] jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
					ContentValues values = new ContentValues ();
					for (JSONObject obj : jsonObjects)
					{
						values.put ("type", type);
						values.put ("name", name);
						uuid = obj.getString ("uuid");
						values.put ("uuid", uuid);
						util.insert (METADATA_TABLE, values);
					}
				}
				catch (JSONException e)
				{
					Log.e (TAG, e.getMessage ());
				}
			}
		}
		return uuid;
	}

	public String getMetaDescription (String metaType, String name)
	{
		return util.getObject (METADATA_TABLE, "description", "type='" + metaType + "' AND name='" + name + "'");
	}

	public void fillPersonAttributesMetadata () throws JSONException
	{
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		JSONObject[] jsonObjects = new JSONObject[0];
		// Get all person attribute type UUIDs from database
		String[] personAttributeTypeUuids = util.getColumnData (METADATA_TABLE, "uuid", "type='" + PERSON_ATTRIBUTE_TYPE + "'", false);
		ArrayList<String> personAttributeTypeUuidsFromServer = new ArrayList<String> ();
		// Get UUID of all person attribute type from server
		response = client.clientGet (httpsUri + PERSON_ATTRIBUTE_TYPE + "?v=custom:(uuid)");
		if (response != null)
		{
			jsonObj = JSONParser.getJSONObject (response);
			jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
			values = new ContentValues ();
			for (JSONObject j : jsonObjects)
			{
				personAttributeTypeUuidsFromServer.add (j.getString ("uuid"));
			}
			// Delete the UUIDs that are present in local database and not in
			// server
			for (String s : personAttributeTypeUuids)
			{
				if (searchArray (personAttributeTypeUuidsFromServer.toArray (), s) == -1)
				{
					util.delete (METADATA_TABLE, "type='" + PERSON_ATTRIBUTE_TYPE + "' AND uuid=?", new String[] {s});
				}
			}
			// Insert the UUIDs that are not present in the local database
			for (String s : personAttributeTypeUuidsFromServer)
			{
				if (searchArray (personAttributeTypeUuids, s) == -1)
				{
					response = client.clientGet (httpsUri + PERSON_ATTRIBUTE_TYPE + "/" + s + "?v=custom:(" + IdentifierType.FIELDS + ")");
					jsonObj = JSONParser.getJSONObject (response);
					PersonAttributeType personAttributeType = PersonAttributeType.parseJSONObject (jsonObj);
					values = new ContentValues ();
					values.put ("type", PERSON_ATTRIBUTE_TYPE);
					values.put ("name", personAttributeType.getName ());
					values.put ("uuid", personAttributeType.getUuid ());
					values.put ("description", jsonObj.toString ());
					util.insert (METADATA_TABLE, values);
				}
			}
		}
	}

	public void fillIdentifiersMetadata () throws JSONException
	{
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		JSONObject[] jsonObjects = new JSONObject[0];
		// Get all identifier type UUIDs from database
		String[] identifierTypeUuids = util.getColumnData (METADATA_TABLE, "uuid", "type='" + IDENTIFIER_TYPE + "'", false);
		ArrayList<String> identifierTypeUuidsFromServer = new ArrayList<String> ();
		// Get UUID of all identifier types from server
		response = client.clientGet (httpsUri + IDENTIFIER_TYPE + "?v=custom:(uuid)");
		if (response != null)
		{
			jsonObj = JSONParser.getJSONObject (response);
			jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
			values = new ContentValues ();
			for (JSONObject j : jsonObjects)
			{
				identifierTypeUuidsFromServer.add (j.getString ("uuid"));
			}
			// Delete the UUIDs that are present in local database and not in
			// server
			for (String s : identifierTypeUuids)
			{
				if (searchArray (identifierTypeUuidsFromServer.toArray (), s) == -1)
				{
					util.delete (METADATA_TABLE, "type='" + IDENTIFIER_TYPE + "' AND uuid=?", new String[] {s});
				}
			}
			// Insert the UUIDs that are not present in the local database
			for (String s : identifierTypeUuidsFromServer)
			{
				if (searchArray (identifierTypeUuids, s) == -1)
				{
					response = client.clientGet (httpsUri + IDENTIFIER_TYPE + "/" + s + "?v=custom:(" + IdentifierType.FIELDS + ")");
					jsonObj = JSONParser.getJSONObject (response);
					IdentifierType identifierType = IdentifierType.parseJSONObject (jsonObj);
					values = new ContentValues ();
					values.put ("type", IDENTIFIER_TYPE);
					values.put ("name", identifierType.getName ());
					values.put ("uuid", identifierType.getUuid ());
					values.put ("description", jsonObj.toString ());
					util.insert (METADATA_TABLE, values);
				}
			}
		}
	}

	public void fillUsersMetadata () throws JSONException
	{
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		JSONObject[] jsonObjects = new JSONObject[0];
		// Get all user UUIDs from database
		String[] userUuids = util.getColumnData (METADATA_TABLE, "uuid", "type='" + USER + "'", false);
		ArrayList<String> userUuidsFromServer = new ArrayList<String> ();
		// Get UUID of all users from server
		response = client.clientGet (httpsUri + "user?v=custom:(uuid)");
		if (response != null)
		{
			jsonObj = JSONParser.getJSONObject (response);
			jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
			values = new ContentValues ();
			for (JSONObject j : jsonObjects)
			{
				userUuidsFromServer.add (j.getString ("uuid"));
			}
			// Delete the UUIDs that are present in local database and not in
			// server
			for (String s : userUuids)
			{
				if (searchArray (userUuidsFromServer.toArray (), s) == -1)
				{
					util.delete (METADATA_TABLE, "type='" + USER + "' AND uuid=?", new String[] {s});
				}
			}
			// Insert the UUIDs that are not present in the local database
			for (String s : userUuidsFromServer)
			{
				if (searchArray (userUuids, s) == -1)
				{
					response = client.clientGet (httpsUri + USER + "/" + s + "?v=custom:(" + Users.FIELDS + ")");
					try
					{
						jsonObj = JSONParser.getJSONObject (response);
						Users user = Users.parseJSONObject (jsonObj);
						values = new ContentValues ();
						values.put ("type", USER);
						values.put ("name", user.getUsername ());
						values.put ("uuid", user.getUuid ());
						values.put ("description", jsonObj.toString ());
						util.insert (METADATA_TABLE, values);
					}
					catch (Exception e)
					{
						Log.e (TAG, e.getMessage ());
					}
				}
			}
		}
	}

	/**
	 * Warning! This must be called after updating users
	 * 
	 * @throws JSONException
	 */
	public void fillProvidersMetadata () throws JSONException
	{
		/***
		 * Providers are person UUIDs because of the OpenMRS RESTWS structure.
		 * So we iterate all Users and check if their respective Person UUIDs
		 * are saved as Provider UUIDs
		 */
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		// Get all user names
		String[] users = util.getColumnData (METADATA_TABLE, "name", "type='" + USER + "' and name<>'daemon'", false);
		// Remove the providers without UUIDs or not in users
		util.delete (METADATA_TABLE, "type='" + PROVIDER + "' and (uuid is null or uuid='')", null);
		// Get all provider UUIDs from database
		String[] providers = util.getColumnData (METADATA_TABLE, "name", "type='" + PROVIDER + "' and uuid is not null and uuid<>''", false);
		// For all user UUIDs that are missing from provider UUIDs, fetch full
		// User object
		for (String user : users)
		{
			boolean exists = false;
			for (String provider : providers)
			{
				if (provider.equals (user))
					exists = true;
			}
			if (!exists)
			{
				// Retrieve Person object from User's JSON object
				response = client.clientGet (httpsUri + "user/" + getUuid (USER, user));
				if (response != null)
				{
					// Search for UUID
					jsonObj = JSONParser.getJSONObject (response);
					String personJson = jsonObj.getString ("person");
					String uuid = JSONParser.getJSONObject (personJson).getString ("uuid");
					// Save provider
					values = new ContentValues ();
					values.put ("type", PROVIDER);
					values.put ("name", user);
					values.put ("uuid", uuid);
					util.insert (METADATA_TABLE, values);
				}
			}
		}
	}

	public void fillLocationsMetadata () throws JSONException
	{
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		JSONObject[] jsonObjects = new JSONObject[0];
		// Get all location UUIDs from database
		String[] locationUuids = util.getColumnData (METADATA_TABLE, "uuid", "type='" + LOCATION + "'", false);
		ArrayList<String> locationUuidsFromServer = new ArrayList<String> ();
		// Get UUID of all locations from server
		response = client.clientGet (httpsUri + LOCATION + "?v=custom:(uuid)");
		if (response != null)
		{
			jsonObj = JSONParser.getJSONObject (response);
			jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
			values = new ContentValues ();
			for (JSONObject j : jsonObjects)
			{
				locationUuidsFromServer.add (j.getString ("uuid"));
			}
			// Delete the UUIDs that are present in local database and not in
			// server
			for (String s : locationUuids)
			{
				if (searchArray (locationUuidsFromServer.toArray (), s) == -1)
				{
					util.delete (METADATA_TABLE, "type='" + LOCATION + "' AND uuid=?", new String[] {s});
				}
			}
			// Insert the UUIDs that are not present in the local database
			for (String s : locationUuidsFromServer)
			{
				if (searchArray (locationUuids, s) == -1)
				{
					response = client.clientGet (httpsUri + LOCATION + "/" + s + "?v=custom:(" + Location.FIELDS + ")");
					jsonObj = JSONParser.getJSONObject (response);
					Location location = Location.parseJSONObject (jsonObj);
					values = new ContentValues ();
					values.put ("type", LOCATION);
					values.put ("name", location.getName ());
					values.put ("uuid", location.getUuid ());
					values.put ("description", jsonObj.toString ());
					util.insert (METADATA_TABLE, values);
				}
			}
		}
	}

	public void fillEncounterTypesMetadata () throws JSONException
	{
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		JSONObject[] jsonObjects = new JSONObject[0];
		// Get all Encounter Type UUIDs from database
		String[] encounterTypeUuids = util.getColumnData (METADATA_TABLE, "uuid", "type='" + ENCOUNTER_TYPE + "'", false);
		ArrayList<String> encounterTypeUuidsFromServer = new ArrayList<String> ();
		// Get UUID of all Encounter Types from server
		response = client.clientGet (httpsUri + ENCOUNTER_TYPE + "?v=custom:(uuid)");
		if (response != null)
		{
			jsonObj = JSONParser.getJSONObject (response);
			jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
			values = new ContentValues ();
			for (JSONObject j : jsonObjects)
			{
				encounterTypeUuidsFromServer.add (j.getString ("uuid"));
			}
			// Delete the UUIDs that are present in local database and not in
			// server
			for (String s : encounterTypeUuids)
			{
				if (searchArray (encounterTypeUuidsFromServer.toArray (), s) == -1)
				{
					util.delete (METADATA_TABLE, "type='" + ENCOUNTER_TYPE + "' AND uuid=?", new String[] {s});
				}
			}
			// Insert the UUIDs that are not present in the local database
			for (String s : encounterTypeUuidsFromServer)
			{
				if (searchArray (encounterTypeUuids, s) == -1)
				{
					response = client.clientGet (httpsUri + ENCOUNTER_TYPE + "/" + s + "?v=custom:(" + EncounterType.FIELDS + ")");
					jsonObj = JSONParser.getJSONObject (response);
					EncounterType encounterType = EncounterType.parseJSONObject (jsonObj);
					values = new ContentValues ();
					values.put ("type", ENCOUNTER_TYPE);
					values.put ("name", encounterType.getName ());
					values.put ("uuid", encounterType.getUuid ());
					values.put ("description", jsonObj.toString ());
					util.insert (METADATA_TABLE, values);
				}
			}
		}
	}

	public void fillConceptsMetadata () throws JSONException
	{
		ContentValues values;
		String response = "";
		JSONObject jsonObj = new JSONObject ();
		JSONObject[] jsonObjects = new JSONObject[0];
		// Get all Concept UUIDs from database
		String[] conceptTypeUuids = util.getColumnData (METADATA_TABLE, "uuid", "type='" + CONCEPT + "'", false);
		ArrayList<String> conceptTypeUuidsFromServer = new ArrayList<String> ();
		// Get UUID of all Concepts from server
		response = client.clientGet (httpsUri + "concept?v=custom:(uuid)");
		if (response != null)
		{
			jsonObj = JSONParser.getJSONObject (response);
			jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
			values = new ContentValues ();
			for (JSONObject j : jsonObjects)
			{
				conceptTypeUuidsFromServer.add (j.getString ("uuid"));
			}
			// Delete the UUIDs that are present in local database and not in
			// server
			for (String s : conceptTypeUuids)
			{
				if (searchArray (conceptTypeUuidsFromServer.toArray (), s) == -1)
				{
					util.delete (METADATA_TABLE, "type='" + CONCEPT + "' AND uuid=?", new String[] {s});
				}
			}
			// Insert the UUIDs that are not present in the local database
			for (String s : conceptTypeUuidsFromServer)
			{
				if (searchArray (conceptTypeUuids, s) == -1)
				{
					response = client.clientGet (httpsUri + "concept/" + s + "?v=custom:(" + Concept.FIELDS + ")");
					jsonObj = JSONParser.getJSONObject (response);
					Concept concept = Concept.parseJSONObject (jsonObj);
					values = new ContentValues ();
					values.put ("type", CONCEPT);
					values.put ("name", concept.getName ());
					values.put ("uuid", concept.getUuid ());
					values.put ("description", jsonObj.toString ());
					util.insert (METADATA_TABLE, values);
				}
			}
		}
		// Get description and insert into concepts table
		try
		{
			String[][] concepts = util.getTableData ("select uuid, name from " + METADATA_TABLE + " where type='" + CONCEPT + "' and name not in (select name from concepts)");
			for (int i = 0; i < concepts.length; i++)
			{
				response = client.clientGet (httpsUri + "concept/" + concepts[i][0] + "/description?v=custom:(description,locale)");
				jsonObj = JSONParser.getJSONObject (response);
				JSONArray jsonArray = jsonObj.getJSONArray ("results");
				for (int j = 0; j < jsonArray.length (); j++)
				{
					JSONObject description = jsonArray.getJSONObject (j);
					values = new ContentValues ();
					values.put ("name", concepts[i][1]);
					values.put ("lang", description.getString ("locale"));
					values.put ("description", description.getString ("description"));
					util.insert ("concepts", values);
				}
			}
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
		}
	}

	/**
	 * Search and return index of a value in an array. Returns -1 if value not
	 * found.
	 * 
	 * @param array
	 * @param element
	 * @return
	 */
	public static int searchArray (Object[] array, Object element)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i].equals (element))
				return i;
		}
		return -1;
	}
}
