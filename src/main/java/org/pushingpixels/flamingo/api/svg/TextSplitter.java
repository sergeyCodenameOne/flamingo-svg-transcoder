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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Inserts a string inside a text every n lines, on blank lines only.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class TextSplitter {

    static String insert(String content, String separator, int limit) {
        if (content.trim().length() == 0) {
            return content;
        }

        StringBuilder out = new StringBuilder();
        
        List<Chunk> chunks = getChunks(content);
        int accumulatedLines = 0;
        int separatorCount = 0;

        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            if (accumulatedLines > 0 && accumulatedLines + chunk.lines > limit) {
                out.append(separator.replaceAll("\\$\\{count}", String.valueOf(++separatorCount)));
                out.append('\n');
                accumulatedLines = 0;
            } else if (i > 0) {
                out.append('\n');
            }

            accumulatedLines += chunk.lines;
            out.append(chunk.content);
        }

        return out.toString();
    }
    
    static List<Chunk> getChunks(String content) {
        List<Chunk> chunks = new LinkedList<Chunk>();
        
        Chunk chunk = new Chunk();
        chunks.add(chunk);

        try {
            LineNumberReader reader = new LineNumberReader(new StringReader(content));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    chunk = new Chunk();
                    chunks.add(chunk);
                } else {
                    chunk.content.append(line).append('\n');
                    chunk.lines++;
                }
            }

        } catch (IOException e) {
        }
        
        return chunks;
    }

    private static class Chunk {
        StringBuilder content = new StringBuilder();
        int lines;
    }
}
