#!/usr/bin/env bash

set -e

cd "`dirname $0`"
cd ..

./gradlew clean build test publishAllPublicationsToProjectRepoRepository
