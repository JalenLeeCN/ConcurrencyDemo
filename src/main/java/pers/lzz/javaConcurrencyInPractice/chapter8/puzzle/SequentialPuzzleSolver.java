package pers.lzz.javaConcurrencyInPractice.chapter8.puzzle;

import net.jcip.annotations.Immutable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 谜题框架的串行解决方案,在谜题空间中执行一个深度优先搜索,当找到解答方案之后结束搜索.
 * <br> 不一定是最短解决方案
 * @Author lzz
 */
public class SequentialPuzzleSolver<P,M> {
    private final Puzzle<P,M> puzzle;
    private final Set<P> seen = new HashSet<>();

    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        return search(new Node<P, M>(pos, null, null));
    }

    /**
     * 递归查找目标节点并存储已遍历的节点(存储节点链表)
     * @param node
     * @return
     */
    private List<M> search(Node<P, M> node) {
        if (!seen.contains(node.pos)) {
            seen.add(node.pos);
            if(puzzle.isGoal(node.pos))
                return node.asMoveList();
            for (M move : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, move);
                Node<P, M> child = new Node<P, M>(pos, move, node);
                List<M> result = search(child);
                if(result != null) return result;
            }
        }
        return null;
    }

    /**
     * 用于谜题解决框架的链表节点
     * <br> Node代表通过一系列的移动到达的一个位置,其中保存了到达该位置的移动以及前一个Node.
     * 只要沿着Node链接逐步回溯,就可以重新构建出到达当前位置的移动序列.
     * @param <P>
     * @param <M>
     */
    @Immutable
    static class Node<P, M> {
        final P pos;
        final M move;
        final Node<P,M> prev;

        Node(P pos, M move, Node<P, M> prev) {
            this.pos = pos;
            this.move = move;
            this.prev = prev;
        }

        List<M> asMoveList() {
            List<M> solution = new LinkedList<M>();
            for(Node<P,M> n = this;n.move !=null;n = n.prev)
                solution.add(0,n.move);
            return solution;
        }

    }
}