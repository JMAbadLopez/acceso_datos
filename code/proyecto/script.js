const API_URL = 'http://0.0.0.0:8080';

// State
let isAuthenticated = false;

// DOM Elements
const loginSection = document.getElementById('login-section');
const dashboard = document.getElementById('dashboard');
const authStatus = document.getElementById('auth-status');
const userDisplay = document.getElementById('user-display');
const btnLogout = document.getElementById('btn-logout');
const logsContainer = document.getElementById('logs-container');
const usersList = document.getElementById('users-list');

// --- Logger ---
function log(message, type = 'info') {
    const entry = document.createElement('div');
    entry.className = `log-entry ${type}`;
    const time = new Date().toLocaleTimeString();
    entry.innerHTML = `<span class="timestamp">[${time}]</span> ${message}`;
    logsContainer.prepend(entry); // Newest first
}

document.getElementById('btn-clear-logs').addEventListener('click', () => {
    logsContainer.innerHTML = '';
});

// --- Auth Logic ---

function setAuthState(loggedIn, userEmail = null) {
    isAuthenticated = loggedIn;
    if (loggedIn) {
        loginSection.classList.add('hidden');
        dashboard.classList.remove('hidden');
        userDisplay.textContent = userEmail;
        btnLogout.classList.remove('hidden');
        log(`Sesión iniciada como ${userEmail}`, 'success');
        fetchAllUsers(); // Initial load
    } else {
        loginSection.classList.remove('hidden');
        dashboard.classList.add('hidden');
        userDisplay.textContent = 'No identificado';
        btnLogout.classList.add('hidden');
        usersList.innerHTML = '<p class="placeholder-text">Inicia sesión para ver usuarios.</p>';
    }
}

// Login
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ mail: email, password: password }),
            credentials: 'include' // Important for sessions
        });

        const text = await response.text();

        if (response.ok) {
            setAuthState(true, email);
            log(`Login OK: ${text}`, 'success');
        } else {
            log(`Login Fallido: ${text}`, 'error');
        }
    } catch (err) {
        log(`Error de conexión: ${err.message}`, 'error');
    }
});

// Logout
btnLogout.addEventListener('click', async () => {
    try {
        const response = await fetch(`${API_URL}/auth/logout`, { credentials: 'include' });
        const text = await response.text();
        log(text, 'info');
        setAuthState(false);
    } catch (err) {
        log(`Error en logout: ${err.message}`, 'error');
    }
});

// --- Users API ---

// 1. GET ALL
async function fetchAllUsers() {
    try {
        // GET requests also need credentials if endpoints are protected or checks session
        // Although GET /usuarios seems public in Routes, let's be consistent or safe
        const response = await fetch(`${API_URL}/usuarios`, { credentials: 'include' });
        if (response.ok) {
            const users = await response.json();
            renderUsers(users);
            log(`Usuarios cargados: ${users.length}`, 'success');
        } else {
            log('Error al cargar usuarios', 'error');
        }
    } catch (err) {
        log(`Error fetchAll: ${err.message}`, 'error');
    }
}

document.getElementById('btn-refresh').addEventListener('click', fetchAllUsers);

function renderUsers(users) {
    usersList.innerHTML = '';
    if (users.length === 0) {
        usersList.innerHTML = '<p class="placeholder-text">No hay usuarios registrados.</p>';
        return;
    }

    users.forEach(user => {
        const card = document.createElement('div');
        card.className = 'user-card';
        card.innerHTML = `
            <span class="user-id-badge">ID: ${user.id}</span>
            <h3>${user.nombre}</h3>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Password:</strong> ********</p>
        `;
        usersList.appendChild(card);
    });
}

// 2. CREATE
document.getElementById('create-user-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const nombre = document.getElementById('new-nombre').value;
    const email = document.getElementById('new-email').value;
    const password = document.getElementById('new-password').value;

    try {
        const response = await fetch(`${API_URL}/usuarios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, email, password }),
            credentials: 'include'
        });

        if (response.ok) {
            const newId = await response.json(); // Returns created ID
            log(`Usuario creado con ID: ${newId}`, 'success');
            document.getElementById('create-user-form').reset();
            fetchAllUsers();
        } else {
            const errText = await response.text();
            log(`Error crear: ${errText}`, 'error');
        }
    } catch (err) {
        log(`Error POST: ${err.message}`, 'error');
    }
});

// 3. GET BY ID
document.getElementById('btn-get-id').addEventListener('click', async () => {
    const id = document.getElementById('op-id').value;
    if (!id) return log('Introduce un ID', 'warning');

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, { credentials: 'include' });
        if (response.ok) {
            const user = await response.json();
            log(`Usuario encontrado: ${JSON.stringify(user)}`, 'success');
            // Fill update form for convenience
            document.getElementById('update-nombre').value = user.nombre;
            document.getElementById('update-email').value = user.email;
        } else {
            log(`Usuario ${id} no encontrado`, 'error');
        }
    } catch (err) {
        log(`Error GET ID: ${err.message}`, 'error');
    }
});

// 4. UPDATE
document.getElementById('update-user-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('op-id').value;
    if (!id) return log('Introduce un ID para actualizar', 'warning');

    const nombre = document.getElementById('update-nombre').value;
    const email = document.getElementById('update-email').value;
    const password = document.getElementById('update-password').value;

    const payload = {};
    if (nombre) payload.nombre = nombre;
    if (email) payload.email = email;
    if (password) payload.password = password;

    if (!nombre || !email || !password) {
        return log('Por favor rellena todos los campos para actualizar (limitación DTO)', 'warning');
    }

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, email, password }),
            credentials: 'include'
        });

        const text = await response.text();
        if (response.ok) {
            log(`Actualizar: ${text}`, 'success');
            fetchAllUsers();
        } else {
            log(`Error Actualizar: ${text}`, 'error');
        }
    } catch (err) {
        log(`Error PUT: ${err.message}`, 'error');
    }
});

// 5. DELETE
document.getElementById('btn-delete-id').addEventListener('click', async () => {
    const id = document.getElementById('op-id').value;
    if (!id) return log('Introduce un ID para eliminar', 'warning');

    if (!confirm(`¿Seguro que quieres eliminar el usuario ${id}?`)) return;

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const text = await response.text();
        if (response.ok) {
            log(`Eliminado: ${text}`, 'success');
            fetchAllUsers();
        } else {
            log(`Error Eliminar: ${text}`, 'error');
        }
    } catch (err) {
        log(`Error DELETE: ${err.message}`, 'error');
    }
});
