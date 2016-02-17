#!/bin/sh

echo "Building frontend..."
(cd frontend && npm install && gulp $TASK) || exit 1

echo "Copying frontend resources..."
mkdir -p backend/src/main/resources/META-INF/resources || exit 1
cp -R frontend/dist/* backend/src/main/resources/META-INF/resources || exit 1

echo "Building backend..."
(cd backend && mvn -q -P$ADDITIONAL_PROFILES ${GOAL:-clean install} jacoco:report) || exit 1

echo "Done."
