.PHONY: run verify package docker-up docker-down

run:
	./mvnw spring-boot:run

verify:
	./mvnw -B verify

package:
	./mvnw -B package

docker-up:
	docker compose up --build

docker-down:
	docker compose down
