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

### Game Services
- `tic-tac-toe` - Tic-Tac-Toe game
- `checkers` - Checkers game

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
```

### Bash
```bash
# Infrastructure only
./run.sh

# Specific services
./run.sh user session

# All services
./run.sh --all
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
Starts all available services: user, session, social, games, tic-tac-toe, checkers.

#### Parameter Details
| Parameter | Type | Description |
|-----------|------|-------------|
| `-Services` | String array | Comma-separated list of services to start |
| `-All` | Switch | Start all available services (overrides `-Services`) |

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
Starts all available services: user, session, social, games, tic-tac-toe, checkers.

#### Argument Details
| Argument | Description |
|----------|-------------|
| `--all` | Start all available services |
| `<service-name>` | Individual service names (space-separated) |
| None | Start infrastructure only |

---

## Script Behavior

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
