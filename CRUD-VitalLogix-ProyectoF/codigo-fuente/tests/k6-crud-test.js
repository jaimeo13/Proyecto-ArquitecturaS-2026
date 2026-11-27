import http from 'k6/http';
import { check, group, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:8080/api';

export const options = {
  scenarios: {
    // Escenario 1: Pruebas funcionales completas (CRUD + Modificar en masa + Roles + Excepciones + CORS)
    functional_crud: {
      executor: 'per-vu-iterations',
      vus: 1,
      iterations: 1,
      exec: 'functionalCrudFlow',
    },
    // Escenario 2: Prueba de carga concurrente sobre listado paginado y búsqueda en MySQL
    load_read_mysql: {
      executor: 'constant-vus',
      vus: 10,
      duration: '12s',
      startTime: '2s',
      exec: 'loadReadFlow',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500'],
    checks: ['rate==1.0'],
  },
};

export function functionalCrudFlow() {
  // 1. Verificar CORS para 127.0.0.1:5173
  group('CORS Preflight 127.0.0.1:5173', () => {
    const corsRes = http.options(`${BASE_URL}/products`, null, {
      headers: {
        Origin: 'http://127.0.0.1:5173',
        'Access-Control-Request-Method': 'GET',
      },
    });
    check(corsRes, {
      'CORS preflight 200': (r) => r.status === 200,
      'CORS allows 127.0.0.1:5173': (r) =>
        r.headers['Access-Control-Allow-Origin'] === 'http://127.0.0.1:5173',
    });
  });

  // 2. Verificar que usuario normal (USER) NO puede crear, modificar en masa ni borrar
  group('Seguridad: rol USER bloqueado en CRUD', () => {
    const userLogin = http.post(
      `${BASE_URL}/auth/login`,
      JSON.stringify({ username: 'user1', password: 'user123' }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(userLogin, { 'Login user1 200': (r) => r.status === 200 });
    const userToken = userLogin.json('token');

    const forbiddenCreate = http.post(
      `${BASE_URL}/products`,
      JSON.stringify({ name: 'Bloqueado', category: 'Medicamentos', price: 10, stock: 5 }),
      { headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${userToken}` } }
    );
    const forbiddenBulk = http.patch(
      `${BASE_URL}/products/batch`,
      JSON.stringify({ ids: [1], stockDelta: 5 }),
      { headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${userToken}` } }
    );
    check(forbiddenCreate, { 'USER POST /products -> 403': (r) => r.status === 403 });
    check(forbiddenBulk, { 'USER PATCH /products/batch -> 403': (r) => r.status === 403 });
  });

  // 3. Flujo completo ADMIN: Create -> Duplicate 409 -> Bulk Update -> Single Update -> 404 -> Batch Delete
  group('Flujo ADMIN: CRUD + Modificar en masa + Eliminar en masa + Excepciones', () => {
    const adminLogin = http.post(
      `${BASE_URL}/auth/login`,
      JSON.stringify({ username: 'admin1', password: 'admin123' }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(adminLogin, { 'Login admin1 200': (r) => r.status === 200 });
    const token = adminLogin.json('token');
    const authHeaders = {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    };

    const p1Res = http.post(
      `${BASE_URL}/products`,
      JSON.stringify({
        name: 'K6 Producto Alpha',
        code: 'K6-ALPHA-01',
        category: 'Medicamentos',
        price: 80.0,
        stock: 20,
        requiresPrescription: false,
        visibleInSuggestions: true,
      }),
      { headers: authHeaders }
    );
    const p2Res = http.post(
      `${BASE_URL}/products`,
      JSON.stringify({
        name: 'K6 Producto Beta',
        code: 'K6-BETA-02',
        category: 'Medicamentos',
        price: 95.0,
        stock: 30,
        requiresPrescription: false,
        visibleInSuggestions: true,
      }),
      { headers: authHeaders }
    );
    check(p1Res, { 'Crear producto 1 (201)': (r) => r.status === 201 });
    check(p2Res, { 'Crear producto 2 (201)': (r) => r.status === 201 });
    const id1 = p1Res.json('id');
    const id2 = p2Res.json('id');

    // Excepción 409 código duplicado
    const dupRes = http.post(
      `${BASE_URL}/products`,
      JSON.stringify({
        name: 'K6 Duplicado',
        code: 'K6-ALPHA-01',
        category: 'Medicamentos',
        price: 10.0,
        stock: 5,
        requiresPrescription: false,
        visibleInSuggestions: true,
      }),
      { headers: authHeaders }
    );
    check(dupRes, { 'Duplicado lanza 409 Conflict': (r) => r.status === 409 });

    // Modificar en masa (PATCH /api/products/batch)
    const bulkRes = http.patch(
      `${BASE_URL}/products/batch`,
      JSON.stringify({
        ids: [id1, id2],
        category: 'Vitaminas',
        stockDelta: 15,
        requiresPrescription: true,
      }),
      { headers: authHeaders }
    );
    check(bulkRes, {
      'Modificar en masa 200': (r) => r.status === 200,
      'Modificar en masa updatedCount == 2': (r) => r.json('updatedCount') === 2,
    });

    // Verificar cambios persistidos en MySQL
    const verify1 = http.get(`${BASE_URL}/products/${id1}`);
    check(verify1, {
      'Verificado en MySQL category=Vitaminas': (r) => r.json('category') === 'Vitaminas',
      'Verificado en MySQL stock=35': (r) => r.json('stock') === 35,
      'Verificado en MySQL requiresPrescription=true': (r) => r.json('requiresPrescription') === true,
    });

    // Excepción 404 producto inexistente
    const notFound = http.get(`${BASE_URL}/products/999999`);
    check(notFound, { 'ID inexistente lanza 404': (r) => r.status === 404 });

    // Eliminar en masa (DELETE /api/products/batch)
    const delBatch = http.del(
      `${BASE_URL}/products/batch`,
      JSON.stringify({ ids: [id1, id2] }),
      { headers: authHeaders }
    );
    check(delBatch, {
      'Eliminar en masa 200': (r) => r.status === 200,
      'Eliminar en masa deletedCount == 2': (r) => r.json('deletedCount') === 2,
    });
  });
}

export function loadReadFlow() {
  const resPage = http.get(`${BASE_URL}/products?page=0&size=5&sort=id,asc`);
  check(resPage, {
    'GET paginado 200': (r) => r.status === 200,
    'Contiene >= 12 registros en MySQL': (r) => r.json('totalElements') >= 12,
  });

  const resSearch = http.get(`${BASE_URL}/products?q=Paracetamol&page=0&size=5`);
  check(resSearch, {
    'GET búsqueda 200': (r) => r.status === 200,
    'Encuentra Paracetamol': (r) => r.json('totalElements') >= 1,
  });

  sleep(0.2);
}
