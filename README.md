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

## Zadania: złoto, energia, ulepszenia, umiejętności

Wspólny kontrakt jest taki: przed partią wydajesz 10 złota, w partii po własnym ruchu dostajesz 1 energię, a poniżej są zadania — szkielet ekonomii, ulepszenia, trzy nowe figury i 100 umiejętności.

### Kontrakt dla wszystkich zadań

**Złoto.** Każdy gracz zaczyna przygotowanie z 10 złota. Wydaje je naraz, w tajemnicy, a potem obaj odkrywają zakupy. Niewydane złoto przepada. Złotem kupuje się trzy rzeczy: trwałe ulepszenie konkretnej figury ze stanu początkowego, jedną kopię umiejętności do talii albo wymianę figury na jedną z trzech nowych. W trakcie partii złota już nie ma.

**Energia.** Obaj zaczynają partię z 0. Energię dostaje ten, kto właśnie wykonał ruch szachowy: +1 na koniec swojej tury. Nie ma górnego limitu. Na początku swojej tury, po rozliczeniu statusów, gracz może zrobić jedną akcję energetyczną: dokupić jedno ulepszenie figurze stojącej na szachownicy albo zagrać jedną kopię umiejętności z talii. Potem wykonuje zwykły ruch i dopiero on daje +1. Umiejętność oznaczona „zamiast ruchu” kończy turę bez ruchu i bez tego +1.

**Różnica walut.** Ulepszenie ma dwie ceny. Złoto przypina je przed partią, energia przypina je w turze; efekt jest ten sam i trwa do końca partii. Umiejętności energią nie dokupisz: złoto wkłada kopię do talii, energia tę kopię zagrywa i zużywa. Tę samą umiejętność wolno kupić kilka razy, jeśli starcza złota. Cena złota umiejętności to połowa kosztu energii zaokrąglona w górę, najwyżej 10. Przy koszcie 1–2 wychodzi 1 złoto, przy 20 wychodzi 10.

**Jedna figura, wiele nakładek.** Różne ulepszenia mogą siedzieć na jednej figurze. Drugiej kopii tego samego ulepszenia na tej samej figurze kupić nie wolno. Ulepszenie pasuje tylko do typu podanego w zadaniu. Wskrzeszona figura wraca bez ulepszeń. Pionek, który się promuje, traci ulepszenia pionka. Zbita figura traci je razem ze zejściem z planszy.

**Tura, po kolei.** Na początku tury schodzą liczniki statusów i trucizna zabija, zanim gracz cokolwiek wyda. Potem opcjonalna akcja energetyczna, potem ruch szachowy, potem +1 energii. Po akcji i po ruchu własny król ma stać poza szachem. Jeśli gracz jest w szachu, albo akcja ten szach zdejmuje, albo zdejmuje go następujący ruch. „Zamiast ruchu” w szachu jest legalne tylko wtedy, gdy opis umiejętności sam zdejmuje szach.

**Słownik.** „Na 2 tury” przy zamrożeniu oznacza dwie najbliższe tury właściciela figury: nie może nią ruszyć, a figura dalej atakuje i wolno ją zbić. Zamrożenie cudzej figury obejmuje więc jej dwie kolejne tury. „Za 2 tury” przy jadzie liczy tury tego, kto zatruł: figura schodzi na początku jego drugiej następnej tury, jeśli wciąż jest na planszy. Zejście z jadu i usunięcie umiejętnością wrzucają figurę do grobu właściciela, ale nie są zbiciem: nie dają premii za bicie i nie tworzą bicia w przelocie. „Figura się ruszyła” znaczy, że król i wieża tracą roszadę, a pionek traci dwukrok. Grób to stos zdjętych figur gracza, od najnowszej. Króla obejmują tylko te umiejętności, które mówią o królu wprost. Nowe figury wchodzą wyłącznie w przygotowaniu: wymieniona figura schodzi przed partią i do grobu nie trafia.

### Ekonomia — pierwsze zadania

Resztę zadań sklepu (interfejs, kolejność odkrywania, podgląd) zostawiamy na później. Te sześć stawia reguły, na których stoją umiejętności.

**Portfel złota.** Gracz w przygotowaniu wydaje dokładnie zakupione pozycje z budżetu 10, a próba zejścia poniżej zera jest odrzucana. Po odkryciu zakupów niewydane złoto znika i partia startuje już bez złota.

