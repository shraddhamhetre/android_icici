package com.youtility.intelliwiz20.Model;

public class StepCount {
    private long stepCountTimestamp;
    private long steps;
    private long totalSteps;
    private String stepsTaken;

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public long getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(long totalSteps) {
        this.totalSteps = totalSteps;
    }

    public String getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(String stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public long getStepCountTimestamp() {
        return stepCountTimestamp;
    }

    public void setStepCountTimestamp(long stepCountTimestamp) {
        this.stepCountTimestamp = stepCountTimestamp;
    }


}
