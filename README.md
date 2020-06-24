# More recent alternative

https://github.com/pemistahl/lingua

# Introduction

Tools for language detection in Java.

Hacking to make language detection for map specific names like POIs and street names.

Currently there are the following approaches:

 * Our language detection languator is fast and has good accuracy (less than 23% error) for short length location-based names
 * We take the existing tool at
   https://code.google.com/p/language-detection and feed this with OpenStreetMap
   data to improve detection for short text (profile.map). 
   Detection speed is not that good. If the normal short message profile (profile.sm) is used the accuracy is slightly worse compared to our OSM approach.
 * Possible improvement? We mix both tools e.g. the fast detection first and then the language-detection tool

## Installation

You'll need to run the script in libs/maven-install.sh to make the artifacts available via maven

## License

The code from language-detection stands under Apache License 2

Languator stands under Apache License 2
