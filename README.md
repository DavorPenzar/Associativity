#   Associativity

Associations game Android app.

##  Defining New Game Tables

New game tables can be defined in [*CSV* files (*comma-separated values*)](http://wikipedia.org/wiki/Comma-separated_values). If you are unfamiliar with the format, follow the link, although to produce valid game tables a leading spreadheet software will suffice (you may even skip to [**Game Table Format**](#game-table-format)&mdash;however, read the first 5 or so points in [**Supported *CSV* Format**](#supported-csv-format) to know which options to select when producing a table using spreadsheet software (default options will probably be OK)).

### Supported [*CSV* Format](http://wikipedia.org/wiki/Comma-separated_values)

The format of the *CSV* files supported by the implemented parser is extensively described in the documentation of [*TableReader* class](app/src/main/java/com/example/associativity/TableReader.kt), but essentially comes down to the following:

1.  The actual extension of the file does not have to be *.csv*/*.CSV*, because the actual filename extension is nothing more but a part of the file's name (see [***Filename extension*: Usage**](http://en.wikipedia.org/wiki/Filename_extension#Usage)). All filename extensions&mdash;including no extension at all&mdash;are supported; the actual contents of the file are what matters.
2.  All *CSV* files are considered tables of **strings**&mdash;all numbers, usual date formats and other data types and their respectful usual formats are parsed as strings. For instance, an input of *12345* will not result in an integer with the value of `12345`, i. e. *twelve thousand three hundered and fourty five*, but a string `"12345"`, even if all cells in the column are valid integer expressions.
3.  The separator **must** be a comma, i. e. the character `','` ([ASCII](http://wikipedia.org/wiki/ASCII) value *44*, in hex. *2C*).
4.  Single quotes **must** be regular single quotes, i. e. the character `'\''` ([ASCII](http://wikipedia.org/wiki/ASCII) value *39*, in hex. *27*). 
5.  Double quotes **must** be regular double quotes, i. e. the character `'\"'` ([ASCII](http://wikipedia.org/wiki/ASCII) value *34*, in hex. *22*).
6.  Escape character **must** be the backslash, i. e. the character `'\\'` ([ASCII](http://wikipedia.org/wiki/ASCII) value *92*, in hex. *5C*).
7.  Valid escaping expressions are the esacpe character (see above) **immediately** followed by one of the characters listed in the table directly below this numbered list (naturally, the table also lists the resulting characters).
8.  The only *NA* (*not assigned*) value is an empty cell.  Cells containing the usual *NA* indicators, such as `"NA"`, `"na"`, `"NaN"`, `"nan"` etc., are parsed as non-empty strings (for instance,`"NA"` results in a cell containing the string `"NA"`).
9.  Escaping a separator is not mandatory if the cell is enclosed in quotes. Also, escaping double quotes in cells enclosed by single quotes and vice versa is not mandatory. However, these characters may also be escaped in cells enclosed by quotes, therefore be careful how the escape character is used even if the cell is enclosed in quotes.
10. If a cell is not enclosed in quotes, its content is *trimmed* (leading and trailing whitespaces are ignored). Leading and trailing whitespaces before and after quotes (if a cell is enclosed in quotes) are also ignored. Some other [*CSV*](http://wikipedia.org/wiki/Comma-separated_values) parsers do not allow characters other than the separator or the line break before and after the enclosing quotes, but the one implemented in this project does. This allows formatting [*CSV* files](http://wikipedia.org/wiki/Comma-separated_values) to be easily readable by a human.
11. Empty lines and lines containing only whitespace characters are ignored. This allows extra formatting of [*CSV* files](http://wikipedia.org/wiki/Comma-separated_values) (for instance, separating, thus emphasising, the header by an empty line).
12. Rows do not have to contain the same number of cells. If a row contains *n* cells, they are considered to be its **first** *n* cells. For instance, suppose the first row contains 5 cells, but the second one only 4 cells, then the 4 cells in the second row are considered to be directly below the first (the leftmost) 4 cells of the first row, leaving the fifth cell in the first row without a pair from the second row in its own column.
13. Although this is standard [*CSV*](http://wikipedia.org/wiki/Comma-separated_values) practice, it is worth mentioning that adding extra separators in the beginning and/or the end of a row will result in empty cells in the beginning and/or the end. The first cell of a row starts **immediately at the beginning of the row's line**, and the last one ends **at the very end of the row's line** (however, leading and trailing whitespaces will be ignored in cases explained above).

| Escaping character | Esc. ASCII dec. | Esc. ASCII hex. | Resulting character | Res. name       | Res. ASCII dec. | Res. ASCII hex. |
| :----------------: |---------------: |---------------: | :-----------------: | :---------------|---------------: |---------------: |
| `'b'`              | *98*            | *62*            | `'\b'`              | backspace       | *8*             | *8*             |
| `'t'`              | *116*           | *74*            | `'\t'`              | horizontal tab  | *9*             | *9*             |
| `'n'`              | *110*           | *6E*            | `'\n'`              | line break      | *10*            | *A*             |
| `'r'`              | *114*           | *72*            | `'\r'`              | carriage return | *13*            | *D*             |
| `'f'`              | *102*           | *66*            | `'\f'`              | page break      | *12*            | *C*             |
| `'a'`              | *97*            | *61*            | `'\a'`              | alert (bell)    | *7*             | *7*             |
| `'\''`             | *39*            | *27*            | `'\''`              | single quote    | *39*            | *27*            |
| `'\"'`             | *34*            | *22*            | `'\"'`              | double quotes   | *34*            | *22*            |
| `'\\'`             | *92*            | *5C*            | `'\\'`              | backslash       | *92*            | *5C*            |
| `'e'`              | *101*           | *65*            | `'\\'`              | backslash       | *92*            | *5C*            |
| `','`              | *44*            | *2C*            | `','`               | comma           | *44*            | *2C*            |

### Game Table Format

The actual format of tables to define a game table is the following:

1.  The table must contain **at least 6 rows**. Keep reading to see why.
2.  In general, the table should contain 5 columns (some rows may contain only 4 cells, but that will be explained later). The first 4 columns represent columns *A*, *B*, *C* and *D* of the game table, while the last column represents the final solution.
3.  The first row must contain **exactly 5 cells**. Each of the 5 cells may be **either `"0"` or `"1"`** (empty cells are not allowed). A value of `"0"` means that cells in the column must not be shuffled, and a value of `"1"` means that they may be shuffled. The fifth column refers to the order of the columns, not cells in the columns. For instance, if a `"0"` appears in one of the first 4 columns, but `"1"` is in the fifth column, cells inside the column with a `"0"` will not be shuffled, but the column may appear as a column *A*, *B*, *C* or *D*. The same, *mutatis mutandis*, applies if a `"1"` appears in one of the first 4 columns, but a `"0"` appears in the last column. See examples below to understand why this feature was included.
4.  Rows 2 &ndash; 5 (inclusive) may contain **exactly 4 or 5 cells**, but all of them must be **of the same length (number of cells)** and, if they contain 5 cells, the fifth celll must be **empty**. These rows define the values of cells inside columns *A*, *B*, *C* and *D*.
5.  Rows 6 until the end (inclusive) must contain **exactly** 5 cells. These rows define the solutions of columns and the final solution, along with alternative acceptable answers (such as synonyms, abbreviations, alternative spellings etc.). The sixth row defines the exact solutions of columns and the final solution (the one that appears when the user types one of the acceptable answers), while the following rows define alternative acceptable answers. For instance, if *Times New Roman* appears in a column in the sixth row, but *Times* appears in the seventh row, and a user types in *Times* when playing the game, the answer will be accepted and the column will be opened, but *Times New Roman* is going to be displayed as the solution of the column.
6.  All acceptable answers must be **agregated at the top**. This means that if one of the columns has only 3 acceptable answers (including the exact solution), these answers must appear in rows 6, 7 and eigth. For instance, answers appearing in rows 6, 7 and 9, but leaving an empty cell in row 8 is not accepted. If another column has 5 acceptable answers, the column with only 3 acceptable answers should contain empty cells after the acceptable answers were listed.
7.  Acceptable answers are case-insensitive (adding *florence nightingale* as an acceptable answer for *Florence Nightingale* has no effect&mdash;the answer will automatically be accepted even without explicitly mentioning it). However, as it is explained in the documentation of [`MainActivity.isAcceptable` method](app/src/main/java/com/example/associativity/MainActivity.kt#L239), usual formal and informal transcriptions of *special characters* are not equated with their respective original characters by default (follow the link to see why). This means that in order to accept *Strasse* as an answer for *Straße*, the answer *Strasse* must be explicitly added. The same applies to letters such as *Č*, *č* in Serbo-Croatian and their respective usual alternatives such as *C*, *c*.

If any of the conditions mentioned above is not satisfied, an uncaught exception will be thrown causing the app to crash. Of course, if the [*CSV* file](http://wikipedia.org/wiki/Comma-separated_values) does not satisfy the format stated in [**Supported *CSV* Format**](#supported-csv-format), an uncaught exception will be thrown as well.

An example of a column (or the final solution for that matter&mdash;suppose cells in the column are solutions of some other columns) where the order of associations matters is the column *C* in the following associations game table (try to solve the table &#128521;).

| A         | B             | C      | D            |
| :-------: | :-----------: | :----: | :----------: | 
| sheet     | ice           | Mary   | Google Drive |
| cellulose | Christmas     | had    | storm        |
| science   | flakes        | a      | sky          |
| printer   | mountain tops | little | steam        |

A valid [*CSV* file](http://wikipedia.org/wiki/Comma-separated_values) defining the table above would contain (solutions are masked sou you would not be deprived of the fun solving the table)

```
1         , 1             , 0      , 1            , 1

sheet     , ice           , Mary   , Google Drive ,
cellulose , Christmas     , had    , storm        ,
science   , flakes        , a      , sky          ,
printer   , mountain tops , little , steam        ,

XXXXX     , XXXX          , XXXX   , XXXXX        , XXXXX
XXXXXX    , XXXXX         , XXXXX  , XXXXXX       , XXXXXXXXX
          , XXXXXXX       ,        , XXXXXX       ,

```

The format above is human-friendly, while a spreadsheet software would probably produce a [*CSV* file](http://wikipedia.org/wiki/Comma-separated_values) containing (again, the solutions are masked)

```
1,1,0,1,1
sheet,ice,Mary,Google Drive,
cellulose,Christmas,had,storm,
science,flakes,a,sky,
printer,mountain tops,little,steam,
XXXXX,XXXX,XXXX,XXXXX,XXXXX
XXXXXX,XXXXX,XXXXX,XXXXXX,XXXXXXXXX
,XXXXXXX,,XXXXXX,

```

In both formats shown commas at the end of the rows 2 &ndash; 5 may be omitted but, if a final comma is omitted in at least one of the rows, final commas must be omitted in all of them. All 4 formats are supported and will result in the same game table.

**Nota bene. Although solutions in the example above are masked, if you mask the solutions in your [*CSV* file](http://wikipedia.org/wiki/Comma-separated_values) the actual solutions will be *XXXXX* and so on. The app cannot *magically* read your mind, nor is any machine-learning algorithm for solving implemented in the app, to fill in the solutions automatically.**

### Producing a Game Table Using Spreadsheet Software

The easiest way to create a [*CSV* file](http://wikipedia.org/wiki/Comma-separated_values) of a game table is using a spreadsheet software such as [Google Sheets](http://google.com/sheets/about/), [Microsoft Office Excel](http://products.office.com/excel) or [LibreOffice Calc](http://libreoffice.org/discover/calc/). In the software of your choice enter the game table as

|       | A         | B             | C      | D            | E         |
| :---: | :---------| :-------------| :------| :------------| :---------|
| **1** | 1         | 1             | 0      | 1            | 1         |
| **2** | sheet     | ice           | Mary   | Google Drive |           |
| **3** | cellulose | Christmas     | had    | storm        |           |
| **4** | science   | flakes        | a      | sky          |           |
| **5** | printer   | mountain tops | little | steam        |           |
| **6** | XXXXX     | XXXX          | XXXX   | XXXXX        | XXXXX     |
| **7** | XXXXXX    | XXXXX         | XXXXX  | XXXXXX       | XXXXXXXXX |
| **8** |           | XXXXXXX       |        | XXXXXX       |           |

Again, be sure to add actual solutions, not strings such as *XXXXX*, in rows 6 until the end. Of course, if your game table is a *kinky* one and one of the solutions or an alternative acceptable answer is *XXX*, then you should put *XXX* as the answer in the corresponding column.

The next step is to export the sheet to a [*CSV* file](http://wikipedia.org/wiki/Comma-separated_values). This is usually done by *File* > *Export as...*/*Save as...*/*Download* (use *Download* in online software such as [Google Sheets](http://google.com/sheets/about/) and [Microsoft Office 365](http://office.com/)) and choosing [*CSV*](http://wikipedia.org/wiki/Comma-separated_values) as the format. As mentioned above, default options will probably be OK, but check [**Supported *CSV* Format**](#supported-csv-format) to see which options to select.

&hellip;

More will be added soon ;).
