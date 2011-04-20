package org.sonatype.aether.ant;

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

import java.io.File;
import java.io.PrintStream;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.DefaultLogger;
import org.sonatype.aether.test.impl.TestFileProcessor;

public abstract class AntBuildsTest
    extends BuildFileTest
{
    private static final SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();

    private static final SettingsDecrypter settingsDecrypter = new AntSettingsDecryptorFactory().newInstance();

    private static final String SETTINGS_XML = "settings.xml";

    protected static File projectDir;

    protected static File antDir;

    static
    {
        projectDir = new File( "." );
        antDir = new File( projectDir, "src/test/ant" );
        System.setProperty( "project.dir", projectDir.getAbsolutePath() );
        System.setProperty( "project.dir.ant", antDir.getAbsolutePath() );
    }

    protected TestFileProcessor fp;

    protected File defaultLocalRepository;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        this.fp = new TestFileProcessor();


        Settings settings = getSettings();
        String mavenRepoProperty = System.getProperty( "maven.repo.local" );
        if ( mavenRepoProperty != null )
        {
            defaultLocalRepository = new File( mavenRepoProperty );
        }
        else if ( settings.getLocalRepository() != null )
        {
            defaultLocalRepository = new File( settings.getLocalRepository() );
        }
        else
        {
            defaultLocalRepository = new File( System.getProperty( "user.home" ), ".m2/repository" );
        }
    }

    @Override
    public void configureProject( String filename, int logLevel )
        throws BuildException
    {
        super.configureProject( filename, logLevel );
        DefaultLogger logger = new DefaultLogger()
        {
            @Override
            protected void printMessage( String message, PrintStream stream, int priority )
            {
                message = System.currentTimeMillis() + " " + message;
                super.printMessage( message, stream, priority );
            }
        };
        logger.setMessageOutputLevel( logLevel );
        logger.setOutputPrintStream( System.out );
        logger.setErrorPrintStream( System.err );
        getProject().addBuildListener( logger );
    }

    protected synchronized Settings getSettings()
        throws SettingsBuildingException
    {
        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        request.setUserSettingsFile( getUserSettings() );

        Settings settings = settingsBuilder.build( request ).getEffectiveSettings();
        SettingsDecryptionResult result = settingsDecrypter.decrypt( new DefaultSettingsDecryptionRequest( settings ) );
        settings.setServers( result.getServers() );
        settings.setProxies( result.getProxies() );
        return settings;
    }

    protected File getUserSettings()
    {
        File userHome = new File( System.getProperty( "user.home" ) );
        File file = new File( new File( userHome, ".ant" ), SETTINGS_XML );
        if ( file.isFile() )
        {
            return file;
        }
        else
        {
            return new File( new File( userHome, ".m2" ), SETTINGS_XML );
        }
    }
}
