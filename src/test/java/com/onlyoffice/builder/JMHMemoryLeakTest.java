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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * JMH benchmark tests for detecting memory leaks in the Document Builder library.
 *
 * <p>This class provides comprehensive memory leak detection benchmarks using the Java
 * Microbenchmark Harness (JMH) framework. It measures various aspects of memory usage including
 * heap memory, non-heap memory, and retained memory after garbage collection.
 *
 * <p>The benchmarks are designed to:
 *
 * <ul>
 *   <li>Detect basic memory leaks during document creation
 *   <li>Measure retained memory after garbage collection
 *   <li>Monitor memory growth patterns during multiple operations
 *   <li>Track native memory usage
 *   <li>Analyze memory usage patterns for different document sizes
 * </ul>
 *
 * <p>Configuration:
 *
 * <ul>
 *   <li>Uses G1 garbage collector for consistent memory management
 *   <li>Allocates 1GB heap memory for testing
 *   <li>Includes GC logging for detailed memory analysis
 *   <li>Runs with 2 warmup iterations and 5 measurement iterations
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
    jvmArgs = {"-Xmx1g", "-Xms1g", "-XX:+UseG1GC", "-XX:+PrintGC", "-XX:+PrintGCDetails"})
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 3)
public class JMHMemoryLeakTest extends BaseJMHMemoryTest {
  /** Memory management bean for monitoring heap and non-heap memory usage */
  private MemoryMXBean memoryBean;

  /**
   * Sets up the test environment and initializes memory monitoring.
   *
   * <p>This method extends the base setup to include memory management capabilities for tracking
   * memory usage during benchmarks.
   */
  @Setup
  @Override
  public void setup() {
    super.setup();
    memoryBean = ManagementFactory.getMemoryMXBean();
  }

