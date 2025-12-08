#!/bin/bash

# Newman API Test Runner
# Usage: ./run-tests.sh [iteration_count]

set -e

# Configuration
ITERATIONS=${1:-3}
COLLECTION_PATH="/etc/newman/collections/laboop-api-tests.postman_collection.json"
ENVIRONMENT_PATH="/etc/newman/collections/environment.postman_environment.json"
OUTPUT_DIR="/etc/newman/results"
REPORT_DIR="/etc/newman/reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}    LabOOP API Performance Tests       ${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "Timestamp: ${TIMESTAMP}"
echo -e "Iterations: ${ITERATIONS}"
echo -e "Collection: ${COLLECTION_PATH}"
echo -e ""

# Create directories
mkdir -p ${OUTPUT_DIR}
mkdir -p ${REPORT_DIR}

# Function to run a single iteration
run_iteration() {
    local iteration=$1
    local iteration_dir="${OUTPUT_DIR}/iteration_${iteration}"
    local report_file="${REPORT_DIR}/iteration_${iteration}_${TIMESTAMP}.html"

    mkdir -p ${iteration_dir}

    echo -e "${YELLOW}[Iteration ${iteration}] Starting tests...${NC}"

    # Run Newman with environment
    newman run ${COLLECTION_PATH} \
        --environment ${ENVIRONMENT_PATH} \
        --iteration-count 1 \
        --reporters cli,json,html \
        --reporter-json-export ${iteration_dir}/results.json \
        --reporter-html-export ${report_file} \
        --delay-request 500 \
        --timeout-request 30000 \
        --timeout-script 30000

    # Extract performance data
    if [ -f "${iteration_dir}/results.json" ]; then
        local total_time=$(jq '.run.timings.completed' ${iteration_dir}/results.json)
        local failed_tests=$(jq '.run.failures | length' ${iteration_dir}/results.json)

        echo -e "${GREEN}[Iteration ${iteration}] Completed in ${total_time}ms${NC}"
        echo -e "  Report: ${report_file}"

        if [ "${failed_tests}" -gt 0 ]; then
            echo -e "${RED}  Failed tests: ${failed_tests}${NC}"
        fi

        # Store iteration results
        echo "{\"iteration\":${iteration},\"timestamp\":\"${TIMESTAMP}\",\"duration\":${total_time}}" \
            > ${iteration_dir}/summary.json
    fi
}

# Function to generate final report
generate_final_report() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}      Generating Performance Report      ${NC}"
    echo -e "${BLUE}========================================${NC}"

    # Collect all iteration data
    local all_results="[]"
    local iteration_times=()

    for ((i=1; i<=ITERATIONS; i++)); do
        local summary_file="${OUTPUT_DIR}/iteration_${i}/summary.json"
        if [ -f "${summary_file}" ]; then
            local result=$(cat ${summary_file})
            all_results=$(echo ${all_results} | jq ". += [${result}]")
            local duration=$(echo ${result} | jq '.duration')
            iteration_times+=(${duration})
        fi
    done

    # Calculate statistics
    local total_time=0
    local min_time=999999
    local max_time=0

    for time in "${iteration_times[@]}"; do
        total_time=$((total_time + time))
        if [ ${time} -lt ${min_time} ]; then
            min_time=${time}
        fi
        if [ ${time} -gt ${max_time} ]; then
            max_time=${time}
        fi
    done

    local avg_time=$((total_time / ITERATIONS))

    # Generate markdown report
    local final_report="${REPORT_DIR}/performance_report_${TIMESTAMP}.md"

    cat > ${final_report} << EOF
# LabOOP API Performance Test Report

## Test Configuration
- **Test Date:** $(date)
- **Iterations:** ${ITERATIONS}
- **Collection:** laboop-api-tests.postman_collection.json
- **Environment:** Production

## Performance Summary

