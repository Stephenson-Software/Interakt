#!/usr/bin/env bash
# Builds the browser version of Interakt into build/web: index.html plus interakt.jar,
# which web/index.html runs with CheerpJ. Maven compiles the application (and fills the
# version into interakt.properties); the vendored Ponder, EnvironmentLib and Gson jars are
# then unpacked next to it, so the page downloads one jar. EnvironmentLib is compiled for
# Java 16, so the page starts CheerpJ's Java 17 runtime. The test classes, which Maven
# compiles into the same output directory, are left out of the jar.
set -euo pipefail
cd "$(dirname "$0")/.."

rm -rf build/browser-classes build/web
mkdir -p build/browser-classes build/web

mvn -B -q -DskipTests compile

for dependency in gson-2.8.0 Ponder-v0.14-alpha-1 EnvironmentLib-v0.3-alpha-3; do
  unzip -q -o "dependencies/$dependency.jar" -d build/browser-classes
done
rm -rf build/browser-classes/META-INF

cp -r target/classes/. build/browser-classes/
rm -rf build/browser-classes/dansapps/interakt/tests

javac --release 16 -cp build/browser-classes -d build/browser-classes web/java/BrowserMain.java
jar cf build/web/interakt.jar -C build/browser-classes .
cp web/index.html build/web/index.html
echo "Built build/web:"
ls -l build/web
