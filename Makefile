

AWS_ACCOUNT_ID := 742460038063
AWS_DEFAULT_REGION := eu-west-3
AWS_ECR_DOMAIN := $(AWS_ACCOUNT_ID).dkr.ecr.$(AWS_DEFAULT_REGION).amazonaws.com
GIT_SHA := $(shell git rev-parse HEAD)
BUILD_IMAGE := $(AWS_ECR_DOMAIN)/spring-fraud-service
BUILD_TAG := $(if $(BUILD_TAG),$(BUILD_TAG),latest)

# Default goal
.DEFAULT_GOAL := build

# --------------------
# Build JAR with Maven
# --------------------
build:
	mvn clean package -DskipTests

# --------------------
# Docker image build
# --------------------
build-image:
	docker buildx build --platform "linux/amd64" --tag "$(BUILD_IMAGE):$(GIT_SHA)-build" --target "build" .
	docker buildx build --cache-from "$(BUILD_IMAGE):$(GIT_SHA)-build" --platform "linux/amd64" --tag "$(BUILD_IMAGE):$(GIT_SHA)" .

build-image-login:
	aws ecr get-login-password --region $(AWS_DEFAULT_REGION) | docker login --username AWS --password-stdin $(AWS_ECR_DOMAIN)

build-image-push: build-image-login
	docker image push $(BUILD_IMAGE):$(GIT_SHA)

build-image-pull: build-image-login
	docker image pull $(BUILD_IMAGE):$(GIT_SHA)

# --------------------
# Run migrations inside Docker
# --------------------
build-image-migrate:
	# Wait for MongoDB before starting (dockerize)
	$env:DOCKERIZE_URL = "tcp://localhost:27017"; \
	docker container run --entrypoint "dockerize" --network "host" --rm $(BUILD_IMAGE):$(GIT_SHA) -timeout 30s -wait $$env:DOCKERIZE_URL; \
	docker container run --rm --network "host" $(BUILD_IMAGE):$(GIT_SHA) java -jar app.jar --spring.profiles.active=migration

# --------------------
# Promote Image
# --------------------
build-image-promote:
	docker image tag $(BUILD_IMAGE):$(GIT_SHA) $(BUILD_IMAGE):$(BUILD_TAG)
	docker image push $(BUILD_IMAGE):$(BUILD_TAG)

# --------------------
# Docker Compose commands
# --------------------
down:
	docker compose down --remove-orphans --volumes

up: down
	docker compose up --detach

# --------------------
# Deploy script
# --------------------
deploy:
	$env:AWS_ACCOUNT_ID=$(AWS_ACCOUNT_ID); \
	$env:AWS_DEFAULT_REGION=$(AWS_DEFAULT_REGION); \
	$env:AWS_ECR_DOMAIN=$(AWS_ECR_DOMAIN); \
	.\deploy.ps1

# --------------------
# Run local JAR
# --------------------
start: build
	java -jar target/*.jar
