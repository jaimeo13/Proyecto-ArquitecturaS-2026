package com.vitallogix.backend.config;

import com.vitallogix.backend.model.Category;
import com.vitallogix.backend.model.Product;
import com.vitallogix.backend.model.Role;
import com.vitallogix.backend.model.User;
import com.vitallogix.backend.repository.CategoryRepository;
import com.vitallogix.backend.repository.ProductRepository;
import com.vitallogix.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Inicializador de datos base (bootstrap) al arranque de la aplicación Spring Boot.
 * Garantiza que la base de datos MySQL disponga desde el primer inicio de usuarios de prueba
 * (administrador y usuario estándar), categorías predefinidas y un catálogo inicial de productos
 * farmacéuticos para que la tabla CRUD muestre registros inmediatamente al abrir la aplicación.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Configuration
public class DataInitializer {

    /** Nombre de usuario del administrador bootstrap. */
    @Value("${app.bootstrap.admin.username:${APP_BOOTSTRAP_ADMIN_USERNAME:admin1}}")
    private String bootstrapAdminUsername;

    /** Contraseña del administrador bootstrap. */
    @Value("${app.bootstrap.admin.password:${APP_BOOTSTRAP_ADMIN_PASSWORD:admin123}}")
    private String bootstrapAdminPassword;

    /** Bandera que habilita la creación del usuario demo estándar. */
    @Value("${app.bootstrap.demo-user.enabled:${APP_BOOTSTRAP_DEMO_USER_ENABLED:true}}")
    private boolean bootstrapDemoUserEnabled;

    /** Nombre de usuario de la cuenta demo estándar. */
    @Value("${app.bootstrap.demo-user.username:${APP_BOOTSTRAP_DEMO_USER_USERNAME:user1}}")
    private String bootstrapDemoUserUsername;

    /** Contraseña de la cuenta demo estándar. */
    @Value("${app.bootstrap.demo-user.password:${APP_BOOTSTRAP_DEMO_USER_PASSWORD:user123}}")
    private String bootstrapDemoUserPassword;

    /** Bandera que habilita la carga automática de categorías iniciales. */
    @Value("${app.bootstrap.seed-categories.enabled:${APP_BOOTSTRAP_SEED_CATEGORIES_ENABLED:true}}")
    private boolean bootstrapSeedCategoriesEnabled;

    /** Bandera que habilita la carga automática de productos iniciales si la tabla está vacía. */
    @Value("${app.bootstrap.seed-products.enabled:${APP_BOOTSTRAP_SEED_PRODUCTS_ENABLED:true}}")
    private boolean bootstrapSeedProductsEnabled;

    /**
     * Registra el {@link CommandLineRunner} encargado de sembrar usuarios, productos y categorías
     * iniciales en MySQL cuando aún no existen en la base de datos.
     *
     * @param userRepository     repositorio de usuarios.
     * @param categoryRepository repositorio de categorías.
     * @param productRepository  repositorio de productos.
     * @param passwordEncoder    codificador BCrypt para contraseñas.
     * @return tarea ejecutable al arrancar Spring Boot.
     */
    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedUserIfMissing(userRepository, passwordEncoder, bootstrapAdminUsername, bootstrapAdminPassword, Set.of(Role.ADMIN));

            if (bootstrapDemoUserEnabled) {
                seedUserIfMissing(userRepository, passwordEncoder, bootstrapDemoUserUsername, bootstrapDemoUserPassword, Set.of(Role.USER));
            }

            if (bootstrapSeedProductsEnabled && productRepository.count() == 0) {
                seedDefaultProducts(productRepository);
            }

