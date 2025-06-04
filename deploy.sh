#!/usr/bin/env bash
set -e

if [ -z "$1" ]; then
  echo "Ошибка: не указано сообщение коммита."
  echo "Использование: ./deploy.sh \"Ваше сообщение коммита\""
  exit 1
fi

COMMIT_MSG="$1"

echo "=== Сборка проекта ==="
./gradlew clean assembleDebug --quiet

echo "=== git add . ==="
git add .

echo "=== git commit -m \"$COMMIT_MSG\" ==="
git commit -m "$COMMIT_MSG"

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "=== git push origin $CURRENT_BRANCH ==="
git push origin "$CURRENT_BRANCH"

echo "=== Деплой завершён: коммит и пуш в ветку '$CURRENT_BRANCH' ==="
