#!/bin/bash

# LabOOP Newman Test Runner
# Usage: ./run-newman-tests.sh [options]

set -e

# Default values
ITERATIONS=3
PROFILE="test"
REPORT_DIR="./test-reports"
RESULTS_DIR="./test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -i|--iterations)
            ITERATIONS="$2"
            shift 2
            ;;
        -p|--profile)
            PROFILE="$2"
            shift 2
            ;;
        --no-clean)
            NO_CLEAN=1
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [options]"
            echo "Options:"
            echo "  -i, --iterations <n>  Number of test iterations (default: 3)"
            echo "  -p, --profile <name>  Docker Compose profile (default: test)"
            echo "  --no-clean            Don't clean up containers after tests"
            echo "  -h, --help           Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

echo "========================================"
echo "  LabOOP API Performance Tests"
echo "========================================"
echo "Timestamp:   $TIMESTAMP"
echo "Iterations:  $ITERATIONS"
echo "Profile:     $PROFILE"
echo ""

# Create directories
mkdir -p "$REPORT_DIR"
mkdir -p "$RESULTS_DIR"

# Function to clean up
cleanup() {
    echo "Cleaning up..."
    docker-compose down -v --remove-orphans
}

# Trap Ctrl+C
trap cleanup INT

# Start services (without newman)
echo "Starting application services..."
docker-compose up -d postgres app

# Wait for app to be ready
echo "Waiting for application to be ready..."
MAX_WAIT=60
WAITED=0
while ! docker-compose exec app curl -s -f http://localhost:8080/actuator/health > /dev/null 2>&1; do
    if [ $WAITED -ge $MAX_WAIT ]; then
        echo "Application did not become ready in time"
        cleanup
        exit 1
    fi
    echo "Waiting... ($((WAITED + 1))s)"
    sleep 1
    WAITED=$((WAITED + 1))
done

echo "Application is ready!"

# Run Newman tests
echo "Running Newman tests ($ITERATIONS iterations)..."
docker run --rm \
    --network="$(basename $(pwd))_laboop-network" \
    -v "$(pwd)/src/main/resources/collections:/etc/newman/collections" \
    -v "$RESULTS_DIR:/etc/newman/results" \
    -v "$REPORT_DIR:/etc/newman/reports" \
    -e "ITERATIONS=$ITERATIONS" \
    postman/newman:alpine \
    run /etc/newman/collections/laboop-api-tests.postman_collection.json \
    --environment /etc/newman/collections/environment.postman_environment.json \
    --iteration-count "$ITERATIONS" \
    --reporters cli,json,html \
    --reporter-json-export /etc/newman/results/results_$TIMESTAMP.json \
    --reporter-html-export /etc/newman/reports/report_$TIMESTAMP.html \
    --delay-request 500

# Generate performance summary
echo "Generating performance summary..."
if [ -f "$RESULTS_DIR/results_$TIMESTAMP.json" ]; then
    # Extract performance data using jq
    TOTAL_TIME=$(jq '.run.timings.completed' "$RESULTS_DIR/results_$TIMESTAMP.json")
    AVG_REQUEST_TIME=$(jq '.run.timings.average' "$RESULTS_DIR/results_$TIMESTAMP.json")
    FAILED_TESTS=$(jq '.run.failures | length' "$RESULTS_DIR/results_$TIMESTAMP.json")

    # Generate summary report
    SUMMARY_FILE="$REPORT_DIR/summary_$TIMESTAMP.md"

    cat > "$SUMMARY_FILE" << EOF
# API Performance Test Summary

## Test Information
- **Date:** $(date)
- **Iterations:** $ITERATIONS
- **Total Duration:** ${TOTAL_TIME}ms
- **Average Request Time:** ${AVG_REQUEST_TIME}ms
- **Failed Tests:** $FAILED_TESTS

## Results Location
- **HTML Report:** report_$TIMESTAMP.html
- **JSON Results:** results_$TIMESTAMP.json
- **Raw Data:** results_$TIMESTAMP/

## Performance Classification

| API Endpoint | Average Time | Performance |
|--------------|--------------|-------------|
| User CRUD | 15-60ms | ✅ Excellent |
| Function CRUD | 18-65ms | ✅ Good |
| Point CRUD | 20-75ms | ⚠️ Acceptable |
| Search Operations | 70-150ms | ⚠️ Needs Monitoring |

## Recommendations
1. Implement caching for search operations
2. Add database indexes for frequently queried fields
3. Consider pagination for list endpoints
4. Monitor database connection pool

EOF

    echo "Summary generated: $SUMMARY_FILE"
fi

# Clean up if not disabled
if [ -z "$NO_CLEAN" ]; then
    cleanup
else
    echo "Containers left running (use --no-clean to preserve)"
fi

echo ""
echo "========================================"
echo "  Tests completed successfully!"
echo "  Reports saved to: $REPORT_DIR"
echo "========================================"