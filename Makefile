.PHONY: build-prod up-prod down-prod, restart-prod, build-dev up-dev down-dev logs

# Production commands
build-prod:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml build

up-prod:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up

down-prod:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml down

logs:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f

restart-prod: down-prod build-prod up-prod

# Development commands
build-dev:
	docker-compose -f docker-compose.yml build

up-dev:
	docker-compose -f docker-compose.yml up

down-dev:
	docker-compose -f docker-compose.yml down

# Danger zone: delete volumes
# reset-db:
# 	@echo "WARNING: This will delete Postgres volume. Continue in 5s..."
# 	@sleep 10
# 	docker volume rm -f $$(docker volume ls -q --filter name=*_db_data || true)