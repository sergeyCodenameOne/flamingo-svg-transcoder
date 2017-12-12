Flamingo SVG Transcoder (Codename One Port)
===========================================

This is a fork of [the Swing based Flamingo SVG Transcoder](https://github.com/ebourg/flamingo-svg-transcoder) to work with [Codename One's mobile API](https://www.codenameone.com/) instead of Java2D.

Not all functionality or usage is implemented, gradients just pick the start color instead and no animations are available (weren't supported in source port either). The code is relatively simple and easy to enhance if you need support for additional features.

## Info
https://www.codenameone.com/blog/flamingo-svg-transcoder.html

## Compiling
If you are in trouble on compiling the sources to jar, you can use:
https://github.com/codenameone/flamingo-svg-transcoder/blob/master/flamingo-svg-transcoder-core-1.2-jar-with-dependencies.jar

### Hints to compiling:
Install maven

Download flamingo-svg-transcoder-master.zip and extract it in your home

Remove ~/flamingo-svg-transcoder-master/core/src/test (because the tests generate errors)

Add to ~/flamingo-svg-transcoder-master/core/pom.xml in the <build><plugins> section:
  
```
<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <configuration>
    <archive>
      <manifest>
        <mainClass>org.pushingpixels.flamingo.api.svg.SvgBatchConverter</mainClass>
      </manifest>
    </archive>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
  </configuration>
  <executions>
    <execution>
      <id>make-assembly</id> <!-- this is used for inheritance merges -->
      <phase>package</phase> <!-- bind to the packaging phase -->
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

In the terminal:
```
cd ~/flamingo-svg-transcoder-master/core/
mvn clean compile
mvn package
```

The resulting package is:
```
~/flamingo-svg-transcoder-master/core/target/flamingo-svg-transcoder-core-1.2-jar-with-dependencies.jar
```

Simple test:
```
java -jar ~/flamingo-svg-transcoder-master/core/target/flamingo-svg-transcoder-core-1.2-jar-with-dependencies.jar
The output is:
param 0 : dir, param 1 : pkg
```

## Example of Usage
Example of converting SVG to Java (in the directory ~/mySVGfiles there is the file "Logo.svg"):
```
java -jar ~/flamingo-svg-transcoder-master/core/target/flamingo-svg-transcoder-core-1.2-jar-with-dependencies.jar ~/mySVGfiles  com.yourcompany.app.svg
The output is:
Processing Logo.svg
```

## Tips and tricks to get a working Java code from a simple SVG image:

1. Before export your vectorial drawing to SVG, if you can:

1a. transform any special object (like spirals, circles or stars) in a pure path;

2a. remove all transformations.

2. Export your vectorial drawing to SVG in the simplest format, that means without incorporated rasters, without incorporated fonts (export text as curves), without gradients or translucents (full opaque or full transparent colors are fine). If you can choose the SVG version, SVG 1.0 is fine.

A method to remove transformations (it should work in most simple cases):
1. Load your SVG in Method Draw http://editor.method.ac (File > Open Image).
(Method Draw is a vector editor for the web, it is open source and you can find it on Github: https://github.com/duopixel/Method-Draw)
2. Select the image and ungroup your elements (Object > Ungroup elements), you might have to do this more than once.
3. Keeping the image selected, reorient the path (Object > Reorient Paths).
4. Save your image (File > Save Image).

Another method to remove transformations (if the previous method failed because Method Draw doesn't load correctly the image):
1. Ensure that the SVG has only one layer (you can use the XML editor of Inkscape)
2. Install Affinity Designer (there is a ten-days trial version for Windows and Mac)
3. Import the SVG in Affinity Designer and export with "no rasters", "use relative coordinates", "use hex colors", "flatten transforms", "set viewbox", "add line breaks".
3. Try to use Method Draw (as above) with the just exported SVG: now it should load correctly the image.
4. Last resort after all above trials: if you get errors in the generate java code related to unused gradients, unused transformations or unused variables, fix the code manually. In a desperate case, I've done few manual corrections and then the code started to work... :-)

More info about removing transformations from an SVG: https://stackoverflow.com/questions/13329125/removing-transforms-in-svg-files

