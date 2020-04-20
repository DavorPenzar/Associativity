package com.penzart.associativity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * The game launcher activity.
 *
 * See [MainActivity] to understand difficulty level labels.
 *
 * @property difficultiesEnabled Mapping from difficulty level's labels to their enablings.
 *
 * @see MainActivity
 *
 */
class LauncherActivity : AppCompatActivity() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  COMPANION ELEMENTS                                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The companion object of the class [LauncherActivity].
     *
     * @property SUFFIX_ENABLED Suffix for labels of difficulty levels to save their enablings in [onSaveInstanceState].
     *
     */
    private companion object {

        ////////////////////////////////////////////////////////////////////////////////////////////
        //  LABELS FOR [onSaveInstanceState] AND [onRestoreInstanceState] METHODS                 //
        ////////////////////////////////////////////////////////////////////////////////////////////

        private const val SUFFIX_ENABLED: String = "Enabled"


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  AUXILIARY METHODS                                                                     //
        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Construct a label for saving difficulty level enabling in [onSaveInstanceState] from a difficulty level's label and a suffix.
         *
         * @param label A difficulty level's label.
         * @param suffix A suffix for construction.
         *
         * @return The constructed mixed label.
         *
         */
        private fun appendSuffix(label: Int, suffix: String): String = label.toString() + suffix

        /**
         * Construct a label for saving difficulty level enabling in [onSaveInstanceState] from a difficulty level's label.
         *
         * @param label A difficulty level's label.
         *
         * @return The constructed mixed label.
         *
         */
        private fun enabledLabel(label: Int): String = appendSuffix(label, SUFFIX_ENABLED)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  PROPERTIES' DECLARATION AND INITIALISATION                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val difficultiesEnabled: HashMap<Int, Boolean> = HashMap()


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  AUXILIARY METHODS                                                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the array of difficulty levels' labels.
     *
     * @return The array of difficulty levels' labels.
     *
     */
    private fun arrayOfDifficulties(): Array<Int> =
        resources.getIntArray(R.array.difficulty_labels_int).toTypedArray()

    /**
     * Get the id of the button for choosing a difficulty level.
     *
     * @param difficulty Difficulty level label.
     *
     * @return Id of the button of the [difficulty] level.
     *
     */
    private fun idOfButton(difficulty: Int): Int = resources.getIdentifier(
        resources.getString(R.string.button) + difficulty.toInt(),
        resources.getString(R.string.id),
        packageName
    )

    /**
     * Given a known button for choosing the difficulty level, get its difficulty level label.
     *
     * @param button A difficulty level choosing button
     *
     * @return The difficulty level label represented by [button].
     *
     */
    private fun retrieveGameDifficulty(button: Button): Int = button.tag.toString().toInt()


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  INITIALISING METHODS                                                                      //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reset inner properties to their default values.
     *
     */
    private fun resetProperties() = difficultiesEnabled.clear()


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  DIFFICULTY LEVELS BUTTONS                                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Check if a difficulty level's assets subdirectory is non-empty meaning the difficulty level should be enabled.
     *
     * The method returns
     * `MainActivity.isGameTablesSubdirectoryNonEmpty(MainActivity.GAME_TABLES_DEFAULT_DIRECTORY, difficulty, getExternalFilesDir(null), assets)`.
     *
     * @param difficulty Difficulty level label.
     *
     * @return If the [difficulty] level's assets subdirectory is non-empty, `true`; `false` otherwise.
     *
     * @see isDifficultyEnabled
     *
     */
    private fun shouldDifficultyBeEnabled(difficulty: Int): Boolean =
        MainActivity.isGameTablesSubdirectoryNonEmpty(
            MainActivity.GAME_TABLES_DEFAULT_DIRECTORY,
            difficulty,
            assets,
            getExternalFilesDir(null)
        )

    /**
     * Check if a difficulty level is enabled.
     *
     * @param difficulty Difficulty level label.
     *
     * @return If the [difficulty] level is enabled, `true`; `false` otherwise.
     *
     * @see shouldDifficultyBeEnabled
     * @see enableDifficulty
     * @see disableDifficulty
     *
     */
    private fun isDifficultyEnabled(difficulty: Int): Boolean = difficultiesEnabled[difficulty]!!

    /**
     * Change difficulty level's enabling.
     *
     * If [enabled] is `null`, the difficulty level's enabling state is toggled (a disabled
     * difficulty level will be enabled and vice versa).
     *
     * **Note: This method merely changes what [isDifficultyEnabled] method will return.  To
     * actually enable/disable a difficulty level call [enableDifficulty] or [disableDifficulty]
     * method instead which will in turn call this method.**
     *
     * @param difficulty Difficulty level label.
     * @param enabled New enabling of the [difficulty] level.
     *
     * @see isDifficultyEnabled
     * @see enableDifficulty
     * @see disableDifficulty
     *
     */
    private fun changeDifficultyEnabling(difficulty: Int, enabled: Boolean? = null) {
        difficultiesEnabled[difficulty] = enabled ?: !difficultiesEnabled[difficulty]!!
    }

    /**
     * Enable a difficulty level.
     *
     * The method enables the button for choosing the [difficulty] level and sets its on-click
     * method to [launchNewGame].
     *
     * @param difficulty Difficulty level label.
     *
     */
    private fun enableDifficulty(difficulty: Int) {
        // Get [button] for choosing the [difficulty] level.
        val button: Button = findViewById(idOfButton(difficulty))

        // Enable [button].
        button.apply {
            isEnabled = true
            isClickable = true
        }

        // Set the on-click method.
        button.setOnClickListener(this::launchNewGame)

        // Enable the [difficulty] level.
        changeDifficultyEnabling(difficulty, true)
    }

    /**
     * Disable a difficulty level.
     *
     * The method disables the button for choosing the [difficulty] level and removes its on-click
     * method.
     *
     * @param difficulty Difficulty level label.
     *
     */
    private fun disableDifficulty(difficulty: Int) {
        findViewById<Button>(idOfButton(difficulty)).apply {
            // Remove on-click method.
            setOnClickListener(null)

            // Disable the button.
            isEnabled = false
            isClickable = false
        }

        // Disable the [difficulty] level.
        changeDifficultyEnabling(difficulty, false)
    }

    /**
     * On-click method for buttons for choosing the difficulty level.
     *
     * Launches a new game by starting [MainActivity] with an [Intent] saving the chosen difficulty
     * level at key `resources.getString(R.string.difficulty)`.
     *
     * @param it The button for choosing the difficulty level (instance of [Button] class).
     *
     * @see MainActivity
     * @see MainActivity.onCreate
     *
     */
    public fun launchNewGame(it: View) {
        // Get the chosen difficulty level.
        val difficulty: Int = retrieveGameDifficulty(it as Button)

        // Create an [intent] for starting [MainActivity]
        val intent: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra(resources.getString(R.string.dif), difficulty)
        }

        // Start [MainActivity].
        startActivity(intent)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  ACTIVITY LIFECYCLE METHODS                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Save all appropriate fragments' state.
     *
     * @param outState [Bundle] in which to place saved state.
     *
     * @see onCreate
     * @see onRestoreInstanceState
     *
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save enablings of difficulty levels.
        outState.apply {
            for (i in arrayOfDifficulties())
                outState.putBoolean(enabledLabel(i), isDifficultyEnabled(i))
        }
    }

    /**
     * Restore any view state that had previously been frozen by [onSaveInstanceState].
     *
     * This method is called after onStart when the activity is being re-initialized from a
     * previously saved state, given here in [savedInstanceState].  This method is called between
     * [onStart] and [onPostCreate].
     *
     * @param savedInstanceState The data most recently supplied in [onSaveInstanceState] method.
     *
     * @see onSaveInstanceState
     *
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore enablings of difficulty levels.  Actual disabling is done in [onResume] method
        // where needed.
        savedInstanceState.apply {
            for (i in arrayOfDifficulties())
                changeDifficultyEnabling(i, getBoolean(enabledLabel(i)))
        }
    }

    /**
     * Perform initialisation of all fragments.
     *
     * @param savedInstanceState If the activity is being re-initialised after previously being shut down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState] method.  **Note: Otherwise it is `null`.**
     *
     * @see onSaveInstanceState
     * @see onRestoreInstanceState
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // Reset inner properties.
        resetProperties()

        // Initialise external storage for defining custom game tables.
        MainActivity.initialiseStorage(
            MainActivity.GAME_TABLES_DEFAULT_DIRECTORY,
            getExternalFilesDir(null)!!,
            resources.getString(R.string.custom_game_tables_readme_filename),
            resources.getString(
                R.string.custom_game_tables_readme,
                MainActivity.GAME_TABLES_DEFAULT_DIRECTORY
            )
        )

        // Disable difficulty levels for which no game tables are provided.
        if (savedInstanceState == null)
            for (i in arrayOfDifficulties())
                changeDifficultyEnabling(i, shouldDifficultyBeEnabled(i))
    }

    /**
     * Do final initialisation after application code has run.
     *
     * Called when activity start-up is complete (after [onStart] and [onRestoreInstanceState]
     * methods have been called).
     *
     * @param savedInstanceState If the activity is being re-initialised after previously being shut down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState] method.  **Note: Otherwise it is `null`.**
     *
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    /**
     * Dispatch `onStart()` to all fragments.
     *
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * Dispatch `onResume()` to fragments and disable disabled difficulty levels.
     *
     * @see isDifficultyEnabled
     * @see disableDifficulty
     *
     */
    override fun onResume() {
        super.onResume()

        // Disable siabled difficulty levels.
        for (i in arrayOfDifficulties())
            if (!isDifficultyEnabled(i))
                disableDifficulty(i)
    }

    /**
     * Dispatch `onPause()` to fragments.
     *
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * Dispatch `onStop()` to all fragments.
     *
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * Destroy all fragments.
     *
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * Call after [onStop] when the current activity is being re-displayed to the user (the user has navigated back to it).
     *
     * The method will be followed by [onStart] and then [onResume].
     *
     */
    override fun onRestart() {
        super.onRestart()
    }
}