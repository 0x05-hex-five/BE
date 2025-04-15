#!/bin/bash

set -e

APP_DIR=~/ismedi
cd $APP_DIR

echo "[1] docker 설치 확인"
if ! command -v docker &> /dev/null; then
    sudo yum update -y
    sudo amazon-linux-extras enable docker
    sudo yum install docker -y
    sudo systemctl start docker
    sudo systemctl enable docker
fi

echo "[2] docker-compose 설치 확인"
if ! command -v docker-compose &> /dev/null; then
  echo "Docker Compose 설치 중"
  sudo curl -L "https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
fi

echo "[3] 컨테이너 재시작"
DB_ROOT_PASSWORD=$1 DB_USER_PASSWORD=$2 docker compose pull
DB_ROOT_PASSWORD=$1 DB_USER_PASSWORD=$2 docker compose up -d

echo "[✅ 배포 완료]"