package com.penzart.associativity

import android.os.Bundle
import java.io.*

/**
 * Read a table of information for an associations game.
 *
 * The format of the table is defined in [readAssociationsTable].  This class also provides an
 * interface to read information from a *CSV* file, and the format of supported *CSV* files is
 * defined in [readCSV] method.
 *
 * **Note: This class cannot be instantiated.  All methods of the class are defined in its companion
 * object.**
 *
 */
class TableReader : Any {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  COMPANION ELEMENTS                                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The companion object of the class [TableReader].
     *
     * @property ILLEGAL_ESCAPE_EXPR_LENGTH_ERROR_MESSAGE Error message for an exception when the argument `sequence` in [escapeSequence] method is not a single character string.
     * @property ILLEGAL_ESCAPE_CHAR_ERROR_MESSAGE Format error message for an exception when the argument `sequence` in [escapeSequence] method is not a valid escaped sequence.
     *
     * @property CSV_ERROR_MESSAGE_POSITION_INDICATOR Format error message for expressing the position of an error for error messages when parsing *CSV* inputs in [readCSV] method.
     *
     * @property CSV_NEGATIVE_PRECEDING_LINE_INDEX_ERROR_MESSAGE Format error message for an exception when a negative `precedingLine` value is passed in [readCSV] method.
     * @property CSV_UNEXPECTED_LINE_END_ERROR_MESSAGE Format error message for an exception when a line unexpectedly ends when reading a *CSV* file in [readCSV] method.
     * @property CSV_EXPECTED_CELL_END_ERROR_MESSAGE Format error message when a non-whitespace character that is not a cell separator is found after closing a cell when reading a *CSV* file in [readCSV] method.
     * @property CSV_ILLEGAL_QUOTES_ERROR_MESSAGE Format error message for an exception when illegal unescaped quotes appear in [readCSV] method.
     * @property CSV_ILLEGAL_ESCAPE_SEQUENCE_ERROR_MESSAGE Format error message for an exception when an illegal escape sequence appears in [readCSV] method with `parseEscapeSequences` argument set to `true`.
     *
     * @property ASSOCIATIONS_TABLE_TOO_SHORT_ERROR_MESSAGE Format error message for an exception when the table in [readAssociationsTable] method does not contain at least 6 rows.
     * @property ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_LENGTH_ERROR_MESSAGE Format error message for an exception when the first line of the table in [readAssociationsTable] method does not contain exactly 5 cells.
     * @property ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_VALUES_ERROR_MESSAGE Format error message for an exception when the first line of the table in [readAssociationsTable] method contains something other than [ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING] and [ASSOCIATIONS_TABLE_ALLOW_SHUFFLING].
     * @property ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_INCONSISTENT_LENGTH_ERROR_MESSAGE Format error message for an exception when lines 2 to 5 (inclusive) of the table in [readAssociationsTable] method are not of the same length.
     * @property ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_ILLEGAL_LENGTH_ERROR_MESSAGE Format error message for an exception when lines 2 to 5 (inclusive) of the table in [readAssociationsTable] method do not contain exactly 4 or 5 cells.
     * @property ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_ILLEGAL_NON_EMPTY_LAST_CELL_ERROR_MESSAGE Format error message for an exception when lines 2 to 5 (inclusive) of the table in [readAssociationsTable] method contain 5 cells, but the 5th cell is not empty.
     * @property ASSOCIATIONS_TABLE_SOLUTIONS_ILLEGAL_LENGTH_ERROR_MESSAGE Format error message for an exception when rows 6 until the end (inclusive) of the table in [readAssociationsTable] method do not contain exactly 5 cells.
     * @property ASSOCIATIONS_TABLE_SOLUTIONS_LATE_ENTRY_ERROR_MESSAGE Format error message for an exception if a non-empty cells appears in a column in a row 6 until the end (inclusive) of the table in [readAssociationsTable] method but an empty cell has previously appeared in the same column and a higher row (not higher than row 6).
     *
     * @property CSV_DEFAULT_ORIGIN_NAME Default value for `originName` in [readCSV] method.
     *
     * @property ASSOCIATIONS_TABLE_ROWS_LABELS Array of rows' labels in the table of the associations game.
     * @property ASSOCIATIONS_TABLE_COLUMNS_LABELS Array of columns' labels in the table of the associations game.
     * @property ASSOCIATIONS_TABLE_SOLUTION_LABEL Label of the final solution in the associations game.
     *
     * @property SUFFIX_ASSOCIATIONS_TABLE_SHUFFLE Suffix for saving permutation allowing in [readAssociationsTable].
     *
     * @property ESCAPE_CHAR A single-character string representing the escape character.  Actually a string of a single backslash character, i. e. the string `"\\"`.
     *
     * @property CSV_QUOTE_CHAR A single-character string representing double quotes in *CSV* files.  Actually a string of a single double quotes character, i. e. the string `"\""`.
     * @property CSV_SEPARATOR_CHAR A single-character string representing a separator in *CSV* files.  Actually a string of a single comma character, i. e. the string `","`.
     *
     * @property ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING The proper content of a cell in the first row of the table in [readAssociationsTable] method if the corresponding column or the final solution must not be shuffled.  Actually a string of a single `'0'` character, i. e. the string `"0"`.
     * @property ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING The proper content of a cell in the first row of the table in [readAssociationsTable] method if the corresponding column or the final solution may be shuffled.  Actually a string of a single `'1'` character, i. e. the string `"1"`.
     *
     */
    public companion object {

        ////////////////////////////////////////////////////////////////////////////////////////////
        //  ERROR MESSAGES                                                                        //
        ////////////////////////////////////////////////////////////////////////////////////////////

        private const val ILLEGAL_ESCAPE_EXPR_LENGTH_ERROR_MESSAGE: String =
            "Escaped sequence must be a single character string."
        private const val ILLEGAL_ESCAPE_CHAR_ERROR_MESSAGE: String =
            "Illegal escape sequence \"\\%s\"."

        private const val CSV_ERROR_MESSAGE_POSITION_INDICATOR: String = "%s: line %d, row %d"

        private const val CSV_NEGATIVE_PRECEDING_LINE_INDEX_ERROR_MESSAGE: String =
            "Preceding line index must be non-negative; %d is given instead."
        private const val CSV_UNEXPECTED_LINE_END_ERROR_MESSAGE: String =
            "Unexpected line break in %s."
        private const val CSV_EXPECTED_CELL_END_ERROR_MESSAGE: String =
            "Expected a separator or a line end in %s."
        private const val CSV_ILLEGAL_QUOTES_ERROR_MESSAGE: String =
            "Unescaped quotes not allowed in %s."
        private const val CSV_ILLEGAL_ESCAPE_SEQUENCE_ERROR_MESSAGE: String =
            "Unrecognised escape sequence \"%s\" in %s."

        private const val ASSOCIATIONS_TABLE_TOO_SHORT_ERROR_MESSAGE: String =
            "Association table must have at least 6 rows, but a table of %d rows was given."
        private const val ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_LENGTH_ERROR_MESSAGE: String =
            "The first row of the association table must contain exactly 5 cells, but a row of %d cells was given."
        private const val ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_VALUES_ERROR_MESSAGE: String =
            "The first row of the association table must contain only \"%s\" (to disallow shuffling) and \"%s\" (to allow shuffling), but a cell \"%s\" was encountered."
        private const val ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_INCONSISTENT_LENGTH_ERROR_MESSAGE: String =
            "Rows 2 – 5 (inclusive) of the association table must be of the same length, but row %d contains %d cells and row %d contains %d cells."
        private const val ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_ILLEGAL_LENGTH_ERROR_MESSAGE: String =
            "Rows 2 – 5 (inclusive) of the association table must contain either 4 or 5 cells, but row %d contains %d cells."
        private const val ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_ILLEGAL_NON_EMPTY_LAST_CELL_ERROR_MESSAGE: String =
            "If rows 2 – 5 (inclusive) of the association table contain 5 cells, the 5th cell must be empty, but row %d contains \"%s\" as the last cell."
        private const val ASSOCIATIONS_TABLE_SOLUTIONS_ILLEGAL_LENGTH_ERROR_MESSAGE: String =
            "Rows 6 until the end (inclusive) of the association table must contain exactly 5 cells, but row %d contains %d cells."
        private const val ASSOCIATIONS_TABLE_SOLUTIONS_LATE_ENTRY_ERROR_MESSAGE: String =
            "Column %d contains a non-empty cell in row %d after already having an empty cell in row %d (after reaching an empty cell in column 6 until the end (inclusive), all consecutive rows must have empty cells in the column)."


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  PRIVATE CONSTANTS                                                                     //
        ////////////////////////////////////////////////////////////////////////////////////////////

        private const val CSV_DEFAULT_ORIGIN_NAME: String = "input"

        private val ASSOCIATIONS_TABLE_ROWS_LABELS: Array<String> = arrayOf("1", "2", "3", "4")
        private val ASSOCIATIONS_TABLE_COLUMNS_LABELS: Array<String> = arrayOf("A", "B", "C", "D")
        private const val ASSOCIATIONS_TABLE_SOLUTION_LABEL: String = "Sol"

        private const val SUFFIX_ASSOCIATIONS_TABLE_SHUFFLE: String = "Shuffle"


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  PUBLIC CONSTANTS                                                                      //
        ////////////////////////////////////////////////////////////////////////////////////////////

        public const val ESCAPE_CHAR: String = "\\"

        public const val CSV_QUOTE_CHAR: String = "\""
        public const val CSV_SEPARATOR_CHAR: String = ","

        public const val ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING: String = "0"
        public const val ASSOCIATIONS_TABLE_ALLOW_SHUFFLING: String = "1"


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  AUXILIARY METHODS                                                                     //
        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Construct a label for saving permutation allowing in [readAssociationsTable] from a column's or the final solution's label and a suffix.
         *
         * @param label An element's label.
         * @param suffix A suffix for construction.
         *
         * @return The constructed mixed label.
         *
         */
        private fun appendSuffix(label: String, suffix: String): String = label + suffix

        /**
         * Construct a label for saving permutation allowing in [readAssociationsTable] from a column's or the final solution's label.
         *
         * The method returns `appendSuffix(label, SUFFIX_ASSOCIATIONS_TABLE_SHUFFLE)`.
         *
         * @param label An element's label.
         *
         * @return The constructed mixed label.
         *
         * @see appendSuffix
         *
         */
        public fun shuffleAllowingLabel(label: String): String =
            appendSuffix(label, SUFFIX_ASSOCIATIONS_TABLE_SHUFFLE)

        /**
         * Construct a cell's label for saving values in [readAssociationsTable] from the row's and the column's labels.
         *
         * @param row Row's label.
         * @param column Column's label.
         *
         * @return Cell's label.
         *
         */
        public fun joinRowAndColumnLabels(row: String, column: String): String = column + row


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  READING METHODS                                                                       //
        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Parse an escape sequence.
         *
         * Valid escaped sequences and their parsed results are:
         * * `"0"` to a null-character, i. e. `"\0"`,
         * * `"b"` to a backspace, i. e. `"\b"`,
         * * `"t"` to a horizontal tab, i. e. `"\t"`,
         * * `"v"` to a vertical tab, i. e. `"\v"`,
         * * `"n"` to a line break, i. e. `"\n"`,
         * * `"r"` to a carriage return, i. e. `"\r"`,
         * * `"f"` to a page break, i. e. `"\f"`,
         * * `"a"` to an alert (a bell), i. e. `"\a"`,
         * * `"\'"` to a single quote, i. e. `"\'"`,
         * * `"\""` to double quotes, i. e. `"\""`,
         * * `"\\"` to a backslash, i. e. `"\\"`,
         * * `","` to a comma, i. e. `","`,
         * * `"e"` to the escaping character which is the backslash, i. e. `"\\"`.
         *
         * **Note: The initial escaping character [ESCAPE_CHAR] *must* not be passed in
         * [sequence], only the actual escaped sequence should be passed.  For instance, to
         * parse `"\\n"`, pass only `"n"` as the argument [sequence], not the complete string
         * `"\\n"`.**
         *
         * @param sequence Escaped sequence to parse.
         *
         * @return The parsed result of the escaping sequence.
         *
         * @throws IllegalArgumentException If [sequence] is not a single-character string or if the escaped sequence is not valid.
         *
         */
        public fun escapeSequence(sequence: String): String {
            // Check if [expression] is not a single-character string.
            if (sequence.length != 1)
                throw IllegalArgumentException(ILLEGAL_ESCAPE_EXPR_LENGTH_ERROR_MESSAGE)

            // Parse [expression] and return the result.
            return when (sequence) {
                "0" -> (0).toChar().toString()
                "b" -> "\b"
                "t" -> "\t"
                "v" -> (11).toChar().toString()
                "n" -> "\n"
                "r" -> "\r"
                "f" -> (12).toChar().toString()
                "a" -> (7).toChar().toString()
                "\'" -> "\'"
                "\"" -> "\""
                "\\" -> "\\"
                "," -> ","
                "e" -> "\\"
                else -> throw IllegalArgumentException(
                    ILLEGAL_ESCAPE_CHAR_ERROR_MESSAGE.format(sequence)
                )
            }
        }

        /**
         * Parse an escaped character.
         *
         * The method returns `escapeExpression(c.toString())` without catching any exceptions.
         *
         * @param sequence Escaped character to parse.
         *
         * @return The parsed result of the escaped character.
         *
         */
        public fun escapeSequence(sequence: Char): String = escapeSequence(sequence.toString())

        /**
         * Read a *CSV* input.
         *
         * The quotes and separator of *CSV* files are explained by [CSV_QUOTE_CHAR] and
         * [CSV_SEPARATOR_CHAR].  Lines containing only whitespaces—checked by calling
         * [Char.isWhitespace] method—are ignored (this includes empty lines).  The value of each
         * cell in the table is trimmed by calling [String.trim] method, unless the cell was
         * enclosed in quotes and the whitespaces were inside the quotes (whitespaces outside the
         * quotes are ignored).
         *
         * **Note: Cells containing quotes ([CSV_QUOTE_CHAR]) *must be quoted*.  Cells containing
         * line breaks *are not allowed*.  Alternatively, quotes may be escaped using a valid escape
         * sequence for [escapeSequence] method, which also allows parsing escape sequences for
         * line breaks, by setting [parseEscapeSequences] to `true`.**
         *
         * Each cell of the table is considered a [String] value.  To convert data, use another
         * *CSV* reader or manually parse the result of this method.
         *
         * The only *NA* value is an empty cell.  Cells containing the usual *NA* indicators, such
         * as `"NA"`, `"na"`, `"NaN"`, `"nan"` etc., are parsed as non-empty strings (for instance,
         * `"NA"` results in a cell containing the string `"NA"`).  *NA* cells will be represented
         * by empty strings, not by `null` values.
         *
         * The resulting two-dimensional array is the matrix of cells in the table represented by
         * the *CSV* read from [reader].  The first dimension represents the rows of the table,
         * while the second one represents its columns.  Therefore to get the cell in the `j`-th
         * column of the `i`-th row use `table[i][j]`, where `table` is the result of this method.
         *
         * **Note: the method does not check if all rows have the same number of columns.  The
         * number of columns in each row is *n* + 1, where *n* >= 0 is the number of actual
         * separators appearing in the line representing the row (this does not separators inside
         * quotations).  Putting a [CSV_SEPARATOR_CHAR] at the beginning of a line will result in an
         * empty cell at the row's beginning, while putting it at the end will result in an empty
         * cell at the row's end.**
         *
         * @param reader [BufferedReader] from which to read the *CSV* table.
         * @param originName The name or the path of the origin of [reader].  If the table is read from a file, this parameter should be set to the file's name or its path.  This argument is used to explain errors when throwing exceptions.
         * @param precedingLine Index of the line directly before the first line read from [reader].  If the first line that is read should have index `1`, set this argument to `0`.  This argument is used to explain errors when throwing exceptions.  **Note: The first column that is read is assumed to have index `1`.**
         * @param ends Characters indicating the end of reading.  A character that is read is first checked if it is in [ends], before any other *CSV* parsing or parsing of escape sequences; however, input is read from [reader] using [BufferedReader.readLine] method therefore text after the ending character but before the end of the line or stream is ignored and cannot be fetched again.  **Note: Some characters, such as the line break (`'\n'`) will not work properly.  It is best practice to use printable non-whitespace characters for [ends] that are guaranteed not to appear in a *CSV* input by the protocol in use.**
         * @param parseEscapeSequences If `true`, any escape sequence in the text read by [reader] is parsed using [escapeSequence] method.  **Note: Since [CSV_SEPARATOR_CHAR] and [CSV_QUOTE_CHAR] can be escaped using [escapeSequence] method, subsequent parsing of these escape expressions is not possible.**
         *
         * @return The table written in the *CSV* input.
         *
         * @throws IllegalArgumentException If [precedingLine] is negative or if the rules of the *CSV* format are not followed.
         * @throws IOException [IOException]s thrown by the [BufferedReader] used in the method are not caugh, and additional [IOException]s may be thrown by the method itself if the *CSV* input is not formatted properly.
         *
         */
        public fun readCSV(
            reader: BufferedReader,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Array<Array<String>> {
            // Lambda function for inserting rows in tables.  Resulting [table] is returned.
            val insertRowInTable: (
                table: ArrayList<Array<String>>, row: ArrayList<String>
            ) -> ArrayList<Array<String>> =
                { table: ArrayList<Array<String>>, row: ArrayList<String> ->
                    table.add(row.toArray(arrayOf()))

                    table
                }

            // Lambda function for inserting cells in rows.  If [wereQuotes], the cell is built and
            // added to the end; otherwise it is built, trimmed and then added.  In the end [cell]
            // is cleared and resulting [row] is returned.
            val insertCellInRow: (
                row: ArrayList<String>, cell: StringBuilder, wereQuotes: Boolean
            ) -> ArrayList<String> =
                { row: ArrayList<String>, cell: StringBuilder, wereQuotes: Boolean ->
                    row.add(
                        if (wereQuotes)
                            cell.toString()
                        else
                            cell.toString().trim()
                    )

                    cell.clear()

                    row
                }

            // Check if [precedingRow] is negative.
            if (precedingLine < 0)
                throw IllegalArgumentException(
                    CSV_NEGATIVE_PRECEDING_LINE_INDEX_ERROR_MESSAGE.format(precedingLine)
                )

            // Initialise the resulting [table].
            val table: ArrayList<Array<String>> = ArrayList()

            // Initialise indicator of reading to `true`.  When an ending character is read,
            // [reading] is set to `false` and input is no longer read.
            var reading: Boolean = true

            // Initialise indices of rows.
            var i: Int = precedingLine

            // Read the *CSV* input.
            inputLoop@ while (reading) {
                // Read the next [line] from [bufferedReader].  If no [line] is read, break the
                // `while`-loop.
                val line: String = reader.readLine() ?: break@inputLoop

                // Initialise [row].
                val row: ArrayList<String> = ArrayList()

                // Initialise [cell] builders.
                val cell: StringBuilder = StringBuilder()

                // Assume [line] is empty.
                var emptyLine: Boolean = true

                // Initialise indicators of quotes and expectation of the end of [cell].  If
                // [insideQuotes] is `0`, the current character is not inside quotes;
                // if it is `1`, the current character is inside quotes; if it is `-1`, the current
                // character is quotes inside open quotes (or the terminating quotes).  After
                // closing quotes, the end of [cell] is expected.
                var insideQuotes: Int = 0
                var expectCellEnd: Boolean = false

                // Initialise indicator of expectation of an escape sequence to `false`.
                var escaping: Boolean = false

                // Read [line].
                lineLoop@ for (j in line.indices) {
                    // Get the current character as a [Char] and a [String].
                    val c: Char = line[j]
                    val s = c.toString()

                    // If an ending character is read, set [reading] to `false` and break the
                    // `while`-loop.
                    if (c in ends) {
                        reading = false

                        break@lineLoop
                    }

                    // If quotes were previously read after quotes had already been open, check if
                    // double quotes have appeared.
                    if (insideQuotes == -1)
                        when (s) {
                            // If double quotes have appeared, add quotes to [cell].
                            CSV_QUOTE_CHAR -> {
                                // Reset [insideQuotes] to `1`.
                                insideQuotes = 1

                                // Add double quotes to [cell].
                                cell.append(CSV_QUOTE_CHAR)

                                // Continue to the next character.
                                continue@lineLoop
                            }

                            // Otherwise end [cell].
                            else -> {
                                // Close double quotes.
                                insideQuotes = 0

                                // Expect the end of [cell].
                                expectCellEnd = true
                            }
                        }

                    // If an escape sequence is expected, try to parse the current character.
                    if (escaping) {
                        // Try to parse the escape sequence.
                        try {
                            cell.append(escapeSequence(s))
                        } catch (exception: IllegalArgumentException) {
                            throw IllegalArgumentException(
                                CSV_ILLEGAL_ESCAPE_SEQUENCE_ERROR_MESSAGE.format(
                                    s,
                                    CSV_ERROR_MESSAGE_POSITION_INDICATOR.format(
                                        originName,
                                        i + 1,
                                        j + 1
                                    )
                                )
                            )
                        }

                        // Indicate that an escape sequence is no longer expected.
                        escaping = false

                        // Continue to the next character.
                        continue@lineLoop
                    }

                    // Ignore leading or trailing whitespaces.  If a non-whitespace is read,
                    // indicate that [line] is not empty.
                    if (c.isWhitespace()) {
                        if ((cell.isEmpty() && insideQuotes == 0) || expectCellEnd)
                            continue@lineLoop
                    }
                    else
                        emptyLine = false

                    // Check if a separator or the end of [line] were expected but were not found.
                    if (expectCellEnd && s != CSV_SEPARATOR_CHAR)
                        throw IOException(
                            CSV_EXPECTED_CELL_END_ERROR_MESSAGE.format(
                                CSV_ERROR_MESSAGE_POSITION_INDICATOR.format(
                                    originName,
                                    i + 1,
                                    j + 1
                                )
                            )
                        )

                    // If escape sequences should be parsed and the current character is
                    // [ESCAPE_CHAR], indicate that an escape sequence is expected and continue to
                    // the next character.
                    if (parseEscapeSequences && s == ESCAPE_CHAR) {
                        escaping = true

                        continue@lineLoop
                    }

                    // Act accordingly to quotes environment.
                    when (insideQuotes) {
                        // In case of no quotes environment:
                        0 -> when (s) {
                            CSV_QUOTE_CHAR -> {
                                // Opening quotes are allowed only at the beginning of [cell].
                                // Throw an exception if [cell] is not empty.
                                if (cell.isNotEmpty())
                                    throw IOException(
                                        CSV_ILLEGAL_QUOTES_ERROR_MESSAGE.format(
                                            CSV_ERROR_MESSAGE_POSITION_INDICATOR.format(
                                                originName,
                                                i + 1,
                                                j + 1
                                            )
                                        )
                                    )

                                // Open quotes.
                                insideQuotes = 1
                            }

                            CSV_SEPARATOR_CHAR -> {
                                // Insert [cell] in the [row] list of cells.
                                insertCellInRow(row, cell, expectCellEnd)

                                // Do not necessarily expect the end of [cell] any more.
                                expectCellEnd = false
                            }

                            // Otherwise just add the character to [cell].
                            else -> cell.append(s)
                        }

                        // In case of quotes environment:
                        1 -> when (s) {
                            // If double quotes have appeared, indicate the second quotes by setting
                            // [insideQuotes] to `-1`.
                            CSV_QUOTE_CHAR -> insideQuotes = -1

                            // Otherwise just add the character to [cell].
                            else -> cell.append(s)
                        }

                        // Default branch is empty.
                        else -> Unit
                    }
                }

                // If [cell] was enclosed in quotes and [insideQuotes] was left at `-1`, end [cell].
                if (insideQuotes == -1) {
                    // Close quotes.
                    insideQuotes = 0

                    // Expect the end of [cell].
                    expectCellEnd = true
                }

                // Check if [line] ended while inside quotes or while expecting an escape sequence.
                if (insideQuotes != 0 || escaping)
                    throw IllegalArgumentException(
                        CSV_UNEXPECTED_LINE_END_ERROR_MESSAGE.format(
                            CSV_ERROR_MESSAGE_POSITION_INDICATOR.format(
                                originName,
                                i + 1,
                                line.length + 1
                            )
                        )
                    )

                // If [line] is not empty, add [row] to [table].
                if (!emptyLine)
                    insertRowInTable(table, insertCellInRow(row, cell, expectCellEnd))

                // Increment the row index.
                ++i
            }

            // Return [table] as a two-dimensional [Array].
            return table.toArray(arrayOf())
        }

        /**
         * Read a *CSV* input.
         *
         * The method returns
         * `readCSV(BufferedReader(reader), originName, precedingLine, ends, parseEscapeSequence)`
         * without catching any exceptions.
         *
         * @param reader [Reader] from which to read the *CSV* table.
         * @param originName The name or the path of the origin of [reader].
         * @param precedingLine Index of the line directly before the first line read from [reader].
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         *
         * @return The table written in [reader].
         *
         */
        public fun readCSV(
            reader: Reader,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Array<Array<String>> = readCSV(
            BufferedReader(reader),
            originName,
            precedingLine,
            ends,
            parseEscapeSequences
        )

        /**
         * Read a *CSV* input.
         *
         * The method returns
         * `readCSV(input.reader(), originName, precedingLine, ends, parseEscapeSequences)` without
         * catching any exceptions.
         *
         * @param input [InputStream] from which to read the *CSV* table.
         * @param originName The name or the path of the origin of [input].
         * @param precedingLine Index of the line directly before the first line read from [input].
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         *
         * @return The table written in [input].
         *
         */
        public fun readCSV(
            input: InputStream,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Array<Array<String>> = readCSV(
            input.reader(),
            originName,
            precedingLine,
            ends,
            parseEscapeSequences
        )

        /**
         * Read a *CSV* file.
         *
         * The method is equivalent to calling
         * `readCSV(file.reader() as FileReader, originName = file.path, ends = ends, parseEscapeSequences = parseEscapeSequences)`
         * without catching any exceptions, only [FileReader] is explicitly closed.
         *
         * @param file [File] from which to read the *CSV* table.
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         *
         * @return The table written in *CSV* [file].
         *
         */
        public fun readCSV(
            file: File,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Array<Array<String>> {
            // Open *CSV* [file] to read.
            val fileReader: FileReader = file.reader() as FileReader

            // Read *CSV* [file].
            val table: Array<Array<String>> = readCSV(
                fileReader,
                originName = file.path,
                ends = ends,
                parseEscapeSequences = parseEscapeSequences
            )

            // Close *CSV* [file].
            fileReader.close()

            // Return [table] read from *CSV* [file].
            return table
        }

        /**
         * Read a *CSV* file or input.
         *
         * The method returns
         * `readCSV(File(fileNameOrInput), ends = ends, parseEscapeSequences = parseEscapeSequences)`
         * or
         * `readCSV(fileNameOrInput.reader(), ends = ends, parseEscapeSequences = parseEscapeSequences)`
         * without catching any exceptions, however, in the latter case [StringReader] is explicitly
         * closed.
         *
         * @param fileNameOrInput The name of the *CSV* file or an actual *CSV* input.
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         * @param asInput If `true`, [fileNameOrInput] is considered a literal *CSV* input; otherwise [fileNameOrInput] is considered the path to the *CSV* file to read from.
         *
         * @return The table written in the *CSV* file at [fileNameOrInput] or in the actual contents of [fileNameOrInput] (according to [asInput]).
         *
         */
        public fun readCSV(
            fileNameOrInput: String,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false,
            asInput: Boolean = false
        ): Array<Array<String>> = if (asInput) {
            // Open the *CSV* input to read.
            val stringReader: StringReader = fileNameOrInput.reader()

            // Read the *CSV* input.
            val table: Array<Array<String>> = readCSV(
                stringReader,
                ends = ends,
                parseEscapeSequences = parseEscapeSequences
            )

            // Close the *CSV* input.
            stringReader.close()

            // Return [table] read from the *CSV* input.
            table
        } else
            readCSV(File(fileNameOrInput), ends = ends, parseEscapeSequences = parseEscapeSequences)

        /**
         * Read an associations game from a raw table.
         *
         * [rawTable] must:
         * 1. contain at least 6 rows,
         * 2. the first row (index `0`) must be of length of exactly 5 cells each of which may be either [ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING] or [ASSOCIATIONS_TABLE_ALLOW_SHUFFLING],
         * 3. rows 2 to 5 (inclusive) (indices `1` to `4`) must be of the same length which may be either 4 or 5 cells and, if the length is 5 cells, the 5th cell must be empty (an empty [String]),
         * 4. rows 6 until the end (inclusive) (indices `5` to `rawTable.size - 1`) must be of length of exactly 5 cells,
         * 5. if `rawTable[i][j]` is an empty cell for `5 <= i < rawTable.size` and `0 <= j < 5`, then `rawTable[i + k][j]` must also be empty for every `0 <= k < rawTable.size - i - 1` (if a cell is empty in one of those rows, each consecutive row must have an empty cell in the corresponding column—values must be aggregated at the top).
         * If the conditions above are not satisfied, an [IllegalArgumentException] is thrown.
         *
         * Columns of [rawTable] are the following (0-indexed, as is usual in *Kotlin*):
         * * `0` is the column *A*,
         * * `1` is the column *B*,
         * * `2` is the column *C*,
         * * `3` is the column *D*,
         * * `4` is the final solution.
         *
         * Suppose `0 <= j < 5`.  If `rawTable[0][j]` is [ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING],
         * then cells in the corresponding column must not be shuffled (the order of the cells is
         * crucial).  If, on the other hand, `rawTable[0][j]` is
         * [ASSOCIATIONS_TABLE_ALLOW_SHUFFLING], the order of the cells in the column is not
         * crucial, therefor cells may be shuffled without losing any information.  The same is
         * applied on allowing/disallowing the order of columns according to the value of
         * `rawTable[0][4]`.
         *
         * Rows 2 to 5 (inclusive) (indices `1` to `4`) naturally define the values of the cells in
         * the game table.  It is now clear why the 5th column must be empty here if it even exists.
         *
         * Rows 6 until the end (inclusive) (indices `5` to `rawTable.size - 1`) define solutions of
         * columns and the final solution.  Row 6 (index `5`) defines the true solution, while the
         * consecutive rows define alternative acceptable answers.
         *
         * The resulting [Bundle] contains:
         * 1. for each column the allowing of shuffling its cells as a [Boolean] (shuffling is allowed if and only if `true`) at `"columnShuffle"`, where `"column"` is the label of the column (for column *A*, it is `"A"`), and `"Shuffle"` is literally that [String],
         * 2. the allowing of shuffling of columns as a [Boolean] (the same logic as with columns) at `"SolShuffle"` (literally that [String]),
         * 3. for each cell its value as a [String] at the cell's label (for cell *A1*, it is at `"A1"`),
         * 4. for each column its solution + alternative acceptable answers as an [Array] of [String]s at the column's label (for column *A*, it is at `"A"`) in the order of appearance in the table from top to bottom (ignoring the empty cells),
         * 5. the final solution + alternative acceptable answers as an [Array] of [String]s at `"Sol"` in the order of appearance (the same logic as with columns).
         *
         * @param rawTable Raw table of information for an associations game.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @throws IllegalArgumentException See above (conditions on [rawTable]).
         *
         */
        public fun readAssociationsTable(rawTable: Array<Array<String>>): Bundle {
            // Initialise an empty [Bundle] of information.
            val table: Bundle = Bundle()

            // Initialise empty solutions arrays for columns and the final solution.  Also,
            // initialise indicators of reaching empty cells of solutions/alternative acceptable
            // answers in their corresponding columns in  [rawTable] (if -1, a non-empty cell has
            // not yet been reached; otherwise it has been reached in the row at the index given as
            // the indicator's value).
            val columnsValuesEnded: HashMap<String, Int> = HashMap<String, Int>().apply {
                for (column in ASSOCIATIONS_TABLE_COLUMNS_LABELS)
                    put(column, -1)
            }
            val columnsValues: HashMap<String, ArrayList<String>> =
                HashMap<String, ArrayList<String>>().apply {
                    for (column in ASSOCIATIONS_TABLE_COLUMNS_LABELS)
                        put(column, ArrayList())
                }
            var solutionValueEnded: Int = -1
            val solutionValue: ArrayList<String> = ArrayList()

            // Check if [rawTable] has at least 6 rows.
            if (rawTable.size < 6)
                throw IllegalArgumentException(
                    ASSOCIATIONS_TABLE_TOO_SHORT_ERROR_MESSAGE.format(rawTable.size)
                )

            // Check if the first row in [rawTable] has exactly 5 cells.
            if (rawTable[0].size != 5)
                throw IllegalArgumentException(
                    ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_LENGTH_ERROR_MESSAGE.format(
                        rawTable[0].size
                    )
                )

            // Extract shuffling allowings from the first row in [rawTable].
            for (j in 0 until 4)
                when (rawTable[0][j]) {
                    ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING -> table.putBoolean(
                        shuffleAllowingLabel(ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]),
                        false
                    )
                    ASSOCIATIONS_TABLE_ALLOW_SHUFFLING -> table.putBoolean(
                        shuffleAllowingLabel(ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]),
                        true
                    )
                    else -> throw IllegalArgumentException(
                        ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_VALUES_ERROR_MESSAGE.format(
                            ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING,
                            ASSOCIATIONS_TABLE_ALLOW_SHUFFLING,
                            rawTable[0][j]
                        )
                    )
                }
            when (rawTable[0][4]) {
                ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING -> table.putBoolean(
                    shuffleAllowingLabel(ASSOCIATIONS_TABLE_SOLUTION_LABEL),
                    false
                )
                ASSOCIATIONS_TABLE_ALLOW_SHUFFLING -> table.putBoolean(
                    shuffleAllowingLabel(ASSOCIATIONS_TABLE_SOLUTION_LABEL),
                    true
                )
                else -> throw IllegalArgumentException(
                    ASSOCIATIONS_TABLE_SHUFFLE_ALLOWING_INDICATOR_ILLEGAL_VALUES_ERROR_MESSAGE.format(
                        ASSOCIATIONS_TABLE_DISALLOW_SHUFFLING,
                        ASSOCIATIONS_TABLE_ALLOW_SHUFFLING,
                        rawTable[0][4]
                    )
                )
            }

            // Extract values of cells from rows 2 to 5 (inclusive) in [rawTable].
            for (i in 1 until 5) {
                // Check if the row is of the same length as row 2.
                if (rawTable[i].size != rawTable[1].size)
                    throw IllegalArgumentException(
                        ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_INCONSISTENT_LENGTH_ERROR_MESSAGE.format(
                            2,
                            rawTable[1].size,
                            i + 1,
                            rawTable[i].size
                        )
                    )

                // Check the length of the row and the content of the 5th cell if it exists.
                when (rawTable[i].size) {
                    4 -> Unit
                    5 -> {
                        if (rawTable[i][4] != String())
                            throw IllegalArgumentException(
                                ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_ILLEGAL_NON_EMPTY_LAST_CELL_ERROR_MESSAGE.format(
                                    i + 1,
                                    rawTable[i][4]
                                )
                            )
                    }
                    else -> throw IllegalArgumentException(
                        ASSOCIATIONS_TABLE_FIRST_FOUR_ROWS_ILLEGAL_LENGTH_ERROR_MESSAGE.format(
                            i + 1,
                            rawTable[i].size
                        )
                    )
                }

                // Extract values of cells in the row.
                for (j in 0 until 4)
                    table.putString(
                        joinRowAndColumnLabels(
                            ASSOCIATIONS_TABLE_ROWS_LABELS[i - 1],
                            ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]
                        ),
                        rawTable[i][j]
                    )
            }

            // Extract solutions/alternative acceptable answers from rows 6 until the end
            // (inclusive) in [rawTable].
            for (i in 5 until rawTable.size) {
                // Check if the row has exactly 5 cells.
                if (rawTable[i].size != 5)
                    throw IllegalArgumentException(
                        ASSOCIATIONS_TABLE_SOLUTIONS_ILLEGAL_LENGTH_ERROR_MESSAGE.format(
                            i + 1,
                            rawTable[i].size
                        )
                    )

                // Extract answers for solutions of columns.
                for (j in 0 until 4)
                    when (rawTable[i][j]) {
                        // Indicate that the column has reached an empty cell.
                        String() -> {
                            if (columnsValuesEnded[ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]]!! == -1)
                                columnsValuesEnded[ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]] = i
                        }

                        else -> {
                            // Check if the column has previously reached an empty cell.
                            if (columnsValuesEnded[ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]]!! != -1)
                                throw IllegalArgumentException(
                                    ASSOCIATIONS_TABLE_SOLUTIONS_LATE_ENTRY_ERROR_MESSAGE.format(
                                        j + 1,
                                        i + 1,
                                        columnsValuesEnded[ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]]!! +
                                            1
                                    )
                                )

                            // Add the answer to [columnsValues].
                            columnsValues[ASSOCIATIONS_TABLE_COLUMNS_LABELS[j]]!!.add(
                                rawTable[i][j]
                            )
                        }
                    }
                when (rawTable[i][4]) {
                    // Indicate that the final solution has reached an empty cell.
                    String() -> {
                        if (solutionValueEnded == -1)
                            solutionValueEnded = i
                    }

                    else -> {
                        // Check if the final solution has previously reached an empty cell.
                        if (solutionValueEnded != -1)
                            throw IllegalArgumentException(
                                ASSOCIATIONS_TABLE_SOLUTIONS_LATE_ENTRY_ERROR_MESSAGE.format(
                                    5,
                                    i + 1,
                                    solutionValueEnded + 1
                                )
                            )

                        // Add the answer to [solutionValue].
                        solutionValue.add(rawTable[i][4])
                    }
                }
            }

            // Put values from [columnsValues] and [solutionValue] to [table].
            for (column in ASSOCIATIONS_TABLE_COLUMNS_LABELS)
                table.putStringArray(column, columnsValues[column]!!.toArray(arrayOf()))
            table.putStringArray(
                ASSOCIATIONS_TABLE_SOLUTION_LABEL, solutionValue.toArray(arrayOf())
            )

            // Return the [Bundle] of extracted and sorted information.
            return table
        }

        /**
         * Read an associations game from a raw table.
         *
         * The method returns
         * `readAssociationsTable(readCSV(reader, originName, precedingLine, ends, parseEscapeSequences))`
         * without catching any exceptions.
         *
         * @param reader *CSV* [Reader] containing the raw table of information for an associations game.
         * @param originName The name or the path of the origin of [reader].
         * @param precedingLine Index of the line directly before the first line read from [reader].
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(
            reader: Reader,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Bundle = readAssociationsTable(
            readCSV(reader, originName, precedingLine, ends, parseEscapeSequences)
        )

        /**
         * Read an associations game from a raw table.
         *
         * The method returns
         * `readAssociationsTable(readCSV(input, originName, precedingLine, ends, parseEscapeSequences))`
         * without catching any exceptions.
         *
         * @param input *CSV* [InputStream] containing the raw table of information for an associations game.
         * @param originName The name or the path of the origin of [input].
         * @param precedingLine Index of the line directly before the first line read from [input].
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(
            input: InputStream,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Bundle = readAssociationsTable(
            readCSV(input, originName, precedingLine, ends, parseEscapeSequences)
        )

        /**
         * Read an associations game from a raw table in a file.
         *
         * The method returns
         * `readAssociationsTable(readCSV(file, ends, parseEscapeSequences))` without catching any
         * exceptions.
         *
         * @param file *CSV* [File] containing the raw table of information for an associations game.
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(
            file: File,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false
        ): Bundle = readAssociationsTable(readCSV(file, ends, parseEscapeSequences))

        /**
         * Read an associations game from a raw table in a file or input.
         *
         * The method returns
         * `readAssociationsTable(readCSV(fileNameOrInput, ends, parseEscapeSequences, asInput))`
         * without catching any exceptions.
         *
         * @param fileNameOrInput The name of the *CSV* file or an actual *CSV* input containing the raw table of information for an associations game.
         * @param ends Characters indicating the end of reading.
         * @param parseEscapeSequences If `true`, any escape sequence in the text is parsed using [escapeSequence] method.
         * @param asInput If `true`, [fileNameOrInput] is considered a literal *CSV* input; otherwise [fileNameOrInput] is considered the path to the *CSV* file to read from.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(
            fileNameOrInput: String,
            ends: Collection<Char> = setOf(),
            parseEscapeSequences: Boolean = false,
            asInput: Boolean = false
        ): Bundle =
            readAssociationsTable(readCSV(fileNameOrInput, ends, parseEscapeSequences, asInput))
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS                                                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The empty constructor is private.
     *
     */
    private constructor() : super()

    /**
     * The copy constructor is private.
     *
     * @param other Original [TableReader] to copy.
     *
     */
    private constructor(other: TableReader?) : super()


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  OVERRIDDEN METHODS OF [Any]                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the hash code value of the instance of [TableReader].
     *
     * **Note: The method is hardcoded to always return `0`.**
     *
     * @return The hash code of the instance of [TableReader].
     *
     */
    override fun hashCode(): Int = 0

    /**
     * Check whether some other object is "equal to" this one.
     *
     * **Note: The method is hardcoded to always return `false`.**
     *
     * @return If the objects are equal, `true`; `false` otherwise.
     *
     */
    override fun equals(other: Any?): Boolean = false

    /**
     * Get the string representation of the instance of [TableReader].
     *
     * **Note: The method is hardcoded to always return `this.javaClass.name`.**
     *
     * @return The string representation of the instance of [TableReader].
     *
     */
    override fun toString(): String = this.javaClass.name
}
