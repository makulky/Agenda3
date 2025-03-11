# AgendaAPP

## Descripción General
AgendaAPP es una aplicación Android que permite a los usuarios registrarse, iniciar sesión y gestionar sus contactos. Cada usuario puede agregar, modificar y eliminar sus contactos. La aplicación utiliza SQLite para almacenar la información de los usuarios y sus contactos.

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
Crea las tablas users y contacts en la base de datos.

- users: Almacena información de los usuarios con ID, nombre, email y contraseña.
- contacts: Almacena información de los contactos, vinculándolos con el usuario propietario.

#### onUpgrade

```kotlin
override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
```
Elimina y recrea las tablas en caso de actualización de la base de datos.

#### registerUser

```kotlin
fun registerUser(name: String, email: String, password: String, context: Context): Boolean
```
- Registra un nuevo usuario en la base de datos.
- Recibe nombre, email y contraseña.
- Devuelve `true` si el registro es exitoso y false en caso de error.

#### loginUser

```kotlin
fun loginUser(email: String, password: String, context: Context): Boolean
```

- Verifica si un usuario existe en la base de datos.
- Busca un usuario con el email y contraseña proporcionados.
- Si existe, guarda el `user_id` en `SharedPreferences`.
- Retorna true si el inicio de sesión es exitoso, false en caso contrario.

#### addContact

```kotlin
fun addContact(userId: Int, name: String, surname: String, phone: String, context: Context): Boolean
```

- Agrega un contacto a la base de datos.
- Recibe el ID del usuario propietario y los datos del contacto.
- Devuelve true si la inserción es exitosa, false en caso de error.

#### getContacts

```kotlin
fun getContacts(userId: Int): List<Contact>
```

- Obtiene la lista de contactos de un usuario.
- Consulta la base de datos y devuelve una lista de objetos `Contact`.

#### getContactById

```kotlin
fun getContactById(contactId: Int): Contact?
```

- Busca un contacto específico por su ID.
- Devuelve un objeto Contact si lo encuentra, null en caso contrario.

#### updateContact

```kotlin
fun updateContact(contactId: Int, name: String, surname: String, phone: String, context: Context): Boolean
```

- Modifica los datos de un contacto existente.
- Retorna true si la actualización es exitosa, false si hay error.

#### deleteContact

```kotlin
fun deleteContact(contactId: Int, context: Context): Boolean
```

- Elimina un contacto de la base de datos.
- Devuelve `true` si la eliminación fue exitosa, false en caso de error.

#### Contact

```kotlin
data class Contact(val id: Int, val name: String, val surname: String, val phone: String)
```

Define la estructura de un contacto.
- id: Identificador del contacto.
- name: Nombre del contacto.
- surname: Apellido del contacto.
- phone: Número telefónico del contacto.

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

##### Configuración del checkbox para mostrar/ocultar contraseña

Se configura un listener para el checkbox, de modo que, si está marcado, se muestra la contraseña en el campo de texto, y si está desmarcado, la contraseña se oculta.

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

##### Navegación a la pantalla de registro

Cuando el usuario hace clic en el enlace de "Registrarse" (`textViewRegister`), se navega a la pantalla de registro utilizando la función `findNavController()`.

```kotlin
textViewRegister.setOnClickListener {
    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
}
```

##### Verificación de credenciales de usuario.

Cuando el usuario hace clic en el botón de inicio de sesión (`buttonLogin`), se obtiene el correo electrónico y la contraseña introducidos, y se verifica si son correctos utilizando el método `loginUser` de la clase `DatabaseHelper`. Si las credenciales son correctas, se navega a la pantalla principal (`MainFragment`).

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

El fragmento interactúa con la clase `DatabaseHelper` para verificar las credenciales del usuario durante el inicio de sesión. El método `loginUser` se utiliza para autenticar al usuario con su correo electrónico y contraseña.

#### loginUser

```kotlin
fun loginUser(email: String, password: String, context: Context): Boolean
```

Este método de `DatabaseHelper` recibe el correo electrónico y la contraseña ingresados por el usuario. Realiza una consulta en la base de datos para verificar si existe un usuario con esas credenciales. Si el usuario es encontrado, almacena el `user_id` en `SharedPreferences` y devuelve `true`, indicando que el inicio de sesión fue exitoso.

