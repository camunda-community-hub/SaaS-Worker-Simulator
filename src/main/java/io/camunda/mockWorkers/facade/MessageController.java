package io.camunda.mockWorkers.facade;

import io.camunda.mockWorkers.App;
import io.camunda.mockWorkers.domain.Cluster;
import io.camunda.mockWorkers.domain.InstanceStarter;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    @Autowired
    private ClusterController clusterController;

    @GetMapping("/send-message")
    public boolean sendMessage(String clusterName, String messageName, String correlationKey, String variables){
        Cluster cluster = clusterController.getCluster(clusterName);
        ZeebeClient zeebeClient = cluster.getZeebeClient();

        try {
            zeebeClient
                    .newPublishMessageCommand()
                    .messageName(messageName)
                    .correlationKey(correlationKey)
                    .variables(variables.isEmpty() ? "{}" : variables)
                    .send()
                    .join();
        }catch (Exception ex)
        {
            return false;
        }
        return true;
    }

}
