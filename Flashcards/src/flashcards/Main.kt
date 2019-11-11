package flashcards

import java.io.File
import java.io.FileNotFoundException
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val deck = mutableMapOf<String?, String?>()
    var exit = false
    while (!exit) {
        println("\nInput the action (add, remove, import, export, ask, exit):")
        when (if (scanner.hasNext()) scanner.nextLine() else null) {
            "add" -> add(deck)
            "ask" -> ask(deck)
            "remove" -> remove(deck)
            "export" -> export(deck)
            "import" -> import(deck)
            "exit" -> {
                exit = true
                println("Bye bye!")
            }
            else -> {
                println("Please choose from the options from above!")
            }
        }
    }
}

fun add(deck: MutableMap<String?, String?>) {
    val scanner = Scanner(System.`in`)
    //println("Input the number of cards:")
    //val numOfCards: Int = scanner.nextInt() //readLine()?.toInt() ?: 0 // as a result of stage 5 not needed anymore
    //scanner.nextLine()
    var ok = false
    println("The card:")//println("The card #${i + 1}:")
    var term = if (scanner.hasNext()) scanner.nextLine() else null //readLine()
    for (key in deck.keys) {
        if (term == key) {
            println("The card \"$term\" already exists.")
            return;
        }
    }
    println("The definition of the card:")//println("The definition of the card #${i + 1}:")
    var definition = if (scanner.hasNext()) scanner.nextLine() else null //readLine()
    for (value in deck.values) {
        if (definition == value) {
            println("The definition \"$definition\" already exists.")
            return;
        }
    }
    deck[term] = definition
    println("The pair (\"$term\":\"$definition\") has been added.")
}

fun remove(deck: MutableMap<String?, String?>) {
    val scanner = Scanner(System.`in`)
    println("The card:")
    val termToRemove = if (scanner.hasNext()) scanner.nextLine() else null
    for (term in deck.keys) {
        if (term == termToRemove) {
            deck.remove(term)
            println("The card has been removed.")
            return
        }
    }
    println("Can't remove \"$termToRemove\": there is no such card.")
}

fun ask(deck: MutableMap<String?, String?>) {
    val scanner = Scanner(System.`in`)
    println("How many times to ask?")
    var times = if (scanner.hasNextInt()) scanner.nextInt() else 0 //readLine()
    scanner.nextLine()
    for ((k, _) in deck) {
        while (times > 0) {
            println("Print the definition of \"${k}\":")
            val answer = if (scanner.hasNext()) scanner.nextLine() else null //readLine()
            if (answer == deck[k]) {
                println("Correct answer.")
            } else if (deck.containsValue(answer)) {
                for ((key, _) in deck) {
                    if (answer == deck[key]) {
                        println("Wrong answer. The correct one is \"${deck[k]}\", you've just written the definition of \"$key\"." +
                                "The asked card was \"$k\", the answer was \"${deck[key]}\".")
                        break
                    }
                }
            } else {
                println("Wrong answer. The correct one is \"${deck[k]}\".")
            }
            times--
        }
    }
}

fun export(deck: MutableMap<String?, String?>) {
    val scanner = Scanner(System.`in`)
    println("File name:")
    val fileName = if (scanner.hasNext()) scanner.nextLine() else "generatedOutput.txt"
    File(fileName).writeText(deck.toString())
    println("${deck.size} cards have been saved.")
}

fun import(deck: MutableMap<String?, String?>) {
    val scanner = Scanner(System.`in`)
    println("File name:")
    val fileName = if (scanner.hasNext()) scanner.nextLine() else ""
    try {
        parse(File(fileName).readText(), deck)
    } catch (fnfe: FileNotFoundException) {
        println("File not found.")
    } catch (e: Exception) {
        println("Unknown error.")
    }
}

fun parse(container: String, deck: MutableMap<String?, String?>) {
    val loadedDeck: List<String> = container.split(",", "=", "{", "}").filter { e -> e != "" }.map { s -> s.dropWhile { !it.isLetterOrDigit() } }
    for (i in 0 until loadedDeck.size) {
        if (i % 2 == 0) {
            if (deck.containsKey(loadedDeck.get(i))) {
                deck[loadedDeck.get(i)] = loadedDeck.get(i + 1)
            } else {
                deck.put(loadedDeck.get(i), loadedDeck.get(i + 1))
            }
        }
    }
    println("${loadedDeck.size/2} cards have been loaded")
}