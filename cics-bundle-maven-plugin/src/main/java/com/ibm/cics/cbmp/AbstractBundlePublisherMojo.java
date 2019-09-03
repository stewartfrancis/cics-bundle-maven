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

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.buildhelper.versioning.VersionInformation;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.ibm.cics.bundle.parts.BundlePublisher;

public abstract class AbstractBundlePublisherMojo extends AbstractMojo {

	protected static final String EAR = "ear";
	protected static final String WAR = "war";
	protected static final String JAR = "jar";

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${project.basedir}", required = true, readonly = true)
	protected File baseDir;
	
	@Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
	protected File buildDir;

	@Parameter(defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}", required = true, readonly = true)
	protected File workDir;

	@Parameter(required = false)
	protected List<BundlePartBinding> bundleParts = Collections.emptyList();
	
	@Component
	protected BuildContext buildContext;
	
	protected BundlePublisher getBundlePublisher() throws MojoExecutionException {
		BundlePublisher bundlePublisher = (BundlePublisher) project.getContextValue(BundlePublisher.class.getName());
		if (bundlePublisher == null) {
			bundlePublisher = createBundlePublisher();
			project.setContextValue(BundlePublisher.class.getName(), bundlePublisher);
		}
		
		return bundlePublisher;
	}

	private BundlePublisher createBundlePublisher() throws MojoExecutionException {
		VersionInformation v = new VersionInformation(project.getVersion());
		BundlePublisher bundlePublisher = new BundlePublisher(
			workDir.toPath(),
			project.getArtifactId(),
			v.getMajor(),
			v.getMinor(),
			v.getPatch(),
			v.getBuildNumber()
		);
		
		//Notify the build context of file changes
		bundlePublisher.setFileChangeListener(path -> buildContext.refresh(path.toFile()));
		
		initBundlePublisher(bundlePublisher);
		return bundlePublisher;
	}
	
	protected void initBundlePublisher(BundlePublisher bundlePublisher) throws MojoExecutionException {
		
	};

}
