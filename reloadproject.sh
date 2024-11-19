docker compose down -v
# Очистка Docker
docker rm -f $(docker ps -aq)
docker rmi -f $(docker images -aq)
docker system prune -a --volumes --force