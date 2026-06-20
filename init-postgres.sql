-- Run this script as PostgreSQL superuser before starting the app
-- psql -U postgres -f init-postgres.sql

CREATE DATABASE warehouse_db;
CREATE USER warehouse_user WITH ENCRYPTED PASSWORD 'warehouse_pass';
GRANT ALL PRIVILEGES ON DATABASE warehouse_db TO warehouse_user;

\c warehouse_db
GRANT ALL ON SCHEMA public TO warehouse_user;
