/*
 * @(#) CompareItem.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.compare.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import ut.seal.plugins.utils.UTFile;

/**
 * @author Myoungkyu Song
 * @date Jan 23, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {
	private File	mFile;
	private String	mContents, mName;
	private long	mTime;

	public CompareItem(File file) {
		this.mFile = file;
		this.mName = file.getName();
		this.mContents = UTFile.readFileWithNewLine(file.getAbsolutePath());
		try {
			this.mTime = UTFile.getLastModifiedTime(file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CompareItem(String name, String contents, long time) {
		this.mName = name;
		this.mContents = contents;
		this.mTime = time;
	}

	@Override
	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(mContents.getBytes());
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public long getModificationDate() {
		return mTime;
	}

	@Override
	public String getName() {
		return mName;
	}

	public String getString() {
		return mContents;
	}

	@Override
	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		this.mTime = time;
	}

	public void setContents(String contents) {
		this.mContents = contents;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public File getFile() {
		return mFile;
	}

	public void setFile(File aFile) {
		this.mFile = aFile;
	}

}