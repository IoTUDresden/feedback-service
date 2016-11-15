# Feedback Service

This service enabled the MAPE-K feedback control loop for existing process-models and -execution engines within CPS.

See the following publication for more detail:

Seiger, R., Huber, S., Heisig, P., & Assmann, U. (2016, June). Enabling Self-adaptive Workflows for Cyber-physical Systems. In International Workshop on Business Process Modeling, Development and Support (pp. 3-17). Springer International Publishing.

* Run: ```OPENHAB_HOST=172.17.0.1 docker-compose up```
* Build: ```./gradlew buildDocker```

To get the host's address (OPENHAB_HOST), check the IP of the docker network interface (```docker0```).
If you're using an external Neo4j instance, the version should be less or equal to 2.3.6.

* 9000: MAPE-K (feedback service)
    * example DogOnt/OpenHAB instances: examples/openhab-instance.ttl (file type: "text/turtle" for context creation)
* 5602: Kibana (metrics visualization)
    * index pattern: mapek-*
    * example visualizations and dashboard: examples/kibana-export.json
* 7474: Neo4j (context and domain graph)
    * web interface for graph visualization
* 9200: Elasticsearch HTTP (metrics index)
    * index: mapek-*

## Operation

### Create a context with the DogOnt-Plugin

Given an OpenHAB service with semantic binding running on *localhost:8080* execute the following HTTP request.
After the context has been created, it will be updated constantly.
The response includes the location of the new context for further reference.

```
POST /contexts HTTP/1.1
Host: localhost:9000
Content-Type: application/json

{
    "name": "home",
    "imports": [{
        "source": "classpath:dogont.owl",
        "mimeType": "application/rdf+xml",
        "name": "ontology"
    },{
        "source": "http://172.17.0.1:8080/rest/semantic",
        "mimeType": "application/rdf+xml",
        "name": "items"
    }]
} 
```

### Create a Workflow

Use your own context reference URL, created by the previous post!

```
POST /workflows HTTP/1.1
Host: localhost:9000
Content-Type: application/json

{
    "name": "cooking with friends scenario",
    "context": "http://localhost:9000/context/3227"
}
```

### Create a Process Goal

Use your own workflow reference URL, created by the previous post!

```
POST /goals HTTP/1.1
Host: localhost:9000
Content-Type: application/json

{
    "name": "enough light for cooking",
    "workflow": "http://localhost:9000/workflows/12916",
    "objectives": [{
        "name": "kitchen light intensity > 865 lux within two seconds",
        "satisfiedExpression": "#lightIntensity > 865",
        "compensateExpression": "#objective.created.isBefore(#now.minusSeconds(2))",
        "testNodeIdExpression": "#stateId",
        "contextExpressions": [
            "MATCH (thing)-[:type]->(sensor {name: 'LightSensor'})",
            "MATCH (thing)-[:isIn]->(room {name: 'Kitchen_Mueller'})",
            "MATCH (thing)-[:hasState]->(state:LightIntensityState)",
            "MATCH (state)-[:hasStateValue]->(value)",
            "RETURN avg(toFloat(value.realStateValue)) AS lightIntensity, head(collect(id(state))) AS stateId"
        ]
    }]
}
```

## IDE-Import

### IntelliJ

1. *File* > *New* > *Project from existing Sources*
2. Select *build.gradle* from the project's root

### Eclipse

1. Execute on command line
    * Unix: ```./gradlew eclipse```
    * Windows: ```gradlew.bat eclipse```
2. *File* > *Import* > *Existing Projects into Workspace*
3. Choose the project's root directory
4. Check *Search for nested Projects*
