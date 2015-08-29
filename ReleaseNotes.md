# Introduction #

Release Notes


# Details #
```
Release notes for EProperties.


Contact: Paul Bemowski, bemowski@yahoo.com

===============================================================================

1.0.3 Released Feb 6 2010
  - Fixed potential NPE in the toString() method.  toString is now over
    ridden, and returns the same value as the list() method.
  - Updates to Lists
    - Added a getList(String key, List<String>def) method - allowing the
      user to get lists with default values.
    - Added a function to convert a CSV String to a list when the
      getList() method is called, and the value is a String.
  - Updates to JavaDoc

1.0.2 Released 24 Nov 2009
  - Fixed CacheManager when EProperties.load(InputStream) was called.  It 
    was throwing an NPE.
  - Added unit testing stuff, including build time tests.
  - Added tests for loading properties via InputStream and zip/jar URL.

1.0.1 
  - Packaging for deployment


1.0.0    Unreleased.  
  - This version defines the starting point for the open source version 
    of extended properties.
  - This version is checked into SVN, but was never packaged for deployment.
```