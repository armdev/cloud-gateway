ab -n 1000 -c 20 -p payload.json -T application/json http://localhost:8080/profile/api/v2/profile/register

ab -n 100 -c 10 http://localhost:8080/profile/api/v2/profile/fetch?userId=2025

