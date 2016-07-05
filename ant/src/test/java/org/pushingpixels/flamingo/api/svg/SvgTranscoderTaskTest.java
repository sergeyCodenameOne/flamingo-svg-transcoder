/**
 * Copyright 2016 Emmanuel Bourg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pushingpixels.flamingo.api.svg;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SvgTranscoderTaskTest extends TestCase {

    private Project project;

    protected void setUp() throws Exception {
        project = new Project();
        project.setCoreLoader(getClass().getClassLoader());
        project.init();

        File buildFile = new File("target/test-classes/testbuild.xml");
        project.setBaseDir(buildFile.getParentFile());

        ProjectHelper helper = ProjectHelper.getProjectHelper();
        helper.parse(project, buildFile);

        redirectOutput(System.out);
    }

    /**
     * Redirects the Ant output to the specified stream.
     */
    private void redirectOutput(OutputStream out) {
        DefaultLogger logger = new DefaultLogger();
        logger.setOutputPrintStream(new PrintStream(out));
        logger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener(logger);
    }

    public void testInvalidNamingStrategy() {
        try {
            project.executeTarget("invalid-naming-strategy");
            fail("BuildException expected");
        } catch (BuildException e) {
            // expected
        }
    }
    
    public void testTranscode() {
        project.executeTarget("transcode");
        
        File transcoded = new File("target/test-classes/ApacheFeather.java");
        assertTrue(transcoded + " wasn't generated", transcoded.exists());
    }
}