**Portfel energii.** Po każdym wykonanym ruchu szachowym właściciel ruchu dostaje 1 energię, a akcja „zamiast ruchu” tego przychodu nie daje. Wydanie większej ilości, niż gracz ma, jest odrzucane, a energia przechodzi między turami.

**Talia.** Kupno umiejętności za złoto dokłada jedną kopię do talii tego gracza, a zagranie w turze pobiera energię i kopię usuwa. Zagranie umiejętności, której w talii nie ma, jest odrzucane.

**Ulepszenie w dwóch momentach.** To samo ulepszenie można przypiąć albo złotem do figury stanu początkowego, albo energią do figury stojącej w turze. Drugie takie samo ulepszenie na tej figurze jest odrzucane, a figura zeszła z planszy zabiera ulepszenia ze sobą.

**Jedna akcja w turze.** W turze wolno albo dokupić jedno ulepszenie, albo zagrać jedną umiejętność, a potem następuje ruch szachowy. Kolejność rozliczenia jest stała: statusy, akcja, ruch, przychód energii.

**Szach i grób.** Silnik ma powiedzieć, czy król gracza jest w szachu, bo od tego zależy legalność akcji i ruchu. Osobno trzyma grób każdego gracza w kolejności zejścia, tak żeby wskrzeszenia miały skąd brać figurę.

### Ulepszenia figur

Ceny są zapisane jako złoto / energia. Ruch ulepszonej figury jest legalny na tych samych zasadach co zwykły ruch: własne figury zastawia pole, cudze można bić, własny król po ruchu stoi poza szachem.

**Ludożerca.** 4 / 8. Hetman może dodatkowo wejść na pole zajęte przez własną figurę inną niż król i ta figura schodzi do twojego grobu. To jest ruch hetmana, więc hetman „się ruszył”, a premii za bicie przeciwnika ten ruch nie daje.

**Wypad skoczka.** 3 / 6. Skoczek oprócz skoku w L może skoczyć o 3 pola prosto do przodu: białe zwiększają rząd, czarne zmniejszają, kolumna ta sama. Przeskakuje figury po drodze i ląduje na pustym polu albo na cudzej figurze.

**Krótki krok skoczka.** 3 / 6. Skoczek oprócz L może wejść o dokładnie 1 pole w dowolnym z ośmiu kierunków, na puste albo na cudzą figurę. To zwykły krok na sąsiednie pole, bez dalszego ślizgu.

**Ukos wieży.** 3 / 5. Wieża oprócz swojego ślizgu może ruszyć się o dokładnie 1 pole na skos, na puste albo na cudzą figurę. Dalej po skosie nie idzie.

**Orto goniec.** 3 / 5. Goniec oprócz swoich przekątnych może ruszyć się o dokładnie 1 pole w pionie albo w poziomie, na puste albo na cudzą figurę. Dalej w pionie i w poziomie nie idzie.

**Długi król.** 6 / 12. Król może iść o 1 albo o 2 pola w dowolnym z ośmiu kierunków; przy 2 polach pole pośrednie jest puste, a oba pola — pośrednie i docelowe — są wolne od ataku. Na polu startowym kliknięcie pola odległego o 2 w poziomie, przy zachowanym prawie do roszady i wieży na miejscu, pozostaje roszadą; dwukrok jest zwykłym ruchem króla dopiero wtedy, gdy roszada na to pole jest niedostępna. Wieża przy dwukroku stoi w miejscu.

**Pion na wprost.** 2 / 4. Pionek bije także o 1 pole prosto do przodu, obok zwykłego bicia na skos. Wejście na puste pole do przodu zostaje ruchem bez bicia.

**Pion w bok.** 2 / 4. Pionek może wejść o 1 pole w bok na puste pole i tym ruchem nie bije. Na ostatnim rzędzie ten krok promuje tak samo jak wejście do przodu.

**Długi krok pionka.** 3 / 7. Ten pionek może iść o 2 pola do przodu, gdy oba pola są puste, także po tym, jak już się ruszał. Takie przejście o 2 otwiera przeciwnikowi bicie w przelocie.

**Podwójny skok.** 5 / 10. Skoczek w jednym ruchu wykonuje dwa skoki w L; pierwszy ląduje na polu pustym, drugi na pustym albo na cudzej figurze. Szach sprawdza się dopiero po drugim skoku.

