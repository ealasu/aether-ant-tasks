package org.sonatype.aether.ant;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.Arrays;

import org.sonatype.aether.test.util.TestFileUtils;

/*
 * still missing:
 * - deploy snapshots/releases into correct repos
 */
public class DeployTest
    extends AntBuildsTest
{

    private File distRepoPath;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        distRepoPath = new File( "target/dist-repo" );
        System.setProperty( "project.distrepo.url", distRepoPath.toURI().toString() );
        TestFileUtils.delete( distRepoPath );

        configureProject( "src/test/ant/Deploy.xml" );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        TestFileUtils.delete( distRepoPath );
    }

    public void testDeployGlobalPom()
    {
        executeTarget( "testDeployGlobalPom" );
        long tstamp = System.currentTimeMillis();

        assertLogContaining( "Uploading" );
        
        assertUpdatedFile( tstamp, distRepoPath, "test/test/0.1-SNAPSHOT/maven-metadata.xml" );
    }

    public void testDeployOverrideGlobalPom()
    {
        executeTarget( "testDeployOverrideGlobalPom" );
        long tstamp = System.currentTimeMillis();

        assertLogContaining( "Uploading" );

        assertUpdatedFile( tstamp, distRepoPath, "test/other/0.1-SNAPSHOT/maven-metadata.xml" );
    }

    public void testDeployOverrideGlobalPomByRef()
    {
        long tstamp = System.currentTimeMillis();
        executeTarget( "testDeployOverrideGlobalPomByRef" );

        assertLogContaining( "Uploading" );

        assertUpdatedFile( tstamp, distRepoPath, "test/test/0.1-SNAPSHOT/maven-metadata.xml" );
        assertUpdatedFile( tstamp, distRepoPath, "test/other/0.1-SNAPSHOT/maven-metadata.xml" );
    }

    public void testDeployAttachedArtifact()
    {
        executeTarget( "testDeployAttachedArtifact" );

        assertLogContaining( "Uploading" );

        File dir = new File(distRepoPath, "test/test/0.1-SNAPSHOT/" );
        String[] files = dir.list();
        assertThat( "attached artifact not found: " + Arrays.toString( files ), files,
                    hasItemInArray( endsWith( "-ant.xml" ) ) );
    }

    private void assertUpdatedFile( long tstamp, File repoPath, String path )
    {
        File file = new File( repoPath, path );
        assertThat( "File does not exist in default repo: " + file.getAbsolutePath(), file.exists() );
        assertThat( "Files were not updated for 1s before/after timestamp",
                    file.lastModified(),
                    allOf( greaterThanOrEqualTo( ( ( tstamp - 500 ) / 1000 ) * 1000 ),
                           lessThanOrEqualTo( tstamp + 2000 ) ) );
    }
}
