package microsimulator.ws

import org.hamcrest.CoreMatchers
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class ResponseCodesTest extends Specification {

    def "test response codes"() {

        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)

        when:
        simulator.loadSimulations(onClassPath("responses"))

        given().port(port)
                .body("firstBody")
                .post("/responsecode200")
                .then().assertThat().body(CoreMatchers.equalTo("this is the first response code"))
                .statusCode(200)

        given().port(port)
                .body("secondBody")
                .post("/responsecode404")
                .then().assertThat().body(CoreMatchers.equalTo("this is the second response code"))
                .statusCode(404)

        given().port(port)
                .body("thirdBody")
                .post("/responsecode500")
                .then().assertThat().body(CoreMatchers.equalTo("this is the third response code"))
                .statusCode(500)

        given().port(port)
                .body("fourthBody")
                .post("/responsecode401")
                .then().assertThat().body(CoreMatchers.equalTo("this is the fourth response code"))
                .statusCode(401)

        given().port(port)
                .body("fifthBody")
                .post("/responsecode403")
                .then().assertThat().body(CoreMatchers.equalTo("this is the fifth response code"))
                .statusCode(403)

        given().port(port)
                .body("sixthBody")
                .post("/responsecode410")
                .then().assertThat().body(CoreMatchers.equalTo("this is the sixth response code"))
                .statusCode(410)

        given().port(port)
                .body("seventhBody")
                .post("/responsecode503")
                .then().assertThat().body(CoreMatchers.equalTo("this is the seventh response code"))
                .statusCode(503)

        given().port(port)
                .body("eigthBody")
                .post("/responsecode504")
                .then().assertThat().body(CoreMatchers.equalTo("this is the eight response code"))
                .statusCode(504)

        then:
        simulator.simulationCount() == 8

        simulator.findSimulationByName("test.simple").callCount() == 1
        simulator.findSimulationByName("test.simple").lastRequestBody() == 'firstBody'

        simulator.findSimulationByName("test.simple.2").callCount() == 1
        simulator.findSimulationByName("test.simple.2").lastRequestBody() == 'secondBody'

        simulator.findSimulationByName("test.simple.3").callCount() == 1
        simulator.findSimulationByName("test.simple.3").lastRequestBody() == 'thirdBody'

        simulator.findSimulationByName("test.simple.4").callCount() == 1
        simulator.findSimulationByName("test.simple.4").lastRequestBody() == 'fourthBody'

        simulator.findSimulationByName("test.simple.5").callCount() == 1
        simulator.findSimulationByName("test.simple.5").lastRequestBody() == 'fifthBody'

        simulator.findSimulationByName("test.simple.6").callCount() == 1
        simulator.findSimulationByName("test.simple.6").lastRequestBody() == 'sixthBody'

        simulator.findSimulationByName("test.simple.7").callCount() == 1
        simulator.findSimulationByName("test.simple.7").lastRequestBody() == 'seventhBody'

        cleanup:
        simulator.shutdown()

    }

}
