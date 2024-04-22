#!/bin/bash

# Проверяем, передан ли аргумент
if [ $# -eq 0 ]; then
  echo "Usage: $0 <folder_path>"
  exit 1
fi

# Получаем путь к папке из аргумента
FOLDER_PATH=$1

# URL для отправки запроса
URL="http://ваш_сервер/api/tasks"

# Данные о задаче
COMPETITION_ID=123
TASK="Описание задачи"
USER="Имя пользователя"
SOLUTION_NAME="Название решения"
PROGRAM=$(cat "$FOLDER_PATH/ваш_файл")

# Формирование тела запроса в формате JSON
DATA=$(cat <<EOF
{
    "competitionId": $COMPETITION_ID,
    "task": "$TASK",
    "user": "$USER",
    "solutionName": "$SOLUTION_NAME",
    "program": "$PROGRAM"
}
EOF
)

# Отправка POST-запроса с использованием cURL
curl -X POST \
     -H "Content-Type: application/json" \
     -d "$DATA" \
     "$URL"
