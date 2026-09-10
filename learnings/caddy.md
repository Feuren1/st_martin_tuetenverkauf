# Learnings 11.09

# How the pipeline works

1. When a commit is pushed to Main the pipeline is triggered
2. The build.yml contains all steps for building the pipeline. The pipeline:
   * Uses Maven to build the project and run all tests
   * Builds the docker image from the docker file
   * Pushes that image into the container registry
   * Then a smoke test runs actually spinning up the container and checks whether backend is able to talk to the database and health checks pass

# Caddy
Caddy is a server capable of different must have functionalties for webservers right out of the box.

1. Able to Retrieve HTTPS certificates automatically
2. Acts as a reverse proxy in this project being the only entry point into the server
3. Able to do load balancing and rate limiting and other things for the future
4. Recieves request of the domain and reverse proxies them to the correct service/port