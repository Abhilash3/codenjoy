package com.codenjoy.dojo.services.lock;

import com.codenjoy.dojo.services.Joystick;

import java.util.concurrent.locks.ReadWriteLock;

public class LockedJoystick implements Joystick {

    private ReadWriteLock lock;
    private Joystick joystick;

    public LockedJoystick(ReadWriteLock lock) {
        this.lock = lock;
    }

    public Joystick wrap(Joystick joystick) {
        this.joystick = joystick;
        return this;
    }

    @Override
    public void down() {
        lock.writeLock().lock();
        try {
            joystick.down();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void up() {
        lock.writeLock().lock();
        try {
            joystick.up();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void left() {
        lock.writeLock().lock();
        try {
            joystick.left();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void right() {
        lock.writeLock().lock();
        try {
            joystick.right();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void act(int... p) {
        lock.writeLock().lock();
        try {
            joystick.act(p);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
