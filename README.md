EProperties
===========

This is a fork of the original EProperties project created by Paul Bemowski <bemowski@yahoo.com>

https://code.google.com/p/eproperties/

EProperties had been unmaintained for a long time. This forks hopes to clean up some of the logging, dependencies and build system.

EProperties is actively used in [BigSense](http://bigsense.io), however I don't have the capacity to actively develop it. It currently does what I need to, but there are a lot of TODOs in the original authors source code and documentation. I welcome any pull requests. 

Things I've changed
-------------------

* Gradle Build/Dependency System (replacing ant)
* Replaced all commons-logging with slf4j/logback
* Removed unnecessary System.out.println

Todo
----
* Fix all tests
* Publish to central maven repo