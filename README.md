### Run Monolith Application
```bash
mvn -f monolith spring-boot:run
```

### Авторизация
```
http://localhost:8080/api/v1/auth/login
{
    "username": "admin",
    "password": "admin"
}
```

### Пересоздание базы
```shell
docker compose down -v
docker compose up -d
```

###
```shell
mvn clean
cd gateway && mvn compile && cd ..
cd user-service && mvn compile && cd ..
cd category-service && mvn compile && cd ..
```
