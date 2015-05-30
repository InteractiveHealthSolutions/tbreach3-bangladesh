/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.client;

import org.irdresearch.tbr3web.shared.Parameter;
import org.irdresearch.tbr3web.shared.model.Defaults;
import org.irdresearch.tbr3web.shared.model.Definition;
import org.irdresearch.tbr3web.shared.model.Sms;
import org.irdresearch.tbr3web.shared.model.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Async counterpart of <code>Service</code>.
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public interface ServerServiceAsync
{
	/* Authentication methods */
	void authenticate (String userName, String password, String role, AsyncCallback<User> callback) throws Exception;

	void authenticateUser (String text, AsyncCallback<Boolean> callback) throws Exception;

	void verifySecretAnswer (String userName, String secretAnswer, AsyncCallback<Boolean> callback) throws Exception;

	/* Other methods */
	void count (String tableName, String filter, AsyncCallback<Long> callback) throws Exception;

	void exists (String tableName, String filer, AsyncCallback<Boolean> callback) throws Exception;

	void generateCSVfromQuery (String database, String query, AsyncCallback<String> callback) throws Exception;

	void generateReport (String fileName, Parameter[] params, boolean export, AsyncCallback<String> callback) throws Exception;

	void generateReportFromQuery (String database, String reportName, String query, Boolean export,
			AsyncCallback<String> callback) throws Exception;

	void getColumnData (String tableName, String columnName, String filter, AsyncCallback<String[]> callback) throws Exception;

	void getCurrentUserName (AsyncCallback<String> callback) throws Exception;

	void getSchema (AsyncCallback<String[][]> callback) throws Exception;

	void getDefaults (AsyncCallback<Defaults[]> callback) throws Exception;

	void getDefinitions (AsyncCallback<Definition[]> callback) throws Exception;

	void getDumpFiles (AsyncCallback<String[]> callback);

	void getReportsList (AsyncCallback<String[][]> callback) throws Exception;

	void getRowRecord (String tableName, String[] columnNames, String filter, AsyncCallback<String[]> callback) throws Exception;

	void getObject (String tableName, String columnName, String filter, AsyncCallback<String> callback) throws Exception;

	void getQueriesResults (String[] queries, AsyncCallback<String[]> callback) throws Exception;

	void getSecretQuestion (String userName, AsyncCallback<String> callback) throws Exception;

	void getSnapshotTime (AsyncCallback<String> callback) throws Exception;

	void getTableData (String tableName, String[] columnNames, String filter, AsyncCallback<String[][]> callback) throws Exception;

	void getTableData (String sqlQuery, AsyncCallback<String[][]> callback) throws Exception;

	void getUserRgihts (String userName, String role, String menuName, AsyncCallback<Boolean[]> callback) throws Exception;

	void execute (String query, AsyncCallback<Integer> callback) throws Exception;

	void execute (String[] queries, AsyncCallback<Boolean> callback) throws Exception;

	void executeProcedure (String procedure, AsyncCallback<Boolean> callback) throws Exception;

	void recordLogin (String userName, AsyncCallback<Void> callback) throws Exception;

	void recordLogout (String userName, AsyncCallback<Void> callback) throws Exception;

	void sendGenericSMSAlert (Sms sms, AsyncCallback<Void> callback) throws Exception;

	void sendGenericSMSAlert (Sms[] sms, AsyncCallback<Void> callback) throws Exception;

	void setCurrentUser (String userName, String role, AsyncCallback<Void> callback) throws Exception;

	void findUser (String currentUserName, AsyncCallback<User> asyncCallback);
}
