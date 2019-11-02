*For Shopify : I chose to showcase this project because it's not to simple and has a lot of things to discuss about (business, scaling, unit tests, integration tests).*

# Trading Api

This project is composed of three module. The `Application` module is used to launch the `Stock api` and the `Trading api`. The `Stock api` contains fake data representing the price of differents stocks in time. The `Trading api` use the `Stock api` data and enable user to make trades.

## Dependencies

You need `Java 8` and the latest version of `maven` to run this project

## Run the project

```
mvn clean install
mvn exec:java -pl application
```

<<<<<<< HEAD
## Run the tests

```
mvn test
```

## API Documentation

By default the trading api will launch at `localhost:8080`

### Open account

#### request

POST `/accounts`

```
{
  "investorId": 0::long,
  "investorName": ""::string,
  "email":""::string,
  "credits": 0.00::float,
}
```

#### response

HTTP 201 created

`Headers`

```
Location: /accounts/<accountNumber::string>
```

---

### View account details

#### request

GET `/accounts/<accountNumber::string>`

#### response

HTTP 200 OK

`Headers`

```
Location: /accounts/<accountNumber::string>
```

---

### Buy action

#### request

POST `/accounts/<accountNumber::string>/transactions`

```
{
  "type": "BUY",
  "date": "2018-08-21T15:23:20.142Z",
  "stock": {
    "market": ""::string,
    "symbol": ""::string
  },
  "quantity": 0::long
}
```

#### response

HTTP 201 created

`Headers`

```
Location: /accounts/<accountNumber::string>/transactions/<transactionNumber::UUID>
```

---

### Sell action

#### request

POST `/accounts/<accountNumber::string>/transactions`

```
{
  "type": "SELL",
  "date": "2018-08-21T15:23:20.142Z",
  "stock": {
    "market": ""::string,
    "symbol": ""::string
  }
  "transactionNumber": ""::UUID, 
  "quantity": 0::long
}
```

#### response

HTTP 201 Created

`Headers`

```
Location: /accounts/<accountNumber::string>/transactions/<transactionNumber::UUID>
```

---

### View transaction details

#### request

GET `/accounts/<accountNumber::string>/transactions/<transactionNumber::UUID>`

#### response

HTTP 200 OK

`Headers`

```
{
  "transactionNumber": ""::UUID, 
  "type": "BUY|SELL",
  "date": "2018-08-21T15:23:20.142Z",
  "fees": 0.00::float,
  "stock": {
    "market": ""::string,
    "symbol": ""::string,
  },
  "purchasedPrice": 0.00::float, 
  "quantity": 0::long
}
```
