Flamingo SVG Transcoder (Codename One Port)
===========================================

This is a fork of [the Swing based Flamingo SVG Transcoder](https://github.com/ebourg/flamingo-svg-transcoder) to work with [Codename One's mobile API](https://www.codenameone.com/) instead of Java2D.

Not all functionality or usage is implemented, gradients just pick the start color instead and no animations are available (weren't supported in source port either). The code is relatively simple and easy to enhance if you need support for additional features.

## Info
https://www.codenameone.com/blog/flamingo-svg-transcoder.html

## Compiling
If you are in trouble on compiling the sources to jar, you can use:
https://github.com/jsfan3/flamingo-svg-transcoder/blob/master/flamingo-svg-transcoder-core-1.2-jar-with-dependencies.jar

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
