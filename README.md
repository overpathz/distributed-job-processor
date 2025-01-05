# Scalable distributed job processor service

## How to run

### Prerequisites
You have to create k8s cluster
1) Install Minikube: `brew install minikube`
2) Install kubectl: `brew install kubectl`
3) Start the cluster: `minikube start`

### Application itself
1) `git clone https://github.com/overpathz/distributed-job-processor.git`
2) <b>Intermediate step.</b> Your local Docker daemon and the Docker daemon inside Minikube are separate. By default, images built on your local machine are not available inside Minikube’s environment. <br/>
So, execute this command in your project root: `eval $(minikube -p minikube docker-env)`
3) Build app image: `docker build -t distributed-job-processor:0.0.1 .`
4) Run all other services (postgres + observability stack): `docker-compose up -d`
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