En el fragmento, si la autenticación es exitosa, se navega a la pantalla principal de la aplicación:

```kotlin
if (success) {
    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
}
```

### RegisterFragment

#### Descripción

`RegisterFragment` es un fragmento de Android que gestiona el registro de nuevos usuarios en la aplicación. Permite ingresar un nombre, un correo y una contraseña para crear una cuenta en la base de datos.

#### Constructor

```kotlin
class RegisterFragment : Fragment()
```

Define el fragmento que maneja la vista y lógica del registro de usuarios.

### Métodos principales

#### onCreateView

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    return inflater.inflate(R.layout.fragment_register, container, false)
}
```

Infla el diseño `fragment_register.xml` para mostrar la interfaz de usuario.

#### onViewCreated

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
```

Se ejecuta cuando la vista está creada y permite inicializar componentes.

##### Inicialización de Componentes de UI

Se asignan referencias a los elementos de la interfaz.

```kotlin
val editTextName = view.findViewById<EditText>(R.id.etName)
val editTextEmail = view.findViewById<EditText>(R.id.etEmail)
val editTextPassword = view.findViewById<EditText>(R.id.etPassword)
val checkBoxShowPassword = view.findViewById<CheckBox>(R.id.cbShowPassword)
val buttonRegister = view.findViewById<Button>(R.id.btnRegister)
```

##### Conexión con la Base de Datos

Se instancia `DatabaseHelper` para interactuar con SQLite.

```kotlin
val dbHelper = DatabaseHelper(requireContext())
```

##### Mostrar/Ocultar Contraseña

Permite alternar entre mostrar y ocultar la contraseña.

```kotlin
checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) {
        editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
    } else {
        editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
    }
    editTextPassword.setSelection(editTextPassword.text.length)
}
```

##### Registro del Usuario

Valida que los campos no estén vacíos, llama a registerUser en `DatabaseHelper` para guardar los datos y si el registro es exitoso, navega a la pantalla de inicio de sesión.

```kotlin
buttonRegister.setOnClickListener {
    val name = editTextName.text.toString().trim()
    val email = editTextEmail.text.toString().trim()
    val password = editTextPassword.text.toString().trim()

    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
    }

    val isRegistered = dbHelper.registerUser(name, email, password, requireContext())
    if (isRegistered) {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
}
```

### MainFragment

#### Descripción

`MainFragment` es el fragmento principal de la aplicación, donde se muestra la lista de contactos del usuario. Permite acceder a la pantalla para agregar un nuevo contacto y cerrar sesión.

#### Constructor

Define el fragmento principal de la aplicación.

```kotlin
class MainFragment : Fragment()
```

#### Atributos

- _binding: Maneja la vista del fragmento usando ViewBinding.
- dbHelper: Instancia de DatabaseHelper para manejar la base de datos.
- contactAdapter: Adaptador para la lista de contactos.

```kotlin
private var _binding: FragmentMainBinding? = null
private val binding get() = _binding!!
private lateinit var dbHelper: DatabaseHelper
private lateinit var contactAdapter: ContactAdapter
```

### Métodos Principales

#### onCreateView

Infla el layout usando `ViewBinding`.

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    _binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
}
```

#### onViewCreated

Se ejecuta una vez que la vista está creada.

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
```

##### Carga de Contactos

Obtiene el `user_id` desde `SharedPreferences`. Si el `user_id` es válido, carga los contactos desde la base de datos y los muestra en un `RecyclerView` con `LinearLayoutManager`.

```kotlin
dbHelper = DatabaseHelper(requireContext())

val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
val userId = sharedPref.getInt("user_id", -1)

if (userId != -1) {
    val contacts = dbHelper.getContacts(userId)
    contactAdapter = ContactAdapter(contacts)
    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    binding.recyclerView.adapter = contactAdapter
} else {
    // Manejar error: no se encontró el userId
}
```

##### Navegación a Agregar Contacto

Permite al usuario navegar a la pantalla para agregar un nuevo contacto.

