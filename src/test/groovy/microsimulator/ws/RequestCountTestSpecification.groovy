package microsimulator.ws

import org.hamcrest.CoreMatchers
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class RequestCountTestSpecification extends Specification {

    def "call from testfiles resources package and test that requestCount works as expected"() {

        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)

        when:
        simulator.loadSimulations(onClassPath("requestcount"))

        //FIRST CALL
        given().port(port)
                .get("/requestcountresource")
                .then().assertThat().body(CoreMatchers.equalTo("this is the first response"))
                .statusCode(200)

        //SECOND CALL
        given().port(port)
                .get("/requestcountresource")
                .then().assertThat().body(CoreMatchers.equalTo("this is the second response"))
                .statusCode(200)

        //THIRD  CALL
        given().port(port)
                .get("/requestcountresource")
                .then().assertThat().body(CoreMatchers.equalTo("this is the third response"))
                .statusCode(200)
        then:
        simulator.simulationCount() == 3
        simulator.findSimulationByName("test.request.count.1").callCount() == 1
        simulator.findSimulationByName("test.request.count.2").callCount() == 1
        simulator.findSimulationByName("test.request.count.3").callCount() == 1
        cleanup:
        simulator.shutdown()

    }

}
