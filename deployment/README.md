# House of Culture - deployment

## Docker compose  

To run you need installed docker. Then type:
- `docker login --username orzeldev` (and use password)
- `docker-compose up` (on Windows) or `docker compose up` (on Linux/MacOS)

### Current configuration
Configuration includes 3 services: 
- backend (port 8080), 
- mysql database (port 3310),
- mailhog (smtp server on port 1025, additionaly you can access web UI on port 8025). 
