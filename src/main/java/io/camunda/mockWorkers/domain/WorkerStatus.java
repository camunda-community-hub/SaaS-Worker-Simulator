package io.camunda.mockWorkers.domain;

public class WorkerStatus {
    private boolean stopped;
    private boolean onError;
    private int workDurationInSec;
    private String variables;

    public WorkerStatus(boolean stopped, boolean onError, int workDurationInSec, String variables) {
        this.stopped = stopped;
        this.onError = onError;
        this.workDurationInSec = workDurationInSec;
        this.variables = variables;
    }
}
