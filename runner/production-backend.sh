#!/usr/bin/bash

# Get environment variables
source ./env.txt

# Run the production backend server

fuser -k 8999/tcp || true
java -jar production-backend/libs/backend-0.0.1-SNAPSHOT.jar --server.port=8999 --spring.profiles.active=production
