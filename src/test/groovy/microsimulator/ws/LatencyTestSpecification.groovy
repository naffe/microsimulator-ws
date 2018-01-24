package microsimulator.ws

import org.apache.commons.lang3.time.StopWatch
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static MicroSimulator.simulator

class LatencyTestSpecification extends Specification {

    def fiveSecondLatency = """
path: /fiveSeconds
httpMethod: post
name: 5SecondTest.simple
response: 
            body: this is the string
latency: 5000
"""

    def TenSecondLatency = """
path: /tenSeconds
httpMethod: post
name: 10SecondTest.simple
response: 
         body: this is the string
latency: 10000
"""

    def "Simple 5 Second latency simulator Test"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)
        String request = "this is the request"

        when:
        simulator.loadSimulation(fiveSecondLatency)
        StopWatch stopWatch = new StopWatch()
        stopWatch.start()

        then:
        given().port(port)
                .body(request)
                .post("/fiveSeconds").then().assertThat()
                .statusCode(200)
        stopWatch.getTime() > 5000
        simulator.simulationCount() == 1
        simulator.findSimulationByName("5SecondTest.simple").callCount() == 1
        simulator.findSimulationByName("5SecondTest.simple").lastRequestBody() == request

        cleanup:
        simulator.shutdown()
    }

    def "Simple 10 Second latency simulator Test"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)
        String request = "this is the request"

        when:
        simulator.loadSimulation(TenSecondLatency)
        StopWatch stopWatch = new StopWatch()
        stopWatch.start()

        then:
        given().port(port)
                .body(request)
                .post("/tenSeconds").then().assertThat()
                .statusCode(200)
        stopWatch.getTime() > 10000
        simulator.simulationCount() == 1
        simulator.findSimulationByName("10SecondTest.simple").callCount() == 1
        simulator.findSimulationByName("10SecondTest.simple").lastRequestBody() == request

        cleanup:
        simulator.shutdown()
    }

}