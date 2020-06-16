package pers.lzz.javaConcurrencyInPractice.chapter8.puzzle;

import java.util.Set;

/**
 * 表示"搬箱子" 之类谜题的抽象类
 * @param <P> 位置类
 * @param <M> 移动类
 */
public interface Puzzle<P, M> {

    /**
     * 初始化根节点
     * @return
     */
    P initialPosition();

    /**
     * 是否是目标节点
     * @param position
     * @return
     */
    boolean isGoal(P position);

    /**
     * 合法移动
     * @param position
     * @return 移动类集合
     */
    Set<M> legalMoves(P position);

    /**
     * 移动方法
     * @param position 当前位置
     * @param move 移动动作??
     * @return 移动后的位置?
     */
    P move(P position, M move);

}
