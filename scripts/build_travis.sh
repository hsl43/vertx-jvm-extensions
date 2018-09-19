#!/bin/bash

echo "## Beginning..."

./gradlew clean :core:test :reactivex:test :rxjava:test
