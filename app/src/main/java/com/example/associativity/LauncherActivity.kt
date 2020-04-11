package com.example.associativity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * The game launcher activity.
 *
 */
class LauncherActivity : AppCompatActivity() {
    private fun idOfButton(difficulty: Int): Int {
        return resources.getIdentifier(
            resources.getString(R.string.button) + difficulty.toInt(),
            resources.getString(R.string.id),
            packageName
        )
    }

    /**
     * Given a known button for choosing the difficulty level, get its difficulty level label.
     *
     * @param button A difficulty level choosing button
     *
     * @return The difficulty level label represented by the [button].
     *
     */
    private fun retrieveGameDifficulty(button: Button): Int {
        return button.tag.toString().toInt()
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
            putExtra(resources.getString(R.string.difficulty), difficulty)
        }

        // Start [MainActivity].
        startActivity(intent)
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

        // Disable difficulty levels for which no game tables are provided.
        for (i in resources.getIntArray(R.array.difficulty_labels_int)) {
            if (
                assets.list(
                    MainActivity.constructDifficultyLevelSubdirectoryPath(
                        MainActivity.GAME_TABLES_DEFAULT_DIRECTORY,
                        i
                    )
                )!!.isEmpty()
            ) {
                findViewById<Button>(idOfButton(i)).apply {
                    setOnClickListener(null)

                    isEnabled = false
                    isClickable = false
                }
            }
        }
    }
}