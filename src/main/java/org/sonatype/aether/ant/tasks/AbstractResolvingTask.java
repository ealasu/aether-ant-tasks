package org.sonatype.aether.ant.tasks;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;
import org.sonatype.aether.ant.AntRepoSys;
import org.sonatype.aether.ant.types.Dependencies;
import org.sonatype.aether.ant.types.LocalRepository;
import org.sonatype.aether.ant.types.RemoteRepositories;
import org.sonatype.aether.ant.types.RemoteRepository;
import org.sonatype.aether.collection.CollectResult;

public abstract class AbstractResolvingTask
    extends Task
{

    protected Dependencies dependencies;

    protected RemoteRepositories remoteRepositories;

    protected LocalRepository localRepository;

    public void addDependencies( Dependencies dependencies )
    {
        if ( this.dependencies != null )
        {
            throw new BuildException( "You must not specify multiple <dependencies> elements" );
        }
        this.dependencies = dependencies;
    }

    public void setDependenciesRef( Reference ref )
    {
        if ( dependencies == null )
        {
            dependencies = new Dependencies();
            dependencies.setProject( getProject() );
        }
        dependencies.setRefid( ref );
    }

    public LocalRepository createLocalRepo()
    {
        if ( localRepository != null )
        {
            throw new BuildException( "You must not specify multiple <localRepo> elements" );
        }
        localRepository = new LocalRepository( this );
        return localRepository;
    }

    private RemoteRepositories getRemoteRepos()
    {
        if ( remoteRepositories == null )
        {
            remoteRepositories = new RemoteRepositories();
            remoteRepositories.setProject( getProject() );
        }
        return remoteRepositories;
    }

    public void addRemoteRepo( RemoteRepository repository )
    {
        getRemoteRepos().addRemoterepo( repository );
    }

    public void addRemoteRepos( RemoteRepositories repositories )
    {
        getRemoteRepos().addRemoterepos( repositories );
    }

    public void setRemoteReposRef( Reference ref )
    {
        RemoteRepositories repos = new RemoteRepositories();
        repos.setProject( getProject() );
        repos.setRefid( ref );
        getRemoteRepos().addRemoterepos( repos );
    }

    protected CollectResult collectDependencies()
    {
        return AntRepoSys.getInstance( getProject() ).collectDependencies( this, dependencies, localRepository,
                                                                           remoteRepositories );
    }

}