data class Day07File(val filename: String, val size: Long)

data class Day07Directory(
    val dirname: String,
    val parent: Day07Directory?,
    var files: MutableList<Day07File>,
    var subdirMap: HashMap<String, Day07Directory>
) {
    fun addDirectory(name: String) {
        subdirMap[name] = fromName(name, this)
    }

    fun addFileWithSize(name: String, size: Long) {
        files.add(Day07File(name, size))
    }

    fun subdir(name: String): Day07Directory {
        return subdirMap[name]!!
    }

    fun totalSize(): Long {
        return files.sumOf { it.size } + subdirMap.values.sumOf { it.totalSize() }
    }

    fun allSubDirs(): List<Day07Directory> {
        val list = arrayListOf<Day07Directory>()

        subdirMap.values.forEach {
            list.add(it)
            list.addAll(it.allSubDirs())
        }

        return list
    }

    override fun toString(): String {
        return "Day07Directory(name: $dirname, parent name: " + (parent?.dirname
            ?: "none") + ", file count " + files.size + ", subdir count " + subdirMap.size + ")"
    }

    companion object {
        fun fromName(name: String, parent: Day07Directory?): Day07Directory {
            return Day07Directory(name, parent, mutableListOf(), hashMapOf())
        }
    }
}

fun main() {
    fun parseCommand(command: String, rootDir: Day07Directory, cwd: Day07Directory): Day07Directory {
        val cmdParts = command.split(" ")

        if (cmdParts[0] == "$") {
            if (cmdParts[1] == "cd") {
                return when (cmdParts[2]) {
                    ".." -> {
                        cwd.parent!!
                    }

                    "/" -> {
                        rootDir
                    }

                    else -> {
                        cwd.subdir(cmdParts[2])
                    }
                }
            }
        } else {
            if (cmdParts[0] == "dir") {
                cwd.addDirectory(cmdParts[1])
            } else {
                cwd.addFileWithSize(cmdParts[1], cmdParts[0].toLong())
            }
        }

        return cwd
    }

    fun processCommands(rootDir: Day07Directory, input: List<String>) {
        var workingDir = rootDir
        input.forEach { workingDir = parseCommand(it, rootDir, workingDir) }
    }

    fun part1(input: List<String>): Long {
        val rootDir = Day07Directory.fromName("/", null)
        processCommands(rootDir, input)

        return rootDir.allSubDirs().filter { it.totalSize() <= 100000 }.sumOf { it.totalSize() }
    }

    fun part2(input: List<String>): Long {
        val rootDir = Day07Directory.fromName("/", null)
        processCommands(rootDir, input)

        val spaceUsed = 70000000 - rootDir.totalSize()
        val spaceToFreeUp = 30000000 - spaceUsed

        return rootDir.allSubDirs().filter { it.totalSize() >= spaceToFreeUp }.minOf { it.totalSize() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437.toLong())
    check(part2(testInput) == 24933642.toLong())

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
