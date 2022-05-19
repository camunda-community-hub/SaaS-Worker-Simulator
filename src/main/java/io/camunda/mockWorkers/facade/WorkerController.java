package io.camunda.mockWorkers.facade;

import com.google.gson.Gson;
import io.camunda.mockWorkers.App;
import io.camunda.mockWorkers.domain.Cluster;
import io.camunda.mockWorkers.domain.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/workers")
@CrossOrigin(origins = "*")
public class WorkerController {

    @Autowired
    private ClusterController clusterController;

    private Map<String, Worker> workersMap;
    private Map<String, List<Worker>> clusterWorkersMap;

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    public WorkerController() {
        this.workersMap = new HashMap<String,Worker>();
        this.clusterWorkersMap = new HashMap<String,List<Worker>>();
    }

    @GetMapping("/create-worker")
    public boolean createWorker(String clusterName, String name, String type, String variables, int workLengthInSec, Double errorPercentage) throws Exception {
        LOG.info("IN - createWorker - " + clusterName + " - "+ name + " - "+ type + " - "+ variables + " - "+ workLengthInSec);
        try {
        synchronized (this)
        {
            Cluster cluster = clusterController.getCluster(clusterName);
            if (cluster == null) throw new Exception("Cluster not found");
                Worker worker = new Worker(cluster, name, type, variables, workLengthInSec);
                this.workersMap.put(name, worker);
                if (!this.clusterWorkersMap.containsKey(cluster.getName())) {
                    this.clusterWorkersMap.put(cluster.getName(), new ArrayList<Worker>());
                }
                ((ArrayList<Worker>) this.clusterWorkersMap.get(cluster.getName())).add(worker);
        }
        }catch (Exception ex)
        {
            throw new Exception("Cannot create worker :" + ex.getMessage());
        }
        LOG.debug("Out - createWorker");
        return true;
    }

    @GetMapping("/list-workers")
    public String listWorkers()
    {
        if(this.workersMap.isEmpty()) return "{}";
        return new Gson().toJson(this.workersMap.values());
    }

    @GetMapping("/list-cluster-workers")
    public String listClusterWorkers(String clusterChosenName)
    {
        if(this.workersMap.isEmpty()) return "{}";
        return new Gson().toJson(this.clusterWorkersMap.get(clusterChosenName));
    }

    @GetMapping("/start-worker")
    public boolean startWorker(String name) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.startWorker();
        }catch (Exception ex)
        {
            throw new Exception("Failed to start worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/stop-worker")
    public boolean stopWorker(String name) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.stopWorker();
        }catch (Exception ex)
        {
            throw new Exception("Failed to stop worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/delete-worker")
    public boolean deleteWorker(String name) throws Exception {
        try {
            workersMap.remove(name);
        }catch (Exception ex)
        {
            throw new Exception("Failed to delete worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/set-error-worker")
    public boolean setErrorOnWorker(String name, Double errorPercentage) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.generateError();
            worker.setPercentError(errorPercentage);
        }catch (Exception ex)
        {
            throw new Exception("Failed to set error for worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/clear-error-worker")
    public boolean clearErrorOnWorker(String name) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.clearError();
        }catch (Exception ex)
        {
            throw new Exception("Failed to clear error for worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/set-work-duration-worker")
    public boolean setWorkDurationWorker(String name, int workDurationInSec) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.setWorkDurationInSec(workDurationInSec);
        }catch (Exception ex)
        {
            throw new Exception("Failed to set work duration for worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/reset-work-duration-worker")
    public boolean resetWorkDurationWorker(String name) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.resetWorkDurationInSec();
        }catch (Exception ex)
        {
            throw new Exception("Failed to reset work duration for worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/set-variables")
    public boolean setVariables(String name,String variables) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            worker.setVariables(variables);
        }catch (Exception ex)
        {
            throw new Exception("Failed to set variables for worker: "+ ex.getMessage());
        }
        return true;
    }

    @GetMapping("/worker-status")
    public String getWorkerStatus(String name) throws Exception {
        Worker worker = this.getWorker(name);
        try {
            return new Gson().toJson(worker.getWorkerStatus());
        }catch (Exception ex)
        {
            throw new Exception("Failed to get worker status: "+ ex.getMessage());
        }
    }

    private Worker getWorker(String name) throws Exception {
        Worker worker = workersMap.get(name);
        if (worker == null) throw new Exception("Worker not found");
        return worker;
    }

}
