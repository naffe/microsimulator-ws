package microsimulator.ws


data class MicroSimulation @JvmOverloads constructor(
        val path: String,
        val httpMethod: MicroSimulatorHandlerService.HttpMethod = MicroSimulatorHandlerService.HttpMethod.get, val responseCode: Int = 200,
        val name: String = "",
        val latency: String = "",
        /**
         * Sets a priority on the [MicroSimulation] for when multiple simulations match the criteria.
         */
        val priority: Int = Integer.MAX_VALUE,
        /**
         * Allows for tags to be assigned to [MicroSimulation]'s
         */
        val tag: String = "",

        val request: MicroSimulatorRequest = MicroSimulatorRequest(),
        val response: MicroSimulatorResponse = MicroSimulatorResponse()) {

    class MicroSimulatorRequest(
            var contentType: String = "*/*",
            var filterType: RouteRequestFilterType = RouteRequestFilterType.none,
            var filter: String = ""
    )


    class MicroSimulatorResponse(
            var contentType: String = "text/plain",
            var body: String = "",
            var statusCode: Int = 200
    )

    enum class RouteRequestFilterType {
        none, contains, absent, callCount
    }

}
