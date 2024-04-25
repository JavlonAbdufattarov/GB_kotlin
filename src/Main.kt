import java.util.regex.Pattern

sealed class Command {
    abstract fun isValid(): Boolean
}

data class AddCommand(val person: Person, val type: String, val value: String) : Command() {
    override fun isValid(): Boolean = when (type) {
        "phone" -> Pattern.matches("\\+\\d+", value)
        "email" -> Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", value)
        else -> false
    }
}

data class ShowCommand(val name: String) : Command() {
    override fun isValid(): Boolean = true
}

object HelpCommand : Command() {
    override fun isValid(): Boolean = true
}

object ExitCommand : Command() {
    override fun isValid(): Boolean = true
}

data class Person(val name: String, var phone: String? = null, var email: String? = null)

fun readCommand(input: String): Command {
    val parts = input.split(" ")
    return when {
        input.startsWith("add ") && parts.size == 4 -> AddCommand(Person(parts[1]), parts[2], parts[3])
        input.startsWith("show ") && parts.size == 2 -> ShowCommand(parts[1])
        input == "help" -> HelpCommand
        input == "exit" -> ExitCommand
        else -> throw IllegalArgumentException("Unknown command")
    }
}

fun main() {
    val people = mutableMapOf<String, Person>()

    while (true) {
        println("Enter command ('help' for list of commands):")
        val command = readLine() ?: ""
        try {
            val cmd = readCommand(command)
            when (cmd) {
                is ExitCommand -> break
                is HelpCommand -> {
                    println("Available commands:")
                    println("1) exit - Exit the program")
                    println("2) help - Show available commands")
                    println("3) add <Name> phone <PhoneNumber> - Add phone number for a person")
                    println("4) add <Name> email <Email> - Add email for a person")
                    println("5) show <Name> - Show all contacts for a person")
                }
                is AddCommand -> {
                    if (cmd.isValid()) {
                        val person = people.getOrPut(cmd.person.name) { cmd.person }
                        if (cmd.type == "phone") person.phone = cmd.value
                        if (cmd.type == "email") person.email = cmd.value
                        println("Added ${cmd.type} for ${cmd.person.name}: ${cmd.value}")
                    } else {
                        println("Invalid data provided for add command")
                    }
                }
                is ShowCommand -> {
                    people[cmd.name]?.let {
                        println("Contacts for ${it.name}: Phone: ${it.phone}, Email: ${it.email}")
                    } ?: println("Not initialized")
                }
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}
