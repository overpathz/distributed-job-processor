# Scalable distributed job processor service

## Description
This service is a Spring Boot application built to handle high volumes of payment transactions. It’s designed to scale effortlessly within a Kubernetes cluster, allowing multiple nodes to process payment intents in parallel without stepping on each other’s toes. It uses PostgreSQL with FOR UPDATE SKIP LOCKED to safely fetch and lock batches of payment intents for processing. This approach ensures smooth operation and avoids conflicts when multiple nodes are working simultaneously. The app also uses Spring’s scheduling to process transactions in regular batches, making it fast and reliable for large-scale payment workflows. Monitoring can be set up with Prometheus to keep an eye on performance and system health.

## Features

- Batch Processing: Efficiently processes payment intents in configurable batch sizes.
- Concurrency Control: Ensures no duplicate processing using row-level locking with FOR UPDATE SKIP LOCKED.
- Scalability: Easily scales horizontally with multiple Kubernetes replicas.
- Resilience: Handles node crashes gracefully with retry mechanisms and timeout handling.
- Monitoring: Integrated Prometheus metrics for real-time monitoring and alerting.
- Dockerized: Containerized application for easy deployment and management.

## How to run

### Prerequisites
You have to create k8s cluster
1) Install Minikube: `brew install minikube`
2) Install kubectl: `brew install kubectl`
3) Start the cluster: `minikube start`

### Application itself
1) `git clone https://github.com/overpathz/distributed-job-processor.git`
2) Run other services (postgres + observability stack): `docker-compose up -d`
3) <b>Intermediate step.</b> Your local Docker daemon and the Docker daemon inside Minikube are separate. By default, images built on your local machine are not available inside Minikube’s environment. <br/>
So, execute this command in your project root: `eval $(minikube -p minikube docker-env)`
4) Build app image: `docker build -t distributed-job-processor:0.0.1 .`
5) Deploy the application to Kubernetes: `kubectl apply -f deployment.yml` 

### List of commands you may need

#### Delete k8s deployment
- kubectl delete deployment distributed-job-processor

#### Check logs in realtime
- kubectl logs -f <pod name> (it changes dynamically)

#### Rebuild docker image for our service
- docker build -t distributed-job-processor:0.0.1 .
(dot is current context (Dockerfile))

#### List docker volumes
- docker volume list

#### Remove database volume
- docker volume rm db-data

#### Deploy k8s deployment
- kubectl apply -f deployment.yml

#### List pods
- kubectl get pods


