package com.ibm.cics.cbmp;

/*-
 * #%L
 * CICS Bundle Maven Plugin
 * %%
 * Copyright (C) 2019 IBM Corp.
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.ibm.cics.bundle.parts.WarBundlePart;

@Mojo(name = "bundle-war", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.VERIFY)
public class BundleWarMojo extends AbstractBundleJavaMojo {
	
	@Override
	protected WarBundlePart getBundlePart(org.apache.maven.artifact.Artifact artifact) {
		return new WarBundlePart(artifact.getArtifactId() + "_" + artifact.getVersion(), jvmserver, artifact.getFile());
	}

}