**Tarcza pionka.** 4 / 8. Pierwsze bicie tego pionka nie dochodzi do skutku: ruch przeciwnika jest wtedy nielegalny, a tarcza schodzi. Drugie bicie działa już normalnie.

**Wieża przez osłonę.** 5 / 10. Wieża może dodatkowo bić, gdy między nią a cudzą figurą w rzędzie lub kolumnie stoi dokładnie jedna figura, i ląduje na tej cudzej figurze. Zwykły ślizg wieży zostaje.

**Goniec przez własną.** 4 / 8. Goniec może w jednym ślizgu minąć dokładnie jedną własną figurę, ale nie wolno mu na niej stanąć ani minąć drugiej. Bicie cudzej figury za tą osłoną jest dozwolone.

**Hetman jak skoczek.** 5 / 11. Ten hetman ma jeden skok w L na całą partię, obok zwykłych ruchów hetmana. Po użyciu zostaje sam hetman.

**Król jak skoczek.** 5 / 11. Ten król ma jeden skok w L na całą partię, na pole nieatakowane, i tym skokiem traci roszadę. Po użyciu zostaje zwykłym królem, razem z Długim królem, jeśli też go ma.

**Złoty pion.** 3 / 6. Gdy ten pionek zbije cudzą figurę, właściciel dostaje za ten ruch 2 energie zamiast 1. Zbicie w przelocie też uruchamia premię.

### Trzy nowe figury

**Armata.** Armata bez bicia ślizga się o dowolną liczbę pól po rzędzie i kolumnie, gdy droga jest pusta. Bije tylko wtedy, gdy między nią a cudzą figurą stoi dokładnie jedna dowolna figura, a armata ląduje na tej cudzej figurze; po skosie nie bije i bez osłony nie bije. W przygotowaniu zastępuje jedną wieżę za 4 złota.

**Skorpion.** Na puste pole skorpion wchodzi wyłącznie o 1 pole na skos, a bije wyłącznie o 1 pole w pionie albo w poziomie. Nie ślizga się i nie przeskakuje. W przygotowaniu zastępuje jednego gońca za 3 złota.

**Łowca.** Łowca przeskakuje figury i ląduje dokładnie o 3 pola w pionie lub poziomie albo dokładnie o 2 pola na skos, na pustym polu albo na cudzej figurze. Na własną figurę nie wchodzi. W przygotowaniu zastępuje jednego skoczka za 4 złota.

### Umiejętności

Koszt podany przy nazwie jest kosztem energii. Złoto za włożenie kopii do talii wynika z kontraktu. Cel, którego opis nie obejmuje, jest ruchem nielegalnym: zła figura, zajęte pole, własny król w szachu po akcji.

