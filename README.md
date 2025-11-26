# Gym App

Aplikacja webowa dla klubu fitness pozwalająca zarządzać zajęciami, rezerwacjami oraz użytkownikami. Projekt realizuje wymagania przedmiotu *Zaawansowane programowanie w Javie* oraz może posłużyć jako baza do projektu wieloosobowego ocenianego pod kątem jakości kodu i historii Git.

## Stos technologiczny

- Spring Boot 4 (Web MVC, Data JPA, Security, Thymeleaf)
- H2/PostgreSQL (w zależności od profilu uruchomieniowego)
- Spring Security z rolami `USER` i `ADMIN`
- Springdoc OpenAPI 3 (Swagger UI na `/swagger-ui.html`)
- Maven, Lombok, Docker (opcjonalnie)

## Funkcjonalności

- Rejestracja/logowanie użytkowników, podział uprawnień oraz kokpit administratora.
- Publiczna lista zajęć wraz z liczbą wolnych miejsc, możliwość zapisu i anulowania rezerwacji.
- Panel administratora z CRUD-em na zajęciach, liczbą użytkowników/rezerwacji.
- REST API (`/api/classes`, `/api/bookings`) dla klienta mobilnego/SPA.
- Dziennik ćwiczeń – prywatne wpisy użytkownika z ćwiczeniami, seriami, ciężarami (dodawanie, edycja, usuwanie).
- Inicjalizacja kont demo i przykładowych zajęć (`DataInitializer`).

## Wymagania systemowe

- Java 17+
- Maven 3.9+
- (Opcjonalnie) Docker Desktop do uruchomienia bazy produkcyjnej.

## Uruchomienie lokalne

```bash
mvn spring-boot:run
# albo
mvn clean package
java -jar target/gym_app-0.0.1-SNAPSHOT.jar
```

Dane logowania testowych kont:

| Rola   | Login  | Hasło    |
|--------|--------|----------|
| ADMIN  | admin  | admin123 |
| USER   | user   | user123  |

## Testy

- Testy jednostkowe i integracyjne uruchamia komenda `mvn test`.
- Profil `test` korzysta z bazy w pamięci (`jdbc:h2:mem:testdb`).

## Dokumentacja API

- Po uruchomieniu aplikacji: `http://localhost:8080/swagger-ui.html`
- Specyfikacja OpenAPI dostępna pod `http://localhost:8080/v3/api-docs`.

## Struktura repozytorium i praca zespołowa

- Rekomendowana strategia: `main` (stabilny kod) + gałęzie funkcjonalne (`feature/...`).
- Każdy merge powinien przechodzić przez Pull Request z code review.
- README lub osobny dokument powinien opisywać wkład poszczególnych osób oraz harmonogram (np. link do Jira/Notion).

## Pomysły na dalszy rozwój

- Integracja z płatnościami online, listy oczekujących, powiadomienia e-mail/SMS.
- Eksport/import planów treningowych do JSON/XML.
- Testcontainers w integracjach, pipeline CI/CD, Docker Compose.

