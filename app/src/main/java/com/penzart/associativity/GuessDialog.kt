package com.penzart.associativity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment

/**
 * Guess dialog.
 *
 * @property listener Listener of this [GuessDialog].
 * @property dialog Created [AlertDialog].
 *
 */
class GuessDialog : AppCompatDialogFragment() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  LISTENER INTERFACE                                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Interface for the listener of [GuessDialog].
     *
     */
    public interface GuessDialogListener {

        /**
         * Check if giving up is allowed during guessing.
         *
         * @return If giving up is allowed during guessing, `true`; `false` otherwise.
         *
         */
        fun isGuessGivingUpAllowed(): Boolean

        /**
         * Get the current hint for guessing.
         *
         * @param elaborate If `true`, the elaborate hint is returned; otherwise the brief hint is returned.
         *
         * @return Current hint for guessing.
         *
         */
        fun retrieveGuessHint(elaborate: Boolean = true): String

        /**
         * Try to guess a solution.
         *
         * @param guess Offered guess.
         *
         * @see guessGiveUp
         *
         */
        fun guessTry(guess: String)

        /**
         * Give up guessing a solution.
         *
         * @see guessTry
         *
         */
        fun guessGiveUp()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  COMPANION ELEMENTS                                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The companion object of the class [GuessDialog].
     *
     * @property BUTTON_TRY Identifier for the try button.
     * @property BUTTON_DISMISS Identifier for the dismiss button.
     * @property BUTTON_GIVE_UP Identifier for the give up button.
     *
     * @property GUESS_INPUT Label to save the typed guess in [editTextEnterGuess] in [onSaveInstanceState] method.
     *
     */
    public companion object {

        ////////////////////////////////////////////////////////////////////////////////////////////
        //  PRIVATE CONSTANTS                                                                     //
        ////////////////////////////////////////////////////////////////////////////////////////////

        private const val BUTTON_TRY: Int = DialogInterface.BUTTON_POSITIVE
        private const val BUTTON_DISMISS: Int = DialogInterface.BUTTON_NEGATIVE
        private const val BUTTON_GIVE_UP: Int = DialogInterface.BUTTON_NEUTRAL

        private const val GUESS_INPUT: String = "guess"


        ////////////////////////////////////////////////////////////////////////////////////////////
        //  AUXILIARY METHODS                                                                     //
        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            textId: Int,
            listener: DialogInterface.OnClickListener
        ) = setPositiveButton(textId, listener)

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            textId: Int,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ) = setPositiveButton(textId, listener)

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param text The text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            text: CharSequence,
            listener: DialogInterface.OnClickListener
        ) = setPositiveButton(text, listener)

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param text The text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            text: CharSequence,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ) = setPositiveButton(text, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            textId: Int,
            listener: DialogInterface.OnClickListener
        ) = setNegativeButton(textId, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            textId: Int,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ) = setNegativeButton(textId, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param text The text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            text: CharSequence,
            listener: DialogInterface.OnClickListener
        ) = setNegativeButton(text, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param text The text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            text: CharSequence,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ) = setNegativeButton(text, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            textId: Int,
            listener: DialogInterface.OnClickListener
        ) = setNeutralButton(textId, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            textId: Int,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ) = setNeutralButton(textId, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param text The text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            text: CharSequence,
            listener: DialogInterface.OnClickListener
        ) = setNeutralButton(text, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param text The text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            text: CharSequence,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ) = setNeutralButton(text, listener)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  PROPERTIES' DECLARATION AND INITIALISATION                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private var listener: GuessDialogListener? = null
    private var dialog: AlertDialog? = null


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  AUXILIARY METHODS                                                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get one of the buttons used in [dialog].
     *
     * If the specified button does not exist or [dialog] has not yet been fully created, `null` is returned.
     *
     * @param whichButton The identifier of the button that should be returned.  For example, this can be [BUTTON_TRY].
     *
     * @return The button from [dialog], or `null` if a button does not exist.
     *
     */
    private fun getButton(whichButton: Int): Button = dialog!!.getButton(whichButton)

    /**
     * Make IME action *done* of guessing to be clicking the positive button.
     *
     */
    private fun connectEditTextEnterGuessAndPositiveButton() =
        dialog!!.findViewById<EditText>(R.id.editTextEnterGuess).setOnEditorActionListener {
            _, actionId: Int, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        getButton(BUTTON_TRY).performClick()

                        true
                    }
                    else -> false
                }
        }

    /**
     * Make IME action *done* of guessing to be clicking the positive button.
     *
     * @param view [View] with [EditText] for typing a guess.
     *
     */
    private fun connectEditTextEnterGuessAndPositiveButton(view: View) =
        view.findViewById<EditText>(R.id.editTextEnterGuess).setOnEditorActionListener {
                _, actionId: Int, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    getButton(BUTTON_TRY).performClick()

                    true
                }
                else -> false
            }
        }

    /**
     * Disable the neutral button.
     *
     */
    private fun disableGiveUpButton() = getButton(BUTTON_GIVE_UP).apply {
        // Remove on-click method.
        setOnClickListener(null)

        // Disable the button.
        isEnabled = false
        isClickable = false
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  GUESS DIALOG CONTENTS                                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Print an elaborate hint for guessing.
     *
     * @param hint Elaborate hint to display.
     *
     * @see retrieveGuessHint
     *
     */
    private fun displayElaborateGuessHint(hint: String?) = dialog!!.setMessage(hint)

    /**
     * Print a brief hint for guessing.
     *
     * @param hint Brief hint to display.
     *
     * @see retrieveGuessHint
     *
     */
    private fun displayBriefGuessHint(hint: String?) {
        dialog!!.findViewById<EditText>(R.id.editTextEnterGuess).hint = hint
    }

    /**
     * Print a brief hint for guessing.
     *
     * @param hint Brief hint to display.
     * @param view [View] with [EditText] for typing a guess.
     *
     * @see retrieveGuessHint
     *
     */
    private fun displayBriefGuessHint(hint: String?, view: View) {
        view.findViewById<EditText>(R.id.editTextEnterGuess).hint = hint
    }

    /**
     * Get the currently written guess.
     *
     * @return Currently written guess.
     *
     */
    private fun retrieveGuess(): String =
        dialog!!.findViewById<EditText>(R.id.editTextEnterGuess).text.toString()

    /**
     * Get the currently written guess.
     *
     * @param view [View] with [EditText] for typing a guess.
     *
     * @return Currently written guess.
     *
     */
    private fun retrieveGuess(view: View): String =
        view.findViewById<EditText>(R.id.editTextEnterGuess).text.toString()

    /**
     * Set the guess.
     *
     * @param guess Guess to set.
     *
     * @see retrieveGuess
     *
     */
    private fun typeGuess(guess: String?) =
        dialog!!.findViewById<EditText>(R.id.editTextEnterGuess).setText(guess)

    /**
     * Set the guess.
     *
     * @param guess Guess to set.
     * @param view [View] with [EditText] for typing a guess.
     *
     * @see retrieveGuess
     *
     */
    private fun typeGuess(guess: String?, view: View) =
        view.findViewById<EditText>(R.id.editTextEnterGuess).setText(guess)


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  ACTIVITY LIFECYCLE METHODS                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Attach [GuessDialog] to [Context].
     *
     * @param context [Context] of [GuessDialog].
     *
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Set [listener] to [context].
        listener = context as GuessDialogListener
    }

    /**
     * Save all appropriate fragments' state.
     *
     * @param outState [Bundle] in which to place saved state.
     *
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the typed guess.
        outState.putString(GUESS_INPUT, retrieveGuess())
    }

    /**
     * Show [GuessDialog].
     *
     * @param savedInstanceState The last saved instance state of [GuessDialog], or null if this is a freshly created [GuessDialog].
     *
     * @return A new [Dialog] instance to be displayed by [GuessDialog].
     *
     */
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Initialise [builder] of resulting [dialog].
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        // Create [View] of [dialog].
        val view: View = (activity?.layoutInflater ?: LayoutInflater.from(activity)).inflate(
            R.layout.dialog_guess, null
        ).apply {
            // Display brief hint.
            displayBriefGuessHint(listener!!.retrieveGuessHint(elaborate = false), this)

            // Make IME action *done* of guessing to be clicking the button for guessing.
            connectEditTextEnterGuessAndPositiveButton(this)

            // If [savedInstanceState] is not `null`, recover the typed guess.
            if (savedInstanceState != null)
                typeGuess(savedInstanceState.getString(GUESS_INPUT), this)
        }

        // Add elements to [builder].
        builder.apply {
            // Set [View] of [dialog].
            setView(view)

            // Set title and message (elaborate hint) of [dialog].
            setTitle(listener!!.retrieveGuessHint(elaborate = false))
            setMessage(listener!!.retrieveGuessHint(elaborate = true))

            // Set buttons of [dialog].  Create a button for giving up only if giving up is
            // allowed.
            setTryButton(R.string.guess_dialog_try) { _, _ -> listener!!.guessTry(retrieveGuess()) }
            setDismissButton(R.string.guess_dialog_dismiss) { _, _ -> dialog!!.cancel() }
            if (listener!!.isGuessGivingUpAllowed())
                setGiveUpButton(R.string.guess_dialog_give_up) { _, _ -> listener!!.guessGiveUp() }
        }

        // Create [dialog] from [builder].
        dialog = builder.create()

        // Return [dialog].
        return dialog!!
    }
}
