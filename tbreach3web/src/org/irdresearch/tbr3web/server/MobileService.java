/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class incorporates Open MRS API services. The content type used is JSON for Requests and Responses
 * 
 */

package org.irdresearch.tbr3web.server;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.xerces.impl.dv.util.Base64;
import org.hibernate.NonUniqueObjectException;
import org.irdresearch.tbr3web.server.util.DateTimeUtil;
import org.irdresearch.tbr3web.server.util.JsonUtil;
import org.irdresearch.tbr3web.shared.App;
import org.irdresearch.tbr3web.shared.CustomMessage;
import org.irdresearch.tbr3web.shared.ErrorType;
import org.irdresearch.tbr3web.shared.FormType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.ModuleMustStartException;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsUtil;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MobileService
{
	/**
	 * 
	 */
	private HttpServletRequest		request;

	// OpenMRS-related
	static final String				propFilePath	= "/usr/share/tomcat6/.OpenMRS/openmrs-runtime.properties";
	//static final String propFilePath = "C:\\Application Data\\OpenMRS\\openmrs-runtime.properties";
	private static File				propsFile;
	private static Properties		props;
	private static Connection		conn;
	private static String			url, username, password;
	private static MobileService	service			= new MobileService ();

	// Singleton. Called only once to fire up Open MRS
	private MobileService ()
	{
		try
		{
			propsFile = new File (propFilePath);
			props = new Properties ();
			OpenmrsUtil.loadProperties (props, propsFile);
			url = (String) props.get ("connection.url");
			username = (String) props.get ("connection.username");
			password = (String) props.get ("connection.password");
			openConnection ();
			Context.startup (url, username, password, props);
		}
		catch (ModuleMustStartException e)
		{
			e.printStackTrace ();
		}
		catch (DatabaseUpdateException e)
		{
			e.printStackTrace ();
		}
		catch (InputRequiredException e)
		{
			e.printStackTrace ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		finally
		{
			if (Context.isSessionOpen ())
			{
				Context.closeSession ();
			}
		}
	}

	/**
	 * Initialize native connection
	 * 
	 * @return
	 */
	private boolean openConnection ()
	{
		try
		{
			 String driver = "com.mysql.jdbc.Driver";
			 try
			{
				Class.forName(driver).newInstance();
			}
			catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Error: Driver not found");
			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Error: Driver not found");
			}
			conn = DriverManager.getConnection (url.substring (0, url.lastIndexOf ('/')), username, password);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			return false;
		}
		return true;
	}

	public static MobileService getService ()
	{
		return MobileService.service;
	}

	public String handleEvent (HttpServletRequest request)
	{
		System.out.println ("Posting to Server..");
		String response = "Response";
		try
		{
			// Check if the login credentials are provided in request as plain
			// text
			String username = null;
			String password = null;
			try
			{
				username = request.getParameter ("username");
				password = request.getParameter ("password");
				if (username == null || password == null)
				{
					throw new IllegalArgumentException ();
				}
			}
			catch (IllegalArgumentException e)
			{
				// Read the credentials from encrypted Authorization header
				String header = request.getHeader ("Authorization");
				byte[] authBytes = Base64.decode (header);
				String authData = new String (authBytes, "UTF-8");
				// Username and password MUST be separated using colon
				String[] credentials = authData.split (":");
				if (credentials.length == 2)
				{
					username = credentials[0];
					password = credentials[1];
				}
				else
				{
					throw new ContextAuthenticationException ();
				}
			}
			// Open OpenMRS Context session
			Context.openSession ();
			// Authenticate using Username and Password in the parameters
			Context.authenticate (username, password);

			// Read JSON from the Request
			String json = request.getParameter ("content");
			if (json == null)
				throw new JSONException ("JSON Content not found in the request.");
			JSONObject jsonObject = JsonUtil.getJSONObject (json);
			String appVer = jsonObject.getString ("app_ver");
			String s = App.appVersion;
			if (!appVer.equals (App.appVersion))
			{
				return JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.VERSION_MISMATCH_ERROR)).toString ();
			} 
			String formType = jsonObject.getString ("form_name");
			System.out.println(formType);
			if (formType.equals (FormType.GET_USER))
				response = getUser (formType, jsonObject);
			else if (formType.equals (FormType.GET_LOCATION))
				response = getLocation (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT))
				response = getPatient (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_DETAIL))
				response = getPatientDetail (formType, jsonObject);
			else if (formType.equals (FormType.NON_SUSPECT))
				response = doNonSuspectScreening (formType, jsonObject);
			else if (formType.equals (FormType.SCREENING) || formType.equals (FormType.NON_PULMONARY) || formType.equals (FormType.PAEDIATRIC_SCREENING))
				response = doScreening (formType, jsonObject);
			else if (formType.equals (FormType.CUSTOMER_INFO))
				response = doCustomerInfo (formType, jsonObject);
			else if (formType.equals (FormType.FEEDBACK))
				response = doFeedback (formType, jsonObject);
			else if (formType.equals (FormType.TEST_INDICATION) /* || formType.equals (FormType.BLOOD_SUGAR_RESULTS) || formType.equals (FormType.CLINICAL_EVALUATION)
					|| formType.equals (FormType.DRUG_DISPERSAL) || formType.equals (FormType.SPUTUM_INSTRUCTIONS) */)
				response = doGenericForm (formType, jsonObject);
			else
				throw new Exception ();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		catch (ContextAuthenticationException e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.UNKNOWN_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		finally
		{
			if (Context.isSessionOpen ())
			{
				Context.closeSession ();
			}
		}
		return response;
	}

	public HttpServletRequest getRequest ()
	{
		return request;
	}

	public String getUser (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			User user = Context.getUserService ().getUserByUsername (username);
			JSONObject userObj = new JSONObject ();
			userObj.put ("id", user.getUserId ());
			userObj.put ("name", user.getUsername ());
			json = userObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	public String getLocation (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String locationName = values.getString ("location_name");
			List<Location> locations = Context.getLocationService ().getLocations (locationName);
			Location location = locations.get (0);
			JSONObject locationObj = new JSONObject ();
			locationObj.put ("id", location.getLocationId ());
			locationObj.put ("name", location.getName ());
			json = locationObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	public String getPatient (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String patientId = values.getString ("patient_id");
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients == null)
			{
				return json;
			}
			if (patients.isEmpty ())
			{
				return json;
			}
			Patient patient = patients.get (0);
			JSONObject patientObj = new JSONObject ();
			patientObj.put ("id", patient.getPatientId ());
			patientObj.put ("name", patient.getPersonName ().getFullName ());
			json = patientObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	/**
	 * Warning! TNT Method, handle with care. Ever heard of SETI? Yeah, it's
	 * something like that. It searches for details about a Patient
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getPatientDetail (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			List<Patient> patients = new ArrayList<Patient> ();
			String patientId = values.getString ("patient_id");
			if (!patientId.equals (""))
			{
				patients = Context.getPatientService ().getPatients (patientId);
				if (patients != null)
				{
					Patient p = patients.get (0);
					json.put ("name", p.getPersonName ().getGivenName () + " " + p.getPersonName ().getFamilyName ());
					json.put ("gender", p.getGender ());
					json.put ("age", p.getAge ());
					List<Encounter> encountersByPatient = Context.getEncounterService ().getEncountersByPatient (p);
					JSONArray encountersArray = new JSONArray ();
					for (Encounter e : encountersByPatient)
					{
						JSONObject jsonObj = new JSONObject ();
						jsonObj.put ("encounter", e.getEncounterType ().getName ());
						jsonObj.put ("date", DateTimeUtil.getSQLDate (e.getEncounterDatetime ()));
						encountersArray.put (jsonObj);
					}
					if (encountersArray.length () != 0)
					{
						json.put ("encounters", encountersArray.toString ());
					}
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		finally
		{
			try
			{
				if (json.length () == 0)
				{
					json.put ("result", "FAIL. " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Non-suspects into a separate database
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doNonSuspectScreening (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String weight = values.getString ("weight").toString ();
			String height = values.getString ("height").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String formDate = values.getString ("formdate").toString ();
			String uuid = UUID.randomUUID ().toString ();
			Date dob = new Date ();
			dob.setYear (dob.getYear () - age);
			StringBuffer query = new StringBuffer ();
			query.append ("insert into openmrs_rpt.person (pid, first_name, last_name, gender, dob) values (?, ?, ?, ?, ?)");
			String[] params = {uuid, "", "", gender, DateTimeUtil.getSQLDate (dob)};
			if (executeUpdate (query.toString (), params))
			{
				query = new StringBuffer ();
				query.append ("insert into openmrs_rpt.patient (pid, patient_id, date_screened, suspected_by, treatment_centre, weight, height, patient_status) values (?, ?, ?, ?, ?, ?, ?, ?)");
				params = new String[] {uuid, uuid, formDate, username, location, weight, height, "SCREENED"};
				if (executeUpdate (query.toString (), params))
				{
					json.put ("result", "SUCCESS");
				}
				else
				{
					json.put ("result", "Failed to Insert record");
					executeUpdate ("delete from openmrs_rpt.person where pid=?", new String[] {uuid});
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return json.toString ();
	}

	/**
	 * Save Adult, Paediatric, Non Pulmonary or any other Screening form
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doScreening (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String givenName = values.getString ("given_name");
			String familyName = values.getString ("family_name");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Identifier type
			List<PatientIdentifierType> allIdTypes = Context.getPatientService ().getAllPatientIdentifierTypes ();
			PatientIdentifierType patientIdTypeObj = allIdTypes.get (0);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Create Patient
			{
				// Create Person object
				Person person = new Person ();
				Date dob = new Date ();
				dob.setYear (dob.getYear () - age);
				person.setBirthdate (dob);
				person.setBirthdateEstimated (true);
				person.setGender (gender);
				person.setCreator (creatorObj);
				person.setDateCreated (new Date ());
				// Create names set
				{
					SortedSet<PersonName> names = new TreeSet<PersonName> ();
					PersonName name = new PersonName (givenName, null, familyName);
					name.setCreator (creatorObj);
					name.setDateCreated (new Date ());
					name.setPreferred (true);
					names.add (name);
					person.setNames (names);
				}
				// Create Patient object
				Patient patient = new Patient (person);
				// Create Patient identifier
				{
					SortedSet<PatientIdentifier> identifiers = new TreeSet<PatientIdentifier> ();
					PatientIdentifier identifier = new PatientIdentifier ();
					identifier.setIdentifier (patientId);
					identifier.setIdentifierType (patientIdTypeObj);
					identifier.setLocation (locationObj);
					identifier.setCreator (creatorObj);
					identifier.setDateCreated (new Date ());
					identifier.setPreferred (true);
					identifiers.add (identifier);
					patient.setIdentifiers (identifiers);
				}
				patient.setCreator (creatorObj);
				patient.setDateCreated (new Date ());
				patient = Context.getPatientService ().savePatient (patient);
				error = "Patient was created with Error. ";
				Encounter encounter = new Encounter ();
				encounter.setEncounterType (encounterTypeObj);
				encounter.setPatient (patient);
				// In case of Encounter location different than login location
				if (!encounterLocation.equalsIgnoreCase (location))
				{
					locationObj = Context.getLocationService ().getLocation (encounterLocation);
				}
				encounter.setLocation (locationObj);
				encounter.setEncounterDatetime (encounterDatetime);
				encounter.setCreator (creatorObj);
				encounter.setDateCreated (new Date ());
				// Create Observations set
				{
					for (int i = 0; i < obs.length (); i++)
					{
						
						Obs ob = new Obs ();
						// Create Person object
						{
							Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
							ob.setPerson (personObj);
						}
						// Create question/answer Concept object
						{
							JSONObject pair = obs.getJSONObject (i);
							Concept concept = Context.getConceptService ().getConcept (pair.getString ("concept"));
							ob.setConcept (concept);
							String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
							if (hl7Abbreviation.equals ("NM"))
							{
								ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
							}
							else if (hl7Abbreviation.equals ("CWE"))
							{
								Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
								ob.setValueCoded (valueObj);
							}
							else if (hl7Abbreviation.equals ("ST"))
							{
								ob.setValueText (pair.getString ("value"));
							}
							else if (hl7Abbreviation.equals ("DT"))
							{
								ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
							}
						}
						ob.setObsDatetime (encounterDatetime);
						ob.setLocation (locationObj);
						ob.setCreator (creatorObj);
						ob.setDateCreated (new Date ());
						encounter.addObs (ob);
					}
					if (creatorObj.getUsername ().equals (provider))
						encounter.setProvider (creatorObj);
				}
				Context.getEncounterService ().saveEncounter (encounter);
				json.put ("result", "SUCCESS");
			}
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Client's Contact and Address information. Also creates and Encounter
	 * without Observations
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doCustomerInfo (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			String address1 = values.getString ("address1");
			String address2 = values.getString ("address2");
			String stateProvince = values.getString ("stateProvince");
			String address4 = values.getString ("address4");
			String countyDistrict = values.getString ("countyDistrict");
			String cityVillage = values.getString ("cityVillage");
			String country = values.getString ("country");
			JSONArray attributes = new JSONArray (values.getString ("attributes"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Get Person object
			Person person = Context.getPersonService ().getPerson (patient.getPersonId ());
			// Add address details
			{
				PersonAddress address = new PersonAddress ();
				address.setAddress1 (address1);
				address.setAddress2 (address2);
				address.setCityVillage (cityVillage);
				address.setCountry (country);
				address.setStateProvince (stateProvince);
				address.setAddress4 (address4);
				address.setCountyDistrict(countyDistrict);
				address.setCreator (creatorObj);
				address.setDateCreated (new Date ());
				person.addAddress (address);
			}
			// Add patient attributes
			for (int i = 0; i < attributes.length (); i++)
			{
				JSONObject pair = attributes.getJSONObject (i);
				PersonAttributeType personAttributeType;
				try
				{
					personAttributeType = Context.getPersonService ().getPersonAttributeTypeByName (pair.getString ("attribute"));
					PersonAttribute attribute = new PersonAttribute ();
					attribute.setAttributeType (personAttributeType);
					attribute.setValue (pair.getString ("value"));
					attribute.setCreator (creatorObj);
					attribute.setDateCreated (new Date ());
					person.addAttribute (attribute);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			Context.getPersonService ().savePerson (person);
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			if (creatorObj.getUsername ().equals (provider))
				encounter.setProvider (creatorObj);
			// Save encounter without observations
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Forms consisting of Encounters and Observations only
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doGenericForm (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Create Encounter
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			// Create Observations set
			for (int i = 0; i < obs.length (); i++)
			{
				Obs ob = new Obs ();
				// Create Person object
				{
					Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
					ob.setPerson (personObj);
				}
				// Create question/answer Concept object
				{
					JSONObject pair = obs.getJSONObject (i);
					Concept concept = Context.getConceptService ().getConceptByName (pair.getString ("concept"));
					ob.setConcept (concept);
					String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
					if (hl7Abbreviation.equals ("NM"))
					{
						ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
					}
					else if (hl7Abbreviation.equals ("CWE"))
					{
						Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
						ob.setValueCoded (valueObj);
					}
					else if (hl7Abbreviation.equals ("ST"))
					{
						ob.setValueText (pair.getString ("value"));
					}
					else if (hl7Abbreviation.equals ("DT"))
					{
						ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
					}
				}
				ob.setObsDatetime (encounterDatetime);
				ob.setLocation (locationObj);
				ob.setCreator (creatorObj);
				ob.setDateCreated (new Date ());
				encounter.addObs (ob);
			}
			if (creatorObj.getUsername ().equals (provider))
				encounter.setProvider (creatorObj);
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save user's Feedback form into separate database
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String doFeedback (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			String location = values.getString ("location").toUpperCase ();
			String feedbackType = values.getString ("feedback_type").toUpperCase ();
			String feedbackText = values.getString ("feedback").toUpperCase ();
			String userName = values.getString ("username").toUpperCase ();
			StringBuffer query = new StringBuffer ();
			query.append ("insert into openmrs_rpt.feedback (sender_id,feedback_type,feedback,date_reported,feedback_status) values (?, ?, ?, ?, ?)");
			String[] params = {userName, feedbackType, feedbackText.replace ("'", "") + ". Location " + location, DateTimeUtil.getSQLDate (new Date ()), "PENDING"};
			if (executeUpdate (query.toString (), params))
			{
				json.put ("result", "SUCCESS");
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json.toString ();
	}


	/**
	 * Execute native DML query to fetch data
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public String[][] executeQuery (String query, String[] parameterValues)
	{
		String[][] result = null;
		try
		{
			if (conn.isClosed ())
			{
				if (!openConnection ())
				{
					return result;
				}
			}
			PreparedStatement statement = conn.prepareStatement (query);
			int count = 1;
			if (parameterValues != null)
			{
				for (String s : parameterValues)
				{
					statement.setString (count++, s);
				}
			}
			ResultSet resultSet = statement.executeQuery ();
			resultSet.last ();
			int rows = resultSet.getRow ();
			resultSet.beforeFirst ();
			int columns = resultSet.getMetaData ().getColumnCount ();
			result = new String[rows][columns];
			int i = 0;
			while (resultSet.next ())
			{
				for (int j = 0; j < columns; j++)
				{
					result[i][j] = resultSet.getString (j + 1);
				}
				i++;
			}
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			result = null;
		}
		return result;
	}

	/**
	 * Execute Parameterized native DML query
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public boolean executeUpdate (String query, String[] parameterValues)
	{
		boolean result = true;
		try
		{
			if (conn.isClosed ())
			{
				if (!openConnection ())
				{
					return false;
				}
			}
			PreparedStatement statement = conn.prepareStatement (query);
			int count = 1;
			if (parameterValues != null)
			{
				for (String s : parameterValues)
				{
					statement.setString (count++, s);
				}
			}
			statement.executeUpdate ();
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			result = false;
		}
		return result;
	}

	/**
	 * Execute native DML query to fetch data
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public boolean execute (String query)
	{
		boolean result = false;
		try
		{
			if (conn.isClosed ())
			{
				if (!openConnection ())
				{
					return result;
				}
			}
			Statement statement = conn.createStatement ();
			result = statement.execute (query);
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
		}
		return result;
	}

}
