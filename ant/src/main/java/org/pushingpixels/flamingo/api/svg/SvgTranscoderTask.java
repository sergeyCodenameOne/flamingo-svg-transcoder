/**
 * Copyright 2011 Emmanuel Bourg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pushingpixels.flamingo.api.svg;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;

/**
 * Ant task performing the SVG to Java2D transformation on a single file or on a fileset.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SvgTranscoderTask extends Copy {
    
    private NamingStrategy namingStrategy = new DefaultNamingStrategy();
    
    private String targetPackage;
    
    /** The template to use for the generated classes. */
    private Template template = Template.getDefault();

    public void setPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public void setTemplate(String template) throws IOException {
        this.template = new Template(template.toLowerCase() + ".template");
    }

    /**
     * Set the naming strategy for the generated classes ("default" or "camelcase")
     */
    public void setNamingStrategy(String strategy) {
        switch (strategy) {
            case "camelcase":
                namingStrategy = new CamelCaseNamingStrategy();
                break;
            case "default":
                namingStrategy = new DefaultNamingStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unsupported naming strategy: " + strategy);
        }
    }

    @Override
    protected void validateAttributes() throws BuildException {
        super.validateAttributes();
    }
    
    public void execute() {
        // define the default mapper if none is specified
        if (mapperElement == null) {
            GlobPatternMapper mapper = new GlobPatternMapper();
            mapper.setFrom("*.svg");
            mapper.setTo("*.java");
            add(new FileNameMapper() {
                public void setFrom(String from) { }
                public void setTo(String to) { }
                public String[] mapFileName(String filename) {
                    return new String[] { namingStrategy.getClassName(new File(filename)) + ".java" };
                }
            });
        }
        
        super.execute();
    }
    
    protected void doFileOperations() {
        if (fileCopyMap.size() > 0) {
            File basedir = getProject().getBaseDir();
            log("Converting " + fileCopyMap.size() + " file" + (fileCopyMap.size() == 1 ? "" : "s") + " to " + destDir.getAbsolutePath());
            
            Enumeration e = fileCopyMap.keys();
            while (e.hasMoreElements()) {
                String fromFile = (String) e.nextElement();
                String[] toFiles = (String[]) fileCopyMap.get(fromFile);

                for (String toFile : toFiles) {
                    File from = new File(fromFile);
                    File to = new File(toFile);
                    try {
                        String f = fileUtils.isLeadingPath(basedir, from) ? fileUtils.removeLeadingPath(basedir, from) : from.toString();
                        String t = fileUtils.isLeadingPath(basedir, to) ? fileUtils.removeLeadingPath(basedir, to) : to.toString();
                        log("Converting " + f + " to " + t, verbosity);
                        transcode(from, to);
                        
                    } catch (IOException ioe) {
                        String msg = "Failed to convert " + fromFile + " to " + toFile + " due to " + ioe.getMessage();
                        if (to.exists() && !to.delete()) {
                            msg += " and I couldn't delete the corrupt " + toFile;
                        }
                        throw new BuildException(msg, ioe, getLocation());
                    }
                }
            }
        }
    }

    private void transcode(File file, File target) throws IOException {
        target.getParentFile().mkdirs();
        PrintWriter pw = new PrintWriter(target);
        
        SvgTranscoder transcoder = new SvgTranscoder(file.toURI().toURL(), namingStrategy.getClassName(file));
        transcoder.setTemplate(template);
        transcoder.setJavaPackageName(targetPackage);
        transcoder.setPrintWriter(pw);
        transcoder.transcode();
        
        pw.close();
    }
}
