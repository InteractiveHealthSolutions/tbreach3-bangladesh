/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Utility to send SMS alerts
 */

package org.irdresearch.tbr3web.server.util;

import java.util.Date;
import org.irdresearch.tbr3web.shared.model.Feedback;
import org.irdresearch.tbr3web.shared.model.Sms;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SMSUtil
{
	private static final String	status	= "PENDING";
	public static SMSUtil		util	= new SMSUtil ();

	public void sendGenericSMSAlert (String targetNumber, String messageText)
	{
		Sms sms = new Sms (targetNumber, messageText, new Date (), null, status, null, null);
		if (!sms.getTargetNumber ().equals (""))
			HibernateUtil.util.save (sms);
	}

	/**
	 * Send alerts to Technical correspondents on feedback
	 * 
	 * @param feedback
	 */
	public void sendAlertsOnFeedback (Feedback feedback)
	{
		if (feedback.getFeedbackType ().equalsIgnoreCase ("Error/Bug"))
		{
			sendGenericSMSAlert ("03453174270", feedback.toString ());
		}
	}
}
