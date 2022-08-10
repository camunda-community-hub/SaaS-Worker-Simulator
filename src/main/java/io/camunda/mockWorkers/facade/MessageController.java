package io.camunda.mockWorkers.facade;

import com.google.gson.Gson;
import io.camunda.mockWorkers.App;
import io.camunda.mockWorkers.domain.Cluster;
import io.camunda.mockWorkers.domain.Message;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    @Autowired
    private ClusterController clusterController;
    private Map<String, List<Message>> messagesMap;

    public MessageController() {
        this.messagesMap = new HashMap<String,List<Message>>();
    }

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

    @GetMapping("/register-message")
    public boolean registerMessage (String clusterName, String messageName, String messageId)
    {
        synchronized (this) {
            if(!messagesMap.containsKey(clusterName))
                messagesMap.put(clusterName, new ArrayList<Message>(Arrays.asList(new Message(messageName,messageId))));
            else
                messagesMap.get(clusterName).add(new Message(messageName,messageId));
        }
        return true;
    }

    @GetMapping("/get-messages")
    public String getMessage (String clusterName)
    {
        if(!messagesMap.containsKey(clusterName)) return "{}";
        return new Gson().toJson(messagesMap.get(clusterName));
    }
}
