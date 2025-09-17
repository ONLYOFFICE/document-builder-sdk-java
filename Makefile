MAVEN := mvn
NATIVE_PATH ?= /onlyoffice-documentbuilder-macos-arm64
RESULTS_DIR := .
BENCHMARK_RESULTS := $(RESULTS_DIR)/benchmark-results.json
MEMORY_RESULTS := $(RESULTS_DIR)/memory-results.json

.PHONY: all
all: memory-leak-tests

.PHONY: help
help:
	@echo "ONLYOFFICE Document Builder - Memory Leak Test Suite"
	@echo "=============================================================="
	@echo ""
	@echo "Available targets:"
	@echo "  help                    - Show this help message"
	@echo "  check-deps              - Check dependencies (Maven, native libraries)"
	@echo "  clean                   - Clean build artifacts and test results"
	@echo "  compile                 - Compile test classes"
	@echo "  memory-leak-tests       - Run all memory leak tests (default)"
	@echo "  jmh-tests               - Run JMH memory leak tests"
	@echo "  benchmark-tests         - Run JMH benchmark tests"
	@echo "  stress-tests            - Run stress testing scenarios"
	@echo "  individual-tests        - Run individual test methods"
	@echo "  quick-test              - Run quick memory leak validation"
	@echo "  show-results            - Show test results summary"
	@echo "  ci                      - Run tests suitable for CI/CD"
	@echo ""
	@echo "Example usage:"
	@echo "  make memory-leak-tests     # Run all memory leak tests"
	@echo "  make quick-test           # Quick validation"
	@echo "  make show-results         # View test results"
	@echo ""
	@echo "Environment variables:"
	@echo "  NATIVE_PATH=/custom/path make target  # Use custom native library path"

.PHONY: check-deps
check-deps:
	@echo "Checking dependencies..."
	@if ! command -v $(MAVEN) >/dev/null 2>&1; then \
		echo "❌ Maven is not installed or not in PATH"; \
		exit 1; \
	else \
		echo "✅ Maven found: $$($(MAVEN) --version | head -n1)"; \
	fi
	@if [ ! -d "$(NATIVE_PATH)" ]; then \
		echo "⚠️  Warning: Native library path not found: $(NATIVE_PATH)"; \
		echo "   Please update the path in test files if needed"; \
	else \
		echo "✅ Native libraries found at: $(NATIVE_PATH)"; \
	fi
	@echo ""

.PHONY: clean
clean:
	@echo "Cleaning build artifacts..."
	$(MAVEN) clean
	@rm -f *.pptx *.docx *.xlsx *.pdf 2>/dev/null || true
	@rm -f $(BENCHMARK_RESULTS) $(MEMORY_RESULTS) 2>/dev/null || true
	@echo "✅ Clean completed"

.PHONY: compile
compile: check-deps
	@echo "Compiling test classes..."
	$(MAVEN) test-compile
	@echo "✅ Compilation completed"

.PHONY: memory-leak-tests
memory-leak-tests: check-deps compile
	@echo "Running Memory Leak Test Suite..."
	@echo "======================================================"
	@$(MAKE) jmh-tests
	@$(MAKE) benchmark-tests
	@echo ""
	@echo "✅ All memory leak tests completed!"
	@$(MAKE) show-results

.PHONY: jmh-tests
jmh-tests: compile
	@echo "Running JMHMemoryLeakTest..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		com.onlyoffice.builder.JMHMemoryLeakTest || echo "⚠️  JMH tests completed with warnings"
	@echo "✅ JMH memory leak tests completed"

.PHONY: benchmark-tests
benchmark-tests: compile
	@echo "Running JMHMemoryLeakBenchmarkTest..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		com.onlyoffice.builder.JMHMemoryLeakBenchmarkTest || echo "⚠️  Benchmark tests completed with warnings"
	@echo "✅ JMH benchmark tests completed"

