[![CircleCI](https://circleci.com/gh/naffe/microsimulator-ws.svg?style=svg)](https://circleci.com/gh/naffe/microsimulator-ws)

# MicroSimulator-WS
 
The microsimulator-WS is an light weight testing library that allows for you to simulate (mock out) HTTP webservice calls within Unit & Integration tests. A key benefit would be when do not want to introduce mocks or couple your tests to external services or system but yet still need to test such interactions. 
 
 **When would you use the MicroSimulator library?**

 *	For Integration level tests when real public service calls cannot be made (for example, when the producing service does not yet exist, costs prohibit calling services within tests or the service isn’t accessible).
 *	When you quickly want to serve up static repeatable content over HTTP.

**Getting Started**

*   Java 8
*   The dependency from JCenter [distro](https://github.com/CognitiveJ/wssimulator/releases/download/0.2.12/wssimulator-0.2.12.zip "Download Standalone Version")

To simulate service calls within the ws simulator, first you will need to specify the simulation. Simulations are defined in a YAML format with flexibility over what you define. These can either be created external or as part of your tests. 

###### A Simple Simulation that defines the path to launch the simulation on
```yaml
path: /hello
```
To most simple of simulations where only the _path_ the Simulation is listening on (with the default port and on http get).  


###### A More complete Simulation
```yaml
path: /hellopostcomplex
httpMethod: post
name: hellopostcomplex
request:
        contentType: application/json
        filterType: contains
        filter: hello
response:
        contentType: application/text
        body: helloworld
latency: 5000-10000
```
### Dependency Management

MicroSimulator is hosted on jcenter() & can be used as below

###### Gradle
```groovy
repositories {
        jcenter()
    }
    
    dependencies {
    compile "naffe.microsimulator-ws:0.4.0"    
    }
    
```

###### Maven
```xml
    <dependency>
      <groupId>naffe</groupId>
      <artifactId>microsimulator-ws</artimicrosimulator   
      <version>0.4.0</version>
      <type>pom</type>
    </dependency>
```

### Examples when using MicroSimulator within your application

* Further Examples can be found on [here](https://github.com/naffe/microsimulator-ws/tree/master/src/test/groovy/microsimulator "Tests").
* A complete application using MicroSimulator can be found here [here](https://github.com/CognitiveJ/wssimulator/tree/master/src/test/groovy/microsimulator "Tests").

### Features

* Support for all HTTP methods
* Request Based Routing based on Call Count or Request content.
* Simulation of long running service calls. 

