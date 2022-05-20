package io.camunda.mockWorkers.domain;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Random;

public class InternalJobHandler implements JobHandler {

    private String variables;
    private int workDurationInSec;
    private boolean error;
    private Double percentError;

    public InternalJobHandler(String variables,int workDurationInSec) {
        this.variables = variables;
        this.workDurationInSec = workDurationInSec;
        this.error = false;
        this.percentError = new Double(0);
    }

    public String getStatus()
    {
        return "workDurationInSec:"+workDurationInSec+",error:"+error;
    }

    public void setError(boolean error)
    {
        this.error = error;
    }

    public boolean getError()
    {
        return this.error;
    }
    public void setWorkDurationInSec(int workDurationInSec)
    {
        this.workDurationInSec = workDurationInSec;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public String getVariables() {
        return this.variables;
    }

    public int getWorkDurationInSec()
    {
        return this.workDurationInSec;
    }

    public Double getPercentError() { return this.percentError; }

    public void setPercentError(Double pErr) { this.percentError = pErr; }

    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        if(error)
        {
            //TODO: error generation dynamic get code and message in the input for error
            Random r = new Random();
            int isItError = r.nextInt(100);

            if(isItError < this.percentError) {
                client.newThrowErrorCommand(job.getKey()).errorCode("0").errorMessage("Dummy Error!").send();
            } else {
                client.newCompleteCommand(job.getKey()).variables(variables).send().join();
            }
        }else {
            if (this.variables == null || this.variables.isEmpty()) {
                variables = "{}";
            }
            try {
                Thread.sleep(workDurationInSec*1000);
            } catch (InterruptedException e) {
                client.newFailCommand(job.getKey()).retries(job.getRetries() - 1).errorMessage(e.getMessage()).send();
            }
            client.newCompleteCommand(job.getKey()).variables(variables).send().join();
        }
    }
}

