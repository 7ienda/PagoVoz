# PagoVoz — Lector de pagos por voz (Yape, Plin, Tunki, BIM, Plim...)

App Android (Kotlin) que escucha las notificaciones de tus apps de pago
y anuncia por voz cada pago recibido, con historial completo, alertas
en pantalla, funcionamiento en segundo plano y con pantalla bloqueada.

## ⚠️ Importante: esto es el proyecto fuente, no un .apk compilado

Este .zip contiene el **proyecto completo de Android Studio**. No incluye
un archivo `.apk` ya compilado porque compilar un APK requiere el SDK de
Android y los repositorios de Google (Maven), a los que este entorno de
IA no tiene acceso. Compilarlo tarda ~2 minutos siguiendo los pasos de
abajo, es gratis, y el resultado es tuyo (no depende de mí ni de ningún
servidor externo).

## Cómo generar el APK (paso a paso)

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
