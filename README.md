# 📚 CloudFirestore App

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

Приложение для управления личной библиотекой книг с использованием Firebase Authentication и Cloud Firestore.

## ✨ Возможности

- 🔐 Регистрация и вход по email/password
- ✉️ Верификация email
- 📖 Добавление книг с авторами
- ⭐ Оценка книг
- 🔄 Синхронизация данных между устройствами
- 🏷️ Общая база книг и авторов

## 🛠 Технологии

| Категория       | Технологии                                                                                     |
|-----------------|------------------------------------------------------------------------------------------------|
| Язык           | Kotlin          |
| UI             | Jetpack Compose   |
| База данных    | Firestore      |
| Аутентификация | Firebase Auth |
| Архитектура    | MVVM                                                                       |
| DI             | Hilt            |
| Асинхронность  | Coroutines, Flow |

## 📁 Firebase структура данных

```plaintext
- books/
  - {bookId}
    - id: String
    - title: String
    - authorId: String
- authors/
  - {authorId}
    - id: String
    - name: String
- users/
  - {userId}
    - email: String
    - name: String
    - emailVerified: Boolean
    - createdAt: Timestamp
  - {userId}/items/
    - {itemId}
      - bookId: String
      - authorId: String
      - rating: Int
