### Task description
Design and implement a RESTful API (including data model and the backing implementation) formoney transfers between accounts.

Explicit requirements:
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget aboutrequirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require apre-installed container/server).
7. Demonstrate with tests that the API works as expected.


### Architecture
The application consists of three layers: infrastructure, application, domain.
The infrastructure-level can be dependent on application-level and domain-level.
The application-level can have domain-level dependencies.
The domain-level is independent of other levels.

### Transfer money operation description
Why did I do the non-blocking, non-synchronizing money transfer operation?
The money transfer operation consists of two parts:
- to withdraw money from one account
- to deposit money to another account

Each of these operations is implemented thread-safety through using of AtomicReference and the compareAndSet algorithm.
As soon as we can withdraw money from one account (we passed the validation check that money enough and correctly set the new available amount), we can deposit money to the other account.

In my naive algorithm, I exclude such life situations that the account on which the money is deposited can be closed, and in this case the money will not reach the this account.
To handle such case, it is enough for us to return the money to the account from which they were withdrawed. 
We can assume that the account from which the money was withdrawed was also closed. In this case, how to return the money to the client will be managed by a separate banking mechanism or department.


### Available Services
I created only those endpoints that I need to debug and test the main operation transfer money.

#### GET /user/{userName} 
**Usage:** get user by user name

**Body response:**

     {
            "id": "c6376c4a-6b57-44ce-ac01-996b339b2ca5",
            "name": "user1",
            "accounts": [
                {
                    "id": "8cd78541-b682-4ab6-a3f9-ebef94adf69c",
                    "userId": "c6376c4a-6b57-44ce-ac01-996b339b2ca5",
                    "title": "Account 1",
                    "amount": 950.11
                },
                {
                    "id": "e98e1dd4-20e4-4c00-ae3e-0cd99df99316",
                    "userId": "c6376c4a-6b57-44ce-ac01-996b339b2ca5",
                    "title": "Account 2",
                    "amount": 1799.46
                }
            ]
        }        

#### POST /users
**Usage:** for create new user

**Body request:**

     {
     	"name": "user1"
     }

#### POST /users/accounts
**Usage:** for create new user's account

**Body request:**

     {
     	"userId": "8cd78541-b682-4ab6-a3f9-ebef94adf69c",
     	"title": "Account 1"
     }

#### GET /users/accounts/{accountId}
**Usage:** get user's account by id

**Body response:**

     {
         "id": "e807fc5e-4546-4cfe-9ea4-7c69aed5c05f",
         "userId": "48d82288-d616-4072-9c58-07f8b91d1917",
         "title": "Account 1",
         "amount": 0
     }

#### GET /users/accounts/{userId}/{accountTitle}
**Usage:** get user's account by user's id and account's title

**Body response:**

     {
         "id": "e807fc5e-4546-4cfe-9ea4-7c69aed5c05f",
         "userId": "48d82288-d616-4072-9c58-07f8b91d1917",
         "title": "Account 1",
         "amount": 0
     }

#### PUT /users/accounts/deposit
**Usage:** deposit some money on user's account

**Body request:**

     {
     	"accountId": "e807fc5e-4546-4cfe-9ea4-7c69aed5c05f",
     	"amount": 1000.56     	
     }

#### PUT /users/accounts/withdraw
**Usage:** withdraw some money on user's account

**Body request:**

     {
     	"accountId": "e807fc5e-4546-4cfe-9ea4-7c69aed5c05f",
     	"amount": 150.31     	
     }

#### PUT /transfer
**Usage:** transfer some money from one user's account to another

**Body request:**

     {
     	"accountIdFrom": "48d82288-d616-4072-9c58-07f8b91d1917",
     	"accountIdTo": "e807fc5e-4546-4cfe-9ea4-7c69aed5c05f",
     	"amount": 333.78     	
     }   

### Http Status
- 200 OK: The request has succeeded
- 201 Created: The resource was created success
- 400 Bad Request: The request could not be understood by the server 
- 404 Not Found: The requested resource cannot be found
- 405 Method Not Allowed
- 500 Internal Server Error: The server encountered an unexpected condition 

### Compile and run
- Compile with command: mvn clean install
- Go to the directory target: cd target
- Run executable jar-file with command: java -jar money-transfer-1.0-SNAPSHOT-shaded.jar

### Demonstration API
For demonstrating working with API you can use the postman-collection located in the directory "postman-collection".

