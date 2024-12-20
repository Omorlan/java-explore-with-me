# java-explore-with-me
Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить.

Сложнее всего в таком планировании поиск информации и переговоры. Нужно учесть много деталей: какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться.

Explore-with-me — афиша. В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.

# Инструкция по развертыванию проекта:
1. Скачать данный репозиторий
2. mvn clean
3. mvn package
4. docker-compose build
5. docker-compose up -d

# Проект включает в себя:
1. Основной сервис, включающий в себя три раздела:

   1. Открытый - доступен всем;
         ### API для работы с событиями (Events (События))
           - Получение списка событий с возможностью фильтрации.
           - Получение детальной информации о событии по его идентификатору.
         ### API для работы с категориями (Category (Категории))
           - Получение списка всех категорий.
           - Получение информации о категории по её идентификатору.
         ### API для работы с подборками событий (Compilations (Подборки событий))
           - Получение списка подборок событий.
           - Получение данных о подборке по её идентификатору.
   2. Приватный - доступный зарегистрированным пользователям;
         ### API для работы с событиями
           - Получение списка событий, добавленных текущим пользователем.
           - Создание нового события.
           - Получение детальной информации о событии текущего пользователя.
           - Редактирование данных своего события.
         ### API для работы с запросами текущего пользователя на участие в событиях
           - Получение информации о заявках на участие в событиях текущего пользователя.
           - Изменение статуса заявок на участие в событиях текущего пользователя (подтверждение или отмена).
   3. Административный - доступен пользователям с уровнем доступа администратор.
         ### API для работы с событиями
           - Создание новой подборки событий.
           - Удаление подборки событий.
           - Редактирование данных подборки.
         ### API для работы с категориями
           - Поиск событий.
           - Редактирование информации о событии и его статуса (публикация или отклонение).
         ### API для работы с пользователями (Users (Пользователи))
          - Получение данных о пользователях.
          - Добавление нового пользователя.
          - Удаление пользователя.
         ### API для работы с подборками событий
          - Создание новой подборки событий.
          - Удаление подборки событий.
          - Редактирование данных подборки.
   
2. Сервис статистики хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения. Доступен только администратору.
   Создание запроса о посещении и сохранение в базу данных;
   Создание статистики на основе данных о посещениях.

## Дополнительный функционал (Комментарии)
Расширяет возможности приложения добавляя возможность пользователям оставлять комментарии к событиям.
 
Для открытого раздела:
- Просмотр комментариев

Для приватного раздела:
- Удаление комментария
- Добавление комментария

Для административного раздела:
- Удаление комментария
- Получение комментариев пользователя

Ссылка на PR: https://github.com/Omorlan/java-explore-with-me/pull/3
