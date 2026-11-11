# Szachy

Dwie osoby przy jednej przeglądarce. Białe i czarne grają na zmianę.
Serwer trzyma partię i liczy ruchy, przeglądarka tylko pokazuje planszę i wysyła kliknięcia.

Pion na ostatniej linii staje się hetmanem. Działa roszada i bicie w przelocie.
Aplikacja nie ogłasza mata, pata ani remisu.

## Docker

```bash
docker compose up --build
```

Plansza: http://localhost:3000

Debugger backendu: `localhost:5005` (JDWP). Aplikacja wstaje od razu, debugger można podłączyć w dowolnej chwili. W Cursorze: Run and Debug, konfiguracja „Docker: backend”.

Start wstrzymany do podłączenia debugera:

```bash
# PowerShell
$env:DEBUG_SUSPEND = "y"
docker compose up
```

## Lokalnie

Potrzebne Java 21, Maven i Node.js.

```bash
cd backend
mvn spring-boot:run
```

```bash
cd frontend
npm install
npm run dev
```

Plansza: http://localhost:5173
