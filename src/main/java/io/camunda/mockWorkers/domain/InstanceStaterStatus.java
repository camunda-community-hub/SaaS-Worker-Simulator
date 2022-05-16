package io.camunda.mockWorkers.domain;

public class InstanceStaterStatus {
    private boolean running;
    private int frequencyInSec;
    private String variables;

    public InstanceStaterStatus(boolean running, int frequencyInSec, String variables) {
        this.running = running;
        this.frequencyInSec = frequencyInSec;
        this.variables = variables;
    }
}
