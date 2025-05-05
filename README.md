Thought for a few seconds


````markdown
# Inventory and Order Management API

A RESTful API for managing inventory, customers, and orders, built with **Spring Boot 3**, **JPA/Hibernate**, and **MySQL**. Users can register, authenticate with JWT, manage products, customers, and orders, and ensure stock integrity with optimistic locking and soft deletes. Metrics logging is provided via Spring Boot Actuator.

---

## 🌟 Features
- ✅ User registration and JWT‑based authentication  
- ✅ Manage products (create, list, update stock, soft delete)  
- ✅ Manage customers (create, list)  
- ✅ Create and retrieve orders with automatic stock deduction  
- ✅ Prevent overselling with transactional integrity and optimistic locking  
- ✅ Soft deletes for products and customers  
- ✅ Metrics logging (e.g. orders per day) via Spring Boot Actuator  
- 🔒 Secure endpoints with JWT  
- 🧪 Unit & integration tests for key functionality  

---

## 🛠 Technologies
- **Java 17**  
- **Spring Boot 3.2.0**  
- **Spring Security** (JWT Authentication)  
- **MySQL 8.0** / **H2** (testing)  
- **JPA/Hibernate**  
- **Maven**  
- **Lombok**  
- **Spring Boot Actuator** (metrics)  

---

## ⚙️ Setup

### Prerequisites
- Java 17  
- Maven 3.8+  
- MySQL 8.0+  

### Installation

1. **Clone the repository**  
   ```bash
   git clone https://gitlab.com/your-username/inventory-order-api.git
   cd inventory-order-api
````

2. **Create MySQL database**

   ```sql
   CREATE DATABASE inventory_order_db;
   ```

3. **Configure application**
   Edit `src/main/resources/application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/inventory_order_db?useSSL=false&serverTimezone=UTC
       username: your_db_username
       password: your_db_password

   jwt:
     secret: your-256‑bit‑secret‑key
     expiration: 86400000  # 24 hours in milliseconds
   ```

4. **Build and run**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The API will start at `http://localhost:8080`.

---

## 📚 API Documentation

### Authentication

| Endpoint             | Method | Description              | Request Body                            |
| -------------------- | :----: | ------------------------ | --------------------------------------- |
| `/api/auth/register` |  POST  | Register a new user      | `{"username":"user","password":"pass"}` |
| `/api/auth/login`    |  POST  | Log in and get JWT token | `{"username":"user","password":"pass"}` |

### Product Management

| Endpoint                    | Method | Description           | Request Body                                                 |
| --------------------------- | :----: | --------------------- | ------------------------------------------------------------ |
| `/api/products`             |  POST  | Create a new product  | `{"name":"Laptop","sku":"LAP001","price":999.99,"stock":10}` |
| `/api/products`             |   GET  | List all products     | —                                                            |
| `/api/products/{sku}`       |   GET  | Get product by SKU    | —                                                            |
| `/api/products/{sku}/stock` |   PUT  | Update stock quantity | Query param: `?stock=5`                                      |
| `/api/products/{sku}`       | DELETE | Soft delete a product | —                                                            |

### Customer Management

| Endpoint              | Method | Description           | Request Body                                     |
| --------------------- | :----: | --------------------- | ------------------------------------------------ |
| `/api/customers`      |  POST  | Create a new customer | `{"name":"John Doe","email":"john@example.com"}` |
| `/api/customers`      |   GET  | List all customers    | —                                                |
| `/api/customers/{id}` |   GET  | Get customer by ID    | —                                                |

### Order Management

| Endpoint                             | Method | Description                | Request Body                                               |
| ------------------------------------ | :----: | -------------------------- | ---------------------------------------------------------- |
| `/api/orders`                        |  POST  | Create a new order         | `{"customerId":1,"items":[{"sku":"LAP001","quantity":2}]}` |
| `/api/customers/{customerId}/orders` |   GET  | List orders for a customer | —                                                          |
| `/api/orders/{id}`                   |   GET  | Get order details          | —                                                          |

---

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Example cURL Requests

* **Register a user**

  ```bash
  curl -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"john","password":"secret"}' \
    http://localhost:8080/api/auth/register
  ```

* **Log in to get JWT**

  ```bash
  curl -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"john","password":"secret"}' \
    http://localhost:8080/api/auth/login
  ```

* **Create a product**

  ```bash
  curl -X POST \
    -H "Authorization: Bearer <TOKEN>" \
    -H "Content-Type: application/json" \
    -d '{"name":"Laptop","sku":"LAP001","price":999.99,"stock":10}' \
    http://localhost:8080/api/products
  ```

* **Create an order**

  ```bash
  curl -X POST \
    -H "Authorization: Bearer <TOKEN>" \
    -H "Content-Type: application/json" \
    -d '{"customerId":1,"items":[{"sku":"LAP001","quantity":2}]}' \
    http://localhost:8080/api/orders
  ```

---

## 🗄️ Database Schema

**Tables & Columns**

* **users**: `id`, `username`, `password`, `role`, `created_at`, `updated_at`
* **products**: `id`, `name`, `sku`, `price`, `stock`, `version`, `deleted`, `created_at`, `updated_at`
* **customers**: `id`, `name`, `email`, `deleted`, `created_at`, `updated_at`
* **orders**: `id`, `customer_id`, `total`, `order_date`, `created_at`, `updated_at`
* **order\_items**: `id`, `order_id`, `product_id`, `quantity`, `unit_price`

> **Notes:**
>
> * Soft deletes via `deleted` flag
> * Optimistic locking via `version` on `products`
> * Unique constraints on `sku` (products) and `email` (customers)

---

## 🔒 Security

* Passwords hashed with **BCrypt**
* JWT expiration: **24 hours**
* All endpoints (except `/api/auth/**`) require a valid JWT

---

## 📈 Metrics

* Enabled via **Spring Boot Actuator**
* Access metrics at `/actuator/metrics` (e.g. `orders.created`)
* Custom order metrics available after setup

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch

   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes

   ```bash
   git commit -m "Add your feature"
   ```
4. Push to your branch

   ```bash
   git push origin feature/your-feature
   ```
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 📝 Additional Notes

* **Performance:**

   * `@Transactional` on order creation with `Isolation.SERIALIZABLE` for stock integrity.
   * Optimistic locking to prevent concurrent stock updates.
   * Indexes on `sku` and `email` for fast lookups.

* **Testing:**

   * Unit tests cover product CRUD, stock updates, and order workflows.
   * Integration tests via Testcontainers MySQL.

* **Future Improvements:**

   * Add Swagger/OpenAPI for live documentation
   * Integrate Redis for caching frequently accessed data
   * Implement more granular custom metrics (e.g. stock levels)

---

🚀 **Happy coding!**

```
```