```kotlin
binding.fab.setOnClickListener {
    findNavController().navigate(R.id.action_mainFragment_to_addContactFragment)
}
```

##### Cerrar Sesión

Permite cerrar sesión y regresar a la pantalla de inicio de sesión.

```kotlin
binding.logout.setOnClickListener {
    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
}
```

##### Liberar Recursos

Libera la referencia de binding para evitar fugas de memoria.

```kotlin
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

### AddContactFragment

#### Descripción

`AddContactFragment` es un fragmento que permite al usuario agregar nuevos contactos a su agenda. Los usuarios pueden ingresar el nombre, apellido y número de teléfono del contacto. Una vez que se ha completado el formulario, el contacto se guarda en la base de datos.

#### Constructor

Define el fragmento que maneja la vista y lógica de agregar un nuevo contacto.

```kotlin
class AddContactFragment : Fragment()
```

### Métodos principales

#### onCreateView

Este método infla la vista del fragmento `fragment_add_contact.xml`, donde se muestran los campos de entrada para el nombre, apellidos y teléfono del contacto. Se inicializan los elementos de la interfaz de usuario y se configura el botón "Añadir" para agregar un nuevo contacto en la base de datos.

##### Lógica de adición de contacto
En el `onCreateView`, se verifica si el userId está disponible en `SharedPreferences`, lo que indica que un usuario está autenticado. Si los campos de nombre, apellidos y teléfono no están vacíos, el fragmento utiliza el método `addContact` de la clase `DatabaseHelper` para agregar el contacto a la base de datos del usuario actual. Si la inserción es exitosa, navega hacia la pantalla principal (`MainFragment`).

##### Manejo de errores
El código también incluye comentarios donde se pueden manejar los errores en caso de que alguno de los campos esté vacío o si no se puede recuperar el `userId` del usuario desde `SharedPreferences`. En estos casos, se pueden mostrar mensajes de error o realizar otras acciones según sea necesario.

```kotlin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_add_contact, container, false)

    dbHelper = DatabaseHelper(requireContext())

    nombreEditText = view.findViewById(R.id.et_nombre)
    apellidosEditText = view.findViewById(R.id.et_apellidos)
    telefonoEditText = view.findViewById(R.id.et_telefono)
    addButton = view.findViewById(R.id.btn_añadir)

    addButton.setOnClickListener {
        val nombre = nombreEditText.text.toString()
        val apellidos = apellidosEditText.text.toString()
        val telefono = telefonoEditText.text.toString()

        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId != -1) {
            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && telefono.isNotEmpty()) {
                val success = dbHelper.addContact(userId, nombre, apellidos, telefono, requireContext())
                if (success) {
                    findNavController().navigate(R.id.action_addContactFragment_to_mainFragment)
                }
            } else {
                // Manejar error de campos vacíos
            }
        } else {
            // Manejar error: no se encontró el userId
        }
    }

    return view
}
```

### InformationFragment

#### Descripción

`InformationFragment` es un fragmento que permite al usuario ver y modificar la información de un contacto existente en la agenda. Los usuarios pueden editar el nombre, apellido y número de teléfono de un contacto, o eliminar el contacto completamente.

#### Constructor

```kotlin
class InformationFragment : Fragment()
```

Define el fragmento que maneja la vista y lógica para mostrar la información de un contacto y ofrecer opciones para modificarla o eliminarla.

### Métodos principales

#### onCreateView

Este método infla la vista del fragmento `fragment_information.xml`, que muestra los campos de entrada para editar el nombre, apellido y teléfono de un contacto. Además, maneja los botones de "Modificar" y "Eliminar" para actualizar o borrar el contacto correspondiente.

##### Lógica para mostrar la información del contacto
Se obtiene el `contactId` a través de los argumentos del fragmento (`args.contactId`). Luego, se recupera la información del contacto usando el método `getContactById` de la clase `DatabaseHelper` y se muestra en los campos de texto.

##### Lógica de modificación de contacto
Cuando el usuario hace clic en el botón de "Modificar" (`modifyButton`), se obtiene el nuevo nombre, apellido y teléfono de los campos de texto. Si los campos no están vacíos, se actualiza la información del contacto en la base de datos usando el método `updateContact`. Después de la actualización, el fragmento navega a la pantalla principal (`MainFragment`).

##### Lógica de eliminación de contacto
Si el usuario hace clic en el botón de "Eliminar" (`deleteButton`), el contacto correspondiente se elimina de la base de datos utilizando el método `deleteContact`. Luego, el fragmento navega de vuelta a la pantalla principal (`MainFragment`).

##### Manejo de errores
El código incluye un lugar donde se podría manejar el error de campos vacíos antes de realizar la modificación de un contacto. Esto puede incluir la visualización de un mensaje de error o la validación adicional de los campos.

```kotin
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_information, container, false)

    dbHelper = DatabaseHelper(requireContext())
    nameEditText = view.findViewById(R.id.tv_name)
    surnameEditText = view.findViewById(R.id.tv_surname)
    phoneEditText = view.findViewById(R.id.tv_phone)
    modifyButton = view.findViewById(R.id.btn_modify)
    deleteButton = view.findViewById(R.id.btn_delete)

    val contactId = args.contactId
    val contact = dbHelper.getContactById(contactId)
    contact?.let {
        nameEditText.setText(it.name)
        surnameEditText.setText(it.surname)
        phoneEditText.setText(it.phone)
    }

    modifyButton.setOnClickListener {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val phone = phoneEditText.text.toString()

        if (name.isNotEmpty() && surname.isNotEmpty() && phone.isNotEmpty()) {
            dbHelper.updateContact(contactId, name, surname, phone, requireContext())
            findNavController().navigate(R.id.action_informationFragment_to_mainFragment)
        } else {
            // Manejar error de campos vacíos
        }
    }

    deleteButton.setOnClickListener {
        dbHelper.deleteContact(contactId, requireContext())
        // Navegar de vuelta al MainFragment después de eliminar
        findNavController().navigate(R.id.action_informationFragment_to_mainFragment)
    }

    return view
}
```

### ContactAdapter

#### Descripción

`ContactAdapter` es un adaptador para un RecyclerView que se utiliza para mostrar una lista de contactos en la aplicación. Cada elemento de la lista muestra el nombre y el número de teléfono de un contacto. Además, permite navegar al fragmento `InformationFragment` cuando se selecciona un contacto.

#### Constructor

```kotlin
class ContactAdapter(private val contacts: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>()
```

Este adaptador recibe una lista de contactos y se encarga de crear y vincular las vistas correspondientes para cada contacto en el `RecyclerView`.

### Métodos principales

#### onCreateViewHolder

Este método infla el diseño `item_contact.xml` para cada elemento de la lista, creando una vista para un solo contacto. Luego, retorna un `ContactViewHolder` que contiene las referencias a los elementos de la vista.

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
    return ContactViewHolder(view)
}
```

#### onBindViewHolder

En este método, se vinculan los datos del contacto con los elementos de la vista. El nombre y teléfono del contacto se establecen en los `TextView` correspondientes. Además, se configura un `OnClickListener` para cada elemento de la lista: al hacer clic en un contacto, se navega al fragmento `InformationFragment` pasando el `contact.id` como argumento.

```kotlin
override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
    val contact = contacts[position]
    holder.contactName.text = "${contact.name} ${contact.surname}"
    holder.contactPhone.text = contact.phone

    // Añade el OnClickListener para navegar a InformationFragment
    holder.itemView.setOnClickListener {
        val action = MainFragmentDirections.actionMainFragmentToInformationFragment(contact.id)
        it.findNavController().navigate(action)
    }
}
```

#### getItemCount

Este método devuelve la cantidad de elementos en la lista de contactos, lo que permite al `RecyclerView` saber cuántos elementos debe mostrar.

```kotlin
override fun getItemCount() = contacts.size
```

#### ContactViewHolder

El `ContactViewHolder` es una clase que contiene las referencias a las vistas dentro de cada ítem de contacto en el `RecyclerView`. Estas vistas son los TextView que muestran el nombre y el teléfono del contacto.

```kotlin
class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val contactName: TextView = itemView.findViewById(R.id.contactName)
    val contactPhone: TextView = itemView.findViewById(R.id.contactPhone)
}
```

## Instrucciones de Uso






