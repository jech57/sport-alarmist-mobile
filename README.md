# sport-alarmist-mobile
Alerts — app mobile para gestionar torneos deportivos de barrio: inscripción de equipos, confirmación de asistencia a partidos y recordatorios de horarios, con maquetación fiel al diseño en Figma.

## Tabla de contenido

1. [Instalación y ejecución](#instalación-y-ejecución)
2. [Recomendaciones](#recomendaciones)
3. [Explicaciones de cambios](#explicaciones-de-cambios)
4. [Distribución de trabajo](#distribución-de-trabajo)

## Instalación y ejecución
1. Descargue el archivo apk en su dispositivo celular Android. Puede encontrarlo [aquí](https://uniandes-my.sharepoint.com/:f:/g/personal/l_restrepop_uniandes_edu_co/IgA918iwPExaRIcRSvWMpdPNARImep6wge2hQSiIe3m2_Ek?e=S4WDaF).

2. Abra el archivo apk. Esto debería preguntarle si desea instalar la aplicación. Escoja que sí, y proceda si/cuando le avise que no se pudo revisar si hay contenido malicioso (no se preocupe, no lo hay).

3. Listo! La aplicación debería aparecer en su celular lista para abrir y usar.

## Recomendaciones
Tenga en cuenta las siguientes recomendaciones al usar la aplicación:

- Hay 8 torneos disponibles para que usted haga pruebas, pero tenga en cuenta que los datos persisten, por lo que si quiere volver a inscribirse a un torneo para reiniciar los flujos, deberá desinstalar la aplicación otra vez y volver a instalar mediante la apk.
- Cuando instale la aplicación por primera vez y escoja 'Voy' para una alarma, se generará un trigger a las 5 segundos que le mostrará un modal indicando que hubo cambios en la fecha y/o hora. Por favor, espere ese tiempo para simular el flujo de configuración de alarma tras un cambio por parte del organizador. Este trigger también se repite con un 33% de probabilidad la próxima vez que escoja 'Voy' al configurar una alarma por primera vez para cada partido subsiguiente. Si desea consutar más detalles sobre esta decisión de diseño, diríjase a la siguiente sección.

## Explicaciones de cambios

| # | Versión de mockup | Cambio | Justificación |
|---|-------------------|--------|---------------|
| 1 |                   |        |               |

## Distribución de trabajo
Para ver detalladamente qué cambios hizo cada integrante, puede dirijirse al apartado de [Pull Requests](https://github.com/jech57/sport-alarmist-mobile/pulls). Además, debajo puede encontrar el listado de las pantallas que desarrolló cada uno.

### 👨‍💻 Javier
- Listado de partidos/alarmas
- Listado de torneos
- Inscripción a torneo
- Detalle de torneo

### 👩‍💻 Laura
- Configuración de alarma (primera vez)
- Edición de alarma
- Configuración de alarma tras un cambio de fecha y/o por parte del organizador
- Modal de aviso de cambio de fecha y/o hora
