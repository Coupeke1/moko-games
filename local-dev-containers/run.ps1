param (
    [switch]$All,
    [string[]]$Services
)

# If --all is provided, override services
if ($All) {
    $Services = @("user", "session", "social", "games", "store", "tic-tac-toe", "checkers", "chess")
    Write-Host "--all provided. Starting ALL services: $($Services -join ', ')"
} else {
    if (-not $Services) {
        Write-Host "No services specified. Only starting infrastructure."
    } else {
        Write-Host "Starting selected services: $($Services -join ', ')"
    }
}

# Validate services and build profiles
$ValidServices = @("user", "session", "social", "games", "store", "tic-tac-toe", "checkers", "chess")
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