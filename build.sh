#!/bin/sh

echo "Building frontend..."
(cd frontend && npm install && npm run build) || exit 1

echo "Copying frontend resources..."
cp -Rv frontend/dist/* backend/src/main/resources/META-INF/resources || exit 1

echo "Building backend (profiles: bintray,$ADDITIONAL_PROFILES , goals: $GOAL)..."
(cd backend && mvn -DmongoHosts=localhost -U -Pbintray,$ADDITIONAL_PROFILES clean ${GOAL:-clean install} jacoco:report) || exit 1

echo "Done."
