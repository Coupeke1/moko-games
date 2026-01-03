#!/usr/bin/env bash

export CI_REGISTRY=registry.gitlab.com
export CI_REGISTRY_IMAGE=registry.gitlab.com/kdg-ti/integratieproject-j3/teams-25-26/team22/socket-service

export CI_COMMIT_BRANCH=$(git branch --show-current)
export CI_COMMIT_REF_SLUG=$(git branch --show-current | tr '/' '-')
export CI_PIPELINE_SOURCE=push
export CI_COMMIT_TAG=""
export CI_PIPELINE_ID=local
export CI_COMMIT_SHA=$(git rev-parse HEAD)
export CI_PIPELINE_CREATED_AT=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

docker build \
  --build-arg CI_COMMIT_SHA="$CI_COMMIT_SHA" \
  --build-arg CI_PIPELINE_CREATED_AT="$CI_PIPELINE_CREATED_AT" \
  --build-arg CI_COMMIT_REF_NAME="$CI_COMMIT_BRANCH" \
  --build-arg CI_COMMIT_TAG="$CI_COMMIT_TAG" \
  --build-arg CI_PIPELINE_ID="$CI_PIPELINE_ID" \
  -t $CI_REGISTRY_IMAGE:temp-build .


if [ -n "$CI_COMMIT_TAG" ]; then
  TAGS="$CI_COMMIT_TAG latest"
elif [ "$CI_COMMIT_BRANCH" = "main" ]; then
  TAGS="main-latest"
else
  TAGS="branch-$CI_COMMIT_REF_SLUG"
fi

for t in $TAGS; do
  docker tag $CI_REGISTRY_IMAGE:temp-build $CI_REGISTRY_IMAGE:$t
  docker push $CI_REGISTRY_IMAGE:$t
done
