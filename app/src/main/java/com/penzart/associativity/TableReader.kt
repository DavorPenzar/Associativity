package com.penzart.associativity

import android.os.Bundle
import java.io.*

/**
 * Read a table of information for an associations game.
 *
 * The format of the table is defined in [readAssociationsTable].  This class also provides an
 * interface to read information from a CSV file, and the format of supported CSV files is defined
 * in [readCSV] and [escapeExpression].
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
     * @property ILLEGAL_ESCAPE_EXPR_LENGTH_ERROR_MESSAGE Error message for an exception when the argument `expression` in [escapeExpression] method is not a single character string.
     * @property ILLEGAL_ESCAPE_CHAR_ERROR_MESSAGE Format error message for an exception when the argument `expression` in [escapeExpression] method is not a valid escaping expression.
     *
     * @property CSV_NEGATIVE_PRECEDING_LINE_INDEX_ERROR_MESSAGE Format error message for an exception when a negative `precedingLine` value is passed in [readCSV] method.
     * @property CSV_UNEXPECTED_LINE_END_ERROR_MESSAGE Format error message for an exception when a line unexpectedly ends when reading a CSV file in [readCSV] method.
     * @property CSV_EXPECTED_CELL_END_ERROR_MESSAGE Format error message when a non-whitespace character that is not a cell separator is found after closing a cell when reading a CSV file in [readCSV] method.
     * @property CSV_ILLEGAL_ESCAPE_EXPR_ERROR_MESSAGE Format error message for an exception when an illegal escape expression is found (when [escapeExpression] method throws an exception) when reading a CSV file in [readCSV] method.
     * @property CSV_ILLEGAL_QUOTES_ERROR_MESSAGE Format error message for an exception when illegal unescaped quotes appear in [readCSV] method.
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
     * @property CSV_LINE_BREAK A single-character string representing a line break character in CSV files.  Actually a string of a single new-line character, i. e. the string `"\n"`.
     * @property CSV_SINGLE_QUOTE A single-character string representing a single quote in CSV files.  Actually a string of a single single quote character, i. e. the string `"\'"`.
     * @property CSV_DOUBLE_QUOTE A single-character string representing double quotes in CSV files.  Actually a string of a single double quotes character, i. e. the string `"\""`.
     * @property CSV_SEPARATOR A single-character string representing a separator in CSV files.  Actually a string of a single comma character, i. e. the string `","`.
     * @property CSV_ESCAPE_CHAR A single-character string representing the escape character in CSV files.  Actually a string of a single backslash character, i. e. the string `"\\"`.
     *
     * @property CSV_LITERAL_LINE_BREAK The string of the proper escaping expression of [CSV_LINE_BREAK] in CSV files.  Actually a string of [CSV_ESCAPE_CHAR] followed by the character `'n'`, i. e. the string `"\\n"`.
     * @property CSV_LITERAL_SINGLE_QUOTE The string of the proper escaping expression of [CSV_SINGLE_QUOTE] in CSV files.  Actually a string of [CSV_ESCAPE_CHAR] followed by [CSV_SINGLE_QUOTE], i. e. the string `"\\\'"`.
     * @property CSV_LITERAL_DOUBLE_QUOTE The string of the proper escaping expression of [CSV_DOUBLE_QUOTE] in CSV files.  Actually a string of [CSV_ESCAPE_CHAR] followed by [CSV_DOUBLE_QUOTE], i. e. the string `"\\\""`.
     * @property CSV_LITERAL_SEPARATOR The string of the proper escaping expression of [CSV_SEPARATOR] in CSV files.  Actually a string of [CSV_ESCAPE_CHAR] followed by [CSV_SEPARATOR], i. e. the string `"\\,"`.
     * @property CSV_LITERAL_ESCAPE_CHAR The string of the proper escaping expression of [CSV_ESCAPE_CHAR] in CSV files.  Actually a string of double [CSV_ESCAPE_CHAR], i. e. the string `"\\\\"`.
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
            "Escape expression must be a single character string."
        private const val ILLEGAL_ESCAPE_CHAR_ERROR_MESSAGE: String =
            "Illegal escape expression \"\\%s\"."

        private const val CSV_NEGATIVE_PRECEDING_LINE_INDEX_ERROR_MESSAGE: String =
            "Preceding line index must be non-negative; %d is given instead."
        private const val CSV_UNEXPECTED_LINE_END_ERROR_MESSAGE: String =
            "Unexpected line end in %s:%d.%d."
        private const val CSV_EXPECTED_CELL_END_ERROR_MESSAGE: String =
            "Expected a separator or a line end in %s:%d.%d."
        private const val CSV_ILLEGAL_ESCAPE_EXPR_ERROR_MESSAGE: String =
            "Escape expression failed in %s:%d.%d."
        private const val CSV_ILLEGAL_QUOTES_ERROR_MESSAGE: String =
            "Unescaped quotes not allowed in %s:%d.%d."

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

        public const val SUFFIX_ASSOCIATIONS_TABLE_SHUFFLE: String = "Shuffle"


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  PUBLIC CONSTANTS                                                                      //
        ////////////////////////////////////////////////////////////////////////////////////////////

        public const val CSV_LINE_BREAK: String = "\n"
        public const val CSV_SINGLE_QUOTE: String = "\'"
        public const val CSV_DOUBLE_QUOTE: String = "\""
        public const val CSV_SEPARATOR: String = ","
        public const val CSV_ESCAPE_CHAR: String = "\\"

        public const val CSV_LITERAL_LINE_BREAK: String = "\\\n"
        public const val CSV_LITERAL_SINGLE_QUOTE: String = "\\\'"
        public const val CSV_LITERAL_DOUBLE_QUOTE: String = "\\\""
        public const val CSV_LITERAL_SEPARATOR: String = "\\\\"
        public const val CSV_LITERAL_ESCAPE_CHAR: String = "\\,"

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
        private fun appendSuffix(label: String, suffix: String): String {
            return label + suffix
        }

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
        public fun shuffleAllowingLabel(label: String): String {
            return appendSuffix(label, SUFFIX_ASSOCIATIONS_TABLE_SHUFFLE)
        }

        /**
         * Construct a cell's label for saving values in [readAssociationsTable] from the row's and the column's labels.
         *
         * @param row Row's label.
         * @param column Column's label.
         *
         * @return Cell's label.
         *
         */
        public fun joinRowAndColumnLabels(row: String, column: String): String {
            return column + row
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  READING METHODS                                                                       //
        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Parse an escaping expression.
         *
         * Valid escaping expressions and their parsed results are:
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
         * * `"e"` to the escaping character which is the backslash, i. e. `"\\"`,
         * * `","` to a comma, i. e. `","`.
         *
         * **Note: The initial escaping character [escapeExpression] *must* not be passed in
         * [expression], only the actual escaping expression should be passed.  For instance, to
         * parse `"\\n"`, pass only `"n"` as the argument [expression], not the complete string
         * `"\\n"`.**
         *
         * @param expression Escaping expression to parse.
         *
         * @return The parsed result of the escaping expression.
         *
         * @throws IllegalArgumentException If [expression] is not a single-character string or if the escaping expression is not valid.
         *
         */
        public fun escapeExpression(expression: String): String {
            // Check if [expression] is not a single-character string.
            if (expression.length != 1)
                throw IllegalArgumentException(ILLEGAL_ESCAPE_EXPR_LENGTH_ERROR_MESSAGE)

            // Parse [expression] and return the result.
            return when (expression) {
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
                "e" -> "\\"
                "," -> ","
                else -> throw IllegalArgumentException(
                    ILLEGAL_ESCAPE_CHAR_ERROR_MESSAGE.format(expression)
                )
            }
        }

        /**
         * Parse an escaping character.
         *
         * The method returns `escapeExpression(c.toString())` without catching any exceptions.
         *
         * @param c Escaping character to parse.
         *
         * @return The parsed result of the escaping expression.
         *
         */
        public fun escapeExpression(c: Char): String {
            return escapeExpression(c.toString())
        }

        /**
         * Read a CSV input.
         *
         * The quotations, separator and escaping character of CSV files are explained by
         * [CSV_SINGLE_QUOTE], [CSV_DOUBLE_QUOTE], [CSV_SEPARATOR] and [CSV_ESCAPE_CHAR].  Lines
         * containing only whitespaces—checked by calling [Char.isWhitespace] method—are ignored
         * (this includes empty lines).  The value of each cell in the table is trimmed by calling
         * [String.trim] method, unless the cell was enclosed in quotes and whitespaces were inside
         * the quotes.
         *
         * Escaping a [CSV_SEPARATOR] is not mandatory if the cell is enclosed in
         * [CSV_SINGLE_QUOTE]s or [CSV_DOUBLE_QUOTE]s.  Also, escaping [CSV_DOUBLE_QUOTE]s in cells
         * enclosed by [CSV_SINGLE_QUOTE]s and vice versa is not mandatory.  However, these
         * characters may also be escaped in cells enclosed by quotes, therefore be careful how
         * [CSV_ESCAPE_CHAR] is used even if the cell is enclosed in quotes.
         *
         * Each cell of the table is considered a [String] value.  To convert data, use another CSV
         * reader or manually parse the result of this method.
         *
         * The only *NA* value is an empty cell.  Cells containing the usual *NA* indicators, such
         * as `"NA"`, `"na"`, `"NaN"`, `"nan"` etc., are parsed as non-empty strings (for instance,
         * `"NA"` results in a cell containing the string `"NA"`).
         *
         * The resulting two-dimensional array is the matrix of cells in the table represented by
         * the CSV read from [bufferedReader].  The first dimension represents the rows of the
         * table, while the second one represents its columns.  Therefore to get the cell in the
         * `j`-th column of the `i`-th row use `table[i][j]`, where `table` is the result of this
         * method.
         *
         * **Note: the method does not check if all rows have the same number of columns.  The
         * number of columns in each row is *n* + 1, where *n* >= 0 is the number of actual
         * separators appearing in the line representing the row (this does not include escaped
         * separator characters and separators inside quotations).  Putting a [CSV_SEPARATOR] at the
         * beginning of a line will result in an empty cell at the row's beginning, while putting it
         * at the end will result in an empty cell at the row's end.**
         *
         * @param bufferedReader [BufferedReader] from which to read the CSV table.
         * @param originName The name or the path of the origin of [bufferedReader].  If the table is read from a file, this parameter should be set to the file's name or its path.  This argument is used to explain errors when throwing exceptions.
         * @param precedingLine Index of the line directly before the first line read from [bufferedReader].  If the first line that is read should have index `1`, set this argument to `0`.  This argument is used to explain errors when throwing exceptions.  **Note: The first column that is read is assumed to have index `1`.**
         *
         * @return The table written in the CSV input.
         *
         * @throws IllegalArgumentException If [precedingLine] is negative.
         * @throws IOException [IOException]s thrown by the [FileReader] used in the method are not caught, and additional [IOException]s may be thrown by the method itself if the CSV [file] is not formatted properly.
         *
         * @see escapeExpression
         *
         */
        public fun readCSV(
            bufferedReader: BufferedReader,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0
        ): Array<Array<String>> {
            // Lambda function for inserting rows in tables.  The resulting [table] is returned.
            val insertRowInTable: (
                table: ArrayList<Array<String>>, row: ArrayList<String>
            ) -> ArrayList<Array<String>> =
                { table: ArrayList<Array<String>>, row: ArrayList<String> ->
                    table.add(row.toArray(arrayOf()))

                    table
                }

            // Lambda function for inserting cells in rows.  If [wereQuotes], the cell is built and
            // added to the end; otherwise it is built, trimmed and then added.  The resulting [row]
            // is returned.
            val insertCellInRow: (
                row: ArrayList<String>, cell: StringBuilder, wereQuotes: Boolean
            ) -> ArrayList<String> =
                { row: ArrayList<String>, cell: StringBuilder, wereQuotes: Boolean ->
                    when (wereQuotes) {
                        true -> row.add(cell.toString())
                        else -> row.add(cell.toString().trim())
                    }

                    row
                }

            // Check if [precedingRow] is negative.
            if (precedingLine < 0)
                throw IllegalArgumentException(
                    CSV_NEGATIVE_PRECEDING_LINE_INDEX_ERROR_MESSAGE.format(precedingLine)
                )

            // Initialise the resulting [table].
            val table: ArrayList<Array<String>> = ArrayList()

            // Initialise indices of rows.
            var i: Int = precedingLine

            // Read the CSV input.
            while (true) {
                // Increment the row index.
                ++i

                // Read the next [line] from [bufferedReader].  If no [line] is read, break the
                // `while`-loop.
                val line: String = bufferedReader.readLine() ?: break

                // Initialise the [row].
                val row: ArrayList<String> = ArrayList()

                // Initialise [cell] builders.
                var cell: StringBuilder = StringBuilder()

                // Assume the row is empty.
                var emptyLine: Boolean = true

                // Initialise indicators of quotes, expectation of the end of a cell and escaping
                // commands.  If [insideQuotes] is 0, the current character is not inside quotes;
                // if it is 1, the current character is inside single quotes (or the terminating
                // single quote); if it is 2, the current character is inside double quotes (or the
                // terminating double quotes).  After closing quotes, the end of the cell is
                // expected.
                var insideQuotes: Int = 0
                var expectCellEnd: Boolean = false
                var escaping: Boolean = false

                // Read the line.
                for (j in line.indices) {
                    // Get the current character as a [Char] and a [String].
                    val c: Char = line[j]
                    val s = c.toString()

                    // Ignore leading or trailing whitespaces.  If a non-whitespace is read,
                    // indicate that the line is not empty.
                    if (c.isWhitespace()) {
                        if ((cell.isEmpty() && insideQuotes == 0) || expectCellEnd)
                            continue
                    }
                    else
                        emptyLine = false

                    // Check if a separator or the end of the line were expected but were not found.
                    if (expectCellEnd && s != CSV_SEPARATOR)
                        throw IOException(
                            CSV_EXPECTED_CELL_END_ERROR_MESSAGE.format(originName, i, j + 1)
                        )

                    // If an escaping expression is expected, parse it.
                    if (escaping) {
                        // Try to parse the expression.  In case of a fail, throw an [IOException].
                        try {
                            cell.append(escapeExpression(s))
                        } catch (exception: IllegalArgumentException) {
                            throw IOException(
                                CSV_ILLEGAL_ESCAPE_EXPR_ERROR_MESSAGE.format(originName, i, j + 1),
                                exception
                            )
                        }

                        // Set the indicator of an escaping command to `false`.
                        escaping = false

                        // Continue to the next character.
                        continue
                    }

                    // If an escaping character is found, indicate the escaping command.
                    if (s == CSV_ESCAPE_CHAR) {
                        // Set the indicator of an escaping command to `true`.
                        escaping = true

                        // Continue to the next character.
                        continue
                    }

                    // Act accordingly to quotes environment.
                    when (insideQuotes) {
                        // In case of no quotes environment:
                        0 -> when (s) {
                            CSV_SINGLE_QUOTE -> {
                                // Opening quotes are allowed only at the beginning of a [cell].
                                // Throw an exception if the [cell] is not empty.
                                if (cell.isNotEmpty())
                                    throw IOException(
                                        CSV_ILLEGAL_QUOTES_ERROR_MESSAGE.format(
                                            originName,
                                            i,
                                            j + 1
                                        )
                                    )

                                // Open single quotes.
                                insideQuotes = 1
                            }

                            CSV_DOUBLE_QUOTE -> {
                                // Opening quotes are allowed only at the beginning of a [cell].
                                // Throw an exception if the [cell] is not empty.
                                if (cell.isNotEmpty())
                                    throw IOException(
                                        CSV_ILLEGAL_QUOTES_ERROR_MESSAGE.format(
                                            originName,
                                            i,
                                            j + 1
                                        )
                                    )

                                // Open double quotes.
                                insideQuotes = 2
                            }

                            CSV_SEPARATOR -> {
                                // Insert the [cell] in the [row] list of cells.
                                insertCellInRow(row, cell, expectCellEnd)

                                // Do not necessarily expect the end of the [cell] any more.
                                expectCellEnd = false

                                // Initialise a new [cell].
                                cell = StringBuilder()
                            }

                            // Otherwise just add the character to the [cell].
                            else -> cell.append(s)
                        }

                        1 -> when (s) {
                            CSV_SINGLE_QUOTE -> {
                                // Close single quotes.
                                insideQuotes = 0

                                // Expect the end of the [cell].
                                expectCellEnd = true
                            }

                            // Otherwise just add the character to the [cell].
                            else -> cell.append(s)
                        }

                        2 -> when (s) {
                            CSV_DOUBLE_QUOTE -> {
                                // Close double quotes.
                                insideQuotes = 0

                                // Expect the end of the [cell].
                                expectCellEnd = true
                            }

                            // Otherwise just add the character to the [cell].
                            else -> cell.append(s)
                        }
                    }
                }

                // Check if the [line] ended while inside quotes or while expecting an escaping
                // expression.
                if (insideQuotes != 0 || escaping)
                    throw IllegalArgumentException(
                        CSV_UNEXPECTED_LINE_END_ERROR_MESSAGE.format(originName, i, line.length)
                    )

                // If the [line] is not empty, add the [row] to the [table].
                if (!emptyLine)
                    insertRowInTable(table, insertCellInRow(row, cell, expectCellEnd))
            }

            // Return the [table] as a two-dimensional [Array].
            return table.toArray(arrayOf())
        }

        /**
         * Read a CSV input.
         *
         * The method returns `readCSV(BufferedReader(reader), originName, precedingLine)` without
         * catching any exceptions.
         *
         * @param reader [Reader] from which to read the CSV table.
         * @param originName The name or the path of the origin of [reader].
         * @param precedingLine Index of the line directly before the first line read from [reader].
         *
         * @return The table written in [reader].
         *
         */
        public fun readCSV(
            reader: Reader,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0
        ): Array<Array<String>> {
            return readCSV(BufferedReader(reader), originName, precedingLine)
        }

        /**
         * Read a CSV input.
         *
         * The method returns `readCSV(inputStream.reader(), originName, precedingLine)` without
         * catching any exceptions.
         *
         * @param inputStream [InputStream] from which to read the CSV table.
         * @param originName The name or the path of the origin of [inputStream].
         * @param precedingLine Index of the line directly before the first line read from [inputStream].
         *
         * @return The table written in [inputStream].
         *
         */
        public fun readCSV(
            inputStream: InputStream,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0
        ): Array<Array<String>> {
            return readCSV(inputStream.reader(), originName, precedingLine)
        }

        /**
         * Read a CSV file.
         *
         * The method is equivalent to calling `readCSV(FileReader(fileDescriptor), file.path, 0)`
         * without catching any exceptions, only the [FileReader] is explicitly closed.
         *
         * @param fileDescriptor [FileDescriptor] of the file from which to read the CSV table.
         *
         * @return The table written in the CSV file described by [fileDescriptor].
         *
         */
        public fun readCSV(
            fileDescriptor: FileDescriptor,
            originName: String = CSV_DEFAULT_ORIGIN_NAME
        ): Array<Array<String>> {
            // Open the CSV file to read.
            val fileReader: FileReader = FileReader(fileDescriptor)

            // Read the CSV file.
            val table: Array<Array<String>> = readCSV(fileReader, originName, 0)

            // Close the CSV [file].
            fileReader.close()

            // Return the CSV table read from the CSV [file].
            return table
        }

        /**
         * Read a CSV file.
         *
         * The method is equivalent to calling `readCSV(FileReader(file), file.path, 0)` without
         * catching any exceptions, only the [FileReader] is explicitly closed.
         *
         * @param file [File] from which to read the CSV table.
         *
         * @return The table written in the CSV [file].
         *
         */
        public fun readCSV(file: File): Array<Array<String>> {
            // Open the CSV [file] to read.
            val fileReader: FileReader = FileReader(file)

            // Read the CSV [file].
            val table: Array<Array<String>> = readCSV(fileReader, file.path, 0)

            // Close the CSV [file].
            fileReader.close()

            // Return the CSV table read from the CSV [file].
            return table
        }

        /**
         * Read a CSV file.
         *
         * The method returns `readCSV(File(fileName))` without catching any exceptions.
         *
         * @param fileName The name of the CSV file.
         *
         * @return The table written in the CSV file at [fileName].
         *
         */
        public fun readCSV(fileName: String): Array<Array<String>> {
            return readCSV(File(fileName))
        }

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
         * The method returns `readAssociationsTable(readCSV(reader, originName, precedingLine))`
         * without catching any exceptions.
         *
         * @param reader CSV [Reader] containing the raw table of information for an associations game.
         * @param originName The name or the path of the origin of [reader].
         * @param precedingLine Index of the line directly before the first line read from [reader].
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(
            reader: Reader,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0
        ): Bundle {
            return readAssociationsTable(readCSV(reader, originName, precedingLine))
        }

        /**
         * Read an associations game from a raw table.
         *
         * The method returns
         * `readAssociationsTable(readCSV(inputStream, originName, precedingLine))` without catching
         * any exceptions.
         *
         * @param inputStream CSV [InputStream] containing the raw table of information for an associations game.
         * @param originName The name or the path of the origin of [inputStream].
         * @param precedingLine Index of the line directly before the first line read from [inputStream].
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(
            inputStream: InputStream,
            originName: String = CSV_DEFAULT_ORIGIN_NAME,
            precedingLine: Int = 0
        ): Bundle {
            return readAssociationsTable(readCSV(inputStream, originName, precedingLine))
        }

        /**
         * Read an associations game from a raw table in a file.
         *
         * The method returns `readAssociationsTable(readCSV(fileDescriptor))` without catching any
         * exceptions.
         *
         * @param fileDescriptor [FileDescriptor] of the CSV file containing the raw table of information for an associations game.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(fileDescriptor: FileDescriptor): Bundle {
            return readAssociationsTable(readCSV(fileDescriptor))
        }

        /**
         * Read an associations game from a raw table in a file.
         *
         * The method returns `readAssociationsTable(readCSV(file))` without catching any exceptions.
         *
         * @param file CSV [File] containing the raw table of information for an associations game.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(file: File): Bundle {
            return readAssociationsTable(readCSV(file))
        }

        /**
         * Read an associations game from a raw table in a file.
         *
         * The method returns `readAssociationsTable(readCSV(fileName))` without catching any exceptions.
         *
         * @param fileName The name of the CSV file containing the raw table of information for an associations game.
         *
         * @return [Bundle] of extracted and sorted information.
         *
         * @see readCSV
         *
         */
        public fun readAssociationsTable(fileName: String): Bundle {
            return readAssociationsTable(readCSV(fileName))
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS                                                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The empty constructor is private.
     *
     */
    private constructor() {
    }

    /**
     * The copy constructor is private.
     *
     * @param other Original [TableReader] to copy.
     *
     */
    private constructor(other: TableReader) {
    }


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
    override fun hashCode(): Int {
        return 0
    }

    /**
     * Check whether some other object is "equal to" this one.
     *
     * **Note: The method is hardcoded to always return `false`.**
     *
     * @return If the objects are equal, `true`; `false` otherwise.
     *
     */
    override fun equals(other: Any?): Boolean {
        return false
    }

    /**
     * Get the string representation of the instance of [TableReader].
     *
     * **Note: The method is hardcoded to always return `this.javaClass.name`.**
     *
     * @return The string representation of the instance of [TableReader].
     *
     */
    override fun toString(): String {
        return this.javaClass.name
    }
}