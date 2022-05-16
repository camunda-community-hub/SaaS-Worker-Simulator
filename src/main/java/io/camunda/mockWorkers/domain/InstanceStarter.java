package io.camunda.mockWorkers.domain;

import io.camunda.mockWorkers.App;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceStarter implements Runnable{

    private String processId;
    private int frequencyInSec;
    private String variables;

    private Cluster cluster;

    private final static Logger LOG = LoggerFactory.getLogger(App.class);
    public InstanceStarter(Cluster cluster, String processId, int frequencyInSec, String variables) {
        this.processId = processId;
        this.frequencyInSec = frequencyInSec;
        this.variables = variables;
        this.cluster = cluster;
    }

    public InstanceStaterStatus getStatus()
    {
        return new InstanceStaterStatus(true,this.frequencyInSec,this.variables);
    }

    @Override
    public void run() {
        LOG.debug("IN - InstanceStarter for " + this.processId + "Started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ProcessInstanceEvent processInstanceEvent =
                    cluster.getZeebeClient()
                            .newCreateInstanceCommand()
                            .bpmnProcessId(this.processId)
                            .latestVersion()
                            .variables(this.variables.isEmpty() ? "{}" : this.variables)
                            .send()
                            .join();
                LOG.debug("ProcessInstanceKey" + processInstanceEvent.getProcessInstanceKey());
                Thread.sleep(frequencyInSec*1000);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        LOG.debug("OUT - InstanceStarter for " + this.processId + "Started");
    }
}