1. **Szron.** Koszt 3. Jedna cudza figura inna niż król nie może się ruszyć przez 2 tury swojego właściciela; dalej atakuje i wolno ją zbić.
2. **Szron krótki.** Koszt 2. Jak Szron, na 1 turę właściciela.
3. **Szron długi.** Koszt 6. Jak Szron, na 4 tury właściciela.
4. **Zimny rząd.** Koszt 8. Wszystkie cudze figury inne niż król w wybranym rzędzie dostają Szron na 2 tury.
5. **Zimna kolumna.** Koszt 8. Jak Zimny rząd, dla wybranej kolumny.
6. **Zimna przekątna.** Koszt 9. Gracz wskazuje pole i jedną z dwóch linii gońca przez to pole; cudze figury inne niż król na tej linii dostają Szron na 2 tury.
7. **Zimny kolor.** Koszt 12. Wszystkie cudze figury inne niż król na polach koloru wskazanego pola dostają Szron na 1 turę.
8. **Oddech zimy.** Koszt 14. Wszystkie cudze figury inne niż król dostają Szron na 1 turę.
9. **Wieczna zima.** Koszt 20. Wszystkie cudze figury inne niż król dostają Szron na 2 tury.
10. **Kajdany pionów.** Koszt 4. Wszystkie cudze pionki dostają Szron na 2 tury.
11. **Kajdany lekkich.** Koszt 7. Wszystkie cudze skoczki i gońce dostają Szron na 1 turę.
12. **Kajdany wież.** Koszt 9. Wszystkie cudze wieże dostają Szron na 2 tury.
13. **Zamrożenie hetmana.** Koszt 11. Wybrany cudzy hetman dostaje Szron na 3 tury.
14. **Odwilż.** Koszt 2. Jedna własna figura traci zamrożenie i w tej turze może już ruszyć.
15. **Bursztyn.** Koszt 5. Własna figura inna niż król dostaje Szron na 1 własną turę, a przez najbliższą turę przeciwnika nie wolno jej zbić.
16. **Most lodowy.** Koszt 7. W wybranym rzędzie wszystkie figury poza królami, z obu stron, dostają Szron na 1 turę swojego właściciela.
17. **Jad.** Koszt 4. Cudza figura inna niż król schodzi do grobu właściciela na początku twojej drugiej następnej tury, jeśli wciąż stoi na planszy.
18. **Jad błyskawiczny.** Koszt 8. Jak Jad, ale schodzi na początku twojej najbliższej następnej tury.
19. **Jad powolny.** Koszt 2. Jak Jad, ale schodzi na początku twojej trzeciej następnej tury.
20. **Zaraza rzędu.** Koszt 10. Każda cudza figura inna niż król w wybranym rzędzie dostaje Jad.
21. **Zaraza kolumny.** Koszt 10. Jak Zaraza rzędu, dla wybranej kolumny.
22. **Jad pionków.** Koszt 6. Każdy cudzy pionek dostaje Jad.
23. **Odtrutka.** Koszt 3. Własna figura traci truciznę, zanim licznik zdąży zejść do zera.
24. **Zarażenie.** Koszt 9. Gdy twoja trucizna zdejmie figurę, jedna cudza figura inna niż król na polu sąsiadującym z miejscem śmierci dostaje Jad. Ten nowy jad już kolejnego sąsiada nie zaraża.
25. **Wyrok.** Koszt 15. Cudza figura inna niż król schodzi od razu do grobu swojego właściciela. Ruch jest legalny, gdy po zdjęciu twój król stoi poza szachem.
26. **Ścięcie wieży.** Koszt 12. Jak Wyrok, wyłącznie na cudzą wieżę.
27. **Ścięcie gońca.** Koszt 8. Jak Wyrok, wyłącznie na cudzego gońca.
28. **Ścięcie skoczka.** Koszt 8. Jak Wyrok, wyłącznie na cudzego skoczka.
29. **Strat pionka.** Koszt 3. Jak Wyrok, wyłącznie na cudzego pionka.
30. **Zniszczenie własne.** Koszt 1. Własna figura inna niż król schodzi do twojego grobu. Wolno ją potem wskrzesić.
31. **Danina.** Koszt 1. Własny pionek schodzi do twojego grobu, a ty natychmiast dostajesz 2 energie.
32. **Popiół.** Koszt 2. Własny skoczek albo goniec schodzi do grobu, a ty natychmiast dostajesz 4 energie.
33. **Przeskok.** Koszt 5. Własna figura inna niż król przechodzi na puste pole oddalone o co najwyżej 3 kroki króla i liczy się jako figura, która się ruszyła.
34. **Teleport.** Koszt 8. Własna figura inna niż król przechodzi na dowolne puste pole i liczy się jako figura, która się ruszyła.
35. **Zamiana.** Koszt 6. Dwie własne figury inne niż król wymieniają się polami i obie liczą się jako ruszone.
36. **Odsunięcie.** Koszt 4. Cudza figura inna niż król przechodzi o 1 krok króla na puste pole i liczy się jako ruszona.
37. **Przyciągnięcie.** Koszt 4. Cudza figura inna niż król przechodzi na jedyne puste sąsiednie pole, które zmniejsza odległość królewską do twojego króla. Gdy takiego pola nie ma albo jest ich kilka, akcja jest odrzucana.
38. **Wymiana biegunów.** Koszt 9. Własna i cudza figura, żadna nie jest królem, wymieniają się polami i obie liczą się jako ruszone.
39. **Skok króla.** Koszt 14. Własny król przechodzi na puste, nieatakowane pole w odległości co najwyżej 3 i traci roszadę. Wolno z tego skorzystać, będąc w szachu, gdy na nowym polu szach znika.
40. **Bramka.** Koszt 7. Własny skoczek przechodzi na dowolne puste pole tego samego koloru szachownicy i liczy się jako ruszony.
41. **Wycofanie.** Koszt 3. Własna figura wraca na pole, z którego weszła w poprzednim twoim ruchu, gdy tamten ruch nie był biciem, a pole jest puste.
42. **Przestawienie pionka.** Koszt 2. Własny pionek wchodzi o 1 pole w bok na puste pole, nie bije i nie promuje, i liczy się jako ruszony.
43. **Katapulta.** Koszt 6. Własny pionek przechodzi na dowolne puste pole swojej kolumny poza ostatnim rzędem, bez promocji, i liczy się jako ruszony.
44. **Ucieczka wieży.** Koszt 5. Własna wieża przechodzi na dowolne puste pole swojego rzędu lub swojej kolumny, figury po drodze jej nie blokują, i traci roszadę.
45. **Lustro.** Koszt 11. Wszystkie twoje figury odbijają się jednocześnie względem osi między kolumnami d i e (a z h, b z g, c z f, d z e). Akcja jest legalna, gdy każde pole docelowe jest wolne albo zajęte przez twoją figurę biorącą udział w tym odbiciu, a król po odbiciu stoi poza szachem; król i wieże, które zmieniły pole, tracą roszadę.
46. **Odepchnięcie.** Koszt 8. Każda cudza figura inna niż król w wybranym rzędzie przesuwa się o jeden rząd w stronę swojej promocji, jeśli to pole jest puste. Figura, której pole jest zajęte albo leży poza planszą, zostaje.
47. **Ściąganie.** Koszt 7. Własna figura inna niż król przechodzi na puste pole sąsiadujące z własnym królem i liczy się jako ruszona.
48. **Wyrzut.** Koszt 6. Własna figura inna niż król przechodzi na dowolne puste pole krawędzi szachownicy i liczy się jako ruszona.
49. **Wskrzeszenie pionka.** Koszt 6. Ostatni pionek z twojego grobu wraca na puste pole rzędu 2–4 dla białych albo 5–7 dla czarnych i jest już ruszony. Bez pionka w grobie akcja jest odrzucana.
50. **Wskrzeszenie lekkiej.** Koszt 10. Z twojego grobu wraca ta z pary skoczek/goniec, która zeszła później, na dowolne puste pole.
51. **Wskrzeszenie wieży.** Koszt 13. Ostatnia wieża z twojego grobu wraca na dowolne puste pole, bez prawa do roszady.
52. **Wskrzeszenie hetmana.** Koszt 18. Ostatni hetman z twojego grobu wraca na dowolne puste pole.
53. **Powrót z wyboru.** Koszt 16. Dowolna jedna figura z twojego grobu, wskazana przez ciebie, wraca na dowolne puste pole. Wieża wraca bez roszady, pionek jako już ruszony.
54. **Promocja polowa.** Koszt 9. Własny pionek na dowolnym polu staje się hetmanem, wieżą, gońcem albo skoczkiem, do wyboru. Wieża z tej promocji nie ma prawa do roszady.
55. **Cicha promocja.** Koszt 4. Własny pionek na dowolnym polu staje się skoczkiem.
56. **Awans na wieżę.** Koszt 6. Własny pionek na dowolnym polu staje się wieżą bez prawa do roszady.
57. **Degradacja.** Koszt 7. Cudza figura inna niż król i inna niż pionek staje się pionkiem swojego koloru na tym samym polu i ten pionek jest już ruszony. Na ostatnim rzędzie tego koloru akcja jest odrzucana.
58. **Przemiana lekka.** Koszt 5. Własny skoczek staje się gońcem albo własny goniec skoczkiem, na tym samym polu.
59. **Koronacja wieży.** Koszt 8. Własna wieża staje się hetmanem na tym samym polu i traci roszadę.
60. **Reinkarnacja.** Koszt 12. Jeśli obowiązkowy ruch tej tury zbije cudzą figurę inną niż król, na pustym polu sąsiadującym z polem zbicia staje twój pionek, już ruszony, a zbita figura nie idzie do grobu. Gdy pustego sąsiada brak, bicie idzie zwykłą drogą do grobu, a kopia i tak jest zużyta.
61. **Kradzież.** Koszt 20. Cudza figura inna niż król staje się twoja i zostaje na polu; pionek jest już ruszony, a wieża nie ma roszady. Akcja jest legalna, gdy po zmianie koloru twój król stoi poza szachem.
62. **Dar.** Koszt 3. Własny pionek staje się pionkiem przeciwnika na tym samym polu. Akcja jest legalna, gdy po zmianie twój król stoi poza szachem, a pionek nie stoi na ostatnim rzędzie nowego właściciela.
63. **Klon pionka.** Koszt 11. Na pustym polu rzędu 2–4 dla białych albo 5–7 dla czarnych staje dodatkowy twój pionek, już ruszony. Przy ośmiu pionkach na szachownicy akcja jest odrzucana.
64. **Echo.** Koszt 14. Na pustym polu sąsiadującym z własnym skoczkiem albo gońcem staje druga figura tego samego typu i koloru.
65. **Stos.** Koszt 4. Własna wieża schodzi do grobu, a ty natychmiast dostajesz 6 energii.
66. **Korona w płomieniach.** Koszt 6. Własny hetman schodzi do grobu, a ty natychmiast dostajesz 9 energii.
67. **Haracz.** Koszt 2. Gdy w twoim grobie leży co najmniej jedna figura, dostajesz 3 energie i nic z grobu nie wraca. Pusty grób odrzuca akcję.
68. **Tempo.** Koszt 7. Znacznik „zamiast ruchu”: nie ruszasz figury, dostajesz 2 energie i tura przechodzi. W szachu akcja jest odrzucana.
69. **Dodatkowy ruch.** Koszt 18. Po swoim ruchu szachowym wykonujesz drugi ruch szachowy. Przychód +1 nalicza się raz, po drugim ruchu; mat przy pierwszym ruchu kończy partię od razu.
70. **Cisza.** Koszt 1. Znacznik „zamiast ruchu”: pozycja zostaje, energii nie dostajesz, tura przechodzi. W szachu akcja jest odrzucana; mając Ciszę w talii i żadnego legalnego ruchu, gracz może ją zagrać i partia trwa dalej zamiast stać się patem.
71. **Pożyczka.** Koszt 3. Dostajesz natychmiast 4 energie, a na początku następnej swojej tury oddajesz 4. Brakującą resztę spłacają kolejne przychody +1 za ruch, aż dług zniknie.
72. **Dziesięcina.** Koszt 5. Przeciwnik traci 3 energie, a gdy ma mniej, schodzi do zera.
73. **Wampir energii.** Koszt 8. Przeciwnik traci 2 energie, a ty dostajesz tyle, ile realnie stracił.
74. **Skarbiec.** Koszt 4. Jeśli twój ruch w tej turze jest biciem, za ten ruch dostajesz 3 energie zamiast 1. Jeśli biciem nie jest, dostajesz 0, a kopia i tak schodzi z talii.
75. **Umowa pionkowa.** Koszt 4. Ruch tej tury ma być ruchem pionka bez bicia, a za ten ruch dostajesz 2 energie zamiast 1. Bez takiego legalnego ruchu akcja jest odrzucana.
76. **Oszczędność.** Koszt 6. Do końca partii każdy twój wykonany ruch szachowy daje 2 energie zamiast 1. Drugiej Oszczędności ten gracz już nie zagra.
77. **Roszada awaryjna.** Koszt 5. Wykonujesz roszadę, gdy król i wieża stoją na polach startowych, pola między nimi są puste, a król nie jest w szachu, nie przechodzi przez atakowane pole i nie ląduje na atakowanym. Działa także wtedy, gdy prawo do roszady już przepadło; wieża idzie jak przy roszadzie.
78. **Roszada w szachu.** Koszt 13. Zwykła roszada, do której masz prawo, jest legalna mimo szacha. Król nadal nie przechodzi przez atakowane pole i nie ląduje na atakowanym.
79. **Zakaz roszady.** Koszt 3. Przeciwnik traci oba prawa do roszady.
80. **Zakaz przelotu.** Koszt 2. Przez najbliższe 4 twoje tury żadna strona nie wykona bicia w przelocie.
81. **Ślepy atak.** Koszt 10. Wskazana cudza figura przez 2 twoje tury nie jest liczona przy szachu, przy polach atakowanych i przy drodze roszady. Swoje ruchy i bicia wykonuje normalnie.
82. **Wymuszenie króla.** Koszt 9. W najbliższej turze przeciwnik rusza królem, jeśli ma legalny ruch królem. Gdy takiego ruchu nie ma, efekt przepada i przeciwnik gra dowolną figurą.
83. **Zakaz hetmana.** Koszt 8. W najbliższej turze przeciwnik nie rusza hetmanem, chyba że hetmanem jest jedyny legalny sposób zejścia z szacha.
84. **Zakaz pionków.** Koszt 6. W najbliższej turze przeciwnik nie rusza pionkiem, z tym samym wyjątkiem zejścia z szacha.
85. **Azyl.** Koszt 9. Przez 2 twoje tury twojego króla szachują tylko figury stojące w odległości królewskiej co najwyżej 2.
86. **Tarcza króla.** Koszt 11. Najbliższy szach twojemu królowi jest ignorowany dla jednej szachującej figury, wybranej przez obrońcę, i tarcza wtedy pęka. Drugi szachujący w tym samym ruchu nadal daje szach.
87. **Wygnanie króla.** Koszt 16. Cudzy król przechodzi o 1 pole na puste pole, które nie jest atakowane, i traci roszadę. Bez takiego pola akcja jest odrzucana.
88. **Spokój.** Koszt 15. Znacznik „zamiast ruchu”. Każda figura, która właśnie szachuje twojego króla, dostaje Ślepy atak do początku twojej następnej tury. Akcja jest legalna tylko wtedy, gdy po tych oślepieniach król stoi poza szachem; przychodu +1 nie ma.
89. **Rów.** Koszt 5. Wskazane puste pole do początku twojej następnej tury jest wyłączone z wejścia i z przejścia. Akcja jest legalna, gdy po postawieniu rowu twój król stoi poza szachem.
90. **Palisada.** Koszt 9. Trzy sąsiednie puste pola w jednym rzędzie dostają efekt Rowu do początku twojej następnej tury.
91. **Mgła kolumny.** Koszt 6. Przez 2 twoje tury żaden ruch nie kończy się w wybranej kolumnie; przejście przez nią przy ślizgu zostaje dozwolone. Król stojący w tej kolumnie może z niej wyjść, a akcja jest odrzucana, gdy zabrałaby mu ostatnie legalne pole.
92. **Ciemne pola.** Koszt 7. Przez najbliższą turę przeciwnik kończy ruch tylko na jasnym polu, z wyjątkiem ruchu, który jest jedynym zejściem z szacha.
93. **Jasne pola.** Koszt 7. Jak Ciemne pola, dla pól jasnych: przeciwnik kończy ruch na ciemnym polu.
94. **Pionek tyłem.** Koszt 3. Wskazany własny pionek do końca partii rusza się i bije w przeciwną stronę, a promuje na nowym ostatnim rzędzie. Dwukrok też jest odwrócony.
95. **Długi pion.** Koszt 5. Wskazany własny pionek do końca partii może iść o 2 pola do przodu, gdy oba pola są puste, także po ruszeniu. Takie przejście otwiera bicie w przelocie.
96. **Kotwica.** Koszt 4. Wskazana własna figura do końca partii nie jest legalnym celem Przeskoku, Teleportu, Zamiany, Odsunięcia, Przyciągnięcia, Wymiany biegunów, Lustra, Ściągania, Wyrzutu, Kradzieży ani Daru. Nadal wolno ją zbić, zamrozić i zatruć.
97. **Znak.** Koszt 1. Oznaczasz jedną cudzą figurę inną niż król. Gdy zbije ją twój ruch, za tamtą turę dostajesz 4 energie zamiast 1; drugi Znak, dopóki pierwszy trwa, jest odrzucany.
98. **Ślad.** Koszt 2. Plansza pokazuje wszystkie legalne ruchy szachowe przeciwnika w tej pozycji. Pozycja i energia przeciwnika zostają bez zmian.
99. **Ofiara pola.** Koszt 2. Do początku twojej następnej tury wskazane puste pole jest atakowane tak, że cudzy król nie może na nie wejść. Twoje figury wchodzą na nie normalnie.
100. **Reset statusów.** Koszt 8. Z planszy schodzą zamrożenia, trucizny, bursztyn, ślepy atak, rowy, palisada, mgła kolumny oraz zakaz ciemnych i jasnych pól. Trwałe ulepszenia, Pionek tyłem, Długi pion, Kotwica, Znak, Oszczędność i prawa roszady zostają.
