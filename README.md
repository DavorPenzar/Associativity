#   Associativity

Associations game Android app.

##  Defining New Game Tables

New game tables can be defined by [*CSV* files (*comma-separated values*)](http://wikipedia.org/wiki/Comma-separated_values). The format of the *CSV* values is extensively described in the documentation of [*TableReader* class](app/src/main/java/com/example/associativity/TableReader.kt), but essentially comes down to the following:

1.  All *CSV* files are considered tables of **strings**&mdash;all numbers, usual date formats and other data types are parsed as strings. For instance, an input of *12345* will not result in an integer with the value of `12345`, i. e. twelve thousand three hundered and fourty five, but a string `"12345"`, even if all cells in the column are valid integer expressions.
2.  The separator **must** be a comma, i. e. the character `','` ([ASCII](http://wikipedia.org/wiki/ASCII) value *44*, in hex. *2C*).
3.  Single quotes **must** be regular single quotes, i. e. the character `'\''` ([ASCII](http://wikipedia.org/wiki/ASCII) value *39*, in hex. *27*). Double quotes **must** be regular double quotes, i. e. the character `'\"'` ([ASCII](http://wikipedia.org/wiki/ASCII) value *34*, in hex. *22*).
4.  Escape character **must** be the backslash, i. e. the character `'\\'` ([ASCII](http://wikipedia.org/wiki/ASCII) value *92*, in hex. *5C*).

&hellip;

More will be added soon ;).
