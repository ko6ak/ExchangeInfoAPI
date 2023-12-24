## ExchangeInfoApi

Название рублевой валюты - `RUB`</br>
Внешний API - https://exchangerate.host/ </br>
Для работы требуется указать `access key` в application.properties 

1. Склонировать репозиторий: `git clone https://github.com/ko6ak/ExchangeInfoApi.git`
2. Перейти в папку с программой: `cd  /ExchangeInfoApi`
3. Запустить docker-образ (файл docker.yml находится в корне проекта): `docker compose -f docker.yml up`
4. Собрать приложение: `gradle build`