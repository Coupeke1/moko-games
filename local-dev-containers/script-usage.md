# Service Startup Scripts Documentation

This document explains how to use the provided scripts to start specific services or all services using **Docker Compose**. Both **PowerShell** and **Bash** versions of the script are supported.

---

## Available Services

The scripts can start the following services individually:

- `user`
- `session`
- `social`
- `games`

You can start multiple services at once or all services using the `--all` option.

---

## Options

### `--all`

- When provided, the scripts will start **all available services**.
- Overrides any individual services you specify.

---

## Usage

### PowerShell

```powershell
# Start specific services
.\run.ps1 -Services user,session

# Start all services
.\run.ps1 -All

# Start only infrastructre
.\run.ps1
```

#### Behavior:

- If no services are specified and -All is not provided, only infrastructure services will start. 
- If an unknown service is specified, the script will display an error and exit.

### Bash

```shell
# Start specific services
./run.sh user session

# Start all services
./run.sh --all

# Start only infrastructure
./run.sh
```

#### Behavior:

- If no services are provided and --all is not used, only infrastructure services will start. 
- If an invalid service name is provided, the script will display an error and exit.

## Notes
- Ensure Docker and Docker Compose are installed before running the scripts. 
- The scripts exit if an unknown service is provided to prevent misconfiguration. 
- The order of services doesn’t matter; the scripts will validate and start them in any order.

## Summary

| Option           | Description                                              |
|------------------|----------------------------------------------------------|
| `--all` / `-All` | Start all available services                             |
| `<services`      | Start specific service(s) (user, session, social, games) |
| None             | Start only infrastructure services                       |
