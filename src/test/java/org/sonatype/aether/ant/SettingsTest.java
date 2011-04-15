package org.sonatype.aether.ant;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

public class SettingsTest
    extends AntBuildsTest
{

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        configureProject( "src/test/ant/Settings.xml" );
    }

    public void testUserSettings()
    {
        executeTarget( "testUserSettings" );
        assertThat( "user settings not set", AntRepoSys.getInstance( getProject() ).getUserSettings().getName(),
                    equalTo( "userSettings.xml" ) );
    }

    public void testGlobalSettings()
    {
        executeTarget( "testGlobalSettings" );
        assertThat( "global settings not set", AntRepoSys.getInstance( getProject() ).getGlobalSettings().getName(),
                    equalTo( "globalSettings.xml" ) );
    }

    public void testBothSettings()
    {
        executeTarget( "testBothSettings" );
        assertThat( "global settings not set", AntRepoSys.getInstance( getProject() ).getGlobalSettings().getName(),
                    equalTo( "globalSettings.xml" ) );
        assertThat( "user settings not set", AntRepoSys.getInstance( getProject() ).getUserSettings().getName(),
                    equalTo( "userSettings.xml" ) );
    }

    public void testFallback()
        throws IOException
    {
        executeTarget("setUp");
        assertThat( "no fallback to local settings",
                    AntRepoSys.getInstance( getProject() ).getUserSettings().getAbsolutePath(),
                    endsWith( ".m2/settings.xml" ) );
    }
}
