import java.io.File

const val WIN_SCORE = 6
const val DRAW_SCORE = 3
const val LOSS_SCORE = 0

const val ROCK_SCORE = 1
const val PAPER_SCORE = 2
const val SCISSORS_SCORE = 3

const val ROCK_MOVE = "X"
const val PAPER_MOVE = "Y"
const val SCISSORS_MOVE = "Z"

const val OPP_ROCK = "A"
const val OPP_PAPER = "B"

const val RESULT_WIN = "Z"
const val RESULT_DRAW = "Y"
const val RESULT_LOSS = "X"

fun main() {
    fun resultOfRound(moves: List<String>): Int {
        if (moves[0] == OPP_ROCK) {
            // opponent plays rock, we win if we play paper, lose if we play scissors
            if (moves[1] == ROCK_MOVE) return DRAW_SCORE
            if (moves[1] == PAPER_MOVE) return WIN_SCORE
            return LOSS_SCORE
        } else if (moves[0] == OPP_PAPER) {
            // opponent plays paper, we win if we play scissors, lose if we play rock
            if (moves[1] == ROCK_MOVE) return LOSS_SCORE
            if (moves[1] == PAPER_MOVE) return DRAW_SCORE
            return WIN_SCORE
        } else {
            // opponent plays scissors, we win if we play rock, lose if we play paper
            if (moves[1] == ROCK_MOVE) return WIN_SCORE
            if (moves[1] == PAPER_MOVE) return LOSS_SCORE
            return DRAW_SCORE
        }
    }

    fun scoreForShape(shape: String): Int {
        if (shape == ROCK_MOVE) return ROCK_SCORE
        if (shape == PAPER_MOVE) return PAPER_SCORE
        return SCISSORS_SCORE
    }

    fun scoreForRoundPart1(line: String): Int {
        val moves = line.split(" ")

        return resultOfRound(moves) + scoreForShape(moves[1])
    }

    fun getMove(opponentMove: String, requiredResult: String): String {
        when (opponentMove) {
            OPP_ROCK -> {
                // opponent plays rock, we win if we play paper, lose if we play scissors
                if (requiredResult == RESULT_WIN) return PAPER_MOVE
                if (requiredResult == RESULT_LOSS) return SCISSORS_MOVE
                return ROCK_MOVE
            }

            OPP_PAPER -> {
                // opponent plays paper, we win if we play scissors, lose if we play rock
                if (requiredResult == RESULT_WIN) return SCISSORS_MOVE
                if (requiredResult == RESULT_LOSS) return ROCK_MOVE
                return PAPER_MOVE
            }

            else -> {
                // opponent plays scissors, we win if we play rock, lose if we play paper
                if (requiredResult == RESULT_WIN) return ROCK_MOVE
                if (requiredResult == RESULT_LOSS) return PAPER_MOVE
                return SCISSORS_MOVE
            }
        }
    }

    fun scoreForResult(requiredResult: String): Int {
        if (requiredResult == RESULT_DRAW) return DRAW_SCORE
        if (requiredResult == RESULT_LOSS) return LOSS_SCORE
        return WIN_SCORE
    }

    fun scoreForRoundPart2(line: String): Int {
        val moves = line.split(" ")

        val opponentMove = moves[0]
        val requiredResult = moves[1]

        val requiredShape = getMove(opponentMove, requiredResult)

        return scoreForResult(requiredResult) + scoreForShape(requiredShape)
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { scoreForRoundPart1(it) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { scoreForRoundPart2(it) }
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
