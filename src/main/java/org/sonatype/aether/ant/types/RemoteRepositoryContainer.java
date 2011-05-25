package org.sonatype.aether.ant.types;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.util.List;

import org.apache.tools.ant.Task;

/**
 * @author Benjamin Bentmann
 */
public interface RemoteRepositoryContainer
{

    void validate( Task task );

    List<RemoteRepository> getRepositories();

}
