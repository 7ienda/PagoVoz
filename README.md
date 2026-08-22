# PagoVoz — Lector de pagos por voz (Yape, Plin, Tunki, BIM, Plim...)

App Android (Kotlin) que escucha las notificaciones de tus apps de pago
y anuncia por voz cada pago recibido, con historial completo, alertas
en pantalla, funcionamiento en segundo plano y con pantalla bloqueada.

## 🚀 Forma más fácil y 100% gratis: compilar con GitHub (sin instalar nada)

Este proyecto ya incluye un archivo `.github/workflows/build-apk.yml` que
hace que **GitHub compile el APK por ti**, en sus propios servidores,
gratis, y te deje un link de descarga directa para tu celular. No
necesitas instalar Android Studio ni saber programar.

### Paso 1 — Crea una cuenta en GitHub (si no tienes)
Ve a https://github.com/signup — es gratis.

### Paso 2 — Crea un repositorio nuevo
1. Click en el botón **"+"** arriba a la derecha → **"New repository"**.
2. Ponle un nombre, por ejemplo `pagovoz`.
3. Déjalo en **Public** (o Private, ambos son gratis).
4. NO marques "Add a README" (ya tenemos uno). Click **Create repository**.

### Paso 3 — Sube los archivos del proyecto
1. Descomprime en tu computadora el .zip que te di (`PagoVoz-proyecto-android.zip`).
2. En la página de tu repositorio nuevo, click en **"uploading an existing file"**
   (o el botón **Add file → Upload files**).
3. Abre la carpeta `PagoVoz` que descomprimiste y arrastra **todo su
   contenido** (todas las carpetas y archivos de adentro, no la carpeta
   `PagoVoz` en sí) hacia la zona de subida de GitHub. En Chrome/Edge
   puedes arrastrar carpetas completas y GitHub respeta la estructura.
4. Abajo, escribe un mensaje como "Primera subida" y click **Commit changes**.

### Paso 4 — Espera a que se compile solo
1. Ve a la pestaña **"Actions"** en tu repositorio (arriba).
2. Verás un proceso corriendo llamado "Build & Release APK" (tarda 2-4 min).
3. Cuando el ícono se ponga en ✅ verde, ya está listo.

### Paso 5 — Descarga el APK en tu celular
1. Ve a la pestaña **"Releases"** de tu repositorio (o entra directo a
   `https://github.com/TU-USUARIO/pagovoz/releases`).
2. Ábrela desde el navegador de tu celular.
3. Toca el archivo **PagoVoz.apk** para descargarlo.
4. Android te pedirá permiso para "instalar apps de fuentes desconocidas"
   la primera vez — acéptalo solo para este archivo.
5. Instala y abre la app.

Cada vez que subas un cambio nuevo a `main`, el APK en Releases se
actualiza solo automáticamente.

> Nota: este APK de "debug" se instala perfectamente y funciona al 100%,
> solo que Android puede mostrar una advertencia genérica de "app no
> verificada por Play Protect" porque no viene de la Play Store — es
> normal y no significa que tenga virus, es tu propio código compilado.

---

## Alternativa: compilar localmente con Android Studio



1. Descarga e instala **Android Studio** (gratis): https://developer.android.com/studio
2. Abre Android Studio → `Open` → selecciona la carpeta `PagoVoz` (la que
   contiene `settings.gradle`).
3. Espera a que Gradle sincronice (la primera vez descarga dependencias,
   puede tardar unos minutos).
4. Conecta tu celular por USB con "Depuración USB" activada, o crea un
   emulador, y presiona el botón ▶️ Run para probarlo directo en el equipo.
5. Para obtener el `.apk` instalable: menú **Build → Generate Signed Bundle / APK**
   → elige **APK** → crea una nueva "key" (guárdala, la necesitarás para
   futuras actualizaciones) → selecciona `release` → Finish.
6. El APK queda en `app/release/app-release.apk`. Ese es el archivo que
   compartes/instalas en cualquier Android.

