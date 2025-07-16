package com.example.cs446_fit4me.model

data class Quote(
    val text: String,
    val author: String? = null
) {
    companion object {
        val motivationalQuotes = listOf(
            Quote("Push yourself, no one else is going to do it for you."),
            Quote("No pain, no gain."),
            Quote("Don't limit your challenges, challenge your limits."),
            Quote("Train insane or remain the same."),
            Quote("It never gets easier. You just get stronger."),
            Quote("The only bad workout is the one that didn’t happen."),
            Quote("Success starts with self-discipline."),
            Quote("Wake up. Work out. Crush it. Repeat."),
            Quote("Sweat is fat crying."),
            Quote("The pain you feel today will be the strength you feel tomorrow.")
        )
    }
}
