#!/usr/bin/env bash

HOST="localhost"
PORT="5443"
USER="user"
PASSWORD="password"
DB_NAME="moko"
SCHEMA="user_service"

SQL_FILE="$(dirname "$0")/data.sql"

export PGPASSWORD="$PASSWORD"

psql -h "$HOST" -p "$PORT" -U "$USER" -d "$DB_NAME" -f "$SQL_FILE"

unset PGPASSWORD
