# Feedback Service

This service enabled the MAPE-K feedback control loop for existing process-models and -execution engines within CPS.

## Run

* Unix: ```./gradlew bootRun``` 
* Windows: ```gradlew.bat bootRun```

## Operation

### Create a context with the DogOnt-Plugin

Given an OpenHAB service with semantic binding running on *localhost:8080* execute the following HTTP request. After the context has been created, it will be updated constantly.

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
