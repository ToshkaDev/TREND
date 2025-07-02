#!/bin/sh
. ./.env
docker exec -it $(docker ps -qf "name=db") psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"