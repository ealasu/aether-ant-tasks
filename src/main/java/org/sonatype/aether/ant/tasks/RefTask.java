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
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;

/**
 * @author Benjamin Bentmann
 */
public abstract class RefTask
    extends Task
{

    private Reference ref;

    public boolean isReference()
    {
        return ref != null;
    }

    public void setRefid( final Reference ref )
    {
        this.ref = ref;
    }

    protected void checkAttributesAllowed()
    {
        if ( isReference() )
        {
            throw tooManyAttributes();
        }
    }

    protected void checkChildrenAllowed()
    {
        if ( isReference() )
        {
            throw noChildrenAllowed();
        }
    }

    protected BuildException tooManyAttributes()
    {
        return new BuildException( "You must not specify more than one " + "attribute when using refid" );
    }

    protected BuildException noChildrenAllowed()
    {
        return new BuildException( "You must not specify nested elements " + "when using refid" );
    }

    protected String getDataTypeName()
    {
        return ComponentHelper.getElementName( getProject(), this, true );
    }

    protected Object getCheckedRef()
    {
        return getCheckedRef( getClass(), getDataTypeName(), getProject() );
    }

    protected Object getCheckedRef( final Class<?> requiredClass, final String dataTypeName, final Project project )
    {
        if ( project == null )
        {
            throw new BuildException( "No Project specified" );
        }
        Object o = ref.getReferencedObject( project );
        if ( !( requiredClass.isAssignableFrom( o.getClass() ) ) )
        {
            log( "Class " + o.getClass() + " is not a subclass of " + requiredClass, Project.MSG_VERBOSE );
            String msg = ref.getRefId() + " doesn\'t denote a " + dataTypeName;
            throw new BuildException( msg );
        }
        return o;
    }

}
