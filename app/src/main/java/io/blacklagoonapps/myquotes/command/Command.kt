package io.blacklagoonapps.myquotes.command

interface Command {
    fun execute()

    companion object {
        val NO_OPERATION: Command = object : Command {
            override fun execute() {}
        }
    }
}
