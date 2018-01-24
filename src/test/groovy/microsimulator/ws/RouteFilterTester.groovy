package microsimulator.ws

import io.restassured.http.ContentType
import org.slf4j.Logger
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static org.hamcrest.core.IsEqual.equalTo
import static org.slf4j.LoggerFactory.getLogger

class RouteFilterTester extends Specification {
    private static final Logger LOG = getLogger(RouteFilterTester.class)

    def "Validate the route filter feature"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = MicroSimulator.simulator(port)
        when:
        simulator.loadSimulations(onClassPath("route"))
        then:
        LOG.info("Validate the route filter feature:then")
        given().port(port)
                .contentType(ContentType.XML)
                .body("This is just random test with Action1 contained within it")
                .post("/publish").then().assertThat()
                .statusCode(200).and().body(equalTo("FilteredByAction1"))
        LOG.info("Validate the route filter feature:then-1")

        given().port(port)
                .contentType(ContentType.XML)
                .body("This is just random test with Action2 contained within it")
                .post("/publish").then().assertThat()
                .statusCode(200).and().body(equalTo("FilteredByAction2"))
        LOG.info("Validate the route filter feature:then-2")

        given().port(port)
                .contentType(ContentType.XML)
                .body("This is just random test with Action3 contained within it")
                .post("/publish").then().assertThat()
                .statusCode(200).and().body(equalTo("FilteredByAction3"))
        LOG.info("Validate the route filter feature:then-3")

        given().port(port)
                .contentType(ContentType.XML)
                .body("This is just random test with no action mapped contained within it")
                .post("/publish").then().assertThat()
                .statusCode(200).and().body(equalTo("FilteredByAction4"))
        LOG.info("Validate the route filter feature:end")

        cleanup:
        LOG.info("Validate the route filter feature:cleanup")
        simulator.shutdown()
    }

}
