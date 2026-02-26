# Library App
> Web Application for book management

## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Features](#features)
* [Screenshots](#screenshots)
* [Setup](#setup)
* [Usage](#usage)
* [Project Status](#project-status)
* [Room for Improvement](#room-for-improvement)


## General Information
- A web app that helps you manage your books. You can register/log in/log out of your account. You can search for books and add or remove them from your list.
- The application was created for the purpose of a recruitment task 



## Technologies Used
- Java 17
- Spring boot 3.5.11
- Postgres 16 alpine
- React 19.2.0
- TypeScript 5.9.3


## Features
- Spring Security
- Jwt
- Pagination
- PWA
- Tests
- Docker
- Connection with Google Api
- Rateliming


## Screenshots
Login page<br> <img width="1031" height="624" alt="obraz" src="https://github.com/user-attachments/assets/d043cd05-c2c7-43ba-ac33-750e8decbe7d" /> <br>
Register page <br><img width="896" height="450" alt="obraz" src="https://github.com/user-attachments/assets/e8029223-a579-4868-a80f-c23d6b117a1d" /><br>
Search page <br><img width="787" height="741" alt="obraz" src="https://github.com/user-attachments/assets/bd8c8a88-9064-4db1-bb35-6bf591304846" /><br>
My books page<br> <img width="1183" height="1125" alt="obraz" src="https://github.com/user-attachments/assets/6df12eed-e8eb-481e-aef7-f0eef7370fa2" /><br>


## Setup
Docker Desktop / Docker Engine required\

CMD: 
git clone <<link_do_repo>>
cd <<nazwa_repo>>
docker compose up --build<br>


Fronend works on http//:localhost:5173<br>
Backend works on http//:localhost:8080<br>
All containers are in docker


## Usage
The frontend (React + Nginx) communicates with the backend through endpoints starting with /api.
Nginx forwards all /api/* requests to the backend (http://backend:8080)

Backend (Spring Boot) handles business logic,authorizes users using JWT,
communicates with the PostgreSQL database,acts as a proxy for the Google Books API

PostgreSQL stores user data, hashed passwords, read lists


The Google Books API is called exclusively by the backend—the frontend does not communicate with it directly.


## Project Status
_Complete_

## Room for Improvement
Room for improvement:
- More unit tests
- Better UI
