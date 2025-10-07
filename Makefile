default_target: run

run:
	docker compose down && docker compose up -d
.PHONY: run
