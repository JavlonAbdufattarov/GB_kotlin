fun main() {
    val contacts = mutableMapOf<String, MutableList<String>>()

    while (true) {
        println("Enter command ('help' for list of commands):")
        val command = readLine() ?: ""

        when {
            command == "exit" -> break
            command == "help" -> {
                println("Available commands:")
                println("1) exit - Exit the program")
                println("2) help - Show available commands")
                println("3) add <Name> phone <PhoneNumber> - Add phone number for a person")
                println("4) add <Name> email <Email> - Add email for a person")
                println("5) show <Name> - Show all contacts for a person")
            }
            command.startsWith("add ") -> {
                val parts = command.split(" ")
                if (parts.size == 4) {
                    val name = parts[1]
                    val type = parts[2] // "phone" or "email"
                    val value = parts[3]

                    if (type == "phone" && !value.startsWith("+")) {
                        println("Error: Phone number must start with '+'.")
                    } else {
                        // Adding the contact information to the map
                        contacts.getOrPut(name) { mutableListOf() }.add("$type: $value")
                        println("Added $type for $name: $value")
                    }
                } else {
                    println("Invalid format for add command")
                }
            }
            command.startsWith("show ") -> {
                val name = command.substringAfter("show ")
                if (name in contacts) {
                    println("Contacts for $name:")
                    contacts[name]?.forEach { println(it) }
                } else {
                    println("No contacts found for $name")
                }
            }
            else -> println("Unknown command")
        }
    }
}
