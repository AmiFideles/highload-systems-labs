### ReRun
```bash
docker compose down -v
docker compose up -d --build
```

### Stop
```bash
docker compose down -v
```

### Авторизация
```
http://localhost:8080/api/v1/auth/login
{
    "username": "admin",
    "password": "admin"
}
```
