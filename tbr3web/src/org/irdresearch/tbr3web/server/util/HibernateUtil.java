/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Implements hibernate features. This class must be initialized before performing any hibernate operation 
 */

package org.irdresearch.tbr3web.server.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.irdresearch.tbr3web.server.LogType;
import org.irdresearch.tbr3web.shared.TBR3;
import org.irdresearch.tbr3web.shared.model.LogData;
import org.irdresearch.tbr3web.shared.model.LogLogin;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class HibernateUtil implements Serializable
{
	private static final long		serialVersionUID	= -2333198879612643152L;
	public static HibernateUtil		util				= new HibernateUtil ();
	private static SessionFactory	factory;

	// private Class<?>[] classes;

	/**
	 * Default Constructor to initialize Session
	 */
	public HibernateUtil ()
	{
		/* Use when trying Annotation */
		// factory = getInitializedConfiguration().buildSessionFactory();
		factory = new Configuration ().configure ().buildSessionFactory ();
	}

	/**
	 * Create a query object from query string
	 * 
	 * @param queryString
	 * @return
	 */
	public Query create (String query)
	{
		return getSession ().createQuery (query);
	}

	/**
	 * Get record count for given query
	 * 
	 * @return
	 */
	public long count (String query)
	{
		return count (create (query));
	}

	/**
	 * Get record count for given query
	 * 
	 * @return
	 */
	public long count (Query query)
	{
		return Long.parseLong (query.uniqueResult ().toString ());
	}

	/**
	 * Overloaded method for findObject(String)
	 * 
	 * @param query
	 * @return
	 */
	public Object findObject (String query)
	{
		try
		{
			return findObjects (query)[0];
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}

	/**
	 * Get a list of objects from given query.
	 * 
	 * @param query
	 * @return
	 */
	public Object[] findObjects (String query)
	{
		try
		{
			Session session = getSession ();
			Query q = session.createQuery (query);
			List<?> list = q.list ();
			return list.toArray ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}

	/**
	 * Get a list of objects from given SQL query
	 * 
	 * @param SQLQuery
	 * @return
	 */
	public Object selectObject (String sqlQuery)
	{
		return selectObjects (sqlQuery)[0];
	}

	/**
	 * Get a list of objects from given SQL query
	 * 
	 * @param SQLQuery
	 * @return
	 */
	public Object[] selectObjects (String sqlQuery)
	{
		Session session = getSession ();
		// session.beginTransaction();
		SQLQuery q = session.createSQLQuery (sqlQuery);
		List<?> list = q.list ();
		return list.toArray ();
	}

	/**
	 * Get a list of objects from given SQL query
	 * 
	 * @param SQLQuery
	 * @return
	 */
	public Object[][] selectData (String SqlQuery)
	{
		Session session = getSession ();
		SQLQuery q = session.createSQLQuery (SqlQuery);
		List<?> list = q.list ();
		Object[][] tableData = new Object[list.size ()][];
		try
		{
			int cnt = 0;
			for (ListIterator<?> iter = list.listIterator (); iter.hasNext ();)
			{
				Object[] array = (Object[]) iter.next ();
				tableData[cnt++] = array;
			}
			return tableData;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}

	/**
	 * Update an object present in the database
	 * 
	 * @param obj
	 * @return true if record was successfully updated false if record did not
	 *         save for some constraint null if transaction causes breakage in
	 *         integrity (duplication, etc.)
	 */
	public Boolean update (Object obj)
	{
		try
		{
			Session session = getSession ();
			session.update (obj);
			session.flush ();
			session.close ();
			recordLog (LogType.UPDATE, obj);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			if (e.getCause ().getClass () == BatchUpdateException.class)
				return null;
			else
				return false;
		}
	}

	/**
	 * Saves multiple objects into database
	 * 
	 * @param obj
	 * @return true if all record was successfully saved false if any of the
	 *         records did not save
	 */
	public boolean bulkSave (Object[] objects)
	{
		try
		{
			Session session = getSession ();
			for (Object o : objects)
				session.save (o);
			session.flush ();
			session.close ();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return false;
		}
	}

	/**
	 * Save an object in the database
	 * 
	 * @param obj
	 * @return true if record was successfully saved false if record did not
	 *         save for some constraint null if transaction causes breakage in
	 *         integrity (duplication, etc.)
	 */
	public Boolean save (Object obj)
	{
		try
		{
			Session session = getSession ();
			session.save (obj);
			session.flush ();
			session.close ();
			recordLog (LogType.INSERT, obj);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			if (e.getCause ().getClass () == BatchUpdateException.class)
				return null;
			else
				return false;
		}
	}

	/**
	 * Deletes an Object from database
	 * 
	 * @param obj
	 * @return true if record was successfully deleted false if record did not
	 *         delete for some constraint null if transaction causes breakage in
	 *         integrity (duplication, etc.)
	 */
	public Boolean delete (Object obj)
	{
		try
		{
			Session session = getSession ();
			session.delete (obj);
			session.flush ();
			session.close ();
			recordLog (LogType.DELETE, obj);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			if (e.getCause ().getClass () == BatchUpdateException.class)
				return null;
			else
				return false;
		}
	}

	/**
	 * Records an object into one of the Log tables
	 * 
	 * @param logType
	 *            determine type of log (Delete Log, Change Log, etc.)
	 * @param obj
	 *            actual object to be logged
	 */
	public void recordLog (LogType logType, Object obj)
	{
		Object logObj = null;
		Class<?> objClass = obj.getClass ();
		String className = obj.getClass ().getSimpleName ();
		switch (logType)
		{
			case DELETE :
			case INSERT :
			case UPDATE :
				logObj = new LogData (TBR3.getCurrentUserName (), logType.toString (), new Date (), className, (objClass).cast (obj).toString ());
				break;
			case LOGIN :
				logObj = new LogLogin (TBR3.getCurrentUserName (), new Date (), null, "");
				break;
			default :
				break;
		}
		try
		{
			Session session = getSession ();
			session.save (logObj);
			session.flush ();
			session.close ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Executes a DML command
	 * 
	 * @param command
	 *            SQL statement as command
	 * @return number of records affected
	 */
	public int runCommand (String command)
	{
		Session session = getSession ();
		Transaction transaction = session.beginTransaction ();
		SQLQuery q = session.createSQLQuery (command);
		int results = q.executeUpdate ();
		transaction.commit ();
		return results;
	}

	/**
	 * Executes a Stored Procedure
	 * 
	 * @param procedure
	 *            SQL statement as procedure
	 * @return number of records affected
	 */
	@SuppressWarnings("deprecation")
	public boolean runProcedure (String procedure)
	{
		Session session = getSession ();
		session.beginTransaction ();
		try
		{
			CallableStatement callableStatement = session.connection ().prepareCall (procedure);
			System.out.println ("Executing stored procedure: " + procedure);
			callableStatement.execute ();
			session.getTransaction ().commit ();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			return false;
		}
	}

	/**
	 * @return the session
	 */
	public Session getSession ()
	{
		return factory.openSession ();
	}

	/**
	 * Returns the respective Database class to which an object belongs
	 * 
	 * @param obj
	 *            object to be identified
	 * @return Class object of class 'Class'
	 */
	public Class<?> identifyClass (Object obj)
	{
		return obj.getClass ();
	}

	/**
	 * Returns Entity name (table name) of a Persistance class
	 * 
	 * @param persistanceClass
	 * @return
	 */
	public String getTableName (Class<?> persistanceClass)
	{
		return factory.getClassMetadata (persistanceClass).getEntityName ();
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and sub packages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses (String packageName) throws ClassNotFoundException, IOException
	{
		ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		assert classLoader != null;
		String path = packageName.replace ('.', '/');
		Enumeration<URL> resources = classLoader.getResources (path);
		List<File> dirs = new ArrayList<File> ();
		while (resources.hasMoreElements ())
		{
			URL resource = resources.nextElement ();
			dirs.add (new File (resource.getFile ()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>> ();
		for (File directory : dirs)
		{
			classes.addAll (findClasses (directory, packageName));
		}
		return classes.toArray (new Class[classes.size ()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and sub
	 * directories.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses (File directory, String packageName) throws ClassNotFoundException
	{
		List<Class<?>> classes = new ArrayList<Class<?>> ();
		if (!directory.exists ())
		{
			return classes;
		}
		File[] files = directory.listFiles ();
		for (File file : files)
		{
			if (file.isDirectory ())
			{
				assert !file.getName ().contains (".");
				classes.addAll (findClasses (file, packageName + "." + file.getName ()));
			}
			else if (file.getName ().endsWith (".class"))
			{
				classes.add (Class.forName (packageName + '.' + file.getName ().substring (0, file.getName ().length () - 6)));
			}
		}
		return classes;
	}

}
