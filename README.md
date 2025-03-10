# AgendaAPP

## Descripción General
`AgendaAPP` es una aplicación Android que permite a los usuarios registrarse, iniciar sesión y gestionar sus contactos. Cada usuario puede agregar, modificar y eliminar sus contactos. La aplicación utiliza SQLite para almacenar la información de los usuarios y sus contactos.

## Índice
1. [Descripción General](#descripción-general)
2. [Clases y Métodos](#clases-y-métodos)
   - [DatabaseHelper](#databasehelper)
   - [Contact](#contact)
   - [LoginFragment](#loginfragment)
   - [RegisterFragment](#registerfragment)
   - [MainFragment](#mainfragment)
   - [AddContactFragment](#addcontactfragment)
   - [InformationFragment](#informationfragment)
   - [ContactAdapter](#contactadapter)
3. [Instrucciones de Uso](#instrucciones-de-uso)

## Clases y Métodos

### DatabaseHelper

#### Descripción
`DatabaseHelper` es una clase que extiende `SQLiteOpenHelper` y gestiona la creación y actualización de la base de datos. Contiene métodos para registrar usuarios, iniciar sesión, agregar, modificar y eliminar contactos.

#### Constructor
```kotlin
DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
```

### Métodos principales

#### onCreate
```kotlin
override fun onCreate(db: SQLiteDatabase)
```
Crea las tablas `users` y `contacts` en la base de datos.

- `users`: Almacena información de los usuarios con ID, nombre, email y contraseña.
- `contacts`: Almacena información de los contactos, vinculándolos con el usuario propietario.

#### onUpgrade
```kotlin
override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
```
Elimina y recrea las tablas en caso de actualización de la base de datos.

#### registerUser
```kotlin
fun registerUser(name: String, email: String, password: String, context: Context): Boolean
```
Registra un nuevo usuario en la base de datos.
- Recibe nombre, email y contraseña.
- Devuelve `true` si el registro es exitoso y `false` en caso de error.

#### loginUser
```kotlin
fun loginUser(email: String, password: String, context: Context): Boolean
```
Verifica si un usuario existe en la base de datos.
- Busca un usuario con el email y contraseña proporcionados.
- Si existe, guarda el `user_id` en `SharedPreferences`.
- Retorna `true` si el inicio de sesión es exitoso, `false` en caso contrario.

#### addContact
```kotlin
fun addContact(userId: Int, name: String, surname: String, phone: String, context: Context): Boolean
```
Agrega un contacto a la base de datos.
- Recibe el ID del usuario propietario y los datos del contacto.
- Devuelve `true` si la inserción es exitosa, `false` en caso de error.

#### getContacts
```kotlin
fun getContacts(userId: Int): List<Contact>
```
Obtiene la lista de contactos de un usuario.
- Consulta la base de datos y devuelve una lista de objetos `Contact`.

#### getContactById
```kotlin
fun getContactById(contactId: Int): Contact?
```
Busca un contacto específico por su ID.
- Devuelve un objeto `Contact` si lo encuentra, `null` en caso contrario.

#### updateContact
```kotlin
fun updateContact(contactId: Int, name: String, surname: String, phone: String, context: Context): Boolean
```
Modifica los datos de un contacto existente.
- Retorna `true` si la actualización es exitosa, `false` si hay error.

#### deleteContact
```kotlin
fun deleteContact(contactId: Int, context: Context): Boolean
```
Elimina un contacto de la base de datos.
- Devuelve `true` si la eliminación fue exitosa, `false` en caso de error.

### Contact
```kotlin
data class Contact(val id: Int, val name: String, val surname: String, val phone: String)
```
Define la estructura de un contacto.
- `id`: Identificador del contacto.
- `name`: Nombre del contacto.
- `surname`: Apellido del contacto.
- `phone`: Número telefónico del contacto.

