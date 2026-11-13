# Práctica S11 — Persistencia y Comunicación Multi-componente en Kubernetes

Stack: Java 17 · Spring Boot 3.2 · Spring WebFlux · Spring Data Reactive MongoDB

## Arquitectura

```
Cliente → NodePort :30080 → Backend (Spring WebFlux) → ClusterIP :27017 → MongoDB
                                 ↑                            ↑
                           ConfigMap + Secret              Secret + PVC
```

## Despliegue rápido

> **Nota:** `k8s/03-db-secret.yaml` no está en el repo (contiene credenciales). Créalo antes de desplegar:
> ```bash
> kubectl create secret generic db-secret \
>   --from-literal=MONGO_INITDB_ROOT_USERNAME=admin \
>   --from-literal=MONGO_INITDB_ROOT_PASSWORD=Admin1234! \
>   -n practica-s11
> ```

```bash
docker build -t backend-webflux:1.0 .
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-mongo-pvc.yaml
kubectl apply -f k8s/02-app-configmap.yaml
# Crear el secret con el comando de arriba
kubectl apply -f k8s/04-mongo-stack.yaml
kubectl apply -f k8s/05-backend-stack.yaml
```

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/items` | Lista todos los items |
| POST | `/api/items` | Crea un item |
| GET | `/actuator/health` | Estado de salud |
