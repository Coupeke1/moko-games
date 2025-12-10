# Service Startup Scripts Guide

This guide explains how to use the startup scripts to manage local development services using Docker Compose. The scripts automatically handle service orchestration and validation.

**Table of Contents**
- [Prerequisites](#prerequisites)
- [Available Services](#available-services)
- [Quick Start](#quick-start)
- [Detailed Usage](#detailed-usage)
- [Common Use Cases](#common-use-cases)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

- **Docker** and **Docker Compose** installed and running
- Scripts are located in the `local-dev-containers` directory
- Both PowerShell (`.ps1`) and Bash (`.sh`) versions are available

---

## Available Services

The scripts can orchestrate the following services:

### Platform Services
- `user` - User management service
- `session` - Session management service
- `social` - Social features service
- `games` - Core games service
- `store` - Store backend service
- `communication` - Notifications and messages

### Game Services
- `tic-tac-toe` - Tic-Tac-Toe game backend
- `checkers` - Checkers game backend

### Frontend
- `frontend_platform` - Main moko platform
- `frontend_tic_tac_toe` - Tic-Tac-Toe game frontend
- `frontend_checkers` - Checkers game frontend

### Black Box (Chess) Services
- `chess` - Frontend, backend of chess game and related infrastructure (Black Box)

### Infrastructure
Infrastructure services (database, messaging, etc.) are **always started** regardless of which services you specify.

---

## Quick Start

### PowerShell
```powershell
# Infrastructure only
.\run.ps1

# Specific services
.\run.ps1 -Services user,session

# All services
.\run.ps1 -All

# All services except some
.\run.ps1 -All -Exclude checkers,tic-tac-toe
```

### Bash
```bash
# Infrastructure only
./run.sh

# Specific services
./run.sh user session

# All services
./run.sh --all

# All services except some
./run.sh --all --exclude checkers --exclude tic-tac-toe
```

---

## Detailed Usage

### PowerShell Script (`run.ps1`)

#### Starting Infrastructure Only
```powershell
.\run.ps1
```
Starts only the infrastructure (no platform or game services).

#### Starting Specific Services
```powershell
# Start one service with infrastructure
.\run.ps1 -Services user

# Start multiple services (comma-separated)
.\run.ps1 -Services user,session,social
```

#### Starting All Services
```powershell
.\run.ps1 -All
```
Starts all available services: user, session, social, games, store, communication, tic-tac-toe, checkers, chess, frontend-platform, frontend-tic-tac-toe, frontend_checkers.

#### Starting All Services Except Some
```powershell
# Exclude one service
.\run.ps1 -All -Exclude checkers

# Exclude multiple services (comma-separated)
.\run.ps1 -All -Exclude checkers,tic-tac-toe
```

#### Parameter Details
| Parameter   | Type         | Description                                                                                        |
|-------------|--------------|----------------------------------------------------------------------------------------------------|
| `-Services` | String array | Comma-separated list of services to start                                                          |
| `-All`      | Switch       | Start all available services (overrides `-Services`)                                               |
| `-Exclude`  | String array | Comma-separated list of services to exclude (works with `-All` or when starting with all services) |

---

### Bash Script (`run.sh`)

#### Starting Infrastructure Only
```bash
./run.sh
```
Starts only the infrastructure (no platform or game services).

#### Starting Specific Services
```bash
# Start one service with infrastructure
./run.sh user

# Start multiple services (space-separated)
./run.sh user session social
```

#### Starting All Services
```bash
./run.sh --all
```
Starts all available services: services: user, session, social, games, store, communication, tic-tac-toe, checkers, chess, frontend-platform, frontend-tic-tac-toe, frontend_checkers.

#### Starting All Services Except Some
```bash
# Exclude one service
./run.sh --all --exclude checkers

# Exclude multiple services
./run.sh --all --exclude checkers --exclude tic-tac-toe
```

#### Argument Details
| Argument                   | Description                                    |
|----------------------------|------------------------------------------------|
| `--all`                    | Start all available services                   |
| `--exclude <service-name>` | Exclude a service (can be used multiple times) |
| `<service-name>`           | Individual service names (space-separated)     |
| None                       | Start infrastructure only                      |

---

## Common Use Cases

### Run everything except a specific game
```powershell
# PowerShell
.\run.ps1 -All -Exclude checkers
```
```bash
# Bash
./run.sh --all --exclude checkers
```

### Run all services except multiple games
```powershell
# PowerShell
.\run.ps1 -All -Exclude checkers,tic-tac-toe
```
```bash
# Bash
./run.sh --all --exclude checkers --exclude tic-tac-toe
```

### Run only platform services (exclude games and chess)
```powershell
# PowerShell
.\run.ps1 -All -Exclude tic-tac-toe,checkers,chess,frontend-tic-tac-toe
```
```bash
# Bash
./run.sh --all --exclude tic-tac-toe --exclude checkers --exclude chess --exclude frontend-tic-tac-toe
```

---

### Startup Flow
1. Script validates all requested services
2. Infrastructure services start automatically
3. Specified services start via Docker Compose profiles
4. Containers run in foreground (use `Ctrl+C` to stop)

### Service Order
The scripts start services in any order—Docker Compose handles dependencies automatically.

### Error Handling
- **Unknown Service**: Script displays error message with valid service names and exits
- **Invalid Configuration**: Script exits before starting containers to prevent misconfiguration

---

## Troubleshooting

### Issue: "Unknown service" error
**Solution**: Check the service name against the available services list. Service names are case-sensitive.

```powershell
# ❌ Wrong
.\run.ps1 -Services User

# ✅ Correct
.\run.ps1 -Services user
```

### Issue: Containers not starting
**Check**:
1. Docker daemon is running: `docker ps`
2. Docker Compose version: `docker compose version` (requires Docker Compose v2)
3. No port conflicts with existing containers

### Issue: Services starting too slowly
**Note**: First-time startup takes longer due to image downloads. Subsequent starts are faster.

### Issue: Permission denied (Bash)
**Solution**: Make script executable:
```bash
chmod +x run.sh
```

---

## Script Composition

The scripts use Docker Compose with these configuration files:
- `infrastructure.yaml` - Core infrastructure services
- `platform-services.yaml` - Platform services (user, session, social, games)
- `game-services.yaml` - Game services (tic-tac-toe, checkers)

Each service is organized as a Docker Compose profile for flexible orchestration.
