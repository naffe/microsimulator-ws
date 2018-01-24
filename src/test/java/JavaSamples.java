import io.restassured.response.Response;
import microsimulator.ws.MicroSimulator;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static microsimulator.ws.Helpers.onClassPath;
import static microsimulator.ws.MicroSimulator.simulator;

@SuppressWarnings("ConstantConditions")
public class JavaSamples {

    @Test
    public void helloFromMicroSimulator() {
        //launch the simulator on port 8080
        simulator(8080)
                .loadSimulations(onClassPath("samples/hello"));
        //call the newly launched simulation
        given().get("/hello").asString(); //helloworld
    }

    @Test
    public void simplePostSimulation() {
        MicroSimulator simulator = simulator(8080)
                .loadSimulations(onClassPath("samples/hellopost"));
        given().body("hi")
                .post("/hellopost").asString(); //= helloworld
        simulator.findSimulationByName("hellopost")
                .lastRequestBody();//= hi
    }


    @Test
    public void moreComplexPostSimulation() {
        MicroSimulator simulator = simulator(8080)
                .loadSimulations(onClassPath("samples/hellocomplex"));
        given().body("hello request")
                .post("/hellopost").asString(); //= hello post complex
        simulator.findSimulationByName("hellopostcomplex")
                .lastRequestBody();//= hello
    }
}
