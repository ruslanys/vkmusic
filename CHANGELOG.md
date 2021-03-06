# Лог изменений
Все значимые изменения в этом проекте будут задокументированы в этом файле.

Формат лога основан на [Ведите changelog](http://keepachangelog.com/ru/1.0.0/)
и [Семантическом версионировании](http://semver.org/lang/ru/spec/v2.0.0.html).

## [Новое]
### Исправлено
- Обработка исключений при извлечении адреса MP3 файла из M3U.

## [3.1.4] - 2019-02-23
### Исправлено
- Обновлен алгоритм выгрузки адресов MP3 файлов,
в соответствии с правками, внесенными на стороне VK.

## [3.1.3] - 2018-10-19
### Исправлено
- Обновлен алгоритм выгрузки адресов MP3 файлов,
в соответствии с правками, внесенными на стороне VK.

### Изменено
- Spring Boot обновлен до версии 2.0.6.
- Gradle обновлен до 4.10.2

## [3.1.2] - 2018-09-14
### Исправлено
- Высвобождение ресурсов после аутентификации.

## [3.1.1] - 2018-09-04
### Исправлено
- Баг с индикатором загрузки

## [3.1.0] - 2018-09-02
### Добавлено
- Открыть папку назначения из контекстного меню, если файл был загружен (#7)
- Возможность скрывать/отображать некоторые столбцы
- Поиск композиций среди своего плейлиста (#26)
- Индикатор загрузки (#28)

### Исправлено
- Увеличен размер столбца "Статус"
- Установлены минимальные размеры столбцов "Исполнитель" и "Наименование"
- Уменьшен минимальный размер столбца "ID"
- Столбец "ID" скрыт по-умолчанию

## [3.0.0] - 2018-08-30
### Изменено
- Swing заменен на JavaFX
- Spring Boot обновлен до версии 2.0.4
- Код переписан на Kotlin
- Произведен рефакторинг ВК Клиента
- Полностью переработана механика приложения
- Полностью переработан UI
- Мне больше не стыдно за код

### Удалено
- Слой работы с БД

## [2.1.2] - 2017-12-08
### Исправлено
- Обновлен алгоритм дешифрации адресов mp3 файлов (#20).
- Упрощен (обновлен) Gradle скрипт сборки проекта.

### Изменено
- Gradle обновлен до 4.4.

## [2.1.1] - 2017-10-29
### Добавлено
- Установочные пакеты приложения для Windows, Unix, MacOS со встроенной JRE.
- Установлена иконка (#14).

### Исправлено
- Установлены минимальные размеры окон.
- Очистка WebView после успешно пройденной авторизации.
- Ошибка при копировании содержимого из таблицы в буфер (#16).

### Изменено
- Для сборки проекта используется Gradle вместо Maven.


## [2.1.0] - 2017-10-19
### Добавлено
- Использование кук (#1): для того, чтобы не хранить логин/пароль и не отправлять их при каждом запуске, 
хранится ID сессии. 

### Изменено
- Окно авторизации (#11): ВК периодически показывает капчу, вместо того, чтобы пытаться её обойти,
лучше отправлять пользователя на нативную страницу авторизации и работать с ID сессии.
- При синхронизации на UI обновляется не вся таблица, а только необходимая строка.
- Spring Boot обновлен до 1.5.8.RELEASE.
- Изменен загрузочный спиннер. Спасибо loading.io за это.
- При смене юзера/логауте БД не очищается.

### Исправлено
- Баг с иконкой в трее в случае смены пользователя.


## [2.0.0] - 2017-10-10
### Добавлено
- Пользовательский интерфейс.
- Окно просмотра композиций.
- Возможность просмотра статуса загрузки композиции.
- Возможность отслеживания процесса синхронизации.
- Возможность изменения директории назначения загрузок.
- Запуск синхронизации по требованию.
- Автоматическая синхронизация.
- Запуск синхронизации проваленных загрузок.
- Сохранение переменных окружения в БД.
- Сохранение библиотеки композиций в БД.
- Системные уведомления о статусе синхронизации.
- Иконка в трее.
- Лог изменений.
- Информация о лицензии.

### Изменено
- Наименование поменялось с VKAudioSaver на VKMusic.
- Переписан слой интеграции с ВК.

### Исправлено
- Парсировщик ВК: добавлен слой дешифрации хэша адресов композиций.

### Удалено
- Поддержка ВК API по работе с музыкой.


[Новое]: https://github.com/ruslanys/vkmusic/compare/v3.1.4...HEAD
[3.1.4]: https://github.com/ruslanys/vkmusic/compare/v3.1.3...v3.1.4
[3.1.3]: https://github.com/ruslanys/vkmusic/compare/v3.1.2...v3.1.3
[3.1.2]: https://github.com/ruslanys/vkmusic/compare/v3.1.1...v3.1.2
[3.1.1]: https://github.com/ruslanys/vkmusic/compare/v3.1.0...v3.1.1
[3.1.0]: https://github.com/ruslanys/vkmusic/compare/v3.0.0...v3.1.0
[3.0.0]: https://github.com/ruslanys/vkmusic/compare/v2.1.2...v3.0.0
[2.1.2]: https://github.com/ruslanys/vkmusic/compare/v2.1.1...v2.1.2
[2.1.1]: https://github.com/ruslanys/vkmusic/compare/v2.1.0...v2.1.1
[2.1.0]: https://github.com/ruslanys/vkmusic/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/ruslanys/vkmusic/releases/tag/v2.0.0
