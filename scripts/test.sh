#!/usr/bin/env sh
set -eu

scripts/compile.sh
javac -d out -cp out src/test/java/GameTest.java
java -cp out Main --self-test
java -ea -cp out GameTest