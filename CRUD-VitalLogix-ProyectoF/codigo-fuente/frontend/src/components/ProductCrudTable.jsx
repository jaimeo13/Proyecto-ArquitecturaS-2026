import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  createProduct,
  deleteProduct,
  deleteProductsBatch,
  getActiveCategories,
  getProducts,
  updateProduct,
  updateProductsBatch,
} from '../services/api';

const DEFAULT_CATEGORIES = [
  'Analgésicos',
  'Antibióticos',
  'Antiinflamatorios',
  'Antigripales',
  'Antialérgicos',
  'Vitaminas',
  'Suplementos',
  'Medicamentos',
  'Higiene',
  'Cuidado personal',
];

const EMPTY_FORM = {
  name: '',
  code: '',
  category: 'Medicamentos',
  description: '',
  price: '',
  stock: '',
  expirationDate: '',
  requiresPrescription: false,
  visibleInSuggestions: true,
};

function extractApiError(err, fallback) {
  const data = err?.response?.data;
  if (!data) {
    return err?.message || fallback;
  }
  if (typeof data === 'string') return data;
  if (Array.isArray(data.messages) && data.messages.length > 0) {
    return data.messages.join(' • ');
  }
  return data.message || data.error || fallback;
}

function ProductCrudTable({ auth, isAdmin, onRequireAuth, onProductsChanged }) {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [sort, setSort] = useState('id,asc');
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [searchInput, setSearchInput] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [availableCategories, setAvailableCategories] = useState(DEFAULT_CATEGORIES);

  const [selectedIds, setSelectedIds] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [tableError, setTableError] = useState('');

  // Floating Modals
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [formData, setFormData] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState('');
  const [formSaving, setFormSaving] = useState(false);

  const [deleteModal, setDeleteModal] = useState({ open: false, mode: 'single', target: null, ids: [] });
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [deleteError, setDeleteError] = useState('');

  const [detailProduct, setDetailProduct] = useState(null);

  // Floating Modal 4: Bulk Edit ("Modificar en masa")
  const [isBulkEditOpen, setIsBulkEditOpen] = useState(false);
  const [bulkEditForm, setBulkEditForm] = useState({
    category: '',
    price: '',
    stockMode: 'none',
    stockValue: '',
    requiresPrescription: 'unchanged',
    visibleInSuggestions: 'unchanged',
  });
  const [bulkEditError, setBulkEditError] = useState('');
  const [bulkEditSaving, setBulkEditSaving] = useState(false);

  // Toast notifications
  const [toast, setToast] = useState(null);
  const selectAllRef = useRef(null);

  const showToast = useCallback((type, message) => {
    setToast({ type, message, id: Date.now() });
  }, []);

  useEffect(() => {
    if (!toast) return;
    const timer = setTimeout(() => setToast(null), 4500);
    return () => clearTimeout(timer);
  }, [toast]);

  // Debounce live search
  useEffect(() => {
    const timer = setTimeout(() => {
      setPage(0);
      setSearchQuery(searchInput.trim());
    }, 250);
    return () => clearTimeout(timer);
  }, [searchInput]);

  const loadCategories = useCallback(async () => {
    try {
      const res = await getActiveCategories();
      const apiCats = (res.data || []).map((c) => c.name).filter(Boolean);
      const merged = Array.from(new Set([...DEFAULT_CATEGORIES, ...apiCats])).sort((a, b) => a.localeCompare(b));
      setAvailableCategories(merged);
    } catch {
      setAvailableCategories(DEFAULT_CATEGORIES);
    }
  }, []);

  const loadProducts = useCallback(async () => {
    setLoading(true);
    setTableError('');
    try {
      const params = { page, size, sort };
      if (searchQuery) params.q = searchQuery;
      if (categoryFilter) params.category = categoryFilter;

      const res = await getProducts(params);
      const data = res.data;
      const content = Array.isArray(data) ? data : (data?.content || []);
      const total = Array.isArray(data) ? data.length : (data?.totalElements ?? content.length);
      const pages = Array.isArray(data) ? 1 : (data?.totalPages ?? 1);

      setRows(content);
      setTotalElements(total);
      setTotalPages(Math.max(1, pages));

      // Keep only selected IDs that are still visible or remove deleted ones on empty pages
      if (page > 0 && content.length === 0 && total > 0) {
        setPage(Math.max(0, pages - 1));
      }
    } catch (err) {
      setTableError(extractApiError(err, 'No se pudo cargar el listado de productos desde MySQL. Verifica que el backend esté activo en el puerto 8080.'));
    } finally {
      setLoading(false);
    }
  }, [page, size, sort, searchQuery, categoryFilter]);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  // Selection logic
  const currentPageIds = useMemo(() => rows.map((r) => r.id), [rows]);
  const allOnPageSelected = currentPageIds.length > 0 && currentPageIds.every((id) => selectedIds.has(id));
  const someOnPageSelected = currentPageIds.some((id) => selectedIds.has(id)) && !allOnPageSelected;

  useEffect(() => {
    if (selectAllRef.current) {
      selectAllRef.current.indeterminate = someOnPageSelected;
    }
  }, [someOnPageSelected]);

  const toggleSelectAllOnPage = () => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (allOnPageSelected) {
        currentPageIds.forEach((id) => next.delete(id));
      } else {
        currentPageIds.forEach((id) => next.add(id));
      }
      return next;
    });
  };

  const toggleSelectRow = (id) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const ensureAdminSession = (actionLabel) => {
    if (isAdmin) return true;
    const message = auth?.logged
      ? `Tu sesión actual (${auth.username}) no tiene permisos de Administrador para ${actionLabel}. Inicia sesión con una cuenta ADMIN (ej. admin1 / admin123).`
      : `Inicia sesión como Administrador para ${actionLabel} registros en MySQL (ej. admin1 / admin123).`;
    showToast('warning', message);
    if (onRequireAuth) onRequireAuth(message);
    return false;
  };

  // Create / Edit modal handlers
  const openCreateModal = () => {
    if (!ensureAdminSession('crear nuevos')) return;
    setEditingProduct(null);
    setFormData({
      ...EMPTY_FORM,
      expirationDate: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
    });
    setFormError('');
    setIsFormModalOpen(true);
  };

  const openEditModal = (product) => {
    if (!ensureAdminSession('editar')) return;
    setEditingProduct(product);
    setFormData({
      name: product.name || '',
      code: product.code || '',
      category: product.category || 'Medicamentos',
      description: product.description || '',
      price: product.price != null ? String(product.price) : '',
      stock: product.stock != null ? String(product.stock) : '',
      expirationDate: product.expirationDate ? String(product.expirationDate).slice(0, 10) : '',
      requiresPrescription: Boolean(product.requiresPrescription),
      visibleInSuggestions: product.visibleInSuggestions !== false,
    });
    setFormError('');
    setIsFormModalOpen(true);
  };

  const handleSaveProduct = async (e) => {
    e.preventDefault();
    setFormError('');

    const priceNum = Number(formData.price);
    const stockNum = Number(formData.stock);
    if (!formData.name.trim()) {
      setFormError('El nombre del producto es obligatorio.');
      return;
    }
    if (!Number.isFinite(priceNum) || priceNum < 0) {
      setFormError('El precio debe ser un valor numérico mayor o igual a 0.');
      return;
    }
    if (!Number.isInteger(stockNum) || stockNum < 0) {
      setFormError('El stock debe ser un número entero mayor o igual a 0.');
      return;
    }

    const payload = {
      name: formData.name.trim(),
      code: formData.code.trim() || null,
      description: formData.description.trim() || null,
      imageUrl: editingProduct?.imageUrl || null,
      category: formData.category.trim() || 'Medicamentos',
      price: priceNum,
      stock: stockNum,
      requiresPrescription: Boolean(formData.requiresPrescription),
      visibleInSuggestions: Boolean(formData.visibleInSuggestions),
      promotionType: editingProduct?.promotionType || 'NONE',
      promoBuyQuantity: editingProduct?.promoBuyQuantity ?? null,
      promoPayQuantity: editingProduct?.promoPayQuantity ?? null,
      promoPercentDiscount: editingProduct?.promoPercentDiscount ?? null,
      expirationDate: formData.expirationDate ? `${formData.expirationDate}T00:00:00` : null,
    };

    setFormSaving(true);
    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, payload);
        showToast('success', `Producto #${editingProduct.id} (${payload.name}) actualizado correctamente.`);
      } else {
        const created = await createProduct(payload);
        showToast('success', `Producto #${created.data?.id ?? ''} (${payload.name}) registrado en MySQL.`);
      }
      setIsFormModalOpen(false);
      await loadProducts();
      if (onProductsChanged) onProductsChanged();
    } catch (err) {
      setFormError(extractApiError(err, 'No se pudo guardar el registro en MySQL.'));
    } finally {
      setFormSaving(false);
    }
  };

  // Delete / Batch delete handlers
  const openSingleDeleteModal = (product) => {
    if (!ensureAdminSession('eliminar')) return;
    setDeleteError('');
    setDeleteModal({ open: true, mode: 'single', target: product, ids: [product.id] });
  };

  const openBatchDeleteModal = () => {
    if (selectedIds.size === 0) {
      showToast('warning', 'Selecciona al menos un registro usando las casillas de verificación de la tabla.');
      return;
    }
    if (!ensureAdminSession('eliminar los registros seleccionados')) return;
    setDeleteError('');
    setDeleteModal({ open: true, mode: 'batch', target: null, ids: Array.from(selectedIds) });
  };

  const openBulkEditModal = () => {
    if (selectedIds.size === 0) {
      showToast('warning', 'Selecciona al menos un producto usando las casillas de verificación para modificar en masa.');
      return;
    }
    if (!ensureAdminSession('modificar en masa')) return;
    setBulkEditForm({
      category: '',
      price: '',
      stockMode: 'none',
      stockValue: '',
      requiresPrescription: 'unchanged',
      visibleInSuggestions: 'unchanged',
    });
    setBulkEditError('');
    setIsBulkEditOpen(true);
  };

  const handleBulkEditSubmit = async (e) => {
    e.preventDefault();
    setBulkEditError('');

    const payload = {
      ids: Array.from(selectedIds),
      category: bulkEditForm.category.trim() || null,
      price: bulkEditForm.price !== '' ? Number(bulkEditForm.price) : null,
      stock: bulkEditForm.stockMode === 'set' && bulkEditForm.stockValue !== '' ? Number(bulkEditForm.stockValue) : null,
      stockDelta: bulkEditForm.stockMode === 'delta' && bulkEditForm.stockValue !== '' ? Number(bulkEditForm.stockValue) : null,
      requiresPrescription:
        bulkEditForm.requiresPrescription === 'unchanged'
          ? null
          : bulkEditForm.requiresPrescription === 'true',
      visibleInSuggestions:
        bulkEditForm.visibleInSuggestions === 'unchanged'
          ? null
          : bulkEditForm.visibleInSuggestions === 'true',
    };

    const hasAnyChange =
      payload.category !== null ||
      payload.price !== null ||
      payload.stock !== null ||
      payload.stockDelta !== null ||
      payload.requiresPrescription !== null ||
      payload.visibleInSuggestions !== null;

    if (!hasAnyChange) {
      setBulkEditError('Selecciona al menos un campo para modificar en masa.');
      return;
    }

    setBulkEditSaving(true);
    try {
      const res = await updateProductsBatch(payload);
      showToast('success', res.data?.message || `Se modificaron en masa ${payload.ids.length} producto(s).`);
      setIsBulkEditOpen(false);
      await loadProducts();
      if (onProductsChanged) onProductsChanged();
    } catch (err) {
      setBulkEditError(extractApiError(err, 'No se pudo completar la modificación en masa.'));
    } finally {
      setBulkEditSaving(false);
    }
  };

  const confirmDelete = async () => {
    setDeleteLoading(true);
    setDeleteError('');
    try {
      if (deleteModal.mode === 'single' && deleteModal.target) {
        await deleteProduct(deleteModal.target.id);
        setSelectedIds((prev) => {
          const next = new Set(prev);
          next.delete(deleteModal.target.id);
          return next;
        });
        showToast('success', `Producto #${deleteModal.target.id} (${deleteModal.target.name}) eliminado correctamente.`);
      } else {
        const res = await deleteProductsBatch(deleteModal.ids);
        setSelectedIds((prev) => {
          const next = new Set(prev);
          deleteModal.ids.forEach((id) => next.delete(id));
          return next;
        });
        showToast('success', res.data?.message || `Se eliminaron ${deleteModal.ids.length} registro(s) correctamente.`);
      }
      setDeleteModal({ open: false, mode: 'single', target: null, ids: [] });
      await loadProducts();
      if (onProductsChanged) onProductsChanged();
    } catch (err) {
      const msg = extractApiError(err, 'Error al eliminar el registro seleccionado.');
      setDeleteError(msg);
      showToast('error', msg);
    } finally {
      setDeleteLoading(false);
    }
  };

  // Metrics on current page
  const pageStockTotal = useMemo(() => rows.reduce((sum, r) => sum + Number(r.stock || 0), 0), [rows]);
  const pageValueTotal = useMemo(
    () => rows.reduce((sum, r) => sum + Number(r.price || 0) * Number(r.stock || 0), 0),
    [rows]
  );

  const showingFrom = totalElements === 0 ? 0 : page * size + 1;
  const showingTo = Math.min((page + 1) * size, totalElements);

  const pageNumbers = useMemo(() => {
    const pages = [];
    const start = Math.max(0, page - 2);
    const end = Math.min(totalPages - 1, page + 2);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  }, [page, totalPages]);

  const toggleSortColumn = (field) => {
    const [currentField, currentDir] = sort.split(',');
    if (currentField === field) {
      setSort(`${field},${currentDir === 'asc' ? 'desc' : 'asc'}`);
    } else {
      setSort(`${field},asc`);
    }
    setPage(0);
  };

  const renderSortIndicator = (field) => {
    const [currentField, currentDir] = sort.split(',');
    if (currentField !== field) return <span className="ml-1 opacity-30">↕</span>;
    return <span className="ml-1 text-blue-700">{currentDir === 'asc' ? '↑' : '↓'}</span>;
  };

  if (!isAdmin) {
    return null;
  }

  return (
    <div className="max-w-7xl mx-auto space-y-5">
      {/* Toast Notification */}
      {toast && (
        <div
          role="alert"
          className={`flex items-center justify-between gap-4 rounded-2xl border px-5 py-3.5 text-sm font-bold shadow-lg transition-all ${
            toast.type === 'success'
              ? 'border-emerald-200 bg-emerald-50 text-emerald-900'
              : toast.type === 'warning'
              ? 'border-amber-200 bg-amber-50 text-amber-900'
              : 'border-red-200 bg-red-50 text-red-900'
          }`}
        >
          <div className="flex items-center gap-2.5">
            <span className="text-lg">
              {toast.type === 'success' ? '✅' : toast.type === 'warning' ? '⚠️' : '❌'}
            </span>
            <span>{toast.message}</span>
          </div>
          <button
            type="button"
            onClick={() => setToast(null)}
            className="rounded-lg px-2 py-1 text-xs font-black uppercase opacity-70 hover:opacity-100"
          >
            Cerrar
          </button>
        </div>
      )}

      {/* Summary KPI Strip */}
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <p className="text-[11px] font-black uppercase tracking-widest text-slate-400">Registros en MySQL</p>
          <p className="mt-1 text-2xl font-black text-slate-900">{totalElements}</p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <p className="text-[11px] font-black uppercase tracking-widest text-slate-400">Stock (página actual)</p>
          <p className="mt-1 text-2xl font-black text-blue-700">{pageStockTotal} uds.</p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <p className="text-[11px] font-black uppercase tracking-widest text-slate-400">Valorizado (página)</p>
          <p className="mt-1 text-2xl font-black text-emerald-700">${pageValueTotal.toFixed(2)}</p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <p className="text-[11px] font-black uppercase tracking-widest text-slate-400">Seleccionados</p>
          <p className="mt-1 text-2xl font-black text-rose-600">{selectedIds.size}</p>
        </div>
      </div>

      {/* Main CRUD Card (Inspired by Manage Employees + VitalLogix styling) */}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl">
        {/* Top Dark Header Bar with Delete Selected & Add New Buttons */}
        <div className="flex flex-wrap items-center justify-between gap-4 bg-[#435d7d] px-6 py-4 text-white">
          <div>
            <h2 className="text-xl sm:text-2xl font-bold tracking-tight">
              Gestión de <span className="font-black">Productos</span> (Modificar en Masa)
            </h2>
            <p className="text-xs font-medium text-blue-100/90">
              Tabla MySQL <code className="rounded bg-white/15 px-1.5 py-0.5 font-mono text-[11px]">products</code>
              {' • '}
              <span className="text-emerald-200 font-bold">Panel exclusivo Administrador ({auth?.username})</span>
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <button
              type="button"
              onClick={openBatchDeleteModal}
              className={`inline-flex items-center gap-2 rounded-lg px-4 py-2.5 text-xs font-black uppercase tracking-wider text-white shadow transition-all ${
                selectedIds.size > 0
                  ? 'bg-[#dc3545] hover:bg-[#bb2d3b]'
                  : 'bg-[#dc3545]/75 hover:bg-[#dc3545]'
              }`}
            >
              <span className="inline-flex h-4 w-4 items-center justify-center rounded-full bg-white text-[#dc3545] text-xs font-black leading-none">
                −
              </span>
              <span>
                Eliminar{selectedIds.size > 0 ? ` (${selectedIds.size})` : ''}
              </span>
            </button>

            <button
              type="button"
              onClick={openBulkEditModal}
              className={`inline-flex items-center gap-2 rounded-lg px-4 py-2.5 text-xs font-black uppercase tracking-wider text-white shadow transition-all ${
                selectedIds.size > 0
                  ? 'bg-amber-500 hover:bg-amber-600'
                  : 'bg-amber-500/75 hover:bg-amber-500'
              }`}
            >
              <span className="inline-flex h-4 w-4 items-center justify-center rounded-full bg-white text-amber-600 text-[10px] font-black leading-none">
                ✎
              </span>
              <span>
                Modificar en masa{selectedIds.size > 0 ? ` (${selectedIds.size})` : ''}
              </span>
            </button>

            <button
              type="button"
              onClick={openCreateModal}
              className="inline-flex items-center gap-2 rounded-lg bg-[#28a745] px-4 py-2.5 text-xs font-black uppercase tracking-wider text-white shadow transition-all hover:bg-[#218838]"
            >
              <span className="inline-flex h-4 w-4 items-center justify-center rounded-full bg-white text-[#28a745] text-xs font-black leading-none">
                +
              </span>
              <span>Nuevo Producto</span>
            </button>
          </div>
        </div>

        {/* Search, Filter & Page-Size Toolbar */}
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-200 bg-slate-50 px-6 py-3.5">
          <div className="flex flex-wrap flex-1 items-center gap-3 min-w-[260px]">
            <div className="relative flex-1 min-w-[220px] max-w-md">
              <input
                type="text"
                placeholder="Buscar por nombre, código o categoría..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                className="w-full rounded-xl border border-slate-300 bg-white py-2 pl-9 pr-8 text-sm font-medium text-slate-700 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              />
              <span className="pointer-events-none absolute left-3 top-2.5 text-slate-400">🔍</span>
              {searchInput && (
                <button
                  type="button"
                  onClick={() => setSearchInput('')}
                  className="absolute right-2.5 top-2 text-xs font-black text-slate-400 hover:text-red-500"
                  aria-label="Limpiar búsqueda"
                >
                  ✕
                </button>
              )}
            </div>

            <select
              value={categoryFilter}
              onChange={(e) => {
                setCategoryFilter(e.target.value);
                setPage(0);
              }}
              className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 outline-none focus:border-blue-500"
            >
              <option value="">Todas las categorías</option>
              {availableCategories.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
          </div>

          <div className="flex items-center gap-3">
            <label className="flex items-center gap-2 text-xs font-bold text-slate-600">
              <span>Filas:</span>
              <select
                value={size}
                onChange={(e) => {
                  setSize(Number(e.target.value));
                  setPage(0);
                }}
                className="rounded-lg border border-slate-300 bg-white px-2.5 py-1.5 text-xs font-bold text-slate-700 outline-none focus:border-blue-500"
              >
                <option value={5}>5</option>
                <option value={10}>10</option>
                <option value={20}>20</option>
              </select>
            </label>

            <button
              type="button"
              onClick={loadProducts}
              className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-bold text-slate-700 hover:bg-slate-100"
              title="Recargar registros desde MySQL"
            >
              ↻ Actualizar
            </button>
          </div>
        </div>

        {/* Table Content */}
        <div className="overflow-x-auto">
          {tableError ? (
            <div className="p-8 text-center">
              <p className="text-sm font-bold text-red-700">{tableError}</p>
              <button
                type="button"
                onClick={loadProducts}
                className="mt-4 rounded-xl bg-blue-700 px-4 py-2 text-xs font-black uppercase tracking-wider text-white hover:bg-blue-800"
              >
                Reintentar conexión
              </button>
            </div>
          ) : (
            <table className="w-full min-w-[820px] border-collapse text-left">
              <thead>
                <tr className="border-b border-slate-200 bg-white text-[12px] font-bold text-slate-600">
                  <th className="w-12 px-5 py-3.5 text-center">
                    <input
                      ref={selectAllRef}
                      type="checkbox"
                      checked={allOnPageSelected}
                      onChange={toggleSelectAllOnPage}
                      aria-label="Seleccionar todos los registros de la página"
                      className="h-4 w-4 cursor-pointer rounded border-slate-300 accent-blue-600"
                    />
                  </th>
                  <th
                    onClick={() => toggleSortColumn('id')}
                    className="cursor-pointer select-none px-4 py-3.5 hover:text-blue-700"
                  >
                    ID {renderSortIndicator('id')}
                  </th>
                  <th className="px-4 py-3.5">Código</th>
                  <th
                    onClick={() => toggleSortColumn('name')}
                    className="cursor-pointer select-none px-4 py-3.5 hover:text-blue-700"
                  >
                    Nombre {renderSortIndicator('name')}
                  </th>
                  <th className="px-4 py-3.5">Categoría</th>
                  <th
                    onClick={() => toggleSortColumn('price')}
                    className="cursor-pointer select-none px-4 py-3.5 text-right hover:text-blue-700"
                  >
                    Precio {renderSortIndicator('price')}
                  </th>
                  <th
                    onClick={() => toggleSortColumn('stock')}
                    className="cursor-pointer select-none px-4 py-3.5 text-center hover:text-blue-700"
                  >
                    Stock {renderSortIndicator('stock')}
                  </th>
                  <th className="px-5 py-3.5 text-center">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-sm">
                {loading ? (
                  <tr>
                    <td colSpan={8} className="px-6 py-12 text-center font-bold text-slate-400">
                      Cargando registros desde MySQL...
                    </td>
                  </tr>
                ) : rows.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="px-6 py-12 text-center">
                      <p className="font-bold text-slate-500">No se encontraron registros que coincidan con los filtros.</p>
                      {(searchQuery || categoryFilter) && (
                        <button
                          type="button"
                          onClick={() => {
                            setSearchInput('');
                            setSearchQuery('');
                            setCategoryFilter('');
                            setPage(0);
                          }}
                          className="mt-3 rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-bold text-slate-700 hover:bg-slate-50"
                        >
                          Limpiar filtros
                        </button>
                      )}
                    </td>
                  </tr>
                ) : (
                  rows.map((product, index) => {
                    const isSelected = selectedIds.has(product.id);
                    return (
                      <tr
                        key={product.id}
                        className={`transition-colors ${
                          isSelected
                            ? 'bg-blue-50/80'
                            : index % 2 === 1
                            ? 'bg-slate-50/60 hover:bg-slate-100/70'
                            : 'bg-white hover:bg-slate-50'
                        }`}
                      >
                        <td className="px-5 py-3.5 text-center">
                          <input
                            type="checkbox"
                            checked={isSelected}
                            onChange={() => toggleSelectRow(product.id)}
                            aria-label={`Seleccionar producto ${product.name}`}
                            className="h-4 w-4 cursor-pointer rounded border-slate-300 accent-blue-600"
                          />
                        </td>
                        <td className="px-4 py-3.5 font-mono text-xs font-black text-slate-500">
                          #{product.id}
                        </td>
                        <td className="px-4 py-3.5 font-mono text-xs font-bold text-slate-600">
                          {product.code || '—'}
                        </td>
                        <td className="px-4 py-3.5">
                          <div className="flex items-center gap-2">
                            <span className="font-bold text-slate-800">{product.name}</span>
                            {product.requiresPrescription && (
                              <span className="rounded-md bg-purple-100 px-1.5 py-0.5 text-[10px] font-black uppercase text-purple-700">
                                Receta
                              </span>
                            )}
                          </div>
                          {product.description && (
                            <p className="mt-0.5 max-w-xs truncate text-xs text-slate-400">
                              {product.description}
                            </p>
                          )}
                        </td>
                        <td className="px-4 py-3.5">
                          <span className="inline-block rounded-full bg-blue-50 px-2.5 py-1 text-xs font-bold text-blue-700">
                            {product.category || 'Sin categoría'}
                          </span>
                        </td>
                        <td className="px-4 py-3.5 text-right font-black text-slate-800">
                          ${Number(product.price || 0).toFixed(2)}
                        </td>
                        <td className="px-4 py-3.5 text-center">
                          <span
                            className={`inline-block rounded-full px-2.5 py-1 text-xs font-black ${
                              product.stock <= 0
                                ? 'bg-red-100 text-red-700'
                                : product.stock <= 20
                                ? 'bg-amber-100 text-amber-800'
                                : 'bg-emerald-100 text-emerald-700'
                            }`}
                          >
                            {product.stock}
                          </span>
                        </td>
                        <td className="px-5 py-3.5">
                          <div className="flex items-center justify-center gap-2">
                            <button
                              type="button"
                              onClick={() => setDetailProduct(product)}
                              title="Ver detalle"
                              aria-label={`Ver detalle de ${product.name}`}
                              className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-sky-600 hover:bg-sky-50 transition-colors"
                            >
                              <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 12s3.75-6.75 9.75-6.75S21.75 12 21.75 12s-3.75 6.75-9.75 6.75S2.25 12 2.25 12z" />
                                <circle cx="12" cy="12" r="2.75" />
                              </svg>
                            </button>
                            <button
                              type="button"
                              onClick={() => openEditModal(product)}
                              title="Editar registro"
                              aria-label={`Editar ${product.name}`}
                              className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-amber-500 hover:bg-amber-50 transition-colors"
                            >
                              <svg viewBox="0 0 24 24" className="h-4 w-4" fill="currentColor">
                                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zm17.71-10.21a1.003 1.003 0 0 0 0-1.42l-2.34-2.34a1.003 1.003 0 0 0-1.42 0l-1.83 1.83 3.75 3.75 1.84-1.82z" />
                              </svg>
                            </button>
                            <button
                              type="button"
                              onClick={() => openSingleDeleteModal(product)}
                              title="Eliminar registro"
                              aria-label={`Eliminar ${product.name}`}
                              className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-red-500 hover:bg-red-50 transition-colors"
                            >
                              <svg viewBox="0 0 24 24" className="h-4 w-4" fill="currentColor">
                                <path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                              </svg>
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          )}
        </div>

        {/* Bottom Pagination Footer */}
        <div className="flex flex-wrap items-center justify-between gap-4 border-t border-slate-200 bg-slate-50 px-6 py-3.5 text-xs font-semibold text-slate-600">
          <div>
            Mostrando <span className="font-black text-slate-900">{showingFrom}</span>–
            <span className="font-black text-slate-900">{showingTo}</span> de{' '}
            <span className="font-black text-slate-900">{totalElements}</span> registros
            {selectedIds.size > 0 && (
              <button
                type="button"
                onClick={() => setSelectedIds(new Set())}
                className="ml-3 font-bold text-blue-700 underline hover:text-blue-900"
              >
                Limpiar selección ({selectedIds.size})
              </button>
            )}
          </div>

          <div className="flex items-center gap-1.5">
            <button
              type="button"
              disabled={page <= 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 font-bold text-slate-700 hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-45"
            >
              Anterior
            </button>

            {pageNumbers.map((idx) => (
              <button
                key={idx}
                type="button"
                onClick={() => setPage(idx)}
                className={`h-8 min-w-8 rounded-lg px-2.5 font-black transition-colors ${
                  idx === page
                    ? 'bg-[#435d7d] text-white shadow-sm'
                    : 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-100'
                }`}
              >
                {idx + 1}
              </button>
            ))}

            <button
              type="button"
              disabled={page + 1 >= totalPages}
              onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
              className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 font-bold text-slate-700 hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-45"
            >
              Siguiente
            </button>
          </div>
        </div>
      </div>

      {/* FLOATING MODAL 1: CREATE / EDIT PRODUCT */}
      {isFormModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-sm">
          <div className="w-full max-w-lg overflow-hidden rounded-2xl bg-white shadow-2xl">
            <div className="flex items-center justify-between bg-[#435d7d] px-6 py-4 text-white">
              <h3 className="text-base font-black uppercase tracking-wider">
                {editingProduct ? `Editar Producto #${editingProduct.id}` : 'Registrar Nuevo Producto'}
              </h3>
              <button
                type="button"
                onClick={() => setIsFormModalOpen(false)}
                className="text-xl font-bold text-white/80 hover:text-white"
              >
                ×
              </button>
            </div>

            <form onSubmit={handleSaveProduct} className="space-y-4 p-6">
              {formError && (
                <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-xs font-bold text-red-800">
                  {formError}
                </div>
              )}

              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="sm:col-span-2">
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Nombre del producto *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="Ej. Paracetamol 500mg"
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm font-semibold outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Código SKU (opcional)
                  </label>
                  <input
                    type="text"
                    value={formData.code}
                    onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                    placeholder="Autogenerado si vacío"
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm font-mono font-semibold uppercase outline-none focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Categoría *
                  </label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2.5 text-sm font-semibold outline-none focus:border-blue-500"
                  >
                    {availableCategories.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Precio ($) *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    required
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    placeholder="0.00"
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm font-semibold outline-none focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Stock (unidades) *
                  </label>
                  <input
                    type="number"
                    min="0"
                    step="1"
                    required
                    value={formData.stock}
                    onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
                    placeholder="0"
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm font-semibold outline-none focus:border-blue-500"
                  />
                </div>

                <div className="sm:col-span-2">
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Descripción
                  </label>
                  <textarea
                    rows={2}
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    placeholder="Presentación, dosis o notas del medicamento"
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm font-medium outline-none focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Fecha de caducidad
                  </label>
                  <input
                    type="date"
                    value={formData.expirationDate}
                    onChange={(e) => setFormData({ ...formData, expirationDate: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm font-semibold outline-none focus:border-blue-500"
                  />
                </div>

                <div className="flex flex-col justify-center gap-2 pt-3">
                  <label className="flex items-center gap-2 text-xs font-bold text-slate-700">
                    <input
                      type="checkbox"
                      checked={formData.requiresPrescription}
                      onChange={(e) => setFormData({ ...formData, requiresPrescription: e.target.checked })}
                      className="h-4 w-4 accent-blue-600"
                    />
                    Requiere receta médica
                  </label>
                  <label className="flex items-center gap-2 text-xs font-bold text-slate-700">
                    <input
                      type="checkbox"
                      checked={formData.visibleInSuggestions}
                      onChange={(e) => setFormData({ ...formData, visibleInSuggestions: e.target.checked })}
                      className="h-4 w-4 accent-blue-600"
                    />
                    Visible en sugerencias
                  </label>
                </div>
              </div>

              <div className="flex justify-end gap-3 border-t border-slate-100 pt-4">
                <button
                  type="button"
                  onClick={() => setIsFormModalOpen(false)}
                  className="rounded-xl border border-slate-300 bg-white px-5 py-2.5 text-xs font-black uppercase tracking-wider text-slate-600 hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={formSaving}
                  className="rounded-xl bg-[#28a745] px-5 py-2.5 text-xs font-black uppercase tracking-wider text-white shadow hover:bg-[#218838] disabled:opacity-60"
                >
                  {formSaving ? 'Guardando...' : editingProduct ? 'Guardar cambios' : 'Crear registro'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* FLOATING MODAL 2: DELETE / BATCH DELETE CONFIRMATION */}
      {deleteModal.open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-sm">
          <div className="w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-2xl">
            <div className="bg-[#dc3545] px-6 py-4 text-white">
              <h3 className="text-base font-black uppercase tracking-wider">
                {deleteModal.mode === 'single'
                  ? 'Confirmar eliminación'
                  : `Eliminar ${deleteModal.ids.length} registro(s)`}
              </h3>
            </div>
            <div className="space-y-4 p-6">
              {deleteModal.mode === 'single' ? (
                <p className="text-sm font-medium text-slate-700">
                  ¿Estás seguro de que deseas eliminar el producto{' '}
                  <span className="font-black text-slate-900">
                    #{deleteModal.target?.id} — {deleteModal.target?.name}
                  </span>
                  ? Esta acción eliminará el registro de MySQL de forma permanente.
                </p>
              ) : (
                <p className="text-sm font-medium text-slate-700">
                  ¿Estás seguro de que deseas eliminar los{' '}
                  <span className="font-black text-red-700">{deleteModal.ids.length} registros seleccionados</span>{' '}
                  (IDs: {deleteModal.ids.map((id) => `#${id}`).join(', ')}) de la base de datos MySQL?
                </p>
              )}

              {deleteError && (
                <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-xs font-bold text-red-800">
                  {deleteError}
                </div>
              )}

              <div className="flex justify-end gap-3 border-t border-slate-100 pt-4">
                <button
                  type="button"
                  disabled={deleteLoading}
                  onClick={() => setDeleteModal({ open: false, mode: 'single', target: null, ids: [] })}
                  className="rounded-xl border border-slate-300 bg-white px-5 py-2.5 text-xs font-black uppercase tracking-wider text-slate-600 hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  disabled={deleteLoading}
                  onClick={confirmDelete}
                  className="rounded-xl bg-[#dc3545] px-5 py-2.5 text-xs font-black uppercase tracking-wider text-white shadow hover:bg-[#bb2d3b] disabled:opacity-60"
                >
                  {deleteLoading ? 'Eliminando...' : 'Eliminar'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* FLOATING MODAL 4: BULK EDIT ("MODIFICAR EN MASA") */}
      {isBulkEditOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-sm">
          <div className="w-full max-w-lg overflow-hidden rounded-2xl bg-white shadow-2xl">
            <div className="flex items-center justify-between bg-amber-600 px-6 py-4 text-white">
              <div>
                <h3 className="text-base font-black uppercase tracking-wider">
                  Modificar en masa ({selectedIds.size} producto{selectedIds.size === 1 ? '' : 's'})
                </h3>
                <p className="text-xs font-medium text-amber-100">
                  Deja vacío cualquier campo que no desees cambiar.
                </p>
              </div>
              <button
                type="button"
                onClick={() => setIsBulkEditOpen(false)}
                className="text-xl font-bold text-white/80 hover:text-white"
              >
                ×
              </button>
            </div>

            <form onSubmit={handleBulkEditSubmit} className="space-y-4 p-6">
              {bulkEditError && (
                <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-xs font-bold text-red-800">
                  {bulkEditError}
                </div>
              )}

              <div className="rounded-xl bg-slate-50 px-3.5 py-2.5 text-xs font-semibold text-slate-600">
                IDs seleccionados: <span className="font-mono font-bold text-slate-900">{Array.from(selectedIds).map((id) => `#${id}`).join(', ')}</span>
              </div>

              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Nueva Categoría
                  </label>
                  <select
                    value={bulkEditForm.category}
                    onChange={(e) => setBulkEditForm({ ...bulkEditForm, category: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm font-semibold outline-none focus:border-amber-500"
                  >
                    <option value="">— Sin cambio —</option>
                    {availableCategories.map((cat) => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Nuevo Precio ($)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Sin cambio"
                    value={bulkEditForm.price}
                    onChange={(e) => setBulkEditForm({ ...bulkEditForm, price: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 px-3 py-2.5 text-sm font-semibold outline-none focus:border-amber-500"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Operación de Stock
                  </label>
                  <select
                    value={bulkEditForm.stockMode}
                    onChange={(e) => setBulkEditForm({ ...bulkEditForm, stockMode: e.target.value, stockValue: '' })}
                    className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm font-semibold outline-none focus:border-amber-500"
                  >
                    <option value="none">— Sin cambio —</option>
                    <option value="delta">Sumar / Restar (+/-)</option>
                    <option value="set">Establecer stock fijo</option>
                  </select>
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    {bulkEditForm.stockMode === 'delta' ? 'Unidades (+/-)' : 'Valor de Stock'}
                  </label>
                  <input
                    type="number"
                    step="1"
                    min={bulkEditForm.stockMode === 'set' ? '0' : undefined}
                    disabled={bulkEditForm.stockMode === 'none'}
                    placeholder={bulkEditForm.stockMode === 'delta' ? 'Ej. +10 o -5' : 'Ej. 50'}
                    value={bulkEditForm.stockValue}
                    onChange={(e) => setBulkEditForm({ ...bulkEditForm, stockValue: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 px-3 py-2.5 text-sm font-semibold outline-none focus:border-amber-500 disabled:bg-slate-100 disabled:text-slate-400"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Requiere Receta
                  </label>
                  <select
                    value={bulkEditForm.requiresPrescription}
                    onChange={(e) => setBulkEditForm({ ...bulkEditForm, requiresPrescription: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm font-semibold outline-none focus:border-amber-500"
                  >
                    <option value="unchanged">— Sin cambio —</option>
                    <option value="true">Sí (Con receta)</option>
                    <option value="false">No (Venta libre)</option>
                  </select>
                </div>

                <div>
                  <label className="mb-1 block text-xs font-black uppercase tracking-wider text-slate-500">
                    Visible en Sugerencias
                  </label>
                  <select
                    value={bulkEditForm.visibleInSuggestions}
                    onChange={(e) => setBulkEditForm({ ...bulkEditForm, visibleInSuggestions: e.target.value })}
                    className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm font-semibold outline-none focus:border-amber-500"
                  >
                    <option value="unchanged">— Sin cambio —</option>
                    <option value="true">Sí (Visible)</option>
                    <option value="false">No (Oculto)</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-3 border-t border-slate-100 pt-4">
                <button
                  type="button"
                  disabled={bulkEditSaving}
                  onClick={() => setIsBulkEditOpen(false)}
                  className="rounded-xl border border-slate-300 bg-white px-5 py-2.5 text-xs font-black uppercase tracking-wider text-slate-600 hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={bulkEditSaving}
                  className="rounded-xl bg-amber-600 px-5 py-2.5 text-xs font-black uppercase tracking-wider text-white shadow hover:bg-amber-700 disabled:opacity-60"
                >
                  {bulkEditSaving ? 'Aplicando...' : 'Aplicar cambios'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* FLOATING MODAL 3: PRODUCT DETAIL */}
      {detailProduct && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-sm">
          <div className="w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-2xl">
            <div className="flex items-center justify-between bg-[#435d7d] px-6 py-4 text-white">
              <h3 className="text-base font-black uppercase tracking-wider">
                Detalle del Producto #{detailProduct.id}
              </h3>
              <button
                type="button"
                onClick={() => setDetailProduct(null)}
                className="text-xl font-bold text-white/80 hover:text-white"
              >
                ×
              </button>
            </div>
            <div className="space-y-3 p-6 text-sm">
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Nombre</span>
                <span className="font-black text-slate-900">{detailProduct.name}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Código SKU</span>
                <span className="font-mono font-bold text-slate-800">{detailProduct.code || '—'}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Categoría</span>
                <span className="font-bold text-blue-700">{detailProduct.category || 'Sin categoría'}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Precio unitario</span>
                <span className="font-black text-emerald-700">${Number(detailProduct.price || 0).toFixed(2)}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Stock disponible</span>
                <span className="font-black text-slate-900">{detailProduct.stock} unidades</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Requiere receta</span>
                <span className="font-bold">{detailProduct.requiresPrescription ? 'Sí' : 'No'}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="font-bold text-slate-500">Caducidad</span>
                <span className="font-semibold">
                  {detailProduct.expirationDate ? String(detailProduct.expirationDate).slice(0, 10) : 'No especificada'}
                </span>
              </div>
              <div>
                <span className="block font-bold text-slate-500">Descripción</span>
                <p className="mt-1 rounded-xl bg-slate-50 p-3 text-xs font-medium text-slate-700">
                  {detailProduct.description || 'Sin descripción registrada.'}
                </p>
              </div>
              <div className="flex justify-end pt-3">
                <button
                  type="button"
                  onClick={() => setDetailProduct(null)}
                  className="rounded-xl bg-[#435d7d] px-5 py-2 text-xs font-black uppercase tracking-wider text-white hover:bg-[#354b66]"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProductCrudTable;
