# GitHub Workflows

This repository provides three GitHub Actions workflows:

- **build.yml** – Builds the project and pushes a Docker image to Amazon ECR whenever changes are pushed to the `main` branch.
- **deploy.yml** – Manually triggered deployment of the Kubernetes manifests using a provided kubeconfig.
- **infra.yml** – Manually triggered workflow that creates or removes AWS infrastructure via CloudFormation.

## Required Secrets

Configure the following secrets in your repository settings so the workflows can access AWS and your Kubernetes cluster:

| Secret Name | Used By | Description |
|-------------|---------|-------------|
| `AWS_ACCESS_KEY_ID` | build.yml, infra.yml | IAM user access key for AWS operations |
| `AWS_SECRET_ACCESS_KEY` | build.yml, infra.yml | Secret key associated with `AWS_ACCESS_KEY_ID` |
| `AWS_REGION` | build.yml, infra.yml | AWS region for ECR and CloudFormation |
| `ECR_REGISTRY` | build.yml | ECR registry URL (e.g. `123456789012.dkr.ecr.us-east-1.amazonaws.com`) |
| `KUBECONFIG` | deploy.yml | Base64‑encoded kubeconfig for your Kubernetes cluster |
| `DB_PASSWORD` | infra.yml | Database master password used when creating the RDS instance |
| `HOSTED_ZONE` | infra.yml (optional) | Domain name for Route53 records (defaults to `example.com.`) |

Create these secrets in **Settings → Secrets and variables → Actions**.

The `infra.yml` workflow reads `deploy/cloudformation/config.json` if present in the repository to determine whether to create or delete the stack and whether termination protection should be enabled.
