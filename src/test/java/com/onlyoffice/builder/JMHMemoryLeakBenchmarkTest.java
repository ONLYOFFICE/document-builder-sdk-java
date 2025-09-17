/**
 * (c) Copyright Ascensio System SIA 2025
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onlyoffice.builder;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * High-performance JMH benchmark tests for memory leak detection and performance analysis.
 *
 * <p>This class provides intensive benchmarking capabilities for analyzing memory allocation
 * patterns, garbage collection behavior, and overall performance of the Document Builder library.
 * It extends the base memory test functionality with more aggressive testing parameters and
 * additional profiling capabilities.
 *
 * <p>Key differences from basic memory leak tests:
 *
 * <ul>
 *   <li>Higher memory allocation (2GB heap) for stress testing
 *   <li>More iterations and longer measurement periods
 *   <li>Additional profiling with GC and stack profilers
 *   <li>Rapid iteration testing for memory management stress
 * </ul>
 *
 * <p>Configuration:
 *
 * <ul>
 *   <li>Uses G1 garbage collector for optimal performance
 *   <li>Allocates 2GB heap memory for intensive testing
 *   <li>Runs with 3 warmup iterations and 10 measurement iterations
 *   <li>Includes GC and stack profiling for detailed analysis
 * </ul>
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(
    value = 1,
    jvmArgs = {"-Xmx2g", "-Xms2g", "-XX:+UseG1GC"})
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 10, time = 5)
public class JMHMemoryLeakBenchmarkTest extends BaseJMHMemoryTest {

  /**
   * Benchmarks basic memory allocation during fluent API operations.
   *
   * <p>This benchmark measures the memory allocation patterns when using the fluent API to create
   * documents. It provides baseline performance metrics for memory usage during typical document
   * creation operations.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_BenchmarkingFluentAPI_Then_MemoryAllocationMeasured(Blackhole blackhole) {
    var resultPath = "benchmark-result-" + System.currentTimeMillis() + ".pptx";

    try {
      createWithFluentAPI(loader, resultPath);

      blackhole.consume(resultPath);
    } catch (Exception e) {
      logBenchmarkError("createWithFluentAPI", e);
    } finally {
      cleanupFile(resultPath);
    }
  }

  /**
   * Benchmarks memory retention patterns after garbage collection.
   *
   * <p>This benchmark analyzes how much memory is retained after garbage collection, which is
   * crucial for identifying memory leaks and understanding the garbage collector's effectiveness
   * with the Document Builder library.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_BenchmarkingWithGC_Then_RetainedMemoryMeasured(Blackhole blackhole) {
    var resultPath = "benchmark-gc-result-" + System.currentTimeMillis() + ".pptx";

    try {
      createWithFluentAPI(loader, resultPath);

      blackhole.consume(resultPath);

      try {
        System.gc();
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    } catch (Exception e) {
      logBenchmarkError("createWithFluentAPI", e);
    } finally {
      cleanupFile(resultPath);
    }
  }

  /**
   * Benchmarks memory management under rapid iteration stress.
   *
   * <p>This benchmark creates multiple documents in rapid succession to stress test the memory
   * management system and identify potential bottlenecks or memory leaks that may occur under
   * high-frequency usage.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_RunningRapidIterations_Then_MemoryManagementStressed(Blackhole blackhole) {
    for (int i = 0; i < 5; i++) {
      var resultPath = "benchmark-rapid-" + i + "-" + System.currentTimeMillis() + ".pptx";

      try {
        createWithFluentAPI(loader, resultPath);

        blackhole.consume(resultPath);
      } catch (Exception e) {
        logBenchmarkError("createWithFluentAPI", e);
      } finally {
        cleanupFile(resultPath);
      }
    }
  }

  /**
   * Benchmarks memory allocation patterns and tracking.
   *
   * <p>This benchmark uses Runtime memory monitoring to track memory allocation patterns during
   * document creation. It provides insights into how memory is allocated and used throughout the
   * document creation process.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_MonitoringMemory_Then_AllocationPatternsTracked(Blackhole blackhole) {
    var runtime = Runtime.getRuntime();
    var memoryBefore = runtime.totalMemory() - runtime.freeMemory();
    var resultPath = "benchmark-memory-" + System.currentTimeMillis() + ".pptx";

    try {
      createWithFluentAPI(loader, resultPath);

      blackhole.consume(resultPath);

      var memoryAfter = runtime.totalMemory() - runtime.freeMemory();
      var memoryUsed = memoryAfter - memoryBefore;

      blackhole.consume(memoryUsed);
    } catch (Exception e) {
      logBenchmarkError("demonstrateFluentAPI", e);
    } finally {
      cleanupFile(resultPath);
    }
  }

  /**
   * Main method to run the intensive JMH benchmarks with profiling.
   *
   * <p>This method configures and executes all benchmarks in this class with additional profiling
   * capabilities including GC profiling and stack profiling for comprehensive performance analysis.
   *
   * @param args command line arguments (not used)
   * @throws RunnerException if an error occurs during benchmark execution
   */
  public static void main(String[] args) throws RunnerException {
    var opt =
        new OptionsBuilder()
            .include(JMHMemoryLeakBenchmarkTest.class.getSimpleName())
            .addProfiler("gc")
            .addProfiler("stack")
            .result("benchmark-results.json")
            .build();

    new Runner(opt).run();
  }
}
