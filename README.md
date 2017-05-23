Flamingo SVG Transcoder (Codename One Port)
===========================================

This is a fork of [the Swing based Flamingo SVG Transcoder](https://github.com/ebourg/flamingo-svg-transcoder) to work with [Codename One's mobile API](https://www.codenameone.com/) instead of Java2D.

Not all functionality or usage is implemented, gradients just pick the start color instead and no animations are available (weren't supported in source port either). The code is relatively simple and easy to enhance if you need support for additional features.

=== Usage

This can be invoked from command line for a one time conversion using 

````
java -jar path_to_directory_with_svg_files com.package.name.for.generated.java.files
````

There is an ant task that can probably be used as well but I haven't tested it.
