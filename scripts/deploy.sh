#!/bin/bash

set -e

APP_DIR=~/ismedi
cd "$APP_DIR"

if ! command -v docker &> /dev/null; then
    sudo apt update -y
    sudo apt install -y docker.io
    sudo systemctl start docker
    sudo systemctl enable docker
fi
echo "[1] docker 설치 완료"

if ! command -v docker-compose &> /dev/null; then
  curl -L "https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
fi
echo "[2] docker-compose 설치 완료"

echo "[3] 컨테이너 재시작"
sudo docker-compose down
DB_ROOT_PASSWORD="${DB_ROOT_PASSWORD}" DB_USER_PASSWORD="${DB_USER_PASSWORD}" sudo docker-compose pull
DB_ROOT_PASSWORD="${DB_ROOT_PASSWORD}" DB_USER_PASSWORD="${DB_USER_PASSWORD}" sudo docker-compose up -d

echo "[✅ 배포 완료]"