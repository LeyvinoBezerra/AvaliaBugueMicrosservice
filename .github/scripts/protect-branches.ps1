$ErrorActionPreference = "Stop"

$repository = gh repo view --json nameWithOwner --jq .nameWithOwner
if (-not $repository) {
    throw "Could not determine the current GitHub repository. Run gh auth login and execute this script from the repository."
}

$requiredChecks = @(
    "CI / validate-and-test"
)

foreach ($branch in @("develop", "main")) {
    $arguments = @(
        "api",
        "--method", "PUT",
        "-H", "Accept: application/vnd.github+json",
        "/repos/$repository/branches/$branch/protection",
        "-F", "required_status_checks[strict]=true",
        "-F", "enforce_admins=true",
        "-F", "required_pull_request_reviews[dismiss_stale_reviews]=true",
        "-F", "required_pull_request_reviews[required_approving_review_count]=1",
        "-F", "restrictions=null",
        "-F", "allow_force_pushes[enabled]=false",
        "-F", "allow_deletions[enabled]=false"
    )

    foreach ($check in $requiredChecks) {
        $arguments += @("-F", "required_status_checks[contexts][]=$check")
    }

    gh @arguments | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to protect branch '$branch'. Ensure gh is authenticated as a repository administrator."
    }

    Write-Host "Branch '$branch' is protected: PR required, one approval, CI check required, no force-push or deletion."
}
