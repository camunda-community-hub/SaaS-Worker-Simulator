package io.camunda.mockWorkers.domain;

import io.camunda.zeebe.client.ZeebeClient;


public class Cluster {

    private String name;
    private String clientId;
    private String clientSecret;
    private String clusterId;
    private String region;
    private transient ZeebeClient zeebeClient;

    public Cluster(
            String name,
            String clientId,
            String clientSecret,
            String clusterId,
            String region) {
        this.name = name;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clusterId = clusterId;
        this.region = region;

        this.zeebeClient = ZeebeClient.newCloudClientBuilder()
                .withClusterId(clusterId)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRegion(region)
                .build();
    }

    public ZeebeClient getZeebeClient() {
        return this.zeebeClient;
    }

    public String getName(){return this.name;}
}
