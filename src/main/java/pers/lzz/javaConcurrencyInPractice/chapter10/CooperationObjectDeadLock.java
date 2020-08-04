package pers.lzz.javaConcurrencyInPractice.chapter10;

import net.jcip.annotations.GuardedBy;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 协作对象之间发生的死锁
 * <br>模拟出租车调度系统演示协作对象死锁
 *
 * @Author lzz
 */
public class CooperationObjectDeadLock {

}

/**
 * 出租车对象
 */
class Taxi {
    /**
     * 位置 ， 目的地
     */
    @GuardedBy("this")
    private Point location, destination;
    private final Dispatcher dispatcher;

    public Taxi(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public synchronized Point getLocation() {
        return location;
    }

    //设置位置会获取dispatcher锁(到达目的地需要在可用车队添加taxi信息)
    public synchronized void setLocation(Point location) {
        this.location = location;
        if (location.equals(destination)) {
            dispatcher.notifyAvailable(this);
        }
    }


}

class Point {

}

/**
 * 出租车车队
 */
class Dispatcher {

    @GuardedBy("this")
    private final Set<Taxi> taxis;
    @GuardedBy("this")
    private final Set<Taxi> availableTaxis;

    public Dispatcher() {
        taxis = new HashSet<>();
        availableTaxis = new HashSet<>();
    }

    public Dispatcher(Set<Taxi> taxis, Set<Taxi> availableTaxis) {
        this.taxis = taxis;
        this.availableTaxis = availableTaxis;
    }

    public synchronized void notifyAvailable(Taxi taxi) {
        availableTaxis.add(taxi);
    }

    //获取图像需要遍历taxis逐个获取Taxi锁(获取位置加锁)
    public synchronized Image getImage() {
        Image image = new Image();
        for (Taxi t : taxis) {
            image.drawMarker(t.getLocation());
        }
        return image;
    }
}

class Image {

    public void drawMarker(Point location) {
        System.out.println("draw marker ");
    }
}