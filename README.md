# Lab Power Supply
# Opis
Projekt ma na celu stworzenie funkcjonalnego zasilacza labolatoryjnego w celu lepszej kontroli nad trawieniem ostrzy wolframowych z wykorzystaniem źródła prądowego i platformy Arduino. Dostarczany prąd będzie ograniczony do kilkuset mA, a napięcie do 12 V. Chciałbym osiągnąć jak największą stabilność i dokładność (zadowalające byłyby części dziesięne mA i setne V). Zasilacz ma być wielosystemowy - prąd stały oraz prostokątny przebieg prądu zmiennego. Dodatkowym atrybutem będzie aplikacja mobilna, z której planuję umożliwić odczyt teraźniejszych danych, zapis serii, a także zadanie wartości do układu. Zasilanie samego mikrokontrolera oraz reszty komponentów prawdopodobnie zostanie dostarczone z zasilacza komputerowego.

# Analiza rozwiązań rynkowych
## Zasilanie układu
Większość układów przyjmuje prąd zmienny na wejście, a następnie go konwertuje na prąd stały, który jest odpowiednio filtrowany i stabilizowany, aby zminimalizować wpływ sygnału z sieci na wyjściu. Ta część jest realizowana przez zasilacz, aczkolwiek ciężko stwierdzić na obecnym etapie w jakim stopniu - prawdopodobnie mogą być wymagane dodatkowe ingerencje w celu poprawienia jakości tego sygnału. Zdarzają się też rozwiązania, które, jeżeli mają wygenerować funkcję sinusoidalną, nie wykonują transformacji na DC, tylko manipulują tym co otrzymają z sieci. Takie układy jednak są bardzo rzadkie, ponieważ są skomplikowane w budowie, nieefektywne przez wysoki współczynnik mocy oraz tworzą zakłócenia dla niskich częstotliwości.

## Generowanie sygnału
W zasadzie nie spotyka się układów takich jak ten, który zbuduję (choć takie istnieją), czyli o pojedynczym wyjściu dc oraz ac. Raczej jest to, albo samo wyjście prądu stałego, albo cały generator funkcyjny. Generalnie to co otrzymamy na wyjście jest tworzone przez ostatnią część układu. Odpowiednie ustawienie tranzystorów, bądź wzmacniacz operacyjny lub pozwolą nam na otrzymanie przebiegu stałego. Zaś w przypadku zmiennego mogą być również wykorzystane wzmacniacze w serii, bądź odpowiednia kompozycja integratora, komparatora i diod. Wzmacniacze mogą też być umieszczone wewnątrz układu scalonego i obecenie najczęściej takie są wykorzystywane.

## Porównanie cen i jakości
Niestety dostępne na rynku, przeciętne rozwiązania są niewystarczające dla zastosowania w przypadku ostrzy wolframowych. Zasilacze labolatoryjne w cenie kilkuset złotych są dość niedokładne i znacznie wychodzą poza zakres używanych napieć i prądu, co dodatkowo obniża ich sprawność. Brakuje im też wyjścia z prądem zmiennym, a te które by je posiadały (de facto generatory funkcyjne) są wykonane jeszcze gorzej, przy tym budżecie, ze względu na ilość możliwych do wygenerowania syngałów. Z drugiej strony zakup porządnego urządzenia to koszt przynajmniej kilku tysiecy złotych, a potencjał byłby niewykorzystany.

Moje rozwiązanie pozwoli na przystępną cenę - szacuję, że nie powinno być to więcej niż 500 zł (sądzę, że mniej, aczkolwiek nie mam jeszcze pełnego obrazu przedsięwzięcia) - przy ograniczeniu się do tych parametrów, które mogą być wykorzystane w laboratorium, co umożliwi lepszą kontrolę oraz większą precyzję.

## Sterowanie
Planuję umieścić pokrętło sterowania napięciem oraz maksymalnym prądem fizycznie na zasilaczu (jak to ma miejsce w każdym z rozwiązań rynkowych) i możliwe, że panel z cyframi w celu dokładnego podania wartości. Te opcje ma też docelowo zapewniać aplikacja z komunikacją bezprzewodową i prawdopodobnie przewodową. O ile klasyczne sterowanie, czy nawet takie bardziej zaawansowane jest czym normalnym, tak dostęp z aplikacji na smartfonie/komputerze już nie. Tak naprawdę znalazłem 1 model, który posiadałby ten atrybut, a moim zdaniem jest on dość przydatny. Najczęsciej, jeśli już jest, to istnieje sama możliwość dostępu np. przez usb czy Bluetooth i komunikacja szeregowa po jednym z dostępnych protokołów.

# Plan działania
1. Zapoznanie się i przetestowanie poszczególnych elementów.
2. Rozpoczęcie pisania aplikacji.
3. Sprawdzenie jakości (stabilności) przekazywanego przez zasilacz prądu.
4. Utworzenie podstawowego wyglądu aplikacji.
5. Stworzenie źródła prądowego.
6. Połączenie Bluetooth i odbiór danych.
7. Dodanie sygnału prostokątnego.
8. Kontrola ustawień Arduino z aplikacji.

# Rozwiązania, które chcę wykorzystać
* W celu stworzenia źródła prądowego będę musiał ciągle wykonywać pomiar natężenia na wyjściu układu i przekazywać go do mikrokontrolera, a następnie dostosowywać wartość obciążenie rezystora w dzielniku napięcia.
* Do uzyskania sygnału prostokątnego spróbuję wykorzystać oscylator z mikrokontrolera i za pomocą kodu storzyć odpowiednią falę. Jeśli nie będzie ona posiadać w pełni pożądanego kształtu zdecyduję się na wykorzystanie zewnętrznego osyclatora, bądź innego rozwiązania.
* Oba sygnały będą wykorzystywały wzmacniacze operacyjne, w celu uzyskania napięcia do 12 volt.
* Pomiar natężenia mogę wykonać na samym Arduino (o ile będzie się dało wykonać to sensownie z potrzebnym zakresem) lub za pomocą zewnętrznego układu (np. INAxxx) i pewnie to drugie rozwiązabue zostanie wdrożone.

# Znane potrzebne komponenty
- Mikrokontroler Arduino
- Moduł Bluetooth
- Kable
- Zasilacz (np. komputerowy) z wyjściami 5V i 12V
- Podstawowe elementy elektroniczne
- Potencjometry/rezystory sterowane
- Wzmaczniacze operacyjne
- Diody LED

# Źródła do zaobserwowanych projektów
* [Arduino Controlled Power Constant Current Source](https://www.bristolwatch.com/ele4/ard_css.htm)
* [Constant Voltage or Current Supply with ARDUINO](https://www.youtube.com/watch?v=rwqY0rYPlVE)