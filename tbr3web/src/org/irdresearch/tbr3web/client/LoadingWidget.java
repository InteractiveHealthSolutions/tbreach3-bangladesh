/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Loading Widget for GWT 
 */

package org.irdresearch.tbr3web.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author branflake2267
 * 
 */
public class LoadingWidget extends Composite
{
	private HorizontalPanel	pWidget	= new HorizontalPanel ();
	private HTML			html	= new HTML ();

	/**
	 * Initialize loading widget
	 */
	public LoadingWidget ()
	{
		Image image = new Image ("images/loading.gif");
		pWidget.add (new HTML ("&nbsp;"));
		pWidget.add (image);
		pWidget.add (new HTML ("&nbsp;"));
		pWidget.add (html);
		initWidget (pWidget);
		pWidget.setStyleName ("loadingImage");
		hide ();
	}

	/**
	 * Hide loading widget
	 */
	public void hide ()
	{
		pWidget.setVisible (false);
		Window.setStatus ("Done");
	}

	/**
	 * Show loading widget
	 */
	public void show ()
	{
		pWidget.setVisible (true);
		Window.setStatus ("Loading...");
	}

	/**
	 * Show loading widget with text
	 * 
	 * @param s
	 */
	public void show (String s)
	{
		show ();
		setHTML (s);
	}

	/**
	 * Set text for loading
	 * 
	 * @param s
	 */
	public void setHTML (String s)
	{
		html.setVisible (true);
		html.setHTML (s);
	}

	/**
	 * Hide loading widget timed
	 */
	public void hideTimed ()
	{
		pWidget.setVisible (true);
		Timer t = new Timer ()
		{
			public void run ()
			{
				pWidget.setVisible (false);
			}
		};
		t.schedule (3000);
	}
}
