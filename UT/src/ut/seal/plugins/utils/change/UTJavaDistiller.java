/*
 * @(#) UTJavaDistiller.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.change;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ch.uzh.ifi.seal.changedistiller.JavaChangeDistillerModule;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.distilling.DistillerFactory;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;

/**
 * @author Myoungkyu Song
 * @date Dec 6, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTJavaDistiller {
	protected static final Injector	sInjector;

	static {
		sInjector = Guice.createInjector(new JavaChangeDistillerModule());
	}

	Distiller getDistiller(StructureEntityVersion structureEntity) {
		return sInjector.getInstance(DistillerFactory.class).create(structureEntity);
	}

	static Distiller getDistillerStatic(StructureEntityVersion structureEntity) {
		return sInjector.getInstance(DistillerFactory.class).create(structureEntity);
	}

}