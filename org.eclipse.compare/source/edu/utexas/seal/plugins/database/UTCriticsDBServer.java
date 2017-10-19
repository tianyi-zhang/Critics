/*
 * @(#) UTCriticsDBServer.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.hsqldb.server.Server;

import edu.utexas.seal.plugins.edit.UTAbstractEdit;

/**
 * @author troy
 * 
 */
public class UTCriticsDBServer {
	Connection		con;
	Server			server;
	private String	leftContextName;
	private String	rightContextName;
	private String	diffsName;

	public void start() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String path = workspace.getRoot().getLocation().toString();
		path = "file:" + path + "/diffDB/diffDB";

		server = new Server();
		server.setAddress("localhost");
		server.setDatabaseName(0, "diffDB");
		server.setDatabasePath(0, path);
		server.setPort(9010);
		server.setTrace(true);
		server.setLogWriter(new PrintWriter(System.out));
		server.start();
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace(System.out);
		}
	}

	public Connection connect() {
		try {
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9010/diffDB", "SA", "");
		} catch (SQLException e) {
			System.out.println("ERROR: failed to connect to DB server.");
			e.printStackTrace();
		}

		return con;
	}

	/**
	 * Used to create tables, including diffs table, old context table and new context table
	 * It is called each time comparing two projects
	 * 
	 * @param name1
	 * @param name2
	 */
	public void createTables(String name1, String name2) {
		leftContextName = "left" + "_" + name1 + "_" + name2;
		String leftContextTableS = "create table " + leftContextName +
		// "( id int generated by default as identity(START WITH 1  INCREMENT BY 1), " +
				"( id int, " + "context clob, " + "Primary Key(id) )";

		rightContextName = "right" + "_" + name1 + "_" + name2;
		String rightContextTableS = "create table " + rightContextName +
		// "( id int generated by default as identity(START WITH 1  INCREMENT BY 1), " +
				"( id int, " + "context clob, " + "Primary Key(id) )";

		diffsName = "diffs" + "_" + name1 + "_" + name2;
		String diffsTableS = "create table "
				+ diffsName
				// "( id int generated by default as identity(START WITH 1  INCREMENT BY 1), " +
				+ "( id int, " + "type varchar(50), " + "leftStart int, " + "leftEnd int, " + "rightStart int, " + "rightEnd int, " + "leftS clob, " + "rightS clob, "
				+ "leftC int, " + "rightC int, " + "leftFile varchar(256), " + "rightFile varchar(256), " + "Primary Key(id), " + "Foreign Key(leftC) references "
				+ leftContextName + "(id), " + "Foreign Key(leftC) references " + rightContextName + "(id) )";
		try {
			con.createStatement().executeUpdate(leftContextTableS);
			con.createStatement().executeUpdate(rightContextTableS);
			con.createStatement().executeUpdate(diffsTableS);
		} catch (SQLException e) {
			System.out.println("ERROR: failed to create tables.");
			e.printStackTrace();
		}
	}

	public void insertNewContext(UTAbstractEdit edit) {
		String newContext;
		if (edit.getLeftEditContext() == null) {
			newContext = "null";
		} else {
			newContext = edit.getLeftEditContext();
		}

		String insert = "insert into " + leftContextName + "(id, context) " + "values(" + "'" + edit.getEditId() + "'," + "'" + newContext + "')";

		try {
			con.createStatement().execute(insert);
		} catch (SQLException e) {
			System.out.println("ERROR: failed to insert new contexts.");
			e.printStackTrace();
		}
	}

	public void insertOldContext(UTAbstractEdit edit) {
		String oldContext;
		if (edit.getRightEditContext() == null) {
			oldContext = "null";
		} else {
			oldContext = edit.getRightEditContext();
		}

		String insert = "insert into " + rightContextName + "(id, context) " + "values(" + "'" + edit.getEditId() + "'," + "'" + oldContext + "')";
		try {
			con.createStatement().execute(insert);
		} catch (SQLException e) {
			System.out.println("ERROR: failed to insert old contexts.");
			e.printStackTrace();
		}
	}

	public void insertEdit(UTAbstractEdit edit, String leftFile, String rightFile) {
		String insert = "insert into " + diffsName + "(id, type, leftStart, leftEnd, rightStart, rightEnd, leftS, rightS, leftC, rightC, leftFile, rightFile) "
				+ "values(" + "'" + edit.getEditId() + "'," + "'" + edit.getEditKind() + "'," + "'" + edit.getLeftEditOffsetBgn() + "'," + "'"
				+ edit.getLeftEditOffsetEnd() + "'," + "'" + edit.getRightEditOffsetEnd() + "'," + "'" + edit.getRightEditOffsetEnd() + "'," + "'"
				+ edit.getLeftEditStatement() + "'," + "'" + edit.getRightEditStatement() + "'," + "'" + edit.getEditId() + "'," + "'" + edit.getEditId() + "'," + "'"
				+ leftFile + "'," + "'" + rightFile + "')";
		try {
			con.createStatement().execute(insert);
		} catch (SQLException e) {
			System.out.println("ERROR: failed to insert edits.");
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}