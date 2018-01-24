@file:JvmName("Helpers")
package microsimulator.ws

import microsimulator.ws.loader.ClassPathLoader
import microsimulator.ws.loader.ExcludeByNameFilter
import microsimulator.ws.loader.Filter

        fun onClassPath(classpathLocation: String): ClassPathLoader {
            return onClassPath(listOf(classpathLocation), emptyList())
        }

        /**
         * Scans a classpath location for valid [MicroSimulation] simulations with additional [Filter]
         * @param classpathLocation1 The location on the first classpath to scan
         * @param classpathLocation2 The location on the second classpath to scan
         * @param filter additional Exclusion [Filter]
         * @return the configured @ClassPathLoader
         */
        fun onClassPath(classpathLocation1: String, classpathLocation2: String, filter: Filter): ClassPathLoader {
            return onClassPath(listOf(classpathLocation1, classpathLocation2), listOf(filter))
        }

        /**
         * Scans a classpath location for valid MicroSimulation definition filters.
         * @param classpathLocation1 The location on the first classpath to scan
         * @param classpathLocation2 The location on the second classpath to scan
         * @param classpathLocation3 The location on the third classpath to scan
         * @return the configured @ClassPathLoader
         */
        fun onClassPath(classpathLocation1: String, classpathLocation2: String, classpathLocation3: String, filter: Filter): ClassPathLoader {
            return onClassPath(listOf(classpathLocation1, classpathLocation2, classpathLocation3), listOf(filter))
        }

        /**
         * Scans a classpath locations for valid [MicroSimulation] simulations.
         * @param classpathLocations The locations on the first classpath to scan
         * @return the configured @ClassPathLoader
         */
        fun onClassPath(vararg classpathLocations: String): ClassPathLoader {
            return onClassPath(classpathLocations.toList(), emptyList())
        }

        /**
         * Scans a classpath locations for valid [MicroSimulation] simulations within a [Collection].
         * @param classpathLocations The locations on the classpath to scan
         * @return the configured @ClassPathLoader
         */
        fun onClassPath(classpathLocations: Collection<String>): ClassPathLoader {
            return onClassPath(classpathLocations, emptyList())
        }

        /**
         * Scans a classpath locations for valid [MicroSimulation] simulations within a [Collection] with an additional [Filter].
         * @param classpathLocations The locations on the classpath to scan
         * @param filters to [Filter] the [MicroSimulation]
         * @return the configured @ClassPathLoader
         */
        fun onClassPath(classpathLocations: Collection<String>, filter: Filter): ClassPathLoader {
            return onClassPath(classpathLocations, listOf(filter))
        }

        /**
         * Scans a classpath locations for valid [MicroSimulation] simulations within a [Collection].
         * @param classpathLocations The locations on the classpath to scan
         * @param collection of filters to [Filter] the [MicroSimulation]
         * @return the configured @ClassPathLoader
         */
        fun onClassPath(classpathLocations: Collection<String>, filter: Collection<Filter>): ClassPathLoader {
            return ClassPathLoader(classpathLocations.toSet(), filter);
        }

        /**
         * Exclude [MicroSimulation]'s based on simulation name.
         */
        fun excludeByName(filter: String): ExcludeByNameFilter {
            return ExcludeByNameFilter(filter);
        }

