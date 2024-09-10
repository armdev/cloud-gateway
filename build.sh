#!/usr/bin/env bash

set -e

echo "Build The World"

mvn clean package -U -Dmaven.test.skip=true

