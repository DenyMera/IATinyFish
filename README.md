# 🐟 IATinyFish (TinyPrice)

**IATinyFish** (también conocido como **TinyPrice**) es una aplicación Android moderna desarrollada con **Jetpack Compose** que permite buscar y comparar en tiempo real ofertas y precios de productos en la web, conectándose a funciones backend mediante **Supabase Edge Functions**.

---

## 🚀 Características principales
- **Búsqueda web inteligente**: Compara precios, costos de envío, valoraciones y disponibilidad de tiendas en tiempo real.
- **Integración con Supabase**: Se conecta mediante Retrofit a Supabase Edge Functions (`search-products`).
- **UI Moderna y Fluida (Material 3)**: Diseñada 100% con Jetpack Compose, incluyendo animaciones fluidas con `AnimatedContent` y listas optimizadas.
- **Arquitectura Robusta (MVVM)**: Implementa flujos reactivos con `StateFlow`, corrutinas y separación clara de capas (`data`, `presentation`).
- **Pruebas Automatizadas**: Incluye pruebas unitarias para el ViewModel (`kotlinx-coroutines-test`) y pruebas instrumentadas de UI (`Jetpack Compose UI Testing`).

---

## 🛠️ Tecnologías utilizadas
- **Lenguaje**: Kotlin 2.2+
- **UI**: Jetpack Compose & Material 3
- **Arquitectura**: MVVM + StateFlow
- **Red**: Retrofit 3.0 & Gson
- **Backend**: Supabase Edge Functions (Deno / TypeScript)
- **Testing**: JUnit, Coroutines Test, Compose UI Testing

---

## 📥 Cómo clonar y ejecutar el proyecto

### 1. Clonar el repositorio
```bash
git clone https://github.com/DenyMera/IATinyFish.git
cd IATinyFish
```

### 2. Configuración de Propiedades y Supabase
El proyecto incluye las propiedades configuradas en `gradle.properties`:
```properties
useRemoteDeals=true
edgeRuntimeBaseUrl=https://btkjntkvrbqjldncnzrm.supabase.co/functions/v1/search-products
```
Si deseas probar la función en Supabase localmente, crea el archivo `supabase/.env.production.local` con:
```env
TINYFISH_API_KEY=YOUR_TINYFISH_API_KEY
```

### 3. Compilar y ejecutar desde la terminal (Gradle)
- **Compilar APK de desarrollo (Debug)**:
  ```bash
  ./gradlew :app:assembleDebug
  ```
- **Ejecutar pruebas unitarias del ViewModel**:
  ```bash
  ./gradlew :app:testDebugUnitTest
  ```
- **Ejecutar pruebas instrumentadas de Compose** (requiere emulador o dispositivo conectado):
  ```bash
  ./gradlew :app:connectedDebugAndroidTest
  ```

### 4. Ejecutar desde Android Studio
1. Abre **Android Studio** y selecciona **Open** en la carpeta del proyecto `IATinyFish`.
2. Espera a que finalice el **Gradle Sync**.
3. Selecciona tu dispositivo o emulador y haz clic en **Run (Shift + F10)**.
