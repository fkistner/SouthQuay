package com.fkistner.SouthQuay.UI

/**
 * Interface for an implementation that reacts and potentially responds to script execution requests/stimuli.
 * @param T Type of the stimuli response.
 */
interface ExecutionState<out T> {
    /** Requests to start the script execution (run stimulus). */
    fun run(): T

    /** Requests to abort the script execution (abort stimulus). */
    fun abort(): T

    /** Signals that the script execution has finished (finish stimulus). */
    fun finish(): T
}
