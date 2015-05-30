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
import org.irdresearch.tbr3mobile.model.Users;
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

	public ServerService (Context context)
	{
		this.context = context;
		// Specify REST module link
		httpsUri = App.getServer () + "/ws/rest/v1/";
		httpClient = new HttpRequest (this.context);
		httpsClient = new HttpsClient (this.context);
		mdUtil = new MetadataUtil (this.context);
		dbUtil = new DatabaseUtil (this.context);
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

	public String post (String entity, String json)
	{
		String response = null;
		if (App.isUseSsl ())
			response = httpsClient.clientPost (entity, json);
		else
			response = httpClient.clientPost (entity, json);
		return response;
	}

	public String get (String uri)
	{
		String response = null;
		if (App.isUseSsl ())
			response = httpsClient.clientGet (uri);
		else
			response = httpClient.clientGet (uri);
		return response;
	}

	/**
	 * Gets username from App variable and checks to see if it exists in the
	 * local database. The method doesn't exactly matches the user but attempts
	 * to see if the call is authenticated on the server
	 * 
	 * @param userName
	 * @return status
	 */
	public boolean checkOrGetCurrentUser ()
	{
		// Search for UUID in local database first
		String description = dbUtil.getObject (MetadataUtil.METADATA_TABLE, "description", "type='" + MetadataUtil.USER + "' AND name='" + App.getUsername () + "'");
		boolean status = false;
		// If not found, fetch from server and save this user
		if (description == null)
		{
			String response = get (httpsUri + "user?v=custom:(username,uuid)&q=" + App.getUsername ());
			if (response != null)
			{
				try
				{
					JSONObject jsonObj = JSONParser.getJSONObject (response);
					JSONObject[] jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
					ContentValues values = new ContentValues ();
					for (JSONObject j : jsonObjects)
					{
						String userName = j.getString ("username");
						// If user is found, then save it into local DB
						if (userName.equalsIgnoreCase (App.getUsername ()))
						{
							Users user = Users.parseJSONObject (j);
							values = new ContentValues ();
							values.put ("type", MetadataUtil.USER);
							values.put ("name", user.getUsername ());
							values.put ("uuid", user.getUuid ());
							values.put ("description", j.toString ());
							dbUtil.insert (MetadataUtil.METADATA_TABLE, values);
							// Retrieve Person object from User's JSON object
							response = get (httpsUri + "user/" + user.getUuid ());
							if (response != null)
							{
								// Search for UUID
								jsonObj = JSONParser.getJSONObject (response);
								String personJson = jsonObj.getString ("person");
								String uuid = JSONParser.getJSONObject (personJson).getString ("uuid");
								// Save provider
								values = new ContentValues ();
								values.put ("type", MetadataUtil.PROVIDER);
								values.put ("name", userName);
								values.put ("uuid", uuid);
								dbUtil.insert (MetadataUtil.METADATA_TABLE, values);
							}
							return true;
						}
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace ();
				}
			}
			return status;
		}
		Users user = Users.parseJSONObject (JSONParser.getJSONObject (description));
		String response = get (httpsUri + "user/" + user.getUuid ());
		if (response != null)
		{
			status = true;
		}
		return status;
	}

	public void setCurrentUser (String userName)
	{
		String description = dbUtil.getObject (MetadataUtil.METADATA_TABLE, "description", "type='" + MetadataUtil.USER + "' AND name='" + userName + "'");
		if (description != null)
			App.setCurrentUser (Users.parseJSONObject (JSONParser.getJSONObject (description)));
		else
			App.setCurrentUser (new Users ("", userName));
	}

	public void setCurrentLocation (String locationName)
	{
		String description = dbUtil.getObject (MetadataUtil.METADATA_TABLE, "description", "type='" + MetadataUtil.LOCATION + "' AND name='" + locationName + "'");
		if (description != null)
			App.setCurrentLocation (Location.parseJSONObject (JSONParser.getJSONObject (description)));
		else
			App.setCurrentLocation (new Location ("", locationName));
	}

	public Location getLocation  (String locationId)
	{
		Object locationObj = dbUtil.getObject (MetadataUtil.METADATA_TABLE, "description", "type='" + MetadataUtil.LOCATION + "' and name like '%" + locationId + "%'");
		Location location = null;
		// If not found, fetch from server and save this user
		if (locationObj == null)
		{
			App.setThreadSafety (false);
			String response = get (httpsUri + "location?v=custom:(username,uuid)&q=" + locationId);
			App.setThreadSafety (true);
			if (response != null)
			{
				try
				{
					JSONObject jsonObj = JSONParser.getJSONObject (response);
					JSONObject[] jsonObjects = JSONParser.getJSONArrayFromObject (jsonObj, "results");
					ContentValues values = new ContentValues ();
					for (JSONObject j : jsonObjects)
					{
						// If location is found, then save it into local DB
						location = Location.parseJSONObject (j);
						values = new ContentValues ();
						values.put ("type", MetadataUtil.LOCATION);
						values.put ("name", location.getName ());
						values.put ("uuid", location.getUuid ());
						values.put ("description", j.toString ());
						dbUtil.insert (MetadataUtil.METADATA_TABLE, values);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		}
		else
		{
			location = Location.parseJSONObject (JSONParser.getJSONObject (locationObj.toString ()));
		}
		return location;
	}

	public Location[] getLocations ()
	{
		ArrayList<Location> locations = new ArrayList<Location> ();
		String[] columnData = dbUtil.getColumnData (MetadataUtil.METADATA_TABLE, "name", "type='" + MetadataUtil.LOCATION + "'", true);
		for (String s : columnData)
		{
			JSONObject json = JSONParser.getJSONObject (mdUtil.getMetaDescription (MetadataUtil.LOCATION, s));
			Location location = Location.parseJSONObject (json);
			locations.add (location);
		}
		return locations.toArray (new Location[] {});
	}

	/**
	 * Returns UUID of the Patient Identifier Type from local database
	 * 
	 * @return
	 */
	public String getIdentifierTypeUuid ()
	{
		String uuid = dbUtil.getObject (MetadataUtil.METADATA_TABLE, "uuid", "name='OpenMRS Identification Number'");
		return uuid;
	}

	public String getPatientUuid (String patientId)
	{
		String uuid = null;
		try
		{
			String response = get (httpsUri + "patient?v=custom:(uuid)&q=" + patientId);
			JSONObject json = JSONParser.getJSONObject (response);
			JSONObject[] uuids = JSONParser.getJSONArrayFromObject (json, "results");
			if (uuids.length > 0)
				uuid = uuids[0].getString ("uuid");
		}
		catch (Exception e)
		{
		}
		return uuid;
	}

	/**
	 * Returns list of Patients matching the parameter(s) from the server
	 * 
	 * @param patientId
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String[] searchPatient (String patientId) throws UnsupportedEncodingException
	{
		ArrayList<String> patients = new ArrayList<String> ();
		String query = URLEncoder.encode (patientId.trim (), "utf-8");
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
				patients.add (display);
			}
			catch (JSONException e)
			{
			}
		}
		return patients.toArray (new String[] {});
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

	public boolean saveLocalForm (ContentValues formValues)
	{
		return dbUtil.insert ("forms", formValues);
	}
	
	public String[][] getSavedForms ()
	{
		String[][] forms = dbUtil.getTableData ("forms", "form_name, patient_id", "");
		return forms;
	}
	
	public String[][] getLocalForms ()
	{   
		
			return dbUtil.getTableData ("select form_name, patient_id, form_date from forms");
		
	}

	public String saveScreening (String encounterType, ContentValues values, String[][] observations)
	{
		String response = "";
		// Demographics
		String givenName = values.getAsString ("firstName");
		String familyName = values.getAsString ("lastName");
		int age = values.getAsInteger ("age");
		String gender = values.getAsString ("gender");
		String patientId = values.getAsString ("patientId");
		String location = values.getAsString ("location");
		String encounterDateTime = values.getAsString ("formDate");
		try
		{
			String uuid = getPatientUuid (patientId);
			if (uuid != null)
				return context.getResources ().getString (R.string.duplication);
			// Save person
			JSONObject person = new JSONObject ();
			JSONArray names = new JSONArray ();
			JSONObject name = new JSONObject ();
			name.put ("givenName", givenName);
			name.put ("familyName", familyName);
			names.put (name);
			person.put ("gender", gender);
			person.put ("age", age);
			person.put ("names", names);
			response = post (httpsUri + "person", person.toString ());
			// Fetch the JSON object and UUID from newly created patient
			// Enclosing brackets are necessary
			JSONObject newPerson = JSONParser.getJSONObject ("{" + response + "}");
			// Add Identifier
			JSONArray identifiers = new JSONArray ();
			JSONObject identifier = new JSONObject ();
			identifier.put ("identifier", patientId);
			identifier.put ("identifierType", getIdentifierTypeUuid ());
			identifier.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
			identifier.put ("preferred", "true");
			identifiers.put (identifier);
			// Save patient
			JSONObject patient = new JSONObject ();
			patient.put ("person", newPerson.get ("uuid"));
			patient.put ("identifiers", identifiers);
			response = post (httpsUri + "patient", patient.toString ());
			// Save encounter
			StringBuilder obs = new StringBuilder ();
			try
			{
				response = "SUCCESS";
				obs.append ("\"obs\":[");
				for (int i = 0; i < observations.length; i++)
				{
					if ("".equals (observations[i][0]) || "".equals (observations[i][1]))
						continue;
					String conceptUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][0]);
					if (conceptUuid == null)
					{
						response = context.getResources ().getString (R.string.concept_not_found);
						break;
					}
					String valueUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][1]);
					obs.append ("{\"concept\":\"" + conceptUuid + "\",\"value\":\"" + (valueUuid == null ? observations[i][1] : valueUuid) + "\"}");
					if (i != observations.length - 1)
						obs.append (",");
				}
				obs.append ("]");
				if (response == "SUCCESS")
				{
					JSONObject encounter = new JSONObject ();
					encounter.put ("encounterType", mdUtil.getUuid (MetadataUtil.ENCOUNTER_TYPE, encounterType));
					encounter.put ("patient", newPerson.get ("uuid"));
					encounter.put ("encounterDatetime", encounterDateTime);
					encounter.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
					encounter.put ("provider", mdUtil.getUuid (MetadataUtil.PROVIDER, App.getCurrentUser ().getUsername ()));
					String jsonString = encounter.toString ();
					jsonString = jsonString.substring (0, jsonString.length () - 1) + "," + obs + "}";
					response = post (httpsUri + "encounter", jsonString);
				}
				else
				{
					response = context.getResources ().getString (R.string.incomplete_operation);
				}
			}
			catch (JSONException e)
			{
				Log.e (TAG, e.getMessage ());
			}
			if (!response.contains ("error"))
			{
				// Save form in local repository
				ContentValues formValues = new ContentValues ();
				formValues.put ("form_name", encounterType);
				formValues.put ("username", App.getUsername ());
				formValues.put ("patient_id", patientId);
				formValues.put ("location", location);
				formValues.put ("form_date", encounterDateTime);
				formValues.put ("start_date", App.getSqlDateTime (new Date ()));
				formValues.put ("end_date", App.getSqlDateTime (new Date ()));
				formValues.put ("description", obs.toString ());
				dbUtil.insert ("forms", formValues);
				response = "SUCCESS";
			}
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
		}
		return response;
	}

	public String savePaediatricScreening (String encounterType, ContentValues values, String[][] observations)
	{
		String response = "";
		// Demographics
		String givenName = values.getAsString ("firstName");
		String familyName = values.getAsString ("lastName");
		int age = values.getAsInteger ("age");
		String birthDate = values.getAsString ("dob");
		String gender = values.getAsString ("gender");
		String patientId = values.getAsString ("patientId");
		String encounterDateTime = values.getAsString ("formDate");
		String location = values.getAsString ("location");
		try
		{
			String uuid = getPatientUuid (patientId);
			if (uuid != null)
				return context.getResources ().getString (R.string.duplication);
			// Save person
			JSONObject person = new JSONObject ();
			JSONArray names = new JSONArray ();
			JSONObject name = new JSONObject ();
			name.put ("givenName", givenName);
			name.put ("familyName", familyName);
			names.put (name);
			person.put ("gender", gender);
			person.put ("age", age);
			person.put ("birthdate", birthDate);
			person.put ("names", names);
			response = post (httpsUri + "person", person.toString ());
			// Fetch the JSON object and UUID from newly created patient
			// Enclosing brackets are necessary
			JSONObject newPerson = JSONParser.getJSONObject ("{" + response + "}");
			// Add Identifier
			JSONArray identifiers = new JSONArray ();
			JSONObject identifier = new JSONObject ();
			identifier.put ("identifier", patientId);
			identifier.put ("identifierType", getIdentifierTypeUuid ());
			identifier.put ("location", App.getCurrentLocation ().getUuid ());
			identifier.put ("preferred", "true");
			identifiers.put (identifier);
			// Save patient
			JSONObject patient = new JSONObject ();
			patient.put ("person", newPerson.get ("uuid"));
			patient.put ("identifiers", identifiers);
			response = post (httpsUri + "patient", patient.toString ());
			// Save encounter
			StringBuilder obs = new StringBuilder ();
			try
			{
				response = "SUCCESS";
				obs.append ("\"obs\":[");
				for (int i = 0; i < observations.length; i++)
				{
					if ("".equals (observations[i][0]) || "".equals (observations[i][1]))
						continue;
					String conceptUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][0]);
					if (conceptUuid == null)
					{
						response = context.getResources ().getString (R.string.concept_not_found);
						break;
					}
					String valueUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][1]);
					obs.append ("{\"concept\":\"" + conceptUuid + "\",\"value\":\"" + (valueUuid == null ? observations[i][1] : valueUuid) + "\"}");
					if (i != observations.length - 1)
						obs.append (",");
				}
				obs.append ("]");
				if (response == "SUCCESS")
				{
					JSONObject encounter = new JSONObject ();
					encounter.put ("encounterType", mdUtil.getUuid (MetadataUtil.ENCOUNTER_TYPE, encounterType));
					encounter.put ("patient", newPerson.get ("uuid"));
					encounter.put ("encounterDatetime", encounterDateTime);
					encounter.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
					encounter.put ("provider", mdUtil.getUuid (MetadataUtil.PROVIDER, App.getCurrentUser ().getUsername ()));
					String jsonString = encounter.toString ();
					jsonString = jsonString.substring (0, jsonString.length () - 1) + "," + obs + "}";
					response = post (httpsUri + "encounter", jsonString);
				}
				else
				{
					response = context.getResources ().getString (R.string.incomplete_operation);
				}
			}
			catch (JSONException e)
			{
				Log.e (TAG, e.getMessage ());
			}
			if (!response.contains ("error"))
			{
				ContentValues formValues = new ContentValues ();
				formValues.put ("form_name", encounterType);
				formValues.put ("username", App.getUsername ());
				formValues.put ("patient_id", "");
				formValues.put ("location", location);
				formValues.put ("form_date", encounterDateTime);
				formValues.put ("start_date", App.getSqlDateTime (new Date ()));
				formValues.put ("end_date", App.getSqlDateTime (new Date ()));
				formValues.put ("description", obs.toString ());
				saveLocalForm (formValues);
				response = "SUCCESS";
			}
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
		}
		return response;
	}

	public String saveNonPulmonarySuspect (String encounterType, ContentValues values, String[][] observations)
	{
		String response = "";
		// Demographics
		String givenName = values.getAsString ("firstName");
		String familyName = values.getAsString ("lastName");
		int age = values.getAsInteger ("age");
		String gender = values.getAsString ("gender");
		String patientId = values.getAsString ("patientId");
		String location = values.getAsString ("location");
		String encounterDateTime = values.getAsString ("formDate");
		try
		{
			String uuid = getPatientUuid (patientId);
			if (uuid != null)
				return context.getResources ().getString (R.string.duplication);
			// Save person
			JSONObject person = new JSONObject ();
			JSONArray names = new JSONArray ();
			JSONObject name = new JSONObject ();
			name.put ("givenName", givenName);
			name.put ("familyName", familyName);
			names.put (name);
			person.put ("gender", gender);
			person.put ("age", age);
			person.put ("names", names);
			response = post (httpsUri + "person", person.toString ());
			// Fetch the JSON object and UUID from newly created patient
			// Enclosing brackets are necessary
			JSONObject newPerson = JSONParser.getJSONObject ("{" + response + "}");
			// Add Identifier
			JSONArray identifiers = new JSONArray ();
			JSONObject identifier = new JSONObject ();
			identifier.put ("identifier", patientId);
			identifier.put ("identifierType", getIdentifierTypeUuid ());
			identifier.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
			identifier.put ("preferred", "true");
			identifiers.put (identifier);
			// Save patient
			JSONObject patient = new JSONObject ();
			patient.put ("person", newPerson.get ("uuid"));
			patient.put ("identifiers", identifiers);
			response = post (httpsUri + "patient", patient.toString ());
			// Save encounter
			StringBuilder obs = new StringBuilder ();
			try
			{
				response = "SUCCESS";
				obs.append ("\"obs\":[");
				for (int i = 0; i < observations.length; i++)
				{
					if ("".equals (observations[i][0]) || "".equals (observations[i][1]))
						continue;
					String conceptUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][0]);
					if (conceptUuid == null)
					{
						response = context.getResources ().getString (R.string.concept_not_found);
						break;
					}
					String valueUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][1]);
					obs.append ("{\"concept\":\"" + conceptUuid + "\",\"value\":\"" + (valueUuid == null ? observations[i][1] : valueUuid) + "\"}");
					if (i != observations.length - 1)
						obs.append (",");
				}
				obs.append ("]");
				if (response == "SUCCESS")
				{
					JSONObject encounter = new JSONObject ();
					// encounter.put ("obs", obsArray.toString ());
					encounter.put ("encounterType", mdUtil.getUuid (MetadataUtil.ENCOUNTER_TYPE, encounterType));
					encounter.put ("patient", newPerson.get ("uuid"));
					encounter.put ("encounterDatetime", encounterDateTime);
					encounter.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
					encounter.put ("provider", mdUtil.getUuid (MetadataUtil.PROVIDER, App.getCurrentUser ().getUsername ()));
					String jsonString = encounter.toString ();
					jsonString = jsonString.substring (0, jsonString.length () - 1) + "," + obs + "}";
					response = post (httpsUri + "encounter", jsonString);
				}
				else
				{
					response = context.getResources ().getString (R.string.incomplete_operation);
				}
			}
			catch (JSONException e)
			{
				Log.e (TAG, e.getMessage ());
			}
			if (!response.contains ("error"))
			{
				ContentValues formValues = new ContentValues ();
				formValues.put ("form_name", encounterType);
				formValues.put ("username", App.getUsername ());
				formValues.put ("patient_id", "");
				formValues.put ("location", location);
				formValues.put ("form_date", encounterDateTime);
				formValues.put ("start_date", App.getSqlDateTime (new Date ()));
				formValues.put ("end_date", App.getSqlDateTime (new Date ()));
				formValues.put ("description", obs.toString ());
				saveLocalForm (formValues);
				response = "SUCCESS";
			}
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
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
		try
		{
			// Fetch the respective person UUID from patient Id
			String uuid = getPatientUuid (patientId);
			if (uuid == null)
				return context.getResources ().getString (R.string.patient_id_missing);
			// Save encounter
			JSONObject encounter = new JSONObject ();
			encounter.put ("encounterType", mdUtil.getUuid (MetadataUtil.ENCOUNTER_TYPE, encounterType));
			encounter.put ("patient", uuid);
			encounter.put ("encounterDatetime", encounterDateTime);
			encounter.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
			encounter.put ("provider", mdUtil.getUuid (MetadataUtil.PROVIDER, App.getCurrentUser ().getUsername ()));
			response = post (httpsUri + "encounter", encounter.toString ());

			// TODO: Check if there is an error; respond accordingly
			JSONObject personAddress = new JSONObject ();
			personAddress.put ("address1", address1);
			personAddress.put ("address2", address2);
			personAddress.put ("address4", town);
			personAddress.put ("stateProvince", area);
			personAddress.put ("cityVillage", cityVillage);
			personAddress.put ("country", country);
			personAddress.put ("countyDistrict", district);
			response = post (httpsUri + "person/" + uuid + "/address", personAddress.toString ());
			// Update person's contacts
			JSONObject attribute = new JSONObject ();
			String phone1 = values.getAsString ("phone1");
			if (phone1 != null)
			{
				attribute = new JSONObject ();
				attribute.put ("attributeType", mdUtil.getUuid (MetadataUtil.PERSON_ATTRIBUTE_TYPE, "Primary Mobile"));
				attribute.put ("value", phone1);
				response = post (httpsUri + "person/" + uuid + "/attribute", attribute.toString ());
			}
			String phone2 = values.getAsString ("phone2");
			if (phone2 != null)
			{
				attribute = new JSONObject ();
				attribute.put ("attributeType", mdUtil.getUuid (MetadataUtil.PERSON_ATTRIBUTE_TYPE, "Secondary Mobile"));
				attribute.put ("value", phone2);
				response = post (httpsUri + "person/" + uuid + "/attribute", attribute.toString ());
			}
			String phone1Owner = values.getAsString ("phone1Owner");
			if (phone1Owner != null)
			{
				attribute = new JSONObject ();
				attribute.put ("attributeType", mdUtil.getUuid (MetadataUtil.PERSON_ATTRIBUTE_TYPE, "Primary Mobile Owner"));
				attribute.put ("value", phone1Owner);
				response = post (httpsUri + "person/" + uuid + "/attribute", attribute.toString ());
			}
			String phone2Owner = values.getAsString ("phone2Owner");
			if (phone2Owner != null)
			{
				attribute = new JSONObject ();
				attribute.put ("attributeType", mdUtil.getUuid (MetadataUtil.PERSON_ATTRIBUTE_TYPE, "Secondary Mobile Owner"));
				attribute.put ("value", phone2Owner);
				response = post (httpsUri + "person/" + uuid + "/attribute", attribute.toString ());
			}
			if (!response.contains ("error"))
			{
				ContentValues formValues = new ContentValues ();
				formValues.put ("form_name", encounterType);
				formValues.put ("username", App.getUsername ());
				formValues.put ("patient_id", patientId);
				formValues.put ("location", location);
				formValues.put ("form_date", encounterDateTime);
				formValues.put ("start_date", App.getSqlDateTime (new Date ()));
				formValues.put ("end_date", App.getSqlDateTime (new Date ()));
				formValues.put ("description", values.toString ());
				saveLocalForm (formValues);
				response = "SUCCESS";
			}
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
		}
		return response;
	}

	public String saveTestIndication (String encounterType, ContentValues values, String[][] observations)
	{
		String response = "";
		String patientId = values.getAsString ("patientId");
		String location = values.getAsString ("location");
		String encounterDateTime = values.getAsString ("formDate");
		try
		{
			String uuid = getPatientUuid (patientId);
			if (uuid == null)
				return context.getResources ().getString (R.string.patient_id_missing);
			// Save encounter
			response = "SUCCESS";
			StringBuilder obs = new StringBuilder ();
			obs.append ("\"obs\":[");
			for (int i = 0; i < observations.length; i++)
			{
				if ("".equals (observations[i][0]) || "".equals (observations[i][1]))
					continue;
				String conceptUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][0]);
				if (conceptUuid == null)
				{
					response = context.getResources ().getString (R.string.concept_not_found);
					break;
				}
				String valueUuid = mdUtil.getUuid (MetadataUtil.CONCEPT, observations[i][1]);
				obs.append ("{\"concept\":\"" + conceptUuid + "\",\"value\":\"" + (valueUuid == null ? observations[i][1] : valueUuid) + "\"}");
				if (i != observations.length - 1)
					obs.append (",");
			}
			obs.append ("]");
			if (response == "SUCCESS")
			{
				JSONObject encounter = new JSONObject ();
				encounter.put ("encounterType", mdUtil.getUuid (MetadataUtil.ENCOUNTER_TYPE, encounterType));
				encounter.put ("patient", uuid);
				encounter.put ("encounterDatetime", encounterDateTime);
				encounter.put ("location", mdUtil.getUuid (MetadataUtil.LOCATION, location));
				encounter.put ("provider", mdUtil.getUuid (MetadataUtil.PROVIDER, App.getCurrentUser ().getUsername ()));
				String jsonString = encounter.toString ();
				jsonString = jsonString.substring (0, jsonString.length () - 1) + "," + obs + "}";
				response = post (httpsUri + "encounter", jsonString);
			}
			else
			{
				response = "error:" + response;
			}
			if (!response.contains ("error"))
			{
				// Save form in local repository
				ContentValues formValues = new ContentValues ();
				formValues.put ("form_name", encounterType);
				formValues.put ("username", App.getUsername ());
				formValues.put ("patient_id", patientId);
				formValues.put ("location", location);
				formValues.put ("form_date", encounterDateTime);
				formValues.put ("start_date", App.getSqlDateTime (new Date ()));
				formValues.put ("end_date", App.getSqlDateTime (new Date ()));
				formValues.put ("description", obs.toString ());
				saveLocalForm (formValues);
				response = "SUCCESS";
			}
		}
		catch (JSONException e)
		{
			Log.e (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.insert_error);
		}
		return response;
	}

	public String saveFeedback (String encounterType, ContentValues values)
	{
		String response = "";
		String encounterDateTime = values.getAsString ("formDate");
		String location = values.getAsString ("location");
		String feedbackType = values.getAsString ("feedbackType");
		String feedback = values.getAsString ("feedback");
		// String provider = values.getAsString ("provider");
		try
		{
			JSONObject feedbackObj = new JSONObject ();
			feedbackObj.put ("encounterDatetime", encounterDateTime);
			feedbackObj.put ("location", location);
			feedbackObj.put ("feedbackType", feedbackType);
			feedbackObj.put ("feedback", feedback);
			response = "SUCCESS";
			if (!response.contains ("error"))
			{
				// Save form in local repository
				ContentValues formValues = new ContentValues ();
				formValues.put ("form_name", encounterType);
				formValues.put ("username", App.getUsername ());
				formValues.put ("patient_id", "");
				formValues.put ("location", location);
				formValues.put ("form_date", encounterDateTime);
				formValues.put ("start_date", App.getSqlDateTime (new Date ()));
				formValues.put ("end_date", App.getSqlDateTime (new Date ()));
				formValues.put ("description", "Feedback Type: " + feedbackType + ". Feedback: " + feedback);
				saveLocalForm (formValues);
				response = "SUCCESS";
			}
		}
		catch (JSONException e)
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
			JSONObject nonSuspect = new JSONObject ();
			nonSuspect.put ("form", "SCREENING");
			nonSuspect.put ("age", values.getAsString ("age"));
			nonSuspect.put ("gender", values.getAsString ("gender"));
			nonSuspect.put ("weight", values.getAsString ("weight"));
			nonSuspect.put ("height", values.getAsString ("height"));
			nonSuspect.put ("location", values.getAsString ("location"));
			nonSuspect.put ("username", App.getCurrentUser ().getUsername ());
			nonSuspect.put ("formdate", values.getAsString ("formDate"));
			//TODO Change to R.string.alternate_server_address when going live
			String serverAddress = App.getServer ();
			int i = serverAddress.lastIndexOf ("/");
			String nonSuspectServerAddress = serverAddress.substring (0, i+1) + "tbr3web/mobileservice/mobileservice";
			response = httpsClient.clientPost (nonSuspectServerAddress, nonSuspect.toString ());
			if (response.contains ("success"))
			{
				response = "SUCCESS";
			}
			else if (response.toString ().equals ("SERVER_NOT_RESPONDING"))
			{
				response = context.getResources ().getString (R.string.data_connection_error);
			}
			else
			{
				response = context.getResources ().getString (R.string.insert_error);
			}
		}
		catch (NotFoundException e)
		{
			Log.d (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.empty_data);
		}
		catch (JSONException e)
		{
			Log.d (TAG, e.getMessage ());
			response = context.getResources ().getString (R.string.invalid_data);
		}
		return response;
	}
}
