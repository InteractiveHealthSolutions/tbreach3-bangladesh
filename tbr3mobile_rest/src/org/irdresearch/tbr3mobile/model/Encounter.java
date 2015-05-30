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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.format.DateFormat;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class Encounter extends AbstractModel
{
	public static final String	FIELDS	= "uuid,encounterDatetime,patient,encounterType,location,provider,obs";

	String						encounterType;
	Date						encounterDate;
	String						patient;
	String						location;
	String						provider;
	Observation[]				observations;

	public Encounter (String uuid)
	{
		super (uuid);
	}

	public Encounter (String uuid, String encounterType, Date encounterDate, String patient,
			String location, String provider, Observation[] observations)
	{
		super (uuid);
		this.encounterType = encounterType;
		this.encounterDate = encounterDate;
		this.patient = patient;
		this.location = location;
		this.provider = provider;
		this.observations = observations;
	}

	public JSONObject getJSONObject ()
	{
		JSONObject jsonObject = new JSONObject ();
		try
		{
			jsonObject.put ("uuid", super.getUuid ());
			jsonObject.put ("encounterType", encounterType);
			jsonObject.put ("encounterDatetime", DateFormat.format ("yyyy-MM-dd hh:mm:ss", encounterDate));
			jsonObject.put ("patient", patient);
			jsonObject.put ("location", location);
			jsonObject.put ("provider", provider);
			JSONArray obsArray = new JSONArray ();
			for (Observation o : observations)
			{
				JSONObject obj = o.getJSONObject ();
				obsArray.put (obj);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			jsonObject = null;
		}
		return jsonObject;
	}

	public static Encounter parseJSONObject (JSONObject json)
	{
		Encounter encounter = null;
		String uuid = "";
		String encounterType = null;
		Date encounterDate = null;
		String patient = null;
		String location = null;
		String provider = null;
		Observation[] observations = null;
		try
		{
			uuid = json.getString ("uuid");
			encounterType = json.getString ("encountertype");
			// TODO: Change the locale based on preferences
			SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss", Locale.getDefault ());
			encounterDate = format.parse (json.getString ("encounterdate"));
			patient = json.getString ("patient");
			location = json.getString ("location");
			provider = json.getString ("provider");
			JSONArray obsArray = new JSONArray (json.getString ("observations"));
			observations = new Observation[obsArray.length ()];
			for (int i = 0; i < obsArray.length (); i++)
			{
				JSONObject obs = obsArray.getJSONObject (i);
				obs.get ("uuid");
				observations[i] = Observation.parseJSONObject (obs);
			}
			encounter = new Encounter (uuid, encounterType, encounterDate, patient, location, provider, observations);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			encounter = null;
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			encounter = null;
		}
		encounter = new Encounter (uuid);
		return encounter;
	}

	public String getEncounterType ()
	{
		return encounterType;
	}

	public void setEncounterType (String encounterType)
	{
		this.encounterType = encounterType;
	}

	public Date getEncounterDate ()
	{
		return encounterDate;
	}

	public void setEncounterDate (Date encounterDate)
	{
		this.encounterDate = encounterDate;
	}

	public String getPatient ()
	{
		return patient;
	}

	public void setPatient (String patient)
	{
		this.patient = patient;
	}

	public String getLocation ()
	{
		return location;
	}

	public void setLocation (String location)
	{
		this.location = location;
	}

	public String getProvider ()
	{
		return provider;
	}

	public void setProvider (String provider)
	{
		this.provider = provider;
	}

	public Observation[] getObservations ()
	{
		return observations;
	}

	public void setObservations (Observation[] observations)
	{
		this.observations = observations;
	}

	@Override
	public String toString ()
	{
		return super.toString () + ", " + encounterType + ", " + encounterDate + ", " + patient + ", "
				+ location + ", " + provider + ", " + Arrays.toString (observations);
	}

}
