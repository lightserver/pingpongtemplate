## Ping Pong scalajs template

This example  shows build system for scalajs and web application - based
on react.js.
It uses both **sbt** and **gulp**.

## Project layout

#app - scala files with 3 sections
    #app/JS - scalajs code (for web)
    #app/JVM  - server code
    #app/shared -shared code (both web and jvm)
#web - webapplication

## Development

IN order to run incremental development cycle You need three commands.
#sbt appJVM/run  - run server
#sbt ~fastOptJS  - run JS recompilation
#cd web; gulp - run local web publishing



## Release
#web/gulp
#sbt appJVM/package



