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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

/**
 * Template replacing tokens with values to create a Java source file.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Template {

    public enum Token {
        PACKAGE, CLASSNAME, PAINTING_CODE, X, Y, WIDTH, HEIGHT
    }

    private URL url;
    private String template;

    public Template(String resource) throws IOException {
        load(getClass().getResource(resource));

    }

    public Template(URL url) throws IOException {
        load(url);
    }

    private void load(URL url) throws IOException {
        InputStream in = url.openStream();
        this.url = url;

        try {
            in = url.openStream();
            StringBuilder buffer = new StringBuilder();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            
            template = buffer.toString();
        } finally {
            in.close();
        }
    }

    public void apply(PrintWriter out, Map<Token, Object> params) {
        String template = this.template;
        
        for (Token token : params.keySet()) {
            template = template.replaceAll("\\$\\{" + token.name() + "}", params.get(token).toString());
        }
        
        out.println(template);
        out.close();
    }

    public URL getURL() {
        return url;
    }

    public static Template getDefault() {
        try {
            return new Template("plain.template");
        } catch (IOException e) {
            throw new RuntimeException("Could not create default template", e);
        }
    }
}
