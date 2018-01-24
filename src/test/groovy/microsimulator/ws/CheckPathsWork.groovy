package microsimulator.ws

import org.hamcrest.CoreMatchers
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class CheckPathsWork extends Specification {

    def "calling classpath's with multiple '/'s "() {

        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)

        when:
        simulator.loadSimulations(onClassPath("route/filterset1/filterset1route1"))
        simulator.loadSimulations(onClassPath("loaders/loc1/loc1valid1"))

        given().port(port)
                .body("Action1")
                .post("/filtersetroute1")
                .then().assertThat().body(CoreMatchers.equalTo("response: FilteredByAction1 - true; route 1"))
                .statusCode(200)


        then:
        simulator.simulationCount() == 2
        simulator.findSimulationByName("test.simple").callCount() == 1
        simulator.findSimulationByName("test.simple").lastRequestBody() == 'Action1'

        cleanup:
        simulator.shutdown()
    }
}