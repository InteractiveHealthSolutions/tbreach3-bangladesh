/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class handles all mobile form requests to the server
 */

package org.irdresearch.tbr3mobile.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import org.irdresearch.tbr3mobile.App;
import org.irdresearch.tbr3mobile.R;
import org.irdresearch.tbr3mobile.model.Location;
import org.irdresearch.tbr3mobile.model.OpenMrsObject;
import org.irdresearch.tbr3mobile.model.Users;
import org.irdresearch.tbr3mobile.shared.FormType;
import org.irdresearch.tbr3mobile.shared.Metadata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ServerService
{
	private static final String	TAG	= "ServerService";
	private static String		httpsUri;
	private static MetadataUtil	mdUtil;
	private static DatabaseUtil	dbUtil;
	private HttpRequest			httpClient;
	private HttpsClient			httpsClient;
	private Context				context;
	
	
	private static String		tbr3Uri;

	public ServerService (Context context)
	{	
		this.context = context;
		String prefix = "http" + (App.isUseSsl () ? "s" : "") + "://";
		tbr3Uri = prefix + App.getServer () + "/tbreach3web";
		httpClient = new HttpRequest (this.context);
		httpsClient = new HttpsClient (this.context);
		dbUtil = new DatabaseUtil (this.context);
	}

	/**
	 * Returns true/false after checking if the user in App variable exists in
	 * the local database. If not found, it searches for the user in the Server.
	 * 
	 * @return status
	 */
	public boolean authenticate ()
	{
		return (getUser (App.getUsername ()) != null);
	}
	
	
	/**
	 * Checks to see if the client is connected to any network (GPRS/Wi-Fi)
	 * 
	 * @return status
	 */
	public boolean checkInternetConnection ()
	{
		boolean status = false;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo ();
		if (netInfo != null && netInfo.isConnectedOrConnecting ())
		{
			status = true;
		}
		return status;
	}
	
	public String post (String json)
	{
		String response = null;
		try
		{
			if (App.isUseSsl ())
				response = httpsClient.clientPost (tbr3Uri + json, null);
			else
				response = httpClient.clientPost (tbr3Uri + json, null);
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
		}
		return response;
	}

	public String get (String uri)
	{
		String response = null;
		if (App.isUseSsl ())
			response = httpsClient.clientGet (tbr3Uri + uri);
		else
			response = httpClient.clientGet (tbr3Uri + uri);
		return response;
	}

	public void setCurrentUser (String userName)
	{
		String id = dbUtil.getObject (Metadata.METADATA_TABLE, "id", "type='" + Metadata.USER + "' AND name='" + userName + "'");
		if (id != null)
			App.setCurrentUser (getOpenMrsObjectFromDb (Metadata.USER, userName));
	}

	public OpenMrsObject getLocation (String name)
	{
		String id = dbUtil.getObject (Metadata.METADATA_TABLE, "id", "type='" + Metadata.LOCATION + "' and name like '%" + name + "%'");
		OpenMrsObject location = null;
		// If not found, fetch from server and save this user
		if (id == null)
		{
			try
			{
				JSONObject json = new JSONObject ();
				json.put ("app_ver", App.getVersion ());
				json.put ("form_name", FormType.GET_LOCATION);
				json.put ("location_name", name);
				String response = get ("?content=" + JsonUtil.getEncodedJson (json));
				if (response != null)
				{
					JSONObject locationObj = JsonUtil.getJSONObject (response);
					ContentValues values = new ContentValues ();
					name = locationObj.getString ("name");
					// If location is found, then save it into local DB
					values = new ContentValues ();
					values.put ("id", locationObj.getInt ("id"));
					values.put ("type", Metadata.LOCATION);
					values.put ("name", name);
					dbUtil.insert (Metadata.METADATA_TABLE, values);
				}
			}
			catch (JSONException e)
			{
				Log.e (TAG, e.getMessage ());
			}
			catch (UnsupportedEncodingException e)
			{
				Log.e (TAG, e.getMessage ());
			}
		}
		location = getOpenMrsObjectFromDb (Metadata.LOCATION, name);
		return location;
	}

	public OpenMrsObject[] getLocations ()
	{
		ArrayList<OpenMrsObject> locations = new ArrayList<OpenMrsObject> ();
		String[][] tableData = dbUtil.getTableData ("select id, name from " + Metadata.METADATA_TABLE + " where type='" + Metadata.LOCATION + "'");
		for (int i = 0; i < tableData.length; i++)
		{
			OpenMrsObject location = new OpenMrsObject (tableData[i][0], Metadata.LOCATION, tableData[i][1]);
			locations.add (location);
		}
		return locations.toArray (new OpenMrsObject[] {});
	}
	
	
	/**
	 * Returns patient's DB Id using Patient Identifier
	 * 
	 * @param patientId
	 * @return
	 */
	public String getPatientId (String patientId)
	{
		try
		{
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", FormType.GET_PATIENT);
			json.put ("patient_id", patientId);
			String response = get ("?content=" + JsonUtil.getEncodedJson (json));
			JSONObject jsonResponse = JsonUtil.getJSONObject (response);
			if (response != null)
			{
				if (jsonResponse == null)
				{
					return null;
				}
				if (jsonResponse.has ("id"))
				{
					return jsonResponse.getString ("id");
				}
				return null;
			}
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
		}
		return null;
	}


	/**
	 * Returns list of Patients matching the parameter(s) from the server
	 * 
	 * @param fullName
	 * @param gender
	 * @param ageStart
	 * @param ageEnd
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String[] searchPatient (String fullName, String gender, int ageStart, int ageEnd) throws UnsupportedEncodingException
	{
		ArrayList<String> patients = new ArrayList<String> ();
		String query = URLEncoder.encode (fullName.trim (), "utf-8");
		String response = get (httpsUri + "patient?q=" + query + "&v=full");
		if (response == null)
			return null;
		JSONObject json = JSONParser.getJSONObject (response);
		JSONObject[] patientList = JSONParser.getJSONArrayFromObject (json, "results");
		for (JSONObject obj : patientList)
		{
			try
			{
				String display = obj.get ("display").toString ();
				JSONObject person = obj.getJSONObject ("person");
				int age = Integer.parseInt (person.get ("age").toString ());
				String gen = person.get ("gender").toString ();
				if (gen.equals (gender) && age >= ageStart && age <= ageEnd)
				{
					patients.add (display);
				}
			}
			catch (JSONException e)
			{
			}
		}
		return patients.toArray (new String[] {});
	}
	
	public OpenMrsObject getUser (String name)
	{
		OpenMrsObject user = null;
		// Always fetch from server and save this user
		try
		{
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", FormType.GET_USER);
			json.put ("username", name);
			String response = get ("?content=" + JsonUtil.getEncodedJson (json));
			if (response != null)
			{
				JSONObject userObj = JsonUtil.getJSONObject (response);
				ContentValues values = new ContentValues ();
				String userName = userObj.getString ("name");
				// If user is found, then save it into local DB
				if (userName.equalsIgnoreCase (App.getUsername ()))
				{
					values = new ContentValues ();
					values.put ("id", userObj.getInt ("id"));
					values.put ("type", Metadata.USER);
					values.put ("name", userName);
					// If the user doesn't exist in DB, save it
					String id = dbUtil.getObject (Metadata.METADATA_TABLE, "id", "type='" + Metadata.USER + "' AND name='" + name + "'");
					if (id == null)
					{
						dbUtil.insert (Metadata.METADATA_TABLE, values);
					}
					user = getOpenMrsObjectFromDb (Metadata.USER, name);
				}
			}
			else
			{
				return null;
			}
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e (TAG, e.getMessage ());
		}
		return user;
	}
	
	/**
	 * Returns single Object from DB by building a query on the basis of type
	 * and name provided. For multiple records, only first one will be returned
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	private OpenMrsObject getOpenMrsObjectFromDb (String type, String name)
	{
		String[] record = dbUtil.getRecord ("select id, type, name from " + Metadata.METADATA_TABLE + " where type='" + type + "' and name like '%" + name + "%'");
		if (record.length == 0)
		{
			return null;
		}
		OpenMrsObject openMrsObject = new OpenMrsObject (record[0], record[1], record[2]);
		return openMrsObject;
	}
	
	public String saveScreening (String encounterType, ContentValues values, String[][] observations)
	{
		String response = "";
		// Demographics
		String givenName = TextUtil.capitalizeFirstLetter (values.getAsString ("firstName"));
		String familyName = TextUtil.capitalizeFirstLetter (values.getAsString ("lastName"));
		int age = values.getAsInteger ("age");
		String gender = values.getAsString ("gender");
		String patientId = values.getAsString ("patientId");
		String location = values.getAsString ("location");
		String formDate = values.getAsString ("formDate");
		try
		{
			String id = getPatientId (patientId);
			if (id != null)
				return context.getResources ().getString (R.string.duplication);
			// Save Patient
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", encounterType);
			json.put ("username", App.getUsername ());
			json.put ("patient_id", patientId);
			json.put ("given_name", givenName);
			json.put ("family_name", familyName);
			json.put ("gender", gender);
			json.put ("age", age);
			json.put ("location", location);
			JSONArray obs = new JSONArray ();
			for (int i = 0; i < observations.length; i++)
			{
				if ("".equals (observations[i][0]) || "".equals (observations[i][1]))
					continue;
				JSONObject obsJson = new JSONObject ();
				obsJson.put ("concept", observations[i][0]);
				obsJson.put ("value", observations[i][1]);
				obs.put (obsJson);
			}
			json.put ("encounter_type", encounterType);
			json.put ("form_date", formDate);
			json.put ("encounter_location", location);
			json.put ("provider", App.getUsername ());
			json.put ("obs", obs.toString ());
			
			response = post ("?content=" + JsonUtil.getEncodedJson (json));
			JSONObject jsonResponse = JsonUtil.getJSONObject (response);
			if (jsonResponse == null)
			{
				return response;
			}
			if (jsonResponse.has ("result"))
			{
				String result = jsonResponse.getString ("result");
				return result;
			}
			return response;
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.invalid_data);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.unknown_error);
		}
		return response;
	}


	public String saveCustomerInfo (String encounterType, ContentValues values)
	{
		String response = "";
		String encounterDateTime = values.getAsString ("formDate");
		String patientId = values.getAsString ("patientId");
		String address1 = values.getAsString ("address1");
		String address2 = values.getAsString ("address2");
		String cityVillage = values.getAsString ("city");
		String country = values.getAsString ("country");
		String district = values.getAsString ("district");
		String area = values.getAsString ("state");
		String town = values.getAsString ("town");
		String location = values.getAsString ("location");
		String phone1 = values.getAsString ("phone1");
		String phone2 = values.getAsString ("phone2");
		String phone1Owner = values.getAsString ("phone1Owner");
		String phone2Owner = values.getAsString ("phone2Owner");
		
		try
		{
			// Check if the patient Id exists
			String id = getPatientId (patientId);
			if (id == null)
				return context.getResources ().getString (R.string.patient_id_missing);
			// Form JSON
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", encounterType);
			json.put ("username", App.getUsername ());
			json.put ("patient_id", patientId);
			json.put ("location", location);
			json.put ("encounter_type", encounterType);
			json.put ("form_date", encounterDateTime);
			json.put ("encounter_location", location);
			json.put ("provider", App.getUsername ());
			// Add address
			json.put ("address1", address1);
			json.put ("address2", address2);
			json.put ("cityVillage", cityVillage);
			json.put ("country", country);
			json.put ("countyDistrict", district);
			json.put ("stateProvince", area);
			json.put ("address4", town);
			// Add contacts as array of person attributes
			JSONArray attributes = new JSONArray ();
			JSONObject attributeJson = new JSONObject ();
			if (phone1 != null || "".equals (phone1))
			{
				attributeJson.put ("attribute", "Primary Mobile");
				attributeJson.put ("value", phone1);
				attributes.put (attributeJson);
			}
			if (phone2 != null || "".equals (phone2))
			{
				attributeJson = new JSONObject ();
				attributeJson.put ("attribute", "Secondary Mobile");
				attributeJson.put ("value", phone2);
				attributes.put (attributeJson);
			}
			if (phone1Owner != null || "".equals (phone1Owner))
			{
				attributeJson = new JSONObject ();
				attributeJson.put ("attribute", "Primary Mobile Owner");
				attributeJson.put ("value", phone1Owner);
				attributes.put (attributeJson);
			}
			if (phone2Owner != null || "".equals (phone2Owner))
			{
				attributeJson = new JSONObject ();
				attributeJson.put ("attribute", "Secondary Mobile Owner");
				attributeJson.put ("value", phone2Owner);
				attributes.put (attributeJson);
			}
			json.put ("attributes", attributes.toString ());
			response = post ("?content=" + JsonUtil.getEncodedJson (json));
			JSONObject jsonResponse = JsonUtil.getJSONObject (response);
			if (jsonResponse == null)
			{
				return response;
			}
			if (jsonResponse.has ("result"))
			{
				String result = jsonResponse.getString ("result");
				return result;
			}
			return response;
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.invalid_data);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.unknown_error);
		}
		
		return response;	
	}

	
	public String saveTestIndication (String encounterType, ContentValues values, String[][] observations)
	{
		String response = "";
		String patientId = values.getAsString ("patientId");
		String location = values.getAsString ("location");
		String formDate = values.getAsString ("formDate");
		try
		{
			String id = getPatientId (patientId);
			if (id == null)
				return context.getResources ().getString (R.string.patient_id_missing);
			// Save Patient
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", encounterType);
			json.put ("username", App.getUsername ());
			json.put ("patient_id", patientId);
			json.put ("location", location);
			JSONArray obs = new JSONArray ();
			for (int i = 0; i < observations.length; i++)
			{
				if ("".equals (observations[i][0]) || "".equals (observations[i][1]))
					continue;
				JSONObject obsJson = new JSONObject ();
				obsJson.put ("concept", observations[i][0]);
				obsJson.put ("value", observations[i][1]);
				obs.put (obsJson);
			}
			json.put ("encounter_type", encounterType);
			json.put ("form_date", formDate);
			json.put ("encounter_location", location);
			json.put ("provider", App.getUsername ());
			json.put ("obs", obs.toString ());
			response = post ("?content=" + JsonUtil.getEncodedJson (json));
			JSONObject jsonResponse = JsonUtil.getJSONObject (response);
			if (jsonResponse == null)
			{
				return response;
			}
			if (jsonResponse.has ("result"))
			{
				String result = jsonResponse.getString ("result");
				return result;
			}
			return response;
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.invalid_data);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.unknown_error);
		}
		return response;
	}

	public String saveFeedback (String encounterType, ContentValues values)
	{
		
		String response = "";
		try
		{
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", encounterType);
			json.put ("username", App.getUsername ());
			json.put ("location", values.getAsString ("location"));
			json.put ("feedback_type", values.getAsString ("feedbackType"));
			json.put ("feedback", values.getAsString ("feedback"));
			response = post ("?content=" + JsonUtil.getEncodedJson (json));
			JSONObject jsonResponse = JsonUtil.getJSONObject (response);
			if (jsonResponse.has ("result"))
			{
				String result = jsonResponse.getString ("result");
				return result;
			}
			else
			{
				return response;
			}
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
		}
		return response;
	}

	/**
	 * Saves non-suspect information to the other web service (e.g. tbr3web)
	 * 
	 * @param encounterType
	 * @param values
	 * @return
	 */
	public String saveNonSuspect (String encounterType, ContentValues values)
	{
		String response = "";
		try
		{
			JSONObject json = new JSONObject ();
			json.put ("app_ver", App.getVersion ());
			json.put ("form_name", encounterType);
			json.put ("age", values.getAsString ("age"));
			json.put ("gender", values.getAsString ("gender"));
			json.put ("weight", values.getAsString ("weight"));
			json.put ("height", values.getAsString ("height"));
			json.put ("location", values.getAsString ("location"));
			json.put ("username", App.getUsername ());
			json.put ("formdate", values.getAsString ("formDate"));
			
			response = post ("?content=" + JsonUtil.getEncodedJson (json));
			JSONObject jsonResponse = JsonUtil.getJSONObject (response);
			if (jsonResponse.has ("result"))
			{
				String result = jsonResponse.getString ("result");
				return result;
			}
			else
			{
				return response;
			}
		}
		catch (NotFoundException e)
		{
			Log.d (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.empty_data);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.d (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.unknown_error);
		}
		catch (JSONException e)
		{
			Log.d (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.invalid_data);
		}
		return response;
	}
}
