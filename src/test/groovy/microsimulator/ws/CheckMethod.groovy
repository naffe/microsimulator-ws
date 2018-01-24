package microsimulator.ws

import org.hamcrest.CoreMatchers
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class CheckMethod extends Specification {

    def "call from TestYmlFiles resources test"() {

        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)

        when:
        simulator.loadSimulations(onClassPath("methods"))

        given().port(port)
                .body("firstBody")
                .post("/postmethod")
                .then().assertThat().body(CoreMatchers.equalTo("this is the post method"))
                .statusCode(200)

        given().port(port)
                .get("/getmethod")
                .then().assertThat().body(CoreMatchers.equalTo("this is the get method"))
                .statusCode(200)

        given().port(port)
                .body("thirdBody")
                .put("/putmethod")
                .then().assertThat().body(CoreMatchers.equalTo("this is the put method"))
                .statusCode(200)

        given().port(port)
                .body("fourthBody")
                .delete("/deletemethod")
                .then().assertThat().body(CoreMatchers.equalTo("this is the delete method"))
                .statusCode(200)

        given().port(port)
                .body("fifthBody")
                .head("/headmethod")
                .then().assertThat().body(CoreMatchers.equalTo(""))
                .statusCode(200)

        given().port(port)
                .body("sixthBody")
                .patch("/patchmethod")
                .then().assertThat().body(CoreMatchers.equalTo("this is the patch method"))
                .statusCode(200)

        then:
        simulator.simulationCount() == 6
        simulator.findSimulationByName("test.post").callCount() == 1
        simulator.findSimulationByName("test.get").callCount() == 1
        simulator.findSimulationByName("test.put").callCount() == 1
        simulator.findSimulationByName("test.delete").callCount() == 1
        simulator.findSimulationByName("test.head").callCount() == 1
        simulator.findSimulationByName("test.patch").callCount() == 1

        cleanup:
        simulator.shutdown()

    }
}
