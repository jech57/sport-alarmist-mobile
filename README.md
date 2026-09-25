# sport-alarmist-mobile
Alerts — app mobile para gestionar torneos deportivos de barrio: inscripción de equipos, confirmación de asistencia a partidos y recordatorios de horarios, con maquetación fiel al diseño en Figma.

## Tabla de contenido

1. [Instalación y ejecución](#instalación-y-ejecución)
2. [Recomendaciones](#recomendaciones)
3. [Explicaciones de cambios](#explicaciones-de-cambios)
4. [Distribución de trabajo](#distribución-de-trabajo)

## Instalación y ejecución
### 1. Descargar apk
Descargue el archivo apk en su dispositivo celular Android. Puede encontrarlo [aquí](https://uniandes-my.sharepoint.com/:f:/g/personal/l_restrepop_uniandes_edu_co/IgA918iwPExaRIcRSvWMpdPNARImep6wge2hQSiIe3m2_Ek?e=S4WDaF).

### 2. Instalar apk
Abra el archivo apk descargado. Esto debería preguntarle si desea instalar la aplicación. Escoja que sí, y proceda si/cuando le avise que no se pudo revisar si hay contenido malicioso (no se preocupe, no lo hay).

### 3. Ejecutar aplicación
Listo! La aplicación debería aparecer en su celular lista para abrir y usar.

## Recomendaciones
Tenga en cuenta las siguientes recomendaciones al usar la aplicación:

- Hay 8 torneos disponibles para que usted haga pruebas, pero tenga en cuenta que los datos persisten, por lo que si quiere volver a inscribirse a un torneo para reiniciar los flujos, deberá desinstalar la aplicación otra vez y volver a instalar mediante la apk.
- Cuando instale la aplicación por primera vez y escoja 'Voy' para una alarma, se generará un trigger a las 5 segundos que le mostrará un modal indicando que hubo cambios en la fecha y/o hora. Por favor, espere ese tiempo para simular el flujo de configuración de alarma tras un cambio por parte del organizador. Este trigger también se repite con un 33% de probabilidad la próxima vez que escoja 'Voy' al configurar una alarma por primera vez para cada partido subsiguiente. Si desea consutar más detalles sobre esta decisión de diseño, diríjase a la siguiente sección.

## Explicaciones de cambios

| # | Versión de mockup | Cambio | Justificación |
|---|-------------------|--------|---------------|
| 1 | <img width="734" height="232" alt="image" src="https://github.com/user-attachments/assets/69e62370-d270-41f7-9638-5a83132d2c31" /> | <img width="386" height="193" alt="image" src="https://github.com/user-attachments/assets/a129b13f-1d3c-4df7-aa3e-4d298ef2a4a6" /> | Se optó por crear un modal directamente en la aplicación por no que no se cuenta con un sistema externo de notificaciones push. No solo eso, sino que el modal llama más la atención al estar centrado, a diferencia de una notificación que puede ser omitida. |
| 2 | <img alt="image" src="https://github.com/user-attachments/assets/6e60774e-1b0a-4875-8b50-8e896024be2f" /> | <img alt="image" src="https://github.com/user-attachments/assets/75e1842a-3675-4df9-b48a-a08f3a2b056e" /> | Esto fue maquetado en los wireframes pero no en los mockups, así que fue corrección. |
| 3 | <img width="379" height="198" alt="image" src="https://github.com/user-attachments/assets/a2acecb1-a5cf-47d6-9867-f4b91014ddfa" /> | <img width="387" height="188" alt="image" src="https://github.com/user-attachments/assets/cccb48a3-25e0-4616-b23f-c84539b1d538" /> | Esto facilita la distinción del torneo de cada partido cuando hay múltiples cards en el listado. Además, permite verificar que el filtro funcione. |
| 4 | <img alt="image" src="https://github.com/user-attachments/assets/6e60774e-1b0a-4875-8b50-8e896024be2f" /> | <img width="382" height="313" alt="image" src="https://github.com/user-attachments/assets/e92f983e-060f-4feb-8e98-86cad120a920" /> | Fecha en formato dd-mm-aa. De este modo, la fecha queda con el mismo formato en todas las pantalla. |
| 5 | N/A |  <img width="372" height="42" alt="image" src="https://github.com/user-attachments/assets/ef8f434f-689d-4576-b929-aa0cdc046da6" /> | Flecha para devolverse en ciertas pantallas para facilitar la navegación. | 

## Distribución de trabajo
Para ver detalladamente qué cambios hizo cada integrante, puede dirijirse al apartado de [Pull Requests](https://github.com/jech57/sport-alarmist-mobile/pulls?q=is%3Apr+state%3Aclosed). Allí también podrá encontrar fotos de las modificaciones que fueron siendo agregadas. Además, debajo puede encontrar el listado de las pantallas que desarrolló cada uno.

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
