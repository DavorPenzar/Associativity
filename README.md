#   Associativity

[*Android*](http://android.com/) app for the famous associations game.

If you are not interested in the code, skip until the paragraph immediately before [**Defining New Game Tables**](#defining-new-game-tables).

The documentation of classes and methods is given as inline documentation in the source code and additional information is given in [*XML* files](http://en.wikipedia.org/wiki/XML) of the app. Although the source code is open, I do not expect anyone to use it as a base for their own app therefore the documentation is not very useful to someone not having been involved in the development of this app. If, however, someone wishes to create a new app based on the source code of this one, they are encouraged to contact me and I will try to provide them the information they need.

Developers wanting to build the app should open the project in [*Android Studio*](http://developer.android.com/studio/). Not only was this project created using the mentioned software meaning the software will *know* how to build the app, but this is also official [*Android*'s](http://android.com/) development software for creating apps and I highly recommend it for its user-friendliness, even for beginners such as myself (this is my first app).

The app is written in [*Kotlin* programming language](http://kotlinlang.org/). Following [the guidelines](http://kotlinlang.org/docs/reference/kotlin-doc.html), inline documentation of classes and methods is written **immediately before** the object being documented. For instance, a method `foo` would be documented as (the documentation is in the `/** ... */`-block)

```Kotlin
/**
 * Foo a bar.
 *
 * @param bar Object to foo. The parameter may be `null`.
 *
 * @return If fooing [bar] succeds, `true`; `false` otherwise.
 *
 * @throws IllegalArgumentException If [bar] is illegal.
 *
 */
fun foo(bar: Any?): Boolean {
	// Foo [bar]...

	// In case the method reaches the end, return `false` (fooing did not succeed).
	return false
}

```

See [here](http://en.wikipedia.org/wiki/Foobar) the meaning of the terms *foo* and *bar*.

The rest of the [*README.md* file](README.md) explains how to create new game tables. No special computer knowledge is needed for this part and anyone is invited to give it a try &#128077;

##  Defining New Game Tables

New game tables can be defined in [*CSV* (*comma-separated values*) files](http://en.wikipedia.org/wiki/Comma-separated_values). If you are unfamiliar with the format, follow the provided link, although to produce valid game tables a leading contemporary [spreadsheet software](http://en.wikipedia.org/wiki/Spreadsheet) will suffice. In fact, you may even skip to [**Game Table Format**](#game-table-format)&mdash;however, it is recomended to read the first few points in [**Supported *CSV* Format**](#supported-csv-format) to see which options to select when producing a table using [spreadsheet software](http://en.wikipedia.org/wiki/Spreadsheet) (default options will probably be OK unless your cells contain line breaks and maybe if they contain quotes).

### Supported [*CSV* Format](http://en.wikipedia.org/wiki/Comma-separated_values)

The format of the [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) supported by the implemented parser is extensively described in the documentation of [`TableReader` class](app/src/main/java/com/penzart/associativity/TableReader.kt), more specifically [`TableReader.readCSV` method](app/src/main/java/com/penzart/associativity/TableReader.kt#L292), but essentially comes down to the following:

1.  The actual extension of the file does not have to be *.csv*/*.CSV*, because the actual filename extension is nothing more but a substring of the filename (see [here](http://en.wikipedia.org/wiki/Filename_extension#Usage)). All filename extensions&mdash;including no extension at all&mdash;are supported; the actual contents of the file are what matters.
2.  All [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) are considered tables of **strings**&mdash;all numbers, usual date formats and other data types and their respectful usual formats are parsed as strings. For instance, an input of *12345* will not result in an integer with the value of `12345`, i. e. *twelve thousand three hundred forty-five*, but a string `"12345"`, even if all cells in the column are valid integer expressions.
3.  [Encoding](http://en.wikipedia.org/wiki/Character_encoding) **must** be [*UTF-8*](http://en.wikipedia.org/wiki/UTF-8).
4.  The separator **must** be a comma, i. e. the character `','` ([ASCII value](http://en.wikipedia.org/wiki/ASCII) *44*, in hex. *2C*).
5.  Quotes (in the context of the [*CSV* format](http://en.wikipedia.org/wiki/Comma-separated_values)&mdash;you may use other quotes for the purposes of quoting) **must** be regular double quotes, i. e. the character `'\"'` ([ASCII value](http://en.wikipedia.org/wiki/ASCII) *34*, in hex. *22*). For instance, if the contents of a cell should be *London, England*, the cell should be expressed as `"London, England"` (the quotes are the boundaries of the cell's contents) to indicate that the character `','` is a part of the cell's contents and not a separator between cells; however, if the contents of the cell should be *un «ami»*, then the cell may be expressed both as `un «ami»` and `"un «ami»"` (in the latter case the outmost quotes are the boundaries of the cell's contents).
6.  Line breaks inside cells are **not allowed**, even if the cell is quoted.
7.  Cells containg the double quotes, i. e. the character `'\"'` ([ASCII value](http://en.wikipedia.org/wiki/ASCII) *34*, in hex. *22*), **must** be quoted, and the placement of the double quotes must be indicated by two immediately consecutive double quotes&mdash;the (sub)string *""*. For instance, if the contents of a cell should be *a "friend"*, then the cell must be expressed as `"a ""friend"""` (the outmost quotes are the quotes as boundaries enclosing the contents of the cell, while the inner *double double quotes* indicate where a quotation mark *"* is placed).
8.  The only *NA* (*not assigned*) value is an **empty cell**.  Cells containing the usual *NA* indicators, such as *NA*, *na*, *NaN*, *nan* etc., are parsed as non-empty strings (for instance, *NA* results in a cell containing the string `"NA"`).
9.  If a cell is not enclosed in quotes, its content is ***trimmed*** (leading and trailing whitespaces are ignored). Leading and trailing whitespaces before and after quotes (if a cell is enclosed in quotes) are also ignored. Some other [*CSV*](http://en.wikipedia.org/wiki/Comma-separated_values) parsers do not allow characters other than the separator or the line break before and after the enclosing quotes, but the one implemented in this project does. This allows formatting [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) to be easily readable by a human.
10. Empty lines and lines containing only whitespace characters are **ignored**. This allows extra formatting of the [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) (for instance, separating, thus emphasising, the header by an empty line).
11. Rows do not have to contain the same number of cells. If a row contains *n* cells, they are considered to be its **first** *n* cells. For instance, suppose the first row contains 5 cells, but the second one only 4 cells. Then the 4 cells in the second row are considered to be directly below the first (the leftmost&mdash;assuming the language is written/read from left to right) 4 cells of the first row, leaving the fifth cell in the first row without a pair from the second row in its own column.
12. Although this is standard [*CSV*](http://en.wikipedia.org/wiki/Comma-separated_values) practice, it is worth mentioning that adding extra separators at the beginning and/or the end of a row will result in empty cells at the beginning and/or the end (not saying that there are no cases where this is the desired result&mdash;such cases are even mentioned in [**Game Table Format**](#game-table-format)). The first cell of a row starts **immediately at the beginning of the row's line**, and the last one ends **at the very end of the row's line** (leading and trailing whitespaces are ignored in cases explained above).

### Game Table Format

The actual format of defining game tables is the following:

1.  The table must contain **at least 6 rows**. Keep reading to see why.
2.  In general, the table should contain 5 columns (some rows may contain only 4 cells, but that will be explained later). The first 4 columns represent columns *A*, *B*, *C* and *D* of the game table, while the last column represents the final solution.
3.  The first row must contain **exactly 5 cells**. Each of the 5 cells may be **either `"0"` or `"1"`** (empty cells are not allowed)&mdash;of course, in the [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) these values may be expressed as *0* and *1* without the quotes. A value of `"0"` means that cells in the column must not be shuffled, and a value of `"1"` means that shuffling is allowed. The fifth column refers to the order of the columns, not cells inside the columns. For instance, if a `"0"` appears in one of the first 4 columns, but `"1"` is in the fifth column, cells inside the column with a `"0"` will not be shuffled, but the column may appear as a column *A*, *B*, *C* or *D* according to the [pseudorandomly](http://en.wikipedia.org/wiki/Pseudorandom_number_generator) chosen [permutation](http://en.wikipedia.org/wiki/Permutation) at the game's initialisation. The same, [*mutatis mutandis*](http://en.wikipedia.org/wiki/Mutatis_mutandis), applies if a `"1"` appears in one of the first 4 columns, but a `"0"` appears in the last column. See the example below to understand why this feature was included.
4.  Rows 2 &ndash; 5 (inclusive) may contain **exactly 4 or 5 cells**, but all of them must be of **the same length (number of cells)** and, if they contain 5 cells, the fifth cell must be **empty**. These rows define the values of cells inside columns *A*, *B*, *C* and *D*. If a column does not allow shuffling (see the point above), the top-to-bootom order of cells is preserved. Similarly, the left-to-right, i. e. *A*-to-*D* order of columns is preserved if the final solution does not allow shuffling of the columns.
5.  Rows 6 until the end (inclusive) must contain **exactly** 5 cells. These rows define the solutions of columns and the final solution, along with alternative acceptable answers (such as synonyms, abbreviations, alternative spellings etc.). The sixth row defines the exact solutions of columns and the final solution (the ones that appear when the user types one of the acceptable answers), while the following rows define alternative acceptable answers. For instance, if *Times New Roman* appears in a column in the sixth row, but *Times* appears in the seventh row of the same column, and the user types in *Times* while playing the game, the answer will be accepted and the column will be opened, but *Times New Roman* will be displayed as the solution of the column.
6.  All acceptable answers must be **agregated at the top**. This means that if one of the columns has only 3 acceptable answers (including the exact solution), these answers must appear in rows 6, 7 and 8. For instance, answers appearing in rows 6, 7 and 9 but leaving an empty cell in row 8 is not accepted. Furthermore, if another column has 5 acceptable answers, the column with only 3 acceptable answers should contain empty cells after the three acceptable answers were listed.
7.  Acceptable answers are **case-insensitive** (adding *florence nightingale* as an acceptable answer for *Florence Nightingale* has no effect&mdash;the answer will be automatically accepted even without explicitly mentioning it amongst acceptable answers). However, as it is explained in the documentation of [`MainActivity.isAcceptable` method](app/src/main/java/com/penzart/associativity/MainActivity.kt#L345), usual formal and informal transcriptions of *special characters* are not equated with their respective original characters by default (follow the link to see why). This means that in order to accept *Strasse* as an answer for *Straße*, the answer *Strasse* must be explicitly added. The same applies to letters such as *Č*, *č* in Serbo-Croatian and their respective usual informal alternatives such as *C*, *c*.

If any of the conditions mentioned above is not satisfied, an uncaught exception will be thrown causing the app to crash. Of course, if the [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) does not satisfy the format stated in [**Supported *CSV* Format**](#supported-csv-format), an unexpected result may arise or an uncaught exception will be thrown as well.

An example of a column (or the final solution for that matter&mdash;suppose cells in the column are solutions of some other columns) where the order of associations matters is the column *C* in the following associations game table (try to solve the table &#128521;).

|       | **A**     | **B**        | **C**  | **D**        |
|-----: | :-------: | :----------: | :----: | :----------: |
| **1** | sheet     | ice          | Mary   | Google Drive |
| **2** | cellulose | Christmas    | had    | storm        |
| **3** | academia  | flake        | a      | sky          |
| **4** | printer   | mountain top | little | water        |

A valid [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) defining the table above would contain (solutions are masked so you would not be deprived of the fun solving the table)

```
1         , 1            , 0      , 1            , 1

sheet     , ice          , Mary   , Google Drive ,
cellulose , Christmas    , had    , storm        ,
academia  , flake        , a      , sky          ,
printer   , mountain top , little , water        ,

XXXXX     , XXXX         , XXXX   , XXXXX        , XXXXX
XXXXXX    , XXXXX        , XXXXX  , XXXXXX       , XXXXX XXXXXX
          , XXXXXXX      , XXXXX  , XXXXXX       , XXXXX XXXXX
          ,              ,        ,              , XXXXXXXXX

```

The format above is human-friendly, while a [spreadsheet software](http://en.wikipedia.org/wiki/Spreadsheet) would probably produce a [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) containing (again, the solutions are masked)

```
"1","1","0","1","1"
"sheet","ice","Mary","Google Drive",""
"cellulose","Christmas","had","storm",""
"academia","flake","a","sky",""
"printer","mountain top","little","water",""
"XXXXX","XXXX","XXXX","XXXXX","XXXXX"
"XXXXXX","XXXXX","XXXXX","XXXXXX","XXXXX XXXXXX"
"","XXXXXXX","XXXXX","XXXXXX","XXXXX XXXXX"
"","","","","XXXXXXXXX"

```

In both formats shown commas (and empty cells in the second format) at the end of rows 2 &ndash; 5 may be omitted, but, if a final comma is omitted in at least one of the rows, final commas must be omitted in all of them. Quotes in the second format may be omitted as well, even the [spreadsheet software](http://en.wikipedia.org/wiki/Spreadsheet) that is used will probably omit them while producing the [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) since no cell contains a comma or quotes. All formats and other mentioned variants are supported and will result in the same game table (remember, leading and trailing whitespaces are **ignored**).

**Nota bene. Although solutions in the example above are masked, if you mask the solutions in your [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values), the actual solutions will be *XXXXX* and so on. The app cannot *magically* read your mind, nor is any [machine learning algorithm](http://en.wikipedia.org/wiki/Machine_learning) for solving implemented in the app, to fill in the solutions automatically. The latter option, the realistic one, seems like an interesting project, but requires expertise in [NLP](http://en.wikipedia.org/wiki/Natural_language_processing), general culture, culture/language/location-specific knowledge and constant updates to keep up with trending knowledge.**

### Producing a Game Table Using [Spreadsheet Software](http://en.wikipedia.org/wiki/Spreadsheet)

The easiest way to create a [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values) of a game table is using a [spreadsheet software](http://en.wikipedia.org/wiki/Spreadsheet) such as [*Sheets*](http://google.com/sheets/about/), [*Numbers*](http://apple.com/numbers/), [*Excel*](http://products.office.com/excel), [*OpenOffice Calc*](http://openoffice.org/product/calc.html) or [*LibreOffice Calc*](http://libreoffice.org/discover/calc/). In the software of your choice enter the game table as

|              | **A**     | **B**        | **C**   | **D**        | **E**          | **F** | **&hellip;** |
| :----------: | :---------| :------------| :-------| :------------| :--------------| :-----| :------------|
| **1**        | 1         | 1            | 0       | 1            | 1              |       |              |
| **2**        | sheet     | ice          | Mary    | Google Drive |                |       |              |
| **3**        | cellulose | Christmas    | had     | storm        |                |       |              |
| **4**        | academia  | flake        | a       | sky          |                |       |              |
| **5**        | printer   | mountain top | little  | water        |                |       |              |
| **6**        | *XXXXX*   | *XXXX*       | *XXXX*  | *XXXXX*      | *XXXXX*        |       |              |
| **7**        | *XXXXXX*  | *XXXXX*      | *XXXXX* | *XXXXXX*     | *XXXXX XXXXXX* |       |              |
| **8**        |           | *XXXXXXX*    | *XXXXX* | *XXXXXX*     | *XXXXX XXXXX*  |       |              |
| **9**        |           |              |         |              | *XXXXXXXXX*    |       |              |
| **10**       |           |              |         |              |                |       |              |
| **&vellip;** |           |              |         |              |                |       |              |

Again, be sure to add actual solutions, not strings such as *XXXXX*, in rows 6 until the end. Of course, if your game table is a *kinky* one and one of the solutions or an alternative acceptable answer is in fact *XXX*, then you *should* put *XXX* as the answer in the corresponding column. Keep in mind that the final solution and its alternative acceptable answers are placed in the column *E* from the sixth row downward.

Examples of valid game tables are provided in [*~/examples/game_tables/* subdirectory](examples/game_tables). These game tables are the same as the above, but the solutions are unmasked. Note that only the [*CSV* table](http://en.wikipedia.org/wiki/Comma-separated_values) is readable by the app, the other formats are provided merely to show how to define such tables in various [spreadsheet software](http://en.wikipedia.org/wiki/Spreadsheet).

The next step is to export the sheet to a [*CSV* file](http://en.wikipedia.org/wiki/Comma-separated_values). This is usually done by *File* > *Export as&hellip;*/*Save as&hellip;*/*Download as&hellip;* and choosing [*CSV*](http://en.wikipedia.org/wiki/Comma-separated_values) as the format. As mentioned above, default options will probably be OK, but check [**Supported *CSV* Format**](#supported-csv-format) to see which options to actually select.

### Adding Game Tables to the App

Ideally there would be an online server to which one could upload their own new game table and from which the app would download the game table and run it. The problem is that servers are not free and this is currently a non-profit one man's project essentially meaning no such server is implemented. If an app's server for distributing game tables existed, a [DBMS](http://en.wikipedia.org/wiki/Database#Database_management_system) would probably be installed/connected and the need for an in-app [*CSV*](http://en.wikipedia.org/wiki/Comma-separated_values) parser might be avoided.

**If you only want to mount your game tables into the app, but do not have the will or knowledge to build the app yourself**, put your game tables' [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) into the *game_tables/* subdirectory of the app's directory on your device. The app's directory will probably be */Android/data/com.penzart.associativity/files/* in internal or external (such as an [*SD* card](http://en.wikipedia.org/wiki/SD_card)) memory. After the app was run for the first time, a *README.txt* file should appear in the app's directory, but the README file will essentially direct you to [this *README.md* file](README.md) and will not reveal any useful information. Game tables put in the specified *game_tables/* directory should be playable by choosing *Custom* difficulty level, but this feature was not fully tested yet.

**If you want to build the app with your game tables yourself**, [clone or download the repository](http://help.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) and put your game table(s) in one of the [*~/app/src/main/assets/game_tables/* assets subdirectory's](app/src/main/assets/game_tables) subdirectory. Read the [*~/app/src/main/assets/game_tables/* assets subdirectory's README.md file](app/src/main/assets/game_tables/README.md) to find out more.

Finally, **if you want to contribute to the development of the app, but do not know how to program or how to use [Git](http://git-scm.com/)**, you can contact me and send me the game tables you have created so I can test them and incorporate them into the app. I will try to test them without solving them so I can enjoy playing them, too &#128522;

**Nota bene 1 (actually a warning). The validity of game tables' [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) is not checked. Do not put anything but valid game tables' [*CSV* files](http://en.wikipedia.org/wiki/Comma-separated_values) in the game tables' directories. I cannot stress this enough: game tables not following [*Supported CSV Format*](#supported-csv-format) and [*Game Table Format*](#game-table-format), including completely unrelated files (such as an [*MP3* file](http://en.wikipedia.org/wiki/MP3)), may cause the app to crash. The only thing the original game developer may be accused of here is that no exception handling was implemented for such cases. Keep calm though, this is not a fatal error: the app may easily be restarted and no damage will be done to the device's memory or system.**

**Nota bene 2. Do not change the contents of the custom game tables directory while the app is running, even if it is running in background. Be sure to close the app before changing anything and restart the app after all changes are done. Changing the memory while the app is running may cause unexpected results (such that the app could crash) when starting a new game.**

**Nota bene 3. It is advisable that the game tables' [*CSV* files'](http://en.wikipedia.org/wiki/Comma-separated_values) names do not reveal any of the solutions. You can name the files by time and date when they were created, by the value/contents of one of their inner cells, such as *A1* (personally, I would avoid this option although this naming system has the highest probability of you being able to locate a specific game table after a while), or by giving them random file names, such as *LI8no5.csv*, *yI7ikF.csv*, *fnFXQl.csv*, *OffM0k.csv*&hellip;. However, the name of the file from which the game table was read is not shown to the user, therefore you can name the files any way you want. If a user wants to *hack* the solution, they could scope around the memory of their device, but giving unrevealing names to files will make them have no other option but to open all files they can find.**