            if (bootstrapSeedCategoriesEnabled) {
                seedDefaultCategoriesIfMissing(categoryRepository);
                syncCategoriesFromProducts(categoryRepository, productRepository);
            }
        };
    }

    /**
     * Inserta un conjunto inicial de 12 medicamentos y productos de farmacia en MySQL
     * cuando la tabla {@code products} se encuentra vacía.
     *
     * @param productRepository repositorio de productos.
     */
    private void seedDefaultProducts(ProductRepository productRepository) {
        List<Product> defaults = List.of(
                buildProduct("Paracetamol 500mg", "PARA-500MG", "Caja con 20 tabletas analgésicas y antipiréticas", "Analgésicos", "45.50", 120, false, 18),
                buildProduct("Ibuprofeno 400mg", "IBUP-400MG", "Caja con 12 cápsulas de alivio antiinflamatorio", "Antiinflamatorios", "68.00", 85, false, 16),
                buildProduct("Amoxicilina 500mg", "AMOX-500MG", "Antibiótico de amplio espectro, caja con 12 cápsulas", "Antibióticos", "135.00", 40, true, 12),
                buildProduct("Vitamina C 1000mg", "VITC-1000", "Tubo con 20 tabletas efervescentes sabor naranja", "Vitaminas", "112.50", 95, false, 20),
                buildProduct("Omeprazol 20mg", "OMEP-020MG", "Protector gástrico, caja con 14 cápsulas", "Medicamentos", "89.00", 60, false, 15),
                buildProduct("Loratadina 10mg", "LORA-010MG", "Antihistamínico sin somnolencia, caja con 10 tabletas", "Antialérgicos", "54.00", 110, false, 22),
                buildProduct("Complejo B Forte", "COMP-B100", "Frasco con 60 tabletas de vitaminas del complejo B", "Vitaminas", "148.00", 52, false, 19),
                buildProduct("Jarabe Antigripal Adulto", "GRIP-120ML", "Frasco de 120 ml para alivio de tos y congestión", "Antigripales", "124.00", 38, false, 10),
                buildProduct("Zinc + Vitamina D3", "ZINC-VD360", "Suplemento inmunológico, frasco con 60 cápsulas", "Suplementos", "195.00", 70, false, 24),
                buildProduct("Suero Oral Electrolitos", "SUER-500ML", "Solución hidratante sabor manzana 500 ml", "Suplementos", "32.00", 150, false, 14),
                buildProduct("Gel Antibacterial 500ml", "GELA-500ML", "Alcohol en gel al 70% con aloe vera", "Higiene", "78.50", 64, false, 26),
                buildProduct("Crema Hidratante DermaCare", "DERM-250ML", "Emulsión dermatológica para piel sensible 250 ml", "Cuidado personal", "210.00", 29, false, 18)
        );
        productRepository.saveAll(defaults);
    }

    /**
     * Construye una entidad {@link Product} lista para inserción inicial.
     *
     * @param name                 nombre del producto.
     * @param code                 código SKU único.
     * @param description          descripción breve.
     * @param category             categoría asignada.
     * @param price                precio unitario en formato decimal.
     * @param stock                existencias iniciales.
     * @param requiresPrescription indica si requiere receta.
     * @param monthsToExpire       meses restantes para su fecha de caducidad.
     * @return instancia configurada de {@link Product}.
     */
    private Product buildProduct(
            String name,
            String code,
            String description,
            String category,
            String price,
            int stock,
            boolean requiresPrescription,
            int monthsToExpire
    ) {
        Product p = new Product();
        p.setName(name);
        p.setCode(code);
        p.setDescription(description);
        p.setCategory(category);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setRequiresPrescription(requiresPrescription);
        p.setVisibleToUsers(true);
        p.setVisibleInSuggestions(true);
        p.setPromotionType("NONE");
        p.setExpirationDate(LocalDateTime.now().plusMonths(monthsToExpire));
        return p;
    }

    /**
     * Inserta las categorías base si todavía no existen en MySQL.
     *
     * @param categoryRepository repositorio de categorías.
     */
    private void seedDefaultCategoriesIfMissing(CategoryRepository categoryRepository) {
        List<String> defaults = List.of("Vitaminas", "Suplementos", "Medicamentos");
        for (String categoryName : defaults) {
            if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
                continue;
            }

            Category c = new Category();
            c.setName(categoryName);
            c.setDescription("Categoría predefinida del sistema");
            c.setStatus(Category.StatusEnum.ACTIVE);
            c.setType(Category.TypeEnum.PREDEFINED);
            c.setVisibleInSuggestions(true);
            categoryRepository.save(c);
        }
    }

    /**
     * Sincroniza la tabla {@code categories} con las categorías presentes en los productos registrados.
     *
     * @param categoryRepository repositorio de categorías.
     * @param productRepository  repositorio de productos.
     */
    private void syncCategoriesFromProducts(CategoryRepository categoryRepository, ProductRepository productRepository) {
        Set<String> existing = categoryRepository.findAll().stream()
                .map(Category::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        Set<String> productCategoryNames = productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        for (String productCategoryName : productCategoryNames) {
            if (existing.contains(productCategoryName.toLowerCase(Locale.ROOT))) {
                continue;
            }

            Category c = new Category();
            c.setName(productCategoryName);
            c.setDescription("Sincronizada automáticamente desde el catálogo de productos");
            c.setStatus(Category.StatusEnum.ACTIVE);
            c.setType(Category.TypeEnum.PREDEFINED);
            c.setVisibleInSuggestions(true);
            categoryRepository.save(c);
        }
    }

    /**
     * Crea un usuario inicial si no existe previamente en la base de datos.
     *
     * @param userRepository repositorio de usuarios.
     * @param encoder        codificador de contraseñas.
     * @param username       nombre de usuario.
     * @param password       contraseña en texto plano para cifrar.
     * @param roles          conjunto de roles asignados.
     */
    private void seedUserIfMissing(UserRepository userRepository, PasswordEncoder encoder, String username, String password, Set<Role> roles) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return;
        }

        String normalizedUsername = username.trim();

        if (userRepository.findByNormalizedUsername(normalizedUsername).isPresent()) {
            return;
        }

        User u = new User();
        u.setUsername(normalizedUsername);
        u.setPassword(encoder.encode(password));
        u.setRoles(roles);
        userRepository.save(u);
    }
}
