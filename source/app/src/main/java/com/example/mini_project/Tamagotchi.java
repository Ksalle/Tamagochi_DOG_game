package com.example.mini_project;

public class Tamagotchi {
    private int hunger;
    private int tiredness;
    private int boredom;
    private int happiness;
    private boolean alive;
    private long startTime;
    private long bestTime;

    public Tamagotchi() {
        reset();
        bestTime = 0;
    }

    public void update() {
        if (!alive) return;

        hunger = Math.min(100, hunger + 2);
        tiredness = Math.min(100, tiredness + 1);
        boredom = Math.min(100, boredom + 1);
        happiness = Math.max(0, happiness - 1);

        if (hunger >= 100 || tiredness >= 100 || boredom >= 100 || happiness <= 0) {
            alive = false;
        }
    }

    public void feed() {
        if (!alive) return;
        hunger = Math.max(0, hunger - 20);
        happiness = Math.min(100, happiness + 10);
    }

    public void sleep() {
        if (!alive) return;
        tiredness = Math.max(0, tiredness - 30);
        happiness = Math.min(100, happiness + 5);
    }

    public void play() {
        if (!alive) return;
        boredom = Math.max(0, boredom - 20);
        happiness = Math.min(100, happiness + 15);
        tiredness = Math.min(100, tiredness + 5);
    }

    public void reset() {
        hunger = 50;
        tiredness = 50;
        boredom = 50;
        happiness = 50;
        alive = true;
        startTime = System.currentTimeMillis();
    }

    public int getHunger() {
        return hunger;
    }

    public int getTiredness() {
        return tiredness;
    }

    public int getBoredom() {
        return boredom;
    }

    public int getHappiness() {
        return happiness;
    }

    public boolean isAlive() {
        return alive;
    }

    public long getTimeAlive() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setBestTime(long time) {
        bestTime = time;
    }
}