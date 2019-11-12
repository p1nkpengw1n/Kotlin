package flashcards

import java.io.File
import java.io.FileNotFoundException
import java.util.*

val logList = mutableListOf<String>()

fun main() {
    val scanner = Scanner(System.`in`)
    val deck = mutableMapOf<String?, String?>()
    val mistakeCounter = mutableMapOf<String?, Int>()
    var exit = false
    while (!exit) {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        logList.add("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (if (scanner.hasNext()) scanner.nextLine() else null) {
            "add" -> add(deck)
            "ask" -> ask(deck, mistakeCounter)
            "remove" -> remove(deck)
            "export" -> export(deck, mistakeCounter)
            "import" -> import(deck, mistakeCounter)
            "log" -> log()
            "hardest card" -> hardestCard(mistakeCounter)
            "reset stats" -> resetStats(mistakeCounter)
            "exit" -> {
                exit = true
                println("Bye bye!")
                logList.add("Bye bye!")
            }
            else -> {
                println("Please choose from the options from above!")
                logList.add("Please choose from the options from above!")
            }
        }
    }
}

fun add(deck: MutableMap<String?, String?>) {
    logList.add("add")
    val scanner = Scanner(System.`in`)
    //println("Input the number of cards:")
    //val numOfCards: Int = scanner.nextInt() //readLine()?.toInt() ?: 0 // as a result of stage 5 not needed anymore
    //scanner.nextLine()
    println("The card:")//println("The card #${i + 1}:")
    logList.add("The card:")
    val term = if (scanner.hasNext()) scanner.nextLine().dropWhile { !it.isLetterOrDigit() } else "" //readLine()
    logList.add(term)
    for (key in deck.keys) {
        if (term == key) {
            println("The card \"$term\" already exists.")
            logList.add("The card \"$term\" already exists.")
            return;
        }
    }
    println("The definition of the card:")//println("The definition of the card #${i + 1}:")
    logList.add("The definition of the card:")
    val definition = if (scanner.hasNext()) scanner.nextLine() else "" //readLine()
    logList.add(definition)
    for (value in deck.values) {
        if (definition == value) {
            println("The definition \"$definition\" already exists.")
            logList.add("The definition \"$definition\" already exists.")
            return;
        }
    }
    deck[term] = definition
    println("The pair (\"$term\":\"$definition\") has been added.")
    logList.add("The pair (\"$term\":\"$definition\") has been added.")
}

fun remove(deck: MutableMap<String?, String?>) {
    logList.add("remove")
    val scanner = Scanner(System.`in`)
    println("The card:")
    logList.add("The card:")
    val termToRemove = if (scanner.hasNext()) scanner.nextLine() else ""
    logList.add(termToRemove)
    for (term in deck.keys) {
        if (term == termToRemove) {
            deck.remove(term)
            println("The card has been removed.")
            logList.add("The card has been removed.")
            return
        }
    }
    println("Can't remove \"$termToRemove\": there is no such card.")
    logList.add("Can't remove \"$termToRemove\": there is no such card.")
}

fun ask(deck: MutableMap<String?, String?>, mistakeCounter: MutableMap<String?, Int>) {
//TO-DO: implement random card picking
    logList.add("ask")
    val scanner = Scanner(System.`in`)
    println("How many times to ask?")
    logList.add("How many times to ask?")
    var times = if (scanner.hasNextInt()) scanner.nextInt() else 0 //readLine()
    scanner.nextLine()
    logList.add(times.toString())
    while (times > 0) {
        for ((k, _) in deck) {
            if (times > 0) {
                println("Print the definition of \"${k}\":")
                logList.add("Print the definition of \"${k}\":")
                val answer = if (scanner.hasNext()) scanner.nextLine() else "" //readLine()
                logList.add(answer)
                if (answer == deck[k]) {
                    println("Correct answer.")
                    logList.add("Correct answer.")
                } else if (deck.containsValue(answer)) {
                    if (!mistakeCounter.containsKey(k)) {
                        mistakeCounter.put(k, 1)
                    } else {
                        mistakeCounter.put(k, mistakeCounter[k]!! + 1)
                    }
                    for ((key, _) in deck) {
                        if (answer == deck[key]) {
                            println("Wrong answer. The correct one is \"${deck[k]}\", you've just written the definition of \"$key\"." +
                                    "The asked card was \"$k\", the answer was \"${deck[key]}\".")
                            logList.add("Wrong answer. The correct one is \"${deck[k]}\", you've just written the definition of \"$key\"." +
                                    "The asked card was \"$k\", the answer was \"${deck[key]}\".")
                            break
                        }
                    }
                } else {
                    if (!mistakeCounter.containsKey(k)) {
                        mistakeCounter.put(k, 1)
                    } else {
                        mistakeCounter.put(k, mistakeCounter[k]!! + 1)
                    }
                    println("Wrong answer. The correct one is \"${deck[k]}\".")
                    logList.add("Wrong answer. The correct one is \"${deck[k]}\".")
                }
                times--
            } else {
                break
            }
        }
    }
}

