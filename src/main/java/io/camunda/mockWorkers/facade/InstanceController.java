package io.camunda.mockWorkers.facade;

import com.google.gson.Gson;
import io.camunda.mockWorkers.App;
import io.camunda.mockWorkers.domain.Cluster;
import io.camunda.mockWorkers.domain.InstanceStarter;
import io.camunda.mockWorkers.domain.InstanceStaterStatus;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/instances")
@CrossOrigin(origins = "*")
public class InstanceController {

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    private Map<String, Thread> instancesMap;
    private Map<Thread, InstanceStarter> threadMaps;

    @Autowired
    private ClusterController clusterController;

    public InstanceController() {
        this.instancesMap = new HashMap<String, Thread>();
        this.threadMaps = new HashMap<Thread, InstanceStarter>();
    }

    @GetMapping("/start-instances")
    public boolean startInstances(String clusterName, String processId, int frequencyInSec, String variables){
        InstanceStarter it = new InstanceStarter(clusterController.getCluster(clusterName), processId,frequencyInSec,variables);
        Thread t = new Thread(it);
        t.start();
        instancesMap.put(processId,t);
        threadMaps.put(t,it);
        return true;
    }

    @GetMapping("/start-instance")
    public long startInstance(String clusterName, String processId, String variables){
        Cluster cluster = clusterController.getCluster(clusterName);

        ProcessInstanceEvent processInstanceEvent = cluster.getZeebeClient()
                .newCreateInstanceCommand()
                .bpmnProcessId(processId)
                .latestVersion()
                .variables(variables.isEmpty() ? "{}" : variables)
                .send()
                .join();

        return processInstanceEvent.getProcessInstanceKey();
    }

    @GetMapping("/stop-instances")
    public boolean stopInstance(String processId){
        Thread t = instancesMap.get(processId);
        this.threadMaps.remove(t);
        t.interrupt();
        this.instancesMap.remove(processId);
        return true;
    }

    @GetMapping("/get-status")
    public String getStatus(String processId){

        if(this.instancesMap.containsKey(processId))
                return new Gson().toJson(this.threadMaps.get(this.instancesMap.get(processId)).getStatus());
        else
            return new Gson().toJson(new InstanceStaterStatus(false,0,""));
    }

}