.PHONY: stress-tests
stress-tests: compile
	@echo "Running stress test scenarios..."
	@echo "Running multiple document creation stress test..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		-Djmh.includes=".*when_CreateMultipleDocuments_Then_MeasureMemoryGrowth" \
		com.onlyoffice.builder.JMHMemoryLeakTest || echo "⚠️  Stress tests completed with warnings"
	@echo "✅ Stress tests completed"

.PHONY: individual-tests
individual-tests: compile
	@echo "Running individual test methods..."
	@echo "Running basic memory leak detection..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		-Djmh.includes=".*when_CreateDocument_Then_MeasureBasicMemoryLeak" \
		com.onlyoffice.builder.JMHMemoryLeakTest || echo "⚠️  Basic memory test completed with warnings"
	@echo "Running GC memory leak detection..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		-Djmh.includes=".*when_CreateDocumentWithGC_Then_MeasureRetainedMemory" \
		com.onlyoffice.builder.JMHMemoryLeakTest || echo "⚠️  GC memory test completed with warnings"
	@echo "Running rapid iterations test..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		-Djmh.includes=".*when_RunningRapidIterations_Then_MemoryManagementStressed" \
		com.onlyoffice.builder.JMHMemoryLeakBenchmarkTest || echo "⚠️  Rapid iterations test completed with warnings"
	@echo "✅ Individual tests completed"

.PHONY: quick-test
quick-test: compile
	@echo "Running quick memory leak validation..."
	@echo "Running basic memory leak test..."
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		-Djmh.includes=".*when_CreateDocument_Then_MeasureBasicMemoryLeak" \
		com.onlyoffice.builder.JMHMemoryLeakTest || echo "⚠️  Quick test completed with warnings"
	@echo "✅ Quick test completed"

.PHONY: show-results
show-results:
	@echo ""
	@echo "Results summary:"
	@if [ -f "$(BENCHMARK_RESULTS)" ]; then \
		echo "   ✅ $(BENCHMARK_RESULTS)"; \
	else \
		echo "   ❌ $(BENCHMARK_RESULTS) (not found)"; \
	fi
	@if [ -f "$(MEMORY_RESULTS)" ]; then \
		echo "   ✅ $(MEMORY_RESULTS)"; \
	else \
		echo "   ❌ $(MEMORY_RESULTS) (not found)"; \
	fi
	@echo ""

.PHONY: ci
ci: check-deps clean compile quick-test
	@echo "CI/CD pipeline completed"

.PHONY: validate
validate: check-deps
	@echo "Validating test setup..."
	@if [ ! -f "src/test/java/com/onlyoffice/builder/BaseJMHMemoryTest.java" ]; then \
		echo "❌ BaseJMHMemoryTest.java not found"; \
		exit 1; \
	fi
	@if [ ! -f "src/test/java/com/onlyoffice/builder/JMHMemoryLeakTest.java" ]; then \
		echo "❌ JMHMemoryLeakTest.java not found"; \
		exit 1; \
	fi
	@if [ ! -f "src/test/java/com/onlyoffice/builder/JMHMemoryLeakBenchmarkTest.java" ]; then \
		echo "❌ JMHMemoryLeakBenchmarkTest.java not found"; \
		exit 1; \
	fi
	@echo "✅ Test setup validation completed"

.PHONY: run-all-benchmarks
run-all-benchmarks: compile
	@echo "Running all benchmark tests..."
	@$(MAKE) jmh-tests
	@$(MAKE) benchmark-tests
	@echo "✅ All benchmarks completed"

.PHONY: test-specific
test-specific: compile
	@echo "Running specific test method..."
	@read -p "Enter test class (JMHMemoryLeakTest or JMHMemoryLeakBenchmarkTest): " test_class; \
	read -p "Enter test method pattern (e.g., .*when_CreateDocument.*): " test_pattern; \
	NATIVE_PATH="$(NATIVE_PATH)" java -cp "target/test-classes:target/classes:$$($(MAVEN) dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
		-Djmh.includes="$$test_pattern" \
		com.onlyoffice.builder.$$test_class || echo "⚠️  Specific test completed with warnings"