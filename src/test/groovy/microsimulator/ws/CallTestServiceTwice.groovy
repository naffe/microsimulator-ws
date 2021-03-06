package microsimulator.ws

import org.hamcrest.CoreMatchers
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class CallTestServiceTwice extends Specification {

    def "call TestYmlFiles resource twice test"() {

        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)

        when:
        simulator.loadSimulations(onClassPath("testfiles"))

        //FIRST CALL
        given().port(port)
                .body("firstBody")
                .post("/helloresource1")
                .then().assertThat().body(CoreMatchers.equalTo("this is the first response"))

        //SECOND CALL
        given().port(port)
                .body("secondBody")
                .post("/helloresource2")
                .then().assertThat().body(CoreMatchers.equalTo("this is the second response"))
                .statusCode(406)

        then:
        simulator.findSimulationByName("test.simple").callCount() == 1
        simulator.simulationCount() ==2
        simulator.findSimulationByName("test.simple").lastRequestBody() == 'firstBody'
        simulator.findSimulationByName("test.simple.2").callCount() == 1
        simulator.findSimulationByName("test.simple.2").lastRequestBody() == 'secondBody'

        cleanup:
        simulator.shutdown()

    }

}
