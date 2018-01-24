package microsimulator.ws

import org.hamcrest.CoreMatchers
import org.junit.Assume
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class SpecialCharactersTest extends Specification {

    def "call resource with special characters paths/names"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)
        when:
        simulator.loadSimulations(onClassPath("characters"))
        Assume.assumeTrue(simulator.simulationCount() == 1)
        given().port(port)
                .body("test")
                .post("/characterresource")
                .then().assertThat().body(CoreMatchers.equalTo("this is the special character response"))
                .statusCode(200)
        then:

        simulator.findSimulationByName("test.simple*!#").callCount() == 1
        simulator.findSimulationByName("test.simple*!#").lastRequestBody() == 'test'
        cleanup:
        simulator.shutdown()
    }
}
