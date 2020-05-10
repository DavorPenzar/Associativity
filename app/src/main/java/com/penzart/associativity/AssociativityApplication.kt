package com.penzart.associativity

import android.app.Application
import com.google.android.gms.ads.MobileAds
import java.io.File

/**
 * Class of the Associativity application.
 *
 */
class AssociativityApplication : Application() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  COMPANION ELEMENTS                                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public companion object {
        /**
         * Construct a path from subpaths.
         *
         * The method returns [parts] joined to a string with [File.separator] as a separator in
         * the order given.  If any of the [parts] ends with a non-escaped [File.separator], it will
         * be removed before the joining.
         *
         * **Note: This method is implemented as a substitution of [Paths.join] method because of
         * compatibility issues with older versions of *Android*.**
         *
         * @param parts Subpaths to join in a complete path.
         *
         * @return Complete path constructed from subpaths.
         *
         */
        public fun constructPath(vararg parts: String): String {
            // Extract [parts] with terminating [File.separator] removed.
            val refinedParts: Array<String> = Array(parts.size) { i: Int ->
                // If the current part is empty, copy it.
                if (parts[i].isEmpty())
                    String()

                // If the current part does not end with [File.separator], copy it.
                if (!parts[i].endsWith(File.separatorChar))
                    parts[i]

                // Assume the terminating [File.separator] is not escaped.
                var escaping: Boolean = false

                // If an even number of [TableReader.ESCAPE_CHAR]s precedes the terminating
                // [File.separator], the terminating [File.separator] is escaped; otherwise it is
                // not escaped.
                for (j in parts[i].length - 2 downTo 0) {
                    if (parts[i][j].toString() != TableReader.ESCAPE_CHAR)
                        break

                    escaping = !escaping
                }

                // If the terminating [File.separator] is escaped, copy the current part except the
                // terminating [File.separator].
                if (escaping)
                    parts[i].substring(0 until parts[i].length - 1)

                // Copy the current part.
                parts[i]
            }

            // Return [refinedParts] joint into a string delimited by [File.separator]s.
            return refinedParts.joinToString(File.separator)
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  APPLICATION LIFECYCLE METHODS                                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Perform initialisation of the application.
     *
     * [MobileAds.initialize] method is called.
     *
     */
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(applicationContext)
    }
}
