#!/bin/sh

echo "Building frontend..."
(cd frontend && npm install && grunt $TASK)

echo "Copying frontend resources..."
mkdir -p backend/src/main/resources/META-INF/resources
cp -R frontend/dist/* backend/src/main/resources/META-INF/resources

echo "Building backend..."
(cd backend && mvn -q -P$ADDITIONAL_PROFILES ${GOAL:-clean install} jacoco:report)

echo "Done."
