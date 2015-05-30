/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Provides various Client-side methods used in the Application
 */

package org.irdresearch.tbr3web.client;

import org.irdresearch.tbr3web.shared.TBR3;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class TBRK3lient
{
	/**
	 * Creates a 'long' code for a given string using some mathematics
	 * 
	 * @param string
	 * @return
	 */
	public static long getSimpleCode (String string)
	{
		long code = 1;
		for (int i = 0; i < string.length (); i++)
			code *= string.charAt (i);
		return code;
	}

	/**
	 * Verifies whether client has entered a valid pass code (required for some
	 * sensitive operations)
	 * 
	 * @return
	 */
	public static boolean verifyClientPasscode (String passcode)
	{
		try
		{
			String storedPasscode = Cookies.getCookie ("Pass");
			long passedCode = getSimpleCode (passcode.substring (0, 3));
			long existing = Long.parseLong (storedPasscode);
			return (passedCode == existing);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return false;
		}
	}

	/**
	 * Get usually desired value from a widget
	 * 1. Text fields return their respective text
	 * 2. List boxes return selected value
	 * 
	 * @param widget
	 * @return
	 */
	public static String get (Widget widget)
	{
		try
		{
			if (widget instanceof TextBoxBase)
				return ((TextBoxBase) widget).getText ();
			if (widget instanceof ListBox)
				return ((ListBox) widget).getValue (((ListBox) widget).getSelectedIndex ());
			if (widget instanceof ValueBoxBase<?>)
				return ((ValueBoxBase<?>) widget).getText ();
		}
		catch (Exception e)
		{
		}
		return "";
	}

	/**
	 * Get usually desired value from a widget
	 * 1. Text fields return their respective text
	 * 2. List boxes return selected value
	 * 
	 * @param listBox
	 * @return
	 */
	public static String getKey (ListBox listBox)
	{
		try
		{
			return TBR3.getDefinitionKey (listBox.getName (), get (listBox));
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * Get index of a given value from a widget (probably ListBox)
	 * 
	 * @param widget
	 * 
	 * @param value
	 * @return
	 */
	public static int getIndex (Widget widget, String value)
	{
		if (widget instanceof ListBox)
		{
			ListBox listBox = (ListBox) widget;
			for (int i = 0; i < listBox.getItemCount (); i++)
				if (listBox.getValue (i).equalsIgnoreCase (value))
					return i;
		}
		return -1;
	}

	/**
	 * Fill a widget with values from definition based on its "name" property.
	 * This method also sets current index of a widget to default (if exists in defaults)
	 * 
	 * @param widget
	 * @return
	 */
	public static Widget fillList (Widget widget)
	{
		if (widget instanceof ListBox)
		{
			ListBox listBox = (ListBox) widget;
			String name = listBox.getName ();
			listBox.clear ();
			String[] values = TBR3.getDefinitionValues (name);
			for (String s : values)
				listBox.addItem (s, TBR3.getDefinitionKey (name, s));
			String defaultValue = TBR3.getDefinitionValue (name, TBR3.getDefaultValue (name));
			listBox.setSelectedIndex (TBRK3lient.getIndex (listBox, defaultValue));
			return listBox;
		}
		return widget;
	}
}
