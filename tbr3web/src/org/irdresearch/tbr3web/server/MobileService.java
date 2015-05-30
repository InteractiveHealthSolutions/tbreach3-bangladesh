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

package org.irdresearch.tbr3web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.irdresearch.tbr3web.server.util.DateTimeUtil;
import org.irdresearch.tbr3web.server.util.HibernateUtil;
import org.irdresearch.tbr3web.server.util.XmlUtil;
import org.irdresearch.tbr3web.shared.CustomMessage;
import org.irdresearch.tbr3web.shared.ErrorType;
import org.irdresearch.tbr3web.shared.FormType;
import org.irdresearch.tbr3web.shared.model.Feedback;
import org.irdresearch.tbr3web.shared.model.Patient;
import org.irdresearch.tbr3web.shared.model.Person;
import org.irdresearch.tbr3web.shared.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MobileService extends HttpServlet
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4096512335786671901L;
	private HttpServletRequest	request;
	private ServerServiceImpl	serverService;
	private HibernateUtil		util;

	public MobileService ()
	{
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		request = req;
		System.out.println ("Posting to Server..");
		serverService = new ServerServiceImpl ();
		util = new HibernateUtil ();
		PrintWriter out = resp.getWriter ();
		String response = "Response";
		try
		{
			BufferedReader reader = req.getReader ();
			StringBuffer sb = new StringBuffer ();
			while (reader.read () != -1)
			{
				sb.append (reader.readLine ());
			}
			String content = sb.toString ().replace ("}", "").replace ("\"", "");
			String[] valuePairs = content.split (",");
			HashMap<String, String> valuesMap = new HashMap<String, String> ();
			for (String str : valuePairs)
			{
				String key = "";
				String value = "";
				String[] valuePair = str.split (":");
				if (valuePair != null)
				{
					key = valuePair[0];
					if (valuePair.length == 2)
					{
						value = valuePair[1];
					}
				}
				valuesMap.put (key, value);
			}
			String formType = valuesMap.get ("form");
			if (formType.equals (FormType.SCREENING))
				response = doScreening (formType, valuesMap);
			else if (formType.equals (FormType.FEEDBACK))
				response = doFeedback (formType);
			else
				throw new Exception ();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			response = XmlUtil.createErrorXml (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR));
		}
		catch (Exception e)
		{
			response = XmlUtil.createErrorXml (CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR));
		}
		out.print (response);
		out.flush ();
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
	}

	public void setRequest (HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpServletRequest getRequest ()
	{
		return request;
	}

	@SuppressWarnings("deprecation")
	private String doScreening (String formType, HashMap<String, String> values)
	{
		String xml = null;
		try
		{
			int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.get ("gender").toString ();
			String weight = values.get ("weight").toString ();
			String height = values.get ("height").toString ();
			String location = values.get ("location").toString ();
			String username = values.get ("username").toString ();
			String formDate = values.get ("formdate").toString ();

			String uuid = UUID.randomUUID ().toString ();
			Person person = new Person (uuid, "");
			Date dob = new Date ();
			dob.setYear (dob.getYear () - age);
			person.setDob (dob);
			person.setGender (gender.charAt (0));
			Patient patient = new Patient (uuid, uuid);
			patient.setWeight (Float.parseFloat (weight));
			patient.setHeight (Float.parseFloat (height));
			patient.setDateScreened (DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE));
			patient.setSuspectedBy (username);
			patient.setTreatmentCentre (location);
			patient.setPatientStatus ("SCREENED");
			if (util.save (person))
			{
				util.save (patient);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		// Create XML response
		Document doc = null;
		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement ("response");
		Element node = doc.createElement ("status");
		Text value = doc.createTextNode ("success");
		node.appendChild (value);
		responseNode.appendChild (node);
		doc.appendChild (responseNode);
		xml = XmlUtil.docToString (doc);
		return xml;
	}

	private String doFeedback (String formType)
	{
		String xml = null;
		String dateReported = request.getParameter ("dateReported").toUpperCase ();
		String userName = request.getParameter ("userName").toUpperCase ();
		String feedbackType = request.getParameter ("feedbackType").toUpperCase ();
		String feedbackText = request.getParameter ("feedback").toUpperCase ();

		User user = serverService.findUser (userName);
		if (user == null)
			return XmlUtil.createErrorXml ("User not found in the database.");
		Feedback feedback = new Feedback (userName, feedbackType, feedbackText, new Date (Long.parseLong (dateReported)), "P");
		feedback.setAttachment (null);
		Boolean status = util.save (feedback);
		if (!status)
		{
			return XmlUtil.createErrorXml ("ERROR");
		}
		// Create XML response
		Document doc = null;
		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement ("response");
		Element node = doc.createElement ("status");
		Text value = doc.createTextNode ("success");
		node.appendChild (value);
		responseNode.appendChild (node);
		doc.appendChild (responseNode);
		xml = XmlUtil.docToString (doc);
		return xml;
	}
}
