#   Associativity

##  Game Tables Assets Subdirectory Organisation

Valid game tables' [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) should be put in one of the following subdirectories according to their difficulty level:

*   [*1*](1) for *easy* game tables,
*   [*2*](2) for *medium* game tables,
*   [*3*](3) for *hard* game tables.

No other difficulty level is allowed and to allow it you should reprogram the app. For instance, if you create and populate a directory named *4*, you will not be able to play these game tables by default&mdash;you will have to make some changes in the app's code to reach these game tables. Note that a label of *0* in the app's code is currently reserved for custom game tables.

**Nota bene. Do not put anything but valid game tables' [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) in subdirectories mentioned above. When reading a file from the subdirectories the app does not check if it is a valid game table's [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) bu automatically assumes it is, which may cause the app to crash if it is not valid.**
