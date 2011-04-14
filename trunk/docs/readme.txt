
The EProperties system Extends the extends the existing java.util.Properties
object.  Extensions to the properties system include:

  - Ability to nest properties objects.
  - Ability to define Lists of strings as properties
  - Ability to perform dynamic substitution of keys and values
  - Ability to include properties from other arbitrary sources
  - A simple, lightweight, intuitive syntax for defining these extensions
    in a file or stream.
  
  - An even listener mechanism, that listens for changes to the values
    of properties within an EProperties set. 
  
  - An arbitratry load/save persistence mechanism, tied to the inclusion
    mechanism.
    
By using these core features, it is possible to define dynamic property 
sets for a variety of common tasks.

  - Property inclusion and override
    - Useful for environment specific configuration
    - Useful for arbitrary extension mechanisms.
    
  - Supports Factory type patterns.
    - Allows runtime configuration of object implementations using a 
      powerful code-like syntax. 
    - Supports the 'Instance Factory' pattern to accomplish this goal.
    
    
  - Use as a basis for dependency injection in a structured way w/o XML.
