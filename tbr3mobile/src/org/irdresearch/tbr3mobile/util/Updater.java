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

package org.irdresearch.tbr3mobile.util;

import org.irdresearch.tbr3mobile.App;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class Updater extends Thread
{
	public boolean	isRunning	= false;
	private Context	context;

	public Updater (Context context)
	{
		this.context = context;
	}

	@Override
	public void run ()
	{
		super.run ();
		while (isRunning)
		{
			try
			{
				Log.d (Updater.class.getSimpleName (), "Forrest running...");
				Thread.sleep (App.getDelay ());

				try
				{
					DatabaseUtil util = new DatabaseUtil (this.context);
					SQLiteDatabase db = util.getWritableDatabase ();

					ContentValues values = new ContentValues ();
					values.put ("Preference", "Color");
					values.put ("Value", "Green");
					db.insert ("preference", null, values);
					// THIS IS WHERE I LEFT
					db.insertWithOnConflict ("", null, values, SQLiteDatabase.CONFLICT_REPLACE);
					db.close ();
					util.close ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace ();
				isRunning = false;
			}
		}
	}
}
