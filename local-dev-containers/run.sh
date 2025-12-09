#!/usr/bin/env bash

ALL=false
SERVICES=()

# Parse arguments
while [ "$#" -gt 0 ]; do
    case "$1" in
        --all)
            ALL=true
            shift
            ;;
        *)
            SERVICES+=("$1")
            shift
            ;;
    esac
done

# If --all is provided, override services
if [ "$ALL" = true ]; then
    SERVICES=("user" "session" "social" "games" "store" "communication" "tic-tac-toe" "checkers" "chess" "frontend-platform" "frontend-tic-tac-toe")
    echo "--all provided. Starting ALL services: ${SERVICES[*]}"
else
    if [ "${#SERVICES[@]}" -eq 0 ]; then
        echo "No services specified. Only starting infrastructure."
    else
        echo "Starting selected services: ${SERVICES[*]}"
    fi
fi

# Validate services and build profiles
VALID_SERVICES=("user" "session" "social" "games" "store" "communication" "tic-tac-toe" "checkers" "chess"  "frontend-platform" "frontend-tic-tac-toe")
PROFILES=()

for service in "${SERVICES[@]}"; do
    found=false
    for valid in "${VALID_SERVICES[@]}"; do
        if [ "$service" = "$valid" ]; then
            found=true
            break
        fi
    done

    if [ "$found" = true ]; then
        PROFILES+=("--profile" "$service")
    else
        echo "Error: Unknown service: $service"
        echo "Valid services: ${VALID_SERVICES[*]}"
        exit 1
    fi
done

# Run docker compose
docker compose "${PROFILES[@]}" up
