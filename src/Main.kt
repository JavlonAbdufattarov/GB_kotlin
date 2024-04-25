import java.util.regex.Pattern

sealed class Command {
    abstract fun isValid(): Boolean
}

data class AddPhoneCommand(val name: String, val phone: String) : Command() {
    override fun isValid(): Boolean = Pattern.matches("\\+\\d+", phone)
}

data class AddEmailCommand(val name: String, val email: String) : Command() {
    override fun isValid(): Boolean = Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", email)
}

data class ShowCommand(val name: String) : Command() {
    override fun isValid(): Boolean = true
}

data class FindCommand(val contact: String) : Command() {
    override fun isValid(): Boolean = true  // Validation could be refined if needed
}

object HelpCommand : Command() {
    override fun isValid(): Boolean = true
}

object ExitCommand : Command() {
    override fun isValid(): Boolean = true
}

data class Person(val name: String, val phones: MutableList<String> = mutableListOf(), val emails: MutableList<String> = mutableListOf())

fun readCommand(input: String): Command {
    val parts = input.trim().split("\\s+".toRegex())
    return when {
        input.startsWith("add ") && parts.size == 4 -> {
            val name = parts[1]
            when (parts[2]) {
                "phone" -> AddPhoneCommand(name, parts[3])
                "email" -> AddEmailCommand(name, parts[3])
                else -> throw IllegalArgumentException("Invalid type for add command")
            }
        }
        input.startsWith("show ") && parts.size == 2 -> ShowCommand(parts[1])
        input.startsWith("find ") && parts.size == 2 -> FindCommand(parts[1])
        input == "help" -> HelpCommand
        input == "exit" -> ExitCommand
        else -> throw IllegalArgumentException("Unknown command")
    }
}

fun main() {
    val people = mutableMapOf<String, Person>()

    while (true) {
        println("Enter command ('help' for list of commands):")
        val command = readLine() ?: continue
        try {
            val cmd = readCommand(command)
            if (!cmd.isValid()) {
                println("Invalid command or arguments.")
                continue
            }
            when (cmd) {
                is ExitCommand -> break
                is HelpCommand -> {
                    println("Available commands:")
                    println("1) exit - Exit the program")
                    println("2) help - Show available commands")
                    println("3) add <Name> phone <PhoneNumber> - Add phone number for a person")
                    println("4) add <Name> email <Email> - Add email for a person")
                    println("5) show <Name> - Show all contacts for a person")
                    println("6) find <Contact> - Find people by phone or email")
                }
                is AddPhoneCommand -> {
                    val person = people.getOrPut(cmd.name) { Person(cmd.name) }
                    person.phones.add(cmd.phone)
                    println("Added phone for ${cmd.name}: ${cmd.phone}")
                }
                is AddEmailCommand -> {
                    val person = people.getOrPut(cmd.name) { Person(cmd.name) }
                    person.emails.add(cmd.email)
                    println("Added email for ${cmd.name}: ${cmd.email}")
                }
                is ShowCommand -> {
                    people[cmd.name]?.let {
                        println("Contacts for ${it.name}: Phones: ${it.phones.joinToString()}, Emails: ${it.emails.joinToString()}")
                    } ?: println("No records found for ${cmd.name}")
                }
                is FindCommand -> {
                    val found = people.filter { it.value.phones.contains(cmd.contact) || it.value.emails.contains(cmd.contact) }
                    if (found.isNotEmpty()) {
                        found.forEach { (name, _) ->
                            println(name)
                        }
                    } else {
                        println("No entries found for ${cmd.contact}")
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}
