# Feedback Service

This service enabled the MAPE-K feedback control loop for existing process-models and -execution engines within CPS.

## Run

* Unix: ```./gradlew bootRun``` 
* Windows: ```gradlew.bat bootRun```

## Operation

### Create a context with the DogOnt-Plugin

Given an OpenHAB service with semantic binding running on *localhost:8080* execute the following HTTP request.
After the context has been created, it will be updated constantly.
The response includes the location of the new context for further reference.

```
POST /context HTTP/1.1
Host: localhost:9000
Content-Type: application/json

{
    "name": "home",
    "imports": [{
        "source": "classpath:dogont.owl",
        "mime": "application/rdf+xml",
        "name": "ontology"
    },{
        "source": "http://localhost:8080/rest/semantic",
        "mime": "application/rdf+xml",
        "name": "items"
    }]
} 
```

### Create a Process

```
POST /workflows HTTP/1.1
Host: localhost:9000
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 628e4199-20db-1226-395e-5f62d7e43635

{
    "name": "cooking with friends scenario",
    "context": "http://localhost:9000/context/3227"
}
```

### Create a Process Goal

```
POST /goals HTTP/1.1
Host: localhost:9000
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 879cf86a-deda-a203-ac99-2c5019dc50b9

{
    "name": "enough light for cooking",
    "workflow": "http://localhost:9000/workflows/6348",
    "objectives": [{
        "name": "within the next ten seconds",
        "mime": "application/spel+joda",
        "expression": "now().plusSeconds(10)"
    }, {
        "name": "light intensity in the kitchen will be above 1000 lux",
        "mime": "application/cypher",
        "expressions": [
            "MATCH (thing)-[:isIn]->({ name: 'Kitchen_Mueller' })",
            "MATCH (thing)-[:hasState]->(state:LightIntensityState)",
            "MATCH (state)-[:hasStateValue]->(value)",
            "WHERE toFloat(value.realStateValue) > 1000",
            "RETURN state"
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
