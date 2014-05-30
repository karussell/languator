Hacking to make language detection for map specific names like POIs and street names.

Currently there are the following approaches:

 * A very simple keywords based approach which is fast and reaches over
   99% accuracy but has limited language support and it might be that it won't scale for many languages.
 * We take the existing tool at
   https://code.google.com/p/language-detection and feed this with OpenStreetMap
   data to improve detection for short text (profile.map). It takes 6 minutes for entire Germany to create one language for this profile.
   Detection speed is not that good
   but okayish if not executed too often e.g. for our purpose of identifying
   the language of query strings. If the normal short message profile (profile.sm) is used the
   accuracy is slightly worse compared to our own.
 * Possible improvement? We mix both tools e.g. the fast detection first and then the language-detection tool

## Installation

You'll need to run the script in libs/maven-install.sh to make the artifacts available via maven