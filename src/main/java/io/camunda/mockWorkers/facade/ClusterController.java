package io.camunda.mockWorkers.facade;

import com.google.gson.Gson;
import io.camunda.mockWorkers.App;
import io.camunda.mockWorkers.domain.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/clusters")
@CrossOrigin(origins = "*")
public class ClusterController {

    private Map<String, Cluster> clusterMap;

    public ClusterController() {
        this.clusterMap = new HashMap<String, Cluster>();
    }

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    @GetMapping("/add-cluster")
    public boolean addCluster(
            String name,
            String clientId,
            String clientSecret,
            String clusterId,
            String region
    ) throws Exception {
        LOG.debug("IN - addCluster - " + name + " - "+ clientId + " - "+ clientSecret + " - "+ clusterId + " - "+ region);
        try {
            this.clusterMap.put(name, new Cluster(name, clientId, clientSecret, clusterId, region));
        }catch (Exception ex)
        {
            throw new Exception("Cannot create cluster : " + ex.getMessage());
        }
        LOG.debug("OUT - addCluster");
        return true;
    }

    @GetMapping("/delete-cluster")
    public boolean deleteCluster(
            String name
    ) throws Exception {
        try{
            this.clusterMap.remove(name);
        }catch (Exception ex)
        {
            throw new Exception("Cannot delete cluster : " + ex.getMessage());
        }
        return true;
    }

    @GetMapping("/list-clusters")
    public String listClusters(
    )
    {
        if(this.clusterMap.isEmpty()) return "";
        return new Gson().toJson(this.clusterMap.values());
    }

    public Cluster getCluster(String name) {
        return clusterMap.get(name);
    }
}
