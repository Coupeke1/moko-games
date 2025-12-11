param (
    [switch]$All,
    [string[]]$Services,
    [string[]]$Exclude
)

$ValidServices = @("user", "session", "social", "games", "store", "tic-tac-toe", "checkers", "chess","communication")

# If --all is provided, start with all services
if ($All) {
    $Services = $ValidServices.Clone()
    Write-Host "--all provided. Starting ALL services: $($Services -join ', ')"
} else {
    # If no services specified, start with all (they'll be filtered by exclude)
    if (-not $Services) {
        if ($Exclude) {
            $Services = $ValidServices.Clone()
        } else {
            Write-Host "No services specified. Only starting infrastructure."
            $Services = @()
        }
    }
}

# Handle exclusions
if ($Exclude) {
    # Validate excluded services
    foreach ($service in $Exclude) {
        if ($ValidServices -notcontains $service) {
            Write-Host "Error: Unknown service to exclude: $service"
            Write-Host "Valid services: $($ValidServices -join ', ')"
            exit 1
        }
    }
    # Remove excluded services
    $Services = $Services | Where-Object { $Exclude -notcontains $_ }
    Write-Host "Excluding services: $($Exclude -join ', ')"
    if ($Services) {
        Write-Host "Starting services: $($Services -join ', ')"
    } else {
        Write-Host "No services to start. Only starting infrastructure."
    }
}

# Validate services and build profiles
$Profiles = @()

foreach ($service in $Services) {
    if ($ValidServices -contains $service) {
        $Profiles += "--profile"
        $Profiles += $service
    } else {
        Write-Host "Error: Unknown service: $service"
        Write-Host "Valid services: $($ValidServices -join ', ')"
        exit 1
    }
}

# Run docker compose
docker compose @Profiles up