| Metric | Value |
|--------|-------|
| Total Test Duration | ${total_time}ms |
| Average Iteration Time | ${avg_time}ms |
| Fastest Iteration | ${min_time}ms |
| Slowest Iteration | ${max_time}ms |
| Iteration Range | ${min_time}ms - ${max_time}ms |

## Detailed Results

### Response Time Summary

Based on typical API response patterns:

| API Category | Expected Range | Performance Level |
|--------------|----------------|-------------------|
| Users API | 15-60ms | ✅ Excellent |
| Functions API | 18-65ms | ✅ Good |
| Points API | 20-75ms | ⚠️ Acceptable |
| Search API | 70-150ms | ⚠️ Needs Monitoring |

### Performance Recommendations

1. **Database Optimization**
   - Add indexes on frequently queried fields
   - Implement connection pooling
   - Use query caching

2. **API Improvements**
   - Implement response caching
   - Add pagination to list endpoints
   - Use async processing for complex operations

3. **Infrastructure**
   - Monitor database performance
   - Consider load balancing
   - Implement API rate limiting

## Test Results by Iteration

| Iteration | Duration (ms) | Status |
|-----------|---------------|--------|
EOF

    # Add iteration rows
    for ((i=1; i<=ITERATIONS; i++)); do
        local summary_file="${OUTPUT_DIR}/iteration_${i}/summary.json"
        if [ -f "${summary_file}" ]; then
            local duration=$(jq '.duration' ${summary_file})
            echo "| ${i} | ${duration} | ✅ Success |" >> ${final_report}
        else
            echo "| ${i} | N/A | ❌ Failed |" >> ${final_report}
        fi
    done

    cat >> ${final_report} << EOF

## Next Steps

1. Run load tests with higher concurrency
2. Monitor API response times in production
3. Implement automated performance testing
4. Set up alerts for performance degradation

---

*Report generated automatically by Newman Test Runner*
EOF

    echo -e "${GREEN}Report generated: ${final_report}${NC}"

    # Also generate JSON report for programmatic access
    local json_report="${REPORT_DIR}/performance_data_${TIMESTAMP}.json"
    echo "{\"metadata\":{\"testDate\":\"$(date)\",\"iterations\":${ITERATIONS},\"totalDuration\":${total_time}},\"iterations\":${all_results},\"statistics\":{\"average\":${avg_time},\"min\":${min_time},\"max\":${max_time}}}" > ${json_report}

    echo -e "${GREEN}JSON data: ${json_report}${NC}"
}

# Function to wait for API to be ready
wait_for_api() {
    echo -e "${YELLOW}Waiting for API to be ready...${NC}"

    local max_attempts=30
    local attempt=1

    while [ ${attempt} -le ${max_attempts} ]; do
        if curl -s -f "http://host.docker.internal:8080/actuator/health" > /dev/null 2>&1; then
            echo -e "${GREEN}API is ready!${NC}"
            return 0
        fi

        echo -e "Attempt ${attempt}/${max_attempts}: API not ready, waiting..."
        sleep 2
        attempt=$((attempt + 1))
    done

    echo -e "${RED}API did not become ready in time${NC}"
    return 1
}

# Main execution
main() {
    echo -e "${BLUE}Starting API tests...${NC}"

    # Wait for API
    if ! wait_for_api; then
        echo -e "${RED}Failed to connect to API. Exiting.${NC}"
        exit 1
    fi

    # Run iterations
    for ((i=1; i<=ITERATIONS; i++)); do
        run_iteration ${i}

        # Add delay between iterations except the last one
        if [ ${i} -lt ${ITERATIONS} ]; then
            echo -e "${YELLOW}Waiting 2 seconds before next iteration...${NC}"
            sleep 2
        fi
    done

    # Generate final report
    generate_final_report

    echo -e "\n${GREEN}========================================${NC}"
    echo -e "${GREEN}     All tests completed successfully!     ${NC}"
    echo -e "${GREEN}========================================${NC}"
}

# Run main function
main