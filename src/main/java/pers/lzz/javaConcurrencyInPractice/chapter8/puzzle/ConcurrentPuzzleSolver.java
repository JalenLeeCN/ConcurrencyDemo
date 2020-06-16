package pers.lzz.javaConcurrencyInPractice.chapter8.puzzle;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * 计算某次移动过程在很大程度上与计算其他移动过程是相互独立的.
 * (各个任务之间会共享一些可变状态,eg: 已遍历位置的集合)
 *
 * @Author lzz
 */
public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService exec;
    private final ConcurrentMap<P, Boolean> seen;
    final ValueLatch<SequentialPuzzleSolver.Node<P, M>> solution = new ValueLatch<>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec, ConcurrentMap<P, Boolean> seen) {
        this.puzzle = puzzle;
        this.exec = exec;
        this.seen = seen;
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            //首次计算只提交初始化节点, 不存在移动方法及前置节点
            exec.execute(newTask(p, null, null));
            //阻塞直到找到解答
            SequentialPuzzleSolver.Node<P, M> solnNode = solution.getValue();
            return solnNode == null ? null : solnNode.asMoveList();
        } finally {
            exec.shutdown();
        }
    }

    private Runnable newTask(P p, M m, SequentialPuzzleSolver.Node<P, M> n) {
        return new SolverTask(p, m, n);
    }

    /**
     * 计算出下一步可能到达的位置,并去掉已经到达的位置,然后判断任务是否完成,最后将尚未搜索过的位置提交给Executor
     */
    private class SolverTask extends SequentialPuzzleSolver.Node<P, M> implements Runnable {

        public SolverTask(P p, M m, SequentialPuzzleSolver.Node<P, M> n) {
            super(p, m, n);
        }

        @Override
        public void run() {
            if (solution.isSet() || seen.putIfAbsent(pos, true) != null) return;
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (M m : puzzle.legalMoves(pos))
                    exec.execute(newTask(puzzle.move(pos, m), m, this));
            }
        }
    }
}
