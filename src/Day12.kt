import kotlin.math.absoluteValue

// first time doing pathfinding, this was a lifesaver:
// https://www.atomiccommits.io/dijkstras-algorithm-in-kotlin
data class Graph<T>(
    val vertices: Set<T>,
    val edges: Map<T, Set<T>>,
    val weights: Map<Pair<T, T>, Int>
)

fun <T> dijkstra(graph: Graph<T>, start: T): Map<T, T?> {
    val S: MutableSet<T> = mutableSetOf() // a subset of vertices, for which we know the true distance

    val delta = graph.vertices.associateWith { Int.MAX_VALUE }.toMutableMap()
    delta[start] = 0

    val previous: MutableMap<T, T?> = graph.vertices.associateWith { null }.toMutableMap()

    while (S != graph.vertices) {
        val v: T = delta
            .filter { !S.contains(it.key) }
            .minBy { it.value }
            .key

        graph.edges.getValue(v).minus(S).forEach { neighbor ->
            val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

            if (newPath < delta.getValue(neighbor)) {
                delta[neighbor] = newPath
                previous[neighbor] = v
            }
        }

        S.add(v)
    }

    return previous.toMap()
}

fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
    fun pathTo(start: T, end: T): List<T> {
        if (shortestPathTree[end] == null) return listOf(end)
        return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
    }

    return pathTo(start, end)
}

data class Coordinate(val row: Int, val col: Int) {
    fun distanceFrom(other: Coordinate) = (row - other.row).absoluteValue + (col - other.col).absoluteValue
}

class Node(private val position: Coordinate, private val display: Char) {
    var rels = listOf<Node>()

    val height: Char

    val isStart: Boolean
    val isEnd: Boolean

    init {
        when (display) {
            'S' -> {
                this.isStart = true
                this.isEnd = false
                this.height = 'a'
            }

            'E' -> {
                this.isStart = false
                this.isEnd = true
                this.height = 'z'
            }

            else -> {
                this.height = display
                this.isStart = false
                this.isEnd = false
            }
        }
    }

    fun createRels(allNodes: List<Node>) {
        // look in each direction, if letter is greater than or equal to our letter add a connection
        this.rels = allNodes.filter { it.position.distanceFrom(this.position) == 1 }
            .filter { (it.height - this.height) <= 1 }
            .toList()
    }

    override fun toString(): String {
        return "<(" + position.row + "," + position.col + ") [" + display + "]>"
    }
}

fun main() {
    fun createNodes(input: List<String>): List<Node> {
        // first build a 2d list of chars
        val rows = input.map { it.toList() }

        val nodes = mutableListOf<Node>()

        for (i in rows.indices) {
            for (j in rows[i].indices) {
                nodes.add(Node(Coordinate(i, j), rows[i][j]))
            }
        }

        nodes.forEach { it.createRels(nodes) }

        return nodes
    }


    fun setupDijkstra(nodes: List<Node>): Pair<Map<Pair<Node, Node>, Int>, Map<Node, Set<Node>>> {
        val weights = nodes.flatMap { a -> a.rels.map { b -> Pair(a, b) } }.distinct().associateWith { 1 }
        val edges = nodes.associateWith { it.rels.toSet() }
        return Pair(weights, edges)
    }

    fun part1(input: List<String>): Int {
        val nodes = createNodes(input)

        val start = nodes.first { it.isStart }
        val end = nodes.first { it.isEnd }

        val (weights, edges) = setupDijkstra(nodes)

        val shortestPathTree = dijkstra(Graph(nodes.toSet(), edges, weights), start)
        return shortestPath(shortestPathTree, start, end).size - 1
    }

    fun part2(input: List<String>): Int {
        val nodes = createNodes(input)

        val (weights, edges) = setupDijkstra(nodes)
        val end = nodes.first { it.isEnd }

        // takes forever to run as there are almost 1000 "a" elevations in
        // my test data, but it does work...
        return nodes.filter { it.height == 'a' }.minOfOrNull {
            val shortestPathTree = dijkstra(Graph(nodes.toSet(), edges, weights), it)
            shortestPath(shortestPathTree, it, end).size - 1
        } ?: 0
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    part1(input).println() // 412
    part2(input).println() // 402
}
