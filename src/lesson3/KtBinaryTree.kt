package lesson3

import java.util.SortedSet
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
            root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    override fun remove(element: T): Boolean {

        // O(log(N))
        // O(log(N))

        if (find(element) == null)
            return false
        var current = root ?: return false
        var parent = root ?: return false
        var isLeft = true
        while (current.value != element) {
            parent = current
            if (element < current.value) {              //go left?
                isLeft = true
                current = current.left ?: return false
            } else {                                    //go right?
                isLeft = false
                current = current.right ?: return false
            }
        }

        if (current.left == null && current.right == null) {
            when {
                current == root -> root = null
                isLeft -> parent.left = null
                else -> parent.right = null
            }
        } else if (current.right == null) {
            when {
                current == root -> root = current.left ?: return false
                isLeft -> parent.left = current.left ?: return false
                else -> parent.right = current.left ?: return false
            }
        } else if (current.left == null) {
            when {
                current == root -> root = current.right ?: return false
                isLeft -> parent.left = current.left ?: return false
                else -> parent.right = current.right ?: return false
            }
        } else {
            val successor = getSuccessor(current)
            when {
                current == root -> root = successor
                isLeft -> parent.left = successor
                else -> parent.right = successor
            }
        }

        return true
    }

    private fun getSuccessor(deletedNode: Node<T>): Node<T> {
        var succParent = deletedNode
        var succ = deletedNode
        var current = deletedNode.right
        while (current != null) {
            succParent = succ
            succ = current
            current = current.left
        }
        if (succ != deletedNode.right) {
            succParent.left = succ.right
            succ.right = deletedNode.right
        }
        return succ
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
            root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator : MutableIterator<T> {

        private var current: Node<T>? = null

        /**
         * Поиск следующего элемента
         * Средняя
         */

        /**
         * Данную задачу я позаимствовал у одногруппника, а не делал сам, для того, чтобы была
         * возможность осуществитьь проверку других заданий
         */

        private fun findNext(): Node<T>? {
            if (size == 0) return null
            val currentNode = current ?: return find(first())
            if (currentNode.value == last()) return null
            if (currentNode.right != null) {
                var successor = currentNode.right ?: throw IllegalArgumentException()
                while (successor.left != null) {
                    successor = successor.left ?: return successor
                }
                return successor
            } else {
                var successor = root ?: throw IllegalArgumentException()
                var ancestor = root ?: throw IllegalArgumentException()
                while (ancestor != currentNode) {
                    if (currentNode.value < ancestor.value) {
                        successor = ancestor
                        ancestor = ancestor.left ?: return null
                    } else ancestor = ancestor.right ?: return null
                }
                return successor
            }
        }

        override fun hasNext(): Boolean = findNext() != null

        override fun next(): T {
            current = findNext()
            return (current ?: throw NoSuchElementException()).value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        override fun remove() {

            // O(log(N))
            // O(log(N))

            val curr = current
            current = findNext()
            remove(curr?.value)
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
