# AgendaAPP

## Descripción General
`AgendaAPP` es una aplicación Android que permite a los usuarios registrarse, iniciar sesión y gestionar sus contactos. Cada usuario puede agregar, modificar y eliminar sus contactos. La aplicación utiliza SQLite para almacenar la información de los usuarios y sus contactos.

## Índice
1. [Descripción General](#descripción-general)
2. [Clases y Métodos](#clases-y-métodos)
   - [DatabaseHelper](#databasehelper)
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

#### Contact
```kotlin
data class Contact(val id: Int, val name: String, val surname: String, val phone: String)
```
Define la estructura de un contacto.
- `id`: Identificador del contacto.
- `name`: Nombre del contacto.
- `surname`: Apellido del contacto.
- `phone`: Número telefónico del contacto.

### LoginFragment

#### Descripción

`LoginFragment` es el fragmento encargado de manejar el inicio de sesión del usuario. Permite ingresar el correo electrónico y la contraseña, con una opción para mostrar/ocultar la contraseña. Si las credenciales son correctas, navega a la pantalla principal de la aplicación.

### Métodos principales

#### onCreateView
```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
```
Este método infla el layout del fragmento de inicio de sesión (`fragment_login.xml`), que contiene los campos para ingresar el correo electrónico, la contraseña, un checkbox para mostrar la contraseña, un botón de inicio de sesión y un enlace para registrarse.

#### onViewCreated
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?)
```
Este método se ejecuta después de que la vista haya sido creada. En este fragmento, se realiza la inicialización de los elementos de la interfaz de usuario (`UI`), como el campo de texto para el correo electrónico (`EditText`), la contraseña (`EditText`), el checkbox para mostrar la contraseña, y el botón de inicio de sesión (`Button`).

**Configuración del checkbox para mostrar/ocultar contraseña:** Se configura un listener para el checkbox, de modo que, si está marcado, se muestra la contraseña en el campo de texto, y si está desmarcado, la contraseña se oculta.

```kotlin
checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) {
        editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
    } else {
        editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
    }
    // Mueve el cursor al final después de cambiar la transformación
    editTextPassword.setSelection(editTextPassword.text.length)
}
```

**Navegación a la pantalla de registro:** Cuando el usuario hace clic en el enlace de "Registrarse" (`textViewRegister`), se navega a la pantalla de registro utilizando la función findNavController().

```kotlin
textViewRegister.setOnClickListener {
    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
}
```

**Verificación de credenciales de usuario:** Cuando el usuario hace clic en el botón de inicio de sesión (`buttonLogin`), se obtiene el correo electrónico y la contraseña introducidos, y se verifica si son correctos utilizando el método loginUser de la clase DatabaseHelper. Si las credenciales son correctas, se navega a la pantalla principal (`MainFragment`).

```kotlin
buttonLogin.setOnClickListener {
    val email = editTextEmail.text.toString()
    val password = editTextPassword.text.toString()

    if (email.isNotEmpty() && password.isNotEmpty()) {
        val success = dbHelper.loginUser(email, password, requireContext())
        if (success) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
    } else {
        Toast.makeText(requireContext(), "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
    }
}
```

El fragmento interactúa con la clase DatabaseHelper para verificar las credenciales del usuario durante el inicio de sesión. El método loginUser se utiliza para autenticar al usuario con su correo electrónico y contraseña.

####loginUser

```kotlin
fun loginUser(email: String, password: String, context: Context): Boolean
```

Este método de DatabaseHelper recibe el correo electrónico y la contraseña ingresados por el usuario. Realiza una consulta en la base de datos para verificar si existe un usuario con esas credenciales. Si el usuario es encontrado, almacena el user_id en SharedPreferences y devuelve true, indicando que el inicio de sesión fue exitoso.

En el fragmento, si la autenticación es exitosa, se navega a la pantalla principal de la aplicación:

```kotlin
if (success) {
    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
}
```


