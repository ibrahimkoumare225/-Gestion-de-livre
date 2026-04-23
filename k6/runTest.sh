#!/bin/bash

# K6 Performance Test Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
BASE_URL="http://localhost:8080"
SCRIPT="./k6/index.js"
REPORT_DIR="./k6-reports"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --url)
            BASE_URL="$2"
            shift 2
            ;;
        --script)
            SCRIPT="$2"
            shift 2
            ;;
        --report-dir)
            REPORT_DIR="$2"
            shift 2
            ;;
        --help)
            echo "Usage: ./runTest.sh [options]"
            echo ""
            echo "Options:"
            echo "  --url <url>         Base URL for API (default: http://localhost:8080)"
            echo "  --script <path>     K6 script path (default: ./k6/index.js)"
            echo "  --report-dir <dir>  Report output directory (default: ./k6-reports)"
            echo "  --help              Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Check if K6 is installed
if ! command -v k6 &> /dev/null; then
    echo -e "${RED}K6 is not installed. Please install K6 first.${NC}"
    echo "Visit: https://k6.io/docs/getting-started/installation/"
    exit 1
fi

# Check if script exists
if [ ! -f "$SCRIPT" ]; then
    echo -e "${RED}Script not found: $SCRIPT${NC}"
    exit 1
fi

# Create report directory
mkdir -p "$REPORT_DIR"

# Print test information
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}K6 Performance Test${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "${YELLOW}Base URL: $BASE_URL${NC}"
echo -e "${YELLOW}Script: $SCRIPT${NC}"
echo -e "${YELLOW}Report Directory: $REPORT_DIR${NC}"
echo -e "${YELLOW}Timestamp: $TIMESTAMP${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Run K6 test
echo -e "${YELLOW}Starting K6 test...${NC}"
k6 run \
    --out csv="$REPORT_DIR/results_${TIMESTAMP}.csv" \
    -e BASE_URL="$BASE_URL" \
    "$SCRIPT"

TEST_EXIT_CODE=$?

echo ""
echo -e "${GREEN}========================================${NC}"

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ K6 test completed successfully${NC}"
    echo -e "${GREEN}Report: $REPORT_DIR/results_${TIMESTAMP}.csv${NC}"
else
    echo -e "${RED}✗ K6 test failed with exit code: $TEST_EXIT_CODE${NC}"
fi

echo -e "${GREEN}========================================${NC}"

exit $TEST_EXIT_CODE