fun export(deck: MutableMap<String?, String?>, mistakeCounter: MutableMap<String?, Int>) {
    logList.add("export")
    val sb = StringBuilder()
    for ((k, v) in deck) {
        sb.append(k)
        sb.append("=")
        sb.append(v)
        if (mistakeCounter.containsKey(k)) {
            sb.append("/")
            sb.append(mistakeCounter[k])
        } else {
            sb.append(0)
        }
        sb.append(";")
    }
    val scanner = Scanner(System.`in`)
    println("File name:")
    logList.add("File name:")
    val fileName = if (scanner.hasNext()) scanner.nextLine() else "generatedOutput.txt"
    logList.add(fileName)
    File(fileName).writeText(sb.toString())
    println("${deck.size} cards have been saved.")
    logList.add("${deck.size} cards have been saved.")
}

fun import(deck: MutableMap<String?, String?>, mistakeCounter: MutableMap<String?, Int>) {
    logList.add("import")
    val scanner = Scanner(System.`in`)
    println("File name:")
    logList.add("File name:")
    val fileName = if (scanner.hasNext()) scanner.nextLine() else ""
    logList.add(fileName)
    try {
        parse(File(fileName).readText(), deck, mistakeCounter)
    } catch (fnfe: FileNotFoundException) {
        println("File not found.")
        logList.add("File not found.")
    } catch (e: Exception) {
        println("Unknown error.")
        logList.add("Unknown error.")
    }
}

private fun parse(container: String, deck: MutableMap<String?, String?>, mistakeCounter: MutableMap<String?, Int>) {
    val loadedDeck: List<String> = container.split("=", "/", ";").map { s -> s.dropWhile { !it.isLetterOrDigit() } }
    for (i in 0 until loadedDeck.size step 3) {
        val term = loadedDeck.get(i)
        val definition = loadedDeck.get(i + 1)
        val mistakeCount = Integer.parseInt(loadedDeck.get(i + 2))
        if (deck.containsKey(term)) {
            deck[term] = definition
        }
        else {
            deck.put(loadedDeck.get(i), definition)
        }
        if(mistakeCounter.containsKey(term)) {
            mistakeCounter[term] = mistakeCount
        }
        else {
            mistakeCounter.put(term,mistakeCount)
        }
    }
    println("${loadedDeck.size / 2} cards have been loaded")
    logList.add("${loadedDeck.size / 2} cards have been loaded")
}

fun log() {
    logList.add("log")
    val scanner = Scanner(System.`in`)
    println("File name:")
    logList.add("File name:")
    val fileName = if (scanner.hasNext()) scanner.nextLine() else "generatedOutput.txt"
    logList.add(fileName)
    File(fileName).writeText(logList.toString())
    println("The log has been saved.")
    logList.add("The log has been saved.")
}

fun hardestCard(mistakeCounter: MutableMap<String?, Int>) {
    logList.add("hardest card")
    if (mistakeCounter.isEmpty()) {
        println("There are no cards with errors..")
        logList.add("There are no cards with errors..")
        return
    }
    var biggestValue = 0
    val listOfHardestCards = mutableListOf<String?>()
    for ((_, v) in mistakeCounter) {
        if (v > biggestValue) {
            biggestValue = v
        }
    }
    for ((k, v) in mistakeCounter) {
        if (v == biggestValue) {
            listOfHardestCards.add(k)
        }
    }
    if (listOfHardestCards.size > 1) {
        print("The hardest cards are")
        logList.add("The hardest cards are")
        for (i in 0 until listOfHardestCards.size - 1) {
            print(" \"${listOfHardestCards[i]}\", ")
            logList.add(" \"${listOfHardestCards[i]}\", ")
        }
        print("\"${listOfHardestCards[listOfHardestCards.size - 1]}\". ")
        println("You have $biggestValue errors answering them.")
        logList.add("You have $biggestValue errors answering them.")
    } else {
        println("The hardest card is \"${listOfHardestCards[0]}\". You have $biggestValue errors answering it.")
        logList.add("The hardest card is \"${listOfHardestCards[0]}\". You have $biggestValue errors answering it.")
    }
    return
}

fun resetStats(mistakeCounter: MutableMap<String?, Int>) {
    logList.add("reset stats")
    println("Card statistics has been reset.")
    logList.add("Card statistics has been reset.")
    mistakeCounter.clear()
}