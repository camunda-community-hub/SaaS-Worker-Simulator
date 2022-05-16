package io.camunda.mockWorkers.domain;

import io.camunda.mockWorkers.App;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Worker {
    private transient Cluster cluster;
    private String name;
    private String type;
    private String variables;
    private int workDurationInSec;
    private transient InternalJobHandler internalJobHandler;
    private transient JobWorker workerRegistration;

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    public Worker(Cluster cluster, String name, String type, String variables, int workDurationInSec) {
        LOG.debug("IN - Worker constructor - " + cluster.getName() + " - "+ name + " - "+ type + " - "+ variables + " - "+ workDurationInSec);
        this.cluster = cluster;
        this.name = name;
        this.type = type;
        this.variables = variables;
        this.workDurationInSec = workDurationInSec;
        this.internalJobHandler = new InternalJobHandler(variables,workDurationInSec);
        //this.createWorker();
        LOG.debug("Out - Worker constructor");
    }

    public WorkerStatus getWorkerStatus()
    {
        return new WorkerStatus((workerRegistration == null || workerRegistration.isClosed()),internalJobHandler.getError(),internalJobHandler.getWorkDurationInSec(),internalJobHandler.getVariables());
    }

    public void startWorker()
    {
        if(workerRegistration != null && workerRegistration.isOpen()) return;
        this.createWorker();
    }

    public void stopWorker() throws InterruptedException {
        if(this.workerRegistration != null) this.workerRegistration.close();
        while (!this.workerRegistration.isClosed()) {
            System.out.println("wait");
            Thread.sleep(100);
        }
    }

    public void generateError()
    {
        this.internalJobHandler.setError(true);
    }

    public void clearError()
    {
        this.internalJobHandler.setError(false);
    }

    public void setWorkDurationInSec(int workDurationInSec)
    {
        this.internalJobHandler.setWorkDurationInSec(workDurationInSec);
    }

    public void resetWorkDurationInSec()
    {
        this.internalJobHandler.setWorkDurationInSec(this.workDurationInSec);
    }

    public void setVariables(String variables)
    {
        this.internalJobHandler.setVariables(variables);
    }

    private void createWorker()
    {
        LOG.debug("In - createWorker");
        LOG.debug("Context - "+type+" - "+internalJobHandler.getStatus());
        workerRegistration =  cluster.getZeebeClient()
                .newWorker()
                .jobType(type)
                .handler(internalJobHandler)
                .timeout(Duration.ofSeconds(this.workDurationInSec+30))
                .open();
        LOG.debug("Out - createWorker");
    }
}