## Después de instalar la app (permisos que debes activar)

La app te lo pedirá en pantalla, pero en resumen necesita:

1. **Acceso a notificaciones** (obligatorio) — Ajustes → Apps con acceso
   especial → Acceso a notificaciones → activa PagoVoz.
2. **Mostrar sobre otras apps** (para la tarjeta flotante) — se pide
   automáticamente, o desde Ajustes de PagoVoz dentro de la app.
3. **Desactivar optimización de batería** para PagoVoz — así Android no
   mata el servicio en segundo plano. Hay un botón directo en Ajustes.
4. Si tu celular es Xiaomi/Huawei/Oppo/Vivo, además activa manualmente
   "Autoinicio" / "Inicio automático" para PagoVoz en la configuración
   de batería de la marca (estos fabricantes matan servicios en segundo
   plano de forma agresiva por defecto).

## Funciones incluidas

**Lo que pediste:**
- Detecta notificaciones de Yape, Plin (BBVA/Interbank/Scotiabank/BanBif),
  Tunki, BIM, Plim y Mibanco (fácil de agregar más bancos en
  `util/SupportedApps.kt`).
- Anuncio por voz (texto a voz en español) de cada pago.
- Pantalla de Alertas con feed en vivo y total del día.
- Funciona con la pantalla bloqueada (el listener de notificaciones y el
  TTS de Android no dependen de que la pantalla esté encendida).
- Servicio en primer plano + BootReceiver: se mantiene activo en segundo
  plano y se reinicia solo al prender el teléfono.
- Deduplicación real por hash + ventana de tiempo: nunca anuncia dos
  veces la misma notificación.
- Historial de pagos con una pestaña "Todos" + una pestaña dedicada por
  cada app de la que hayas recibido pagos.

**Funciones premium extra que agregué:**
- Tarjeta flotante (overlay) con el monto, visible encima de cualquier
  app y sobre la pantalla de bloqueo.
- Plantilla de anuncio personalizable ("Pago recibido de {app}. {monto}").
- Control de velocidad y tono de voz.
- Monto mínimo para anunciar (filtro anti-spam de notificaciones chicas).
- Horario silencioso (no anuncia de noche, por ejemplo).
- Vibración configurable al recibir un pago.
- Activar/desactivar el monitoreo app por app.
- Bloqueo de la app con huella dactilar / PIN al abrirla.
- Tema oscuro.
- Nombre de negocio personalizado.
- Exportar historial completo a CSV (abrible en Excel/Google Sheets).
- Botón para borrar historial.
- Pantalla de bienvenida (onboarding) que guía a activar el permiso de
  notificaciones la primera vez.

## Ideas para seguir mejorándola (roadmap sugerido)

- Gráficos de estadísticas (ya incluí la librería MPAndroidChart en las
  dependencias, solo falta dibujar el gráfico de totales por día/semana/mes).
- Widget de escritorio con el total del día.
- Copia de seguridad en la nube del historial.
- Multi-negocio (varias "cajas" con historiales separados).
- Publicarla en Google Play (necesitas cuenta de desarrollador, ~$25 USD
  pago único).

## Estructura del proyecto

```
PagoVoz/
├── app/src/main/java/com/pagovoz/app/
│   ├── service/          → NotificationListener, foreground service, overlay
│   ├── data/              → Room (base de datos del historial)
│   ├── util/              → parser de montos, TTS, preferencias
│   ├── ui/alerts/         → pantalla de alertas
│   ├── ui/history/        → historial con pestañas por app
│   ├── ui/settings/       → ajustes y permisos
│   └── ui/onboarding/     → pantalla de bienvenida
└── app/src/main/res/      → layouts, colores, íconos
```

## Nota legal

Esta app solo **lee** las notificaciones ya generadas por las apps de
pago que tú mismo tienes instaladas (no accede a tus cuentas bancarias
ni a Internet fuera de tu propio dispositivo). Aun así, revisa que su
uso sea acorde a los Términos de Servicio de cada app de pago/banco.
