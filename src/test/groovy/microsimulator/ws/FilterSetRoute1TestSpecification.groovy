package microsimulator.ws

import io.restassured.http.ContentType
import org.slf4j.Logger
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static org.hamcrest.core.IsEqual.equalTo
import static org.slf4j.LoggerFactory.getLogger


class FilterSetRoute1TestSpecification extends Specification {
    private static final Logger LOG = getLogger(FilterSetRoute1TestSpecification.class)
    def "Validate the route filter feature"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = MicroSimulator.simulator(port)
        when:
        simulator.loadSimulations(onClassPath("route/filterset1"))
        then:
        LOG.info("Validate the route filter feature:then")
        given().port(port)
                .contentType(ContentType.URLENC)
                .body("Action1")
                .post("/filtersetroute1").then().assertThat()
                .statusCode(200).and().body(equalTo("response: FilteredByAction1 - true; route 1"))
        LOG.info("Validate the route filter feature:then-1")

        given().port(port)
                .contentType(ContentType.HTML)
                .body("Action1 with text")
                .post("/filtersetroute1").then().assertThat()
                .statusCode(200).and().body(equalTo("response: FilteredByAction1 - true; route 1"))
        LOG.info("Validate the route filter feature:then-2")

        given().port(port)
                .contentType(ContentType.XML)
                .body("Action1 with text one")
                .post("/filtersetroute1").then().assertThat()
                .statusCode(200).and().body(equalTo("response: FilteredByAction1 - true; route 1"))

        given().port(port)
                .contentType(ContentType.JSON)
                .body("Action1 with text two")
                .post("/filtersetroute1").then().assertThat()
                .statusCode(200).and().body(equalTo("response: FilteredByAction1 - true; route 1"))
        LOG.info("Validate the route filter feature:end")

        cleanup:
        LOG.info("Validate the route filter feature:cleanup")
        simulator.shutdown()
    }

}
