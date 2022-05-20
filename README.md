![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)
[![Community extension badge](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![Community badge: Incubating](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-)



# Camunda 8 SaaS Worker Simulator

Web application where a user can upload a C8 SaaS credentials file and a BPMN file. The application will create workers for all service tasks in the BPMN file. All workers can then be configured to generate a error, execute for X amount of seconds, change return values or just stop. The application can also start instances with a configurable cadence.

## Usage

* Run `mvn spring-boot:run`
* Swagger endpoint - http://localhost:8080/swagger-ui/index.html
* Application endpoint http://localhost:8080/index.html
* Upload the credentials file or fill the information
 ![Create Cluster](https://user-images.githubusercontent.com/86626127/168623161-da47c87e-49f6-459f-97e1-53b643a555d3.gif)
* Choose the cluster
 ![Select Cluster](https://user-images.githubusercontent.com/86626127/168623733-852ad7d7-68c9-4bf9-b65d-daea77135085.gif)
* Import the BPMN file to create the worker on the diagram or fill the field to create new ones. Note: tasks containing special characters may cause issues with import at this time
   ![Import BPMN](https://user-images.githubusercontent.com/86626127/168624065-9bd78fef-7de9-41ca-8d9d-aa5f92b081ad.gif)
  * Worker Name -> Friendly unique name for the worker
  * Worker type -> Worker type defined on the diagram
  * Variables (JSON) -> Output variable from the worker JSON format
  * Work Duration (sec.) -> Duration in seconds that the worker will take before finish the task
* Start creating instances
![2022-05-16 16-53-42](https://user-images.githubusercontent.com/86626127/168624765-c8b152a4-d149-4423-90e2-27a22347bf1b.gif)
  * Process Id -> Id of the process to be instantiated
  * Creation interval (sec.) -> Cadence of process creation in seconds
  * Variables (JSON) -> Variables to start the process in JSON
* Config. Workers
![2022-05-16 16-53-51](https://user-images.githubusercontent.com/86626127/168627870-9010a403-2cf0-4bb6-bca4-35c65863a944.gif)
  * Worker when created are stopped
  * Start/Stop -> Start or Stop worker
  * Set Error/Clear Error -> Worker will generate an internal error (Code: 0 & Message: Dummy Error!) based on a percentage of your choice or it can be turned off by clearing it
  * Reset Work Duration -> Resets the worker to the creation duration
  * Set Work Duration -> Set the duration of the worker to a new value in seconds
  * Set variables -> change return variables from the worker in JSON

## Versions

- `main` - Java 16