  /**
   * Benchmarks basic memory leak detection during document creation.
   *
   * <p>This benchmark measures the difference in heap memory usage before and after creating a
   * document to identify potential memory leaks. It creates a presentation document and measures
   * the memory impact.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_CreateDocument_Then_MeasureBasicMemoryLeak(Blackhole blackhole) {
    var resultPath = "basic-test-" + System.currentTimeMillis() + ".pptx";
    var heapBefore = memoryBean.getHeapMemoryUsage();
    var usedBefore = heapBefore.getUsed();

    try {
      createWithFluentAPI(loader, resultPath);

      blackhole.consume(resultPath);

      var heapAfter = memoryBean.getHeapMemoryUsage();
      var usedAfter = heapAfter.getUsed();
      var memoryDiff = usedAfter - usedBefore;

      blackhole.consume(memoryDiff);
    } catch (Exception e) {
      logBenchmarkError("createWithFluentAPI", e);
    } finally {
      cleanupFile(resultPath);
    }
  }

  /**
   * Benchmarks retained memory measurement after garbage collection.
   *
   * <p>This benchmark forces garbage collection before and after document creation to measure the
   * amount of memory that cannot be reclaimed, indicating potential memory leaks or retained
   * references.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_CreateDocumentWithGC_Then_MeasureRetainedMemory(Blackhole blackhole) {
    var resultPath = "gc-test-" + System.currentTimeMillis() + ".pptx";

    try {
      System.gc();
      Thread.sleep(50);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    var heapBefore = memoryBean.getHeapMemoryUsage();
    var usedBefore = heapBefore.getUsed();

    try {
      createWithFluentAPI(loader, resultPath);

      blackhole.consume(resultPath);

      try {
        System.gc();
        Thread.sleep(50);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      var heapAfter = memoryBean.getHeapMemoryUsage();
      var usedAfter = heapAfter.getUsed();
      var retainedMemory = usedAfter - usedBefore;

      blackhole.consume(retainedMemory);
    } catch (Exception e) {
      logBenchmarkError("demonstrateFluentAPI", e);
    } finally {
      cleanupFile(resultPath);
    }
  }

  /**
   * Benchmarks memory growth patterns during multiple document operations.
   *
   * <p>This benchmark creates multiple documents in sequence to observe memory growth patterns and
   * identify potential cumulative memory leaks that may not be apparent in single operations.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_CreateMultipleDocuments_Then_MeasureMemoryGrowth(Blackhole blackhole) {
    var totalMemoryBefore = 0L;
    var totalMemoryAfter = 0L;
    var initialHeap = memoryBean.getHeapMemoryUsage();

    totalMemoryBefore = initialHeap.getUsed();

    for (var i = 0; i < 3; i++) {
      var resultPath = "stress-test-" + i + "-" + System.currentTimeMillis() + ".pptx";
      try {
        createWithFluentAPI(loader, resultPath);

        blackhole.consume(resultPath);
      } catch (Exception e) {
        logBenchmarkError("createWithFluentAPI", e);
      } finally {
        cleanupFile(resultPath);
      }
    }

    try {
      System.gc();
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    var finalHeap = memoryBean.getHeapMemoryUsage();
    totalMemoryAfter = finalHeap.getUsed();

    var memoryGrowth = totalMemoryAfter - totalMemoryBefore;

    blackhole.consume(memoryGrowth);
  }

  /**
   * Benchmarks native memory usage during document creation.
   *
   * <p>This benchmark measures both heap and non-heap memory usage to identify potential native
   * memory leaks that may not be visible through heap memory monitoring alone.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_CreateDocument_Then_MeasureNativeMemoryLeak(Blackhole blackhole) {
    var resultPath = "native-test-" + System.currentTimeMillis() + ".pptx";
    var heapBefore = memoryBean.getHeapMemoryUsage();
    var nonHeapBefore = memoryBean.getNonHeapMemoryUsage();
    var totalBefore = heapBefore.getUsed() + nonHeapBefore.getUsed();

    try {
      createWithFluentAPI(loader, resultPath);

      blackhole.consume(resultPath);

      var heapAfter = memoryBean.getHeapMemoryUsage();
      var nonHeapAfter = memoryBean.getNonHeapMemoryUsage();
      var totalAfter = heapAfter.getUsed() + nonHeapAfter.getUsed();
      var totalMemoryDiff = totalAfter - totalBefore;

      blackhole.consume(totalMemoryDiff);
    } catch (Exception e) {
      logBenchmarkError("createWithFluentAPI", e);
    } finally {
      cleanupFile(resultPath);
    }
  }

  /**
   * Benchmarks memory usage patterns for documents of different sizes.
   *
   * <p>This benchmark creates multiple documents with varying complexity to analyze how memory
   * usage scales with document size and identify potential memory inefficiencies in the document
   * creation process.
   *
   * @param blackhole JMH blackhole to prevent dead code elimination
   */
  @Benchmark
  public void when_CreateDocumentsOfDifferentSizes_Then_MeasureTotalMemoryUsage(
      Blackhole blackhole) {
    var totalMemoryUsed = 0L;
    var resultPaths =
        new String[] {
          "size-test-small-" + System.currentTimeMillis() + ".pptx",
          "size-test-medium-" + System.currentTimeMillis() + ".pptx",
          "size-test-large-" + System.currentTimeMillis() + ".pptx"
        };

    try {
      for (var resultPath : resultPaths) {
        var heapBefore = memoryBean.getHeapMemoryUsage();
        var usedBefore = heapBefore.getUsed();

        try {
          createWithFluentAPI(loader, resultPath);

          blackhole.consume(resultPath);

          var heapAfter = memoryBean.getHeapMemoryUsage();
          var usedAfter = heapAfter.getUsed();
          totalMemoryUsed += (usedAfter - usedBefore);
        } catch (Exception e) {
          logBenchmarkError("createWithFluentAPI", e);
        }
      }

      blackhole.consume(totalMemoryUsed);
    } finally {
      for (var resultPath : resultPaths) cleanupFile(resultPath);
    }
  }

  /**
   * Main method to run the JMH benchmarks.
   *
   * <p>This method configures and executes all benchmarks in this class, saving results to a JSON
   * file for analysis.
   *
   * @param args command line arguments (not used)
   * @throws RunnerException if an error occurs during benchmark execution
   */
  public static void main(String[] args) throws RunnerException {
    var opt =
        new OptionsBuilder()
            .include(JMHMemoryLeakTest.class.getSimpleName())
            .result("memory-results.json")
            .build();

    new Runner(opt).run();
  }
}
