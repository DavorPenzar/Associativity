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
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            textId: Int,
            listener: DialogInterface.OnClickListener
        ): AlertDialog.Builder = setPositiveButton(textId, listener)

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            textId: Int,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ): AlertDialog.Builder = setPositiveButton(textId, listener)

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param text The text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            text: CharSequence,
            listener: DialogInterface.OnClickListener
        ): AlertDialog.Builder = setPositiveButton(text, listener)

        /**
         * Set a listener to be invoked when the try button of the dialog is pressed.
         *
         * @param text The text to display in the try button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setTryButton(
            text: CharSequence,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ): AlertDialog.Builder = setPositiveButton(text, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            textId: Int,
            listener: DialogInterface.OnClickListener
        ): AlertDialog.Builder = setNegativeButton(textId, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            textId: Int,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ): AlertDialog.Builder = setNegativeButton(textId, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param text The text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            text: CharSequence,
            listener: DialogInterface.OnClickListener
        ): AlertDialog.Builder = setNegativeButton(text, listener)

        /**
         * Set a listener to be invoked when the dismiss button of the dialog is pressed.
         *
         * @param text The text to display in the dismiss button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setDismissButton(
            text: CharSequence,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ): AlertDialog.Builder = setNegativeButton(text, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            textId: Int,
            listener: DialogInterface.OnClickListener
        ): AlertDialog.Builder = setNeutralButton(textId, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param textId The resource id of the text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            textId: Int,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ): AlertDialog.Builder = setNeutralButton(textId, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param text The text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            text: CharSequence,
            listener: DialogInterface.OnClickListener
        ): AlertDialog.Builder = setNeutralButton(text, listener)

        /**
         * Set a listener to be invoked when the give up button of the dialog is pressed.
         *
         * @param text The text to display in the give up button.
         * @param listener [DialogInterface.OnClickListener] to use.
         *
         * @return This [AlertDialog.Builder] object to allow for chaining of calls to set method.
         *
         */
        private fun AlertDialog.Builder.setGiveUpButton(
            text: CharSequence,
            listener: (dialog: DialogInterface, which: Int) -> Unit
        ): AlertDialog.Builder = setNeutralButton(text, listener)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  PROPERTIES' DECLARATION AND INITIALISATION                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private lateinit var listener: GuessDialogListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  AUXILIARY METHODS                                                                         //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get one of the buttons used in the dialog.
     *
     * If the specified button does not exist or the dialog has not yet been fully created,
     * `null` is returned.
     *
     * @param whichButton The identifier of the button that should be returned.  For example, this can be [BUTTON_TRY].
     *
     * @return The button from [dialog], or `null` if a button does not exist.
     *
     */
    private fun getButton(whichButton: Int): Button = getButton(whichButton, dialog as AlertDialog)

    /**
     * Get one of the buttons used in the dialog.
     *
     * If the specified button does not exist or the dialog has not yet been fully created,
     * `null` is returned.
     *
     * @param whichButton The identifier of the button that should be returned.  For example, this can be [BUTTON_TRY].
     * @param dialog [AlertDialog] of a [GuessDialog] from which to get the button.
     *
     * @return The button from [dialog], or `null` if a button does not exist.
     *
     */
    private fun getButton(whichButton: Int, dialog: AlertDialog): Button =
        dialog.getButton(whichButton)

    /**
     * Make IME action *done* of guessing to be clicking the try button.
     *
     */
    private fun connectEditTextEnterGuessAndPositiveButton() =
        connectEditTextEnterGuessAndPositiveButton(dialog as AlertDialog)

    /**
     * Make IME action *done* of guessing to be clicking the try button of [AlertDialog].
     *
     * @param dialog [AlertDialog] of a [GuessDialog] with [EditText] for typing a guess.
     *
     */
    private fun connectEditTextEnterGuessAndPositiveButton(dialog: AlertDialog) =
        dialog.findViewById<EditText>(R.id.editTextEnterGuess).setOnEditorActionListener {
            _, actionId: Int, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        getButton(BUTTON_TRY, dialog).performClick()

                        true
                    }
                    else -> false
                }
        }

    /**
     * Make IME action *done* of guessing to be clicking the try button.
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
     * Disable the give up button.
     *
     */
    private fun disableGiveUpButton() = disableGiveUpButton(dialog as AlertDialog)

    /**
     * Disable the give up button of [AlertDialog].
     *
     * @param dialog [AlertDialog] of a [GuessDialog] to which to disable the give up button.
     *
     */
    private fun disableGiveUpButton(dialog: AlertDialog) = getButton(BUTTON_GIVE_UP, dialog).apply {
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
     */
    private fun displayElaborateGuessHint(hint: String?) =
        displayElaborateGuessHint(hint, dialog as AlertDialog)

    /**
     * Print an elaborate hint for guessing to [AlertDialog].
     *
     * @param hint Elaborate hint to display.
     * @param dialog [AlertDialog] of a [GuessDialog] at which to display [hint].
     *
     */
    private fun displayElaborateGuessHint(hint: String?, dialog: AlertDialog) =
        dialog.setMessage(hint)

    /**
     * Print a brief hint for guessing.
     *
     * @param hint Brief hint to display.
     *
     */
    private fun displayBriefGuessHint(hint: String?) =
        displayBriefGuessHint(hint, dialog as AlertDialog)

    /**
     * Print a brief hint for guessing to [AlertDialog].
     *
     * @param hint Brief hint to display.
     * @param dialog [AlertDialog] of a [GuessDialog] with [EditText] for typing a guess.
     *
     */
    private fun displayBriefGuessHint(hint: String?, dialog: AlertDialog) {
        dialog.findViewById<EditText>(R.id.editTextEnterGuess).hint = hint
    }

    /**
     * Print a brief hint for guessing to [View].
     *
     * @param hint Brief hint to display.
     * @param view [View] with [EditText] for typing a guess.
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
    private fun retrieveGuess(): String = retrieveGuess(dialog as AlertDialog)

    /**
     * Get the currently written guess in [AlertDialog].
     *
     * @param dialog [AlertDialog] of a [GuessDialog] with [EditText] for typing a guess.
     *
     * @return Currently written guess.
     *
     */
    private fun retrieveGuess(dialog: AlertDialog): String =
        dialog.findViewById<EditText>(R.id.editTextEnterGuess).text.toString()

    /**
     * Get the currently written guess in [View].
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
    private fun typeGuess(guess: String?) = typeGuess(guess, dialog as AlertDialog)

    /**
     * Set the guess to [AlertDialog].
     *
     * @param guess Guess to set.
     * @param dialog [AlertDialog] of a [GuessDialog] with [EditText] for typing a guess.
     *
     * @see retrieveGuess
     *
     */
    private fun typeGuess(guess: String?, dialog: AlertDialog) =
        dialog.findViewById<EditText>(R.id.editTextEnterGuess).setText(guess)

    /**
     * Set the guess to [View].
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
     * Attach this [GuessDialog] object to a [Context] object.
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
     * Create [Dialog] to be displayed by this [GuessDialog] object.
     *
     * @param savedInstanceState The last saved instance state of [GuessDialog], or null if this is a freshly created [GuessDialog].
     *
     * @return A new [Dialog] instance to be displayed by [GuessDialog].
     *
     */
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create [builder] of the resulting dialog.
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity).apply {
            // Set the dialog to cancelable.
            setCancelable(true)

            // Set [View] of the dialog.
            setView(
                (activity?.layoutInflater ?: LayoutInflater.from(activity)).inflate(
                    R.layout.dialog_guess,
                    null
                ).apply {
                    // Display brief hint.
                    displayBriefGuessHint(listener.retrieveGuessHint(elaborate = false), this)

                    // Make IME action *done* of guessing to be clicking the button for guessing.
                    connectEditTextEnterGuessAndPositiveButton(this)

                    // If [savedInstanceState] is not `null`, recover the typed guess.
                    if (savedInstanceState != null)
                        typeGuess(savedInstanceState.getString(GUESS_INPUT), this)
                }
            )

            // Set title and message (elaborate hint) of the dialog.
            setTitle(listener.retrieveGuessHint(elaborate = false))
            setMessage(listener.retrieveGuessHint(elaborate = true))

            // Set buttons of the dialog.  Create a button for giving up only if giving up is
            // allowed.
            setTryButton(R.string.guess_dialog_try) { dialog: DialogInterface, _ ->
                listener.guessTry(retrieveGuess(dialog as AlertDialog))
            }
            setDismissButton(R.string.guess_dialog_dismiss) { dialog: DialogInterface, _ ->
                dialog.cancel()
            }
            if (listener.isGuessGivingUpAllowed())
                setGiveUpButton(R.string.guess_dialog_give_up) { _, _ -> listener.guessGiveUp() }
        }

        // Create [dialog] from [builder].
        val dialog: AlertDialog = builder.create()

        // Set [dialog] to be canceled when touched outside.
        dialog.setCanceledOnTouchOutside(true)

        // Return [dialog].
        return dialog
    }
}
