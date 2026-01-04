#!/usr/bin/env bash

ALL=false
SERVICES=()
EXCLUDE=()

# Parse arguments
while [ "$#" -gt 0 ]; do
    case "$1" in
        --all)
            ALL=true
            shift
            ;;
        --exclude)
            shift
            EXCLUDE+=("$1")
            shift
            ;;
        *)
            SERVICES+=("$1")
            shift
            ;;
    esac
done

VALID_SERVICES=("user" "session" "social" "games" "store" "communication" "tic-tac-toe" "checkers" "chess" "acl" "socket")

# If --all is provided, start with all services
if [ "$ALL" = true ]; then
    SERVICES=("${VALID_SERVICES[@]}")
    echo "--all provided. Starting ALL services: ${SERVICES[*]}"
else
    # If no services specified, start with all if excluding, otherwise infrastructure only
    if [ "${#SERVICES[@]}" -eq 0 ]; then
        if [ "${#EXCLUDE[@]}" -gt 0 ]; then
            SERVICES=("${VALID_SERVICES[@]}")
        else
            echo "No services specified. Only starting infrastructure."
        fi
    fi
fi

# Handle exclusions
if [ "${#EXCLUDE[@]}" -gt 0 ]; then
    # Validate excluded services
    for exclude_service in "${EXCLUDE[@]}"; do
        found=false
        for valid in "${VALID_SERVICES[@]}"; do
            if [ "$exclude_service" = "$valid" ]; then
                found=true
                break
            fi
        done
        if [ "$found" = false ]; then
            echo "Error: Unknown service to exclude: $exclude_service"
            echo "Valid services: ${VALID_SERVICES[*]}"
            exit 1
        fi
    done

    # Remove excluded services
    FILTERED_SERVICES=()
    for service in "${SERVICES[@]}"; do
        exclude=false
        for exclude_service in "${EXCLUDE[@]}"; do
            if [ "$service" = "$exclude_service" ]; then
                exclude=true
                break
            fi
        done
        if [ "$exclude" = false ]; then
            FILTERED_SERVICES+=("$service")
        fi
    done
    SERVICES=("${FILTERED_SERVICES[@]}")
    echo "Excluding services: ${EXCLUDE[*]}"
    if [ "${#SERVICES[@]}" -gt 0 ]; then
        echo "Starting services: ${SERVICES[*]}"
    else
        echo "No services to start. Only starting infrastructure."
    fi
fi

# Validate services and build profiles
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
